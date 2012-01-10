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
package uk.ac.ed.ph.jqtiplus.node.content.xhtml.list;

import uk.ac.ed.ph.jqtiplus.exception2.QtiIllegalChildException;
import uk.ac.ed.ph.jqtiplus.node.content.BodyElement;

import java.util.HashMap;
import java.util.Map;

/**
 * dlElement types
 * 
 * @author Jonathon Hare
 */
public enum DlElementType {
    /**
     * dd
     */
    DD(Dd.CLASS_TAG) {

        @Override
        public DlElement create(BodyElement parent) {
            return new Dd(parent);
        }
    },
    /**
     * dt
     */
    DT(Dt.CLASS_TAG) {

        @Override
        public DlElement create(BodyElement parent) {
            return new Dt(parent);
        }
    };

    private static Map<String, DlElementType> dlElementTypes;

    static {
        dlElementTypes = new HashMap<String, DlElementType>();

        for (final DlElementType dlElementType : DlElementType.values()) {
            dlElementTypes.put(dlElementType.dlElementType, dlElementType);
        }
    }

    private String dlElementType;

    DlElementType(String inlineType) {
        this.dlElementType = inlineType;
    }

    /**
     * Gets CLASS_TAG of this dlElement type.
     * 
     * @return CLASS_TAG of this dlElement type
     */
    public String getClassTag() {
        return dlElementType;
    }

    /**
     * Creates dlElement element.
     * 
     * @param parent parent of created dlElement
     * @return created dlElement
     */
    public abstract DlElement create(BodyElement parent);

    @Override
    public String toString() {
        return dlElementType;
    }

    /**
     * Gets dlElement type for given CLASS_TAG.
     * 
     * @param classTag CLASS_TAG
     * @return dlElement type for given CLASS_TAG
     */
    public static DlElementType getType(String classTag) {
        return dlElementTypes.get(classTag);
    }

    /**
     * Creates dlElement element.
     * 
     * @param parent parent of created dlElement
     * @param classTag CLASS_TAG of created dlElement
     * @return created expression
     */
    public static DlElement getInstance(BodyElement parent, String classTag) {
        final DlElementType dlElementType = dlElementTypes.get(classTag);

        if (dlElementType == null) {
            throw new QtiIllegalChildException(parent, classTag);
        }

        return dlElementType.create(parent);
    }
}
