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
package uk.ac.ed.ph.jqtiplus.value;


/**
 * The enumerates the various permitted combinations of {@link BaseType}
 * and {@link Cardinality}.
 * <p>
 * This is new in JQTI+ and is designed to make checking logic simpler and
 * easier to read. Please use this for new code where possible.
 *
 * @author David McKain
 */
public enum Signature {

    SINGLE_BOOLEAN("single boolean", Cardinality.SINGLE, BaseType.BOOLEAN),
    SINGLE_DIRECTED_PAIR("single directedPair", Cardinality.SINGLE, BaseType.DIRECTED_PAIR),
    SINGLE_DURATION("single duration", Cardinality.SINGLE, BaseType.DURATION),
    SINGLE_FILE("single file", Cardinality.SINGLE, BaseType.FILE),
    SINGLE_FLOAT("single float", Cardinality.SINGLE, BaseType.FLOAT),
    SINGLE_IDENTIFIER("single identifier", Cardinality.SINGLE, BaseType.IDENTIFIER),
    SINGLE_INTEGER("single integer", Cardinality.SINGLE, BaseType.INTEGER),
    SINGLE_PAIR("single pair", Cardinality.SINGLE, BaseType.PAIR),
    SINGLE_POINT("single point", Cardinality.SINGLE, BaseType.POINT),
    SINGLE_STRING("single string", Cardinality.SINGLE, BaseType.STRING),
    SINGLE_URI("single uri", Cardinality.SINGLE, BaseType.URI),
    MULTIPLE_BOOLEAN("single boolean", Cardinality.MULTIPLE, BaseType.BOOLEAN),
    MULTIPLE_DIRECTED_PAIR("single directedPair", Cardinality.MULTIPLE, BaseType.DIRECTED_PAIR),
    MULTIPLE_DURATION("single duration", Cardinality.MULTIPLE, BaseType.DURATION),
    MULTIPLE_FILE("single file", Cardinality.MULTIPLE, BaseType.FILE),
    MULTIPLE_FLOAT("single float", Cardinality.MULTIPLE, BaseType.FLOAT),
    MULTIPLE_IDENTIFIER("single identifier", Cardinality.MULTIPLE, BaseType.IDENTIFIER),
    MULTIPLE_INTEGER("single integer", Cardinality.MULTIPLE, BaseType.INTEGER),
    MULTIPLE_PAIR("single pair", Cardinality.MULTIPLE, BaseType.PAIR),
    MULTIPLE_POINT("single point", Cardinality.MULTIPLE, BaseType.POINT),
    MULTIPLE_STRING("single string", Cardinality.MULTIPLE, BaseType.STRING),
    MULTIPLE_URI("single uri", Cardinality.MULTIPLE, BaseType.URI),
    ORDERED_BOOLEAN("single boolean", Cardinality.ORDERED, BaseType.BOOLEAN),
    ORDERED_DIRECTED_PAIR("single directedPair", Cardinality.ORDERED, BaseType.DIRECTED_PAIR),
    ORDERED_DURATION("single duration", Cardinality.ORDERED, BaseType.DURATION),
    ORDERED_FILE("single file", Cardinality.ORDERED, BaseType.FILE),
    ORDERED_FLOAT("single float", Cardinality.ORDERED, BaseType.FLOAT),
    ORDERED_IDENTIFIER("single identifier", Cardinality.ORDERED, BaseType.IDENTIFIER),
    ORDERED_INTEGER("single integer", Cardinality.ORDERED, BaseType.INTEGER),
    ORDERED_PAIR("single pair", Cardinality.ORDERED, BaseType.PAIR),
    ORDERED_POINT("single point", Cardinality.ORDERED, BaseType.POINT),
    ORDERED_STRING("single string", Cardinality.ORDERED, BaseType.STRING),
    ORDERED_URI("single uri", Cardinality.ORDERED, BaseType.URI),
    RECORD("record", Cardinality.RECORD, null),

    ;

    //-------------------------------------------------------------------

    private final String displayName;
    private final Cardinality cardinality;
    private final BaseType baseType;

    private Signature(final String displayName, final Cardinality cardinality, final BaseType baseType) {
        this.displayName = displayName;
        this.baseType = baseType;
        this.cardinality = cardinality;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BaseType getBaseType() {
        return baseType;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    //-------------------------------------------------------------------

    /* Matrix for looking up a Signature by Cardinality and BaseType.
     *
     * This exploits the fact that the first 3 Cardinality ordinals are
     * the ones that have baseTypes. The remaining Cardinality (RECORD)
     * is special.
     */
    private static Signature[][] signatureMatrix;
    static {
        signatureMatrix = new Signature[3][];
        for (int i=0; i<3; i++) {
            signatureMatrix[i] = new Signature[BaseType.values().length];
        }
        for (final Signature s : values()) {
            final int i = s.getCardinality().ordinal();
            if (i < 3) { /* (i.e. SINGLE, ORDERED, MULTIPLE) */
                final int j = s.getBaseType().ordinal();
                signatureMatrix[i][j] = s;
            }
        }
    }

    public static Signature getSignature(final Cardinality cardinality, final BaseType baseType) {
        if (cardinality==null) {
            return null;
        }
        if (cardinality==Cardinality.RECORD) {
            return Signature.RECORD;
        }
        else {
            if (baseType==null) {
                return null;
            }
            return signatureMatrix[cardinality.ordinal()][baseType.ordinal()];
        }
    }
}
