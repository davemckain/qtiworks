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
package uk.ac.ed.ph.jqtiplus.node.item.interaction;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.control.AssessmentItemController;
import uk.ac.ed.ph.jqtiplus.exception2.ResponseBindingException;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.BodyElement;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.OrderedValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.List;

/**
 * Interactions allow the candidate to interact with the item.
 * Through an interaction, the candidate selects or constructs A response.
 * The candidate's responses are stored in the response variables. Each
 * interaction is associated with (at least) one response variable.
 * 
 * @author Jonathon Hare
 */
public abstract class Interaction extends BodyElement {

    private static final long serialVersionUID = -1653661113115253620L;

    /** Display name of this class. */
    public static final String DISPLAY_NAME = "interaction";

    /** Name of responseIdentifier attribute in xml schema. */
    public static final String ATTR_RESPONSE_IDENTIFIER_NAME = "responseIdentifier";

    /** All interactions in xml schema end in "Interaction" */
    public static final String CLASS_TAG_SUFFIX = "Interaction";

    /**
     * Construct new interaction.
     * 
     * @param parent Parent node
     */
    public Interaction(XmlNode parent) {
        super(parent);

        getAttributes().add(new IdentifierAttribute(this, ATTR_RESPONSE_IDENTIFIER_NAME, null, null, true));
    }

    /**
     * Gets value of responseIdentifier attribute.
     * 
     * @return value of responseIdentifier attribute
     * @see #setResponseIdentifier
     */
    public Identifier getResponseIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_RESPONSE_IDENTIFIER_NAME).getValue();
    }

    /**
     * Sets new value of responseIdentifier attribute.
     * 
     * @param responseIdentifier new value of responseIdentifier attribute
     * @see #getResponseIdentifier
     */
    public void setResponseIdentifier(Identifier responseIdentifier) {
        getAttributes().getIdentifierAttribute(ATTR_RESPONSE_IDENTIFIER_NAME).setValue(responseIdentifier);
    }

    /**
     * Gets the responseDeclaration for this interaction.
     * 
     * @return the responseDeclaration for this interactions responseIdentifier
     */
    public ResponseDeclaration getResponseDeclaration() {
        return getRootNode(AssessmentItem.class).getResponseDeclaration(getResponseIdentifier());
    }

    @Override
    public void validate(ValidationContext context, AbstractValidationResult result) {
        super.validate(context, result);

        if (getResponseIdentifier() != null) {
            final ResponseDeclaration declaration = getResponseDeclaration();
            if (declaration == null) {
                result.add(new ValidationError(this, "Response declaration for variable (" + getResponseIdentifier() + ") not found"));
            }
        }
    }

    /**
     * Initialize the interaction.
     * Subclasses should override this method as required.
     */
    public void initialize(@SuppressWarnings("unused") AssessmentItemController itemController) {
        /* Let subclasses override as required */
    }

    /**
     * Given the user response to the interaction in the form of a
     * List of Strings, set the appropriate response variables.
     * <p>
     * This default implementation calls up {@link #bindResponse(ResponseDeclaration, List)}. and sets the value of the appropriate response declaration. You'll
     * need to override this for things that might do more, such as string interactions that might bind two variables.
     * <p>
     * (This was called <tt>processResponse</tt> previously, which I found confusing.)
     * 
     * @param responseList Response to process
     * @see AssessmentItem#setResponses
     * @throws ResponseBindingException if the response cannot be bound to the
     *             value encoded by the responseList
     */
    public void bindResponse(AssessmentItemController itemController, List<String> responseList)
            throws ResponseBindingException {
        final ResponseDeclaration responseDeclaration = getResponseDeclaration();
        final Value value = bindResponse(responseDeclaration, responseList);
        itemController.getItemState().setResponseValue(this, value);
    }

    /**
     * Binds the raw user response to the interaction to an appropriate {@link Value}.
     * <p>
     * This default implementation is sufficient in many cases, but may need overridden for certain types of Interactions.
     * <p>
     * (This was called <tt>processResponse</tt> previously, which I found confusing.)
     * 
     * @param responseList Response to process
     * @param resposneDeclaration underlying response declaration
     * @see #bindResponse(AssessmentItemController, List)
     * @throws ResponseBindingException if the response cannot be bound to the
     *             value encoded by the responseList
     */
    @SuppressWarnings("static-method")
    protected Value bindResponse(ResponseDeclaration responseDeclaration, List<String> responseList)
            throws ResponseBindingException {
        Value value = null;

        if (responseDeclaration.getCardinality() == Cardinality.SINGLE) {
            if (responseList == null || responseList.size() == 0 || responseList.get(0).trim().length() == 0) {
                value = NullValue.INSTANCE;
            }
            else {
                value = responseDeclaration.getBaseType().parseSingleValue(responseList.get(0));
            }
        }
        else if (!(responseDeclaration.getCardinality() == Cardinality.RECORD)) {
            final SingleValue[] values = new SingleValue[responseList.size()];

            for (int i = 0; i < responseList.size(); i++) {
                values[i] = responseDeclaration.getBaseType().parseSingleValue(responseList.get(i));
            }

            if (responseDeclaration.getCardinality() == Cardinality.MULTIPLE) {
                value = new MultipleValue(values);
            }
            else {
                value = new OrderedValue(values);
            }
        }
        return value;
    }

    /**
     * Validate the response associated with this interaction.
     * This is called after {@link #bindResponse(AssessmentItemController, List)}
     * 
     * @return true if the response is valid, false otherwise
     */
    public abstract boolean validateResponse(AssessmentItemController itemController, Value responseValue);
}
