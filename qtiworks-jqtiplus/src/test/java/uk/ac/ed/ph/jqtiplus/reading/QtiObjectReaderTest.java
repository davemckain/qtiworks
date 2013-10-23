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
package uk.ac.ed.ph.jqtiplus.reading;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import uk.ac.ed.ph.jqtiplus.node.accessibility.AccessElement;
import uk.ac.ed.ph.jqtiplus.node.accessibility.ApipAccessibility;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.Calculator;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.CalculatorTypeType;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.CompanionMaterialsInfo;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.DigitalMaterial;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.LinearUnitSI;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.LinearUnitUS;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.PhysicalMaterial;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.Protractor;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.RadialIncrementSystemSI;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.RadialIncrementSystemUS;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.RadialUnitSI;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.RadialUnitUS;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.ReadingPassage;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.Rule;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.RuleSystemSI;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.RuleSystemUS;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.AslDefaultOrder;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.BrailleDefaultOrder;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.InclusionOrder;
import uk.ac.ed.ph.jqtiplus.node.accessibility.link.ContentLinkInfo;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.AnswerReduction;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.AudioFileInfo;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.BrailleText;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.DefinitionId;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.GuidanceSupport;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.LabelledString;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.MarkupFileEmbedded;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.ObjectFileInfo;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.RelatedElementInfo;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.RemoveTagGroup;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.Scaffold;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.SignFile;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.Signing;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.Spoken;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.StructuredMask;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.TactileFile;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.VideoFileInfo;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.VoiceSpeed;
import uk.ac.ed.ph.jqtiplus.node.accessibility.related.VoiceType;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Block;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.P;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.ChoiceInteraction;
import uk.ac.ed.ph.jqtiplus.testutils.UnitTestHelper;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalTime;
import org.junit.Test;

/**
 * @author Zack Pierce
 */
public class QtiObjectReaderTest {

    @Test
    public void testLookupRootNodeSimpleItem() throws Exception {
        final QtiObjectReadResult<AssessmentItem> lookupRootNode = UnitTestHelper.readAssessmentItemQtiObjectTest("reading/choice.xml");
        assertNotNull(lookupRootNode);
        final AssessmentItem item = lookupRootNode.getRootNode();
        assertNotNull(item);
        final List<Block> blocks = item.getItemBody().getBlocks();
        assertEquals(3, blocks.size());
        assertEquals(P.class, blocks.get(0).getClass());
        assertEquals(ChoiceInteraction.class, blocks.get(2).getClass());
        assertNull(item.getApipAccessibility());
    }

    @Test
    public void testLookupRootNodeWithAccessibilityInclusionOrder() throws Exception {
        final QtiObjectReadResult<AssessmentItem> lookupRootNode = UnitTestHelper.readAssessmentItemQtiObjectTest("reading/accessible-core-qti.xml");
        final AssessmentItem item = lookupRootNode.getRootNode();
        final ApipAccessibility apipAccessibility = item.getApipAccessibility();

        final InclusionOrder inclusionOrder = apipAccessibility.getInclusionOrder();
        assertNotNull(inclusionOrder);

        final AslDefaultOrder aslDefaultOrder = inclusionOrder.getAslDefaultOrder();
        assertNotNull(aslDefaultOrder);
        assertEquals(1, aslDefaultOrder.getElementOrders().size());
        assertEquals(1, aslDefaultOrder.getElementOrders().get(0).getOrder().intValue());
        assertEquals("ae01", aslDefaultOrder.getElementOrders().get(0).getIdentifierRef().toString());
        assertNotNull(inclusionOrder.getAslOnDemandOrder());

        final BrailleDefaultOrder brailleDefaultOrder = inclusionOrder.getBrailleDefaultOrder();
        assertNotNull(brailleDefaultOrder);
        assertEquals(2, brailleDefaultOrder.getElementOrders().size());
        assertEquals(1, brailleDefaultOrder.getElementOrders().get(0).getOrder().intValue());
        assertEquals("ae01", brailleDefaultOrder.getElementOrders().get(0).getIdentifierRef().toString());
        assertEquals(2, brailleDefaultOrder.getElementOrders().get(1).getOrder().intValue());
        assertEquals("ae02", brailleDefaultOrder.getElementOrders().get(1).getIdentifierRef().toString());
        assertNull(inclusionOrder.getTextOnlyOnDemandOrder());
    }

