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
package uk.ac.ed.ph.jqtiplus.node.item.interaction;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.exception.ResponseBindingException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.BodyElement;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.Choice;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.running.InteractionBindingContext;
import uk.ac.ed.ph.jqtiplus.types.FileResponseData;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.ResponseData.ResponseDataType;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.FileValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.OrderedValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Interactions allow the candidate to interact with the item.
 * Through an interaction, the candidate selects or constructs a response.
 * The candidate's responses are stored in the response variables. Each
 * interaction is associated with (at least) one response variable.
 *
 * @see Shuffleable
 *
 * @author Jonathon Hare
 * @author David McKain
 */
public abstract class Interaction extends BodyElement {

    private static final long serialVersionUID = -1653661113115253620L;

    /** Display name of this class. */
    public static final String DISPLAY_NAME = "interaction";

    /** Name of responseIdentifier attribute in xml schema. */
    public static final String ATTR_RESPONSE_IDENTIFIER_NAME = "responseIdentifier";

    public Interaction(final QtiNode parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getAttributes().add(new IdentifierAttribute(this, ATTR_RESPONSE_IDENTIFIER_NAME, true));
    }


    public Identifier getResponseIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_RESPONSE_IDENTIFIER_NAME).getComputedValue();
    }

    public void setResponseIdentifier(final Identifier responseIdentifier) {
        getAttributes().getIdentifierAttribute(ATTR_RESPONSE_IDENTIFIER_NAME).setValue(responseIdentifier);
    }

    /**
     * Returns the (first) {@link ResponseDeclaration} corresponding to this {@link Interaction}.
     * <p>
     * Note that this may return NULL for invalid items.
     * <p>
     * NB: Don't use this for validation as it only returns the first {@link ResponseDeclaration}
     * if found, so is no good for validating references.
     */
    public ResponseDeclaration getResponseDeclaration() {
        return getRootNode(AssessmentItem.class).getResponseDeclaration(getResponseIdentifier());
    }

    @Override
    protected final void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final Identifier responseIdentifier = getResponseIdentifier();
        ResponseDeclaration responseDeclaration = null;
        if (responseIdentifier!=null) {
            final VariableDeclaration declaration = context.checkLocalVariableReference(this, responseIdentifier);
            if (declaration!=null && declaration.getCardinality()!=null
                    && context.checkVariableType(this, declaration, VariableType.RESPONSE)) {
                responseDeclaration = (ResponseDeclaration) declaration;
            }
        }
        validateThis(context, responseDeclaration);
    }

    /**
     * Partial implementation of {@link #validateThis(ValidationContext)} that has already looked
     * up the target {@link ResponseDeclaration} and checked whether it is unique and of the correct type.
     *
     * @param context {@link ValidationContext}
     * @param responseDeclaration resolved {@link ResponseDeclaration}, or null if
     *   the {@link ResponseDeclaration} was not considered valid
     *
     * @see ValidationContext#checkLocalVariableReference(QtiNode, Identifier)
     */
    protected abstract void validateThis(final ValidationContext context, final ResponseDeclaration responseDeclaration);

    /**
     * Helper for implementations of {@link Shuffleable} to wrap up simple (single)
     * lists of choice in the form expected by {@link Shuffleable#computeShuffleableChoices()}
     */
    public static <C extends Choice> List<List<C>> wrapSingleChoiceList(final List<C> choices) {
        final List<List<C>> result = new ArrayList<List<C>>();
        result.add(choices);
        return result;
    }

    /**
     * Given the user response to the interaction in the form of a
     * List of Strings, set the appropriate response variables.
     * <p>
     * This default implementation calls up {@link #parseResponse(ResponseDeclaration, ResponseData)}
     * and sets the value of the appropriate uncommitted response declaration. You'll need to override this
     * for things that might do more, such as string interactions that might bind two variables.
     * <p>
     * (This was called <tt>processResponse</tt> previously, which I found confusing.)
     *
     * @param responseData Response to process, which must not be null
     * @throws ResponseBindingException if the response cannot be bound to the
     *             value encoded by the responseList
     */
    public void bindResponse(final InteractionBindingContext interactionBindingContext, final ResponseData responseData)
            throws ResponseBindingException {
        Assert.notNull(responseData, "responseData");
        final ResponseDeclaration responseDeclaration = getResponseDeclaration();
        if (responseDeclaration == null) {
            interactionBindingContext.fireRuntimeError(this, "No corresponding responseDeclaration found with identifier " + getResponseIdentifier());
            return;
        }
        final Value value = parseResponse(responseDeclaration, responseData);
        interactionBindingContext.bindResponseVariable(responseDeclaration.getIdentifier(), value);
    }

    /**
     * Parses the raw user response to the interaction to an appropriate {@link Value}.
     * <p>
     * This default implementation is sufficient in many cases,
     * but may need overridden for certain types of Interactions.
     * <p>
     * (This was called <tt>processResponse</tt> previously, which I found confusing.)
     * <p>
     * OVERRIDE NOTE: Make sure you catch all {@link QtiParseException}s when parsing the raw data, and
     * convert them to a {@link ResponseBindingException}.
     *
     * @param responseData Response to process, which will never be null
     * @param responseDeclaration underlying response declaration
     * @see #bindResponse(InteractionBindingContext, ResponseData)
     * @throws ResponseBindingException if the response cannot be bound to the
     *             value encoded by the responseList
     */
    protected Value parseResponse(final ResponseDeclaration responseDeclaration, final ResponseData responseData)
            throws ResponseBindingException {
        Value value = null;
        final BaseType responseBaseType = responseDeclaration.getBaseType();
        final Cardinality responseCardinality = responseDeclaration.getCardinality();

        if (responseBaseType==BaseType.FILE) {
            if (responseData.getType()!=ResponseDataType.FILE) {
                throw new ResponseBindingException(responseDeclaration, responseData, "Attempted to bind non-file response data to a file response");
            }
            final FileResponseData fileResponseData = (FileResponseData) responseData;
            value = new FileValue(fileResponseData);
        }
        else {
            if (responseData.getType()!=ResponseDataType.STRING) {
                throw new ResponseBindingException(responseDeclaration, responseData, "Attempted to bind non-string response data to response with baseType " + responseBaseType);
            }
            try {
                final List<String> stringResponseData = ((StringResponseData) responseData).getResponseData();
                if (responseCardinality == Cardinality.SINGLE) {
                    if (stringResponseData.isEmpty() || stringResponseData.get(0).trim().length() == 0) {
                        value = NullValue.INSTANCE;
                    }
                    else {
                        value = responseDeclaration.getBaseType().parseSingleValueLax(stringResponseData.get(0));
                    }
                }
                else if (!(responseCardinality == Cardinality.RECORD)) {
                    final List<SingleValue> values = new ArrayList<SingleValue>(stringResponseData.size());
                    for (final String stringResponseDatum : stringResponseData) {
                        values.add(responseBaseType.parseSingleValue(stringResponseDatum));
                    }

                    if (responseCardinality == Cardinality.MULTIPLE) {
                        value = MultipleValue.createMultipleValue(values);
                    }
                    else {
                        value = OrderedValue.createOrderedValue(values);
                    }
                }
            }
            catch (final QtiParseException e) {
                throw new ResponseBindingException(responseDeclaration, responseData,
                        "Failed to parse string response data to required value");
            }
        }
        return value;
    }

    /**
     * Validate the response associated with this interaction.
     * This is called after {@link #bindResponse(InteractionBindingContext, ResponseData)}
     *
     * @return true if the response is valid, false otherwise
     */
    public abstract boolean validateResponse(InteractionBindingContext interactionBindingContext, Value responseValue);
}
