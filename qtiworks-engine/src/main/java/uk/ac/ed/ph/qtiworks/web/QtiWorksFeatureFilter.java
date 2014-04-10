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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.qtiworks.web;

import uk.ac.ed.ph.qtiworks.config.beans.QtiWorksDeploymentSettings;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

/**
 * This filter controls availability of optional features
 *
 * @author David McKain
 */
public final class QtiWorksFeatureFilter extends AbstractWebFilterUsingApplicationContext {

    private static final Logger logger = LoggerFactory.getLogger(QtiWorksFeatureFilter.class);

    private QtiWorksDeploymentSettings qtiWorksDeploymentSettings;

    @Override
    protected void initWithApplicationContext(final FilterConfig filterConfig, final WebApplicationContext webApplicationContext)
            throws Exception {
        this.qtiWorksDeploymentSettings = webApplicationContext.getBean(QtiWorksDeploymentSettings.class);
    }

    @Override
    public void doWebFilter(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse, final FilterChain chain)
            throws IOException, ServletException {
        final String servletPath = httpRequest.getServletPath();
        final String pathInfo = httpRequest.getPathInfo();

        final boolean isPublicUrl = servletPath.startsWith("/public/")
                || servletPath.equals("/anonymous")
                || servletPath.equals("/web/anonymous") /* (Legacy URL for Uniqurate) */
                ;
        /* NB: REST URL is currently a sub-path of public, so be careful! */
        final boolean isRestUrl = servletPath.equals("/anonymous")
                && pathInfo.equals("/simplerestrunner");

        if (!qtiWorksDeploymentSettings.isPublicDemosEnabled()) {
            /* Public demos are disabled, so intercept access to those URLs */
            if (isPublicUrl && !isRestUrl) {
                disallow(httpRequest, httpResponse);
                return;
            }
        }
        if (!qtiWorksDeploymentSettings.isRestEnabled()) {
            /* REST functionality disabled */
            if (isRestUrl) {
                disallow(httpRequest, httpResponse);
                return;
            }
        }
        chain.doFilter(httpRequest, httpResponse);
    }

    private void disallow(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse) throws IOException {
        logger.info("Access to {} is being forbidden as the corresponding feature is not enabled", httpRequest.getServletPath());
        httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "This feature is not enabled in this instance of QTIWorks");
    }

}
