/* Copyright (c) 2012, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.qtiworks.web.controller;

import uk.ac.ed.ph.qtiworks.rendering.Renderer;
import uk.ac.ed.ph.qtiworks.rendering.SerializationMethod;
import uk.ac.ed.ph.qtiworks.samples.AllSampleSets;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleResource;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleSet;
import uk.ac.ed.ph.qtiworks.web.exception.QtiSampleNotFoundException;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * First stab at controller for doing candidate (item) sessions
 *
 * @author David McKain
 */
@Controller
public class CandidateController {
    
    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);
    
    private static final String CURRENT_ITEM = "currentItem";
    private static final String CURRENT_ITEM_SESSION_STATE = "currentItemSessionState";
    
    @Resource
    private JqtiExtensionManager jqtiExtensionManager;
    
    @Resource
    private QtiXmlReader qtiXmlReader;
    
    @Resource
    private Renderer renderer;

    /**
     * Starts a new item session using the sample resource with the given path
     */
    @RequestMapping(value="/newSession/{setIndex}/{itemIndex}", method=RequestMethod.GET)
    public String newSampleItemSession(HttpSession httpSession, @PathVariable int setIndex, @PathVariable int itemIndex) {
        logger.info("newSampleItemSession(setIndex={}, itemIndex={})", setIndex, itemIndex);
        
        final QtiSampleSet[] allSampleSets = AllSampleSets.asArray();
        if (setIndex < 0 || setIndex >= allSampleSets.length) {
            throw new QtiSampleNotFoundException("Could not find sample set with index " + setIndex);
        }
        final QtiSampleSet qtiSampleSet = allSampleSets[setIndex];
        final List<QtiSampleResource> qtiSampleResources = qtiSampleSet.getResources();
        if (itemIndex <0 || itemIndex >= qtiSampleResources.size()) {
            throw new QtiSampleNotFoundException("Could not find sample resource with index " + setIndex + " in set " + qtiSampleSet);
        }
        final QtiSampleResource qtiSampleResource = qtiSampleResources.get(itemIndex);
        logger.info("Starting new session for {}", qtiSampleResource);

        /* Load and resolve item */
        final ResourceLocator sampleResourceLocator = new ClassPathResourceLocator();
        final URI sampleResourceUri = qtiSampleResource.toClassPathUri();
        final QtiXmlObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(sampleResourceLocator);
        final AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);
        ResolvedAssessmentItem resolvedAssessmentItem = objectManager.resolveAssessmentItem(sampleResourceUri, ModelRichness.FULL_ASSUMED_VALID);
        
        /* Create new item session */
        ItemSessionState itemSessionState = new ItemSessionState();
        
        /* TEMP: Store things in the HTTP session */
        httpSession.setAttribute(CURRENT_ITEM, resolvedAssessmentItem);
        httpSession.setAttribute(CURRENT_ITEM_SESSION_STATE, itemSessionState);

        /* Redirect to session handler */
        return "redirect:/dispatcher/itemSession";
    }
    
    @RequestMapping(value="/itemSession", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public String sampleItemSession(HttpSession httpSession) {
        /* TEMP */
        
        /* TEMP: Extract current item & state from HTTP session */
        ResolvedAssessmentItem resolvedAssessmentItem = (ResolvedAssessmentItem) httpSession.getAttribute(CURRENT_ITEM);
        ItemSessionState itemSessionState = (ItemSessionState) httpSession.getAttribute(CURRENT_ITEM_SESSION_STATE);
        
        try {
            if (!itemSessionState.isInitialized()) {
                /* Session hasn't been initialized yet */
                return initializeItemSession(resolvedAssessmentItem, itemSessionState);
            }
        }
        catch (RuntimeValidationException e) {
            throw new QtiLogicException("Unexpected RuntimeValidationException encountered", e);
        }

        return null;
    }
    
    private String initializeItemSession(ResolvedAssessmentItem resolvedAssessmentItem, ItemSessionState itemSessionState) throws RuntimeValidationException {
        ItemSessionController itemController = new ItemSessionController(jqtiExtensionManager, resolvedAssessmentItem, itemSessionState);
        itemController.initialize();
        
        Map<String, Object> itemParameters = new HashMap<String, Object>();
        Map<String, Object> renderingParameters = new HashMap<String, Object>();
        return renderer.renderFreshStandaloneItem(resolvedAssessmentItem, itemSessionState,
                "/RESOURCES-TODO", itemParameters, renderingParameters, SerializationMethod.HTML5_MATHJAX);
    }
}
