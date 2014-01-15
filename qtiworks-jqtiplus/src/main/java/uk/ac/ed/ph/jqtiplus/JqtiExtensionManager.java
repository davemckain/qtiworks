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

import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.CustomOperator;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.UnsupportedCustomOperator;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.CustomInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.UnsupportedCustomInteraction;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registry/manager for all JQTI extensions you choose to support.
 * <p>
 * Once created, all properties of this manager are unmodifiable and safe to use by multiple threads.
 *
 * @author David McKain
 */
public final class JqtiExtensionManager {

    private static final Logger logger = LoggerFactory.getLogger(JqtiExtensionManager.class);

    private final List<JqtiExtensionPackage<?>> jqtiExtensionPackages;
    private final Map<String, ExtensionNamespaceInfo> extensionNamepaceInfoMap;

    public JqtiExtensionManager(final JqtiExtensionPackage<?>... jqtiExtensionPackages) {
        this(Arrays.asList(jqtiExtensionPackages));
    }

    public JqtiExtensionManager(final List<JqtiExtensionPackage<?>> jqtiExtensionPackages) {
        this.jqtiExtensionPackages = ObjectUtilities.unmodifiableList(jqtiExtensionPackages);
        this.extensionNamepaceInfoMap = ObjectUtilities.unmodifiableMap(buildExtensionNamespaceInfoMap());
    }

    public List<JqtiExtensionPackage<?>> getExtensionPackages() {
        return jqtiExtensionPackages;
    }

    public Map<String, ExtensionNamespaceInfo> getExtensionNamepaceInfoMap() {
        return extensionNamepaceInfoMap;
    }

    private Map<String, ExtensionNamespaceInfo> buildExtensionNamespaceInfoMap() {
        final Map<String, ExtensionNamespaceInfo> result = new HashMap<String, ExtensionNamespaceInfo>();
        for (final JqtiExtensionPackage<?> extensionPackage : jqtiExtensionPackages) {
            for (final Entry<String, ExtensionNamespaceInfo> entry : extensionPackage.getNamespaceInfoMap().entrySet()) {
                final String namespaceUri = entry.getKey();
                if (QtiConstants.QTI_21_NAMESPACE_URI.equals(namespaceUri)
                        || QtiConstants.QTI_20_NAMESPACE_URI.equals(namespaceUri)
                        || QtiConstants.QTI_RESULT_21_NAMESPACE_URI.equals(namespaceUri)) {
                    throw new IllegalArgumentException("Namespace URI " + namespaceUri
                            + " is reserved for QTI and may not be used for extensions");
                }
                if (result.containsKey(namespaceUri)) {
                    throw new IllegalArgumentException("Namespace URI " + namespaceUri
                            + " is used by more than one extension in " + jqtiExtensionPackages
                            + ". We can only support one at a time.");
                }
                final ExtensionNamespaceInfo extensionNamespaceInfo = entry.getValue();
                result.put(namespaceUri, extensionNamespaceInfo);
            }
        }
        return result;
    }

    //---------------------------------------------------------------------
    // Extension package lifecycle management

    /**
     * Allows all registered {@link JqtiExtensionPackage}s to initialise themselves.
     * <p>
     * This MUST be called before the {@link JqtiExtensionManager} is used if any extensions
     * have been registered. If no packages have been registered, there is no need to call this
     * method.
     */
    public void init() {
        logger.info("Initialising all registered JqtiExtensionPackages");
        fireJqtiLifecycleEvent(this, JqtiLifecycleEventType.MANAGER_INITIALISED);
    }

    /**
     * Allows all registered {@link JqtiExtensionPackage}s to safely destroy themselves.
     * <p>
     * This MUST be called at a suitable time if any extensions have been registered to allow
     * them to perform any required clean-up. If no packages have been registered,
     * there is no need to call this method.
     */
    public void destroy() {
        logger.info("Destroying all registered JqtiExtensionPackages");
        fireJqtiLifecycleEvent(this, JqtiLifecycleEventType.MANAGER_DESTROYED);
    }

    /**
     * Fires off a lifecycle event to all registered extension packages.
     * <p>
     * This should only be used INTERNALLY.
     */
    private void fireJqtiLifecycleEvent(final Object source, final JqtiLifecycleEventType eventType) {
        for (final JqtiExtensionPackage<?> jqtiExtensionPackage : jqtiExtensionPackages) {
            jqtiExtensionPackage.lifecycleEvent(source, eventType);
        }
    }

    //---------------------------------------------------------------------
    // Custom operators and interactions

