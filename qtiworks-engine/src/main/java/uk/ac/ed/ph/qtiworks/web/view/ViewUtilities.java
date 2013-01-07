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
package uk.ac.ed.ph.qtiworks.web.view;

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

/**
 * Some potentially useful view-related utility methods.
 *
 * @author David McKain
 */
public final class ViewUtilities {

    public static String createInternalLink(final HttpServletRequest request, final String withinContextPath) {
        return buildPageLink(request.getContextPath(), withinContextPath, null, null);
    }

    public static String createInternalLink(final HttpServletRequest request, final String withinContextPath,
            final String pathInfo, final String fragment, final Object... nameValueParams) {
        return buildPageLink(request.getContextPath(), withinContextPath, pathInfo, fragment, nameValueParams);
    }

    private static String buildPageLink(final String base, final String withinContextPath,
            final String pathInfo, final String fragment, final Object... nameValueParams) {
        /* Build URL from context path up to page URL */
        final StringBuilder resultBuilder = new StringBuilder(base).append(withinContextPath);
        try {
            /* Append pathInfo, if required. We'll URL-encode this too as it is often
             * something like a client's file name or something potentially awful.
             */
            if (pathInfo!=null) {
                resultBuilder.append('/').append(URLEncoder.encode(pathInfo, "UTF-8"));
            }

            /* Append query parameters */
            Object name, value;
            for (int i=0; i<nameValueParams.length; ) {
                name = nameValueParams[i++];
                value = nameValueParams[i++];
                resultBuilder.append(i==2 ? '?' : '&')
                    .append(name) /* (Assume name is already safe) */
                    .append('=')
                    .append(URLEncoder.encode(value.toString(), "UTF-8")); /* (Need to escape value) */
            }

            /* Append fragment, if requested. We're not encoding this as the fragment should
             * include the '#' */
            if (fragment!=null) {
                resultBuilder.append(fragment);
            }
        }
        catch (final UnsupportedEncodingException e) {
            throw QtiWorksRuntimeException.unexpectedException(e);
        }

        /* That's it */
        return resultBuilder.toString();
    }

    //-------------------------------------------------

    public static final DateFormat getTimeFormat() {
        return new SimpleDateFormat("HH:mm");
    }

    public static final DateFormat getDateFormat() {
        return new SimpleDateFormat("dd/MM/yy");
    }

    public static final DateFormat getDateAndTimeFormat() {
        return new SimpleDateFormat("dd/MM/yy\u00a0'at'\u00a0HH:mm");
    }

    public static final DateFormat getDayDateAndTimeFormat() {
        return new SimpleDateFormat("EEEE\u00a0dd/MM/yy\u00a0'at'\u00a0HH:mm");
    }
}
