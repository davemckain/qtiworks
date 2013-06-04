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
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Shuffleable;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.template.declaration.TemplateDeclaration;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.result.SessionStatus;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulates the current state of a candidate's item session.
 * <p>
 * An instance of this class is mutable, but you must let JQTI+ perform all
 * mutation operations for you via the {@link ItemSessionController} to
 * ensure the integrity of state.
 * <p>
 * An instance of this class is NOT safe for use by multiple threads.
 *
 * @see ItemSessionController
 *
 * @author David McKain
 */
@ObjectDumperOptions(DumpMode.DEEP)
public final class ItemSessionState extends AbstractPartSessionState implements Serializable {

    private static final long serialVersionUID = -7586529679289092485L;

    /**
     * Map of interaction choice orders, for all {@link Shuffleable} interactions.
     * This is keyed on the interaction's responseIdentifier.
     */
    private final Map<Identifier, List<Identifier>> shuffledInteractionChoiceOrders;

    /**
     * Map of template values, keyed on Identifier.
     */
    private final Map<Identifier, Value> templateValues;

    /**
     * Map of (committed) response values, keyed on Identifier.
     * <p>
     * This does not include the implicit <code>duration</code>
     * and <code>numAttempts</code> variables.
     */
    private final Map<Identifier, Value> responseValues;

    /**
     * Map of outcome values, keyed on Identifier.
     * <p>
     * This does not include the implicit <code>completionStatus</code>
     * variable.
     */
    private final Map<Identifier, Value> outcomeValues;

    private final Map<Identifier, ResponseData> rawResponseDataMap;
    private final Set<Identifier> unboundResponseIdentifiers;
    private final Set<Identifier> invalidResponseIdentifiers;

    /**
     * Bound but not yet committed response variables.
     */
    private final Map<Identifier, Value> uncommittedResponseValues;

    private final Map<Identifier, Value> overriddenTemplateDefaultValues;
    private final Map<Identifier, Value> overriddenResponseDefaultValues;
    private final Map<Identifier, Value> overriddenOutcomeDefaultValues;
    private final Map<Identifier, Value> overriddenCorrectResponseValues;

    private int numAttempts;
    private String completionStatus;

    private SessionStatus sessionStatus;
    private boolean initialized;
    private boolean responded;
    private Date suspendTime;
    private String candidateComment;

    public ItemSessionState() {
        super();
        this.shuffledInteractionChoiceOrders = new HashMap<Identifier, List<Identifier>>();
        this.templateValues = new HashMap<Identifier, Value>();
        this.responseValues = new HashMap<Identifier, Value>();
        this.outcomeValues = new HashMap<Identifier, Value>();
        this.rawResponseDataMap = new HashMap<Identifier, ResponseData>();
        this.unboundResponseIdentifiers = new HashSet<Identifier>();
        this.invalidResponseIdentifiers = new HashSet<Identifier>();
        this.uncommittedResponseValues = new HashMap<Identifier, Value>();
        this.overriddenTemplateDefaultValues = new HashMap<Identifier, Value>();
        this.overriddenResponseDefaultValues = new HashMap<Identifier, Value>();
        this.overriddenOutcomeDefaultValues = new HashMap<Identifier, Value>();
        this.overriddenCorrectResponseValues = new HashMap<Identifier, Value>();
        this.sessionStatus = null;
        this.initialized = false;
        this.responded = false;
        this.suspendTime = null;
        this.candidateComment = null;
        resetBuiltinVariables();
    }

    //----------------------------------------------------------------

    @Override
    public void reset() {
        super.reset();
        this.shuffledInteractionChoiceOrders.clear();
        this.templateValues.clear();
        this.responseValues.clear();
        this.outcomeValues.clear();
        this.rawResponseDataMap.clear();
        this.unboundResponseIdentifiers.clear();
        this.invalidResponseIdentifiers.clear();
        this.uncommittedResponseValues.clear();
        this.overriddenTemplateDefaultValues.clear();
        this.overriddenResponseDefaultValues.clear();
        this.overriddenOutcomeDefaultValues.clear();
        this.overriddenCorrectResponseValues.clear();
        this.sessionStatus = SessionStatus.INITIAL;
        this.initialized = false;
        this.responded = false;
        this.suspendTime = null;
        this.candidateComment = null;
        resetBuiltinVariables();
    }

