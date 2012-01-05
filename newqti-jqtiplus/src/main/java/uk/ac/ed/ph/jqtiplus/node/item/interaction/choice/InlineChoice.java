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
package uk.ac.ed.ph.jqtiplus.node.item.interaction.choice;

import uk.ac.ed.ph.jqtiplus.group.content.TextOrVariableGroup;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.variable.PrintedVariable;
import uk.ac.ed.ph.jqtiplus.node.content.variable.TextOrVariable;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple run of text to be displayed to the user, may be subject to variable value substitution with printedVariable.
 * Spec is wrong I think - must contain textOrVariable children (see addendum).
 * 
 * @see PrintedVariable
 * @author Jonathon Hare
 */
public class InlineChoice extends Choice {

    private static final long serialVersionUID = 2530302190076210162L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "inlineChoice";

    /**
     * Construct new inlineChoice.
     * 
     * @param parent Parent node
     */
    public InlineChoice(XmlNode parent) {
        super(parent);

        getNodeGroups().add(new TextOrVariableGroup(this));
    }

    @Override
    public void validate(ValidationContext context, ValidationResult result) {
        super.validate(context, result);

        final List<Interaction> interactions = new ArrayList<Interaction>();
        search(getChildren(), Interaction.class, interactions);

        if (interactions.size() > 0) {
            result.add(new ValidationError(this, CLASS_TAG + " cannot contain nested interactions"));
        }
    }

    @Override
    public List<TextOrVariable> getChildren() {
        return getNodeGroups().getTextOrVariableGroup().getTextOrVariables();
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }
}
