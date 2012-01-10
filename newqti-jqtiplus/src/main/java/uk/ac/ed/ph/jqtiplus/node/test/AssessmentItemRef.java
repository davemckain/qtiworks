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
package uk.ac.ed.ph.jqtiplus.node.test;

import uk.ac.ed.ph.jqtiplus.attribute.value.StringMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.UriAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QTIEvaluationException;
import uk.ac.ed.ph.jqtiplus.group.test.TemplateDefaultGroup;
import uk.ac.ed.ph.jqtiplus.group.test.VariableMappingGroup;
import uk.ac.ed.ph.jqtiplus.group.test.WeightGroup;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Items are incorporated into the test by reference and not by direct aggregation.
 * <p>
 * Note that the identifier of the reference need not have any meaning outside the test. In particular it is not required to be unique in the context of any
 * catalogue, or be represented in the item's meta-data. The syntax of this identifier is more restrictive than that of the identifier attribute of the
 * assessmentItem itself.
 * 
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
public class AssessmentItemRef extends SectionPart {

    private static final long serialVersionUID = 5469740022955051680L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "assessmentItemRef";

    /** Name of href attribute in xml schema. */
    public static final String ATTR_HREF_NAME = "href";

    /** Name of category attribute in xml schema. */
    public static final String ATTR_CATEGORIES_NAME = "category";

    /** Default value of category attribute. */
    public static final List<String> ATTR_CATEGORIES_DEFAULT_VALUE = null;

    //    private final LifecycleListener lifecycleEventProxy;

    private final List<AbstractPart> children;

    //    private AssessmentItem item;

    //    private class ItemState implements Serializable {
    //        private static final long serialVersionUID = 1L;
    //
    //        public ItemState(AssessmentItemRef air) {
    //            outcomes = new TreeMap<String, Value>();
    //            sessionStatus = SessionStatus.INITIAL;
    //            timeRecord = new TimeRecord(air);
    //        }
    //        
    //        private boolean presented;
    //        private boolean responded;
    //        private boolean skipped;
    //        private boolean timedOut;
    //
    //        private Map<String, Value> outcomes;
    //
    //        private SessionStatus sessionStatus;
    //        private String candidateComment;
    //
    //        private TimeRecord timeRecord;
    //    }
    //    private ItemState currentState;
    //    private List<ItemState> states;

    /**
     * Constructs item reference.
     * 
     * @param parent parent of constructed item reference
     */
    public AssessmentItemRef(AssessmentSection parent) {
        super(parent);

        getAttributes().add(new UriAttribute(this, ATTR_HREF_NAME));
        getAttributes().add(new StringMultipleAttribute(this, ATTR_CATEGORIES_NAME, ATTR_CATEGORIES_DEFAULT_VALUE));

        getNodeGroups().add(new VariableMappingGroup(this));
        getNodeGroups().add(new WeightGroup(this));
        getNodeGroups().add(new TemplateDefaultGroup(this));

        children = new ArrayList<AbstractPart>();
        //        states = new ArrayList<ItemState>();
        //        
        //        /* Create a LifecycleListener to attach to Items that will forward Lifecycle events
        //         * via the owning AssessmentTest
        //         */
        //        lifecycleEventProxy = new LifecycleEventProxy(getRootNode(AssessmentTest.class));
        //        
        //        initNewState();
    }

    //
    //    /**
    //     * Initialize A new item state. Clients should not normally call this.
    //     */
    //    public void initNewState() {
    //        currentState = new ItemState(this);
    //
    //        if (getItem() != null) {
    //            item.setTimeRecord(currentState.timeRecord);
    //        }
    //        states.add(currentState);
    //        finished = false;
    //
    //        ControlObject parent = this;
    //        while ((parent = parent.getParent()) != null) {
    //            parent.finished = false;
    //        }
    //    }
    //    
    //    /**
    //     * Gets list of item states. Clients should not normally call this.
    //     * @return List of item states
    //     */
    //    public List<ItemState> getStates() {
    //        return states;
    //    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    @Override
    public List<AbstractPart> getChildren() {
        return children;
    }

