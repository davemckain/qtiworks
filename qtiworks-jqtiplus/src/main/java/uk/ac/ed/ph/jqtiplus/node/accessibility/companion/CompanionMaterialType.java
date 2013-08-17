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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.node.accessibility.companion;

import uk.ac.ed.ph.jqtiplus.exception.QtiIllegalChildException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Enumeration of the types of companion material elements that
 * may be found within a standard CompanionMaterialsInfo element:
 * Calculator, DigitalMaterial, PhysicalMaterial, Protractor, ReadingPassage, Rule
 *
 * @author Zack Pierce
 */
public enum CompanionMaterialType {

    CALCULATOR(Calculator.QTI_CLASS_NAME, Calculator.class) {

        @Override
        public CompanionMaterial create(final CompanionMaterialsInfo parent) {
            return new Calculator(parent);
        }

    },

    DIGITAL_MATERIAL(DigitalMaterial.QTI_CLASS_NAME, DigitalMaterial.class) {

        @Override
        public CompanionMaterial create(final CompanionMaterialsInfo parent) {
            return new DigitalMaterial(parent);
        }

    },

    PHYSICAL_MATERIAL(PhysicalMaterial.QTI_CLASS_NAME, PhysicalMaterial.class) {

        @Override
        public CompanionMaterial create(final CompanionMaterialsInfo parent) {
            return new PhysicalMaterial(parent);
        }

    },

    PROTRACTOR(Protractor.QTI_CLASS_NAME, Protractor.class) {

        @Override
        public CompanionMaterial create(final CompanionMaterialsInfo parent) {
            return new Protractor(parent);
        }

    },

    READING_PASSAGE(ReadingPassage.QTI_CLASS_NAME, ReadingPassage.class) {

        @Override
        public CompanionMaterial create(final CompanionMaterialsInfo parent) {
            return new ReadingPassage(parent);
        }

    },

    RULE(Rule.QTI_CLASS_NAME, Rule.class) {

        @Override
        public CompanionMaterial create(final CompanionMaterialsInfo parent) {
            return new Rule(parent);
        }

    };

    private static Map<String, CompanionMaterialType> companionMaterialNameToTypes;

    static {
        companionMaterialNameToTypes = new HashMap<String, CompanionMaterialType>();
        for (final CompanionMaterialType type : CompanionMaterialType.values()) {
            companionMaterialNameToTypes.put(type.qtiClassName, type);
        }

    }

    private String qtiClassName;

    private Class<? extends CompanionMaterial> clazz;

    CompanionMaterialType(final String type, final Class<? extends CompanionMaterial> clazz) {
        this.qtiClassName = type;
        this.clazz = clazz;
    }

    public abstract CompanionMaterial create(CompanionMaterialsInfo parent);

    public Class<? extends CompanionMaterial> getClazz() {
        return clazz;
    }

    public static CompanionMaterial getCompanionMaterialInstance(final CompanionMaterialsInfo parent, final String qtiClassName) {
        final CompanionMaterialType companionMaterialType = companionMaterialNameToTypes.get(qtiClassName);

        if (companionMaterialType == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }

        return companionMaterialType.create(parent);

    }

    public static Set<String> getCompanionMaterialNames() {
        return companionMaterialNameToTypes.keySet();
    }
}
