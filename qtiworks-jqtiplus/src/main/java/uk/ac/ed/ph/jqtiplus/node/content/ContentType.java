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
package uk.ac.ed.ph.jqtiplus.node.content;

import uk.ac.ed.ph.jqtiplus.exception.QtiIllegalChildException;
import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Block;
import uk.ac.ed.ph.jqtiplus.node.content.basic.BlockStatic;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Flow;
import uk.ac.ed.ph.jqtiplus.node.content.basic.FlowStatic;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Inline;
import uk.ac.ed.ph.jqtiplus.node.content.basic.InlineStatic;
import uk.ac.ed.ph.jqtiplus.node.content.basic.ObjectFlow;
import uk.ac.ed.ph.jqtiplus.node.content.basic.TextRun;
import uk.ac.ed.ph.jqtiplus.node.content.mathml.Math;
import uk.ac.ed.ph.jqtiplus.node.content.template.TemplateBlock;
import uk.ac.ed.ph.jqtiplus.node.content.template.TemplateInline;
import uk.ac.ed.ph.jqtiplus.node.content.variable.FeedbackBlock;
import uk.ac.ed.ph.jqtiplus.node.content.variable.FeedbackInline;
import uk.ac.ed.ph.jqtiplus.node.content.variable.PrintedVariable;
import uk.ac.ed.ph.jqtiplus.node.content.variable.RubricBlock;
import uk.ac.ed.ph.jqtiplus.node.content.variable.TextOrVariable;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.hypertext.A;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.image.Img;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.list.Dd;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.list.Dl;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.list.Dt;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.list.Li;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.list.Ol;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.list.Ul;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.object.Param;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.B;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.Big;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.Hr;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.I;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.Small;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.Sub;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.Sup;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.Tt;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Caption;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Col;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Colgroup;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Table;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Tbody;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Td;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Tfoot;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Th;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Thead;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Tr;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Abbr;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Acronym;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Address;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Blockquote;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Br;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Cite;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Code;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Dfn;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Div;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Em;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H1;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H2;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H3;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H4;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H5;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H6;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Kbd;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.P;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Pre;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Q;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Samp;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Span;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Strong;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Var;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.AssociateInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.ChoiceInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.CustomInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.DrawingInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.EndAttemptInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.ExtendedTextInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.GapMatchInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.GraphicAssociateInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.GraphicGapMatchInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.GraphicOrderInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.HotspotInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.HottextInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.InlineChoiceInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.MatchInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.MediaInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.OrderInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.PositionObjectInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Prompt;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.SelectPointInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.SliderInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.TextEntryInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.UploadInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.GapChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.GapImg;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.GapText;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.InlineChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleAssociableChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleMatchSet;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.content.Gap;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.content.Hottext;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.content.PositionObjectStage;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.graphic.AssociableHotspot;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.graphic.HotspotChoice;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * All the types of content.
 *
 * @author Jonathon Hare
 */
public enum ContentType {

