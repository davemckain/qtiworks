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
package uk.ac.ed.ph.jqtiplus.node.test.outcome.processing;

import uk.ac.ed.ph.jqtiplus.exception.QtiIllegalChildException;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class creates all supported outcome rules from given QTI_CLASS_NAME.
 * <p>
 * Supported outcome rules: outcomeCondition, setOutcomeValue, exitTest.
 * <p>
 * Not implemented outcome rules: include.
 *
 * @author Jiri Kajaba
 */
public enum OutcomeRuleType {
    /**
     * Creates lookupOutcomeValue outcome rule.
     *
     * @see LookupOutcomeValue
     */
    LOOKUP_OUTCOME_VALUE(LookupOutcomeValue.QTI_CLASS_NAME) {

        @Override
        public OutcomeRule create(final QtiNode parent) {
            return new LookupOutcomeValue(parent);
        }
    },

    /**
     * Creates outcomeCondition outcome rule.
     *
     * @see OutcomeCondition
     */
    OUTCOME_CONDITION(OutcomeCondition.QTI_CLASS_NAME) {

        @Override
        public OutcomeRule create(final QtiNode parent) {
            return new OutcomeCondition(parent);
        }
    },

    /**
     * Creates outcomeProcessingFragment outcome rule.
     *
     * @see OutcomeProcessingFragment
     */
    OUTCOME_PROCESSING_FRAGMENT(OutcomeProcessingFragment.QTI_CLASS_NAME) {

        @Override
        public OutcomeRule create(final QtiNode parent) {
            return new OutcomeProcessingFragment(parent);
        }
    },

    /**
     * Creates setOutcomeValue outcome rule.
     *
     * @see SetOutcomeValue
     */
    SET_OUTCOME_VALUE(SetOutcomeValue.QTI_CLASS_NAME) {

        @Override
        public OutcomeRule create(final QtiNode parent) {
            return new SetOutcomeValue(parent);
        }
    },
    /**
     * Creates exitTest outcome rule.
     *
     * @see ExitTest
     */
    EXIT_TEST(ExitTest.QTI_CLASS_NAME) {

        @Override
        public OutcomeRule create(final QtiNode parent) {
            return new ExitTest(parent);
        }
    };

    private static Map<String, OutcomeRuleType> outcomeRuleTypes;

    static {
        outcomeRuleTypes = new HashMap<String, OutcomeRuleType>();

        for (final OutcomeRuleType outcomeRuleType : OutcomeRuleType.values()) {
            outcomeRuleTypes.put(outcomeRuleType.outcomeRuleType, outcomeRuleType);
        }
    }

    private String outcomeRuleType;

    private OutcomeRuleType(final String outcomeRuleType) {
        this.outcomeRuleType = outcomeRuleType;
    }

    /**
     * Creates outcome rule.
     *
     * @param parent parent of created outcome rule
     * @return created outcome rule
     */
    public abstract OutcomeRule create(QtiNode parent);

    @Override
    public String toString() {
        return outcomeRuleType;
    }

    /**
     * Creates outcome rule.
     *
     * @param parent parent of created outcome rule
     * @param qtiClassName QTI_CLASS_NAME of created outcome rule
     * @return created outcome rule
     */
    public static OutcomeRule getInstance(final QtiNode parent, final String qtiClassName) {
        final OutcomeRuleType outcomeRuleType = outcomeRuleTypes.get(qtiClassName);

        if (outcomeRuleType == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }

        return outcomeRuleType.create(parent);
    }

    public static Set<String> getQtiClassNames() {
        return outcomeRuleTypes.keySet();
    }
}
