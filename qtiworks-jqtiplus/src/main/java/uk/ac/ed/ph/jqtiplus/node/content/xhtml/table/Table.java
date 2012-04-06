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
package uk.ac.ed.ph.jqtiplus.node.content.xhtml.table;

import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.CaptionGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.ColGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.ColgroupGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.TbodyGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.TfootGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.TheadGroup;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.BodyElement;
import uk.ac.ed.ph.jqtiplus.node.content.basic.BlockStatic;
import uk.ac.ed.ph.jqtiplus.node.content.basic.FlowStatic;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Attribute : summary [0..1]: string
 * Contains : caption [0..1]
 * Contains : col [*]
 * If A table directly contains A col then it must not contain any colgroup elements.
 * Contains : colgroup [*]
 * If A table contains A colgroup it must not directly contain any col elements.
 * Contains : thead [0..1]
 * Contains : tfoot [0..1]
 * Contains : tbody [1..*]
 * 
 * @author Jonathon Hare
 */
public class Table extends BodyElement implements BlockStatic, FlowStatic {

    private static final long serialVersionUID = -13930375270014305L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "table";

    /** Name of summary attribute in xml schema. */
    public static final String ATTR_SUMMARY_NAME = "summary";

    public Table(XmlNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new StringAttribute(this, ATTR_SUMMARY_NAME, false));

        getNodeGroups().add(new CaptionGroup(this));
        getNodeGroups().add(new ColGroup(this));
        getNodeGroups().add(new ColgroupGroup(this));
        getNodeGroups().add(new TheadGroup(this));
        getNodeGroups().add(new TfootGroup(this));
        getNodeGroups().add(new TbodyGroup(this));
    }

    /**
     * Gets col children.
     * 
     * @return col children
     */
    public List<Col> getCols() {
        return getNodeGroups().getColGroup().getCols();
    }

    /**
     * Gets colgroup children.
     * 
     * @return colgroup children
     */
    public List<Colgroup> getColgroups() {
        return getNodeGroups().getColgroupGroup().getColgroups();
    }

    /**
     * Gets tbody children.
     * 
     * @return tbody children
     */
    public List<Tbody> getTbodys() {
        return getNodeGroups().getTbodyGroup().getTbodys();
    }

    /**
     * Gets caption child.
     * 
     * @return caption child
     */
    public Caption getCaption() {
        return getNodeGroups().getCaptionGroup().getCaption();
    }

    /**
     * Sets caption child.
     * 
     * @param caption Caption to set
     */
    public void setCaption(Caption caption) {
        getNodeGroups().getCaptionGroup().setCaption(caption);
    }

    /**
     * Gets thead child.
     * 
     * @return thead child
     */
    public Thead getThead() {
        return getNodeGroups().getTheadGroup().getThead();
    }

    /**
     * Sets Thead child.
     * 
     * @param thead Thead to set
     */
    public void setThead(Thead thead) {
        getNodeGroups().getTheadGroup().setThead(thead);
    }

    /**
     * Gets tfoot child.
     * 
     * @return tfoot child
     */
    public Tfoot getTfoot() {
        return getNodeGroups().getTfootGroup().getTfoot();
    }

    /**
     * Sets Tfoot child.
     * 
     * @param tfoot Tfoot to set
     */
    public void setTfoot(Tfoot tfoot) {
        getNodeGroups().getTfootGroup().setTfoot(tfoot);
    }

    /**
     * Gets an unmodifiable list of the child elements. Use the other
     * methods on Table to add children to the correct group.
     */
    @Override
    public List<? extends XmlNode> getChildren() {
        final List<BodyElement> children = new ArrayList<BodyElement>();

        children.add(getNodeGroups().getCaptionGroup().getCaption());
        children.addAll(getNodeGroups().getColGroup().getCols());
        children.addAll(getNodeGroups().getColgroupGroup().getColgroups());
        children.add(getNodeGroups().getTheadGroup().getThead());
        children.add(getNodeGroups().getTfootGroup().getTfoot());
        children.addAll(getNodeGroups().getTbodyGroup().getTbodys());

        return Collections.unmodifiableList(children);
    }

    /**
     * Gets value of summary attribute.
     * 
     * @return value of summary attribute
     * @see #setSummary
     */
    public String getSummary() {
        return getAttributes().getStringAttribute(ATTR_SUMMARY_NAME).getComputedValue();
    }

    /**
     * Sets new value of summary attribute.
     * 
     * @param summary new value of summary attribute
     * @see #getSummary
     */
    public void setSummary(String summary) {
        getAttributes().getStringAttribute(ATTR_SUMMARY_NAME).setValue(summary);
    }

    @Override
    public void validate(ValidationContext context) {
        super.validate(context);

        if (getColgroups().size() > 0 && getCols().size() > 0) {
            context.add(new ValidationError(this, QTI_CLASS_NAME + " cannot contain both " + Colgroup.QTI_CLASS_NAME + " and " + Col.QTI_CLASS_NAME + " children"));
        }
    }


}
