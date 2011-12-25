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

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.control.AssessmentItemController;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.group.content.BlockStaticGroup;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.choice.GapChoiceGroup;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.BlockStatic;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.GapChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.GapChoiceContainer;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.content.Gap;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.value.DirectedPairValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 
 * A gap match interaction is a blockInteraction that contains a number 
 * gaps that the candidate can fill from an associated set of choices. 
 * The candidate must be able to review the content with the gaps 
 * filled in context, as indicated by their choices.
 * 
 * The gapMatchInteraction must be bound to a response variable with 
 * base-type directedPair and either single or multiple cardinality, 
 * depending on the number of gaps. The choices represent the source 
 * of the pairing and gaps the targets. Each gap can have at most one 
 * choice associated with it. The maximum occurrence of the choices is 
 * controlled by the matchMax attribute of gapChoice.
 * 
 * Attribute : shuffle [1]: boolean = false
 * If the shuffle attribute is true then the delivery engine must 
 * randomize the order in which the choices are presented (not the gaps),
 * subject to the fixed attribute of the choices themselves.
 * 
 * Contains : gapChoice [1..*]
 * An ordered list of choices for filling the gaps. There may be fewer 
 * choices than gaps if required.
 * 
 * Contains : blockStatic [1..*]
 * The content of the interaction is simply a piece of content that 
 * contains the gaps. If the block contains more than one gap then the 
 * interaction must be bound to a response with multiple cardinality.
 * 
 * @author Jonathon Hare
 *
 */
public class GapMatchInteraction extends BlockInteraction implements GapChoiceContainer, Shuffleable {
    private static final long serialVersionUID = 1L;

    /** Name of this class in xml schema. */
    public static String CLASS_TAG = "gapMatchInteraction";
    
    /** Name of shuffle attribute in xml schema. */
    public static String ATTR_SHUFFLE_NAME = "shuffle";
    /** Default value of shuffle attribute. */
    public static boolean ATTR_SHUFFLE_DEFAULT_VALUE = false;
    
