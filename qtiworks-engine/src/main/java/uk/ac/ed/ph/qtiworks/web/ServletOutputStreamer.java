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
package uk.ac.ed.ph.qtiworks.web;

import uk.ac.ed.ph.qtiworks.services.OutputStreamer;
import uk.ac.ed.ph.qtiworks.services.ServiceUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * Implementation of {@link OutputStreamer} suitable for sending data
 * via an {@link HttpServletResponse}
 *
 * @author David McKain
 */
public final class ServletOutputStreamer implements OutputStreamer {

    private final HttpServletResponse response;
    private final DateFormat httpDateFormat;
    private final long maxCacheAge;

    public ServletOutputStreamer(final HttpServletResponse response, final long maxCacheAge) {
        this.response = response;
        this.httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        this.maxCacheAge = maxCacheAge;
    }

    @Override
    public void streamDynamic(final String pseudoResourceUri, final String contentType, final int contentLength, final Date lastModifiedTime, final InputStream resultStream)
            throws IOException {
        setContentType(contentType);
        setContentLength(contentLength);
        setLastModifiedTime(lastModifiedTime);
        response.setHeader("ETag", computeEtag(pseudoResourceUri));
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Expires", formatHttpDate(lastModifiedTime));
        final ServletOutputStream servletOutputStream = response.getOutputStream();
        try {
            IOUtils.copy(resultStream, servletOutputStream);
        }
        finally {
            servletOutputStream.flush();
        }
    }

    @Override
    public void streamCacheable(final String pseudoResourceUri, final String contentType, final int contentLength, final Date lastModifiedTime, final InputStream resultStream)
            throws IOException {
        setContentType(contentType);
        setContentLength(contentLength);
        setLastModifiedTime(lastModifiedTime);
        response.setHeader("ETag", computeEtag(pseudoResourceUri));
        response.setHeader("Cache-Control", "max-age=" + maxCacheAge);
        response.setHeader("Expires", formatHttpDate(new Date(lastModifiedTime.getTime() + maxCacheAge)));
        final ServletOutputStream servletOutputStream = response.getOutputStream();
        try {
            IOUtils.copy(resultStream, servletOutputStream);
        }
        finally {
            servletOutputStream.flush();
        }
    }

    private void setContentType(final String contentType) {
        response.setContentType(contentType);
    }

    private void setContentLength(final int contentLength) {
        response.setContentLength(contentLength);
    }

    private void setLastModifiedTime(final Date date) {
        response.setHeader("Last-Modified", formatHttpDate(date));
    }

    private String formatHttpDate(final Date date) {
        return httpDateFormat.format(date);
    }

    private String computeEtag(final String pseudoResourceUri) {
        return ServiceUtilities.computeSha1Digest(pseudoResourceUri);

    }
}
