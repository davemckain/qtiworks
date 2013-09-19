/* Copyright (c) 2012-2013, University of Edinburgh.
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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.serialization;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.QtiProfile;
import uk.ac.ed.ph.jqtiplus.node.ForeignElement;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.ItemBody;
import uk.ac.ed.ph.jqtiplus.node.content.mathml.Math;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.P;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReadResult;
import uk.ac.ed.ph.jqtiplus.testutils.UnitTestHelper;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltSerializationOptions;

import java.io.IOException;
import java.net.URI;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Tests the serialization package.
 *
 * @author David McKain
 * @author Zack Pierce
 */
public class QtiSerializerTest {

    @Test
    public void testEmptyItemSerialization() throws SAXException, IOException {
        final AssessmentItem item = new AssessmentItem();
        final String expectedXml = "<assessmentItem xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                + " xmlns='http://www.imsglobal.org/xsd/imsqti_v2p1'"
                + " xsi:schemaLocation='http://www.imsglobal.org/xsd/imsqti_v2p1 http://www.imsglobal.org/xsd/imsqti_v2p1.xsd'"
                + " identifier='' title='' adaptive='' timeDependent=''/>";

        serializeAndCompare(item, expectedXml, new SaxFiringOptions());
    }

    @Test
    public void testEmptyItemSerializationApipCore() throws SAXException, IOException {
        final AssessmentItem item = new AssessmentItem();
        final String expectedXml = "<assessmentItem xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                + " xmlns='http://www.imsglobal.org/xsd/apip/apipv1p0/qtiitem/imsqti_v2p1'"
                + " xsi:schemaLocation='http://www.imsglobal.org/xsd/apip/apipv1p0/qtiitem/imsqti_v2p1 http://www.imsglobal.org/xsd/apip/apipv1p0/apipv1p0_qtiitemv2p1_v1p0.xsd'"
                + " identifier='' title='' adaptive='' timeDependent=''/>";

        serializeAndCompare(item, expectedXml, new SaxFiringOptions(QtiProfile.APIP_CORE));
    }

    @Test
    public void testEmptyItemSerializationWithoutSchemaLocation() throws SAXException, IOException {
        final AssessmentItem item = new AssessmentItem();

        final SaxFiringOptions saxFiringOptions = new SaxFiringOptions(true);

        final String expectedXml = "<assessmentItem xmlns='http://www.imsglobal.org/xsd/imsqti_v2p1'"
                + " identifier='' title='' adaptive='' timeDependent=''/>";

        serializeAndCompare(item, expectedXml, saxFiringOptions);
    }

    @Test
    public void testEmptyItemSerializationWithoutSchemaLocationApipCore() throws SAXException, IOException {
        final AssessmentItem item = new AssessmentItem();

        final SaxFiringOptions saxFiringOptions = new SaxFiringOptions(true, QtiProfile.APIP_CORE);

        final String expectedXml = "<assessmentItem xmlns='http://www.imsglobal.org/xsd/apip/apipv1p0/qtiitem/imsqti_v2p1'"
                + " identifier='' title='' adaptive='' timeDependent=''/>";

        serializeAndCompare(item, expectedXml, saxFiringOptions);
    }



    @Test
    public void testEmptyItemSerializationWithPrefix() throws SAXException, IOException {
        final AssessmentItem item = new AssessmentItem();

        final SaxFiringOptions saxFiringOptions = new SaxFiringOptions();
        saxFiringOptions.getPreferredPrefixMappings().registerStrict(QtiConstants.QTI_21_NAMESPACE_URI, "q");

        final String expectedXml = "<q:assessmentItem xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                + " xmlns:q='http://www.imsglobal.org/xsd/imsqti_v2p1'"
                + " xsi:schemaLocation='http://www.imsglobal.org/xsd/imsqti_v2p1 http://www.imsglobal.org/xsd/imsqti_v2p1.xsd'"
                + " identifier='' title='' adaptive='' timeDependent=''/>";

        serializeAndCompare(item, expectedXml, saxFiringOptions);
    }

    @Test
    public void testEmptyItemSerializationWithPrefixApipCore() throws SAXException, IOException {
        final AssessmentItem item = new AssessmentItem();

        final SaxFiringOptions saxFiringOptions = new SaxFiringOptions(QtiProfile.APIP_CORE);
        saxFiringOptions.getPreferredPrefixMappings().registerStrict(QtiConstants.APIP_CORE_ITEM_URI, "q");

        final String expectedXml = "<q:assessmentItem xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                + " xmlns:q='http://www.imsglobal.org/xsd/apip/apipv1p0/qtiitem/imsqti_v2p1'"
                + " xsi:schemaLocation='http://www.imsglobal.org/xsd/apip/apipv1p0/qtiitem/imsqti_v2p1 http://www.imsglobal.org/xsd/apip/apipv1p0/apipv1p0_qtiitemv2p1_v1p0.xsd'"
                + " identifier='' title='' adaptive='' timeDependent=''/>";

        serializeAndCompare(item, expectedXml, saxFiringOptions);
    }