    /**
     * Returns the {@link JqtiExtensionPackage} providing the {@link CustomInteraction} having the
     * given class attribute, or null if no extension package has been registered to provide this
     * interaction.
     */
    public JqtiExtensionPackage<?> getJqtiExtensionPackageImplementingInteraction(final String interactionClassName) {
        for (final JqtiExtensionPackage<?> extensionPackage : jqtiExtensionPackages) {
            if (extensionPackage.implementsCustomInteraction(interactionClassName)) {
                return extensionPackage;
            }
        }
        return null;
    }

    /**
     * Returns the {@link JqtiExtensionPackage} providing the given {@link CustomInteraction} having the
     * given class attribute. Returns null for an {@link UnsupportedCustomInteraction} or if
     * the interaction's class attribute contains multiple entries.
     */
    @SuppressWarnings("unchecked")
    public <E extends JqtiExtensionPackage<E>> E getJqtiExtensionPackageImplementingInteraction(final CustomInteraction<E> customInteraction) {
        /* NB: customInteraction/@class can contain multiple values. We ignore anything other than one value */
        final List<String> classes = customInteraction.getClassAttr();
        if (classes==null || classes.size()!=1) {
            return null;
        }
        return (E) getJqtiExtensionPackageImplementingInteraction(classes.get(0));
    }

    @SuppressWarnings("unchecked")
    public <E extends JqtiExtensionPackage<E>> E getJqtiExtensionPackageImplementingOperator(final CustomOperator<E> customOperator) {
        final String classAttr = customOperator.getClassAttr();
        if (classAttr==null) {
            return null;
        }
        return (E) getJqtiExtensionPackageImplementingOperator(classAttr);
    }

    public JqtiExtensionPackage<?> getJqtiExtensionPackageImplementingOperator(final String operatorClassName) {
        for (final JqtiExtensionPackage<?> extensionPackage : jqtiExtensionPackages) {
            if (extensionPackage.implementsCustomOperator(operatorClassName)) {
                return extensionPackage;
            }
        }
        return null;
    }

    public CustomInteraction<?> createCustomInteraction(final QtiNode parentObject, final String interactionClassName) {
        CustomInteraction<?> result = null;
        final JqtiExtensionPackage<?> extensionPackage = getJqtiExtensionPackageImplementingInteraction(interactionClassName);
        if (extensionPackage!=null) {
            result = extensionPackage.createCustomInteraction(parentObject, interactionClassName);
            if (result == null) {
                throw new QtiLogicException("JqtiExtensionPackage claimed to support customInteraction class " + interactionClassName
                        + " but did not create the required CustomOperator Object when requested");
            }
        }
        else {
            logger.debug("customInteraction of class {} not supported by any registered package. Using fallback placeholder", interactionClassName);
            result = FallbackExtensionPackage.getInstance().createCustomInteraction(parentObject, interactionClassName);
        }
        return result;
    }

    public CustomOperator<?> createCustomOperator(final ExpressionParent expressionParent, final String operatorClassName) {
        CustomOperator<?> result;
        final JqtiExtensionPackage<?> extensionPackage = getJqtiExtensionPackageImplementingOperator(operatorClassName);
        if (extensionPackage!=null) {
            result = extensionPackage.createCustomOperator(expressionParent, operatorClassName);
            if (result == null) {
                throw new QtiLogicException("JqtiExtensionPackage claimed to support customOperator class " + operatorClassName
                        + " but did not create the required CustomOperator Object when requested");
            }
        }
        else {
            logger.debug("customOperator of class {} not supported by any registered package. Using fallback placeholder", operatorClassName);
            result = FallbackExtensionPackage.getInstance().createCustomOperator(expressionParent, operatorClassName);
        }
        return result;
    }

    //---------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(extensionPackages=" + jqtiExtensionPackages
                + ")";
    }

    //---------------------------------------------------------------------

    /**
     * Internal {@link JqtiExtensionPackage} that becomes the owner of any unsupported
     * {@link CustomOperator}s and {@link CustomInteraction}s. This is never exposed via the API.
     */
    public static final class FallbackExtensionPackage implements JqtiExtensionPackage<FallbackExtensionPackage> {

        private static final FallbackExtensionPackage instance = new FallbackExtensionPackage();

        public static final String DISPLAY_NAME = "Fallback for unsupported customOperators and customInteractions";

        FallbackExtensionPackage() {
        }

        public static FallbackExtensionPackage getInstance() {
            return instance;
        }

        @Override
        public void lifecycleEvent(final Object source, final JqtiLifecycleEventType eventType) {
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
        public CustomOperator<FallbackExtensionPackage> createCustomOperator(final ExpressionParent expressionParent, final String operatorClassName) {
            return new UnsupportedCustomOperator(expressionParent);
        }

        @Override
        public CustomInteraction<FallbackExtensionPackage> createCustomInteraction(final QtiNode parentObject, final String interactionClassName) {
            return new UnsupportedCustomInteraction(parentObject);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this)) + "()";
        }

    }
}
