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
package uk.ac.ed.ph.jqtiplus.state;

import java.io.Serializable;

/**
 * Represents the "effective" values of <code>itemSessionControl</code>
 * after any explicitly-provided values have been merged together and
 * defaults applied.
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

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(final int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }


    public boolean isShowFeedback() {
        return showFeedback;
    }

    public void setShowFeedback(final boolean showFeedback) {
        this.showFeedback = showFeedback;
    }


    public boolean isAllowReview() {
        return allowReview;
    }

    public void setAllowReview(final boolean allowReview) {
        this.allowReview = allowReview;
    }


    public boolean isShowSolution() {
        return showSolution;
    }

    public void setShowSolution(final boolean showSolution) {
        this.showSolution = showSolution;
    }


    public boolean isAllowComment() {
        return allowComment;
    }

    public void setAllowComment(final boolean allowComment) {
        this.allowComment = allowComment;
    }


    public boolean isAllowSkipping() {
        return allowSkipping;
    }

    public void setAllowSkipping(final boolean allowSkipping) {
        this.allowSkipping = allowSkipping;
    }


    public boolean isValidateResponses() {
        return validateResponses;
    }

    public void setValidateResponses(final boolean validateResponses) {
        this.validateResponses = validateResponses;
    }
}
