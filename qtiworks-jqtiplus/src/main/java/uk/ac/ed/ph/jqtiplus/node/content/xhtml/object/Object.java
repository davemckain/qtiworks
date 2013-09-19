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
package uk.ac.ed.ph.jqtiplus.node.content.xhtml.object;

import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.group.content.ObjectFlowGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.AbstractFlowBodyElement;
import uk.ac.ed.ph.jqtiplus.node.content.basic.FlowStatic;
import uk.ac.ed.ph.jqtiplus.node.content.basic.InlineStatic;
import uk.ac.ed.ph.jqtiplus.node.content.basic.ObjectFlow;

import java.util.List;

/**
 * Represents the <tt>object</tt> QTI class
 *
 * @author Jonathon Hare
 */
public final class Object extends AbstractFlowBodyElement implements InlineStatic, FlowStatic {

    private static final long serialVersionUID = -6905074851539593411L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "object";

    /** Name of data attribute in xml schema. */
    public static final String ATTR_DATA_NAME = "data";

    /** Name of type attribute in xml schema. */
    public static final String ATTR_TYPE_NAME = "type";

    /** Name of width attribute in xml schema. */
    public static final String ATTR_WIDTH_NAME = "width";

    /** Name of height attribute in xml schema. */
    public static final String ATTR_HEIGHT_NAME = "height";

    public Object(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new StringAttribute(this, ATTR_DATA_NAME, true));
        getAttributes().add(new StringAttribute(this, ATTR_TYPE_NAME, true));
        getAttributes().add(new StringAttribute(this, ATTR_WIDTH_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_HEIGHT_NAME, false));

        getNodeGroups().add(new ObjectFlowGroup(this));
    }

    /**
     * @deprecated Please now use {@link #getObjectFlows()}
     */
    @Deprecated
    public List<ObjectFlow> getChildren() {
        return getObjectFlows();
    }

    public List<ObjectFlow> getObjectFlows() {
        return getNodeGroups().getObjectFlowGroup().getObjectFlows();
    }

    public String getData() {
        return getAttributes().getStringAttribute(ATTR_DATA_NAME).getComputedValue();
    }

    public void setData(final String data) {
        getAttributes().getStringAttribute(ATTR_DATA_NAME).setValue(data);
    }


    public String getType() {
        return getAttributes().getStringAttribute(ATTR_TYPE_NAME).getComputedValue();
    }

    public void setType(final String type) {
        getAttributes().getStringAttribute(ATTR_TYPE_NAME).setValue(type);
    }


    public String getWidth() {
        return getAttributes().getStringAttribute(ATTR_WIDTH_NAME).getComputedValue();
    }

    public void setWidth(final String width) {
        getAttributes().getStringAttribute(ATTR_WIDTH_NAME).setValue(width);
    }


    public String getHeight() {
        return getAttributes().getStringAttribute(ATTR_HEIGHT_NAME).getComputedValue();
    }

    public void setHeight(final String height) {
        getAttributes().getStringAttribute(ATTR_HEIGHT_NAME).setValue(height);
    }
}
