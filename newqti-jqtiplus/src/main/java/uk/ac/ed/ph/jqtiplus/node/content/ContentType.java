/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
*/

package uk.ac.ed.ph.jqtiplus.node.content;

import uk.ac.ed.ph.jqtiplus.control.QTILogicException;
import uk.ac.ed.ph.jqtiplus.exception.QTINodeGroupException;
import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.XmlObject;
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


/**
 * All the types of content.
 * 
 * @author Jonathon Hare
 *
 */
public enum ContentType {
    
    //blockInteraction:
    /**
     * associateInteraction
     */    
    ASSOCIATE_INTERACTION(AssociateInteraction.CLASS_TAG, AssociateInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new AssociateInteraction(parent);
        }
    },
    /**
     * choiceInteraction
     */
    CHOICE_INTERACTION(ChoiceInteraction.CLASS_TAG, ChoiceInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new ChoiceInteraction(parent);
        }
    },
    /**
     * drawingInteraction
     */
    DRAWING_INTERACTION(DrawingInteraction.CLASS_TAG, DrawingInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new DrawingInteraction(parent);
        }
    },
    /**
     * extendedTextInteraction
     */
    EXTENDED_TEXT_INTERACTION(ExtendedTextInteraction.CLASS_TAG, ExtendedTextInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new ExtendedTextInteraction(parent);
        }
    },
    /**
     * gapMatchInteraction
     */
    GAP_MATCH_INTERACTION(GapMatchInteraction.CLASS_TAG, GapMatchInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new GapMatchInteraction(parent);
        }
    },
    /**
     * graphicAssociateInteraction
     */
    GRAPHIC_ASSOCIATE_INTERACTION(GraphicAssociateInteraction.CLASS_TAG, GraphicAssociateInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new GraphicAssociateInteraction(parent);
        }
    },
    /**
     * graphicGapMatchInteraction
     */
    GRAPHIC_GAP_MATCH_INTERACTION(GraphicGapMatchInteraction.CLASS_TAG, GraphicGapMatchInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new GraphicGapMatchInteraction(parent);
        }
    },
    /**
     * graphicOrderInteration 
     */
    GRAPHIC_ORDER_INTERACTION(GraphicOrderInteraction.CLASS_TAG, GraphicOrderInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new GraphicOrderInteraction(parent);
        }
    },
    /**
     * hotspotInteraction 
     */
    HOTSPOT_INTERACTION(HotspotInteraction.CLASS_TAG, HotspotInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new HotspotInteraction(parent);
        }
    },
    /**
     * selectPointInteraction 
     */
    SELECT_POINT_INTERACTION(SelectPointInteraction.CLASS_TAG, SelectPointInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new SelectPointInteraction(parent);
        }
    },
    /**
     * hottextInteraction
     */
    HOTTEXT_INTERACTION(HottextInteraction.CLASS_TAG, HottextInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new HottextInteraction(parent);
        }
    },
    /**
     * matchInteraction 
     */
    MATCH_INTERACTION(MatchInteraction.CLASS_TAG, MatchInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new MatchInteraction(parent);
        }
    },
    /**
     * mediaInteraction
     */
    MEDIA_INTERACTION(MediaInteraction.CLASS_TAG, MediaInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new MediaInteraction(parent);
        }
    },
    /**
     * orderInteraction
     */
    ORDER_INTERACTION(OrderInteraction.CLASS_TAG, OrderInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new OrderInteraction(parent);
        }
    },
    /**
     * sliderInteraction
     */
    SLIDER_INTERACTION(SliderInteraction.CLASS_TAG, SliderInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new SliderInteraction(parent);
        }
    },
    /**
     * uploadInteraction 
     */
    UPLOAD_INTERACTION(UploadInteraction.CLASS_TAG, UploadInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new UploadInteraction(parent);
        }
    },
    //blockStatic,
    /**
     * address 
     */
    ADDRESS(Address.CLASS_TAG, Address.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Address(parent);
        }
    },
    /**
     * h1 
     */
    H1(uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H1.CLASS_TAG, uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H1.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new H1(parent);
        }
    },
    /**
     * h2 
     */
    H2(uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H2.CLASS_TAG, uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H2.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new H2(parent);
        }
    },
    /**
     * h3 
     */
    H3(uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H3.CLASS_TAG, uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H3.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new H3(parent);
        }
    },
    /**
     * h4 
     */
    H4(uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H4.CLASS_TAG, uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H4.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new H4(parent);
        }
    },
    /**
     * h5 
     */
    H5(uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H5.CLASS_TAG, uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H5.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new H5(parent);
        }
    },
    /**
     * h6 
     */
    H6(uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H6.CLASS_TAG, uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.H6.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new H6(parent);
        }
    },
    /**
     * p 
     */
    P(uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.P.CLASS_TAG, uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.P.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new P(parent);
        }
    },
    /**
     * pre
     */
    PRE(Pre.CLASS_TAG, Pre.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Pre(parent);
        }
    },
    /**
     * div 
     */
    DIV(Div.CLASS_TAG, Div.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Div(parent);
        }
    },
    
    INFOCONTROL(InfoControl.CLASS_TAG, InfoControl.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new InfoControl(parent);
        }
    },
    
    /**
     * dl 
     */
    DL(Dl.CLASS_TAG, Dl.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Dl(parent);
        }
    },
    /**
     * hr 
     */
    HR(Hr.CLASS_TAG, Hr.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Hr(parent);
        }
    },
    /**
     * math 
     */
    MATH(Math.CLASS_TAG, Math.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Math(parent);
        }
    },
    /**
     * ol 
     */
    OL(Ol.CLASS_TAG, Ol.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Ol(parent);
        }
    },
    /**
     * blockquote
     */
    BLOCKQUOTE(Blockquote.CLASS_TAG, Blockquote.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Blockquote(parent);
        }
    },
    /**
     * feedbackBlock
     */
    FEEDBACK_BLOCK(FeedbackBlock.CLASS_TAG, FeedbackBlock.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new FeedbackBlock(parent);
        }
    },
    /**
     * rubricBlock 
     */
    RUBRIC_BLOCK(RubricBlock.CLASS_TAG, RubricBlock.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new RubricBlock(parent);
        }
    },
    /**
     * table 
     */
    TABLE(Table.CLASS_TAG, Table.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Table(parent);
        }
    },
    /**
     * ul 
     */
    UL(Ul.CLASS_TAG, Ul.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Ul(parent);
        }
    },    
    /**
     * customInteraction 
     */
    CUSTOM_INTERACTION(CustomInteraction.CLASS_TAG, CustomInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            throw new QTILogicException("customInteractions should have been intercepted before this method got called");
        }
    },
    /**
     * positionObjectStage 
     */
    POSITION_OBJECT_STAGE(PositionObjectStage.CLASS_TAG, PositionObjectStage.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new PositionObjectStage(parent);
        }
    },
    /**
     * positionObjectInteraction 
     */
    POSITION_OBJECT_INTERACTION(PositionObjectInteraction.CLASS_TAG, PositionObjectInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new PositionObjectInteraction(parent);
        }
    },
    /**
     * endAttemptInteraction 
     */
    END_ATTEMPT_INTERACTION(EndAttemptInteraction.CLASS_TAG, EndAttemptInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new EndAttemptInteraction(parent);
        }
    },
    /**
     * inlineChoiceInteraction
     */
    INLINE_CHOICE_INTERACTION(InlineChoiceInteraction.CLASS_TAG, InlineChoiceInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new InlineChoiceInteraction(parent);
        }
    },
    /**
     * textEntyInteraction
     */
    TEXT_ENTRY_INTERACTION(TextEntryInteraction.CLASS_TAG, TextEntryInteraction.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new TextEntryInteraction(parent);
        }
    },
    //inlineStatic
    /**
     * br 
     */
    BR(Br.CLASS_TAG, Br.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Br(parent);
        }
    },
    /**
     * img 
     */
    IMG(Img.CLASS_TAG, Img.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Img(parent);
        }
    }, 
    /**
     * gap 
     */
    GAP(Gap.CLASS_TAG, Gap.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Gap(parent);
        }
    },
    /**
     * hottext
     */
    HOTTEXT(Hottext.CLASS_TAG, Hottext.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Hottext(parent);
        }
    }, 
    /**
     * object 
     */
    OBJECT(uk.ac.ed.ph.jqtiplus.node.content.xhtml.object.Object.CLASS_TAG, uk.ac.ed.ph.jqtiplus.node.content.xhtml.object.Object.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new uk.ac.ed.ph.jqtiplus.node.content.xhtml.object.Object(parent);
        }
    },
    /**
     * printedVariable
     */
    PRINTED_VARIABLE(PrintedVariable.CLASS_TAG, PrintedVariable.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new PrintedVariable(parent);
        }
    },
    /**
     * a 
     */
    A(uk.ac.ed.ph.jqtiplus.node.content.xhtml.hypertext.A.CLASS_TAG, uk.ac.ed.ph.jqtiplus.node.content.xhtml.hypertext.A.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new A(parent);
        }
    },
    /**
     * abbr 
     */
    ABBR(Abbr.CLASS_TAG, Abbr.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Abbr(parent);
        }
    },
    /**
     * acronym
     */
    ACRONYM(Acronym.CLASS_TAG, Acronym.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Acronym(parent);
        }
    },
    /**
     * b 
     */
    B(uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.B.CLASS_TAG, uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.B.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new B(parent);
        }
    },
    /**
     * big 
     */
    BIG(Big.CLASS_TAG, Big.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Big(parent);
        }
    },
    /**
     * cite
     */
    CITE(Cite.CLASS_TAG, Cite.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Cite(parent);
        }
    },
    /**
     * code 
     */
    CODE(Code.CLASS_TAG, Code.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Code(parent);
        }
    },
    /**
     * dfn 
     */
    DFN(Dfn.CLASS_TAG, Dfn.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Dfn(parent);
        }
    },
    /**
     * em 
     */
    EM(Em.CLASS_TAG, Em.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Em(parent);
        }
    },
    /**
     * feedbackInline
     */
    FEEDBACK_INLINE(FeedbackInline.CLASS_TAG, FeedbackInline.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new FeedbackInline(parent);
        }
    },
    /**
     * i 
     */
    I(uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.I.CLASS_TAG, uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.I.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new I(parent);
        }
    },
    /**
     * kbd 
     */
    KBD(Kbd.CLASS_TAG, Kbd.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Kbd(parent);
        }
    },
    /**
     * q 
     */
    Q(uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Q.CLASS_TAG, uk.ac.ed.ph.jqtiplus.node.content.xhtml.text.Q.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Q(parent);
        }
    },
    /**
     * samp
     */
    SAMP(Samp.CLASS_TAG, Samp.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Samp(parent);
        }
    },
    /**
     * small 
     */
    SMALL(Small.CLASS_TAG, Small.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Small(parent);
        }
    },
    /**
     * span 
     */
    SPAN(Span.CLASS_TAG, Span.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Span(parent);
        }
    },
    /**
     * strong 
     */
    STRONG(Strong.CLASS_TAG, Strong.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Strong(parent);
        }
    },
    /**
     * sub 
     */
    SUB(Sub.CLASS_TAG, Sub.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Sub(parent);
        }
    },
    /**
     * sup 
     */
    SUP(Sup.CLASS_TAG, Sup.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Sup(parent);
        }
    },
    /**
     * tt 
     */
    TT(Tt.CLASS_TAG, Tt.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Tt(parent);
        }
    },
    /**
     * var 
     */
    VAR(Var.CLASS_TAG, Var.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Var(parent);
        }
    }, 
    /**
     * templateInline 
     */
    TEMPLATE_INLINE(TemplateInline.CLASS_TAG, TemplateInline.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new TemplateInline(parent);
        }
    },
    /**
     * textRun 
     */
    TEXT_RUN(TextRun.DISPLAY_NAME, TextRun.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new TextRun(parent, "");
        }
    },
    /**
     * param
     */
    PARAM(Param.CLASS_TAG, Param.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Param(parent);
        }
    },
    /**
     * caption 
     */
    CAPTION(Caption.CLASS_TAG, Caption.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Caption(parent);
        }
    },
    /**
     * simpleChoice
     */
    SIMPLE_CHOICE(SimpleChoice.CLASS_TAG, SimpleChoice.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new SimpleChoice(parent);
        }
    },
    /**
     * simpleAssociableChoice
     */
    SIMPLE_ASSOCIABLE_CHOICE(SimpleAssociableChoice.CLASS_TAG, SimpleAssociableChoice.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new SimpleAssociableChoice(parent);
        }
    },
    /**
     * simpleMatchSet
     */
    SIMPLE_MATCH_SET(SimpleMatchSet.CLASS_TAG, SimpleMatchSet.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new SimpleMatchSet(parent);
        }
    },
    /**
     * gapImg 
     */
    GAP_IMG(GapImg.CLASS_TAG, GapImg.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new GapImg(parent);
        }
    },
    /**
     * gapText
     */
    GAP_TEXT(GapText.CLASS_TAG, GapText.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new GapText(parent);
        }
    },
    /**
     * associableHotspot
     */
    ASSOCIABLE_HOTSPOT(AssociableHotspot.CLASS_TAG, AssociableHotspot.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new AssociableHotspot(parent);
        }
    },
    /**
     * hotspotChoice
     */
    HOTSPOT_CHOICE(HotspotChoice.CLASS_TAG, HotspotChoice.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new HotspotChoice(parent);
        }
    },
    /**
     * inlineChoice
     */
    INLINE_CHOICE(InlineChoice.CLASS_TAG, InlineChoice.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new InlineChoice(parent);
        }
    },
    /**
     * col 
     */
    COL(Col.CLASS_TAG, Col.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Col(parent);
        }
    },
    /**
     * colgroup
     */
    COLGROUP(Colgroup.CLASS_TAG, Colgroup.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Colgroup(parent);
        }
    },
    /**
     * dd 
     */
    DD(Dd.CLASS_TAG, Dd.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Dd(parent);
        }
    },
    /**
     * dt 
     */
    DT(Dt.CLASS_TAG, Dt.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Dt(parent);
        }
    },
    /**
     * itemBody
     */
    ITEM_BODY(ItemBody.CLASS_TAG, ItemBody.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new ItemBody(parent);
        }
    },
    /**
     * li
     */
    LI(Li.CLASS_TAG, Li.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Li(parent);
        }
    },
    /**
     * prompt
     */
    PROMPT(Prompt.CLASS_TAG, Prompt.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Prompt(parent);
        }
    },
    /**
     * td 
     */
    TD(Td.CLASS_TAG, Td.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Td(parent);
        }
    },
    /**
     * th 
     */
    TH(Th.CLASS_TAG, Th.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Th(parent);
        }
    },
    /**
     * tbody
     */
    TBODY(Tbody.CLASS_TAG, Tbody.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Tbody(parent);
        }
    },
    /**
     * tfoot
     */
    TFOOT(Tfoot.CLASS_TAG, Tfoot.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Tfoot(parent);
        }
    },
    /**
     * thead 
     */
    THEAD(Thead.CLASS_TAG, Thead.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Thead(parent);
        }
    },
    /**
     * tr 
     */
    TR(Tr.CLASS_TAG, Tr.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new Tr(parent);
        }
    },
    /**
     * templateBlock
     */
    TEMPLATE_BLOCK(TemplateBlock.CLASS_TAG, TemplateBlock.class) {
        @Override
        public XmlObject create(XmlObject parent) {
            return new TemplateBlock(parent);
        }
    }
    ;
    
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
        for (ContentType type : ContentType.values())
        {
            contentTypes.put(type.classTag, type);
        }
        
        blockTypes = new HashMap<String, ContentType>();
        for (ContentType type : ContentType.values()) 
        {
            if (Block.class.isAssignableFrom(type.clazz))
                blockTypes.put(type.classTag, type);
        }
        
        flowTypes = new HashMap<String, ContentType>();
        for (ContentType type : ContentType.values()) 
        {
            if (Flow.class.isAssignableFrom(type.clazz))
                flowTypes.put(type.classTag, type);
        }
        
        inlineTypes = new HashMap<String, ContentType>();
        for (ContentType type : ContentType.values()) 
        {
            if (Inline.class.isAssignableFrom(type.clazz))
                inlineTypes.put(type.classTag, type);
        }
        
        objectFlowTypes = new HashMap<String, ContentType>();
        for (ContentType type : ContentType.values()) 
        {
            if (ObjectFlow.class.isAssignableFrom(type.clazz))
                objectFlowTypes.put(type.classTag, type);
        }
        
        inlineStaticTypes = new HashMap<String, ContentType>();
        for (ContentType type : ContentType.values()) 
        {
            if (InlineStatic.class.isAssignableFrom(type.clazz))
                inlineStaticTypes.put(type.classTag, type);
        }
        
        flowStaticTypes = new HashMap<String, ContentType>();
        for (ContentType type : ContentType.values()) 
        {
            if (FlowStatic.class.isAssignableFrom(type.clazz))
                flowStaticTypes.put(type.classTag, type);
        }
        
        textOrVariableTypes = new HashMap<String, ContentType>();
        for (ContentType type : ContentType.values()) 
        {
            if (TextOrVariable.class.isAssignableFrom(type.clazz))
                textOrVariableTypes.put(type.classTag, type);
        }
        
        blockStaticTypes = new HashMap<String, ContentType>();
        for (ContentType type : ContentType.values()) 
        {
            if (BlockStatic.class.isAssignableFrom(type.clazz))
                blockStaticTypes.put(type.classTag, type);
        }
        
        gapChoiceTypes = new HashMap<String, ContentType>();
        for (ContentType type : ContentType.values()) 
        {
            if (GapChoice.class.isAssignableFrom(type.clazz))
                gapChoiceTypes.put(type.classTag, type);
        }
        
        interactionTypes = new HashMap<String, ContentType>();
        for (ContentType type : ContentType.values()) 
        {
            if (Interaction.class.isAssignableFrom(type.clazz))
                interactionTypes.put(type.classTag, type);
        }
    }
    
    /**
     * Get block types
     * @return block types
     */
    public static Collection<ContentType> blockValues() {
        return blockTypes.values();
    }
    
    /**
     * Get flow types
     * @return flow types
     */
    public static Collection<ContentType> flowValues() {
        return flowTypes.values();
    }
    
    /**
     * Get inline types
     * @return inline types
     */
    public static Collection<ContentType> inlineValues() {
        return inlineTypes.values();
    }
    
    /**
     * Get objectFlow types
     * @return objectFlow types
     */
    public static Collection<ContentType> objectFlowValues() {
        return objectFlowTypes.values();
    }
    
    /**
     * Get inlineStatic types
     * @return inlineStatic types
     */
    public static Collection<ContentType> inlineStaticValues() {
        return inlineStaticTypes.values();
    }
    
    /**
     * Get flowStatic types
     * @return flowStatic types
     */
    public static Collection<ContentType> flowStaticValues() {
        return flowStaticTypes.values();
    }
    
    /**
     * Get textOrVariable types
     * @return textOrVariable types
     */
    public static Collection<ContentType> textOrVariableValues() {
        return textOrVariableTypes.values();
    }
    
    /**
     * Get blockStatic types
     * @return blockStatic types
     */
    public static Collection<ContentType> blockStaticValues() {
        return blockStaticTypes.values();
    }
    
    /**
     * Get gapChoice types
     * @return gapChoice types
     */
    public static Collection<ContentType> gapChoiceValues() {
        return gapChoiceTypes.values();
    }
    
    /**
     * Get interaction types
     * @return interaction types
     */
    public static Collection<ContentType> interactionValues() {
        return interactionTypes.values();
    }
    
    private String classTag;
    private Class<? extends AbstractNode> clazz;
    
    ContentType(String type, Class<? extends AbstractNode> clazz) {
        this.classTag = type;
        this.clazz = clazz;
    }
    
    /**
     * Gets CLASS_TAG of this block type.
     *
     * @return CLASS_TAG of this block type
     */
    public String getClassTag()
    {
        return classTag;
    }

    /**
     * Gets QTI class of this block type.
     *
     * @return clazz of this block type
     */
    public Class<? extends AbstractNode> getClazz()
    {
        return clazz;
    }
    
    /**
     * Creates block element.
     *
     * @param parent parent of created block
     * @return created block
     */
    public abstract XmlObject create(XmlObject parent);

    @Override
    public String toString()
    {
        return classTag;
    }

    /**
     * Gets content type for given CLASS_TAG.
     *
     * @param classTag CLASS_TAG
     * @return content type for given CLASS_TAG
     */
    public static ContentType getType(String classTag)
    {
        ContentType contentType = contentTypes.get(classTag);

        if (contentType == null)
            throw new QTINodeGroupException("Unsupported content element: " + classTag);

        return contentType;
    }
    
    /**
     * Creates content element.
     *
     * @param parent parent of created content
     * @param classTag CLASS_TAG of created content
     * @return created content
     */
    public static XmlObject getInstance(XmlObject parent, String classTag)
    {
        ContentType contentType = contentTypes.get(classTag);

        if (contentType == null)
            throw new QTIParseException("Unsupported content element: " + classTag);

        return contentType.create(parent);
    }
    
    /**
     * Creates block element.
     *
     * @param parent parent of created block
     * @param classTag CLASS_TAG of created block
     * @return created block
     */
    public static Block getBlockInstance(XmlObject parent, String classTag)
    {
        ContentType blockType = blockTypes.get(classTag);

        if (blockType == null)
            throw new QTIParseException("Unsupported block element: " + classTag);

        return (Block) blockType.create(parent);
    }
    
    /**
     * Creates inline element.
     *
     * @param parent parent of created inline
     * @param classTag CLASS_TAG of created inline
     * @return created inline
     */
    public static Inline getInlineInstance(XmlObject parent, String classTag)
    {
        ContentType inlineType = inlineTypes.get(classTag);

        if (inlineType == null)
            throw new QTIParseException("Unsupported inline element: " + classTag);

        return (Inline) inlineType.create(parent);
    }

    /**
     * Creates flow element.
     *
     * @param parent parent of created flow
     * @param classTag CLASS_TAG of created flow
     * @return created flow
     */
    public static Flow getFlowInstance(XmlObject parent, String classTag)
    {
        ContentType flowType = flowTypes.get(classTag);

        if (flowType == null)
            throw new QTIParseException("Unsupported flow element: " + classTag);

        return (Flow) flowType.create(parent);
    }
    
    /**
     * Creates objectFlow element.
     *
     * @param parent parent of created objectFlow
     * @param classTag CLASS_TAG of created objectFlow
     * @return created objectFlow
     */
    public static ObjectFlow getObjectFlowInstance(XmlObject parent, String classTag)
    {
        ContentType flowType = objectFlowTypes.get(classTag);

        if (flowType == null)
            throw new QTIParseException("Unsupported objectFlow element: " + classTag);

        return (ObjectFlow) flowType.create(parent);
    }
    
    /**
     * Creates inlineStatic element.
     *
     * @param parent parent of created inlineStatic
     * @param classTag CLASS_TAG of created inlineStatic
     * @return created inlineStatic
     */
    public static InlineStatic getInlineStaticInstance(XmlObject parent, String classTag)
    {
        ContentType inlineType = inlineStaticTypes.get(classTag);

        if (inlineType == null)
            throw new QTIParseException("Unsupported inlineStatic element: " + classTag);

        return (InlineStatic) inlineType.create(parent);
    }

    /**
     * Creates flowStatic element.
     *
     * @param parent parent of created flowStatic
     * @param classTag CLASS_TAG of created flowStatic
     * @return created flowStatic
     */
    public static FlowStatic getFlowStaticInstance(XmlObject parent, String classTag)
    {
        ContentType flowStaticType = flowStaticTypes.get(classTag);

        if (flowStaticType == null)
            throw new QTIParseException("Unsupported flowStatic element: " + classTag);

        return (FlowStatic) flowStaticType.create(parent);
    }
    
    /**
     * Creates textOrVariable element.
     *
     * @param parent parent of created textOrVariable
     * @param classTag CLASS_TAG of created textOrVariable
     * @return created textOrVariable
     */
    public static TextOrVariable getTextOrVariableInstance(XmlObject parent, String classTag)
    {
        ContentType textOrVariable = textOrVariableTypes.get(classTag);

        if (textOrVariable == null)
            throw new QTIParseException("Unsupported textOrVariable element: " + classTag);

        return (TextOrVariable) textOrVariable.create(parent);
    }
    
    /**
     * Creates blockStatic element.
     *
     * @param parent parent of created blockStatic
     * @param classTag CLASS_TAG of created blockStatic
     * @return created blockStatic
     */
    public static BlockStatic getBlockStaticInstance(XmlObject parent, String classTag)
    {
        ContentType blockStatic = blockStaticTypes.get(classTag);

        if (blockStatic == null)
            throw new QTIParseException("Unsupported blockStatic element: " + classTag);

        return (BlockStatic) blockStatic.create(parent);
    }
    
    /**
     * Creates gapChoice element.
     *
     * @param parent parent of created gapChoice
     * @param classTag CLASS_TAG of created gapChoice
     * @return created gapChoice
     */
    public static GapChoice getGapChoiceInstance(XmlObject parent, String classTag)
    {
        ContentType gapChoice = gapChoiceTypes.get(classTag);

        if (gapChoice == null)
            throw new QTIParseException("Unsupported gapChoice element: " + classTag);

        return (GapChoice) gapChoice.create(parent);
    }
    
    /**
     * Creates interaction element.
     *
     * @param parent parent of created interaction
     * @param classTag CLASS_TAG of created interaction
     * @return created interaction
     */
    public static Interaction getInteractionInstance(XmlObject parent, String classTag)
    {
        ContentType interaction = interactionTypes.get(classTag);

        if (interaction == null)
            throw new QTIParseException("Unsupported interaction element: " + classTag);

        return (Interaction) interaction.create(parent);
    }
}
