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

import static org.junit.Assert.assertEquals;

import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.TextRun;
import uk.ac.ed.ph.jqtiplus.node.content.variable.FeedbackBlock;
import uk.ac.ed.ph.jqtiplus.node.content.variable.FeedbackInline;
import uk.ac.ed.ph.jqtiplus.node.content.variable.PrintedVariable;
import uk.ac.ed.ph.jqtiplus.node.content.variable.RubricBlock;
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
import uk.ac.ed.ph.jqtiplus.node.item.interaction.MatchInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.MediaInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.OrderInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.PositionObjectInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Prompt;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.SelectPointInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.SliderInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.TextEntryInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.UploadInteraction;
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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Basic test of content classes, taken from old JQTI
 *
 * @author Jiri Kajaba
 */
public class ContentTest {

    public List<Object[]> contentClasses = Arrays.asList(new Object[][] {
            { "a", new A(null) },
            { "abbr", new Abbr(null) },
            { "acronym", new Acronym(null) },
            { "address", new Address(null) }, 
            { "associableHotspot", new AssociableHotspot(null) },
            { "associateInteraction", new AssociateInteraction(null) },
            { "b", new B(null) },
            { "big", new Big(null) },
            { "blockquote", new Blockquote(null) },
            { "br", new Br(null) },
            { "caption", new Caption(null) },
            { "choiceInteraction", new ChoiceInteraction(null) },
            { "cite", new Cite(null) }, { "code", new Code(null) }, 
            { "col", new Col(null) },
            { "colgroup", new Colgroup(null) }, 
            { "dd", new Dd(null) }, 
            { "dfn", new Dfn(null) },
            { "div", new Div(null) },
            { "dl", new Dl(null) },
            { "drawingInteraction", new DrawingInteraction(null) },
            { "dt", new Dt(null) },
            { "em", new Em(null) },
            { "endAttemptInteraction", new EndAttemptInteraction(null) },
            { "extendedTextInteraction", new ExtendedTextInteraction(null) },
            { "feedbackBlock", new FeedbackBlock(null) },
            { "feedbackInline", new FeedbackInline(null) },
            { "gap", new Gap(null) },
            { "gapImg", new GapImg(null) },
            { "gapMatchInteraction", new GapMatchInteraction(null) },
            { "gapText", new GapText(null) },
            { "graphicAssociateInteraction", new GraphicAssociateInteraction(null) }, 
            { "graphicGapMatchInteraction", new GraphicGapMatchInteraction(null) },
            { "graphicOrderInteraction", new GraphicOrderInteraction(null) },
            { "h1", new H1(null) },
            { "h2", new H2(null) },
            { "h3", new H3(null) },
            { "h4", new H4(null) }, 
            { "h5", new H5(null) },
            { "h6", new H6(null) },
            { "hotspotChoice", new HotspotChoice(null) },
            { "hotspotInteraction", new HotspotInteraction(null) },
            { "hottext", new Hottext(null) },
            { "hottextInteraction", new HottextInteraction(null) },
            { "hr", new Hr(null) },
            { "i", new I(null) }, 
            { "img", new Img(null) },
            { "inlineChoice", new InlineChoice(null) },
            { "inlineChoiceInteraction", new InlineChoiceInteraction(null) },
            { "itemBody", new ItemBody(null) },
            { "kbd", new Kbd(null) },
            { "li", new Li(null) },
            { "matchInteraction", new MatchInteraction(null) },
            { "math", new uk.ac.ed.ph.jqtiplus.node.content.mathml.Math(null) },
            { "mediaInteraction", new MediaInteraction(null) }, 
            { "object", new uk.ac.ed.ph.jqtiplus.node.content.xhtml.object.Object(null) },
            { "ol", new Ol(null) },
            { "orderInteraction", new OrderInteraction(null) },
            { "p", new P(null) },
            { "param", new Param(null) },
            { "positionObjectInteraction", new PositionObjectInteraction(null) },
            { "positionObjectStage", new PositionObjectStage(null) },
            { "pre", new Pre(null) },
            { "printedVariable", new PrintedVariable(null) },
            { "prompt", new Prompt(null) },
            { "q", new Q(null) },
            { "rubricBlock", new RubricBlock(null) }, 
            { "samp", new Samp(null) }, 
            { "selectPointInteraction", new SelectPointInteraction(null) },
            { "simpleAssociableChoice", new SimpleAssociableChoice(null) }, 
            { "simpleChoice", new SimpleChoice(null) },
            { "simpleMatchSet", new SimpleMatchSet(null) },
            { "sliderInteraction", new SliderInteraction(null) }, 
            { "small", new Small(null) },
            { "span", new Span(null) },
            { "strong", new Strong(null) }, 
            { "sub", new Sub(null) },
            { "sup", new Sup(null) }, 
            { "table", new Table(null) },
            { "tbody", new Tbody(null) }, 
            { "td", new Td(null) },
            { "textEntryInteraction", new TextEntryInteraction(null) },
            { "textRun", new TextRun(null, "") }, 
            { "tfoot", new Tfoot(null) }, 
            { "th", new Th(null) },
            { "thead", new Thead(null) }, 
            { "tr", new Tr(null) },
            { "tt", new Tt(null) },
            { "ul", new Ul(null) }, 
            { "uploadInteraction", new UploadInteraction(null) },
            { "var", new Var(null) }
    });

    @Test
    public void test() {
        for (final Object[] clz : contentClasses) {
            final String name = (String) clz[0];
            final QtiNode node = (QtiNode) clz[1];

            assertEquals(name, node.getQtiClassName());

            final ContentType c = ContentType.getType(name);
            assertEquals(c.getClazz(), node.getClass());
        }
    }
}
