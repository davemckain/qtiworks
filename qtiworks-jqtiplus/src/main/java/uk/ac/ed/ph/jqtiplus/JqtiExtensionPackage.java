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
package uk.ac.ed.ph.jqtiplus;

import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.CustomOperator;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.CustomInteraction;

import java.util.Map;

/**
 * Interface for defining QTI extensions, such as MathAssess.
 * <p>
 * To create a QTI extension, you should implement this interface
 * and register it with your {@link JqtiExtensionManager}.
 * <p>
 * We currently support the <code>customOperator</code> and
 * <code>customInteraction</code> extension points. The QTI 2.1
 * specification does mention the possibility of having custom
 * selection & ordering classes but doesn't define how these should
 * work, so we don't support these yet.
 * <p>
 * See the <code>qtiworks-mathassess</code> package for a concrete
 * (and rather complex) example of a working package.
 *
 * @param <E> the final implementation of this class
 *
 * @author David McKain
 */
public interface JqtiExtensionPackage<E extends JqtiExtensionPackage<E>> extends JqtiLifecycleListener {

    /**
     * Return a displayable name for this extension package.
     * <p>
     * (This may change in future if we ever have some kind of centralised
     * registry for extensions...)
     */
    String getDisplayName();

    /**
     * Return details about each namespace used by this extension, in the form
     * of a {@link Map} keyed on namespace URI.
     * <p>
     * In JQTI+, it is illegal to register more than one package handling the same namespace URI.
     * <p>
     * This must not return null.
     */
    Map<String, ExtensionNamespaceInfo> getNamespaceInfoMap();

    /**
     * Return whether or not this extension package supports the
     * <code>customOperator</code> having the given "class" name.
     *
     * @param operatorClassName class name to test
     * @return true if this package supports this operator, false otherwise.
     */
    boolean implementsCustomOperator(String operatorClassName);

    /**
     * Return whether or not this extension package supports the
     * <code>customInteraction</code> having the given "class" name.
     *
     * @param interactionClassName class name to test
     * @return true if this package supports this interaction, false otherwise.
     */
    boolean implementsCustomInteraction(String interactionClassName);

    /**
     * Instantiate and return a new {@link CustomOperator} corresponding to the given class name.
     * <p>
     * Return null if this package does not support the stated class.
     *
     * @param expressionParent parent QTI object owning this operator
     * @param operatorClassName class name to instantiate
     */
    CustomOperator<E> createCustomOperator(ExpressionParent expressionParent, String operatorClassName);

    /**
     * Instantiate and return a new {@link CustomInteraction} corresponding to the given class name.
     * <p>
     * Return null if this package does not support the stated class.
     *
     * @param parentObject parent QTI object owning this interaction
     * @param operatorClassName class name to instantiates
     */
    CustomInteraction<E> createCustomInteraction(QtiNode parentObject, String interactionClassName);

}
