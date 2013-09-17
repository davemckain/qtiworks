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
package uk.ac.ed.ph.jqtiplus.node.accessibility.related;

import uk.ac.ed.ph.jqtiplus.group.accessibility.AccessibilityNode;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.AnswerReductionGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.BrailleTextGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.ChunkGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.GuidanceGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.KeyWordEmphasisGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.KeyWordTranslationGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.RevealAlternativeRepresentationGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.ScaffoldGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.SigningGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.SpokenGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.StructuredMaskGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.TactileFileGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.accessibility.AccessElement;

/**
 * Container for all actual accessibility metacontent within a given accessElement
 *
 * @author Zack Pierce
 */
public class RelatedElementInfo extends AbstractNode implements AccessibilityNode {

    private static final long serialVersionUID = -3694130873474050407L;

    public static final String QTI_CLASS_NAME = "relatedElementInfo";

    public RelatedElementInfo(final AccessElement parent) {
        super(parent, QTI_CLASS_NAME);
        getNodeGroups().add(new SpokenGroup(this));
        getNodeGroups().add(new BrailleTextGroup(this));
        getNodeGroups().add(new TactileFileGroup(this));
        getNodeGroups().add(new SigningGroup(this));
        getNodeGroups().add(new KeyWordEmphasisGroup(this));
        getNodeGroups().add(new ChunkGroup(this));
        getNodeGroups().add(new KeyWordTranslationGroup(this));
        getNodeGroups().add(new RevealAlternativeRepresentationGroup(this));
        getNodeGroups().add(new GuidanceGroup(this));
        getNodeGroups().add(new StructuredMaskGroup(this));
        getNodeGroups().add(new ScaffoldGroup(this));
        getNodeGroups().add(new AnswerReductionGroup(this));
    }

    public Spoken getSpoken() {
        return getNodeGroups().getSpokenGroup().getSpoken();
    }

    public void setSpoken(final Spoken spoken) {
        getNodeGroups().getSpokenGroup().setSpoken(spoken);
    }

    public BrailleText getBrailleText() {
        return getNodeGroups().getBrailleTextGroup().getBrailleText();
    }

    public void setBrailleText(final BrailleText brailleText) {
        getNodeGroups().getBrailleTextGroup().setBrailleText(brailleText);
    }

    public TactileFile getTactileFile() {
        return getNodeGroups().getTactileFileGroup().getTactileFile();
    }

    public void setTactileFile(final TactileFile tactileFile) {
        getNodeGroups().getTactileFileGroup().setTactileFile(tactileFile);
    }

    public boolean getKeyWordEmphasis() {
        return getNodeGroups().getKeyWordEmphasisGroup().hasKeyWordEmphasis();
    }

    public void setKeyWordEmphasis(final boolean chunk) {
        getNodeGroups().getKeyWordEmphasisGroup().setHasKeyWordEmphasis(chunk);
    }

    public boolean getChunk() {
        return getNodeGroups().getChunkGroup().hasChunk();
    }

    public void setChunk(final boolean chunk) {
        getNodeGroups().getChunkGroup().setHasChunk(chunk);
    }

    public Signing getSigning() {
        return getNodeGroups().getSigningGroup().getSigning();
    }

    public void setSigning(final Signing signing) {
        getNodeGroups().getSigningGroup().setSigning(signing);
    }

    public KeyWordTranslation getKeyWordTranslation() {
        return getNodeGroups().getKeyWordTranslationGroup().getKeyWordTranslation();
    }

    public void setKeyWordTranslation(final KeyWordTranslation keyWordTranslation) {
        getNodeGroups().getKeyWordTranslationGroup().setKeyWordTranslation(keyWordTranslation);
    }

    public RevealAlternativeRepresentation getRevealAlternativeRepresentation() {
        return getNodeGroups().getRevealAlternativeRepresentationGroup().getRevealAlternativeRepresentation();
    }

    public void setRevealAlternativeRepresentation(final RevealAlternativeRepresentation revealAlternativeRepresentation) {
        getNodeGroups().getRevealAlternativeRepresentationGroup().setRevealAlternativeRepresentation(
                revealAlternativeRepresentation);
    }

    public Guidance getGuidance() {
        return getNodeGroups().getGuidanceGroup().getGuidance();
    }

    public void setGuidance(final Guidance guidance) {
        getNodeGroups().getGuidanceGroup().setGuidance(guidance);
    }

    public StructuredMask getStructuredMask() {
        return getNodeGroups().getStructuredMaskGroup().getStructuredMask();
    }

    public void setStructuredMask(final StructuredMask structuredMask) {
        getNodeGroups().getStructuredMaskGroup().setStructuredMask(structuredMask);
    }

    public Scaffold getScaffold() {
        return getNodeGroups().getScaffoldGroup().getScaffold();
    }

    public void setScaffold(final Scaffold scaffold) {
        getNodeGroups().getScaffoldGroup().setScaffold(scaffold);
    }

    public AnswerReduction getAnswerReduction() {
        return getNodeGroups().getAnswerReductionGroup().getAnswerReduction();
    }

    public void setAnswerReduction(final AnswerReduction answerReduction) {
        getNodeGroups().getAnswerReductionGroup().setAnswerReduction(answerReduction);
    }
}
