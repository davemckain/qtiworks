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

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractObject;
import uk.ac.ed.ph.jqtiplus.types.Identifier;


/**
 * Variable mappings allow outcome variables declared with the name sourceIdentifier in the corresponding item to be
 * treated as if they were declared with the name targetIdentifier during outcomeProcessing. Use of variable mappings
 * allows more control over the way outcomes are aggregated when using testVariables.
 * 
 * @author Jiri Kajaba
 */
public class VariableMapping extends AbstractObject
{
    private static final long serialVersionUID = 1L;
    
    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "variableMapping";

    /** Name of sourceIdentifier attribute in xml schema. */
    public static final String ATTR_SOURCE_IDENTIFIER_NAME = "sourceIdentifier";

    /** Name of targetIdentifier attribute in xml schema. */
    public static final String ATTR_TARGET_IDENTIFIER_NAME = "targetIdentifier";

    /**
     * Constructs parent.
     *
     * @param parent parent of created object
     */
    public VariableMapping(AssessmentItemRef parent)
    {
        super(parent);

        getAttributes().add(new IdentifierAttribute(this, ATTR_SOURCE_IDENTIFIER_NAME));
        getAttributes().add(new IdentifierAttribute(this, ATTR_TARGET_IDENTIFIER_NAME));
    }

    @Override
    public String getClassTag()
    {
        return CLASS_TAG;
    }

    /**
     * Gets value of sourceIdentifier attribute.
     *
     * @return value of sourceIdentifier attribute
     * @see #setSourceIdentifier
     */
    public Identifier getSourceIdentifier()
    {
        return getAttributes().getIdentifierAttribute(ATTR_SOURCE_IDENTIFIER_NAME).getValue();
    }

    /**
     * Sets new value of sourceIdentifier attribute.
     *
     * @param sourceIdentifier new value of sourceIdentifier attribute
     * @see #getSourceIdentifier
     */
    public void setSourceIdentifier(Identifier sourceIdentifier)
    {
        getAttributes().getIdentifierAttribute(ATTR_SOURCE_IDENTIFIER_NAME).setValue(sourceIdentifier);
    }

    /**
     * Gets value of targetIdentifier attribute.
     *
     * @return value of targetIdentifier attribute
     * @see #setTargetIdentifier
     */
    public Identifier getTargetIdentifier()
    {
        return getAttributes().getIdentifierAttribute(ATTR_TARGET_IDENTIFIER_NAME).getValue();
    }

    /**
     * Sets new value of targetIdentifier attribute.
     *
     * @param targetIdentifier new value of targetIdentifier attribute
     * @see #getTargetIdentifier
     */
    public void setTargetIdentifier(Identifier targetIdentifier)
    {
        getAttributes().getIdentifierAttribute(ATTR_TARGET_IDENTIFIER_NAME).setValue(targetIdentifier);
    }
}
