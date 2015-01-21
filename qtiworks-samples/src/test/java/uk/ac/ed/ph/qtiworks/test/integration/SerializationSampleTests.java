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
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.qtiworks.test.integration;

import uk.ac.ed.ph.qtiworks.samples.LanguageSampleSet;
import uk.ac.ed.ph.qtiworks.samples.MathAssessSampleSet;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment.Feature;
import uk.ac.ed.ph.qtiworks.samples.QtiworksRegressionSampleSet;
import uk.ac.ed.ph.qtiworks.samples.StandardQtiSampleSet;
import uk.ac.ed.ph.qtiworks.samples.StompSampleSet;
import uk.ac.ed.ph.qtiworks.samples.UpmcSampleSet;
import uk.ac.ed.ph.qtiworks.test.utils.TestUtils;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReadResult;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlInterpretationException;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer;
import uk.ac.ed.ph.jqtiplus.serialization.SaxFiringOptions;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltSerializationOptions;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltStylesheetManager;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.NodeDetail;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Integration test that checks that serialized forms are re-parsed in the same way.
 *
 * @author David McKain
 */
@RunWith(Parameterized.class)
public class SerializationSampleTests extends AbstractIntegrationTest {

    @Parameters
    public static Collection<Object[]> data() {
        return TestUtils.makeTestParameters(
                StandardQtiSampleSet.instance().withoutFeatures(Feature.NOT_SCHEMA_VALID),
                MathAssessSampleSet.instance().withoutFeatures(Feature.NOT_SCHEMA_VALID),
                UpmcSampleSet.instance().withoutFeatures(Feature.NOT_SCHEMA_VALID),
                StompSampleSet.instance().withoutFeatures(Feature.NOT_SCHEMA_VALID),
                LanguageSampleSet.instance().withoutFeatures(Feature.NOT_SCHEMA_VALID),
                QtiworksRegressionSampleSet.instance().withoutFeatures(Feature.NOT_SCHEMA_VALID)
        );
    }

    public SerializationSampleTests(final QtiSampleAssessment qtiSampleAssessment) {
        super(qtiSampleAssessment);
    }

    @Test
    public void test() throws Exception {
        final ResourceLocator sampleResourceLocator = new ClassPathResourceLocator();
        final QtiObjectReader objectReader = createSampleQtiObjectReader(false);
        QtiObjectReadResult<AssessmentItem> itemReadResult;
        try {
            itemReadResult = objectReader.lookupRootNode(qtiSampleAssessment.assessmentClassPathUri(), AssessmentItem.class);
        }
        catch (final QtiXmlInterpretationException e) {
            System.out.println("Model building errors: " + ObjectDumper.dumpObject(e.getQtiModelBuildingErrors(), DumpMode.DEEP));
            throw e;
        }
        final AssessmentItem item = itemReadResult.getRootNode();

        final XsltSerializationOptions serializationOptions = new XsltSerializationOptions();
        serializationOptions.setIndenting(true);
        final TransformerHandler serializerHandler = XsltStylesheetManager.createSerializerHandler(serializationOptions);
        final StringWriter serializedXmlWriter = new StringWriter();
        serializerHandler.setResult(new StreamResult(serializedXmlWriter));

        final QtiSaxDocumentFirer saxEventFirer = new QtiSaxDocumentFirer(objectReader.getQtiXmlReader().getJqtiExtensionManager(),
                serializerHandler, new SaxFiringOptions());
        saxEventFirer.fireSaxDocument(item);
        final String serializedXml = serializedXmlWriter.toString();

        final InputStream originalXmlStream = sampleResourceLocator.findResource(sampleResourceUri);

        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);
        final Diff diff = new Diff(new InputSource(originalXmlStream), new InputSource(new StringReader(serializedXml)));

