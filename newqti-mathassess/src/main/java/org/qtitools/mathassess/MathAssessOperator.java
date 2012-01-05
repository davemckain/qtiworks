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

import static org.qtitools.mathassess.MathAssessConstants.ATTR_SYNTAX_NAME;
import static org.qtitools.mathassess.MathAssessConstants.MATHASSESS_NAMESPACE_URI;

import uk.ac.ed.ph.jqtiplus.attribute.Attribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.control.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.JQTIExtensionPackage;
import uk.ac.ed.ph.jqtiplus.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.exception.QTIEvaluationException;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.CustomOperator;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.value.Value;

import org.qtitools.mathassess.attribute.SyntaxTypeAttribute;
import org.qtitools.mathassess.tools.qticasbridge.MathsContentTooComplexException;
import org.qtitools.mathassess.type.SyntaxType;

import uk.ac.ed.ph.jacomax.MaximaProcessTerminatedException;
import uk.ac.ed.ph.jacomax.MaximaTimeoutException;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract superclass for all MathAssess customOperators
 * 
 * @author Jonathon Hare
 * @author David McKain
 */
public abstract class MathAssessOperator extends CustomOperator {

    private static final long serialVersionUID = 1275749269462837686L;

    private static String IDENTIFIER_REGEX_VALUE = "[a-zA-Z][a-zA-Z0-9]*";

    public MathAssessOperator(JQTIExtensionPackage jqtiExtensionPackage, ExpressionParent parent) {
        super(jqtiExtensionPackage, parent);

        // add a namespace prefix to this if none there, and no global prefix
        if (getNamespacePrefix().length() == 0) {
            getAttributes().add(new StringAttribute(this, "xmlns:ma", MATHASSESS_NAMESPACE_URI,
                    MATHASSESS_NAMESPACE_URI, true));
        }
        getAttributes().add(new SyntaxTypeAttribute(this, getNamespacePrefix() + ATTR_SYNTAX_NAME,
                null, null, true));
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
     * Get the syntax value of the expression.
     * 
     * @return the value of the syntax attribute
     */
    public SyntaxType getSyntax() {
        return ((SyntaxTypeAttribute) getAttributes().get(getNamespacePrefix() + ATTR_SYNTAX_NAME))
                .getValue();
    }

    /**
     * Set the syntax attribute of the expression.
     * 
     * @param syntax value to set
     */
    public void setSyntax(SyntaxType syntax) {
        ((SyntaxTypeAttribute) getAttributes().get(getNamespacePrefix() + ATTR_SYNTAX_NAME))
                .setValue(syntax);
    }

    @Override
    public final Value evaluateSelf(ProcessingContext context, int depth) {
        switch (getSyntax()) {
            case MAXIMA:
                try {
                    /* FIXME: These operators are legal in test contexts too. We
                     * need to support this eventually. */
                    return maximaEvaluate((ItemProcessingContext) context);
                }
                catch (final MaximaProcessTerminatedException e) {
                    throw new QTIEvaluationException("Maxima process was terminated earlier while processing this item", e);
                }
                catch (final MaximaTimeoutException e) {
                    throw new QTIEvaluationException("Maxima call timed out", e);
                }
                catch (final MathsContentTooComplexException e) {
                    throw new QTIEvaluationException("Math content is too complex for current implementation", e);
                }
                catch (final RuntimeException e) {
                    throw new QTIEvaluationException("Unexpected Exception communicating with Maxima", e);
                }

            default:
                throw new QTIEvaluationException("Unsupported syntax type: " + getSyntax());
        }
    }

    protected abstract Value maximaEvaluate(ItemProcessingContext context)
            throws MaximaTimeoutException, MathsContentTooComplexException;

    /**
     * Gets a list of all the variables that can be read by a cas
     * implementation.
     * 
     * @return readable variables
     */
    public List<VariableDeclaration> getAllCASReadableVariableDeclarations() {
        final List<VariableDeclaration> declarations = new ArrayList<VariableDeclaration>();

        for (final VariableDeclaration decl : getAllReadableVariableDeclarations()) {
            if (decl.getIdentifier().toString().matches(IDENTIFIER_REGEX_VALUE)) {
                declarations.add(decl);
            }
        }
        return declarations;
    }

    private List<VariableDeclaration> getAllReadableVariableDeclarations() {
        final List<VariableDeclaration> declarations = new ArrayList<VariableDeclaration>();

        final AssessmentItem item = getRootNode(AssessmentItem.class);
        final AssessmentTest test = getRootNode(AssessmentTest.class);
        if (item != null) {
            declarations.addAll(item.getResponseDeclarations());
            declarations.addAll(item.getTemplateDeclarations());
            declarations.addAll(item.getOutcomeDeclarations());
        }
        else if (test != null) {
            declarations.addAll(test.getOutcomeDeclarations());
        }

        return declarations;
    }

    /**
     * Gets a list of all the variables assigned to by the cas implementation.
     * 
     * @return writable variables
     */
    public List<VariableDeclaration> getAllCASWriteableVariableDeclarations() {
        final List<VariableDeclaration> declarations = new ArrayList<VariableDeclaration>();

        for (final VariableDeclaration decl : getAllWriteableVariableDeclarations()) {
            if (decl.getIdentifier().toString().matches(IDENTIFIER_REGEX_VALUE)) {
                declarations.add(decl);
            }
        }
        return declarations;
    }

    private List<VariableDeclaration> getAllWriteableVariableDeclarations() {
        final List<VariableDeclaration> declarations = new ArrayList<VariableDeclaration>();

        final AssessmentItem item = getRootNode(AssessmentItem.class);
        final AssessmentTest test = getRootNode(AssessmentTest.class);
        if (item != null) {
            declarations.addAll(item.getTemplateDeclarations());
            declarations.addAll(item.getOutcomeDeclarations());
        }
        else if (test != null) {
            declarations.addAll(test.getOutcomeDeclarations());
        }

        return declarations;
    }

    @Override
    public final void validate(ValidationContext context, ValidationResult result) {
        super.validate(context, result);

        /* First make sure that variable names are all acceptable */
        for (final VariableDeclaration decl : getAllReadableVariableDeclarations()) {
            final String ident = decl.getIdentifier().toString();
            if (!ident.matches(IDENTIFIER_REGEX_VALUE)) {
                result.add(new ValidationWarning(this, "Variable " + ident
                        + " does not follow the naming convention stated in the MathAssess extensions specification"));
            }
        }

        /* Validate syntax attribute */
        if (getSyntax() != null) {
            switch (getSyntax()) {
                case MAXIMA:
                    break;

                default:
                    result.add(new ValidationError(this, "Unsupported syntax type: " + getSyntax()));
            }
        }

        /* Get subclass to validate remaining attrs and/or children */
        doAdditionalValidation(context, result);
    }

    protected abstract void doAdditionalValidation(ValidationContext context, ValidationResult result);
}
