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
package uk.ac.ed.ph.qtiworks.services;

import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackageImportType;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.QtiContentPackageExtractor;
import uk.ac.ed.ph.jqtiplus.xmlutils.CustomUriScheme;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ChainedResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.FileSandboxResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.NetworkHttpResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;

import com.google.common.hash.Hashing;

/**
 * Dumping ground for utilities that currently don't belong anywhere else.
 *
 * @author David McKain
 */
public final class ServiceUtilities {

    /**
     * Creates a {@link ResourceLocator} for reading in the resources associated with the given
     * {@link AssessmentPackage}.
     * <p>
     * For an {@link AssessmentPackage} uploaded by a user, the resulting {@link ResourceLocator} will
     * be restricted to the package's sandbox, plus bundled parser resources and external HTTP locations.
     * <p>
     * For the bundled samples, this will look within the ClassPath at the approptiate locations only.
     */
    public static ResourceLocator createAssessmentResourceLocator(final AssessmentPackage assessmentPackage) {
        final ResourceLocator result;
        if (assessmentPackage.getImportType()==AssessmentPackageImportType.BUNDLED_SAMPLE) {
            /* This is a bundled sample, which lives in the ClassPath */
            result = new ChainedResourceLocator(
                    new ClassPathResourceLocator(), /* (to resolve things in the sample set) */
                    QtiXmlReader.JQTIPLUS_PARSER_RESOURCE_LOCATOR /* (to resolve internal HTTP resources, e.g. RP templates) */
                    /* (No resolution of external resources, since the samples are all self-contained) */
            );
        }
        else {
            /* Uploaded by user, so resource lives in a sandbox within the filesystem */
            final File sandboxDirectory = new File(assessmentPackage.getSandboxPath());
            final CustomUriScheme packageUriScheme = QtiContentPackageExtractor.PACKAGE_URI_SCHEME;
            result = new ChainedResourceLocator(
                    new FileSandboxResourceLocator(packageUriScheme, sandboxDirectory), /* (to resolve things in this package) */
                    QtiXmlReader.JQTIPLUS_PARSER_RESOURCE_LOCATOR, /* (to resolve internal HTTP resources, e.g. RP templates) */
                    new NetworkHttpResourceLocator() /* (to resolve external HTTP resources, e.g. RP templates, external items) */
            );
        }
        return result;
    }

    /**
     * Generates a URI for the {@link AssessmentObject} within the given {@link AssessmentPackage}.
     * <p>
     * For an {@link AssessmentPackage} uploaded by a user, this will be a "package" URI that can
     * access the package's sandbox directory.
     * <p>
     * For the bundled samples, this will be a ClassPath URI
     *
     * @param assessmentPackage
     * @return
     */
    public static URI createAssessmentObjectUri(final AssessmentPackage assessmentPackage) {
        URI result;
        if (assessmentPackage.getImportType()==AssessmentPackageImportType.BUNDLED_SAMPLE) {
            result = QtiSampleAssessment.toClassPathUri(assessmentPackage.getAssessmentHref());
        }
        else {
            final CustomUriScheme packageUriScheme = QtiContentPackageExtractor.PACKAGE_URI_SCHEME;
            result = packageUriScheme.pathToUri(assessmentPackage.getAssessmentHref().toString());
        }
        return result;

    }

    //-------------------------------------------------


    public static final String ellipses = "...";

    public static String trimString(final String string, final int maxLength) {
        Assert.ensureNotNull(string, "string");
        if (maxLength < 0) {
            throw new IllegalArgumentException("maxLength must not be negative");
        }
        final String trimmed = string.trim();
        return trimmed.length() <= maxLength ? trimmed : trimmed.substring(0, maxLength);
    }

    /**
     * Trims a "sentence" down to fit the given size, using ellipses at the end if required.
     * <p>
     * This is handy for "soft" handling of text that is intended to be restricted to a certain
     * size, without rejecting user input.
     */
    public static String trimSentence(final String sentence, final int maxLength) {
        Assert.ensureNotNull(sentence, "sentence");
        if (maxLength < ellipses.length()) {
            throw new IllegalArgumentException("maxLength is clearly too short for a sentence!");
        }
        final String trimmed = sentence.trim();
        if (trimmed.length() <= maxLength) {
            /* Already OK after basic trim */
            return trimmed;
        }
        /* Work out longest possible run of text in result, ensuring room for ellipses */
        final String longestPossibleText = trimmed.substring(0, maxLength - ellipses.length());
        if (trimmed.charAt(longestPossibleText.length())==' ') {
            /* Lucky edge case - space is the next character */
            return longestPossibleText + ellipses;
        }
        final int lastVisibleSpace = longestPossibleText.lastIndexOf(' ');
        final String actualText = lastVisibleSpace!=-1 ? sentence.substring(0, lastVisibleSpace) : longestPossibleText;
        return actualText + ellipses;
    }

    /**
     * Computes a hex-encoded SHA1 digest of the given password String
     */
    public static String computePasswordDigest(final String password) {
        return Hashing.sha1().hashString(password, Charset.forName("UTF-8")).toString();
    }
}