    /**
     * Construct new interaction.
     *  
     * @param parent Parent node
     */
    public GapMatchInteraction(XmlNode parent) {
        super(parent);

        getAttributes().add(new BooleanAttribute(this, ATTR_SHUFFLE_NAME, ATTR_SHUFFLE_DEFAULT_VALUE, ATTR_SHUFFLE_DEFAULT_VALUE, true));
        
        getNodeGroups().add(new GapChoiceGroup(this, 1));
        getNodeGroups().add(new BlockStaticGroup(this, 1));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    /**
     * Gets an unmodifiable list of the child elements. Use the other
     * methods on ChoiceInteraction to add children to the correct group.
     */
    @Override
    public List<? extends XmlNode> getChildren() {
        List<XmlNode> children = new ArrayList<XmlNode>();
        children.addAll(super.getChildren());
        children.addAll(getNodeGroups().getGapChoiceGroup().getGapChoices());
        children.addAll(getNodeGroups().getBlockStaticGroup().getBlockStatics());
        
        return Collections.unmodifiableList(children);
    }

    /**
     * Sets new value of shuffle attribute.
     *
     * @param shuffle new value of shuffle attribute
     * @see #getShuffle
     */
    public void setShuffle(Boolean shuffle)
    {
        getAttributes().getBooleanAttribute(ATTR_SHUFFLE_NAME).setValue(shuffle);
    }

    /**
     * Gets value of shuffle attribute.
     *
     * @return value of shuffle attribute
     * @see #setShuffle
     */
    public Boolean getShuffle()
    {
        return getAttributes().getBooleanAttribute(ATTR_SHUFFLE_NAME).getValue();
    }
    
    /**
     * Gets gapChoice children.
     *
     * @return gapChoice children
     */
    public List<GapChoice> getGapChoices()
    {
        return getNodeGroups().getGapChoiceGroup().getGapChoices();
    }
    
    /**
     * Gets blockStatic children.
     *
     * @return blockStatic children
     */
    public List<BlockStatic> getBlockStatics()
    {
        return getNodeGroups().getBlockStaticGroup().getBlockStatics();
    }

    /**
     * Gets gapChoice child with given identifier or null.
     *
     * @param identifier given identifier
     * @return gapChoice with given identifier or null
     */
    public GapChoice getGapChoice(String identifier)
    {
        for (GapChoice choice : getGapChoices())
            if (choice.getIdentifier() != null && choice.getIdentifier().equals(identifier))
                return choice;

        return null;
    }
    
    @Override
    public void validate(ValidationContext context, ValidationResult result) {
        super.validate(context, result);
        
        if (getResponseIdentifier() != null)
        {
            ResponseDeclaration declaration = getResponseDeclaration();
            if (declaration != null && declaration.getBaseType() != null && !declaration.getBaseType().isDirectedPair())
                result.add(new ValidationError(this, "Response variable must have directed pair base type"));
            
            if (declaration != null && countGaps() == 1 && 
                    declaration.getCardinality() != null && (
                    !declaration.getCardinality().isSingle() &&
                    !declaration.getCardinality().isMultiple()))
                result.add(new ValidationError(this, "Response variable must have single or multiple cardinality"));
            
            if (declaration != null && countGaps() != 1 && declaration.getCardinality() != null && !declaration.getCardinality().isMultiple())
                result.add(new ValidationError(this, "Response variable must have multiple cardinality"));
        }
    }
    
    private int countGaps() {
        List<Gap> results = new ArrayList<Gap>();
        
        search(getNodeGroups().getBlockStaticGroup().getBlockStatics(), Gap.class, results);
        
        return results.size();
    }

    @Override
    public void initialize(AssessmentItemController itemController) {
        super.initialize(itemController);
        itemController.shuffleInteractionChoiceOrder(this, getGapChoices());
    }

    @Override
    public boolean validateResponse(AssessmentItemController itemController, Value responseValue) {
        /* Extract response values */
        List<DirectedPairValue> responseAssociations = new ArrayList<DirectedPairValue>();
        if (responseValue.isNull()) {
            /* Empty response */
        }
        else if (responseValue.getCardinality().isList()) {
            /* (Container response) */
            for (SingleValue association : (ListValue) responseValue) {
                responseAssociations.add((DirectedPairValue) association);
            }
        }
        else {
            /* (Single response) */
            responseAssociations.add((DirectedPairValue) responseValue);
        }
        
        /* Create hashes that will track the number of associations for each gapChoice */
        List<GapChoice> gapChoices = getGapChoices();
        Map<Identifier, Integer> responseGapChoiceAssociationCounts = new HashMap<Identifier, Integer>();
        for (GapChoice gapChoice : gapChoices) {
            responseGapChoiceAssociationCounts.put(gapChoice.getIdentifier(), Integer.valueOf(0));
        }
        /* Work out which <gap>s require an association */
        Set<Identifier> gapIdentifiers = new HashSet<Identifier>();
        Set<Identifier> requiredGapIdentifiers = new HashSet<Identifier>();
        Map<Identifier, Integer> responseGapAssociationCounts = new HashMap<Identifier, Integer>();
        List<Gap> gaps = search(Gap.class);
        for (Gap gap : gaps) {
            Identifier gapIdentifier = gap.getIdentifier();
            gapIdentifiers.add(gapIdentifier);
            responseGapAssociationCounts.put(gapIdentifier, Integer.valueOf(0));
            if (gap.getRequired().booleanValue()) {
                requiredGapIdentifiers.add(gapIdentifier);
            }
        }
        /* Go through each association in the response and tally things up */
        for (DirectedPairValue responseAssociation : responseAssociations) {
            Identifier gapTextIdentifier = responseAssociation.sourceValue();
            Identifier gapIdentifier = responseAssociation.destValue();
            
            if (!gapIdentifiers.contains(gapIdentifier)) { /* (Bad identifier in response) */
                return false;
            }
            responseGapAssociationCounts.put(gapIdentifier, responseGapAssociationCounts.get(gapIdentifier) + 1);
            requiredGapIdentifiers.remove(gapIdentifier);
            
            Integer count = responseGapChoiceAssociationCounts.get(gapTextIdentifier);
            if (count==null) { /* (Bad identifier in response) */
                return false;
            }
            responseGapChoiceAssociationCounts.put(gapTextIdentifier, count+1);
        }
        
        /* Make sure the correct number of associations were made to gapChoices*/
        for (GapChoice gapChoice : gapChoices) {
            if (!validateChoice(gapChoice, responseGapChoiceAssociationCounts.get(gapChoice.getIdentifier()))) {
                return false;
            }
        }
        
        /* Make sure all required <gap> associations were used */
        if (!requiredGapIdentifiers.isEmpty()) {
            return false;
        }
        /* Make sure each <gap> has no more than 1 association */
        for (Gap gap : gaps) {
            if (responseGapAssociationCounts.get(gap.getIdentifier()) > 1) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean validateChoice(GapChoice choice, int responseAssociateCount) {
        int matchMin = choice.getMatchMin();
        int matchMax = choice.getMatchMax();
        if (responseAssociateCount < matchMin) {
            return false;
        }
        else if (matchMax!=0 && responseAssociateCount > matchMax) {
            return false;
        }
        return true;
    }
}