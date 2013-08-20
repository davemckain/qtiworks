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

import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.ATTR_CODE_NAME;
import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.ATTR_SIMPLIFY_NAME;
import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.MATHASSESS_NAMESPACE_URI;

import uk.ac.ed.ph.qtiworks.mathassess.glue.MathAssessBadCasCodeException;
import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.QtiMaximaProcess;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.ValueWrapper;

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import uk.ac.ed.ph.jacomax.MaximaTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the <tt>org.qtitools.mathassess.CasCondition</tt> customOperator
 *
 * @author Jonathon Hare
 */
public final class CasCondition extends MathAssessOperator {

    private static final long serialVersionUID = -7534979343475172387L;

    private static final Logger logger = LoggerFactory.getLogger(CasCondition.class);

    public CasCondition(final ExpressionParent parent) {
        super(parent);

        getAttributes().add(new StringAttribute(this, ATTR_CODE_NAME, MATHASSESS_NAMESPACE_URI, null, true));
        getAttributes().add(new BooleanAttribute(this, ATTR_SIMPLIFY_NAME, MATHASSESS_NAMESPACE_URI, false, false));
    }

    /**
     * Gets value of code attribute.
     *
     * @return value of code attribute
     * @see #setCode
     */
    public String getCode() {
        return ((StringAttribute) getAttributes().get(ATTR_CODE_NAME, MATHASSESS_NAMESPACE_URI))
                .getComputedValue();
    }

    /**
     * Sets new value of code attribute.
     *
     * @param code new value of code attribute
     * @see #getCode
     */
    public void setCode(final String code) {
        ((StringAttribute) getAttributes().get(ATTR_CODE_NAME, MATHASSESS_NAMESPACE_URI))
            .setValue(code);
    }

    /**
     * Gets value of simplify attribute.
     *
     * @return value of simplify attribute
     * @see #setSimplify
     */
    public boolean getSimplify() {
        return ((BooleanAttribute) getAttributes().get(ATTR_SIMPLIFY_NAME, MATHASSESS_NAMESPACE_URI))
                .getComputedNonNullValue();
    }

    /**
     * Sets new value of simplify attribute.
     *
     * @param simplify new value of simplify attribute
     * @see #getSimplify
     */
    public void setSimplify(final Boolean simplify) {
        ((BooleanAttribute) getAttributes().get(ATTR_SIMPLIFY_NAME, MATHASSESS_NAMESPACE_URI))
            .setValue(simplify);
    }

    @Override
    protected void doAdditionalValidation(final ValidationContext context) {
        /* Nothing to do here */
    }

    @Override
    protected Value maximaEvaluate(final MathAssessExtensionPackage mathAssessExtensionPackage, final ItemProcessingContext context, final Value[] childValues) {
        final boolean simplify = getSimplify();
        final String code = getCode().trim();

        if (logger.isDebugEnabled()) {
            logger.debug("Performing casCondition: code={}, simplify={}, values={}",
                    new Object[] { code, simplify, childValues });
        }

        final ValueWrapper[] casValues = new ValueWrapper[childValues.length];
        for (int i=0; i<childValues.length; i++) {
            final Value value = childValues[i];
            final ValueWrapper casValue = GlueValueBinder.jqtiToCas(childValues[i]);
            if (casValue==null) {
                context.fireRuntimeError(this, "Child value " + value.toQtiString() + " at index "
                        + i + " cannot be passed to Maxima - returning NULL");
                return NullValue.INSTANCE;
            }
            casValues[i] = casValue;
        }

        final QtiMaximaProcess qtiMaximaProcess = mathAssessExtensionPackage.obtainMaximaSessionForThread();
        try {
            return BooleanValue.valueOf(qtiMaximaProcess.executeCasCondition(code, simplify, casValues));
        }
        catch (final MaximaTimeoutException e) {
            context.fireRuntimeError(this, "A timeout occurred executing the CasCondition logic. Returning NULL");
            return NullValue.INSTANCE;
        }
        catch (final MathAssessBadCasCodeException e) {
            context.fireRuntimeError(this, "Your CasCondition code did not work as expected. The CAS input was '"
                    + e.getMaximaInput()
                    + "' and the CAS output was '"
                    + e.getMaximaOutput()
                    + "'. The failure reason was " + e.getReason());
            return NullValue.INSTANCE;
        }
        catch (final RuntimeException e) {
            logger.warn("Unexpected Maxima failure", e);
            context.fireRuntimeError(this, "An unexpected problem occurred while executing this CasCondition");
            return BooleanValue.FALSE;
        }
    }

    @Override
    public BaseType[] getProducedBaseTypes(final ValidationContext context) {
        return new BaseType[] { BaseType.BOOLEAN };
    }

    @Override
    public Cardinality[] getProducedCardinalities(final ValidationContext context) {
        return new Cardinality[] { Cardinality.SINGLE };
    }
}
