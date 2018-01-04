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
package uk.ac.ed.ph.qtiworks.domain;

/**
 * Various constants for the domain layer.
 * <p>
 * Many of these are used to define constraints for persisted entities.
 *
 * @author David McKain
 */
public final class DomainConstants {

    /** One second (in milliseconds) */
    public static final long ONE_SECOND = 1000L;

    /** One minute (in milliseconds) */
    public static final long ONE_MINUTE = ONE_SECOND * 60;

    /** One hour (in milliseconds) */
    public static final long ONE_HOUR = ONE_MINUTE * 60;

    public static final int USER_LOGIN_NAME_MAX_LENGTH = 32;
    public static final int USER_EMAIL_ADDRESS_MAX_LENGTH = 128;
    public static final int USER_NAME_COMPONENT_MAX_LENGTH = 256;
    public static final int USER_PASSWORD_SALT_LENGTH = 16;
    public static final int SHA1_DIGEST_LENGTH = 40;

    public static final int ASSESSMENT_NAME_MAX_LENGTH = 64;
    public static final int ASSESSMENT_TITLE_MAX_LENGTH = 256;

    /** Length for the XSRF token appended to candidate session URLs */
    public static final int XSRF_TOKEN_LENGTH = 32;

    /** Maximum length for an LTI "token" (i.e. identifier, primary key) */
    public static final int LTI_TOKEN_MAX_LENGTH = 128;

    /** Maximum length for an LTI shared secret */
    public static final int LTI_SHARED_SECRET_MAX_LENGTH = 32;

    /**
     * Maximum length for an LTI user logical key.
     * (This has been chosen large enough according to the conventions used to generate these keys.)
     */
    public static final int LTI_USER_LOGICAL_KEY_MAX_LENGTH = LTI_TOKEN_MAX_LENGTH + 64;

    /**
     * How long (in milliseconds) to keep transient data before purging
     */
    public static final long TRANSIENT_DATA_LIFETIME = 24 * ONE_HOUR;

    /**
     * Maximum length for an OAuth nonce.
     */
    public static final int OAUTH_NONCE_MAX_LENGTH = 128;

    /**
     * Maximum permitted age (in milliseconds)
     * for an OAuth timestamp before the OAuth message is rejected
     */
    public static final long OAUTH_TIMESTAMP_MAX_AGE = 90 * ONE_MINUTE;

    /**
     * NB: Should be set to the maximum length of the permitted values of
     * the QTI <code>completionStatus</code> variable.
     */
    public static final int QTI_COMPLETION_STATUS_MAX_LENGTH = 13;

    /**
     * Default <tt>Content-Type</tt> to use in uploaded content if no
     * value has been specified.
     */
    public static String DEFAULT_CONTENT_TYPE = "application/octet-stream";
}
