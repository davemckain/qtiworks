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
package uk.ac.ed.ph.jqtiplus.node.content.xhtml.text;


import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.AbstractAtomicBlock;
import uk.ac.ed.ph.jqtiplus.node.content.basic.AtomicBlock;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.image.Img;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.object.Object;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.Big;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.Small;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.Sub;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.presentation.Sup;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;

/**
 * pre
 * 
 * @author Jonathon Hare
 */
public class Pre extends AbstractAtomicBlock implements AtomicBlock {

    private static final long serialVersionUID = 6314971744269416971L;

    /** Name of this class in xml schema. */
    public static String CLASS_TAG = "pre";

    /**
     * Constructs object.
     * 
     * @param parent parent of constructed object
     */
    public Pre(XmlNode parent) {
        super(parent);
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    @Override
    public void validate(ValidationContext context) {
        super.validate(context);

        //Although pre inherits from atomicBlock it must not contain, either directly 
        //or indirectly, any of the following objects: img, object, big, small, sub, sup.
        if (search(Img.class).size() > 0) {
            context.add(new ValidationError(this, "The " + CLASS_TAG + " class cannot contain " + Img.CLASS_TAG + " children"));
        }
        if (search(Object.class).size() > 0) {
            context.add(new ValidationError(this, "The " + CLASS_TAG + " class cannot contain " + Object.CLASS_TAG + " children"));
        }
        if (search(Big.class).size() > 0) {
            context.add(new ValidationError(this, "The " + CLASS_TAG + " class cannot contain " + Big.CLASS_TAG + " children"));
        }
        if (search(Small.class).size() > 0) {
            context.add(new ValidationError(this, "The " + CLASS_TAG + " class cannot contain " + Small.CLASS_TAG + " children"));
        }
        if (search(Sub.class).size() > 0) {
            context.add(new ValidationError(this, "The " + CLASS_TAG + " class cannot contain " + Sub.CLASS_TAG + " children"));
        }
        if (search(Sup.class).size() > 0) {
            context.add(new ValidationError(this, "The " + CLASS_TAG + " class cannot contain " + Sup.CLASS_TAG + " children"));
        }
    }
}
