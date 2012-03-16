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
package uk.ac.ed.ph.qtiworks.web.domain;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;

import java.io.Serializable;

/**
 * Encapsulates a {@link AssessmentObject} that has been uploaded into the engine.
 * 
 * TODO: This will eventually become a persisted Object so needs to stick to convention.
 *
 * @author David McKain
 */
public class AssessmentUpload implements Serializable {
    
    private static final long serialVersionUID = -8906026282623891941L;
    
    public static enum UploadType {
        STANDALONE,
        CONTENT_PACKAGE,
        ;
    }
    
    private final AssessmentPackage assessmentPackage;
    private final UploadType uploadType;
    private final AbstractValidationResult validationResult;

    public AssessmentUpload(AssessmentPackage assessmentPackage, UploadType uploadType, AbstractValidationResult validationResult) {
        this.assessmentPackage = assessmentPackage;
        this.uploadType = uploadType;
        this.validationResult = validationResult;
    }
    
    @ObjectDumperOptions(DumpMode.DEEP)
    public AssessmentPackage getAssessmentPackage() {
        return assessmentPackage;
    }
    
    public UploadType getUploadType() {
        return uploadType;
    }

    @ObjectDumperOptions(DumpMode.DEEP)
    public AbstractValidationResult getValidationResult() {
        return validationResult;
    }


    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }

}
