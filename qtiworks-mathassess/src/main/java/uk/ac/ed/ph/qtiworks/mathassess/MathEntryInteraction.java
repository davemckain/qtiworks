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
package uk.ac.ed.ph.qtiworks.mathassess;

import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.ATTR_EXPECTED_LENGTH_NAME;
import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.ATTR_PRINT_IDENTIFIER_NAME;
import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.ATTR_SYNTAX_NAME;
import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.MATHASSESS_NAMESPACE_URI;

import uk.ac.ed.ph.qtiworks.mathassess.attribute.SyntaxAttribute;
import uk.ac.ed.ph.qtiworks.mathassess.glue.AsciiMathHelper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.AsciiMathInputException;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.MathsContentInputValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.value.SyntaxType;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.exception.ResponseBindingException;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.CustomInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.running.InteractionBindingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.ResponseData.ResponseDataType;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
import uk.ac.ed.ph.jqtiplus.value.Signature;
import uk.ac.ed.ph.jqtiplus.value.StringValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import uk.ac.ed.ph.snuggletex.upconversion.UpConversionFailure;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the <tt>org.qtitools.mathassess.MathEntryInteraction</tt> customInteraction
 *
 * @author Jonathon Hare
 * @author David McKain
 */
public final class MathEntryInteraction extends CustomInteraction<MathAssessExtensionPackage> {

    private static final long serialVersionUID = -7450990903827581043L;

    private static final Logger logger = LoggerFactory.getLogger(MathEntryInteraction.class);

    public MathEntryInteraction(final QtiNode parent) {
        super(parent);

        getAttributes().add(new SyntaxAttribute(this, ATTR_SYNTAX_NAME, MATHASSESS_NAMESPACE_URI));
        getAttributes().add(new IntegerAttribute(this, ATTR_EXPECTED_LENGTH_NAME, MATHASSESS_NAMESPACE_URI, false));
        getAttributes().add(new IdentifierAttribute(this, ATTR_PRINT_IDENTIFIER_NAME, MATHASSESS_NAMESPACE_URI, null, false));
    }

    public SyntaxType getSyntax() {
        return ((SyntaxAttribute) getAttributes().get(ATTR_SYNTAX_NAME, MATHASSESS_NAMESPACE_URI))
                .getComputedValue();
    }

    public void setSyntax(final SyntaxType syntax) {
        ((SyntaxAttribute) getAttributes().get(ATTR_SYNTAX_NAME, MATHASSESS_NAMESPACE_URI))
                .setValue(syntax);
    }


    public Identifier getPrintIdentifier() {
        return ((IdentifierAttribute) getAttributes().get(ATTR_PRINT_IDENTIFIER_NAME, MATHASSESS_NAMESPACE_URI))
                .getComputedValue();
    }

    public void setPrintIdentifier(final Identifier printIdentifier) {
        ((IdentifierAttribute) getAttributes().get(ATTR_PRINT_IDENTIFIER_NAME, MATHASSESS_NAMESPACE_URI))
            .setValue(printIdentifier);
    }

    /**
     * Get the responseDeclaration associated the printIdentifier attribute.
     *
     * @return associated responseDeclaration, or null if not found (or
     *         printIdentifier is not set)
     */
    public ResponseDeclaration getPrintIdentifierResponseDeclaration() {
        if (getPrintIdentifier() == null) {
            return null;
        }
        return getRootNode(AssessmentItem.class).getResponseDeclaration(getPrintIdentifier());
    }

    @Override
    protected void validateThis(final MathAssessExtensionPackage jqtiExtensionPackage, final ValidationContext context, final ResponseDeclaration responseDeclaration) {
        if (responseDeclaration!=null) {
            if (!responseDeclaration.getCardinality().isRecord()) {
                context.fireValidationError(this, "Response variable must have record cardinality");
            }
        }

        final Identifier printIdentifier = getPrintIdentifier();
        if (printIdentifier!= null) {
            final VariableDeclaration printDeclaration = context.checkLocalVariableReference(this, printIdentifier);
            if (printDeclaration!=null) {
                context.checkVariableType(this, printDeclaration, VariableType.RESPONSE);
                context.checkSignature(this, printDeclaration, Signature.SINGLE_STRING);
            }
        }
    }