    @Test
    public void testLookupRootNodeWithAccessibilityInclusionOrderApipCore() throws Exception {
        final QtiObjectReadResult<AssessmentItem> lookupRootNode = UnitTestHelper.readAssessmentItemQtiObjectTest("reading/minimal-core-apip.xml");
        final AssessmentItem item = lookupRootNode.getRootNode();
        final ApipAccessibility apipAccessibility = item.getApipAccessibility();

        final InclusionOrder inclusionOrder = apipAccessibility.getInclusionOrder();
        assertNotNull(inclusionOrder);
        assertNull(inclusionOrder.getAslDefaultOrder());
        assertNull(inclusionOrder.getAslOnDemandOrder());
        assertNull(inclusionOrder.getGraphicsOnlyOnDemandOrder());
        assertNull(inclusionOrder.getSignedEnglishDefaultOrder());
        assertNull(inclusionOrder.getSignedEnglishOnDemandOrder());

        final BrailleDefaultOrder brailleDefaultOrder = inclusionOrder.getBrailleDefaultOrder();
        assertNotNull(brailleDefaultOrder);
        assertEquals(2, brailleDefaultOrder.getElementOrders().size());
        assertEquals(1, brailleDefaultOrder.getElementOrders().get(0).getOrder().intValue());
        assertEquals("ae01", brailleDefaultOrder.getElementOrders().get(0).getIdentifierRef().toString());
        assertEquals(2, brailleDefaultOrder.getElementOrders().get(1).getOrder().intValue());
        assertEquals("ae02", brailleDefaultOrder.getElementOrders().get(1).getIdentifierRef().toString());
        assertNull(inclusionOrder.getTextOnlyOnDemandOrder());

        assertNotNull(inclusionOrder.getNonVisualDefaultOrder());
        assertNotNull(inclusionOrder.getTextGraphicsDefaultOrder());
        assertNotNull(inclusionOrder.getTextGraphicsOnDemandOrder());
        assertNotNull(inclusionOrder.getTextOnlyDefaultOrder());
        assertNull(inclusionOrder.getTextOnlyOnDemandOrder());
    }

