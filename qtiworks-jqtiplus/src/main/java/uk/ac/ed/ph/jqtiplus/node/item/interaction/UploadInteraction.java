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
package uk.ac.ed.ph.jqtiplus.node.item.interaction;

import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.InteractionBindingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * The upload interaction allows the candidate to upload a
 * pre-prepared file representing their response. It must be
 * bound to a response variable with base-type file and single
 * cardinality.
 * Attribute : type [0..1]: mimeType
 * The expected mime-type of the uploaded file.
 *
 * @author Jonathon Hare
 */
public final class UploadInteraction extends BlockInteraction {

    private static final long serialVersionUID = -8426318923809371089L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "uploadInteraction";

    /** Name of type attribute in xml schema. */
    public static final String ATTR_TYPE_NAME = "type";

    public UploadInteraction(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new StringAttribute(this, ATTR_TYPE_NAME, false));
    }


    public String getType() {
        return getAttributes().getStringAttribute(ATTR_TYPE_NAME).getComputedValue();
    }

    public void setType(final String type) {
        getAttributes().getStringAttribute(ATTR_TYPE_NAME).setValue(type);
    }


    @Override
    protected void validateThis(final ValidationContext context, final ResponseDeclaration responseDeclaration) {
        if (responseDeclaration!=null) {
            if (!responseDeclaration.getCardinality().isSingle()) {
                context.fireValidationError(this, "Response variable must have single cardinality");
            }

            if (responseDeclaration.getBaseType() != null && !responseDeclaration.getBaseType().isFile()) {
                context.fireValidationError(this, "Response variable must have file base type");
            }
        }
    }

    @Override
    public boolean validateResponse(final InteractionBindingContext interactionBindingContext, final Value responseValue) {
        /* We assume anything is valid here */
        return true;
    }
}
