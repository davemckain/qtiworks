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
package uk.ac.ed.ph.jqtiplus.node.item.interaction.choice;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.util.List;

/**
 * Other interactions involve associating pairs of predefined choices.
 * These choices all have the following attribute in common:
 * Attribute : matchGroup [0..*]: identifier
 * A set of choices that this choice may be associated with, all others are excluded.
 * If no matchGroup is given, or if it is empty, then all other choices may be associated
 * with this one subject to their own matching constraints.
 *
 * @author Jonathon Hare
 */
public abstract class AssociableChoice extends Choice {

    private static final long serialVersionUID = 1563015843108965243L;

    /** Name of matchGroup attribute in xml schema. */
    public static final String ATTR_MATCH_GROUP_NAME = "matchGroup";

    public AssociableChoice(QtiNode parent, String qtiClassName) {
        super(parent, qtiClassName);
        getAttributes().add(new IdentifierMultipleAttribute(this, ATTR_MATCH_GROUP_NAME, null, false));
    }

    public List<Identifier> getMatchGroup() {
        return getAttributes().getIdentifierMultipleAttribute(ATTR_MATCH_GROUP_NAME).getValue();
    }

    public void setMatchGroup(List<Identifier> value) {
        getAttributes().getIdentifierMultipleAttribute(ATTR_MATCH_GROUP_NAME).setValue(value);
    }
}
