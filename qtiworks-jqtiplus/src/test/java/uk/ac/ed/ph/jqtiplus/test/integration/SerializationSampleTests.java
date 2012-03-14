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
package uk.ac.ed.ph.jqtiplus.test.integration;

import uk.ac.ed.ph.qtiworks.samples.QtiSampleResource;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleResource.Feature;
import uk.ac.ed.ph.qtiworks.samples.StandardQtiSampleSet;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.IOUtilities;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReadResult;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.serialization.SaxEventFirer;
import uk.ac.ed.ph.jqtiplus.serialization.SaxSerializationOptions;
import uk.ac.ed.ph.jqtiplus.testutils.TestUtils;
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
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.InputSource;

/**
 * Integration test that checks that serialized forms are re-parsed in the same way.
 *
 * @author David McKain
 */
@RunWith(Parameterized.class)
public class SerializationSampleTests {
    
    private QtiSampleResource qtiSampleResource;
    
    @Parameters
    public static Collection<Object[]> data() {
        return TestUtils.makeTestParameters(StandardQtiSampleSet.instance().withoutFeature(Feature.NOT_SCHEMA_VALID));
    }
    
    public SerializationSampleTests(QtiSampleResource qtiSampleResource) {
        this.qtiSampleResource = qtiSampleResource;
    }
    
    @Test
    public void test() throws Exception {
        final ResourceLocator sampleResourceLocator = new ClassPathResourceLocator();
        final URI sampleResourceUri = qtiSampleResource.toClassPathUri();
        
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
        final QtiXmlObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(sampleResourceLocator);
        QtiXmlObjectReadResult<AssessmentItem> itemReadResult = objectReader.lookupRootObject(sampleResourceUri, ModelRichness.FULL_ASSUMED_VALID, AssessmentItem.class);
        AssessmentItem item = itemReadResult.getRootObject();
        
        XsltSerializationOptions serializationOptions = new XsltSerializationOptions();
        serializationOptions.setIndenting(true);
        TransformerHandler serializerHandler = new XsltStylesheetManager().getSerializerHandler(serializationOptions);
        StringWriter serializedXmlWriter = new StringWriter();
        serializerHandler.setResult(new StreamResult(serializedXmlWriter));
        
        SaxEventFirer saxEventFirer = new SaxEventFirer(jqtiExtensionManager);
        saxEventFirer.fireSaxDocument(item, serializerHandler, new SaxSerializationOptions());
        String serializedXml = serializedXmlWriter.toString();
        
        InputStream originalXmlStream = sampleResourceLocator.findResource(sampleResourceUri);
        
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);
        Diff diff = new Diff(new InputSource(originalXmlStream), new InputSource(new StringReader(serializedXml)));
        if (!diff.identical()) {
            System.out.println("Test failure for URI: " + sampleResourceUri);
            System.out.println("Difference information:" + diff);
            System.out.println("\n\nOriginal XML: " + IOUtilities.readUnicodeStream(sampleResourceLocator.findResource(sampleResourceUri)));
            System.out.println("\n\nSerialized XML: " + serializedXml);
            Assert.fail("XML differences found: " + diff.toString());
        }
    }
}
