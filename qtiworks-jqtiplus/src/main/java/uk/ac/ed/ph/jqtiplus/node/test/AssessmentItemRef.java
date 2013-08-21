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
package uk.ac.ed.ph.jqtiplus.node.test;

import uk.ac.ed.ph.jqtiplus.attribute.value.StringMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.UriAttribute;
import uk.ac.ed.ph.jqtiplus.group.test.TemplateDefaultGroup;
import uk.ac.ed.ph.jqtiplus.group.test.VariableMappingGroup;
import uk.ac.ed.ph.jqtiplus.group.test.WeightGroup;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.RootNodeLookup;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.TestValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * Represents the <code>assessmentItemRef</code> QTI class
 *
 * @author Jiri Kajaba
 * @author Jonathon Hare
 * @author David McKain
 */
public final class AssessmentItemRef extends SectionPart {

    private static final long serialVersionUID = 5469740022955051680L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "assessmentItemRef";

    /** Name of href attribute in xml schema. */
    public static final String ATTR_HREF_NAME = "href";

    /** Name of category attribute in xml schema. */
    public static final String ATTR_CATEGORIES_NAME = "category";

    public AssessmentItemRef(final AssessmentSection parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new UriAttribute(this, ATTR_HREF_NAME, true));
        getAttributes().add(new StringMultipleAttribute(this, ATTR_CATEGORIES_NAME, false));

        getNodeGroups().add(new VariableMappingGroup(this));
        getNodeGroups().add(new WeightGroup(this));
        getNodeGroups().add(new TemplateDefaultGroup(this));
    }

    @Override
    public List<AbstractPart> getChildAbstractParts() {
        return Collections.emptyList();
    }

    public URI getHref() {
        return getAttributes().getUriAttribute(ATTR_HREF_NAME).getComputedValue();
    }

    public void setHref(final URI href) {
        getAttributes().getUriAttribute(ATTR_HREF_NAME).setValue(href);
    }


    public List<String> getCategories() {
        return getAttributes().getStringMultipleAttribute(ATTR_CATEGORIES_NAME).getComputedValue();
    }

    public void setCategories(final List<String> value) {
        getAttributes().getStringMultipleAttribute(ATTR_CATEGORIES_NAME).setValue(value);
    }


    public List<VariableMapping> getVariableMappings() {
        return getNodeGroups().getVariableMappingGroup().getVariableMappings();
    }


    public List<Weight> getWeights() {
        return getNodeGroups().getWeightGroup().getWeights();
    }

    /**
     * Returns weight with given identifier or null.
     *
     * @param identifier identifier of requested weight
     * @return weight with given identifier or null
     */
    public Weight getWeight(final Identifier identifier) {
        for (final Weight weight : getWeights()) {
            if (weight.getIdentifier() != null && weight.getIdentifier().equals(identifier)) {
                return weight;
            }
        }
        return null;
    }

    /**
     * Returns value of weight with given identifier or default weight value (if weight was not found).
     *
     * @param identifier identifier of requested weight
     * @return value of weight with given identifier or default weight value (if weight was not found)
     */
    public double lookupWeight(final Identifier identifier) {
        for (final Weight weight : getWeights()) {
            if (weight.getIdentifier().equals(identifier)) {
                return weight.getValue();
            }
        }
        return Weight.DEFAULT_WEIGHT;
    }

    public List<TemplateDefault> getTemplateDefaults() {
        return getNodeGroups().getTemplateDefaultGroup().getTemplateDefaults();
    }

    @Override
    public void validateThis(final ValidationContext context) {
        super.validateThis(context);

        /* Issue a warning if the corresponding item is adaptive and we're in SIMULTAENOUS mode,
         * as adaptive items can't work correctly in that case.
         */
        final TestPart testPart = getEnclosingTestPart();
        if (testPart!=null && testPart.getSubmissionMode()==SubmissionMode.SIMULTANEOUS) {
            final TestValidationContext testValidationContext = (TestValidationContext) context;
            final ResolvedAssessmentTest resolvedAssessmentTest = testValidationContext.getResolvedAssessmentTest();
            final ResolvedAssessmentItem resolvedAssessmentItem = resolvedAssessmentTest.getResolvedAssessmentItem(this);
            if (resolvedAssessmentItem!=null) {
                final RootNodeLookup<AssessmentItem> itemLookup = resolvedAssessmentItem.getItemLookup();
                if (itemLookup.wasSuccessful()) {
                    final AssessmentItem assessmentItem = itemLookup.extractAssumingSuccessful();
                    if (assessmentItem.getAdaptive()) {
                        context.fireValidationWarning(this,
                                "Referenced assessmentItem is adaptive and won't work correctly in "
                                + "this testPart having 'simultaneous' submissionMode");
                    }
                }
            }
        }

        /* Check weights */
        for (int i = 0; i < getWeights().size(); i++) {
            final Weight weight = getWeights().get(i);
            final Identifier weightIdentifier = weight.getIdentifier();
            if (weightIdentifier != null) {
                for (int j = i + 1; j < getWeights().size(); j++) {
                    if (weightIdentifier.equals(getWeights().get(j).getIdentifier())) {
                        context.fireValidationError(this, "Duplicate weight identifier: " + weightIdentifier);
                    }
                }
            }
        }
    }

    /**
     * Applies the declared {@link VariableMapping}s to the given "target" {@link Identifier} to give the "source" {@link Identifier} used in the corresponding
     * {@link AssessmentItem}
     */
    public Identifier resolveVariableMapping(final Identifier identifier) {
        Identifier result = identifier;
        for (final VariableMapping mapping : getVariableMappings()) {
            if (identifier.equals(mapping.getTargetIdentifier())) {
                result = mapping.getSourceIdentifier();
                break;
            }
        }
        return result;
    }
}
