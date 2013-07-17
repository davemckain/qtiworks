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
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiContext;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiResource;
import uk.ac.ed.ph.qtiworks.services.base.ServiceUtilities;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.QtiContentPackageExtractor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Some convenience EL functions for the view/JSP layer.
 *
 * @author David McKain
 */
public final class ElFunctions {

    public static String internalLink(final PageContext pageContext, final String withinContextUrl) {
        return escapeLink(ViewUtilities.createInternalLink(getRequest(pageContext), withinContextUrl));
    }

    public static String escapeLink(final String link) {
        try {
            return new URI(link).toASCIIString();
        }
        catch (final URISyntaxException e) {
            throw new QtiWorksRuntimeException("Bad URI link " + link);
        }
    }

    private static HttpServletRequest getRequest(final PageContext pageContext) {
        return (HttpServletRequest) pageContext.getRequest();
    }

    //-------------------------------------------------

    public static String trimSentence(final String sentence, final Integer maxLength) {
        return ServiceUtilities.trimSentence(sentence, maxLength.intValue());
    }

    public static String formatTime(final Date time) {
        return time!=null ? ViewUtilities.getTimeFormat().format(time) : "";
    }

    public static String formatDate(final Date time) {
        return time!=null ? ViewUtilities.getDateFormat().format(time) : "";
    }

    public static String formatDateAndTime(final Date time) {
        return time!=null ? ViewUtilities.getDateAndTimeFormat().format(time) : "";
    }

    public static String formatDayDateAndTime(final Date time) {
        return time!=null ? ViewUtilities.getDayDateAndTimeFormat().format(time) : "";
    }

    public static String dumpObject(final Object object) {
        return escapeXml(ObjectDumper.dumpObject(object, DumpMode.DEEP));
    }

    //-------------------------------------------------

    /* NB: We prefer String over URI here as it's more general, and takes advantage of
     * stringification within the JSTL.
     */
    public static String extractContentPackagePath(final String uriString) {
        return QtiContentPackageExtractor.PACKAGE_URI_SCHEME.uriToDecodedPath(uriString);
    }

    public static String formatAssessmentType(final AssessmentObjectType assessmentType) {
        switch (assessmentType) {
            case ASSESSMENT_ITEM: return "Item";
            case ASSESSMENT_TEST: return "Test";
            default: return "";
        }
    }

    public static String formatAssessmentFileName(final AssessmentPackage assessmentPackage) {
       return escapeXml(assessmentPackage.getFileName());
    }

    public static String formatLtiContextTitle(final LtiContext ltiContext) {
        final String title = ltiContext.getContextTitle();
        return escapeXml(title!=null ? title : "This Course");
    }

    public static String formatLtiResourceTitle(final LtiResource ltiResource) {
        final String title = ltiResource.getResourceLinkTitle();
        return escapeXml(title!=null ? title : "This Launch");
    }

    private static String escapeXml(final String rawString) {
        return StringEscapeUtils.escapeXml(rawString);
    }
}
