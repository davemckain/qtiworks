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
package uk.ac.ed.ph.jqtiplus.node.item.interaction.graphic;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.ShapeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.CoordsAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Shape;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.Choice;

import java.util.List;

/**
 * hotspotChoice
 * 
 * @author Jonathon Hare
 */
public class HotspotChoice extends Choice implements Hotspot {

    private static final long serialVersionUID = 462353986705124436L;

    /** Name of this class in xml schema. */
    public static String CLASS_TAG = "hotspotChoice";

    /**
     * Constructs object.
     * 
     * @param parent parent of constructed object
     */
    public HotspotChoice(XmlNode parent) {
        super(parent);

        getAttributes().add(new ShapeAttribute(this, ATTR_SHAPE_NAME));
        getAttributes().add(new CoordsAttribute(this, ATTR_COORDS_NAME));
        getAttributes().add(new StringAttribute(this, ATTR_HOTSPOT_LABEL_NAME, null, null, false));
    }

    @Override
    public List<? extends XmlNode> getChildren() {
        return null;
    }

    @Override
    public List<Integer> getCoords() {
        return getAttributes().getCoordsAttribute(ATTR_COORDS_NAME).getValues();
    }

    @Override
    public String getHotspotLabel() {
        return getAttributes().getStringAttribute(ATTR_HOTSPOT_LABEL_NAME).getValue();
    }

    @Override
    public Shape getShape() {
        return getAttributes().getShapeAttribute(ATTR_SHAPE_NAME).getValue();
    }

    @Override
    public void setHotspotLabel(String hotspotLabel) {
        getAttributes().getStringAttribute(ATTR_HOTSPOT_LABEL_NAME).setValue(hotspotLabel);
    }

    @Override
    public void setShape(Shape shape) {
        getAttributes().getShapeAttribute(ATTR_SHAPE_NAME).setValue(shape);
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }
}
