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
package uk.ac.ed.ph.qtiworks.services.domain;

import uk.ac.ed.ph.qtiworks.rendering.SerializationMethod;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import java.io.Serializable;

/**
 * FIXME: Document this!
 *
 * @author David McKain
 */
public final class RenderingOptions implements Serializable {

    private static final long serialVersionUID = -1744991243897646596L;

    /** Required {@link SerializationMethod} */
    private SerializationMethod serializationMethod;

    /** TODO: This is currently duplicated, but is probably cleaner here? */
    private String contextPath;

    private String attemptUrl;

    private String resetUrl;

    private String exitUrl;

    private String resultUrl;

    private String sourceUrl;

    //----------------------------------------------------

    public SerializationMethod getSerializationMethod() {
        return serializationMethod;
    }

    public void setSerializationMethod(final SerializationMethod serializationMethod) {
        this.serializationMethod = serializationMethod;
    }


    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(final String contextPath) {
        this.contextPath = contextPath;
    }


    public String getAttemptUrl() {
        return attemptUrl;
    }

    public void setAttemptUrl(final String attemptUrl) {
        this.attemptUrl = attemptUrl;
    }


    public String getResetUrl() {
        return resetUrl;
    }

    public void setResetUrl(final String resetUrl) {
        this.resetUrl = resetUrl;
    }


    public String getExitUrl() {
        return exitUrl;
    }

    public void setExitUrl(final String exitUrl) {
        this.exitUrl = exitUrl;
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

    //----------------------------------------------------

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
