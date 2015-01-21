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
package uk.ac.ed.ph.jqtiplus.node.item.response.declaration;

import uk.ac.ed.ph.jqtiplus.group.item.CorrectResponseGroup;
import uk.ac.ed.ph.jqtiplus.group.item.response.declaration.AreaMappingGroup;
import uk.ac.ed.ph.jqtiplus.group.item.response.declaration.MappingGroup;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.item.CorrectResponse;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;

/**
 * Response variables are declared by response declarations and bound to interactions in the itemBody.
 *
 * @author Jonathon Hare
 */
public final class ResponseDeclaration extends VariableDeclaration {

    private static final long serialVersionUID = 1574002038906870724L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "responseDeclaration";

    public ResponseDeclaration(final AssessmentObject parent) {
        super(parent, QTI_CLASS_NAME);

        getNodeGroups().add(new CorrectResponseGroup(this));
        getNodeGroups().add(new MappingGroup(this));
        getNodeGroups().add(new AreaMappingGroup(this));
    }

    @Override
    public VariableType getVariableType() {
        return VariableType.RESPONSE;
    }

    public CorrectResponse getCorrectResponse() {
        return getNodeGroups().getCorrectResponseGroup().getCorrectResponse();
    }

    public void setCorrectResponse(final CorrectResponse correctResponse) {
        getNodeGroups().getCorrectResponseGroup().setCorrectResponse(correctResponse);
    }


    public Mapping getMapping() {
        return getNodeGroups().getMappingGroup().getMapping();
    }

    public void setMapping(final Mapping mapping) {
        getNodeGroups().getMappingGroup().setMapping(mapping);
    }


    public AreaMapping getAreaMapping() {
        return getNodeGroups().getAreaMappingGroup().getAreaMapping();
    }

    public void setAreaMapping(final AreaMapping areaMapping) {
        getNodeGroups().getAreaMappingGroup().setAreaMapping(areaMapping);
    }


    @Override
    public void validateThis(final ValidationContext context) {
        super.validateThis(context);
        if (getAreaMapping() != null && !hasBaseType(BaseType.POINT)) {
            context.fireValidationError(this, "Base type must be point when using areaMapping.");
        }
    }
}