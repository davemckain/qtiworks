/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
*/

package uk.ac.ed.ph.jqtiplus.node.item.interaction;

import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.control.AssessmentItemController;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.graphic.HotspotChoiceGroup;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.XmlObject;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.graphic.HotspotChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.graphic.HotspotChoiceContainer;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 
 * A hotspot interaction is a graphical interaction with a corresponding 
 * set of choices that are defined as areas of the graphic image. The 
 * candidate's task is to select one or more of the areas (hotspots). 
 * The hotspot interaction should only be used when the spatial relationship 
 * of the choices with respect to each other (as represented by the graphic
 * image) is important to the needs of the item. Otherwise, choiceInteraction
 * should be used instead with separate material for each option.
 * 
 * The delivery engine must clearly indicate the selected area(s) of the 
 * image and may also indicate the unselected areas as well. Interactions 
 * with hidden hotspots are achieved with the selectPointInteraction.
 * 
 * The hotspot interaction must be bound to a response variable with a 
 * baseType of identifier and single or multiple cardinality.
 * 
 * Attribute : maxChoices [1]: integer = 1
 * The maximum number of choices that the candidate is allowed to select. 
 * If maxChoices is 0 there is no restriction. If maxChoices is greater 
 * than 1 (or 0) then the interaction must be bound to a response with 
 * multiple cardinality.
 * 
 * Attribute : minChoices [0..1]: integer = 0
 * The minimum number of choices that the candidate is required to select 
 * to form a valid response. If minChoices is 0 then the candidate is not 
 * required to select any choices. minChoices must be less than or equal 
 * to the limit imposed by maxChoices.
 * 
 * Contains : hotspotChoice [1..*] {ordered}
 * The hotspots that define the choices that can be selected by the candidate. 
 * If the delivery system does not support pointer-based selection then the 
 * order in which the choices are given must be the order in which they are 
 * offered to the candidate for selection. For example, the 'tab order' in 
 * simple keyboard navigation. If hotspots overlap then those listed first 
 * hide overlapping hotspots that appear later. The default hotspot, if 
 * defined, must appear last.
 *
 * @author Jonathon Hare
 */
public class HotspotInteraction extends GraphicInteraction implements HotspotChoiceContainer {
    private static final long serialVersionUID = 1L;

    /** Name of this class in xml schema. */
    public static String CLASS_TAG = "hotspotInteraction";
    
    /** Name of maxChoices attribute in xml schema. */
    public static String ATTR_MAX_CHOICES_NAME = "maxChoices";
    /** Default value of maxChoices attribute. */
    public static int ATTR_MAX_CHOICES_DEFAULT_VALUE = 1;
    
    /** Name of minChoices attribute in xml schema. */
    public static String ATTR_MIN_CHOICES_NAME = "minChoices";
    /** Default value of minChoices attribute. */
    public static int ATTR_MIN_CHOICES_DEFAULT_VALUE = 0;
    
