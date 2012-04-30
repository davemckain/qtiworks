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

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;

import java.io.Serializable;
import java.util.Set;

/**
 * Encapsulates a {@link AssessmentObject} that has been uploaded into the engine.
 *
 * TODO: This will eventually become a persisted Object so needs to stick to convention.
 *
 * @author David McKain
 */
@ToRefactor
public class AssessmentPackageV1 implements Serializable {

    private static final long serialVersionUID = -8906026282623891941L;

    public static enum AssessmentType {
        ITEM,
        TEST,
        ;
    }

    private AssessmentType assessmentType;
    private String sandboxPath;
    private String assessmentObjectHref;
    private Set<String> fileHrefs;

    public AssessmentPackageV1() {
    }


    public AssessmentType getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(final AssessmentType assessmentType) {
        this.assessmentType = assessmentType;
    }


    public String getSandboxPath() {
        return sandboxPath;
    }

    public void setSandboxPath(final String sandboxPath) {
        this.sandboxPath = sandboxPath;
    }


    public String getAssessmentObjectHref() {
        return assessmentObjectHref;
    }

    public void setAssessmentObjectHref(final String assessmentObjectHref) {
        this.assessmentObjectHref = assessmentObjectHref;
    }


    public Set<String> getFileHrefs() {
        return fileHrefs;
    }

    public void setFileHrefs(final Set<String> fileHrefs) {
        this.fileHrefs = fileHrefs;
    }


    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }

}
