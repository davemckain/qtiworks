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

package uk.ac.ed.ph.jqtiplus.node.test;

import uk.ac.ed.ph.jqtiplus.attribute.value.FloatAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractObject;
import uk.ac.ed.ph.jqtiplus.node.IdentifiableObject;
import uk.ac.ed.ph.jqtiplus.types.Identifier;


/**
 * Weights allow custom values to be defined for scaling an item's outcomes.
 * 
 * @author Jiri Kajaba
 */
public class Weight extends AbstractObject implements IdentifiableObject<Identifier> {
    
    private static final long serialVersionUID = -115358594629456681L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "weight";

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    /** Name of value attribute in xml schema. */
    public static final String ATTR_VALUE_NAME = "value";

    /** Default weight if no weight is specified. */
    public static final double DEFAULT_WEIGHT = 1;

    /**
     * Constructs object.
     *
     * @param parent parent of created object
     */
    public Weight(AssessmentItemRef parent)
    {
        super(parent);

        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME));
        getAttributes().add(new FloatAttribute(this, ATTR_VALUE_NAME));
    }

    @Override
    public String getClassTag()
    {
        return CLASS_TAG;
    }

    @Override
    public final String computeXPathComponent() {
        String identifier = getIdentifier().toString();
        if (identifier!=null) {
            return getClassTag() + "[@identifier=\"" + identifier + "\"]";
        }
        return super.computeXPathComponent();
    }

    /**
     * Gets value of identifier attribute.
     *
     * @return value of identifier attribute
     * @see #setIdentifier
     */
    public Identifier getIdentifier()
    {
        return getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).getValue();
    }

    /**
     * Sets new value of identifier attribute.
     *
     * @param identifier new value of identifier attribute
     * @see #getIdentifier
     */
    public void setIdentifier(Identifier identifier)
    {
        getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    /**
     * Gets value of value attribute.
     *
     * @return value of value attribute
     * @see #setValue
     */
    public Double getValue()
    {
        return getAttributes().getFloatAttribute(ATTR_VALUE_NAME).getValue();
    }

    /**
     * Sets new value of value attribute.
     *
     * @param value new value of value attribute
     * @see #getValue
     */
    public void setValue(Double value)
    {
        getAttributes().getFloatAttribute(ATTR_VALUE_NAME).setValue(value);
    }
}
