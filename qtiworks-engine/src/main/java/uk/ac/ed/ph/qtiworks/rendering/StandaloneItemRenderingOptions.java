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
package uk.ac.ed.ph.qtiworks.rendering;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import javax.validation.constraints.NotNull;

/**
 * FIXME: Document this type!
 *
 * @author David McKain
 */
public final class StandaloneItemRenderingOptions extends AbstractRenderingOptions {

    private static final long serialVersionUID = -9121795157165098560L;

    @NotNull
    private String attemptUrl;

    @NotNull
    private String closeUrl;

    @NotNull
    private String resetUrl;

    @NotNull
    private String reinitUrl;

    @NotNull
    private String solutionUrl;

    @NotNull
    private String resultUrl;

    @NotNull
    private String sourceUrl;

    @NotNull
    private String terminateUrl;

    //----------------------------------------------------

    public String getAttemptUrl() {
        return attemptUrl;
    }

    public void setAttemptUrl(final String attemptUrl) {
        this.attemptUrl = attemptUrl;
    }


    public String getCloseUrl() {
        return closeUrl;
    }

    public void setCloseUrl(final String closeUrl) {
        this.closeUrl = closeUrl;
    }


    public String getResetUrl() {
        return resetUrl;
    }

    public void setResetUrl(final String resetUrl) {
        this.resetUrl = resetUrl;
    }


    public String getReinitUrl() {
        return reinitUrl;
    }

    public void setReinitUrl(final String reinitUrl) {
        this.reinitUrl = reinitUrl;
    }


    public String getSolutionUrl() {
        return solutionUrl;
    }

    public void setSolutionUrl(final String solutionUrl) {
        this.solutionUrl = solutionUrl;
    }


    public String getResultUrl() {
        return resultUrl;
    }

    public void setResultUrl(final String resultUrl) {
        this.resultUrl = resultUrl;
    }


    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(final String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }


    public String getTerminateUrl() {
        return terminateUrl;
    }

    public void setTerminateUrl(final String terminateUrl) {
        this.terminateUrl = terminateUrl;
    }

    //----------------------------------------------------

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