    @Test
    public void testLookupRootNodeWithAccessibilityCompanionMaterials() throws Exception {
        final QtiObjectReadResult<AssessmentItem> lookupRootNode = UnitTestHelper.readAssessmentItemQtiObjectTest("reading/accessible-core-qti.xml");
        final AssessmentItem item = lookupRootNode.getRootNode();
        final ApipAccessibility apipAccessibility = item.getApipAccessibility();
        final CompanionMaterialsInfo cmi = apipAccessibility.getCompanionMaterialsInfo();
        assertNotNull(cmi);

        final List<Calculator> calculators = cmi.findCalculators();
        assertEquals(1, calculators.size());
        final Calculator calc = calculators.get(0);
        assertEquals(CalculatorTypeType.STANDARD, calc.getCalculatorTypeType());
        assertEquals("Hello Calculator", calc.getDescriptionText());
        assertEquals("application/javascript", calc.getCalculatorInfo().getMimeType());
        assertEquals("standardCalculator.js", calc.getCalculatorInfo().getFileHref());

        final List<DigitalMaterial> digMaterials = cmi.findDigitalMaterials();
        assertEquals(1, digMaterials.size());
        final DigitalMaterial digMat = digMaterials.get(0);
        assertEquals("text/plain", digMat.getMimeType());
        assertEquals("refSheetA.txt", digMat.getFileHref());

        final List<PhysicalMaterial> physicalMaterials = cmi.findPhysicalMaterials();
        assertEquals(1, physicalMaterials.size());
        assertEquals("Raised terrain map", physicalMaterials.get(0).getTextContent());

        final List<ReadingPassage> passages = cmi.findReadingPassages();
        assertEquals(1, passages.size());
        final ReadingPassage passage = passages.get(0);
        assertEquals("text/html", passage.getMimeType());
        assertEquals("readingPassage.html", passage.getFileHref());

        final List<Protractor> protractors = cmi.findProtractors();
        assertEquals(2, protractors.size());
        assertEquals("SI Protractor", protractors.get(0).getDescriptionText());
        final RadialIncrementSystemSI radialIncrementSystemSI = protractors.get(0).getRadialIncrementSystemSI();
        assertEquals(new BigDecimal("1"), radialIncrementSystemSI.getMinorIncrementRadialSI().getDecimal());
        assertEquals(RadialUnitSI.RADIAN, radialIncrementSystemSI.getMinorIncrementRadialSI().getUnit());
        assertEquals(new BigDecimal("2.22"), radialIncrementSystemSI.getMajorIncrementRadialSI().getDecimal());
        assertEquals(RadialUnitSI.RADIAN, radialIncrementSystemSI.getMajorIncrementRadialSI().getUnit());
        assertNull(protractors.get(0).getRadialIncrementSystemUS());

        assertEquals("US Protractor", protractors.get(1).getDescriptionText());
        final RadialIncrementSystemUS radialIncrementSystemUS = protractors.get(1).getRadialIncrementSystemUS();
        assertEquals(new BigDecimal("0.5"), radialIncrementSystemUS.getMinorIncrementRadialUS().getDecimal());
        assertEquals(RadialUnitUS.DEGREE, radialIncrementSystemUS.getMinorIncrementRadialUS().getUnit());
        assertEquals(new BigDecimal("1.3"), radialIncrementSystemUS.getMajorIncrementRadialUS().getDecimal());
        assertEquals(RadialUnitUS.MINUTE, radialIncrementSystemUS.getMajorIncrementRadialUS().getUnit());
        assertNull(protractors.get(1).getRadialIncrementSystemSI());

        final List<Rule> rules = cmi.findRules();
        assertEquals(2, rules.size());
        final Rule ruleSI = rules.get(0);
        assertEquals("SI Rule", ruleSI.getDescriptionText());
        final RuleSystemSI ruleSystemSI = ruleSI.getRuleSystemSI();
        assertEquals(3, ruleSystemSI.getMinimumLength().getValue().intValue());
        assertEquals(LinearUnitSI.MILLIMETER, ruleSystemSI.getMinorIncrementLinearSI().getUnit());
        assertEquals(new BigDecimal("0.3"), ruleSystemSI.getMinorIncrementLinearSI().getDecimal());
        assertEquals(LinearUnitSI.KILOMETER, ruleSystemSI.getMajorIncrementLinearSI().getUnit());
        assertEquals(new BigDecimal("1.0"), ruleSystemSI.getMajorIncrementLinearSI().getDecimal());
        assertNull(ruleSI.getRuleSystemUS());

        final Rule ruleUS = rules.get(1);
        assertEquals("US Rule", ruleUS.getDescriptionText());
        final RuleSystemUS ruleSystemUS = ruleUS.getRuleSystemUS();
        assertEquals(3, ruleSystemUS.getMinimumLength().getValue().intValue());
        assertEquals(LinearUnitUS.INCH, ruleSystemUS.getMinorIncrementLinearUS().getUnit());
        assertEquals(new BigDecimal("0.3"), ruleSystemUS.getMinorIncrementLinearUS().getDecimal());
        assertEquals(LinearUnitUS.MILE, ruleSystemUS.getMajorIncrementLinearUS().getUnit());
        assertEquals(new BigDecimal("1.0"), ruleSystemUS.getMajorIncrementLinearUS().getDecimal());
        assertNull(ruleUS.getRuleSystemSI());
    }

    @Test
    public void testLookupRootNodeWithAccessibilityCompanionMaterialsApipCore() throws Exception {
        final QtiObjectReadResult<AssessmentItem> lookupRootNode = UnitTestHelper.readAssessmentItemQtiObjectTest("reading/minimal-core-apip.xml");
        final AssessmentItem item = lookupRootNode.getRootNode();
        final ApipAccessibility apipAccessibility = item.getApipAccessibility();
        final CompanionMaterialsInfo cmi = apipAccessibility.getCompanionMaterialsInfo();
        assertNotNull(cmi);
        assertEquals(0, cmi.findCalculators().size());
        assertEquals(1, cmi.findDigitalMaterials().size());
        assertEquals("refSheetA.txt", cmi.findDigitalMaterials().get(0).getFileHref());
        assertEquals("text/plain", cmi.findDigitalMaterials().get(0).getMimeType());
        assertEquals(0, cmi.findPhysicalMaterials().size());
        assertEquals(0, cmi.findProtractors().size());
        assertEquals(0, cmi.findReadingPassages().size());
        assertEquals(0, cmi.findRules().size());

    }

