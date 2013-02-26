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
package uk.ac.ed.ph.jqtiplus.node.item.response.processing;

import uk.ac.ed.ph.jqtiplus.exception.QtiIllegalChildException;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class creates all supported response rules from given QTI_CLASS_NAME.
 * <p>
 * Supported response rules: responseCondition, setOutcomeValue, responseProcessingFragment, responseCondition, exitResponse.
 * <p>
 * Not implemented response rules: include.
 *
 * @author Jonathon Hare
 */
public enum ResponseRuleType {
    /**
     * Creates lookupOutcomeValue response rule.
     *
     * @see LookupOutcomeValue
     */
    LOOKUP_OUTCOME_VALUE(LookupOutcomeValue.QTI_CLASS_NAME) {

        @Override
        public ResponseRule create(final QtiNode parent) {
            return new LookupOutcomeValue(parent);
        }
    },

    /**
     * Creates responseCondition response rule.
     *
     * @see ResponseCondition
     */
    RESPONSE_CONDITION(ResponseCondition.QTI_CLASS_NAME) {

        @Override
        public ResponseRule create(final QtiNode parent) {
            return new ResponseCondition(parent);
        }
    },

    /**
     * Creates responseProcessingFragment response rule.
     *
     * @see ResponseProcessingFragment
     */
    RESPONSE_PROCESSING_FRAGMENT(ResponseProcessingFragment.QTI_CLASS_NAME) {

        @Override
        public ResponseRule create(final QtiNode parent) {
            return new ResponseProcessingFragment(parent);
        }
    },

    /**
     * Creates setOutcomeValue response rule.
     *
     * @see SetOutcomeValue
     */
    SET_OUTCOME_VALUE(SetOutcomeValue.QTI_CLASS_NAME) {

        @Override
        public ResponseRule create(final QtiNode parent) {
            return new SetOutcomeValue(parent);
        }
    },

    /**
     * Creates exitResponse response rule.
     *
     * @see SetOutcomeValue
     */
    EXIT_RESPONSE(ExitResponse.QTI_CLASS_NAME) {

        @Override
        public ResponseRule create(final QtiNode parent) {
            return new ExitResponse(parent);
        }
    };

    private static Map<String, ResponseRuleType> responseRuleTypes;

    static {
        responseRuleTypes = new HashMap<String, ResponseRuleType>();

        for (final ResponseRuleType responseRuleType : ResponseRuleType.values()) {
            responseRuleTypes.put(responseRuleType.responseRuleType, responseRuleType);
        }
    }

    private String responseRuleType;

    private ResponseRuleType(final String responseRuleType) {
        this.responseRuleType = responseRuleType;
    }

    /**
     * Creates response rule.
     *
     * @param parent parent of created response rule
     * @return created response rule
     */
    public abstract ResponseRule create(QtiNode parent);

    @Override
    public String toString() {
        return responseRuleType;
    }

    /**
     * Creates response rule.
     *
     * @param parent parent of created response rule
     * @param qtiClassName QTI_CLASS_NAME of created response rule
     * @return created response rule
     */
    public static ResponseRule getInstance(final QtiNode parent, final String qtiClassName) {
        final ResponseRuleType responseRuleType = responseRuleTypes.get(qtiClassName);

        if (responseRuleType == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }

        return responseRuleType.create(parent);
    }

    public static Set<String> getQtiClassNames() {
        return responseRuleTypes.keySet();
    }
}
