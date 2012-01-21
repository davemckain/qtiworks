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
package org.qtitools.mathassess;

import static org.qtitools.mathassess.MathAssessConstants.ATTR_EXPECTED_LENGTH_NAME;
import static org.qtitools.mathassess.MathAssessConstants.ATTR_PRINT_IDENTIFIER_NAME;
import static org.qtitools.mathassess.MathAssessConstants.ATTR_SYNTAX_NAME;
import static org.qtitools.mathassess.MathAssessConstants.MATHASSESS_NAMESPACE_URI;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.attribute.Attribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QTIEvaluationException;
import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.CustomInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.AssessmentItemAttemptController;
import uk.ac.ed.ph.jqtiplus.state.AssessmentItemState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.StringValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import org.qtitools.mathassess.attribute.SyntaxTypeAttribute;
import org.qtitools.mathassess.tools.qticasbridge.ASCIIMathMLHelper;
import org.qtitools.mathassess.tools.qticasbridge.types.MathsContentInputValueWrapper;
import org.qtitools.mathassess.type.SyntaxType;

import uk.ac.ed.ph.snuggletex.upconversion.UpConversionFailure;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the <tt>org.qtitools.mathassess.MathEntryInteraction</tt>
 * customInteraction
 * 
 * @author Jonathon Hare
 */
public final class MathEntryInteraction extends CustomInteraction {

    private static final long serialVersionUID = -7450990903827581043L;

    private static final Logger logger = LoggerFactory.getLogger(MathEntryInteraction.class);

    public MathEntryInteraction(JqtiExtensionPackage jqtiExtensionPackage, XmlNode parent) {
        super(jqtiExtensionPackage, parent);

        // add a namespace prefix to this if none there, and no global prefix
        if (getNamespacePrefix().length() == 0) {
            getAttributes().add(
                    new StringAttribute(this, "xmlns:ma", MATHASSESS_NAMESPACE_URI,
                            MATHASSESS_NAMESPACE_URI, true));
        }

        getAttributes().add(new SyntaxTypeAttribute(this, getNamespacePrefix() + ATTR_SYNTAX_NAME, null, null, true));
        getAttributes().add(new IntegerAttribute(this, getNamespacePrefix() + ATTR_EXPECTED_LENGTH_NAME, null, null, false));
        getAttributes().add(new IdentifierAttribute(this, getNamespacePrefix() + ATTR_PRINT_IDENTIFIER_NAME, null, null, false));
    }

    /* Iterate through parent nodes looking for a mathassess namespace decl If
     * one is found return the prefix, otherwise return empty string */
    protected String getNamespacePrefix() {
        AbstractNode parent = this;
        while (parent != null) {
            for (final Attribute attr : parent.getAttributes()) {
                if (attr.getName() != null && attr.getName().startsWith("xmlns:")
                        && attr.valueToString() != null
                        && attr.valueToString().equals(MATHASSESS_NAMESPACE_URI)) {
                    return attr.getName().substring(6) + ":";
                }
            }
            parent = (AbstractNode) parent.getParent();
        }
        return "";
    }

    /**
     * Get the value of the syntax attribute of the interaction.
     * 
     * @return the value of the syntax attribute
     */
    public SyntaxType getSyntax() {
        return ((SyntaxTypeAttribute) getAttributes().get(getNamespacePrefix() + ATTR_SYNTAX_NAME))
                .getValue();
    }

    /**
     * Set the syntax attribute of the interaction.
     * 
     * @param syntax value to set
     */
    public void setSyntax(SyntaxType syntax) {
        ((SyntaxTypeAttribute) getAttributes().get(getNamespacePrefix() + ATTR_SYNTAX_NAME))
                .setValue(syntax);
    }

    /**
     * Get the value of the printIdentifier attribute of the interaction.
     * 
     * @return the value of the printIdentifier attribute
     */
    public Identifier getPrintIdentifier() {
        return getAttributes().getIdentifierAttribute(getNamespacePrefix() + ATTR_PRINT_IDENTIFIER_NAME).getValue();
    }

    /**
     * Set the printIdentifier attribute of the interaction.
     * 
     * @param printIdentifier value to set
     */
    public void setPrintIdentifier(Identifier printIdentifier) {
        getAttributes().getIdentifierAttribute(getNamespacePrefix() + ATTR_PRINT_IDENTIFIER_NAME).setValue(printIdentifier);
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
        return getRootObject(AssessmentItem.class).getResponseDeclaration(getPrintIdentifier());
    }

    @Override
    public void validate(ValidationContext context) {
        super.validate(context);

        if (getResponseIdentifier() != null) {
            final ResponseDeclaration declaration = getResponseDeclaration();
            if (declaration != null && declaration.getCardinality() != null
                    && !declaration.getCardinality().isRecord()) {
                context.add(new ValidationError(this, "Response variable must have record cardinality"));
            }
        }

        if (getPrintIdentifier() != null) {
            final ResponseDeclaration declaration = getPrintIdentifierResponseDeclaration();
            if (declaration != null && declaration.getCardinality() != null
                    && !declaration.getCardinality().isSingle()) {
                context.add(new ValidationError(this, "printIdentifier response variable must have record cardinality"));
            }

            if (declaration != null && declaration.getBaseType() != null
                    && !declaration.getBaseType().isString()) {
                context.add(new ValidationError(this, "printIdentifier response variable must have string base type"));
            }
        }
    }

    @Override
    public void bindResponse(AssessmentItemAttemptController itemController, List<String> responseList) {
        if (responseList.size() != 1) {
            throw new QTIEvaluationException("Error: Expected one value to be returned from interaction.");
        }

        /* Parse the raw ASCIIMath input */
        final String asciiMathInput = responseList.get(0).trim();
        Value responseValue;
        Value printResponseValue;
        logger.info("Attempting to bind raw ASCIIMath input '{}' from mathEntryInteraction", asciiMathInput);
        if (asciiMathInput.length() != 0) {
            /* Convert the ASCIIMath input to the appropriate Math Context
             * variable */
            final MathAssessExtensionPackage mathAssessExtensionPackage = (MathAssessExtensionPackage) getJQTIExtensionPackage();
            final ASCIIMathMLHelper helper = new ASCIIMathMLHelper(mathAssessExtensionPackage.getStylesheetCache());
            final MathsContentInputValueWrapper resultWrapper = helper.createMathsContentFromASCIIMath(asciiMathInput);
            final List<UpConversionFailure> upConversionFailures = resultWrapper.getUpConversionFailures();
            if (upConversionFailures != null && !upConversionFailures.isEmpty()) {
                logger.warn("ASCIIMath input '{}' could not be bound to a Maths Content variable", asciiMathInput);
                throw new QTIParseException("Error: Math content is too complex for current implementation");
            }
            responseValue = CasTypeGlue.convertToJQTI(resultWrapper);
            printResponseValue = new StringValue(resultWrapper.getPMathML());

        }
        else {
            /* Blank input, so easy */
            responseValue = NullValue.INSTANCE;
            printResponseValue = NullValue.INSTANCE;
        }

        /* Now bind the variables */
        final AssessmentItemState itemState = itemController.getItemState();
        itemState.setResponseValue(getResponseDeclaration(), responseValue);
        if (getPrintIdentifier() != null) {
            /* handle stringIdentifier binding if required */
            itemState.setResponseValue(getPrintIdentifier(), printResponseValue);
        }
    }

    @Override
    public boolean validateResponse(AssessmentItemAttemptController itemController, Value responseValue) {
        /* Currently, a successful binding is considered the same as a response
         * being valid */
        return true;
    }
}