    @Test
    public void testLookupRootNodeWithAccessibilityAccessElementsContentLinks() throws Exception {
        final QtiObjectReadResult<AssessmentItem> lookupRootNode = UnitTestHelper.readAssessmentItemQtiObjectTest("reading/accessible-core-qti.xml");
        final List<AccessElement> accessElements = lookupRootNode.getRootNode().getApipAccessibility().getAccessElements();
        assertEquals(18, accessElements.size());

        final AccessElement ae01 = accessElements.get(0);
        assertEquals("ae01", ae01.getIdentifier().toString());
        assertEquals(1, ae01.getContentLinkInfos().size());
        final ContentLinkInfo cliA = ae01.getContentLinkInfos().get(0);
        assertEquals("p1", cliA.getQtiLinkIdentifierRef().toString());
        assertNull(cliA.getApipLinkIdentifierRef());
        assertNull(cliA.getObjectLink());
        assertNull(cliA.getTextLink().getCharacterLink());
        assertNull(cliA.getTextLink().getCharacterStringLink());
        assertNotNull(cliA.getTextLink().getFullString());
        assertNull(cliA.getTextLink().getWordLink());

        final AccessElement ae02 = accessElements.get(1);
        assertEquals("ae02", ae02.getIdentifier().toString());
        assertEquals(1, ae02.getContentLinkInfos().size());
        final ContentLinkInfo cliB = ae02.getContentLinkInfos().get(0);
        assertEquals("signImg", cliB.getQtiLinkIdentifierRef().toString());
        assertNull(cliB.getApipLinkIdentifierRef());
        assertNotNull(cliB.getObjectLink());
        assertNull(cliB.getTextLink());

        final AccessElement ae03 = accessElements.get(2);
        assertEquals("ae03", ae03.getIdentifier().toString());
        assertEquals(3, ae03.getContentLinkInfos().size());
        final ContentLinkInfo cliC = ae03.getContentLinkInfos().get(0);
        assertEquals("promptA", cliC.getQtiLinkIdentifierRef().toString());
        assertNull(cliC.getApipLinkIdentifierRef());
        assertNull(cliC.getObjectLink());
        assertNotNull(cliC.getTextLink().getCharacterLink());
        assertEquals(1, cliC.getTextLink().getCharacterLink().getValue().intValue());
        assertNull(cliC.getTextLink().getCharacterStringLink());
        assertNull(cliC.getTextLink().getWordLink());
        assertNull(cliC.getTextLink().getFullString());

        final ContentLinkInfo cliD = ae03.getContentLinkInfos().get(1);
        assertEquals("promptA", cliD.getQtiLinkIdentifierRef().toString());
        assertNull(cliD.getApipLinkIdentifierRef());
        assertNull(cliD.getObjectLink());
        assertNull(cliD.getTextLink().getCharacterLink());
        assertNotNull(cliD.getTextLink().getCharacterStringLink());
        assertEquals(13, cliD.getTextLink().getCharacterStringLink().getStartCharacter().intValue());
        assertEquals(15, cliD.getTextLink().getCharacterStringLink().getStopCharacter().intValue());
        assertNull(cliD.getTextLink().getWordLink());
        assertNull(cliD.getTextLink().getFullString());

        final ContentLinkInfo cliE = ae03.getContentLinkInfos().get(2);
        assertEquals("promptA", cliE.getQtiLinkIdentifierRef().toString());
        assertNull(cliE.getApipLinkIdentifierRef());
        assertNull(cliE.getObjectLink());
        assertNull(cliE.getTextLink().getCharacterLink());
        assertNull(cliE.getTextLink().getCharacterStringLink());
        assertNotNull(cliE.getTextLink().getWordLink());
        assertEquals(2, cliE.getTextLink().getWordLink().getValue().intValue());
        assertNull(cliE.getTextLink().getFullString());


        final ContentLinkInfo cliF = accessElements.get(17).getContentLinkInfos().get(0);
        assertNull(cliF.getQtiLinkIdentifierRef());
        assertEquals("kwtd02", cliF.getApipLinkIdentifierRef().toString());
    }

