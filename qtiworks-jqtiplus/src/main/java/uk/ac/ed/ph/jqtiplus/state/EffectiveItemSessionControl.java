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
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.test.ItemSessionControl;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingInitializer;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents the "effective" values of <code>itemSessionControl</code>
 * after any explicitly-provided values have been merged together and
 * defaults applied.
 * <p>
 * Instances of this class should be created using the static factory
 * methods only and should be considered immutable once created.
 *
 * @see TestProcessingInitializer
 * @see TestProcessingMap
 *
 * @author David McKain
 */
public final class EffectiveItemSessionControl implements Serializable {

    private static final long serialVersionUID = 2032581389894373540L;

    private int maxAttempts;
    private boolean showFeedback;
    private boolean allowReview;
    private boolean showSolution;
    private boolean allowComment;
    private boolean allowSkipping;
    private boolean validateResponses;

    private EffectiveItemSessionControl() {
        /* No public constructor */
    }

    public EffectiveItemSessionControl(final int maxAttempts, final boolean showFeedback, final boolean allowReview,
            final boolean showSolution, final boolean allowComment, final boolean allowSkipping, final boolean validateResponses) {
        this.maxAttempts = maxAttempts;
        this.showFeedback = showFeedback;
        this.allowReview = allowReview;
        this.showSolution = showSolution;
        this.allowComment = allowComment;
        this.allowSkipping = allowSkipping;
        this.validateResponses = validateResponses;
    }

    public static EffectiveItemSessionControl createDefault() {
        final EffectiveItemSessionControl defaultSessionControl = new EffectiveItemSessionControl();
        defaultSessionControl.maxAttempts = ItemSessionControl.MAX_ATTEMPTS_DEFAULT_VALUE;
        defaultSessionControl.showFeedback = ItemSessionControl.SHOW_FEEDBACK_DEFAULT_VALUE;
        defaultSessionControl.allowReview = ItemSessionControl.ALLOW_REVIEW_DEFAULT_VALUE;
        defaultSessionControl.showSolution = ItemSessionControl.SHOW_SOLUTION_DEFAULT_VALUE;
        defaultSessionControl.allowComment = ItemSessionControl.ALLOW_COMMENT_DEFAULT_VALUE;
        defaultSessionControl.allowSkipping = ItemSessionControl.ALLOW_SKIPPING_DEFAULT_VALUE;
        defaultSessionControl.validateResponses = ItemSessionControl.VALIDATE_RESPONSES_DEFAULT_VALUE;
        return defaultSessionControl;
    }

    public static EffectiveItemSessionControl override(final EffectiveItemSessionControl parent, final ItemSessionControl child) {
        if (child==null) {
            return parent;
        }
        final EffectiveItemSessionControl result = new EffectiveItemSessionControl();
        result.maxAttempts = mergeInt(parent.getMaxAttempts(), child.getMaxAttempts());
        result.showFeedback = mergeBoolean(parent.isShowFeedback(), child.getShowFeedback());
        result.allowReview = mergeBoolean(parent.isAllowReview(), child.getAllowReview());
        result.showSolution = mergeBoolean(parent.isShowSolution(), child.getShowSolution());
        result.allowComment = mergeBoolean(parent.isAllowComment(), child.getAllowComment());
        result.allowSkipping = mergeBoolean(parent.isAllowSkipping(), child.getAllowSkipping());
        result.validateResponses = mergeBoolean(parent.isValidateResponses(), child.getValidateResponses());
        return result;
    }

    private static int mergeInt(final int parentValue, final Integer maybeChildValue) {
        return maybeChildValue!=null ? maybeChildValue.intValue() : parentValue;
    }

    private static boolean mergeBoolean(final boolean parentValue, final Boolean maybeChildValue) {
        return maybeChildValue!=null ? maybeChildValue.booleanValue() : parentValue;
    }


    public int getMaxAttempts() {
        return maxAttempts;
    }

    public boolean isShowFeedback() {
        return showFeedback;
    }

    public boolean isAllowReview() {
        return allowReview;
    }

    public boolean isShowSolution() {
        return showSolution;
    }

    public boolean isAllowComment() {
        return allowComment;
    }

    public boolean isAllowSkipping() {
        return allowSkipping;
    }

    public boolean isValidateResponses() {
        return validateResponses;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof EffectiveItemSessionControl)) {
            return false;
        }
        final EffectiveItemSessionControl other = (EffectiveItemSessionControl) obj;
        return maxAttempts==other.maxAttempts
                && showFeedback==other.showFeedback
                && allowReview==other.allowReview
                && showSolution==other.showSolution
                && allowComment==other.allowComment
                && allowSkipping==other.allowSkipping
                && validateResponses==other.validateResponses;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                Integer.valueOf(maxAttempts),
                Boolean.valueOf(showFeedback),
                Boolean.valueOf(allowReview),
                Boolean.valueOf(showSolution),
                Boolean.valueOf(allowComment),
                Boolean.valueOf(allowSkipping),
                Boolean.valueOf(validateResponses)
        });
    }

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
