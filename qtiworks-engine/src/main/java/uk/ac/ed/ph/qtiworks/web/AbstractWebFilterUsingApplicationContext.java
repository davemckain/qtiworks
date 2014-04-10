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

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Convenience base class for web filters that need access to the Spring
 * {@link WebApplicationContext} when initialising themselves.
 *
 * @author David McKain
 */
public abstract class AbstractWebFilterUsingApplicationContext implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AbstractWebFilterUsingApplicationContext.class);

    /** Spring {@link WebApplicationContext} */
    private WebApplicationContext applicationContext;

    @Override
    public final void init(final FilterConfig filterConfig) throws ServletException {
        /* Get main business Objects */
        try {
            applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
            initWithApplicationContext(filterConfig, applicationContext);
        }
        catch (final Exception e) {
            logger.error("initWithApplicationContext() failed on " + this.getClass().getSimpleName(), e);
            throw (e instanceof ServletException) ? (ServletException) e : new ServletException(e);
        }
    }

    /**
     * Subclasses should fill in to access anything they need from the {@link WebApplicationContext}.
     * Any {@link Exception} thrown will be converted into a {@link ServletException}
     *
     * @param webApplicationContext
     */
    protected abstract void initWithApplicationContext(final FilterConfig filterConfig, final WebApplicationContext webApplicationContext)
        throws Exception;

    /**
     * Convenience no-op. Subclasses can override as required.
     */
    @Override
    public void destroy() {
        /* No-op */
    }

    /**
     * Conveniently checks and casts the generic {@link ServletRequest} and {@link ServletResponse} objects
     * into {@link HttpServletRequest} and {@link HttpServletResponse}, before passing to subclass.
     */
    @Override
    public final void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        /* This filter can only HTTP stuff */
        if (!(request instanceof HttpServletRequest)) {
            throw new QtiWorksLogicException("Expected request to be a HttpServletRequest");
        }
        if (!(response instanceof HttpServletResponse)) {
            throw new QtiWorksLogicException("Expected response to be a HttpServletResponse");
        }
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        doWebFilter(httpRequest, httpResponse, chain);
    }

    /**
     * Subclasses should implement this to do their filtering.
     *
     * @param httpRequest
     * @param httpResponse
     * @param chain
     */
    protected abstract void doWebFilter(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse,
            final FilterChain chain)
            throws IOException, ServletException;
}