    @Override
    protected void bindResponse(final MathAssessExtensionPackage mathAssessExtensionPackage, final InteractionBindingContext interactionBindingContext, final ResponseData responseData)
            throws ResponseBindingException {
        /* Bind response value as normal */
        final ResponseDeclaration responseDeclaration = getResponseDeclaration();
        if (responseDeclaration == null) {
            interactionBindingContext.fireRuntimeError(this, "No corresponding responseDeclaration found with identifier " + getResponseIdentifier());
            return;
        }
        final Value value = parseResponse(mathAssessExtensionPackage, responseDeclaration, responseData);
        interactionBindingContext.bindResponseVariable(responseDeclaration.getIdentifier(), value);

        final Identifier printIdentifier = getPrintIdentifier();
        if (printIdentifier != null) {
            /* handle stringIdentifier binding as well, if requested */
            final Value printResponseValue = value.isNull() ? NullValue.INSTANCE : (StringValue) ((RecordValue) value).get(MathAssessConstants.FIELD_PMATHML_IDENTIFIER);
            interactionBindingContext.bindResponseVariable(printIdentifier, printResponseValue);
        }
    }

    @Override
    protected Value parseResponse(final MathAssessExtensionPackage mathAssessExtensionPackage, final ResponseDeclaration responseDeclaration, final ResponseData responseData)
            throws ResponseBindingException {
        if (responseData.getType()!=ResponseDataType.STRING) {
            throw new ResponseBindingException(responseDeclaration, responseData, "mathEntryInteraction must be bound to string response data");
        }
        final List<String> stringResponseData = ((StringResponseData) responseData).getResponseData();
        if (stringResponseData.size() != 1) {
            throw new ResponseBindingException(responseDeclaration, responseData, "Expected one string value to be bound to this response.");
        }

        /* Parse the raw ASCIIMath input */
        final String asciiMathInput = stringResponseData.get(0).trim();
        Value responseValue;
        logger.debug("Attempting to bind raw ASCIIMath input '{}' from mathEntryInteraction", asciiMathInput);
        if (asciiMathInput.length() != 0) {
            /* Convert the ASCIIMath input to the appropriate Math Context
             * variable */
            final AsciiMathHelper helper = new AsciiMathHelper(mathAssessExtensionPackage.getStylesheetCache());
            final MathsContentInputValueWrapper resultWrapper;
            try {
                resultWrapper = helper.createMathsContentFromAsciiMath(asciiMathInput);
            }
            catch (final AsciiMathInputException e) {
                logger.debug("ASCIIMath input '{}' unexpectedly failed bind to a Maths Content variable", asciiMathInput);
                throw new ResponseBindingException(responseDeclaration, responseData, "Math content is too complex for current implementation");
            }
            final List<UpConversionFailure> upConversionFailures = resultWrapper.getUpConversionFailures();
            if (upConversionFailures != null && !upConversionFailures.isEmpty()) {
                logger.debug("ASCIIMath input '{}' could not be bound to a Maths Content variable", asciiMathInput);
                throw new ResponseBindingException(responseDeclaration, responseData, "Math content is too complex for current implementation");
            }
            responseValue = GlueValueBinder.casToJqti(resultWrapper);
        }
        else {
            /* Blank input, so easy */
            responseValue = NullValue.INSTANCE;
        }
        return responseValue;
    }

    @Override
    protected boolean validateResponse(final MathAssessExtensionPackage jqtiExtensionPackage, final InteractionBindingContext interactionBindingContext, final Value responseValue) {
        /* Currently, a successful binding is considered the same as a response
         * being valid */
        return true;
    }
}
