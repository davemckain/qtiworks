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
package uk.ac.ed.ph.jqtiplus.node.result;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.group.item.CorrectResponseGroup;
import uk.ac.ed.ph.jqtiplus.group.result.CandidateResponseGroup;
import uk.ac.ed.ph.jqtiplus.node.item.CorrectResponse;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.FieldValue;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Response variables are declared by response declarations.
 *
 * @author Jonathon Hare
 */
public final class ResponseVariable extends ItemVariable implements ResultNode {

    private static final long serialVersionUID = 6478318320056351297L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "responseVariable";

    /** Name of choiceSequence attribute in xml schema. */
    public static final String ATTR_CHOICE_SEQUENCE_NAME = "choiceSequence";

    public ResponseVariable(final AbstractResult parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IdentifierMultipleAttribute(this, ATTR_CHOICE_SEQUENCE_NAME, false));

        getNodeGroups().add(new CorrectResponseGroup(this));
        getNodeGroups().add(new CandidateResponseGroup(this));
    }

    public ResponseVariable(final AbstractResult parent, final ResponseDeclaration declaration, final Value value, final List<Identifier> shuffledInteractionChoiceIdentifiers) {
        this(parent);

        setIdentifier(declaration.getIdentifier());
        setBaseType(declaration.getBaseType());
        setCardinality(declaration.getCardinality());
        setCorrectResponse(declaration.getCorrectResponse());

        if (shuffledInteractionChoiceIdentifiers != null) {
            List<Identifier> choiceSequence = getChoiceSequence();
            if (choiceSequence==null) {
                choiceSequence = new ArrayList<Identifier>();
                setChoiceSequence(choiceSequence);
            }
            else {
                choiceSequence.clear();
            }
            choiceSequence.addAll(shuffledInteractionChoiceIdentifiers);
        }

        final CandidateResponse response = new CandidateResponse(this);
        response.getFieldValues().addAll(FieldValue.computeValues(response, value));
        setCandidateResponse(response);
    }

    @Override
    public VariableType getVariableType() {
        return VariableType.RESPONSE;
    }

    public List<Identifier> getChoiceSequence() {
        return getAttributes().getIdentifierMultipleAttribute(ATTR_CHOICE_SEQUENCE_NAME).getComputedValue();
    }

    public void setChoiceSequence(final List<Identifier> value) {
        getAttributes().getIdentifierMultipleAttribute(ATTR_CHOICE_SEQUENCE_NAME).setValue(value);
    }


    public CorrectResponse getCorrectResponse() {
        return getNodeGroups().getCorrectResponseGroup().getCorrectResponse();
    }

    public void setCorrectResponse(final CorrectResponse correctResponse) {
        getNodeGroups().getCorrectResponseGroup().setCorrectResponse(correctResponse);
    }


    public CandidateResponse getCandidateResponse() {
        return getNodeGroups().getCandidateResponseGroup().getCandidateResponse();
    }

    public void setCandidateResponse(final CandidateResponse candidateResponse) {
        getNodeGroups().getCandidateResponseGroup().setCandidateResponse(candidateResponse);
    }

    @Override
    public Value getComputedValue() {
        final CandidateResponse candidateResponse = getCandidateResponse();
        if (candidateResponse==null) {
            return NullValue.INSTANCE;
        }
        return FieldValue.computeValue(getCardinality(), candidateResponse.getFieldValues());
    }
}
