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
package uk.ac.ed.ph.qtiworks.test.integration;

import uk.ac.ed.ph.qtiworks.samples.MathAssessSampleSet;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleResource;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleResource.Feature;
import uk.ac.ed.ph.qtiworks.samples.StandardQtiSampleSet;
import uk.ac.ed.ph.qtiworks.test.utils.TestUtils;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.IOUtilities;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReadResult;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer;
import uk.ac.ed.ph.jqtiplus.serialization.SaxFiringOptions;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltSerializationOptions;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltStylesheetManager;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.Collection;

import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Integration test that checks that serialized forms are re-parsed in the same way.
 *
 * @author David McKain
 */
@RunWith(Parameterized.class)
public class SerializationSampleTests {
    
    private QtiSampleResource qtiSampleResource;
    private JqtiExtensionManager jqtiExtensionManager;
    
    @Parameters
    public static Collection<Object[]> data() {
        return TestUtils.makeTestParameters(StandardQtiSampleSet.instance().withoutFeature(Feature.NOT_SCHEMA_VALID),
                MathAssessSampleSet.instance().withoutFeature(Feature.NOT_SCHEMA_VALID));
    }
    
    public SerializationSampleTests(QtiSampleResource qtiSampleResource) {
        this.qtiSampleResource = qtiSampleResource;
    }
    
    @Before
    public void before() {
        jqtiExtensionManager = TestUtils.getJqtiExtensionManager();
        jqtiExtensionManager.init();
    }
    
    @After
    public void after() {
        if (jqtiExtensionManager!=null) {
            jqtiExtensionManager.destroy();
        }
    }
    
    @Test
    public void test() throws Exception {
        final ResourceLocator sampleResourceLocator = new ClassPathResourceLocator();
        final URI sampleResourceUri = qtiSampleResource.toClassPathUri();
        
        final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
        final QtiXmlObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(sampleResourceLocator);
        QtiXmlObjectReadResult<AssessmentItem> itemReadResult = objectReader.lookupRootObject(sampleResourceUri, ModelRichness.FULL_ASSUMED_VALID, AssessmentItem.class);
        AssessmentItem item = itemReadResult.getRootObject();
        
        XsltSerializationOptions serializationOptions = new XsltSerializationOptions();
        serializationOptions.setIndenting(true);
        TransformerHandler serializerHandler = new XsltStylesheetManager().getSerializerHandler(serializationOptions);
        StringWriter serializedXmlWriter = new StringWriter();
        serializerHandler.setResult(new StreamResult(serializedXmlWriter));
        
        QtiSaxDocumentFirer saxEventFirer = new QtiSaxDocumentFirer(serializerHandler, new SaxFiringOptions());
        saxEventFirer.fireSaxDocument(item);
        String serializedXml = serializedXmlWriter.toString();
        
        InputStream originalXmlStream = sampleResourceLocator.findResource(sampleResourceUri);
        
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);
        Diff diff = new Diff(new InputSource(originalXmlStream), new InputSource(new StringReader(serializedXml)));
        
        /* (We need to tell xmlunit to allow differences in namespace prefixes) */
        diff.overrideDifferenceListener(new QtiDifferenceListener());
        if (!diff.identical()) {
            System.out.println("Test failure for URI: " + sampleResourceUri);
            System.out.println("Difference information:" + diff);
            System.out.println("\n\nOriginal XML: " + IOUtilities.readUnicodeStream(sampleResourceLocator.findResource(sampleResourceUri)));
            System.out.println("\n\nSerialized XML: " + serializedXml);
            Assert.fail("XML differences found: " + diff.toString());
        }
    }
    
    /**
     * Custom {@link DifferenceListener} to account for some differences we expect to
     * find between reading and subsequently serializing.
     *
     * @author David McKain
     */
    protected static class QtiDifferenceListener implements DifferenceListener {
        
        @Override
        public void skippedComparison(Node conotrl, Node test) {
            /* No change */
        }
        
        @Override
        public int differenceFound(Difference difference) {
            int differenceId = difference.getId();
            String controlValue = difference.getControlNodeDetail().getValue();
            String testValue = difference.getTestNodeDetail().getValue();
            switch (differenceId) {
                case DifferenceConstants.NAMESPACE_PREFIX_ID:
                    /* Don't worry about namespace prefixes */
                    return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                    
                case DifferenceConstants.TEXT_VALUE_ID:
                    /* Different values. */
                    /* Test for equal floats */
                    if (isEqualFloat(controlValue, testValue)) {
                        return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                    }
                    return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
                    
                case DifferenceConstants.ATTR_VALUE_ID:
                    /* Different attribute values */
                    /* Test for equal floats */
                    if (isEqualFloat(controlValue, testValue)) {
                        return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                    }
                    return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
                    
                default:
                    /* Assume anything else is a valid difference */
                    return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
            }
        }
        
        /**
         * Tests whether two floats are exactly equal when parsed. This allows 2 and 2.0 to be considered
         * equal, even though they're differet as Strings.
         * 
         * @param controlValue
         * @param testValue
         * @return
         */
        private boolean isEqualFloat(String controlValue, String testValue) {
            float controlFloat;
            float testFloat;
            try {
                controlFloat = Float.parseFloat(controlValue);
                testFloat = Float.parseFloat(testValue);
            }
            catch (NumberFormatException e) {
                return false;
            }
            return controlFloat==testFloat; /* Yes, really == here! */
        }
    }
}
