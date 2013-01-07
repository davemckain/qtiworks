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
package uk.ac.ed.ph.qtiworks.web;

import uk.ac.ed.ph.qtiworks.services.domain.OutputStreamer;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * Base implementation of {@link OutputStreamer} suitable for sending data
 * via an {@link HttpServletResponse}
 *
 * @author David McKain
 */
abstract class ServletOutputStreamer implements OutputStreamer {

    protected final HttpServletResponse response;
    protected final String resourceEtag;
    protected final DateFormat httpDateFormat;

    public ServletOutputStreamer(final HttpServletResponse response, final String resourceEtag) {
        this.response = response;
        this.resourceEtag = resourceEtag;

        /* HTTP date format. NB: timezone must be GMT! */
        this.httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    protected void transferResultStream(final InputStream resultStream) throws IOException {
        final ServletOutputStream servletOutputStream = response.getOutputStream();
        try {
            IOUtils.copy(resultStream, servletOutputStream);
        }
        finally {
            servletOutputStream.flush();
        }
    }

    protected void maybeSetEtag() {
        if (resourceEtag!=null) {
            response.setHeader("ETag", resourceEtag);
        }
    }

    protected void setContentType(final String contentType) {
        response.setContentType(contentType);
    }

    protected void setContentLength(final long contentLength) {
        response.setContentLength((int) contentLength); /* Huge files aren't going to happen... */
    }

    protected void setLastModifiedTime(final Date date) {
        response.setHeader("Last-Modified", formatHttpDate(date));
    }

    protected String formatHttpDate(final Date date) {
        return httpDateFormat.format(date);
    }
}
