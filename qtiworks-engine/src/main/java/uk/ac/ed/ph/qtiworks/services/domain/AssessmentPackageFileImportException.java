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
package uk.ac.ed.ph.qtiworks.services.domain;

/**
 * Client exception thrown if submitted assessment package data does not conform
 * to what is expected.
 *
 * NOTE: Validation of the actual QTI data is not to be encapsulated here.
 *
 * @author David McKain
 */
public final class AssessmentPackageFileImportException extends Exception {

    private static final long serialVersionUID = -699513250898841731L;

    public static enum APFIFailureReason {
        /** We expect a standalone XML file or a ZIP stream containing at least one entry */
        NOT_XML_OR_ZIP,

        /** ZIP is not an IMS Content Package */
        NOT_CONTENT_PACKAGE,

        /** IMS manifest could not be parsed */
        BAD_IMS_MANIFEST,

        /** IMS Content Package did not contain a supported combination of {0} items and {1} tests */
        UNSUPPORTED_PACKAGE_CONTENTS,

        /** URI of a referenced File {0} resolved outside the package */
        HREF_OUTSIDE_PACKAGE,

        /** Reference File with href {0} is missing */
        FILE_MISSING,

        ;
    }

    private final EnumerableClientFailure<APFIFailureReason> failure;

    public AssessmentPackageFileImportException(final EnumerableClientFailure<APFIFailureReason> failure) {
        super(failure.toString());
        this.failure = failure;
    }

    public AssessmentPackageFileImportException(final EnumerableClientFailure<APFIFailureReason> failure, final Throwable cause) {
        super(failure.toString(), cause);
        this.failure = failure;
    }

    public AssessmentPackageFileImportException(final APFIFailureReason reason) {
        this(new EnumerableClientFailure<APFIFailureReason>(reason));
    }

    public AssessmentPackageFileImportException(final APFIFailureReason reason, final Throwable cause) {
        this(new EnumerableClientFailure<APFIFailureReason>(reason), cause);
    }

    public AssessmentPackageFileImportException(final APFIFailureReason reason, final Object... arguments) {
        this(new EnumerableClientFailure<APFIFailureReason>(reason, arguments));
    }


    public EnumerableClientFailure<APFIFailureReason> getFailure() {
        return failure;
    }
}