    @Test
    public void testItemWithBase() throws SAXException, IOException {
        final AssessmentItem item = new AssessmentItem();
        final ItemBody itemBody = new ItemBody(item);
        item.setItemBody(itemBody);
        final P paragraph = new P(itemBody);
        paragraph.setBaseUri(URI.create("urn:test"));
        itemBody.getBlocks().add(paragraph);

        final SaxFiringOptions saxFiringOptions = new SaxFiringOptions();

        final String expectedXml = "<assessmentItem xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                + " xmlns='http://www.imsglobal.org/xsd/imsqti_v2p1'"
                + " xsi:schemaLocation='http://www.imsglobal.org/xsd/imsqti_v2p1 http://www.imsglobal.org/xsd/imsqti_v2p1.xsd'"
                + " identifier='' title='' adaptive='' timeDependent=''>" + "<itemBody>" + "<p xml:base='urn:test'/>"
                + "</itemBody>" + "</assessmentItem>";

        serializeAndCompare(item, expectedXml, saxFiringOptions);
    }

    @Test
    public void testItemWithMathSerialization() throws SAXException, IOException {
        final AssessmentItem item = new AssessmentItem();
        final ItemBody itemBody = new ItemBody(item);
        item.setItemBody(itemBody);
        final Math math = new uk.ac.ed.ph.jqtiplus.node.content.mathml.Math(item);
        itemBody.getBlocks().add(math);
        math.getContent().add(new ForeignElement(math, "mrow", QtiConstants.MATHML_NAMESPACE_URI));

        final SaxFiringOptions saxFiringOptions = new SaxFiringOptions();

        final String expectedXml = "<assessmentItem xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                + " xmlns='http://www.imsglobal.org/xsd/imsqti_v2p1'"
                + " xsi:schemaLocation='http://www.imsglobal.org/xsd/imsqti_v2p1 http://www.imsglobal.org/xsd/imsqti_v2p1.xsd'"
                + " identifier='' title='' adaptive='' timeDependent=''>" + "<itemBody>"
                + "<math xmlns='http://www.w3.org/1998/Math/MathML'><mrow/></math>" + "</itemBody>"
                + "</assessmentItem>";

        serializeAndCompare(item, expectedXml, saxFiringOptions);
    }

    @Test
    public void testItemWithMathSerializationPrefix() throws SAXException, IOException {
        final AssessmentItem item = new AssessmentItem();
        final ItemBody itemBody = new ItemBody(item);
        item.setItemBody(itemBody);
        final Math math = new uk.ac.ed.ph.jqtiplus.node.content.mathml.Math(item);
        itemBody.getBlocks().add(math);
        math.getContent().add(new ForeignElement(math, "mrow", QtiConstants.MATHML_NAMESPACE_URI));

        final SaxFiringOptions saxFiringOptions = new SaxFiringOptions();
        saxFiringOptions.getPreferredPrefixMappings().registerLax(QtiConstants.MATHML_NAMESPACE_URI, "m");

        final String expectedXml = "<assessmentItem xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                + " xmlns='http://www.imsglobal.org/xsd/imsqti_v2p1'"
                + " xmlns:m='http://www.w3.org/1998/Math/MathML'"
                + " xsi:schemaLocation='http://www.imsglobal.org/xsd/imsqti_v2p1 http://www.imsglobal.org/xsd/imsqti_v2p1.xsd'"
                + " identifier='' title='' adaptive='' timeDependent=''>" + "<itemBody>" + "<m:math><m:mrow/></m:math>"
                + "</itemBody>" + "</assessmentItem>";

        serializeAndCompare(item, expectedXml, saxFiringOptions);
    }