    /**
     * Gets value of href attribute.
     * 
     * @return value of href attribute
     * @see #setHref
     */
    public URI getHref() {
        return getAttributes().getUriAttribute(ATTR_HREF_NAME).getValue();
    }

    /**
     * Sets new value of href attribute.
     * 
     * @param href new value of href attribute
     * @see #getHref
     */
    public void setHref(URI href) {
        getAttributes().getUriAttribute(ATTR_HREF_NAME).setValue(href);
    }

    /**
     * Sets new value of href attribute.
     * 
     * @param href new value of href attribute
     * @see #getHref
     */
    public void setHref(String href) {
        try {
            setHref(new URI(href));
        }
        catch (final URISyntaxException ex) {
            throw new QTIEvaluationException(ex);
        }
    }

    //    /**
    //     * Gets referenced item (can be null).
    //     * <p>
    //     * Every {@code validate} method call tries to load referenced item from file (even if item is already loaded).
    //     * <p>
    //     * Referenced item is properly loaded after {@code initialize} method call.
    //     *
    //     * @return referenced item (can be null)
    //     * @see #setItem
    //     */
    //    public AssessmentItem getItem()
    //    {
    //        return item;
    //    }

    //    /**
    //     * Sets new referenced item.
    //     * <p>
    //     * Every {@code validate} method call overwrites referenced item!
    //     *
    //     * @param item new referenced item
    //     * @see #getItem
    //     */
    //    public void setItem(AssessmentItem item)
    //    {
    //        if (this.item!=null) {
    //            this.item.removeLifecycleListener(lifecycleEventProxy);
    //        }
    //        this.item = item;
    //        if (item!=null) {
    //            item.addLifecycleListener(lifecycleEventProxy);
    //        }
    //    }

    /**
     * Gets value of category attribute.
     * 
     * @return value of category attribute
     */
    public List<String> getCategories() {
        return getAttributes().getStringMultipleAttribute(ATTR_CATEGORIES_NAME).getValues();
    }

    /**
     * Gets variableMapping children.
     * 
     * @return variableMapping children
     */
    public List<VariableMapping> getVariableMappings() {
        return getNodeGroups().getVariableMappingGroup().getVariableMappings();
    }

    /**
     * Gets weight children.
     * 
     * @return weight children
     */
    public List<Weight> getWeights() {
        return getNodeGroups().getWeightGroup().getWeights();
    }

