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
package uk.ac.ed.ph.jqtiplus;

import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.CustomOperator;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.UnsupportedCustomOperator;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.CustomInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.UnsupportedCustomInteraction;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * This {@link JqtiExtensionPackage} becomes the owner of any unsupported
 * {@link CustomOperator}s and {@link CustomInteraction}s.
 * <p>
 * This is used internally only.
 *
 * @author David McKain
 */
public final class FallbackExtensionPackage implements JqtiExtensionPackage<FallbackExtensionPackage> {

    private static final FallbackExtensionPackage instance = new FallbackExtensionPackage();

    public static final String DISPLAY_NAME = "Fallback for unsupported customOperators and customInteractions";

    FallbackExtensionPackage() {
    }

    public static FallbackExtensionPackage getInstance() {
        return instance;
    }

    @Override
    public void lifecycleEvent(final Object source, final LifecycleEventType eventType) {
        /* Do nothing */
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public Map<String, ExtensionNamespaceInfo> getNamespaceInfoMap() {
        return Collections.emptyMap();
    }

    @Override
    public boolean implementsCustomOperator(final String operatorClassName) {
        return true;
    }

    @Override
    public boolean implementsCustomInteraction(final String interactionClassName) {
        return true;
    }

    @Override
    public Set<String> getImplementedCustomOperatorClasses() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getImplementedCustomInteractionClasses() {
        return Collections.emptySet();
    }

    @Override
    public CustomOperator<FallbackExtensionPackage> createCustomOperator(final ExpressionParent expressionParent, final String operatorClassName) {
        return new UnsupportedCustomOperator(expressionParent);
    }

    @Override
    public CustomInteraction<FallbackExtensionPackage> createCustomInteraction(final XmlNode parentObject, final String interactionClassName) {
        return new UnsupportedCustomInteraction(parentObject);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this)) + "()";
    }

}
