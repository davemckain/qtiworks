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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.node.accessibility.companion;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.types.Stringifiable;

import java.util.HashMap;
import java.util.Map;

/**
 * Radial unit options in the US system for Protractor.
 *
 * @author Zack Pierce
 */
public enum RadialUnitUS implements Stringifiable {
    DEGREE("Degree"),

    MINUTE("Minute"),

    SECOND("Second");

    private static final String QTI_CLASS_NAME = "RadialUSValue";

    private static Map<String, RadialUnitUS> namesToUnits;

    static {
        final HashMap<String, RadialUnitUS> hashMap = new HashMap<String, RadialUnitUS>();
        for (final RadialUnitUS unit : RadialUnitUS.values()) {
            hashMap.put(unit.unit, unit);
        }
        namesToUnits = ObjectUtilities.unmodifiableMap(hashMap);
    }

    private String unit;

    private RadialUnitUS(final String unit) {
        this.unit = unit;
    }

    @Override
    public String toQtiString() {
        return unit;
    }

    public static RadialUnitUS parseRadialUnitUS(final String radialUnitUS) throws QtiParseException {
        final RadialUnitUS result = namesToUnits.get(radialUnitUS);

        if (result == null) {
            throw new QtiParseException("Invalid " + QTI_CLASS_NAME + " '" + radialUnitUS + "'.");
        }

        return result;
    }
}
