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
package uk.ac.ed.ph.qtiworks.services;

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.services.domain.OutputStreamer;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.hash.Hashing;

/**
 * Dumping ground for utilities that currently don't belong anywhere else.
 *
 * @author David McKain
 */
public final class ServiceUtilities {

    private static final Logger logger = LoggerFactory.getLogger(ServiceUtilities.class);

    public static final String ellipses = "...";

    public static String trimString(final String string, final int maxLength) {
        Assert.notNull(string, "string");
        if (maxLength < 0) {
            throw new IllegalArgumentException("maxLength must not be negative");
        }
        final String trimmed = string.trim();
        return trimmed.length() <= maxLength ? trimmed : trimmed.substring(0, maxLength);
    }

    public static String safelyTrimString(final String string, final int maxLength) {
        return safelyTrimString(string, null, maxLength);
    }

    public static String safelyTrimString(final String string, final String resultIfNull, final int maxLength) {
        return string!=null ? trimString(string, maxLength) : resultIfNull;
    }

    /**
     * Trims a "sentence" down to fit the given size, using ellipses at the end if required.
     * <p>
     * This is handy for "soft" handling of text that is intended to be restricted to a certain
     * size, without rejecting user input.
     */
    public static String trimSentence(final String sentence, final int maxLength) {
        Assert.notNull(sentence, "sentence");
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

    //-----------------------------------------------------
    // Random data generation

    private static final SecureRandom secureRandom = new SecureRandom();

    /** "safe" characters used to encode random tokens */
    private static final char[] randomChars = "0123456789ABCDEFGHIJKLMNOPSQRTSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public static String createRandomAlphanumericToken(final int length) {
        final char[] tokenBuilder = new char[length];
        synchronized (secureRandom) {
            for (int i=0; i<length; i++) {
                tokenBuilder[i] = randomChars[secureRandom.nextInt(randomChars.length)];
            }
        }
        return new String(tokenBuilder);
    }

    //-----------------------------------------------------

    public static InputStream ensureInputSream(final MultipartFile multipartFile) {
        try {
            return multipartFile.getInputStream();
        }
        catch (final IOException e) {
            throw new QtiWorksRuntimeException("Unexpected Exception getting InputStream from MultipartFile", e);
        }
    }

    public static void ensureClose(final Closeable... streams) {
        IOException firstException = null;
        for (final Closeable stream : streams) {
            if (stream!=null) {
                try {
                    stream.close();
                }
                catch (final IOException e) {
                    firstException = e;
                }
            }
        }
        if (firstException!=null) {
            throw new QtiWorksRuntimeException("Unexpected failure to close stream", firstException);
        }
    }

    /**
     * Simple method to ensure that a given File exists. If the File
     * does not exist then it is created, along with all required parent
     * directories.
     *
     * @throws QtiWorksRuntimeException if creation could not succeed for some reason.
     */
    public static File ensureFileCreated(final File file) {
        /* Make sure parent exists */
        final File parentDirectory = file.getParentFile();
        if (parentDirectory!=null) {
            ensureDirectoryCreated(parentDirectory);
        }
        /* Now create file */
        if (!file.isFile()) {
            try {
                if (!file.createNewFile()) {
                    throw new QtiWorksRuntimeException("Could not create file " + file);
                }
            }
            catch (final IOException e) {
                throw new QtiWorksRuntimeException("Unexpected Exception trying to create file " + file, e);
            }
        }
        return file;
    }

    /**
     * Simple method to ensure that a given directory exists. If the directory
     * does not exist then it is created, along with all required parents.
     *
     * @throws QtiWorksRuntimeException if creation could not succeed for some reason.
     */
    public static File ensureDirectoryCreated(final File directory) {
        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) {
                throw new QtiWorksRuntimeException("Could not create directory " + directory);
            }
        }
        return directory;
    }

    /**
     * Recursively deletes the contents of the given directory (and
     * possibly the directory itself).
     * <p>
     * An error is logged if this fails to complete successfully, rather than
     * throwing an Exception. Note that failure will not be atomic, so some directories
     * may be left over.
     *
     * @param root directory (or file) whose contents will be deleted
     * @param deleteRoot true deletes root directory, false deletes only
     *  its contents.
     */
    public static void recursivelyDelete(final File root, final boolean deleteRoot) {
        if (root.isDirectory()) {
            final File [] contents = root.listFiles();
            for (final File child : contents) {
                recursivelyDelete(child, true);
            }
        }
        if (deleteRoot) {
            if (!root.delete()) {
                logger.error("Could not delete directory " + root);
            }
        }
    }

    /**
     * Convenience version of {@link #recursivelyDelete(File, boolean)} that
     * deletes the given root directory as well.
     * <p>
     * An error is logged if this fails to complete successfully, rather than
     * throwing an Exception. Note that failure will not be atomic, so some directories
     * may be left over.
     */
    public static void recursivelyDelete(final File root) {
        recursivelyDelete(root, true);
    }

    //-----------------------------------------------------
    // File streaming

    public static void streamFile(final File file, final String contentType,
            final Date lastModifiedTime, final OutputStreamer outputStreamer)
            throws IOException {
        final long contentLength = file.length();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            outputStreamer.stream(contentType, contentLength, lastModifiedTime, fileInputStream);
        }
        finally {
            ensureClose(fileInputStream);
        }
    }
}