    @Test
    public void testItemWithMathSerializationPrefixClash() throws SAXException, IOException {
        final AssessmentItem item = new AssessmentItem();
        final ItemBody itemBody = new ItemBody(item);
        item.setItemBody(itemBody);
        final Math math = new uk.ac.ed.ph.jqtiplus.node.content.mathml.Math(item);
        itemBody.getBlocks().add(math);
        math.getContent().add(new ForeignElement(math, "mrow", QtiConstants.MATHML_NAMESPACE_URI));

        final SaxFiringOptions saxFiringOptions = new SaxFiringOptions();
        saxFiringOptions.getPreferredPrefixMappings().registerLax(QtiConstants.QTI_21_NAMESPACE_URI, "m"); /*
                                                                                                            * This
                                                                                                            * prefix
                                                                                                            * will win
                                                                                                            */
        saxFiringOptions.getPreferredPrefixMappings().registerLax(QtiConstants.MATHML_NAMESPACE_URI, "m"); /*
                                                                                                            * Will
                                                                                                            * become m0:
                                                                                                            */

        final String expectedXml = "<m:assessmentItem xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                + " xmlns:m='http://www.imsglobal.org/xsd/imsqti_v2p1'"
                + " xmlns:m0='http://www.w3.org/1998/Math/MathML'"
                + " xsi:schemaLocation='http://www.imsglobal.org/xsd/imsqti_v2p1 http://www.imsglobal.org/xsd/imsqti_v2p1.xsd'"
                + " identifier='' title='' adaptive='' timeDependent=''>" + "<m:itemBody>"
                + "<m0:math><m0:mrow/></m0:math>" + "</m:itemBody>" + "</m:assessmentItem>";

        serializeAndCompare(item, expectedXml, saxFiringOptions);
    }

    @Test
    public void testItemWithMathAndForeign() throws SAXException, IOException {
        final AssessmentItem item = new AssessmentItem();
        final ItemBody itemBody = new ItemBody(item);
        item.setItemBody(itemBody);
        final Math math = new uk.ac.ed.ph.jqtiplus.node.content.mathml.Math(item);
        itemBody.getBlocks().add(math);
        math.getContent().add(new ForeignElement(math, "silly", "urn:silly"));

        final SaxFiringOptions saxFiringOptions = new SaxFiringOptions();

        final String expectedXml = "<assessmentItem xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                + " xmlns='http://www.imsglobal.org/xsd/imsqti_v2p1'"
                + " xsi:schemaLocation='http://www.imsglobal.org/xsd/imsqti_v2p1 http://www.imsglobal.org/xsd/imsqti_v2p1.xsd'"
                + " identifier='' title='' adaptive='' timeDependent=''>" + "<itemBody>"
                + "<math xmlns='http://www.w3.org/1998/Math/MathML'>" + "<silly xmlns='urn:silly'/>" + "</math>"
                + "</itemBody>" + "</assessmentItem>";

        serializeAndCompare(item, expectedXml, saxFiringOptions);
    }

    @Test
    public void testEmptyResultSerialization() throws SAXException, IOException {
        final AssessmentResult result = new AssessmentResult();
        final String expectedXml = "<assessmentResult xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                + " xmlns='http://www.imsglobal.org/xsd/imsqti_result_v2p1'"
                + " xsi:schemaLocation='http://www.imsglobal.org/xsd/imsqti_result_v2p1 http://www.imsglobal.org/xsd/imsqti_result_v2p1.xsd'"
                + "/>";

        serializeAndCompare(result, expectedXml, new SaxFiringOptions());
    }

    // TODO - test AssessmentTest serialization for QTI and APIP profiles
    // TODO - test AssessmentSection serialization for QTI and APIP profiles
    // TODO - test read-as-QTI, write-as-APIP
    // TODO - test read-as-APIP, write-as-QTI

    @Test
    public void testRoundTripReadAndSerialization() throws SAXException, IOException, Exception {
        final String resourcePath = "serialization/roundtrippable-core-apip.xml";
        final QtiObjectReadResult<AssessmentItem> lookupRootNode = UnitTestHelper.readAssessmentItemQtiObjectTest(resourcePath);
        final AssessmentItem item = lookupRootNode.getRootNode();
        final NamespacePrefixMappings prefixes = new NamespacePrefixMappings();
        prefixes.registerStrict(QtiProfile.APIP_CORE.getAccessibilityNamespace(), "apip");
        final SaxFiringOptions options = new SaxFiringOptions(false, QtiProfile.APIP_CORE, prefixes);
        serializeAndCompare(item, UnitTestHelper.getResourceAsString(resourcePath), options);
    }

    // ---------------------------------------------------------------------------

    private void serializeAndCompare(final QtiNode node, final String expectedXml, final SaxFiringOptions saxFiringOptions)
            throws SAXException, IOException {
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        final QtiSerializer qtiSerializer = new QtiSerializer(jqtiExtensionManager);

        final String serializedXml = qtiSerializer.serializeJqtiObject(node, saxFiringOptions,
                new XsltSerializationOptions());

        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);
        final Diff diff = new Diff(expectedXml, serializedXml);
        if (!diff.identical()) {
            System.out.println("Expected XML:   " + expectedXml);
            System.out.println("Serialized XML: " + serializedXml);
            Assert.fail("XML differences found: " + diff.toString());
        }
    }
}
