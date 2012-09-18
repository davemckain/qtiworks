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

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.exception2.ResponseBindingException;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.ResponseData.ResponseDataType;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.List;

/**
 * The end attempt interaction is A special type of interaction which allows item
 * authors to provide the candidate with control over the way in which the candidate
 * terminates an attempt. The candidate can use the interaction to terminate the
 * attempt (triggering response processing) immediately, typically to request A hint.
 * It must be bound to A response variable with base-type boolean and single cardinality.
 * If the candidate invokes response processing using an endAttemptInteraction then the
 * associated response variable is set to true. If response processing is invoked in any
 * other way, either through A different endAttemptInteraction or through the default
 * method for the delivery engine, then the associated response variable is set to false.
 * The default value of the response variable is always ignored.
 * Attribute : title [1]: string
 * The string that should be displayed to the candidate as A prompt for ending the attempt
 * using this interaction. This should be short, preferably one word. A typical value would
 * be "Hint". For example, in A graphical environment it would be presented as the label
 * on A button that, when pressed, ends the attempt.
 * Attribute: countAttempt [0..1]: boolean = true
 *
 * @author Jonathon Hare
 */
public final class EndAttemptInteraction extends InlineInteraction {

    private static final long serialVersionUID = -4043944468486325265L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "endAttemptInteraction";

    /** Name of title attribute in xml schema. */
    public static final String ATTR_TITLE_NAME = "title";

    /** Name of countAttempt attribute in xml schema. */
    public static final String ATTR_COUNT_ATTEMPT_NAME = "countAttempt";

    /** Default value of countAttempt attribute in xml schema. */
    public static final boolean ATTR_COUNT_ATTEMPT_DEFAULT_VALUE = true;

    /**
     * Construct new interaction.
     *
     * @param parent Parent node
     */
    public EndAttemptInteraction(final XmlNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new StringAttribute(this, ATTR_TITLE_NAME, true));
        getAttributes().add(new BooleanAttribute(this, ATTR_COUNT_ATTEMPT_NAME, ATTR_COUNT_ATTEMPT_DEFAULT_VALUE, false));
    }

    /**
     * Sets new value of title attribute.
     *
     * @param title new value of title attribute
     * @see #getTitle
     */
    public void setTitle(final String title) {
        getAttributes().getStringAttribute(ATTR_TITLE_NAME).setValue(title);
    }

    /**
     * Gets value of title attribute.
     *
     * @return value of title attribute
     * @see #setTitle
     */
    public String getTitle() {
        return getAttributes().getStringAttribute(ATTR_TITLE_NAME).getComputedValue();
    }


    public void setCountAttempt(final Boolean countAttempt) {
        getAttributes().getBooleanAttribute(ATTR_COUNT_ATTEMPT_NAME).setValue(countAttempt);
    }

    public boolean getCountAttempt() {
        return getAttributes().getBooleanAttribute(ATTR_COUNT_ATTEMPT_NAME).getComputedNonNullValue();
    }


    @Override
    public void validate(final ValidationContext context) {
        super.validate(context);

        if (getResponseIdentifier() != null) {
            final ResponseDeclaration declaration = getResponseDeclaration();
            if (declaration != null && declaration.getBaseType() != null && !declaration.getBaseType().isBoolean()) {
                context.add(new ValidationError(this, "Response variable must have boolean base type"));
            }
            if (declaration != null && declaration.getCardinality() != null && !declaration.getCardinality().isSingle()) {
                context.add(new ValidationError(this, "Response variable must have single cardinality"));
            }
        }
    }

    /**
     * The parsing {@link ResponseData} for this interaction is slightly artificial in order to
     * make it easy to implement in a web form:
     *
     * - We accept {@link StringResponseData} only.
     * - An empty list is treated as false, anything else is treated as true.
     */
    @Override
    protected Value parseResponse(final ResponseDeclaration responseDeclaration, final ResponseData responseData)
            throws ResponseBindingException {
        if (responseData.getType()!=ResponseDataType.STRING) {
            throw new ResponseBindingException(responseDeclaration, responseData, "ResponseData for endAttemptInteraction must be of string type");
        }
        final List<String> stringResponseData = ((StringResponseData) responseData).getResponseData();
        return BooleanValue.valueOf(!stringResponseData.isEmpty());
    }

    @Override
    public boolean validateResponse(final ItemSessionController itemSessionController, final Value responseValue) {
        /* No validation to do here */
        return true;
    }
}
