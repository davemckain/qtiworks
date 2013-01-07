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
package uk.ac.ed.ph.jqtiplus.node.item.interaction.graphic;

import uk.ac.ed.ph.jqtiplus.node.expression.operator.Shape;

import java.util.List;

/**
 * Some of the graphic interactions involve images with specially defined areas
 * or hotspots.
 * Attribute : shape [1]: shape
 * The shape of the hotspot.
 * Attribute : coords [1]: coords
 * The size and position of the hotspot, interpreted in conjunction with the shape.
 * Attribute : hotspotLabel [0..1]: string256
 * The alternative text for this (hot) area of the image, if specified it must be
 * treated in the same way as alternative text for img. For hidden hotspots this
 * label is ignored.
 *
 * @author Jonathon Hare
 */
public interface Hotspot {

    /** Name of shape attribute in xml schema. */
    public static final String ATTR_SHAPE_NAME = "shape";

    /** Name of coords attribute in xml schema. */
    public static final String ATTR_COORDS_NAME = "coords";

    /** Name of hotspotLabel attribute in xml schema. */
    public static final String ATTR_HOTSPOT_LABEL_NAME = "hotspotLabel";

    Shape getShape();
    void setShape(Shape shape);

    void setCoords(List<Integer> value);
    List<Integer> getCoords();

    String getHotspotLabel();
    void setHotspotLabel(String hotspotLabel);

}
