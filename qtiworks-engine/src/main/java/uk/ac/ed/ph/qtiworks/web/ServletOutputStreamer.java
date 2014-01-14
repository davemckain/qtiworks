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

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * Implementation of {@link OutputStreamer} suitable for streaming data
 * via an {@link HttpServletResponse}.
 * <p>
 * This supports optional caching for resources via entity tags where it is considered safe
 * or sensible to do so.
 *
 * @author David McKain
 */
public final class ServletOutputStreamer implements OutputStreamer {

    private final HttpServletResponse httpServletResponse;
    private final String etag;

    public ServletOutputStreamer(final HttpServletResponse httpServletResponse, final String etag) {
        Assert.notNull(httpServletResponse, "httpServletResponse");
        this.httpServletResponse = httpServletResponse;
        this.etag = etag;
    }

    @Override
    public void stream(final String contentType, final long contentLength, final Date lastModifiedTime,
            final InputStream resultStream)
            throws IOException {
        /* Set appropriate headers */
        httpServletResponse.setContentType(contentType);
        httpServletResponse.setContentLength((int) contentLength); /* Huge files aren't going to happen... */
        if (lastModifiedTime!=null) {
            httpServletResponse.setHeader("Last-Modified", WebUtilities.formatHttpDate(lastModifiedTime));
        }

        /* Set suitable caching headers based on presence of ETag */
        if (etag!=null) {
            httpServletResponse.setHeader("ETag", etag);
            httpServletResponse.setHeader("Cache-Control", "private, must-revalidate");
        }
        else {
            httpServletResponse.setHeader("Cache-Control", "private, no-cache, no-store, max-age=0, must-revalidate");
        }

        /* Final stream data to ServletOutputStream */
        final ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        try {
            IOUtils.copy(resultStream, servletOutputStream);
        }
        finally {
            servletOutputStream.flush();
        }
    }
}
