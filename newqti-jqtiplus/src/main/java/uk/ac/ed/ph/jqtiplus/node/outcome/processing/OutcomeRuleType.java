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

package uk.ac.ed.ph.jqtiplus.node.outcome.processing;

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;

import java.util.HashMap;
import java.util.Map;


/**
 * This class creates all supported outcome rules from given CLASS_TAG.
 * <p>
 * Supported outcome rules: outcomeCondition, setOutcomeValue, exitTest.
 * <p>
 * Not implemented outcome rules: include.
 * 
 * @author Jiri Kajaba
 */
public enum OutcomeRuleType
{
    /**
     * Creates lookupOutcomeValue outcome rule.
     *
     * @see LookupOutcomeValue
     */
    LOOKUP_OUTCOME_VALUE (LookupOutcomeValue.CLASS_TAG)
    {
        @Override
        public OutcomeRule create(XmlNode parent)
        {
            return new LookupOutcomeValue(parent);
        }
    },

    /**
     * Creates outcomeCondition outcome rule.
     *
     * @see OutcomeCondition
     */
    OUTCOME_CONDITION (OutcomeCondition.CLASS_TAG)
    {
        @Override
        public OutcomeRule create(XmlNode parent)
        {
            return new OutcomeCondition(parent);
        }
    },

    /**
     * Creates outcomeProcessingFragment outcome rule.
     *
     * @see OutcomeProcessingFragment
     */
    OUTCOME_PROCESSING_FRAGMENT (OutcomeProcessingFragment.CLASS_TAG)
    {
        @Override
        public OutcomeRule create(XmlNode parent)
        {
            return new OutcomeProcessingFragment(parent);
        }
    },

    /**
     * Creates setOutcomeValue outcome rule.
     *
     * @see SetOutcomeValue
     */
    SET_OUTCOME_VALUE (SetOutcomeValue.CLASS_TAG)
    {
        @Override
        public OutcomeRule create(XmlNode parent)
        {
            return new SetOutcomeValue(parent);
        }
    },
    /**
     * Creates exitTest outcome rule.
     *
     * @see ExitTest
     */
    EXIT_TEST (ExitTest.CLASS_TAG)
    {
        @Override
        public OutcomeRule create(XmlNode parent)
        {
            return new ExitTest(parent);
        }
    };

    private static Map<String, OutcomeRuleType> outcomeRuleTypes;

    static
    {
        outcomeRuleTypes = new HashMap<String, OutcomeRuleType>();

        for (OutcomeRuleType outcomeRuleType : OutcomeRuleType.values())
            outcomeRuleTypes.put(outcomeRuleType.outcomeRuleType, outcomeRuleType);
    }

    private String outcomeRuleType;

    private OutcomeRuleType(String outcomeRuleType)
    {
        this.outcomeRuleType = outcomeRuleType;
    }

    /**
     * Creates outcome rule.
     *
     * @param parent parent of created outcome rule
     * @return created outcome rule
     */
    public abstract OutcomeRule create(XmlNode parent);

    @Override
    public String toString()
    {
        return outcomeRuleType;
    }

    /**
     * Creates outcome rule.
     *
     * @param parent parent of created outcome rule
     * @param classTag CLASS_TAG of created outcome rule
     * @return created outcome rule
     */
    public static OutcomeRule getInstance(XmlNode parent, String classTag)
    {
        OutcomeRuleType outcomeRuleType = outcomeRuleTypes.get(classTag);

        if (outcomeRuleType == null)
            throw new QTIParseException("Unsupported outcome rule: " + classTag);

        return outcomeRuleType.create(parent);
    }
}
