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
package uk.ac.ed.ph.jqtiplus.node.item.interaction;

import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.control.AssessmentItemController;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
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
public class UploadInteraction extends BlockInteraction {

    private static final long serialVersionUID = -8426318923809371089L;

    /** Name of this class in xml schema. */
    public static String CLASS_TAG = "uploadInteraction";

    /** Name of type attribute in xml schema. */
    public static final String ATTR_TYPE_NAME = "type";

    /**
     * Constructs object.
     * 
     * @param parent parent of constructed object
     */
    public UploadInteraction(XmlNode parent) {
        super(parent);

        getAttributes().add(new StringAttribute(this, ATTR_TYPE_NAME, null, null, false));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    /**
     * Gets value of type attribute.
     * 
     * @return value of type attribute
     * @see #setType
     */
    public String getType() {
        return getAttributes().getStringAttribute(ATTR_TYPE_NAME).getValue();
    }

    /**
     * Sets new value of type attribute.
     * 
     * @param type new value of type attribute
     * @see #getType
     */
    public void setType(String type) {
        getAttributes().getStringAttribute(ATTR_TYPE_NAME).setValue(type);
    }

    @Override
    public void validate(ValidationContext context, ValidationResult result) {
        super.validate(context, result);

        if (getResponseIdentifier() != null) {
            final ResponseDeclaration declaration = getRootNode(AssessmentItem.class).getResponseDeclaration(getResponseIdentifier());
            if (declaration != null && declaration.getCardinality() != null && !declaration.getCardinality().isSingle()) {
                result.add(new ValidationError(this, "Response variable must have single cardinality"));
            }

            if (declaration != null && declaration.getBaseType() != null && !declaration.getBaseType().isFile()) {
                result.add(new ValidationError(this, "Response variable must have file base type"));
            }
        }
    }

    @Override
    public boolean validateResponse(AssessmentItemController itemController, Value responseValue) {
        /* We assume anything is valid here */
        return true;
    }
}
