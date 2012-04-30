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
package uk.ac.ed.ph.qtiworks;

import uk.ac.ed.ph.qtiworks.services.AssessmentPackageImportException;

import uk.ac.ed.ph.jqtiplus.xperimental.ToRemove;

/**
 * This was used in the first sketch of assessment uploading. It should no longer be used
 * in its current form
 *
 * @see AssessmentPackageImportException
 *
 * @author David McKain
 */
@ToRemove
public class UploadException extends Exception {

    private static final long serialVersionUID = -699513250898841731L;

    public static enum UploadFailureReason {
        NOT_XML_OR_ZIP,
        BAD_ZIP,
        NOT_CONTENT_PACKAGE,
        BAD_IMS_MANIFEST,
        UNSUPPORTED_PACKAGE_CONTENTS,
        ;
    }

    private final UploadFailureReason reason;

    public UploadException(final UploadFailureReason reason) {
        super("Unsupported upload " + reason);
        this.reason = reason;
    }

    public UploadException(final UploadFailureReason reason, final Throwable cause) {
        super("Unsupported upload " + reason, cause);
        this.reason = reason;
    }

    public UploadFailureReason getReason() {
        return reason;
    }

}