    /**
     * Returns weight with given identifier or null.
     * 
     * @param identifier identifier of requested weight
     * @return weight with given identifier or null
     */
    public Weight getWeight(Identifier identifier) {
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
    public double lookupWeight(Identifier identifier) {
        for (final Weight weight : getWeights()) {
            if (weight.getIdentifier().equals(identifier)) {
                return weight.getValue();
            }
        }
        return Weight.DEFAULT_WEIGHT;
    }

    /**
     * Gets templateDefault children.
     * 
     * @return templateDefault children
     */
    public List<TemplateDefault> getTemplateDefaults() {
        return getNodeGroups().getTemplateDefaultGroup().getTemplateDefaults();
    }

    @Override
    public void validate(ValidationContext context, AbstractValidationResult result) {
        /* Validation of individual items is done by the calling validator, so there's not
         * much to do here!
         */
        super.validate(context, result);
    }
    
    @Override
    protected void validateChildren(ValidationContext context, AbstractValidationResult result) {
        super.validateChildren(context, result);

        for (int i = 0; i < getWeights().size(); i++) {
            final Weight weight = getWeights().get(i);
            if (weight.getIdentifier() != null) {
                for (int j = i + 1; j < getWeights().size(); j++) {
                    if (weight.getIdentifier().equals(getWeights().get(j).getIdentifier())) {
                        result.add(new ValidationError(this, "Duplicate weight identifier: " + weight.getIdentifier()));
                    }
                }
            }
        }
    }

    //
    //    @Override
    //    public void initialize()
    //    {
    //        super.initialize();
    //
    //        if (item == null)
    //        {
    //            initialiseAssessmentItem();
    //            
    //            item.setTimeRecord(currentState.timeRecord);
    //        }
    //    }
    //
    //    @Override
    //    public boolean isPresented()
    //    {
    //        return currentState.presented;
    //    }
    //
    //    /**
    //     * Sets this object to presented state.
    //     *
    //     * @see #isPresented
    //     */
    //    public void setPresented()
    //    {
    //        currentState.presented = true;
    //    }
    //
    //    /**
    //     * Gets value of session status.
    //     *
    //     * @return value of session status
    //     * @see #setSessionStatus
    //     */
    //    public SessionStatus getSessionStatus()
    //    {
    //        return currentState.sessionStatus;
    //    }
    //
    //    /**
    //     * Sets new value of session status.
    //     *
    //     * @param sessionStatus new value of session status
    //     * @see #getSessionStatus
    //     */
    //    public void setSessionStatus(SessionStatus sessionStatus)
    //    {
    //        this.currentState.sessionStatus = sessionStatus;
    //    }
    //
    //    /**
    //     * Gets candidate comment.
    //     *
    //     * @return candidate comment
    //     * @see #setCandidateComment
    //     */
    //    public String getCandidateComment()
    //    {
    //        return currentState.candidateComment;
    //    }
    //
    //    /**
    //     * Sets new candidate comment.
    //     *
    //     * @param candidateComment new candidate comment
    //     * @see #getCandidateComment
    //     */
    //    public void setCandidateComment(String candidateComment)
    //    {
    //        this.currentState.candidateComment = candidateComment;
    //    }

    /**
     * Applies the declared {@link VariableMapping}s to the given "target" {@link Identifier} to give the "source" {@link Identifier} used in the corresponding
     * {@link AssessmentItem}
     */
    public Identifier resolveVariableMapping(Identifier identifier) {
        Identifier result = identifier;
        for (final VariableMapping mapping : getVariableMappings()) {
            if (identifier.equals(mapping.getTargetIdentifier())) {
                result = mapping.getSourceIdentifier();
                break;
            }
        }
        return result;
    }

    @Override
    @ToRefactor
    /* NB: Maybe this needs to account for selection/ordering? */
    public List<AssessmentItemRef> lookupItemRefs(String identifier, List<String> includeCategories, List<String> excludeCategories) {
        if (getIdentifier() != null && getIdentifier().equals(identifier)) {
            identifier = null;
        }

        final List<AssessmentItemRef> items = new ArrayList<AssessmentItemRef>();

        if (identifier != null) {
            return items;
        }

        if (excludeCategories != null) {
            for (final String excludeCategory : excludeCategories) {
                if (getCategories().contains(excludeCategory)) {
                    return items;
                }
            }
        }

        if (includeCategories == null || includeCategories.size() == 0) {
            items.add(this);
        }
        else {
            for (final String includeCategory : includeCategories) {
                if (getCategories().contains(includeCategory)) {
                    items.add(this);
                }
            }
        }
        return items;
    }

    @Override
    public boolean isBuiltInVariable(Identifier identifier) {
        if (identifier != null) {
            if (identifier.toString().equals(AssessmentItem.VARIABLE_COMPLETION_STATUS)
                    || identifier.toString().equals(AssessmentItem.VARIABLE_NUMBER_OF_ATTEMPTS)) {
                return true;
            }
        }
        return super.isBuiltInVariable(identifier);
    }

    // DM: This dates from the time that AssessmentItemRefs had shadowed outcomeValues.
    //    @Override
    //    public Value lookupValue(String identifier)
    //    {
    //        return lookupValue(identifier, states.size()-1);
    //    }
    //    
    //    /**
    //     * Lookups for value of variable with given identifier in A particular state.
    //     * Clients should not normally call this method, and use lookupValue(identifier) instead.
    //     *
    //     * @param identifier identifier of requested variable.
    //     * @param state State index to use for lookup.
    //     * @return value of variable with given identifier or null.
    //     */
    //    public Value lookupValue(String identifier, int state)
    //    {
    //        if (identifier == null)
    //            return null;
    //
    //        if (identifier.equals(VARIABLE_DURATION_NAME))
    //            return new FloatValue(getDuration(state) / 1000.0);
    //        else if (identifier.equals(AssessmentItem.VARIABLE_NUMBER_OF_ATTEMPTS))
    //            return item.getResponseValue(AssessmentItem.VARIABLE_NUMBER_OF_ATTEMPTS);
    //
    //        OutcomeDeclaration declaration = getItem().getOutcomeDeclaration(identifier);
    //        if (declaration == null)
    //            return null;
    //
    //        Value value = states.get(state).outcomes.get(identifier);
    //        if (value != null)
    //            return value;
    //
    //        for (VariableMapping variableMapping : getVariableMappings())
    //        {
    //            if (variableMapping.getSourceIdentifier().equals(identifier))
    //            {
    //                value = states.get(state).outcomes.get(variableMapping.getTargetIdentifier());
    //
    //                if (value != null)
    //                    return value;
    //            }
    //        }
    //
    //        declaration.resetValue();
    //
    //        return declaration.getValue();
    //    }
    //
    //    /**
    //     * Returns true if this item reference was correctly responded; 
    //     * Correctly responded means ALL defined responseVars match their associated correctResponse.
    //     * Returns null if any of the responseDeclarations don't have  correctResponses.
    //     *
    //     * @return true if this item reference was correctly responded; null if not all 
    //     * responseDeclarations contain correctResponses; false otherwise
    //     * @see #isIncorrect
    //     */
    //    public Boolean isCorrect()
    //    {
    //        //nasty hack for backward compatibility
    //        Value variable = this.currentState.outcomes.get("R2Q2_IS_RESPONSE_CORRECT");
    //        boolean isResponseCorrect = (variable != null) ? ((BooleanValue) variable).booleanValue() : false;
    //        if (isResponseCorrect) return true;
    //        
    //        return item.isCorrect();
    //    }
    //
    //    /**
    //     * Returns true if this item reference was incorrectly responded; 
    //     * Incorrectly responded means ANY defined responseVars didn't match their 
    //     * associated correctResponse.
    //     * 
    //     * Returns null if any of the responseDeclarations don't have correctResponses.
    //     *
    //     * @return true if this item reference was incorrectly responded; null if not all 
    //     * responseDeclarations contain correctResponses; false otherwise
    //     * @see #isCorrect
    //     */
    //    public Boolean isIncorrect()
    //    {
    //        return item.isIncorrect();
    //    }
    //
    //    /**
    //     * Returns true if this item reference was responded; false otherwise.
    //     *
    //     * @return true if this item reference was responded; false otherwise
    //     */
    //    public boolean isResponded()
    //    {
    //        return currentState.responded;
    //    }
    //
    //    /**
    //     * Returns true if this item reference was skipped; false otherwise.
    //     *
    //     * @return true if this item reference was skipped; false otherwise
    //     * @see #skip
    //     */
    //    public boolean isSkipped()
    //    {
    //        return currentState.skipped;
    //    }
    //
    //    /**
    //     * Skips this item reference and sets state to finished.
    //     *
    //     * @throws QTIItemFlowException if this item reference if already finished or skipping is not allowed
    //     * @see #isSkipped
    //     */
    //    public void skip()
    //    {
    //        if (isFinished())
    //            throw new QTIItemFlowException(this, "Item reference is already finished.");
    //
    //        if (!getItemSessionControl().getAllowSkipping())
    //            throw new QTIItemFlowException(this, "It is not allowed to skip this item: ");
    //
    //        currentState.timeRecord.skip(getRootNode(AssessmentTest.class).getTimer().getCurrentTime());
    //        currentState.skipped = true;
    //        setFinished();
    //    }

    //    /**
    //     * Returns true if this item reference was timed out; false otherwise.
    //     *
    //     * @return true if this item reference was timed out; false otherwise
    //     * @see #timeOut
    //     */
    //    public boolean isTimedOut()
    //    {
    //        return currentState.timedOut;
    //    }
    //
    //    /**
    //     * Times out this item reference and sets state to finished.
    //     * <p>
    //     * This method should be called when user submits answer but time was already out.
    //     *
    //     * @throws QTIItemFlowException if this item reference is already finished
    //     * @see #isTimedOut
    //     */
    //    public void timeOut()
    //    {
    //        if (isFinished())
    //            throw new QTIItemFlowException(this, "Item reference is already finished.");
    //
    //        currentState.timeRecord.timeOut(getRootNode(AssessmentTest.class).getTimer().getCurrentTime());
    //        currentState.timedOut = true;
    //        setFinished();
    //    }
    //
    //    /**
    //     * Gets last submitted outcomes.
    //     *
    //     * @return last submitted outcomes
    //     * @see #setOutcomes
    //     */
    //    @ToRefactor
    //    public Map<String, Value> getOutcomes()
    //    {
    //        return currentState.outcomes;
    //    }
    //
    //    /**
    //     * Sets new outcomes (submit this item reference).
    //     *
    //     * @param outcomes new outcomes to be submitted
    //     * @throws QTIItemFlowException if this item reference is already finished
    //     * @see #getOutcomes
    //     */
    //    @ToRefactor
    //    public void setOutcomes(Map<String, Value> outcomes)
    //    {
    //        if (isFinished())
    //            throw new QTIItemFlowException(this, "Item reference is already finished.");
    //
    //        currentState.timeRecord.submit(getRootNode(AssessmentTest.class).getTimer().getCurrentTime());
    //
    //        if (outcomes == null)
    //            outcomes = new TreeMap<String, Value>();
    //
    //        this.currentState.outcomes = outcomes;
    //
    //        currentState.responded = true;
    //
    //        if (getItem().getAdaptive())
    //        {
    //            IdentifierValue status = (IdentifierValue) lookupValue(AssessmentItem.VARIABLE_COMPLETION_STATUS);
    //            if (status != null && status.stringValue().equals(AssessmentItem.VALUE_ITEM_IS_COMPLETED))
    //                setFinished();
    //        }
    //        else
    //        {
    //            int attempts = ((IntegerValue)item.getResponseValue(AssessmentItem.VARIABLE_NUMBER_OF_ATTEMPTS)).intValue();
    //            boolean isResponseCorrect = (isCorrect() == Boolean.TRUE) ? true : false;
    //            //TODO: figure out a way to remove R2Q2_IS_RESPONSE_CORRECT using the following?
    //            //boolean isResponseCorrect = (getItem().countCorrect() > 0 && getItem().countIncorrect() == 0);
    //            
    //            int maxAttempts = getItemSessionControl().getMaxAttempts();
    //            
    //            if (getParentTestPart().getSubmissionMode() == SubmissionMode.SIMULTANEOUS || isResponseCorrect || (maxAttempts != 0 && attempts >= maxAttempts))
    //                setFinished();
    //        }
    //    }
    //
    //    @Override
    //    public int getTotalCount()
    //    {
    //        return 1;
    //    }
    //
    //    @Override
    //    public int getPresentedCount()
    //    {
    //        return 1;
    //    }
    //
    //    @Override
    //    public int getFinishedCount()
    //    {
    //        return 1;
    //    }
    //
    //    /**
    //     * Gets time record of this item reference.
    //     *
    //     * @return time record of this item reference
    //     */
    //    public TimeRecord getTimeRecord()
    //    {
    //        return currentState.timeRecord;
    //    }

    //    @Override
    //    public long getTotalTime()
    //    {
    //        return currentState.timeRecord.getTotal();
    //    }
    //
    //    @Override
    //    public long getResponseTime()
    //    {
    //        return currentState.timeRecord.getDuration();
    //    }
    //
    //    /**
    //     * Gets total time spent <em>inside</em> this object excluding navigation time for A given state.
    //     * Clients should not normally call this method.
    //     * <p>
    //     * This methods returns pure response (thinking) time (navigation time is not included).
    //     * 
    //     * @param state State index to use of lookup. 
    //     *
    //     * @return total time spent <em>inside</em> this object excluding navigation time
    //     * @see #getTotalTime
    //     */
    //    public long getResponseTime(int state)
    //    {
    //        return states.get(state).timeRecord.getDuration();
    //    }
    //    
    //    @Override
    //    public long getDuration()
    //    {
    //        return getResponseTime();
    //    }
    //    
    //    /**
    //     * Gets value of duration built-in variable.
    //     * Clients should not normally call this method.
    //     * <p>
    //     * Duration for test or test part or section means total time (thinking time including navigation time).
    //     * <p>
    //     * Duration for item reference means response time (thinking time excluding navigation time).
    //     * 
    //     * @param state State index to use of lookup. 
    //     *
    //     * @return value of duration built-in variable
    //     */
    //    public long getDuration(int state)
    //    {
    //        return getResponseTime(state);
    //    }
    //
    //    /**
    //     * Returns current result of this item reference.
    //     *
    //     * @param parent parent of created result
    //     * @param sequenceIndex sequence index of created result
    //     * @param sessionStatus session status of created result
    //     * @return current result of this item reference
    //     */
    //    public List<ItemResult> getItemResult(AssessmentResult parent, Integer sequenceIndex, SessionStatus sessionStatus)
    //    {
    //        List<ItemResult> result = new ArrayList<ItemResult>();
    //
    //        for (int i=0; i<states.size(); i++) {
    //            result.add(getItemResult(parent, sequenceIndex, sessionStatus, i));
    //        }
    //
    //        return result;
    //    }
    //
    //    private ItemResult getItemResult(AssessmentResult parent, Integer sequenceIndex, SessionStatus sessionStatus, int state)
    //    {
    //        ItemResult result = new ItemResult(parent);
    //
    //        if (states.size() == 1)
    //            result.setIdentifier(getIdentifier());
    //        else
    //            result.setIdentifier(getIdentifier() + "#" + (state+1));
    //        
    //        result.setDateStamp(new Date());
    //        result.setSequenceIndex(sequenceIndex);
    //        result.setSessionStatus(sessionStatus);
    //        if (states.get(state).candidateComment != null && states.get(state).candidateComment.length() > 0)
    //            result.setCandidateComment(new CandidateComment(result, states.get(state).candidateComment));
    //
    //        for (OutcomeDeclaration declaration : getItem().getOutcomeDeclarations())
    //        {
    //            declaration.resetValue();
    //            Value value = lookupValue(declaration.getIdentifier(), state);
    //            OutcomeVariable variable = new OutcomeVariable(result, declaration, value);
    //            result.getItemVariables().add(variable);
    //        }
    //        result.getItemVariables().add(new OutcomeVariable(result, AssessmentItem.VARIABLE_COMPLETION_STATUS, getItem().getOutcomeValue(AssessmentItem.VARIABLE_COMPLETION_STATUS)));
    //
    //        for (String identifier : getItem().getResponseValues().keySet())
    //        {
    //            ResponseDeclaration declaration = getItem().getResponseDeclaration(identifier);
    //            ResponseVariable variable = new ResponseVariable(result, declaration);
    //            result.getItemVariables().add(variable);
    //        }
    //
    //        for (TemplateDeclaration declaration : getItem().getTemplateDeclarations())
    //        {
    //            TemplateVariable variable = new TemplateVariable(result, declaration);
    //            result.getItemVariables().add(variable);
    //        }
    //
    //        return result;
    //    }
}
