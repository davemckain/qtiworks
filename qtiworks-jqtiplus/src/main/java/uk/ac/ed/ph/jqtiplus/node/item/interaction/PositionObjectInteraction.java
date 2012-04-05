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

import uk.ac.ed.ph.jqtiplus.attribute.value.CoordsAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.group.content.ObjectGroup;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.object.Object;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.PointValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * The position object interaction consists of a single image which must be positioned
 * on another graphic image (the stage) by the candidate. Like selectPointInteraction,
 * the associated response may have an areaMapping that scores the response on the basis
 * of comparing it against predefined areas but the delivery engine must not indicate
 * these areas of the stage. Only the actual position(s) selected by the candidate shall
 * be indicated.
 * The position object interaction must be bound to a response variable with a baseType
 * of point and single or multiple cardinality. The point records the coordinates, with
 * respect to the stage, of the center point of the image being positioned.
 * Attribute : centerPoint [0..2]: integer
 * The centerPoint attribute defines the point on the image being positioned that is to
 * be treated as the center as an offset from the top-left corner of the image in
 * horizontal, vertical order. By default this is the center of the image's bounding
 * rectangle.
 * The stage on which the image is to be positioned may be shared amongst several
 * position object interactions and is therefore defined in a class of its own:
 * positionObjectStage.
 * Attribute : maxChoices [1]: integer = 1
 * The maximum number of positions (on the stage) that the image can be placed. If
 * matchChoices is 0 there is no limit. If maxChoices is greater than 1 (or 0) then the
 * interaction must be bound to a response with multiple cardinality.
 * Attribute : minChoices [0..1]: integer
 * The minimum number of positions that the image must be placed to form a valid response
 * to the interaction. If specified, minChoices must be 1 or greater but must not
 * exceed the limit imposed by maxChoices.
 * Contains : object [1]
 * The image to be positioned on the stage by the candidate.
 * 
 * @author Jonathon Hare
 */
public class PositionObjectInteraction extends BlockInteraction {

    private static final long serialVersionUID = 6496712889271262175L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "positionObjectInteraction";

    /** Name of centerPoint attribute in xml schema. */
    public static String ATTR_CENTER_POINT_NAME = "centerPoint";

    /** Name of maxChoices attribute in xml schema. */
    public static String ATTR_MAX_CHOICES_NAME = "maxChoices";

    /** Default value of maxChoices attribute . */
    public static int ATTR_MAX_CHOICES_DEFAULT_VALUE = 1;

    /** Name of minChoices attribute in xml schema. */
    public static String ATTR_MIN_CHOICES_NAME = "minChoices";

    public PositionObjectInteraction(XmlNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new CoordsAttribute(this, ATTR_CENTER_POINT_NAME, null, null, false));
        getAttributes().add(new IntegerAttribute(this, ATTR_MAX_CHOICES_NAME, ATTR_MAX_CHOICES_DEFAULT_VALUE, ATTR_MAX_CHOICES_DEFAULT_VALUE, true));
        getAttributes().add(new IntegerAttribute(this, ATTR_MIN_CHOICES_NAME, null, null, false));

        getNodeGroups().add(new ObjectGroup(this, true));
    }

    /**
     * Gets value of centerPoint attribute.
     * 
     * @return value of centerPoint attribute
     */
    public List<Integer> getCenterPoint() {
        return getAttributes().getCoordsAttribute(ATTR_CENTER_POINT_NAME).getValue();
    }
    
    public void setCenterPoint(List<Integer> value) {
        getAttributes().getCoordsAttribute(ATTR_CENTER_POINT_NAME).setValue(value);
    }

    /**
     * Sets new value of maxChoices attribute.
     * 
     * @param maxChoices new value of maxChoices attribute
     * @see #getMaxChoices
     */
    public void setMaxChoices(Integer maxChoices) {
        getAttributes().getIntegerAttribute(ATTR_MAX_CHOICES_NAME).setValue(maxChoices);
    }

    /**
     * Gets value of maxChoices attribute.
     * 
     * @return value of maxChoices attribute
     * @see #setMaxChoices
     */
    public Integer getMaxChoices() {
        return getAttributes().getIntegerAttribute(ATTR_MAX_CHOICES_NAME).getComputedValue();
    }

    /**
     * Sets new value of minChoices attribute.
     * 
     * @param minChoices new value of minChoices attribute
     * @see #getMinChoices
     */
    public void setMinChoices(Integer minChoices) {
        getAttributes().getIntegerAttribute(ATTR_MIN_CHOICES_NAME).setValue(minChoices);
    }

    /**
     * Gets value of minChoices attribute.
     * 
     * @return value of minChoices attribute
     * @see #setMinChoices
     */
    public Integer getMinChoices() {
        return getAttributes().getIntegerAttribute(ATTR_MIN_CHOICES_NAME).getComputedValue();
    }

    /**
     * Gets object child.
     * 
     * @return object child
     * @see #setObject
     */
    public Object getObject() {
        return getNodeGroups().getObjectGroup().getObject();
    }

    /**
     * Sets new object child.
     * 
     * @param object new object child
     * @see #getObject
     */
    public void setObject(Object object) {
        getNodeGroups().getObjectGroup().setObject(object);
    }

    @Override
    public void validate(ValidationContext context) {
        super.validate(context);

        if (getMinChoices() != null && getMinChoices() > getMaxChoices()) {
            context.add(new ValidationError(this, "Minimum number of choices can't be bigger than maximum number"));
        }

        if (getMinChoices() != null && getMinChoices() <= 1) {
            context.add(new ValidationError(this, "Minimum number of choices can't be less than 1"));
        }

        if (getResponseIdentifier() != null) {
            final ResponseDeclaration declaration = getResponseDeclaration();
            if (declaration != null && declaration.getBaseType() != null && !declaration.getBaseType().isPoint()) {
                context.add(new ValidationError(this, "Response variable must have point base type"));
            }

            if (declaration != null && getMaxChoices() == 1 &&
                    declaration.getCardinality() != null && !declaration.getCardinality().isSingle() &&
                    !declaration.getCardinality().isMultiple()) {
                context.add(new ValidationError(this, "Response variable must have single or multiple cardinality"));
            }

            if (declaration != null && getMaxChoices() != 1 && declaration.getCardinality() != null && !declaration.getCardinality().isMultiple()) {
                context.add(new ValidationError(this, "Response variable must have multiple cardinality"));
            }
        }
    }


    @Override
    public boolean validateResponse(ItemSessionController itemController, Value responseValue) {
        /* Extract response values */
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
        final Integer minChoices = getMinChoices();
        if (minChoices != null && responsePoints.size() < minChoices.intValue()) {
            return false;
        }
        final int maxChoices = getMaxChoices().intValue();
        if (maxChoices != 0 && responsePoints.size() > maxChoices) {
            return false;
        }

        return true;
    }

}
