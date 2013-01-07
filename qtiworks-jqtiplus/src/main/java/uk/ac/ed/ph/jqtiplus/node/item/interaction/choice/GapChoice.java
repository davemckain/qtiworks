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

import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;

/**
 * The choices that are used to fill the gaps in a gapMatchInteraction
 * are either simple runs of text or single image objects, both derived
 * from gapChoice.
 * Attribute : matchMax [1]: integer
 * The maximum number of choices this choice may be associated with. If
 * matchMax is 0 there is no restriction.
 * Attribute : matchMin [0..1]: integer = 0
 * The minimum number of gaps this choice must be associated with to form
 * a valid response. If matchMin is 0 then the candidate is not required
 * to associate this choice with any gaps at all. matchMin must be less
 * than or equal to the limit imposed by matchMax.
 *
 * @author Jonathon Hare
 */
public abstract class GapChoice extends AssociableChoice {

    private static final long serialVersionUID = -8337963329636378276L;

    /** Display name of this class. */
    public static final String DISPLAY_NAME = "gapChoice";

    /** Name of matchMax attribute in xml schema. */
    public static final String ATTR_MATCH_MAX_NAME = "matchMax";

    /** Name of matchMin attribute in xml schema. */
    public static final String ATTR_MATCH_MIN_NAME = "matchMin";

    /** Default value of matchMin attribute. */
    public static final int ATTR_MATCH_MIN_DEFAULT_VALUE = 0;

    public GapChoice(final QtiNode parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getAttributes().add(new IntegerAttribute(this, ATTR_MATCH_MAX_NAME, true));
        getAttributes().add(new IntegerAttribute(this, ATTR_MATCH_MIN_NAME, ATTR_MATCH_MIN_DEFAULT_VALUE, false));
    }


    public int getMatchMax() {
        return getAttributes().getIntegerAttribute(ATTR_MATCH_MAX_NAME).getComputedNonNullValue();
    }

    public void setMatchMax(final int matchMax) {
        getAttributes().getIntegerAttribute(ATTR_MATCH_MAX_NAME).setValue(Integer.valueOf(matchMax));
    }


    public int getMatchMin() {
        return getAttributes().getIntegerAttribute(ATTR_MATCH_MIN_NAME).getComputedNonNullValue();
    }

    public void setMatchMin(final Integer matchMin) {
        getAttributes().getIntegerAttribute(ATTR_MATCH_MIN_NAME).setValue(matchMin);
    }
}
