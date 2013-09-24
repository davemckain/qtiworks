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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.node.accessibility.related;

import uk.ac.ed.ph.jqtiplus.group.accessibility.AccessibilityNode;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.SignFileGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;

/**
 * Container for the instructions for the provision of signing.
 * ASL and Signed English are the supported modes for signing.
 *
 * @author Zack Pierce
 */
public class Signing extends AbstractNode implements AccessibilityNode {

    private static final long serialVersionUID = -8663009705465444076L;

    public static final String QTI_CLASS_NAME = "signing";

    public static final String ELEM_SIGN_FILE_ASL = "signFileASL";

    public static final String ELEM_SIGN_FILE_SIGNED_ENGLISH = "signFileSignedEnglish";

    public Signing(final RelatedElementInfo parent) {
        super(parent, QTI_CLASS_NAME);
        getNodeGroups().add(new SignFileGroup(this, ELEM_SIGN_FILE_ASL));
        getNodeGroups().add(new SignFileGroup(this, ELEM_SIGN_FILE_SIGNED_ENGLISH));
    }

    public SignFile getSignFileASL() {
        return getNodeGroups().getSignFileGroup(ELEM_SIGN_FILE_ASL).getSignFile();
    }

    public void setSignFileASL(final SignFile signFile) {
        getNodeGroups().getSignFileGroup(ELEM_SIGN_FILE_ASL).setSignFile(signFile);
    }

    public SignFile getSignFileSignedEnglish() {
        return getNodeGroups().getSignFileGroup(ELEM_SIGN_FILE_SIGNED_ENGLISH).getSignFile();
    }

    public void setSignFileSignedEnglish(final SignFile signFile) {
        getNodeGroups().getSignFileGroup(ELEM_SIGN_FILE_SIGNED_ENGLISH).setSignFile(signFile);
    }

}
