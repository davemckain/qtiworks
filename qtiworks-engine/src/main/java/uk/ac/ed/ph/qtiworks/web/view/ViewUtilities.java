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
package uk.ac.ed.ph.qtiworks.web.view;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

/**
 * Some potentially useful view-related utility methods.
 * 
 * @author David McKain
 */
public class ViewUtilities {
    
    public static Page decodePathName(String pageName) {
        try {
            return Page.valueOf(pageName);
        }
        catch (IllegalArgumentException e) {
            throw new QtiWorksLogicException("Enumerated page name " + pageName + " was not found");
        }
    }
    
    public static String createSimpleRedirect(final Page page, final Object... nameValueParams) {
        return createRedirect(page, null, null, nameValueParams);
    }

    public static String createRedirect(final Page page, final String pathInfo,
            final String fragment, final Object... nameValueParams) {
        return buildPageLink("redirect:", page, pathInfo, fragment, nameValueParams);
    }
    
    public static String createPageLink(final HttpServletRequest request, final Page page,
            final String pathInfo, final String fragment, final Object... nameValueParams) {
        return buildPageLink(request.getContextPath(), page, pathInfo, fragment, nameValueParams);
    }
    
    /**
     * Version of {@link #createPageLink(HttpServletRequest, Page, String, String, Object...)}
     * that omits the context path, which is suitable for passing to a Spring "redirect:..."
     * view.
     *  
     * @param request
     * @param page
     * @param pathInfo
     * @param nameValueParams
     */
    public static String createRedirectLink(final HttpServletRequest request, final Page page,
            final String pathInfo, final String fragment, final Object... nameValueParams) {
        return buildPageLink("", page, pathInfo, fragment, nameValueParams);
    }
    
    /**
     * Version of {@link #createPageLink(HttpServletRequest, Page, String, String, Object...)}
     * that accepts the webapp base URL as a String, which is useful when working outside the JSP
     * domain.
     * 
     * @param page
     * @param pathInfo
     * @param nameValueParams
     */
    public static String createPageLink(final String webappBaseUrl, final Page page,
            final String pathInfo, final String fragment, final Object... nameValueParams) {
        return buildPageLink(webappBaseUrl, page, pathInfo, fragment, nameValueParams);
    }
    
    private static String buildPageLink(final String base, final Page page,
            final String pathInfo, final String fragment, final Object... nameValueParams) {
        /* Build URL from context path up to page URL */
        StringBuilder resultBuilder = new StringBuilder(base).append(page.getWithinContextUrl());
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
        catch (UnsupportedEncodingException e) {
            throw QtiWorksLogicException.unexpectedException(e);
        }
        
        /* That's it */
        return resultBuilder.toString();
    }
}
