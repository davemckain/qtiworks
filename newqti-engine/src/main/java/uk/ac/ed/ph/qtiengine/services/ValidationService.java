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
package uk.ac.ed.ph.qtiengine.services;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.utils.QtiContentPackageExtractor;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.CustomUriScheme;
import uk.ac.ed.ph.jqtiplus.xmlutils.FileSandboxResourceLocator;

import uk.ac.ed.ph.qtiengine.EngineException;
import uk.ac.ed.ph.qtiengine.web.domain.AssessmentPackage;

import java.io.File;
import java.net.URI;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

/**
 * FIXME: Document this type
 * 
 * 
 * Then we've got the validation result to account for.
 * 
 * For ZIPs, we'll need to unpack into a temporary directory and ensure it's deleted afterwards.
 *
 * @author David McKain
 */
@Service
public class ValidationService {
    
    @Resource
    private JqtiExtensionManager jqtiExtensionManager;
    
    public AbstractValidationResult validate(AssessmentPackage assessmentPackage) {
        CustomUriScheme packageUriScheme = QtiContentPackageExtractor.PACKAGE_URI_SCHEME;
        FileSandboxResourceLocator inputResourceLocator = new FileSandboxResourceLocator(QtiContentPackageExtractor.PACKAGE_URI_SCHEME, new File(assessmentPackage.getSandboxPath()));
        QtiXmlObjectReader objectReader = new QtiXmlObjectReader(jqtiExtensionManager, inputResourceLocator);
        AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);
        URI objectSystemId = packageUriScheme.pathToUri(assessmentPackage.getAssessmentObjectHref());
        
        AbstractValidationResult result = null;
        switch (assessmentPackage.getPackageType()) {
            case ITEM:
                result = objectManager.validateItem(objectSystemId);
                break;
                
            case TEST:
                result = objectManager.validateTest(objectSystemId);
                break;
                
            default:
                throw new EngineException("Unexpected switch case " + assessmentPackage.getPackageType());
                
        }
        return result;
    }
}
