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
package uk.ac.ed.ph.jqtiplus;

import uk.ac.ed.ph.jqtiplus.types.Identifier;

/**
 * Some useful QTI-related constants
 *
 * @author David McKain
 */
public final class QtiConstants {

    public static final String QTI_20_NAMESPACE_URI = "http://www.imsglobal.org/xsd/imsqti_v2p0";
    public static final String QTI_21_NAMESPACE_URI = "http://www.imsglobal.org/xsd/imsqti_v2p1";
    public static final String QTI_RESULT_21_NAMESPACE_URI = "http://www.imsglobal.org/xsd/imsqti_result_v2p1";

    public static final String QTI_20_SCHEMA_LOCATION = "http://www.imsglobal.org/xsd/imsqti_v2p0.xsd";
    public static final String QTI_21_SCHEMA_LOCATION = "http://www.imsglobal.org/xsd/imsqti_v2p1.xsd";
    public static final String QTI_RESULT_21_SCHEMA_LOCATION = "http://www.imsglobal.org/xsd/imsqti_result_v2p1.xsd";

    public static final String CP_11_NAMESPACE_URI = "http://www.imsglobal.org/xsd/imscp_v1p1";
    public static final String CP_12_NAMESPACE_URI = "http://www.imsglobal.org/xsd/imscp_v1p2";

    public static final String MATHML_NAMESPACE_URI = "http://www.w3.org/1998/Math/MathML";
    public static final String MATHML_SCHEMA_LOCATION = "http://www.w3.org/Math/XMLSchema/mathml2/mathml2.xsd";

    /** Name of <code>duration</code> built-in variable. */
    public static final String VARIABLE_DURATION_NAME = "duration";

    /** Identifier of <code>duration</code> built-in variable. */
    public static final Identifier VARIABLE_DURATION_IDENTIFIER = Identifier.assumedLegal(VARIABLE_DURATION_NAME);

    /** Name of <code>numAttempts</code> built-in variable. */
    public static final String VARIABLE_NUMBER_OF_ATTEMPTS_NAME = "numAttempts";

    /** Identifier of <code>numAttempts</code> built-in variable. */
    public static final Identifier VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER = Identifier.assumedLegal(VARIABLE_NUMBER_OF_ATTEMPTS_NAME);

    /** Name of <code>completionStatus</code> built-in variable. */
    public static final String VARIABLE_COMPLETION_STATUS_NAME = "completionStatus";

    /** Identifier of <code>completionStatus</code> built-in variable. */
    public static final Identifier VARIABLE_COMPLETION_STATUS_IDENTIFIER = Identifier.assumedLegal(VARIABLE_COMPLETION_STATUS_NAME);

    /** Value of completion status built-in variable. */
    public static final String COMPLETION_STATUS_NOT_ATTEMPTED = "not_attempted";

    /** Value of completion status built-in variable. */
    public static final String COMPLETION_STATUS_UNKNOWN = "unknown";

    /** Value of completion status built-in variable. */
    public static final String COMPLETION_STATUS_COMPLETED = "completed";

    /** Value of completion status built-in variable. */
    public static final String COMPLETION_STATUS_INCOMPLETE = "incomplete";

    /** Recommended maximum length for an {@link Identifier} */
    public static final int IDENTIFIER_MAX_LENGTH_RECOMMENDATION = 32;
}
