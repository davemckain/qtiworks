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
package uk.ac.ed.ph.jqtiplus.node.content.xhtml.table;

import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.CaptionGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.ColGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.ColgroupGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.TbodyGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.TfootGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.TheadGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.AbstractFlowBodyElement;
import uk.ac.ed.ph.jqtiplus.node.content.basic.BlockStatic;
import uk.ac.ed.ph.jqtiplus.node.content.basic.FlowStatic;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

import java.util.List;

/**
 * table
 *
 * @author Jonathon Hare
 */
public final class Table extends AbstractFlowBodyElement implements BlockStatic, FlowStatic {

    private static final long serialVersionUID = -13930375270014305L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "table";

    /** Name of summary attribute in xml schema. */
    public static final String ATTR_SUMMARY_NAME = "summary";

    public Table(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new StringAttribute(this, ATTR_SUMMARY_NAME, false));

        getNodeGroups().add(new CaptionGroup(this));
        getNodeGroups().add(new ColGroup(this));
        getNodeGroups().add(new ColgroupGroup(this));
        getNodeGroups().add(new TheadGroup(this));
        getNodeGroups().add(new TfootGroup(this));
        getNodeGroups().add(new TbodyGroup(this));
    }

    public List<Col> getCols() {
        return getNodeGroups().getColGroup().getCols();
    }

    public List<Colgroup> getColgroups() {
        return getNodeGroups().getColgroupGroup().getColgroups();
    }

    public List<Tbody> getTbodys() {
        return getNodeGroups().getTbodyGroup().getTbodys();
    }


    public Caption getCaption() {
        return getNodeGroups().getCaptionGroup().getCaption();
    }

    public void setCaption(final Caption caption) {
        getNodeGroups().getCaptionGroup().setCaption(caption);
    }


    public Thead getThead() {
        return getNodeGroups().getTheadGroup().getThead();
    }

    public void setThead(final Thead thead) {
        getNodeGroups().getTheadGroup().setThead(thead);
    }


    public Tfoot getTfoot() {
        return getNodeGroups().getTfootGroup().getTfoot();
    }

    public void setTfoot(final Tfoot tfoot) {
        getNodeGroups().getTfootGroup().setTfoot(tfoot);
    }


    public String getSummary() {
        return getAttributes().getStringAttribute(ATTR_SUMMARY_NAME).getComputedValue();
    }

    public void setSummary(final String summary) {
        getAttributes().getStringAttribute(ATTR_SUMMARY_NAME).setValue(summary);
    }


    @Override
    public void validateThis(final ValidationContext context) {
        super.validateThis(context);
        if (getColgroups().size() > 0 && getCols().size() > 0) {
            context.fireValidationError(this, QTI_CLASS_NAME + " cannot contain both " + Colgroup.QTI_CLASS_NAME + " and " + Col.QTI_CLASS_NAME + " children");
        }
    }


}
