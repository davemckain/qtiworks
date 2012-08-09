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

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.DomainConstants;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Random;

import org.springframework.web.multipart.MultipartFile;

import com.google.common.hash.Hashing;

/**
 * Dumping ground for utilities that currently don't belong anywhere else.
 *
 * @author David McKain
 */
public final class ServiceUtilities {

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
    public static String computePasswordDigest(final String salt, final String password) {
        return computeSha1Digest(salt + password);
    }

    /**
     * Computes a hex-encoded SHA1 digest of the given String
     */
    public static String computeSha1Digest(final String string) {
        return Hashing.sha1().hashString(string, Charset.forName("UTF-8")).toString();
    }

    public static String createSalt() {
        final char[] saltBuilder = new char[DomainConstants.USER_PASSWORD_SALT_LENGTH];
        final Random random = new Random(System.currentTimeMillis());
        for (int i=0; i<DomainConstants.USER_PASSWORD_SALT_LENGTH; i++) {
            saltBuilder[i] = Character.valueOf((char) (0x20 + random.nextInt(0x5f)));
        }
        return new String(saltBuilder);
    }

    public static String createSessionHash() {
        final char[] hashBuilder = new char[DomainConstants.CANDIDATE_SESSION_HASH_LENGTH];
        final Random random = new Random(System.currentTimeMillis());
        for (int i=0; i<DomainConstants.CANDIDATE_SESSION_HASH_LENGTH; i++) {
            hashBuilder[i] = hexChars[random.nextInt(62)];
        }
        return new String(hashBuilder);
    }

    private static final char[] hexChars = "0123456789ABCDEFGHIJKLMNOPSQRTSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    //-----------------------------------------------------

    public static InputStream ensureInputSream(final MultipartFile multipartFile) {
        try {
            return multipartFile.getInputStream();
        }
        catch (final IOException e) {
            throw new QtiWorksRuntimeException("Unexpected Exception getting InputStream from MultipartFile", e);
        }
    }
}
