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
package uk.ac.ed.ph.jqtiplus.node.test;

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;

/**
 * The ordering class specifies the rule used to arrange the child elements of a section following
 * selection. If no ordering rule is given we assume that the elements are to be ordered in the order
 * in which they are defined.
 *
 * @author Jiri Kajaba
 */
public final class Ordering extends AbstractNode {

    private static final long serialVersionUID = 8836454929046981760L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "ordering";

    /** Name of shuffle attribute in xml schema. */
    public static final String ATTR_SHUFFLE_NAME = "shuffle";

    public Ordering(final AssessmentSection parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new BooleanAttribute(this, ATTR_SHUFFLE_NAME, true));
    }

    @Override
    public AssessmentSection getParent() {
        return (AssessmentSection) super.getParent();
    }

    public boolean getShuffle() {
        return getAttributes().getBooleanAttribute(ATTR_SHUFFLE_NAME).getComputedNonNullValue();
    }

    public void setShuffle(final boolean shuffle) {
        getAttributes().getBooleanAttribute(ATTR_SHUFFLE_NAME).setValue(Boolean.valueOf(shuffle));
    }
}