    public void resetBuiltinVariables() {
        resetDuration();
        setNumAttempts(0);
        setCompletionStatus(QtiConstants.COMPLETION_STATUS_NOT_ATTEMPTED);
    }

    //----------------------------------------------------------------
    // Interaction init helpers

    public Map<Identifier, List<Identifier>> getShuffledInteractionChoiceOrders() {
        return Collections.unmodifiableMap(shuffledInteractionChoiceOrders);
    }

    public List<Identifier> getShuffledInteractionChoiceOrder(final Identifier responseIdentifier) {
        Assert.notNull(responseIdentifier);
        return shuffledInteractionChoiceOrders.get(responseIdentifier);
    }

    public List<Identifier> getShuffledInteractionChoiceOrder(final Interaction interaction) {
        Assert.notNull(interaction);
        return getShuffledInteractionChoiceOrder(interaction.getResponseIdentifier());
    }

    public void setShuffledInteractionChoiceOrder(final Identifier responseIdentifier, final List<Identifier> shuffleOrders) {
        Assert.notNull(responseIdentifier);
        if (shuffleOrders == null || shuffleOrders.isEmpty()) {
            shuffledInteractionChoiceOrders.remove(responseIdentifier);
        }
        else {
            shuffledInteractionChoiceOrders.put(responseIdentifier, shuffleOrders);
        }
    }

    public void setShuffledInteractionChoiceOrder(final Interaction interaction, final List<Identifier> shuffleOrders) {
        Assert.notNull(interaction);
        setShuffledInteractionChoiceOrder(interaction.getResponseIdentifier(), shuffleOrders);
    }

    //----------------------------------------------------------------

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(final boolean initialized) {
        this.initialized = initialized;
    }


    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(final SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    @ObjectDumperOptions(DumpMode.IGNORE)
    public boolean isPresented() {
        return isEntered();
    }


    public boolean isResponded() {
        return responded;
    }

    public void setResponded(final boolean responded) {
        this.responded = responded;
    }


    public boolean isRespondedValidly() {
        return isResponded() && unboundResponseIdentifiers.isEmpty() && invalidResponseIdentifiers.isEmpty();
    }

    @ObjectDumperOptions(DumpMode.IGNORE)
    public boolean isRespondedInvalidly() {
        return isResponded() && !(unboundResponseIdentifiers.isEmpty() && invalidResponseIdentifiers.isEmpty());
    }


    public Date getSuspendTime() {
        return ObjectUtilities.safeClone(suspendTime);
    }

    public void setSuspendTime(final Date suspendTime) {
        this.suspendTime = ObjectUtilities.safeClone(suspendTime);
    }

    @ObjectDumperOptions(DumpMode.IGNORE)
    public boolean isSuspended() {
        return suspendTime!=null;
    }


    public String getCandidateComment() {
        return candidateComment;
    }

    public void setCandidateComment(final String candidateComment) {
        this.candidateComment = candidateComment;
    }

    //----------------------------------------------------------------
    // Built-in variable manipulation

    /**
     * Returns the accumulated duration in seconds. Note that this is the
     * accumulated duration as recorded during the last "touch" of the state.
     */
    public double computeDuration() {
        return getDurationAccumulated() / 1000.0;
    }

    /**
     * Returns the accumulated duration as a {@link FloatValue}. Note that this is the
     * accumulated duration as recorded during the last "touch" of the state.
     */
    @ObjectDumperOptions(DumpMode.IGNORE)
    public FloatValue computeDurationValue() {
        return new FloatValue(computeDuration());
    }


    public int getNumAttempts() {
        return numAttempts;
    }

    public void setNumAttempts(final int numAttempts) {
        this.numAttempts = numAttempts;
    }

    @ObjectDumperOptions(DumpMode.IGNORE)
    public IntegerValue getNumAttemptsValue() {
        return new IntegerValue(numAttempts);
    }


    public String getCompletionStatus() {
        return this.completionStatus;
    }

    public void setCompletionStatus(final String completionStatus) {
        Assert.notNull(completionStatus);
        if (!QtiConstants.COMPLETION_STATUS_UNKNOWN.equals(completionStatus)
                && !QtiConstants.COMPLETION_STATUS_NOT_ATTEMPTED.equals(completionStatus)
                && !QtiConstants.COMPLETION_STATUS_COMPLETED.equals(completionStatus)
                && !QtiConstants.COMPLETION_STATUS_INCOMPLETE.equals(completionStatus)) {
            throw new IllegalArgumentException("Value " + completionStatus + " is not an acceptable completionStatus");
        }
        this.completionStatus = completionStatus;
    }

    @ObjectDumperOptions(DumpMode.IGNORE)
    public IdentifierValue getCompletionStatusValue() {
        return new IdentifierValue(completionStatus);
    }

    //----------------------------------------------------------------

    public Value getOverriddenDefaultValue(final Identifier identifier) {
        Assert.notNull(identifier);
        Value result = getOverriddenTemplateDefaultValue(identifier);
        if (result == null) {
            result = getOverriddenResponseDefaultValue(identifier);
            if (result == null) {
                result = getOverriddenOutcomeDefaultValue(identifier);
            }
        }
        return result;
    }

    public Value getOverriddenDefaultValue(final VariableDeclaration declaration) {
        return getOverriddenDefaultValue(declaration.getIdentifier());
    }

    //----------------------------------------------------------------

    public Value getOverriddenTemplateDefaultValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return overriddenTemplateDefaultValues.get(identifier);
    }

