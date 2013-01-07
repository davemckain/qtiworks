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
 * Various constants for the domain layer
 *
 * @author David McKain
 */
public final class DomainConstants {

    public static final int USER_LOGIN_NAME_MAX_LENGTH = 32;
    public static final int USER_EMAIL_ADDRESS_MAX_LENGTH = 128;
    public static final int USER_NAME_COMPONENT_MAX_LENGTH = 256;
    public static final int USER_PASSWORD_SALT_LENGTH = 16;
    public static final int SHA1_DIGEST_LENGTH = 40;
    public static final int FILE_CONTENT_TYPE_LENGTH = 64;

    public static final int ASSESSMENT_NAME_MAX_LENGTH = 64;
    public static final int ASSESSMENT_TITLE_MAX_LENGTH = 256;

    public static final int CANDIDATE_SESSION_TOKEN_LENGTH = 32;
    public static final int CANDIDATE_SESSION_EXIT_URL_LENGTH = 128;

    public static final int LTI_TOKEN_LENGTH = 32;

    /** FIXME: What limit should we use here? */
    public static final int QTI_IDENTIFIER_MAX_LENGTH = 64;

    /**
     * NB: Should be set to the maximum length of the permitted values of
     * the QTI <code>completionStatus</code> variable.
     */
    public static final int QTI_COMPLETION_STATUS_MAX_LENGTH = 13;

    //----------------------------------------------

    public static final String QTI_DEFAULT_OWNER_LOGIN_NAME = "qtiworks";
    public static final String QTI_DEFAULT_OWNER_FIRST_NAME = "QTI";
    public static final String QTI_DEFAULT_OWNER_LAST_NAME = "Works";

    public static final String QTI_SAMPLE_OWNER_LOGIN_NAME = "qtisamples";
    public static final String QTI_SAMPLE_OWNER_FIRST_NAME = "QTI";
    public static final String QTI_SAMPLE_OWNER_LAST_NAME = "Samples";

}
