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
package uk.ac.ed.ph.qtiengine.web;

import uk.ac.ed.ph.qtiengine.EngineException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Some general servlet-related utility methods.
 * 
 * @author David McKain
 */
public final class WebUtilities {
    
    /** 
     * Name of request Attribute that any custom error messages will be stored in when passing
     * to error JSPs.
     * 
     * @see #sendErrorMessagePage(ServletRequest, HttpServletResponse, int, String)
     */
    public static final String ERROR_MESSAGE_ATTRIBUTE_NAME = "uk.ac.ed.ph.qtiengine.web.errorMessage";
    
    public static final String WITHIN_CONTEXT_REQUEST_URL_ATTRIBUTE_NAME = "uk.ac.ed.ph.qtiengine.web.WithinContextRequestUrl";
    
    public static final String FULL_REQUEST_URL_ATTRIBUTE_NAME = "uk.ac.ed.ph.qtiengine.web.FullRequestUrl";
    
    /**
     * Returns the URL for the given request, starting from AFTER the context path and
     * including path info and query parameters.
     * <p>
     * The result is stored in the {@link HttpServletRequest} as an Attribute for later
     * retrieval so as to avoid needed recalculation.
     * 
     * @param request
     */
    public static String getWithinContextRequestUrl(HttpServletRequest request) {
        String result = (String) request.getAttribute(WITHIN_CONTEXT_REQUEST_URL_ATTRIBUTE_NAME);
        if (result==null) {
            StringBuilder builder = new StringBuilder(request.getServletPath());
            if (request.getPathInfo()!=null) {
                builder.append(request.getPathInfo());
            }
            if (request.getQueryString()!=null) {
                builder.append("?").append(request.getQueryString());
            }
            result = builder.toString();
            request.setAttribute(WITHIN_CONTEXT_REQUEST_URL_ATTRIBUTE_NAME, result);
        }
        return result;
    }
    
    /**
     * Returns the URL for the given request, including context path and
     * including path info and query parameters.
     * <p>
     * The result is stored in the {@link HttpServletRequest} as an Attribute for later
     * retrieval so as to avoid needed recalculation.
     * 
     * @param request
     */
    public static String getFullRequestUrl(HttpServletRequest request) {
        String result = (String) request.getAttribute(FULL_REQUEST_URL_ATTRIBUTE_NAME);
        if (result==null) {
            StringBuilder builder = new StringBuilder(request.getContextPath())
                .append(request.getServletPath());
            if (request.getPathInfo()!=null) {
                builder.append(request.getPathInfo());
            }
            if (request.getQueryString()!=null) {
                builder.append("?").append(request.getQueryString());
            }
            result = builder.toString();
            request.setAttribute(FULL_REQUEST_URL_ATTRIBUTE_NAME, result);
        }
        return result;
    }

    /**
     * Wrapper round {@link ServletContext#getInitParameter(String)} that throws a
     * {@link ServletException} if the parameter could not be found.
     * 
     * @param context
     * @param paramName
     * @throws ServletException
     */
    public static String getRequiredInitParameter(ServletContext context, String paramName)
            throws ServletException {
        String result = context.getInitParameter(paramName);
        if (result==null) {
            throw new ServletException("Could not look up servlet context <init-param/> " + paramName);
        }
        return result;
    }

    /**
     * Wrapper round {@link FilterConfig#getInitParameter(String)} that throws a
     * {@link ServletException} if the parameter could not be found.
     * 
     * @param config
     * @param paramName
     * @throws ServletException
     */
    public static String getRequiredInitParameter(FilterConfig config, String paramName)
            throws ServletException {
        String result = config.getInitParameter(paramName);
        if (result == null) {
            throw new ServletException("Could not look up <init-param/> " + paramName
                    + " for filter " + config.getFilterName());
        }
        return result;
    }

    /**
     * Convenience version of {@link HttpServletResponse#sendError(int, String)} that works when
     * using custom error JSPs. (The message parameter is usually ignored in this case, which is
     * annoying!) This method stores the required error message as a request attribute called
     * {@link #ERROR_MESSAGE_ATTRIBUTE_NAME} which can then be picked up by the JSP.
     * 
     * @see #ERROR_MESSAGE_ATTRIBUTE_NAME
     * 
     * @param request
     * @param response
     * @param message
     * @throws IOException 
     */
    public static void sendErrorMessagePage(ServletRequest request, HttpServletResponse response,
            int responseCode, String message) throws IOException {
        request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, message);
        response.sendError(responseCode, message);
    }
    
    public static Map<String,Object> exposeStaticFields(Class<?> globalClass) {
        Map<String, Object> targetMap = new HashMap<String, Object>() {
            private static final long serialVersionUID = -5029839848191296643L;

            @Override
            public Object get(Object key) {
                Object result = super.get(key);
                if (result==null) {
                    throw new EngineException("No exposed field '" + key + "' found");
                }
                return result;
            }
        };
        try {
            for (Field field : globalClass.getFields()) {
                targetMap.put(field.getName(), field.get(globalClass));
            }
        }
        catch (Exception e) {
            throw new EngineException("Could not expose all static fields in class " + globalClass, e);
        }
        return Collections.unmodifiableMap(targetMap);
    }
}