    public Value getOverriddenTemplateDefaultValue(final TemplateDeclaration templateDeclaration) {
        Assert.notNull(templateDeclaration);
        return getOverriddenTemplateDefaultValue(templateDeclaration.getIdentifier());
    }

    public void setOverriddenTemplateDefaultValue(final Identifier identifier, final Value value) {
        Assert.notNull(identifier);
        Assert.notNull(value);
        overriddenTemplateDefaultValues.put(identifier, value);
    }

    public void setOverriddenTemplateDefaultValue(final TemplateDeclaration templateDeclaration, final Value value) {
        Assert.notNull(templateDeclaration);
        setOverriddenTemplateDefaultValue(templateDeclaration.getIdentifier(), value);
    }

    public Map<Identifier, Value> getOverriddenTemplateDefaultValues() {
        return Collections.unmodifiableMap(overriddenTemplateDefaultValues);
    }

    //----------------------------------------------------------------

    public Value getOverriddenResponseDefaultValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return overriddenResponseDefaultValues.get(identifier);
    }

    public Value getOverriddenResponseDefaultValue(final ResponseDeclaration responseDeclaration) {
        Assert.notNull(responseDeclaration);
        return getOverriddenResponseDefaultValue(responseDeclaration.getIdentifier());
    }

    public void setOverriddenResponseDefaultValue(final Identifier identifier, final Value value) {
        Assert.notNull(identifier);
        Assert.notNull(value);
        overriddenResponseDefaultValues.put(identifier, value);
    }

    public void setOverriddenResponseDefaultValue(final ResponseDeclaration responseDeclaration, final Value value) {
        Assert.notNull(responseDeclaration);
        Assert.notNull(value);
        setOverriddenResponseDefaultValue(responseDeclaration.getIdentifier(), value);
    }

    public Map<Identifier, Value> getOverriddenResponseDefaultValues() {
        return Collections.unmodifiableMap(overriddenResponseDefaultValues);
    }

    //----------------------------------------------------------------

    public Value getOverriddenOutcomeDefaultValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return overriddenOutcomeDefaultValues.get(identifier);
    }

    public Value getOverriddenOutcomeDefaultValue(final OutcomeDeclaration outcomeDeclaration) {
        Assert.notNull(outcomeDeclaration);
        return getOverriddenOutcomeDefaultValue(outcomeDeclaration.getIdentifier());
    }

    public void setOverriddenOutcomeDefaultValue(final Identifier identifier, final Value value) {
        Assert.notNull(identifier);
        Assert.notNull(value);
        overriddenOutcomeDefaultValues.put(identifier, value);
    }

    public void setOverriddenOutcomeDefaultValue(final OutcomeDeclaration outcomeDeclaration, final Value value) {
        Assert.notNull(outcomeDeclaration);
        setOverriddenResponseDefaultValue(outcomeDeclaration.getIdentifier(), value);
    }

    public Map<Identifier, Value> getOverriddenOutcomeDefaultValues() {
        return Collections.unmodifiableMap(overriddenOutcomeDefaultValues);
    }

    //----------------------------------------------------------------

    public Value getOverriddenCorrectResponseValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return overriddenCorrectResponseValues.get(identifier);
    }

    public Value getOverriddenCorrectResponseValue(final ResponseDeclaration responseDeclaration) {
        Assert.notNull(responseDeclaration);
        return getOverriddenCorrectResponseValue(responseDeclaration.getIdentifier());
    }

    public void setOverriddenCorrectResponseValue(final Identifier identifier, final Value value) {
        Assert.notNull(identifier);
        Assert.notNull(value);
        overriddenCorrectResponseValues.put(identifier, value);
    }

    public void setOverriddenCorrectResponseValue(final ResponseDeclaration responseDeclaration, final Value value) {
        Assert.notNull(responseDeclaration);
        setOverriddenCorrectResponseValue(responseDeclaration.getIdentifier(), value);
    }

    public Map<Identifier, Value> getOverriddenCorrectResponseValues() {
        return Collections.unmodifiableMap(overriddenCorrectResponseValues);
    }

    //----------------------------------------------------------------

    public Value getTemplateValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return templateValues.get(identifier);
    }

    public Value getTemplateValue(final String identifierString) {
        Assert.notNull(identifierString);
        return getTemplateValue(Identifier.parseString(identifierString));
    }

    public Value getTemplateValue(final TemplateDeclaration templateDeclaration) {
        Assert.notNull(templateDeclaration);
        return getTemplateValue(templateDeclaration.getIdentifier());
    }

    public void setTemplateValue(final Identifier identifier, final Value value) {
        Assert.notNull(identifier);
        Assert.notNull(value);
        templateValues.put(identifier, value);
    }

    public void setTemplateValue(final String identifierString, final Value value) {
        Assert.notNull(identifierString);
        setTemplateValue(Identifier.parseString(identifierString), value);
    }

    public void setTemplateValue(final TemplateDeclaration templateDeclaration, final Value value) {
        Assert.notNull(templateDeclaration);
        setTemplateValue(templateDeclaration.getIdentifier(), value);
    }

    public Map<Identifier, Value> getTemplateValues() {
        return Collections.unmodifiableMap(templateValues);
    }

    //----------------------------------------------------------------
    // Response mutation

    public ResponseData getRawResponseData(final Identifier identifier) {
        Assert.notNull(identifier, "identifier");
        return rawResponseDataMap.get(identifier);
    }

    public void setRawResponseData(final Identifier identifier, final ResponseData responseData) {
        Assert.notNull(identifier, "identifier");
        Assert.notNull(responseData, "responseData");
        rawResponseDataMap.put(identifier, responseData);
    }

    public Map<Identifier, ResponseData> getRawResponseDataMap() {
        return Collections.unmodifiableMap(rawResponseDataMap);
    }

    public void clearRawResponseDataMap() {
        this.rawResponseDataMap.clear();
    }

    public void setRawResponseDataMap(final Map<Identifier, ResponseData> rawResponseDataMap) {
        this.rawResponseDataMap.clear();
        this.rawResponseDataMap.putAll(rawResponseDataMap);
    }


    public Set<Identifier> getUnboundResponseIdentifiers() {
        return Collections.unmodifiableSet(unboundResponseIdentifiers);
    }

    public void setUnboundResponseIdentifiers(final Collection<Identifier> unboundResponseIdentifiers) {
        this.unboundResponseIdentifiers.clear();
        this.unboundResponseIdentifiers.addAll(unboundResponseIdentifiers);
    }

    public void clearUnboundResponseIdentifiers() {
        this.unboundResponseIdentifiers.clear();
    }

    public Set<Identifier> getInvalidResponseIdentifiers() {
        return Collections.unmodifiableSet(invalidResponseIdentifiers);
    }


    public void setInvalidResponseIdentifiers(final Collection<Identifier> invalidResponseIdentifiers) {
        this.invalidResponseIdentifiers.clear();
        this.invalidResponseIdentifiers.addAll(invalidResponseIdentifiers);
    }

    public void clearInvalidResponseIdentifiers() {
        this.invalidResponseIdentifiers.clear();
    }


    public Value getUncommittedResponseValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return uncommittedResponseValues.get(identifier);
    }

    public Value getUncommittedResponseValue(final ResponseDeclaration responseDeclaration) {
        Assert.notNull(responseDeclaration);
        return getUncommittedResponseValue(responseDeclaration.getIdentifier());
    }

    public Value getUncommittedResponseValue(final Interaction interaction) {
        Assert.notNull(interaction);
        return getUncommittedResponseValue(interaction.getResponseIdentifier());
    }

    public void setUncommittedResponseValue(final Identifier identifier, final Value value) {
        Assert.notNull(identifier);
        Assert.notNull(value);
        uncommittedResponseValues.put(identifier, value);
    }

    public void setUncommittedResponseValue(final ResponseDeclaration responseDeclaration, final Value value) {
        Assert.notNull(responseDeclaration);
        Assert.notNull(value);
        setUncommittedResponseValue(responseDeclaration.getIdentifier(), value);
    }

    public void setUncommittedResponseValue(final Interaction interaction, final Value value) {
        Assert.notNull(interaction);
        Assert.notNull(value);
        setUncommittedResponseValue(interaction.getResponseIdentifier(), value);
    }

    public void clearUncommittedResponseValues() {
        uncommittedResponseValues.clear();
    }

    public Map<Identifier, Value> getUncommittedResponseValues() {
        return Collections.unmodifiableMap(uncommittedResponseValues);
    }

    public boolean hasUncommittedResponseValues() {
        return !uncommittedResponseValues.isEmpty();
    }


    public Value getResponseValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return responseValues.get(identifier);
    }

    public Value getResponseValue(final ResponseDeclaration responseDeclaration) {
        Assert.notNull(responseDeclaration);
        return getResponseValue(responseDeclaration.getIdentifier());
    }

    public Value getResponseValue(final Interaction interaction) {
        Assert.notNull(interaction);
        return getResponseValue(interaction.getResponseIdentifier());
    }

    public void setResponseValue(final Identifier identifier, final Value value) {
        Assert.notNull(identifier);
        Assert.notNull(value);
        responseValues.put(identifier, value);
    }

    public void setResponseValue(final ResponseDeclaration responseDeclaration, final Value value) {
        Assert.notNull(responseDeclaration);
        Assert.notNull(value);
        setResponseValue(responseDeclaration.getIdentifier(), value);
    }

    public void setResponseValue(final Interaction interaction, final Value value) {
        Assert.notNull(interaction);
        Assert.notNull(value);
        setResponseValue(interaction.getResponseIdentifier(), value);
    }

    /**
     * NB: This does not include the <code>numAttempts</code>
     * and <code>duration</code> response values.
     */
    public Map<Identifier, Value> getResponseValues() {
        return Collections.unmodifiableMap(responseValues);
    }

    //----------------------------------------------------------------

    public Value getOutcomeValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return outcomeValues.get(identifier);
    }

    public Value getOutcomeValue(final OutcomeDeclaration outcomeDeclaration) {
        Assert.notNull(outcomeDeclaration);
        return getOutcomeValue(outcomeDeclaration.getIdentifier());
    }

    public void setOutcomeValue(final Identifier identifier, final Value value) {
        Assert.notNull(identifier);
        Assert.notNull(value);
        outcomeValues.put(identifier, value);
    }

    public void setOutcomeValue(final OutcomeDeclaration outcomeDeclaration, final Value value) {
        Assert.notNull(outcomeDeclaration);
        setOutcomeValue(outcomeDeclaration.getIdentifier(), value);
    }

    /**
     * NB: This does not include the <code>completionStatus</code> value.
     */
    public Map<Identifier, Value> getOutcomeValues() {
        return Collections.unmodifiableMap(outcomeValues);
    }

    //---------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ItemSessionState)) {
            return false;
        }
        final ItemSessionState other = (ItemSessionState) obj;
        return super.equals(obj)
                && numAttempts==other.numAttempts
                && initialized==other.initialized
                && responded==other.responded
                && ObjectUtilities.nullSafeEquals(suspendTime, other.suspendTime)
                && ObjectUtilities.nullSafeEquals(completionStatus, other.completionStatus)
                && ObjectUtilities.nullSafeEquals(sessionStatus, other.sessionStatus)
                && ObjectUtilities.nullSafeEquals(candidateComment, other.candidateComment)
                && shuffledInteractionChoiceOrders.equals(other.shuffledInteractionChoiceOrders)
                && rawResponseDataMap.equals(other.rawResponseDataMap)
                && unboundResponseIdentifiers.equals(other.unboundResponseIdentifiers)
                && invalidResponseIdentifiers.equals(other.invalidResponseIdentifiers)
                && uncommittedResponseValues.equals(other.uncommittedResponseValues)
                && templateValues.equals(other.templateValues)
                && responseValues.equals(other.responseValues)
                && outcomeValues.equals(other.outcomeValues)
                && overriddenTemplateDefaultValues.equals(other.overriddenTemplateDefaultValues)
                && overriddenResponseDefaultValues.equals(other.overriddenResponseDefaultValues)
                && overriddenOutcomeDefaultValues.equals(other.overriddenOutcomeDefaultValues)
                && overriddenCorrectResponseValues.equals(other.overriddenCorrectResponseValues)
                ;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                super.hashCode(),
                numAttempts,
                completionStatus,
                initialized,
                sessionStatus,
                responded,
                suspendTime,
                candidateComment,
                shuffledInteractionChoiceOrders,
                rawResponseDataMap,
                unboundResponseIdentifiers,
                invalidResponseIdentifiers,
                uncommittedResponseValues,
                templateValues,
                responseValues,
                outcomeValues,
                overriddenTemplateDefaultValues,
                overriddenResponseDefaultValues,
                overriddenOutcomeDefaultValues,
                overriddenCorrectResponseValues
        });
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(initialized=" + initialized
                + ",sessionStatus=" + sessionStatus
                + ",preConditionFailed=" + isPreConditionFailed()
                + ",jumpedByBranchRule=" + jumpedByBranchRule
                + ",branchRuleTarget=" + branchRuleTarget
                + ",entryTime=" + getEntryTime()
                + ",endTime=" + getEndTime()
                + ",exitTime=" + getExitTime()
                + ",durationAccumulated=" + getDurationAccumulated()
                + ",durationIntervalStartTime=" + getDurationIntervalStartTime()
                + ",numAttempts=" + getNumAttempts()
                + ",completionStatus=" + getCompletionStatus()
                + ",responded=" + responded
                + ",suspendTime=" + suspendTime
                + ",candidateComment=" + candidateComment
                + ",shuffledInteractionChoiceOrders=" + shuffledInteractionChoiceOrders
                + ",overriddenTemplateDefaultValues=" + overriddenTemplateDefaultValues
                + ",overriddenResponseDefaultValues=" + overriddenResponseDefaultValues
                + ",overriddenOutcomeDefaultValues=" + overriddenOutcomeDefaultValues
                + ",overriddenCorrectResponseValues=" + overriddenCorrectResponseValues
                + ",rawResponseDataMap=" + rawResponseDataMap
                + ",unboundResponseIdentifiers=" + unboundResponseIdentifiers
                + ",invalidResponseIdentifiers=" + invalidResponseIdentifiers
                + ",uncommittedResponseValues=" + uncommittedResponseValues
                + ",templateValues=" + templateValues
                + ",responseValues=" + responseValues
                + ",outcomesValues=" + outcomeValues
                + ")";
    }
}
