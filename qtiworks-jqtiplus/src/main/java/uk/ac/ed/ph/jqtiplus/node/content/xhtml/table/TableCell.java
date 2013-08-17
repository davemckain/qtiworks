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

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.TableCellScopeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.group.content.FlowGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.BodyElement;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Flow;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.TableCellScope;

import java.util.List;

/**
 * tableCell
 *
 * @author Jonathon Hare
 */
public abstract class TableCell extends BodyElement {

    private static final long serialVersionUID = 1932167301649954082L;

    /** Name of this class in xml schema. */
    public static final String DISPLAY_NAME = "tableCell";

    /** Name of headers attribute in xml schema. */
    public static final String ATTR_HEADERS_NAME = "headers";

    /** Name of scope attribute in xml schema. */
    public static final String ATTR_SCOPE_NAME = "scope";

    /** Name of abbr attribute in xml schema. */
    public static final String ATTR_ABBR_NAME = "abbr";

    /** Name of axis attribute in xml schema. */
    public static final String ATTR_AXIS_NAME = "axis";

    /** Name of rowspan attribute in xml schema. */
    public static final String ATTR_ROWSPAN_NAME = "rowspan";

    /** Name of colspan attribute in xml schema. */
    public static final String ATTR_COLSPAN_NAME = "colspan";

    public TableCell(final QtiNode parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getAttributes().add(new IdentifierMultipleAttribute(this, ATTR_HEADERS_NAME, false));
        getAttributes().add(new TableCellScopeAttribute(this, ATTR_SCOPE_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_ABBR_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_AXIS_NAME, false));
        getAttributes().add(new IntegerAttribute(this, ATTR_ROWSPAN_NAME, false));
        getAttributes().add(new IntegerAttribute(this, ATTR_COLSPAN_NAME, false));

        getNodeGroups().add(new FlowGroup(this));
    }

    public final List<Flow> getChildren() {
        return getNodeGroups().getFlowGroup().getFlows();
    }

    /**
     * Gets value of headers attribute.
     *
     * @return value of headers attribute
     */
    public final List<Identifier> getHeaders() {
        return getAttributes().getIdentifierMultipleAttribute(ATTR_HEADERS_NAME).getComputedValue();
    }

    public final void setHeaders(final List<Identifier> value) {
        getAttributes().getIdentifierMultipleAttribute(ATTR_HEADERS_NAME).setValue(value);
    }

    /**
     * Gets value of scope attribute.
     *
     * @return value of scope attribute
     * @see #setScope
     */
    public final TableCellScope getScope() {
        return getAttributes().getTableCellScopeAttribute(ATTR_SCOPE_NAME).getComputedValue();
    }

    /**
     * Sets new value of scope attribute.
     *
     * @param scope new value of scope attribute
     * @see #getScope
     */
    public final void setScope(final TableCellScope scope) {
        getAttributes().getTableCellScopeAttribute(ATTR_SCOPE_NAME).setValue(scope);
    }

    /**
     * Gets value of abbr attribute.
     *
     * @return value of abbr attribute
     * @see #setAbbr
     */
    public final String getAbbr() {
        return getAttributes().getStringAttribute(ATTR_ABBR_NAME).getComputedValue();
    }

    /**
     * Sets new value of abbr attribute.
     *
     * @param abbr new value of abbr attribute
     * @see #getAbbr
     */
    public final void setAbbr(final String abbr) {
        getAttributes().getStringAttribute(ATTR_ABBR_NAME).setValue(abbr);
    }

    /**
     * Gets value of axis attribute.
     *
     * @return value of axis attribute
     * @see #setAxis
     */
    public final String getAxis() {
        return getAttributes().getStringAttribute(ATTR_AXIS_NAME).getComputedValue();
    }

    /**
     * Sets new value of axis attribute.
     *
     * @param axis new value of axis attribute
     * @see #getAxis
     */
    public final void setAxis(final String axis) {
        getAttributes().getStringAttribute(ATTR_AXIS_NAME).setValue(axis);
    }

    /**
     * Gets value of rowspan attribute.
     *
     * @return value of rowspan attribute
     * @see #setRowspan
     */
    public final int getRowspan() {
        return getAttributes().getIntegerAttribute(ATTR_ROWSPAN_NAME).getComputedNonNullValue();
    }

    /**
     * Sets new value of rowspan attribute.
     *
     * @param rowspan new value of rowspan attribute
     * @see #getRowspan
     */
    public final void setRowspan(final Integer rowspan) {
        getAttributes().getIntegerAttribute(ATTR_ROWSPAN_NAME).setValue(rowspan);
    }

    /**
     * Gets value of colspan attribute.
     *
     * @return value of colspan attribute
     * @see #setColspan
     */
    public final int getColspan() {
        return getAttributes().getIntegerAttribute(ATTR_COLSPAN_NAME).getComputedNonNullValue();
    }

    /**
     * Sets new value of colspan attribute.
     *
     * @param colspan new value of colspan attribute
     * @see #getColspan
     */
    public final void setColspan(final Integer colspan) {
        getAttributes().getIntegerAttribute(ATTR_COLSPAN_NAME).setValue(colspan);
    }

}
