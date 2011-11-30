/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 * Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
 */
package org.qtitools.mathassess.type;

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;

import java.util.HashMap;
import java.util.Map;


public enum ReturnType {
    MATHS_CONTENT("mathsContent"),
    INTEGER("integer"),
    INTEGER_MULTIPLE("integerMultiple"),
    INTEGER_ORDERED("integerOrdered"),
    FLOAT("float"),
    FLOAT_MULTIPLE("floatMultiple"),
    FLOAT_ORDERED("floatOrdered"),
    BOOLEAN("boolean"),
    BOOLEAN_MULTIPLE("booleanMultiple"),
    BOOLEAN_ORDERED("booleanOrdered");

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "returnTypeCard";
    
    private static Map<String, ReturnType> returnTypes = new HashMap<String, ReturnType>();
    static {
        for (ReturnType type : ReturnType.values()) {
            returnTypes.put(type.getReturnType(), type);
        }
    }
    
    private String returnType;
    
    ReturnType(String returnType) {
        this.returnType = returnType;
    }
    
    public String getReturnType() {
        return returnType;
    }
    
    public static ReturnType parseReturnType(String returnType) {
        ReturnType result = returnTypes.get(returnType);

        if (result == null)
            throw new QTIParseException("Invalid " + CLASS_TAG + " '" + returnType + "'.");

        return result;
    }

    @Override
    public String toString() {
        return returnType;
    }

}
