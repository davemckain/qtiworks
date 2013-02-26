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
package uk.ac.ed.ph.jqtiplus.node.item.template.processing;

import uk.ac.ed.ph.jqtiplus.exception.QtiIllegalChildException;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class creates all supported template rules from given QTI_CLASS_NAME.
 * <p>
 * Supported template rules: templateCondition, setTemplateValue, exitTemplate, setCorrectResponse, setDefaultValue.
 *
 * @author Jonathon Hare
 */
public enum TemplateRuleType {
    /**
     * Creates templateCondition template rule.
     *
     * @see TemplateCondition
     */
    TEMPLATE_CONDITION(TemplateCondition.QTI_CLASS_NAME) {

        @Override
        public TemplateRule create(final QtiNode parent) {
            return new TemplateCondition(parent);
        }
    },

    /**
     * Creates setTemplateValue template rule.
     *
     * @see SetTemplateValue
     */
    SET_TEMPLATE_VALUE(SetTemplateValue.QTI_CLASS_NAME) {

        @Override
        public TemplateRule create(final QtiNode parent) {
            return new SetTemplateValue(parent);
        }
    },

    /**
     * Creates exitTemplate template rule.
     *
     * @see ExitTemplate
     */
    EXIT_TEMPLATE(ExitTemplate.QTI_CLASS_NAME) {

        @Override
        public TemplateRule create(final QtiNode parent) {
            return new ExitTemplate(parent);
        }
    },

    /**
     * Creates setCorrectResponse template rule.
     *
     * @see SetCorrectResponse
     */
    SET_CORRECT_RESPONSE(SetCorrectResponse.QTI_CLASS_NAME) {

        @Override
        public TemplateRule create(final QtiNode parent) {
            return new SetCorrectResponse(parent);
        }
    },

    /**
     * Creates setDefaultValue template rule.
     *
     * @see SetDefaultValue
     */
    SET_DEFAULT_VALUE(SetDefaultValue.QTI_CLASS_NAME) {

        @Override
        public TemplateRule create(final QtiNode parent) {
            return new SetDefaultValue(parent);
        }
    };

    private static Map<String, TemplateRuleType> templateRuleTypes;

    static {
        templateRuleTypes = new HashMap<String, TemplateRuleType>();

        for (final TemplateRuleType templateRuleType : TemplateRuleType.values()) {
            templateRuleTypes.put(templateRuleType.templateRuleType, templateRuleType);
        }
    }

    private String templateRuleType;

    private TemplateRuleType(final String templateRuleType) {
        this.templateRuleType = templateRuleType;
    }

    /**
     * Creates template rule.
     *
     * @param parent parent of created template rule
     * @return created template rule
     */
    public abstract TemplateRule create(QtiNode parent);

    @Override
    public String toString() {
        return templateRuleType;
    }

    /**
     * Creates template rule.
     *
     * @param parent parent of created template rule
     * @param qtiClassName QTI_CLASS_NAME of created template rule
     * @return created template rule
     */
    public static TemplateRule getInstance(final QtiNode parent, final String qtiClassName) {
        final TemplateRuleType templateRuleType = templateRuleTypes.get(qtiClassName);

        if (templateRuleType == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }

        return templateRuleType.create(parent);
    }

    public static Set<String> getQtiClassNames() {
        return templateRuleTypes.keySet();
    }
}
