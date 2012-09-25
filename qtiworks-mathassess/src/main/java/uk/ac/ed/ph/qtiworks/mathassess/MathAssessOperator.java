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
package uk.ac.ed.ph.qtiworks.mathassess;

import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.ATTR_SYNTAX_NAME;
import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.MATHASSESS_NAMESPACE_URI;

import uk.ac.ed.ph.qtiworks.mathassess.attribute.SyntaxAttribute;
import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.QtiMaximaProcess;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.ValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.value.SyntaxType;

import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.CustomOperator;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for all MathAssess customOperators
 *
 * @author Jonathon Hare
 * @author David McKain
 */
public abstract class MathAssessOperator extends CustomOperator<MathAssessExtensionPackage> {

    private static final long serialVersionUID = 1275749269462837686L;

    private static final Logger logger = LoggerFactory.getLogger(MathAssessOperator.class);

    private static String IDENTIFIER_REGEX_VALUE = "[a-zA-Z][a-zA-Z0-9]*";

    public MathAssessOperator(final ExpressionParent parent) {
        super(parent);
        getAttributes().add(new SyntaxAttribute(this, ATTR_SYNTAX_NAME, MATHASSESS_NAMESPACE_URI));
    }

    public SyntaxType getSyntax() {
        return ((SyntaxAttribute) getAttributes().get(ATTR_SYNTAX_NAME, MATHASSESS_NAMESPACE_URI))
                .getComputedValue();
    }

    public void setSyntax(final SyntaxType syntax) {
        ((SyntaxAttribute) getAttributes().get(ATTR_SYNTAX_NAME, MATHASSESS_NAMESPACE_URI))
                .setValue(syntax);
    }

    @Override
    public final Value evaluateSelf(final MathAssessExtensionPackage mathAssessExtensionPackage, final ProcessingContext context, final Value[] childValues, final int depth) {
        switch (getSyntax()) {
            case MAXIMA:
                return maximaEvaluate(mathAssessExtensionPackage, (ItemProcessingContext) context, childValues);

            default:
                context.fireValidationError(this, "Unsupported syntax type: " + getSyntax() + " - returning NULL");
                return NullValue.INSTANCE;
        }
    }

    protected abstract Value maximaEvaluate(MathAssessExtensionPackage mathAssessExtensionPackage, ItemProcessingContext context, Value[] childValues);

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

    protected void passVariablesToMaxima(final QtiMaximaProcess qtiMaximaProcess, final ItemProcessingContext context) {
        /* Pass variables to Maxima */
        logger.trace("Passing variables to maxima");
        for (final VariableDeclaration declaration : getAllCASReadableVariableDeclarations()) {
            passVariableToMaxima(qtiMaximaProcess, context, declaration);
        }
    }

    protected void passVariableToMaxima(final QtiMaximaProcess qtiMaximaProcess, final ItemProcessingContext context,
            final VariableDeclaration declaration) {
        final Value value = context.getItemSessionState().getVariableValue(declaration);
        final ValueWrapper valueWrapper = GlueValueBinder.jqtiToCas(value);
        if (valueWrapper!=null) {
            qtiMaximaProcess.passQTIVariableToMaxima(declaration.getIdentifier().toString(), valueWrapper);
        }
        else {
            context.fireRuntimeInfo(this, "Variable " + declaration.getIdentifier() + " is one of the types supported by the MathAssess extensions so has not been passed to Maxima");
        }
    }

    @Override
    public final void validate(final ValidationContext context) {
        super.validate(context);

        /* First make sure that variable names are all acceptable */
        for (final VariableDeclaration decl : getAllReadableVariableDeclarations()) {
            final String ident = decl.getIdentifier().toString();
            if (!ident.matches(IDENTIFIER_REGEX_VALUE)) {
                context.fireValidationWarning(this, "Variable " + ident
                        + " does not follow the naming convention stated in the MathAssess extensions specification");
            }
        }

        /* Validate syntax attribute */
        if (getSyntax() != null) {
            switch (getSyntax()) {
                case MAXIMA:
                    break;

                default:
                    context.fireValidationError(this, "Unsupported syntax type: " + getSyntax());
            }
        }

        /* Get subclass to validate remaining attrs and/or children */
        doAdditionalValidation(context);
    }

    protected abstract void doAdditionalValidation(ValidationContext context);
}