    /**
     * Construct new interaction.
     *  
     * @param parent Parent node
     */
    public HotspotInteraction(XmlObject parent) {
        super(parent);

        getAttributes().add(new IntegerAttribute(this, ATTR_MAX_CHOICES_NAME, ATTR_MAX_CHOICES_DEFAULT_VALUE, ATTR_MAX_CHOICES_DEFAULT_VALUE, true));
        getAttributes().add(new IntegerAttribute(this, ATTR_MIN_CHOICES_NAME, ATTR_MIN_CHOICES_DEFAULT_VALUE, ATTR_MIN_CHOICES_DEFAULT_VALUE, false));
        
        getNodeGroups().add(new HotspotChoiceGroup(this, 1));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    /**
     * Gets an unmodifiable list of the child elements. Use the other
     * methods on GraphicOrderInteraction to add children to the correct group.
     */
    @Override
    public List<? extends XmlNode> getChildren() {
        List<XmlNode> children = new ArrayList<XmlNode>();
        children.addAll(super.getChildren());
        children.addAll(getNodeGroups().getHotspotChoiceGroup().getHotspotChoices());
        
        return Collections.unmodifiableList(children);
    }

    /**
     * Sets new value of maxChoices attribute.
     *
     * @param maxChoices new value of maxChoices attribute
     * @see #getMaxChoices
     */
    public void setMaxChoices(Integer maxChoices)
    {
        getAttributes().getIntegerAttribute(ATTR_MAX_CHOICES_NAME).setValue(maxChoices);
    }

    /**
     * Gets value of maxChoices attribute.
     *
     * @return value of maxChoices attribute
     * @see #setMaxChoices
     */
    public Integer getMaxChoices()
    {
        return getAttributes().getIntegerAttribute(ATTR_MAX_CHOICES_NAME).getValue();
    }

    /**
     * Sets new value of minChoices attribute.
     *
     * @param minChoices new value of minChoices attribute
     * @see #getMinChoices
     */
    public void setMinChoices(Integer minChoices)
    {
        getAttributes().getIntegerAttribute(ATTR_MIN_CHOICES_NAME).setValue(minChoices);
    }

    /**
     * Gets value of minChoices attribute.
     *
     * @return value of minChoices attribute
     * @see #setMinChoices
     */
    public Integer getMinChoices()
    {
        return getAttributes().getIntegerAttribute(ATTR_MIN_CHOICES_NAME).getValue();
    }
    
    /**
     * Gets hotspotChoice children.
     *
     * @return hotspotChoice children
     */
    public List<HotspotChoice> getHotspotChoices()
    {
        return getNodeGroups().getHotspotChoiceGroup().getHotspotChoices();
    }

    /**
     * Gets hotspotChoice child with given identifier or null.
     *
     * @param identifier given identifier
     * @return hotspotChoice with given identifier or null
     */
    public HotspotChoice getHotspotChoice(String identifier)
    {
        for (HotspotChoice choice : getHotspotChoices())
            if (choice.getIdentifier() != null && choice.getIdentifier().equals(identifier))
                return choice;

        return null;
    }
    
    @Override
    public void validate(ValidationContext context, ValidationResult result) {
        super.validate(context, result);
        
        if (getMaxChoices() < getMinChoices())
            result.add(new ValidationError(this, "Maximum number of choices must be greater or equal to minimum number of choices"));
        
        if (getResponseIdentifier() != null)
        {
            ResponseDeclaration declaration = getResponseDeclaration();
            if (declaration != null && declaration.getBaseType() != null && !declaration.getBaseType().isIdentifier())
                result.add(new ValidationError(this, "Response variable must have identifier base type"));
            
            if (declaration != null && getMaxChoices() == 1 && 
                    declaration.getCardinality() != null && (
                    !declaration.getCardinality().isSingle() &&
                    !declaration.getCardinality().isMultiple()))
                result.add(new ValidationError(this, "Response variable must have single or multiple cardinality"));
            
            if (declaration != null && getMaxChoices() != 1 && declaration.getCardinality() != null && !declaration.getCardinality().isMultiple())
                result.add(new ValidationError(this, "Response variable must have multiple cardinality"));
        }
    }
    
    
    @Override
    public boolean validateResponse(AssessmentItemController itemController, Value responseValue) {
        /* Extract response values */
        Set<Identifier> responseChoiceIdentifiers = new HashSet<Identifier>();
        if (responseValue.isNull()) {
            /* (Empty response) */
        }
        else if (responseValue.getCardinality().isList()) {
            /* (Container response) */
            for (SingleValue hotspotChoiceIdentifier : (ListValue) responseValue) {
                responseChoiceIdentifiers.add(((IdentifierValue) hotspotChoiceIdentifier).identifierValue());
            }
        }
        else {
            /* (Single response) */
            responseChoiceIdentifiers.add(((IdentifierValue) responseValue).identifierValue());
        }
        
        /* Validate min/max */
        int maxChoices = getMaxChoices().intValue();
        int minChoices = getMinChoices().intValue();
        if (responseChoiceIdentifiers.size() < minChoices) {
            return false;
        }
        if (maxChoices !=0 && responseChoiceIdentifiers.size() > maxChoices) {
            return false;
        }
        
        /* Check that each identifier is valid */
        Set<Identifier> choiceIdentifiers = new HashSet<Identifier>();
        for (HotspotChoice choice : getHotspotChoices()) {
            choiceIdentifiers.add(choice.getIdentifier());
        }
        for (Identifier choiceIdentifier : responseChoiceIdentifiers) {
            if (!choiceIdentifiers.contains(choiceIdentifier)) {
                return false;
            }
        }
        
        return true;
    }
}