    @Test
    public void testLookupRootNodeWithAccessibilityCoreRelatedInfo() throws Exception {
        final QtiObjectReadResult<AssessmentItem> lookupRootNode = UnitTestHelper.readAssessmentItemQtiObjectTest("reading/accessible-core-qti.xml");
        final List<AccessElement> accessElements = lookupRootNode.getRootNode().getApipAccessibility().getAccessElements();

        final AccessElement ae01 = accessElements.get(0);
        final RelatedElementInfo relatedElementInfoA = ae01.getRelatedElementInfo();
        assertNotNull(relatedElementInfoA);
        final Spoken spokenA = relatedElementInfoA.getSpoken();
        assertNotNull(spokenA);
        final List<AudioFileInfo> audioFileInfosA = spokenA.getAudioFileInfos();
        assertEquals(2, audioFileInfosA.size());
        final AudioFileInfo afi01 = audioFileInfosA.get(0);
        assertEquals("audio/mpeg", afi01.getMimeType());
        assertEquals("afi01", afi01.getContentLinkIdentifier().toString());
        assertEquals("afi01.mp3", afi01.getFileHref());
        assertEquals(VoiceType.HUMAN, afi01.getVoiceType());
        assertEquals(VoiceSpeed.FAST, afi01.getVoiceSpeed());
        assertEquals(new LocalTime("00:00:10.5"), afi01.getStartTime());
        assertEquals(new LocalTime("00:01:00"), afi01.getDuration());

        final AudioFileInfo afi02 = audioFileInfosA.get(1);
        assertEquals("audio/ogg", afi02.getMimeType());
        assertEquals("afi02", afi02.getContentLinkIdentifier().toString());
        assertEquals("afi02.ogg", afi02.getFileHref());
        assertEquals(VoiceType.SYNTHETIC, afi02.getVoiceType());
        assertEquals(VoiceSpeed.STANDARD, afi02.getVoiceSpeed());
        assertNull(afi02.getStartTime());
        assertNull(afi02.getDuration());
        assertEquals("Look at the text in the picture.", spokenA.getSpokenText().getTextContent());
        assertEquals("st01", spokenA.getSpokenText().getContentLinkIdentifier().toString());
        assertEquals("Luhk at the text in the picture", spokenA.getTextToSpeechPronunciation().getTextContent());
        assertEquals("ttsp01", spokenA.getTextToSpeechPronunciation().getContentLinkIdentifier().toString());

        final BrailleText brailleTextA = relatedElementInfoA.getBrailleText();
        final LabelledString brailleTextStringA = brailleTextA.getBrailleTextString();
        assertEquals("bts01", brailleTextStringA.getContentLinkIdentifier().toString());
        assertEquals("Look at the text in the picture", brailleTextStringA.getTextContent());

        final TactileFile tactileFileA = relatedElementInfoA.getTactileFile();
        final AudioFileInfo taf01 = tactileFileA.getTactileAudioFile();
        assertEquals("audio/mpeg", taf01.getMimeType());
        assertEquals("taf01", taf01.getContentLinkIdentifier().toString());
        assertEquals("taf01.mp3", taf01.getFileHref());
        assertEquals(VoiceType.SYNTHETIC, taf01.getVoiceType());
        assertEquals(VoiceSpeed.STANDARD, taf01.getVoiceSpeed());
        assertNull(taf01.getStartTime());
        assertNull(taf01.getDuration());
        assertEquals("tat01", tactileFileA.getTactileAudioText().getContentLinkIdentifier().toString());
        assertEquals("Look at the picture and consider how it feels on the physical map.", tactileFileA.getTactileAudioText().getTextContent());
        assertEquals("tbt01", tactileFileA.getTactileBrailleText().getContentLinkIdentifier().toString());
        assertEquals("Look at the picture and consider how it feels on the physical map.", tactileFileA.getTactileBrailleText().getTextContent());
    }

