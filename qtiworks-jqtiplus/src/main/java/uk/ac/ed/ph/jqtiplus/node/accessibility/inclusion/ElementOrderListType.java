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
package uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion;

import uk.ac.ed.ph.jqtiplus.exception.QtiIllegalChildException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * All the standard element order lists that may be found in an
 * apip:inclusionOrder.
 *
 * @author Zack Pierce
 */
public enum ElementOrderListType {
    ASL_DEFAULT_ORDER(AslDefaultOrder.QTI_CLASS_NAME, AslDefaultOrder.class) {

        @Override
        public ElementOrderList create(final InclusionOrder parent) {
            return new AslDefaultOrder(parent);
        }
    },

    ASL_ON_DEMAND_ORDER(AslOnDemandOrder.QTI_CLASS_NAME, AslOnDemandOrder.class) {

        @Override
        public ElementOrderList create(final InclusionOrder parent) {
            return new AslOnDemandOrder(parent);
        }
    },

    BRAILLE_DEFAULT_ORDER(BrailleDefaultOrder.QTI_CLASS_NAME, BrailleDefaultOrder.class) {

        @Override
        public ElementOrderList create(final InclusionOrder parent) {
            return new BrailleDefaultOrder(parent);
        }
    },

    GRAPHICS_ONLY_ON_DEMAND_ORDER(GraphicsOnlyOnDemandOrder.QTI_CLASS_NAME, GraphicsOnlyOnDemandOrder.class) {

        @Override
        public ElementOrderList create(final InclusionOrder parent) {
            return new GraphicsOnlyOnDemandOrder(parent);
        }
    },

    NON_VISUAL_DEFAULT_ORDER(NonVisualDefaultOrder.QTI_CLASS_NAME, NonVisualDefaultOrder.class) {

        @Override
        public ElementOrderList create(final InclusionOrder parent) {
            return new NonVisualDefaultOrder(parent);
        }
    },

    SIGNED_ENGLISH_DEFAULT_ORDER(SignedEnglishDefaultOrder.QTI_CLASS_NAME, SignedEnglishDefaultOrder.class) {

        @Override
        public ElementOrderList create(final InclusionOrder parent) {
            return new SignedEnglishDefaultOrder(parent);
        }
    },

    SIGNED_ENGLISH_ON_DEMAND_ORDER(SignedEnglishOnDemandOrder.QTI_CLASS_NAME, SignedEnglishOnDemandOrder.class) {

        @Override
        public ElementOrderList create(final InclusionOrder parent) {
            return new SignedEnglishOnDemandOrder(parent);
        }
    },

    TEXT_GRAPHICS_DEFAULT_ORDER(TextGraphicsDefaultOrder.QTI_CLASS_NAME, TextGraphicsDefaultOrder.class) {

        @Override
        public ElementOrderList create(final InclusionOrder parent) {
            return new TextGraphicsDefaultOrder(parent);
        }
    },

    TEXT_GRAPHICS_ON_DEMAND_ORDER(TextGraphicsOnDemandOrder.QTI_CLASS_NAME, TextGraphicsOnDemandOrder.class) {

        @Override
        public ElementOrderList create(final InclusionOrder parent) {
            return new TextGraphicsOnDemandOrder(parent);
        }
    },

    TEXT_ONLY_DEFAULT_ORDER(TextOnlyDefaultOrder.QTI_CLASS_NAME, TextOnlyDefaultOrder.class) {

        @Override
        public ElementOrderList create(final InclusionOrder parent) {
            return new TextOnlyDefaultOrder(parent);
        }
    },

    TEXT_ONLY_ON_DEMAND_ORDER(TextOnlyOnDemandOrder.QTI_CLASS_NAME, TextOnlyOnDemandOrder.class) {

        @Override
        public ElementOrderList create(final InclusionOrder parent) {
            return new TextOnlyOnDemandOrder(parent);
        }
    };

    public static final String DISPLAY_NAME = "StandardElementOrderListType";

    private static Map<String, ElementOrderListType> elementOrderListNameToTypes;

    static {
        elementOrderListNameToTypes = new HashMap<String, ElementOrderListType>();
        for (final ElementOrderListType type : ElementOrderListType.values()) {
            elementOrderListNameToTypes.put(type.qtiClassName, type);
        }

    }

    private String qtiClassName;

    private Class<? extends ElementOrderList> clazz;

    ElementOrderListType(final String type, final Class<? extends ElementOrderList> clazz) {
        this.qtiClassName = type;
        this.clazz = clazz;
    }

    public abstract ElementOrderList create(InclusionOrder parent);

    public Class<? extends ElementOrderList> getClazz() {
        return clazz;
    }

    public static ElementOrderList getElementOrderListInstance(final InclusionOrder parent, final String qtiClassName) {
        final ElementOrderListType elementOrderListType = elementOrderListNameToTypes.get(qtiClassName);

        if (elementOrderListType == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }

        return elementOrderListType.create(parent);

    }

    public static Set<String> getElementOrderListNames() {
        return elementOrderListNameToTypes.keySet();
    }
}
