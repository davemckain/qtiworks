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
package uk.ac.ed.ph.jqtiplus.value;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.types.Stringifiable;

import java.util.HashMap;
import java.util.Map;

/**
 * This class describes orientation. Supported cardinalities are:
 * <p>
 * horizontal
 * <p>
 * vertical
 * 
 * @author Jiri Kajaba
 */
public enum Orientation implements Stringifiable {
    
    /**
     * horizontal
     */
    HORIZONTAL("horizontal"),

    /**
     * vertical
     */
    VERTICAL("vertical");

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "orientation";

    private static Map<String, Orientation> orientations;

    static {
        orientations = new HashMap<String, Orientation>();

        for (final Orientation orientation : Orientation.values()) {
            orientations.put(orientation.orientation, orientation);
        }
    }

    private String orientation;

    private Orientation(String orientation) {
        this.orientation = orientation;
    }

    /**
     * Returns true if this orientation is horizontal; false otherwise.
     * 
     * @return true if this orientation is horizontal; false otherwise
     */
    public boolean isHorizontal() {
        return this == HORIZONTAL;
    }

    /**
     * Returns true if this orientation is vertical; false otherwise.
     * 
     * @return true if this orientation is vertical; false otherwise
     */
    public boolean isVertical() {
        return this == VERTICAL;
    }

    @Override
    public String toQtiString() {
        return orientation;
    }

    /**
     * Returns parsed <code>Orientation</code> from given <code>String</code>.
     * 
     * @param orientation <code>String</code> representation of <code>Orientation</code>
     * @return parsed <code>Orientation</code> from given <code>String</code>
     * @throws QtiParseException if given <code>String</code> is not valid <code>Orientation</code>
     */
    public static Orientation parseOrientation(String orientation) {
        final Orientation result = orientations.get(orientation);

        if (result == null) {
            throw new QtiParseException("Invalid " + QTI_CLASS_NAME + " '" + orientation + "'.");
        }

        return result;
    }
}
