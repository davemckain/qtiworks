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

import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.graphic.HotspotChoiceContainer;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.InteractionBindingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.PointValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Like hotspotInteraction, a select point interaction is a graphic
 * interaction. The candidate's task is to select one or more points.
 * The associated response may have an areaMapping that scores the
 * response on the basis of comparing it against predefined areas but
 * the delivery engine must not indicate these areas of the image. Only
 * the actual point(s) selected by the candidate shall be indicated.
 * The select point interaction must be bound to a response variable
 * with a baseType of point and single or multiple cardinality.
 * Attribute : maxChoices [1]: integer = 1
 * This attribute is interpreted as the maximum number of points that
 * the candidate is allowed to select. If maxChoices is 0 there is no
 * restriction. If maxChoices is greater than 1 (or 0) then the
 * interaction must be bound to a response with multiple cardinality.
 * Attribute : minChoices [0..1]: integer = 0
 * The minimum number of points that the candidate is required to
 * select to form a valid response. If minChoices is 0 then the
 * candidate is not required to select any points. minChoices must
 * be less than or equal to the limit imposed by maxChoices.
 *
 * @author Jonathon Hare
 */
public final class SelectPointInteraction extends GraphicInteraction implements HotspotChoiceContainer {

    private static final long serialVersionUID = -8629020921488399059L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "selectPointInteraction";

    /** Name of maxChoices attribute in xml schema. */
    public static final String ATTR_MAX_CHOICES_NAME = "maxChoices";

    /** Default value of maxChoices attribute. */
    public static final int ATTR_MAX_CHOICES_DEFAULT_VALUE = 0;

    /** Name of minChoices attribute in xml schema. */
    public static final String ATTR_MIN_CHOICES_NAME = "minChoices";

    /** Default value of minChoices attribute. */
    public static final int ATTR_MIN_CHOICES_DEFAULT_VALUE = 0;

    public SelectPointInteraction(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IntegerAttribute(this, ATTR_MAX_CHOICES_NAME, ATTR_MAX_CHOICES_DEFAULT_VALUE, true));
        getAttributes().add(new IntegerAttribute(this, ATTR_MIN_CHOICES_NAME, ATTR_MIN_CHOICES_DEFAULT_VALUE, false));
    }

    public int getMaxChoices() {
        return getAttributes().getIntegerAttribute(ATTR_MAX_CHOICES_NAME).getComputedNonNullValue();
    }

    public void setMaxChoices(final int maxChoices) {
        getAttributes().getIntegerAttribute(ATTR_MAX_CHOICES_NAME).setValue(Integer.valueOf(maxChoices));
    }


    public int getMinChoices() {
        return getAttributes().getIntegerAttribute(ATTR_MIN_CHOICES_NAME).getComputedNonNullValue();
    }

    public void setMinChoices(final Integer minChoices) {
        getAttributes().getIntegerAttribute(ATTR_MIN_CHOICES_NAME).setValue(minChoices);
    }


    @Override
    public void validateThis(final ValidationContext context, final ResponseDeclaration responseDeclaration) {
        final int maxChoices = getMaxChoices();
        final int minChoices = getMinChoices();

        if (maxChoices < minChoices) {
            context.fireValidationError(this, "Maximum number of choices must be greater or equal to minimum number of choices");
        }

        if (responseDeclaration!=null) {
            if (responseDeclaration.getBaseType() != null && !responseDeclaration.getBaseType().isPoint()) {
                context.fireValidationError(this, "Response variable must have point base type");
            }

            if (maxChoices == 1 &&
                    !responseDeclaration.getCardinality().isSingle() &&
                    !responseDeclaration.getCardinality().isMultiple()) {
                context.fireValidationError(this, "Response variable must have single or multiple cardinality");
            }

            if (maxChoices != 1 && !responseDeclaration.getCardinality().isMultiple()) {
                context.fireValidationError(this, "Response variable must have multiple cardinality");
            }
        }
    }

    @Override
    public boolean validateResponse(final InteractionBindingContext interactionBindingContext, final Value responseValue) {
        final List<PointValue> responsePoints = new ArrayList<PointValue>();
        if (responseValue.isNull()) {
            /* (Empty response) */
        }
        else if (responseValue.getCardinality().isList()) {
            /* (Container response) */
            for (final SingleValue pointValue : (ListValue) responseValue) {
                responsePoints.add((PointValue) pointValue);
            }
        }
        else {
            /* (Single response - this won't actually happen) */
            responsePoints.add((PointValue) responseValue);
        }

        /* Check minChoices/maxChoices */
        final int maxChoices = getMaxChoices();
        final int minChoices = getMinChoices();
        if (responsePoints.size() < minChoices) {
            return false;
        }
        if (maxChoices != 0 && responsePoints.size() > maxChoices) {
            return false;
        }

        return true;
    }
}