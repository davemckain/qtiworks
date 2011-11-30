/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
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

package uk.ac.ed.ph.jqtiplus.node.item.response.declaration;

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.FloatAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.SingleValueAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractObject;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;


/**
 * @see Mapping
 * 
 * @author Jonathon Hare
 */
public class MapEntry extends AbstractObject {
    private static final long serialVersionUID = 1L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "mapEntry";

    /** Name of mapKey attribute in xml schema. */
    public static final String ATTR_MAP_KEY_NAME = "mapKey";

    /** Name of mappedValue attribute in xml schema. */
    public static final String ATTR_MAPPED_VALUE_NAME = "mappedValue";
    
    /** Name of caseSensitive attribute in xml schema (late addition to QTI 2.1) */
    public static final String ATTR_CASE_SENSITIVE_VALUE_NAME = "caseSensitive";
    
    /** Default value of caseSensitive attribute */
    public static final boolean ATTR_CASE_SENSITIVE_DEFAULT_VALUE = true;
    
    /**
     * Construct A new MapEntry.
     * @param parent MapEntry parent
     */
    public MapEntry(Mapping parent) {
        super(parent);
        
        getAttributes().add(new SingleValueAttribute(this, ATTR_MAP_KEY_NAME, parent.getParent().getBaseType()));
        getAttributes().add(new FloatAttribute(this, ATTR_MAPPED_VALUE_NAME));
        getAttributes().add(new BooleanAttribute(this, ATTR_CASE_SENSITIVE_VALUE_NAME, ATTR_CASE_SENSITIVE_DEFAULT_VALUE));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }
    
    /**
     * Gets value of mapKey attribute.
     *
     * @return value of mapKey attribute
     * @see #setMapKey
     */
    public SingleValue getMapKey()
    {
        return getAttributes().getSingleValueAttribute(ATTR_MAP_KEY_NAME).getValue();
    }

    /**
     * Sets new value of mappedValue attribute.
     *
     * @param mapKey new value of mapKey attribute
     * @see #getMapKey
     */
    public void setMapKey(SingleValue mapKey)
    {
        getAttributes().getSingleValueAttribute(ATTR_MAP_KEY_NAME).setValue(mapKey);
    }
    
    /**
     * Gets value of mappedValue attribute.
     *
     * @return value of mappedValue attribute
     * @see #setMappedValue
     */
    public Double getMappedValue()
    {
        return getAttributes().getFloatAttribute(ATTR_MAPPED_VALUE_NAME).getValue();
    }

    /**
     * Sets new value of mappedValue attribute.
     *
     * @param mappedValue new value of mappedValue attribute
     * @see #getMappedValue
     */
    public void setMappedValue(Double mappedValue)
    {
        getAttributes().getFloatAttribute(ATTR_MAPPED_VALUE_NAME).setValue(mappedValue);
    }
    
    
    public Boolean getCaseSensitive()
    {
        return getAttributes().getBooleanAttribute(ATTR_CASE_SENSITIVE_VALUE_NAME).getValue();
    }
    
    public void setCaseSensitive(Boolean caseSensitive)
    {
        getAttributes().getBooleanAttribute(ATTR_CASE_SENSITIVE_VALUE_NAME).setValue(caseSensitive);
    }
}