        /* (We need to tell xmlunit to allow differences in namespace prefixes) */
        diff.overrideDifferenceListener(new QtiDifferenceListener(qtiSampleAssessment));
        if (!diff.identical()) {
            System.out.println("Test failure for URI: " + sampleResourceUri);
            System.out.println("Difference information:" + diff);
            System.out.println("\n\nOriginal XML: " + readSampleXmlSource());
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

        private static final Logger logger = LoggerFactory.getLogger(QtiDifferenceListener.class);

        private final QtiSampleAssessment qtiSampleResouce;

        public QtiDifferenceListener(final QtiSampleAssessment qtiSampleResouce) {
            this.qtiSampleResouce = qtiSampleResouce;
        }

        @Override
        public void skippedComparison(final Node input, final Node output) {
            /* No change */
        }

        @Override
        public int differenceFound(final Difference difference) {
            final int differenceId = difference.getId();
            final NodeDetail inputNodeDetail = difference.getControlNodeDetail();
            final NodeDetail outputNodeDetail = difference.getTestNodeDetail();
            final String inputValue = inputNodeDetail.getValue();
            final String outputValue = outputNodeDetail.getValue();
            switch (differenceId) {
                case DifferenceConstants.NAMESPACE_PREFIX_ID:
                    /* Don't worry about namespace prefixes */
                    return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;

                case DifferenceConstants.TEXT_VALUE_ID:
                    /* Different values. */
                    /* Test for equal floats */
                    if (isEqualFloat(inputValue, outputValue)) {
                        return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                    }
                    /* Test for equal float pairs */
                    if (isEqualFloatPair(inputValue, outputValue)) {
                        return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                    }
                    /* If still here, then assume it's a difference */
                    return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;

                case DifferenceConstants.ATTR_VALUE_ID:
                    /* Different attribute values */
                    /* Test for equal floats */
                    if (isEqualFloat(inputValue, outputValue)) {
                        return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                    }
                    /* Test for equal float pairs */
                    if (isEqualFloatPair(inputValue, outputValue)) {
                        return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                    }
                    /* If still here, then assume it's a difference */
                    return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;

                case DifferenceConstants.SCHEMA_LOCATION_ID:
                    /* Check xsi:schemaLocation */
                    return areSchemaLocationsGoodEnough(inputValue, outputValue) ? DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL : DifferenceListener.RETURN_ACCEPT_DIFFERENCE;

                default:
                    /* Assume anything else is a valid difference */
                    return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
            }
        }

        private boolean areSchemaLocationsGoodEnough(final String input, final String output) {
            final Map<String, String> inputInfo = extractXsiSchemaLocationData(input);
            final Map<String, String> outputInfo = extractXsiSchemaLocationData(output);
            for (final Entry<String, String> inputEntry : inputInfo.entrySet()) {
                final String nsUri = inputEntry.getKey();
                final String inputSchemaUri = inputEntry.getValue();
                final String outputSchemaUri = outputInfo.get(nsUri);
                if (outputSchemaUri==null) {
                    logger.warn("In sample {}: schema URI {} found in input XML but not included in output XML. Allowing this.", qtiSampleResouce, nsUri);
                }
                else if (!outputSchemaUri.equals(inputSchemaUri)) {
                    logger.warn("In sample {}: schema URI {} maps to URI {} in input XML but {} in output XML. Allowing this.",
                            new Object[] { qtiSampleResouce, nsUri, inputSchemaUri, outputSchemaUri });
                }
            }
            for (final Entry<String, String> outputEntry : outputInfo.entrySet()) {
                final String nsUri = outputEntry.getKey();
                if (!inputInfo.containsKey(nsUri)) {
                    logger.error("In sample {}: schema URI {} is in output XML but not input XML", qtiSampleResouce, nsUri);
                    return false;
                }
            }
            return true;
        }

        private Map<String, String> extractXsiSchemaLocationData(final String xsiSchemaLocationAttr) {
            final String[] splitData = xsiSchemaLocationAttr.split("\\s+");
            final Map<String, String> result = new HashMap<String, String>();
            for (int i=0; i<splitData.length; ) {
                final String nsUri = splitData[i++];
                final String schemaUri = splitData[i++];
                result.put(nsUri, schemaUri);
            }
            return result;
        }

        /**
         * Tests whether two floats are exactly equal when parsed. This allows 2 and 2.0 to be considered
         * equal, even though they're different as Strings.
         *
         * @param controlValue
         * @param testValue
         * @return
         */
        private boolean isEqualFloat(final String controlValue, final String testValue) {
            float controlFloat;
            float testFloat;
            try {
                controlFloat = Float.parseFloat(controlValue);
                testFloat = Float.parseFloat(testValue);
            }
            catch (final NumberFormatException e) {
                return false;
            }
            return controlFloat==testFloat; /* Yes, really == here! */
        }

        private boolean isEqualFloatPair(final String controlValue, final String testValue) {
            final String[] controlSplit = controlValue.split("\\s+");
            final String[] testSplit = testValue.split("\\s+");
            return controlSplit.length==2
                    && testSplit.length==2
                    && isEqualFloat(controlSplit[0], testSplit[0])
                    && isEqualFloat(controlSplit[1], testSplit[1]);
        }
    }
}