    @Test
    public void testLookupRootNodeWithAccessibilityBeyondCore() throws Exception {
        final QtiObjectReadResult<AssessmentItem> lookupRootNode = UnitTestHelper.readAssessmentItemQtiObjectTest("reading/accessible-core-qti.xml");
        final List<AccessElement> accessElements = lookupRootNode.getRootNode().getApipAccessibility().getAccessElements();
        final AccessElement ae01 = accessElements.get(0);
        assertFalse(ae01.getRelatedElementInfo().getChunk());
        assertFalse(ae01.getRelatedElementInfo().getKeyWordEmphasis());
        final AccessElement ae02 = accessElements.get(1);
        assertTrue(ae02.getRelatedElementInfo().getChunk());
        assertTrue(ae02.getRelatedElementInfo().getKeyWordEmphasis());

        final Signing signing = ae02.getRelatedElementInfo().getSigning();
        assertNull(signing.getSignFileSignedEnglish());
        final SignFile asl = signing.getSignFileASL();
        final VideoFileInfo aslVideo = asl.getVideoFileInfo();
        assertEquals("sfaslvfi01", aslVideo.getContentLinkIdentifier().toString());
        assertEquals("video/mpeg", aslVideo.getMimeType());
        assertEquals("doNotLeaveLuggage.mpeg", aslVideo.getFileHref());
        assertEquals(new LocalTime("00:00:01"), aslVideo.getStartCue());
        assertEquals(new LocalTime("00:00:20"), aslVideo.getEndCue());

        final ObjectFileInfo bavf = asl.getBoneAnimationVideoFile();
        assertEquals("sfaslbavf", bavf.getContentLinkIdentifier().toString());
        assertEquals("video/x-matroska", bavf.getMimeType());
        assertEquals("neverLeaveLuggage.mkv", bavf.getFileHref());

        final List<DefinitionId> definitionIds = ae02.getRelatedElementInfo().getKeyWordTranslation().getDefinitionIds();
        assertEquals(2, definitionIds.size());
        assertEquals("en-US", definitionIds.get(0).getLang());
        assertEquals("Never leave your bags alone.", definitionIds.get(0).getTextString().getTextContent());
        assertEquals("kwtd01", definitionIds.get(0).getTextString().getContentLinkIdentifier().toString());

        assertEquals("es-419", definitionIds.get(1).getLang());
        assertEquals("nunca deje el equipaje desatendido", definitionIds.get(1).getTextString().getTextContent());
        assertEquals("kwtd02", definitionIds.get(1).getTextString().getContentLinkIdentifier().toString());

        final AudioFileInfo rafi01 = accessElements.get(3).getRelatedElementInfo().getRevealAlternativeRepresentation().getAudioFileInfo();
        assertEquals("audio/mpeg", rafi01.getMimeType());
        assertEquals("rafi01.mp3", rafi01.getFileHref());

        final VideoFileInfo rvfi01 = accessElements.get(4).getRelatedElementInfo().getRevealAlternativeRepresentation().getVideoFileInfo();
        assertEquals("video/ogg", rvfi01.getMimeType());
        assertEquals("rvfi01.ogg", rvfi01.getFileHref());

        final ObjectFileInfo rtfi01 = accessElements.get(5).getRelatedElementInfo().getRevealAlternativeRepresentation().getTextFileInfo();
        assertEquals("rtfi01.txt", rtfi01.getFileHref());

        final LabelledString rts01 = accessElements.get(6).getRelatedElementInfo().getRevealAlternativeRepresentation().getTextString();
        assertEquals("rts01", rts01.getContentLinkIdentifier().toString());
        assertEquals("Never leave luggage unattended.", rts01.getTextContent());

        final ObjectFileInfo rgfi01 = accessElements.get(7).getRelatedElementInfo().getRevealAlternativeRepresentation().getGraphicFileInfo();
        assertEquals("rgfi01.svg", rgfi01.getFileHref());

        final ObjectFileInfo rmfi01 = accessElements.get(8).getRelatedElementInfo().getRevealAlternativeRepresentation().getMarkupFileInfo();
        assertEquals("rmfi01.xhtml", rmfi01.getFileHref());

        final ObjectFileInfo rmfi02 = accessElements.get(9).getRelatedElementInfo().getRevealAlternativeRepresentation().getMarkupFileInfo();
        assertEquals("rmfi02.markdown", rmfi02.getFileHref());

        final ObjectFileInfo refi01 = accessElements.get(10).getRelatedElementInfo().getRevealAlternativeRepresentation().getExecutableFileInfo();
        assertEquals("refi01.js", refi01.getFileHref());

        final MarkupFileEmbedded mfe01 = accessElements.get(11).getRelatedElementInfo().getRevealAlternativeRepresentation().getMarkupFileEmbedded();
        assertNotNull(mfe01);
        // TODO - handle and test 3rd party namespace elements within MarkupFileEmbedded

        final GuidanceSupport gll01 = accessElements.get(3).getRelatedElementInfo().getGuidance().getLanguageLearnerSupport();
        assertEquals(1, gll01.getSupportOrder().intValue());
        assertEquals("gll01", gll01.getTextString().getContentLinkIdentifier().toString());
        assertEquals("\"Luggage\" is a term used for the items one carries during travel.", gll01.getTextString().getTextContent());

        final GuidanceSupport gcg01 = accessElements.get(3).getRelatedElementInfo().getGuidance().getCognitiveGuidanceSupport();
        assertEquals(1, gcg01.getSupportOrder().intValue());
        assertEquals("gcg01", gcg01.getTextString().getContentLinkIdentifier().toString());
        assertEquals("\"Luggage\" means travel bags.", gcg01.getTextString().getTextContent());

        final GuidanceSupport gll02 = accessElements.get(4).getRelatedElementInfo().getGuidance().getLanguageLearnerSupport();
        assertEquals(2, gll02.getSupportOrder().intValue());
        assertEquals("gll02", gll02.getTextString().getContentLinkIdentifier().toString());
        assertEquals("Second set of language learner guidance.", gll02.getTextString().getTextContent());

        final GuidanceSupport gcg02 = accessElements.get(4).getRelatedElementInfo().getGuidance().getCognitiveGuidanceSupport();
        assertEquals(2, gcg02.getSupportOrder().intValue());
        assertEquals("gcg02", gcg02.getTextString().getContentLinkIdentifier().toString());
        assertEquals("Second set of cognitive-difficulties-oriented guidance.", gcg02.getTextString().getTextContent());

        final StructuredMask structuredMaskA = accessElements.get(12).getRelatedElementInfo().getStructuredMask();
        assertEquals(1, structuredMaskA.getRevealOrder().intValue());
        assertEquals(true, structuredMaskA.getAnswerOption().booleanValue());

        final StructuredMask structuredMaskB = accessElements.get(13).getRelatedElementInfo().getStructuredMask();
        assertNull(structuredMaskB.getRevealOrder());
        assertEquals(false, structuredMaskB.getAnswerOption().booleanValue());

        final StructuredMask structuredMaskC = accessElements.get(14).getRelatedElementInfo().getStructuredMask();
        assertNull(structuredMaskC.getRevealOrder());
        assertEquals(true, structuredMaskC.getAnswerOption().booleanValue());

        final Scaffold scaffoldA = accessElements.get(15).getRelatedElementInfo().getScaffold();
        assertEquals(1, scaffoldA.getRevealOrder().intValue());
        assertEquals("audio/ogg", scaffoldA.getScaffoldBehavior().getAudioFileInfo().getMimeType());
        assertEquals("safi01", scaffoldA.getScaffoldBehavior().getAudioFileInfo().getContentLinkIdentifier().toString());
        assertEquals("safi01.ogg", scaffoldA.getScaffoldBehavior().getAudioFileInfo().getFileHref());
        assertEquals("sts01", scaffoldA.getScaffoldBehavior().getTextString().getContentLinkIdentifier().toString());
        assertEquals("What does the sign mean, really?", scaffoldA.getScaffoldBehavior().getTextString().getTextContent());
        assertEquals("sst01", scaffoldA.getScaffoldBehavior().getSpokenText().getContentLinkIdentifier().toString());
        assertEquals("What does the sign really mean?", scaffoldA.getScaffoldBehavior().getSpokenText().getTextContent());

        final AnswerReduction answerReduction01 = accessElements.get(16).getRelatedElementInfo().getAnswerReduction();
        final RemoveTagGroup removeTagGroupBC = answerReduction01.getRemoveTagGroups().get(0);
        assertEquals(1, removeTagGroupBC.getRemoveTagGroupOrder().intValue());
        assertEquals(2, removeTagGroupBC.getRemoveTagIdRefs().size());
        assertEquals("simpleChoiceB", removeTagGroupBC.getRemoveTagIdRefs().get(0).getTextContent());
        assertEquals("simpleChoiceC", removeTagGroupBC.getRemoveTagIdRefs().get(1).getTextContent());

        final RemoveTagGroup removeTagGroupA = answerReduction01.getRemoveTagGroups().get(1);
        assertEquals(2, removeTagGroupA.getRemoveTagGroupOrder().intValue());
        assertEquals(1, removeTagGroupA.getRemoveTagIdRefs().size());
        assertEquals("simpleChoiceA", removeTagGroupA.getRemoveTagIdRefs().get(0).getTextContent());
    }

}