    //blockInteraction:
    /**
     * associateInteraction
     */
    ASSOCIATE_INTERACTION(AssociateInteraction.QTI_CLASS_NAME, AssociateInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new AssociateInteraction(parent);
        }
    },
    /**
     * choiceInteraction
     */
    CHOICE_INTERACTION(ChoiceInteraction.QTI_CLASS_NAME, ChoiceInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new ChoiceInteraction(parent);
        }
    },
    /**
     * drawingInteraction
     */
    DRAWING_INTERACTION(DrawingInteraction.QTI_CLASS_NAME, DrawingInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new DrawingInteraction(parent);
        }
    },
    /**
     * extendedTextInteraction
     */
    EXTENDED_TEXT_INTERACTION(ExtendedTextInteraction.QTI_CLASS_NAME, ExtendedTextInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new ExtendedTextInteraction(parent);
        }
    },
    /**
     * gapMatchInteraction
     */
    GAP_MATCH_INTERACTION(GapMatchInteraction.QTI_CLASS_NAME, GapMatchInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new GapMatchInteraction(parent);
        }
    },
    /**
     * graphicAssociateInteraction
     */
    GRAPHIC_ASSOCIATE_INTERACTION(GraphicAssociateInteraction.QTI_CLASS_NAME, GraphicAssociateInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new GraphicAssociateInteraction(parent);
        }
    },
    /**
     * graphicGapMatchInteraction
     */
    GRAPHIC_GAP_MATCH_INTERACTION(GraphicGapMatchInteraction.QTI_CLASS_NAME, GraphicGapMatchInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new GraphicGapMatchInteraction(parent);
        }
    },
    /**
     * graphicOrderInteration
     */
    GRAPHIC_ORDER_INTERACTION(GraphicOrderInteraction.QTI_CLASS_NAME, GraphicOrderInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new GraphicOrderInteraction(parent);
        }
    },
    /**
     * hotspotInteraction
     */
    HOTSPOT_INTERACTION(HotspotInteraction.QTI_CLASS_NAME, HotspotInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new HotspotInteraction(parent);
        }
    },
    /**
     * selectPointInteraction
     */
    SELECT_POINT_INTERACTION(SelectPointInteraction.QTI_CLASS_NAME, SelectPointInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new SelectPointInteraction(parent);
        }
    },
    /**
     * hottextInteraction
     */
    HOTTEXT_INTERACTION(HottextInteraction.QTI_CLASS_NAME, HottextInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new HottextInteraction(parent);
        }
    },
    /**
     * matchInteraction
     */
    MATCH_INTERACTION(MatchInteraction.QTI_CLASS_NAME, MatchInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new MatchInteraction(parent);
        }
    },
    /**
     * mediaInteraction
     */
    MEDIA_INTERACTION(MediaInteraction.QTI_CLASS_NAME, MediaInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new MediaInteraction(parent);
        }
    },
    /**
     * orderInteraction
     */
    ORDER_INTERACTION(OrderInteraction.QTI_CLASS_NAME, OrderInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new OrderInteraction(parent);
        }
    },
    /**
     * sliderInteraction
     */
    SLIDER_INTERACTION(SliderInteraction.QTI_CLASS_NAME, SliderInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new SliderInteraction(parent);
        }
    },
    /**
     * uploadInteraction
     */
    UPLOAD_INTERACTION(UploadInteraction.QTI_CLASS_NAME, UploadInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new UploadInteraction(parent);
        }
    },
    //blockStatic,
    /**
     * address
     */
    ADDRESS(Address.QTI_CLASS_NAME, Address.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Address(parent);
        }
    },
    /**
     * h1
     */
    H1(uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H1.QTI_CLASS_NAME, uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H1.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new H1(parent);
        }
    },
    /**
     * h2
     */
    H2(uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H2.QTI_CLASS_NAME, uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H2.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new H2(parent);
        }
    },
    /**
     * h3
     */
    H3(uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H3.QTI_CLASS_NAME, uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H3.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new H3(parent);
        }
    },
    /**
     * h4
     */
    H4(uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H4.QTI_CLASS_NAME, uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H4.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new H4(parent);
        }
    },
    /**
     * h5
     */
    H5(uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H5.QTI_CLASS_NAME, uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H5.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new H5(parent);
        }
    },
    /**
     * h6
     */
    H6(uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H6.QTI_CLASS_NAME, uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H6.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new H6(parent);
        }
    },
    /**
     * p
     */
    P(uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.P.QTI_CLASS_NAME, uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.P.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new P(parent);
        }
    },
    /**
     * pre
     */
    PRE(Pre.QTI_CLASS_NAME, Pre.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Pre(parent);
        }
    },
    /**
     * div
     */
    DIV(Div.QTI_CLASS_NAME, Div.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Div(parent);
        }
    },

    INFOCONTROL(InfoControl.QTI_CLASS_NAME, InfoControl.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new InfoControl(parent);
        }
    },

    /**
     * dl
     */
    DL(Dl.QTI_CLASS_NAME, Dl.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Dl(parent);
        }
    },
    /**
     * hr
     */
    HR(Hr.QTI_CLASS_NAME, Hr.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Hr(parent);
        }
    },
    /**
     * math
     */
    MATH(Math.QTI_CLASS_NAME, Math.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Math(parent);
        }
    },
    /**
     * ol
     */
    OL(Ol.QTI_CLASS_NAME, Ol.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Ol(parent);
        }
    },
    /**
     * blockquote
     */
    BLOCKQUOTE(Blockquote.QTI_CLASS_NAME, Blockquote.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Blockquote(parent);
        }
    },
    /**
     * feedbackBlock
     */
    FEEDBACK_BLOCK(FeedbackBlock.QTI_CLASS_NAME, FeedbackBlock.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new FeedbackBlock(parent);
        }
    },
    /**
     * rubricBlock
     */
    RUBRIC_BLOCK(RubricBlock.QTI_CLASS_NAME, RubricBlock.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new RubricBlock(parent);
        }
    },
    /**
     * table
     */
    TABLE(Table.QTI_CLASS_NAME, Table.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Table(parent);
        }
    },
    /**
     * ul
     */
    UL(Ul.QTI_CLASS_NAME, Ul.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Ul(parent);
        }
    },
    /**
     * customInteraction
     */
    CUSTOM_INTERACTION(CustomInteraction.QTI_CLASS_NAME, CustomInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            throw new QtiLogicException("customInteractions should have been intercepted before this method got called");
        }
    },
    /**
     * positionObjectStage
     */
    POSITION_OBJECT_STAGE(PositionObjectStage.QTI_CLASS_NAME, PositionObjectStage.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new PositionObjectStage(parent);
        }
    },
    /**
     * positionObjectInteraction
     */
    POSITION_OBJECT_INTERACTION(PositionObjectInteraction.QTI_CLASS_NAME, PositionObjectInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new PositionObjectInteraction(parent);
        }
    },
    /**
     * endAttemptInteraction
     */
    END_ATTEMPT_INTERACTION(EndAttemptInteraction.QTI_CLASS_NAME, EndAttemptInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new EndAttemptInteraction(parent);
        }
    },
    /**
     * inlineChoiceInteraction
     */
    INLINE_CHOICE_INTERACTION(InlineChoiceInteraction.QTI_CLASS_NAME, InlineChoiceInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new InlineChoiceInteraction(parent);
        }
    },
    /**
     * textEntyInteraction
     */
    TEXT_ENTRY_INTERACTION(TextEntryInteraction.QTI_CLASS_NAME, TextEntryInteraction.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new TextEntryInteraction(parent);
        }
    },
    //inlineStatic
    /**
     * br
     */
    BR(Br.QTI_CLASS_NAME, Br.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Br(parent);
        }
    },
    /**
     * img
     */
    IMG(Img.QTI_CLASS_NAME, Img.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Img(parent);
        }
    },
    /**
     * gap
     */
    GAP(Gap.QTI_CLASS_NAME, Gap.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Gap(parent);
        }
    },
    /**
     * hottext
     */
    HOTTEXT(Hottext.QTI_CLASS_NAME, Hottext.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Hottext(parent);
        }
    },
    /**
     * object
     */
    OBJECT(uk.ac.ed.ph.jqtiplus.node.content.xhtml.object.Object.QTI_CLASS_NAME, uk.ac.ed.ph.jqtiplus.node.content.xhtml.object.Object.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new uk.ac.ed.ph.jqtiplus.node.content.xhtml.object.Object(parent);
        }
    },
    /**
     * printedVariable
     */
    PRINTED_VARIABLE(PrintedVariable.QTI_CLASS_NAME, PrintedVariable.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new PrintedVariable(parent);
        }
    },
    /**
     * a
     */
    A(uk.ac.ed.ph.jqtiplus.node.content.xhtml.hypertext.A.QTI_CLASS_NAME, uk.ac.ed.ph.jqtiplus.node.content.xhtml.hypertext.A.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new A(parent);
        }
    },
    /**
     * abbr
     */
    ABBR(Abbr.QTI_CLASS_NAME, Abbr.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Abbr(parent);
        }
    },
    /**
     * acronym
     */
    ACRONYM(Acronym.QTI_CLASS_NAME, Acronym.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Acronym(parent);
        }
    },
    /**
     * b
     */
    B(uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.B.QTI_CLASS_NAME, uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.B.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new B(parent);
        }
    },
    /**
     * big
     */
    BIG(Big.QTI_CLASS_NAME, Big.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Big(parent);
        }
    },
    /**
     * cite
     */
    CITE(Cite.QTI_CLASS_NAME, Cite.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Cite(parent);
        }
    },
    /**
     * code
     */
    CODE(Code.QTI_CLASS_NAME, Code.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Code(parent);
        }
    },
    /**
     * dfn
     */
    DFN(Dfn.QTI_CLASS_NAME, Dfn.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Dfn(parent);
        }
    },
    /**
     * em
     */
    EM(Em.QTI_CLASS_NAME, Em.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Em(parent);
        }
    },
    /**
     * feedbackInline
     */
    FEEDBACK_INLINE(FeedbackInline.QTI_CLASS_NAME, FeedbackInline.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new FeedbackInline(parent);
        }
    },
    /**
     * i
     */
    I(uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.I.QTI_CLASS_NAME, uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.I.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new I(parent);
        }
    },
    /**
     * kbd
     */
    KBD(Kbd.QTI_CLASS_NAME, Kbd.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Kbd(parent);
        }
    },
    /**
     * q
     */
    Q(uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Q.QTI_CLASS_NAME, uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Q.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Q(parent);
        }
    },
    /**
     * samp
     */
    SAMP(Samp.QTI_CLASS_NAME, Samp.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Samp(parent);
        }
    },
    /**
     * small
     */
    SMALL(Small.QTI_CLASS_NAME, Small.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Small(parent);
        }
    },
    /**
     * span
     */
    SPAN(Span.QTI_CLASS_NAME, Span.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Span(parent);
        }
    },
    /**
     * strong
     */
    STRONG(Strong.QTI_CLASS_NAME, Strong.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Strong(parent);
        }
    },
    /**
     * sub
     */
    SUB(Sub.QTI_CLASS_NAME, Sub.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Sub(parent);
        }
    },
    /**
     * sup
     */
    SUP(Sup.QTI_CLASS_NAME, Sup.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Sup(parent);
        }
    },
    /**
     * tt
     */
    TT(Tt.QTI_CLASS_NAME, Tt.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Tt(parent);
        }
    },
    /**
     * var
     */
    VAR(Var.QTI_CLASS_NAME, Var.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Var(parent);
        }
    },
    /**
     * templateInline
     */
    TEMPLATE_INLINE(TemplateInline.QTI_CLASS_NAME, TemplateInline.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new TemplateInline(parent);
        }
    },
    /**
     * textRun
     */
    TEXT_RUN(TextRun.DISPLAY_NAME, TextRun.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new TextRun(parent, "");
        }
    },
    /**
     * param
     */
    PARAM(Param.QTI_CLASS_NAME, Param.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Param(parent);
        }
    },
    /**
     * caption
     */
    CAPTION(Caption.QTI_CLASS_NAME, Caption.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Caption(parent);
        }
    },
    /**
     * simpleChoice
     */
    SIMPLE_CHOICE(SimpleChoice.QTI_CLASS_NAME, SimpleChoice.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new SimpleChoice(parent);
        }
    },
    /**
     * simpleAssociableChoice
     */
    SIMPLE_ASSOCIABLE_CHOICE(SimpleAssociableChoice.QTI_CLASS_NAME, SimpleAssociableChoice.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new SimpleAssociableChoice(parent);
        }
    },
    /**
     * simpleMatchSet
     */
    SIMPLE_MATCH_SET(SimpleMatchSet.QTI_CLASS_NAME, SimpleMatchSet.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new SimpleMatchSet(parent);
        }
    },
    /**
     * gapImg
     */
    GAP_IMG(GapImg.QTI_CLASS_NAME, GapImg.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new GapImg(parent);
        }
    },
    /**
     * gapText
     */
    GAP_TEXT(GapText.QTI_CLASS_NAME, GapText.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new GapText(parent);
        }
    },
    /**
     * associableHotspot
     */
    ASSOCIABLE_HOTSPOT(AssociableHotspot.QTI_CLASS_NAME, AssociableHotspot.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new AssociableHotspot(parent);
        }
    },
    /**
     * hotspotChoice
     */
    HOTSPOT_CHOICE(HotspotChoice.QTI_CLASS_NAME, HotspotChoice.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new HotspotChoice(parent);
        }
    },
    /**
     * inlineChoice
     */
    INLINE_CHOICE(InlineChoice.QTI_CLASS_NAME, InlineChoice.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new InlineChoice(parent);
        }
    },
    /**
     * col
     */
    COL(Col.QTI_CLASS_NAME, Col.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Col(parent);
        }
    },
    /**
     * colgroup
     */
    COLGROUP(Colgroup.QTI_CLASS_NAME, Colgroup.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Colgroup(parent);
        }
    },
    /**
     * dd
     */
    DD(Dd.QTI_CLASS_NAME, Dd.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Dd(parent);
        }
    },
    /**
     * dt
     */
    DT(Dt.QTI_CLASS_NAME, Dt.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Dt(parent);
        }
    },
    /**
     * itemBody
     */
    ITEM_BODY(ItemBody.QTI_CLASS_NAME, ItemBody.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new ItemBody(parent);
        }
    },
    /**
     * li
     */
    LI(Li.QTI_CLASS_NAME, Li.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Li(parent);
        }
    },
    /**
     * prompt
     */
    PROMPT(Prompt.QTI_CLASS_NAME, Prompt.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Prompt(parent);
        }
    },
    /**
     * td
     */
    TD(Td.QTI_CLASS_NAME, Td.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Td(parent);
        }
    },
    /**
     * th
     */
    TH(Th.QTI_CLASS_NAME, Th.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Th(parent);
        }
    },
    /**
     * tbody
     */
    TBODY(Tbody.QTI_CLASS_NAME, Tbody.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Tbody(parent);
        }
    },
    /**
     * tfoot
     */
    TFOOT(Tfoot.QTI_CLASS_NAME, Tfoot.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Tfoot(parent);
        }
    },
    /**
     * thead
     */
    THEAD(Thead.QTI_CLASS_NAME, Thead.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Thead(parent);
        }
    },
    /**
     * tr
     */
    TR(Tr.QTI_CLASS_NAME, Tr.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new Tr(parent);
        }
    },
    /**
     * templateBlock
     */
    TEMPLATE_BLOCK(TemplateBlock.QTI_CLASS_NAME, TemplateBlock.class) {

        @Override
        public QtiNode create(final QtiNode parent) {
            return new TemplateBlock(parent);
        }
    };

    private static Map<String, ContentType> contentTypes;

    private static Map<String, ContentType> blockTypes;

    private static Map<String, ContentType> flowTypes;

    private static Map<String, ContentType> inlineTypes;

    private static Map<String, ContentType> objectFlowTypes;

    private static Map<String, ContentType> inlineStaticTypes;

    private static Map<String, ContentType> flowStaticTypes;

    private static Map<String, ContentType> textOrVariableTypes;

    private static Map<String, ContentType> blockStaticTypes;

    private static Map<String, ContentType> gapChoiceTypes;

    private static Map<String, ContentType> interactionTypes;

    static {
        contentTypes = new HashMap<String, ContentType>();
        for (final ContentType type : ContentType.values()) {
            contentTypes.put(type.qtiClassName, type);
        }

        blockTypes = new HashMap<String, ContentType>();
        for (final ContentType type : ContentType.values()) {
            if (Block.class.isAssignableFrom(type.clazz)) {
                blockTypes.put(type.qtiClassName, type);
            }
        }

        flowTypes = new HashMap<String, ContentType>();
        for (final ContentType type : ContentType.values()) {
            if (Flow.class.isAssignableFrom(type.clazz)) {
                flowTypes.put(type.qtiClassName, type);
            }
        }

        inlineTypes = new HashMap<String, ContentType>();
        for (final ContentType type : ContentType.values()) {
            if (Inline.class.isAssignableFrom(type.clazz)) {
                inlineTypes.put(type.qtiClassName, type);
            }
        }

        objectFlowTypes = new HashMap<String, ContentType>();
        for (final ContentType type : ContentType.values()) {
            if (ObjectFlow.class.isAssignableFrom(type.clazz)) {
                objectFlowTypes.put(type.qtiClassName, type);
            }
        }

        inlineStaticTypes = new HashMap<String, ContentType>();
        for (final ContentType type : ContentType.values()) {
            if (InlineStatic.class.isAssignableFrom(type.clazz)) {
                inlineStaticTypes.put(type.qtiClassName, type);
            }
        }

        flowStaticTypes = new HashMap<String, ContentType>();
        for (final ContentType type : ContentType.values()) {
            if (FlowStatic.class.isAssignableFrom(type.clazz)) {
                flowStaticTypes.put(type.qtiClassName, type);
            }
        }

        textOrVariableTypes = new HashMap<String, ContentType>();
        for (final ContentType type : ContentType.values()) {
            if (TextOrVariable.class.isAssignableFrom(type.clazz)) {
                textOrVariableTypes.put(type.qtiClassName, type);
            }
        }

        blockStaticTypes = new HashMap<String, ContentType>();
        for (final ContentType type : ContentType.values()) {
            if (BlockStatic.class.isAssignableFrom(type.clazz)) {
                blockStaticTypes.put(type.qtiClassName, type);
            }
        }

        gapChoiceTypes = new HashMap<String, ContentType>();
        for (final ContentType type : ContentType.values()) {
            if (GapChoice.class.isAssignableFrom(type.clazz)) {
                gapChoiceTypes.put(type.qtiClassName, type);
            }
        }

        interactionTypes = new HashMap<String, ContentType>();
        for (final ContentType type : ContentType.values()) {
            if (Interaction.class.isAssignableFrom(type.clazz)) {
                interactionTypes.put(type.qtiClassName, type);
            }
        }
    }

    /**
     * Get block types
     *
     * @return block types
     */
    public static Collection<ContentType> blockValues() {
        return blockTypes.values();
    }

    /**
     * Get flow types
     *
     * @return flow types
     */
    public static Collection<ContentType> flowValues() {
        return flowTypes.values();
    }

    /**
     * Get inline types
     *
     * @return inline types
     */
    public static Collection<ContentType> inlineValues() {
        return inlineTypes.values();
    }

    /**
     * Get objectFlow types
     *
     * @return objectFlow types
     */
    public static Collection<ContentType> objectFlowValues() {
        return objectFlowTypes.values();
    }

    /**
     * Get inlineStatic types
     *
     * @return inlineStatic types
     */
    public static Collection<ContentType> inlineStaticValues() {
        return inlineStaticTypes.values();
    }

    /**
     * Get flowStatic types
     *
     * @return flowStatic types
     */
    public static Collection<ContentType> flowStaticValues() {
        return flowStaticTypes.values();
    }

    /**
     * Get textOrVariable types
     *
     * @return textOrVariable types
     */
    public static Collection<ContentType> textOrVariableValues() {
        return textOrVariableTypes.values();
    }

    /**
     * Get blockStatic types
     *
     * @return blockStatic types
     */
    public static Collection<ContentType> blockStaticValues() {
        return blockStaticTypes.values();
    }

    /**
     * Get gapChoice types
     *
     * @return gapChoice types
     */
    public static Collection<ContentType> gapChoiceValues() {
        return gapChoiceTypes.values();
    }

    /**
     * Get interaction types
     *
     * @return interaction types
     */
    public static Collection<ContentType> interactionValues() {
        return interactionTypes.values();
    }

    private String qtiClassName;

    private Class<? extends AbstractNode> clazz;

    ContentType(final String type, final Class<? extends AbstractNode> clazz) {
        this.qtiClassName = type;
        this.clazz = clazz;
    }

    /**
     * Gets QTI_CLASS_NAME of this block type.
     *
     * @return QTI_CLASS_NAME of this block type
     */
    public String getQtiClassName() {
        return qtiClassName;
    }

    /**
     * Gets QTI class of this block type.
     *
     * @return clazz of this block type
     */
    public Class<? extends AbstractNode> getClazz() {
        return clazz;
    }

    /**
     * Creates block element.
     *
     * @param parent parent of created block
     * @return created block
     */
    public abstract QtiNode create(QtiNode parent);

    @Override
    public String toString() {
        return qtiClassName;
    }

    public static Set<String> getQtiClassNames() {
        return contentTypes.keySet();
    }

    public static Set<String> getBlockQtiClassNames() {
        return blockTypes.keySet();
    }

    public static Set<String> getFlowQtiClassNames() {
        return flowTypes.keySet();
    }

    public static Set<String> getInlineQtiClassNames() {
        return inlineTypes.keySet();
    }

    public static Set<String> getObjectFlowQtiClassNames() {
        return objectFlowTypes.keySet();
    }

    public static Set<String> getInlineStaticQtiClassNames() {
        return inlineStaticTypes.keySet();
    }

    public static Set<String> getFlowStaticQtiClassNames() {
        return flowStaticTypes.keySet();
    }

    public static Set<String> getTextOrVariableQtiClassNames() {
        return textOrVariableTypes.keySet();
    }

    public static Set<String> getBlockStaticQtiClassNames() {
        return blockStaticTypes.keySet();
    }

    public static Set<String> getGapChoiceQtiClassNames() {
        return gapChoiceTypes.keySet();
    }

    public static Set<String> getInteractionTypeQtiClassNames() {
        return interactionTypes.keySet();
    }

    /**
     * Gets content type for given QTI_CLASS_NAME.
     *
     * @param qtiClassName QTI_CLASS_NAME
     * @return content type for given QTI_CLASS_NAME
     */
    public static ContentType getType(final String qtiClassName) {
        return contentTypes.get(qtiClassName);
    }

    /**
     * Creates content element.
     *
     * @param parent parent of created content
     * @param qtiClassName QTI_CLASS_NAME of created content
     * @return created content
     */
    public static QtiNode getInstance(final QtiNode parent, final String qtiClassName) {
        final ContentType contentType = contentTypes.get(qtiClassName);

        if (contentType == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }

        return contentType.create(parent);
    }

    /**
     * Creates block element.
     *
     * @param parent parent of created block
     * @param qtiClassName QTI_CLASS_NAME of created block
     * @return created block
     */
    public static Block getBlockInstance(final QtiNode parent, final String qtiClassName) {
        final ContentType blockType = blockTypes.get(qtiClassName);

        if (blockType == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }

        return (Block) blockType.create(parent);
    }

    /**
     * Creates inline element.
     *
     * @param parent parent of created inline
     * @param qtiClassName QTI_CLASS_NAME of created inline
     * @return created inline
     */
    public static Inline getInlineInstance(final QtiNode parent, final String qtiClassName) {
        final ContentType inlineType = inlineTypes.get(qtiClassName);

        if (inlineType == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }

        return (Inline) inlineType.create(parent);
    }

    /**
     * Creates flow element.
     *
     * @param parent parent of created flow
     * @param qtiClassName QTI_CLASS_NAME of created flow
     * @return created flow
     */
    public static Flow getFlowInstance(final QtiNode parent, final String qtiClassName) {
        final ContentType flowType = flowTypes.get(qtiClassName);

        if (flowType == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }

        return (Flow) flowType.create(parent);
    }

    /**
     * Creates objectFlow element.
     *
     * @param parent parent of created objectFlow
     * @param qtiClassName QTI_CLASS_NAME of created objectFlow
     * @return created objectFlow
     */
    public static ObjectFlow getObjectFlowInstance(final QtiNode parent, final String qtiClassName) {
        final ContentType flowType = objectFlowTypes.get(qtiClassName);

        if (flowType == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }

        return (ObjectFlow) flowType.create(parent);
    }

    /**
     * Creates inlineStatic element.
     *
     * @param parent parent of created inlineStatic
     * @param qtiClassName QTI_CLASS_NAME of created inlineStatic
     * @return created inlineStatic
     */
    public static InlineStatic getInlineStaticInstance(final QtiNode parent, final String qtiClassName) {
        final ContentType inlineType = inlineStaticTypes.get(qtiClassName);

        if (inlineType == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }

        return (InlineStatic) inlineType.create(parent);
    }

    /**
     * Creates flowStatic element.
     *
     * @param parent parent of created flowStatic
     * @param qtiClassName QTI_CLASS_NAME of created flowStatic
     * @return created flowStatic
     */
    public static FlowStatic getFlowStaticInstance(final QtiNode parent, final String qtiClassName) {
        final ContentType flowStaticType = flowStaticTypes.get(qtiClassName);

        if (flowStaticType == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }

        return (FlowStatic) flowStaticType.create(parent);
    }

    /**
     * Creates textOrVariable element.
     *
     * @param parent parent of created textOrVariable
     * @param qtiClassName QTI_CLASS_NAME of created textOrVariable
     * @return created textOrVariable
     */
    public static TextOrVariable getTextOrVariableInstance(final QtiNode parent, final String qtiClassName) {
        final ContentType textOrVariable = textOrVariableTypes.get(qtiClassName);

        if (textOrVariable == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }

        return (TextOrVariable) textOrVariable.create(parent);
    }

    /**
     * Creates blockStatic element.
     *
     * @param parent parent of created blockStatic
     * @param qtiClassName QTI_CLASS_NAME of created blockStatic
     * @return created blockStatic
     */
    public static BlockStatic getBlockStaticInstance(final QtiNode parent, final String qtiClassName) {
        final ContentType blockStatic = blockStaticTypes.get(qtiClassName);

        if (blockStatic == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }

        return (BlockStatic) blockStatic.create(parent);
    }

    /**
     * Creates gapChoice element.
     *
     * @param parent parent of created gapChoice
     * @param qtiClassName QTI_CLASS_NAME of created gapChoice
     * @return created gapChoice
     */
    public static GapChoice getGapChoiceInstance(final QtiNode parent, final String qtiClassName) {
        final ContentType gapChoice = gapChoiceTypes.get(qtiClassName);

        if (gapChoice == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }

        return (GapChoice) gapChoice.create(parent);
    }

    /**
     * Creates interaction element.
     *
     * @param parent parent of created interaction
     * @param qtiClassName QTI_CLASS_NAME of created interaction
     * @return created interaction
     */
    public static Interaction getInteractionInstance(final QtiNode parent, final String qtiClassName) {
        final ContentType interaction = interactionTypes.get(qtiClassName);

        if (interaction == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }

        return (Interaction) interaction.create(parent);
    }
}
