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

import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.CustomOperator;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.UnsupportedCustomOperator;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.CustomInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.UnsupportedCustomInteraction;

import java.util.Arrays;
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

    private final List<JqtiExtensionPackage<?>> extensionPackages;
    private final Map<String, ExtensionNamespaceInfo> extensionNamepaceInfoMap;

    public JqtiExtensionManager(final JqtiExtensionPackage<?>... jqtiExtensionPackages) {
        this(Arrays.asList(jqtiExtensionPackages));
    }

    public JqtiExtensionManager(final List<JqtiExtensionPackage<?>> jqtiExtensionPackages) {
        this.extensionPackages = ObjectUtilities.unmodifiableList(jqtiExtensionPackages);
        this.extensionNamepaceInfoMap = ObjectUtilities.unmodifiableMap(buildExtensionNamespaceInfoMap());
    }

    public List<JqtiExtensionPackage<?>> getExtensionPackages() {
        return extensionPackages;
    }

    public Map<String, ExtensionNamespaceInfo> getExtensionNamepaceInfoMap() {
        return extensionNamepaceInfoMap;
    }

    private Map<String, ExtensionNamespaceInfo> buildExtensionNamespaceInfoMap() {
        final Map<String, ExtensionNamespaceInfo> result = new HashMap<String, ExtensionNamespaceInfo>();
        for (final JqtiExtensionPackage<?> extensionPackage : extensionPackages) {
            for (final Entry<String, ExtensionNamespaceInfo> entry : extensionPackage.getNamespaceInfoMap().entrySet()) {
                final String namespaceUri = entry.getKey();
                if (QtiConstants.QTI_21_NAMESPACE_URI.equals(namespaceUri) || QtiConstants.QTI_20_NAMESPACE_URI.equals(namespaceUri)) {
                    throw new IllegalArgumentException("Namespace URI " + namespaceUri
                            + " is reserved for QTI and may not be used for extensions");
                }
                if (result.containsKey(namespaceUri)) {
                    throw new IllegalArgumentException("Namespace URI " + namespaceUri
                            + " is used by more than one extension in " + extensionPackages
                            + ". We can only support one at a time.");
                }
                final ExtensionNamespaceInfo extensionNamespaceInfo = entry.getValue();
                result.put(namespaceUri, extensionNamespaceInfo);
            }
        }
        return result;
    }

    //---------------------------------------------------------------------
    // Lifecycle methods

    public void init() {
        logger.info("Initialising all registered JqtiExtensionPackages");
        fireLifecycleEvent(this, LifecycleEventType.MANAGER_INITIALISED);
    }

    public void destroy() {
        logger.info("Destroying all registered JqtiExtensionPackages");
        fireLifecycleEvent(this, LifecycleEventType.MANAGER_DESTROYED);
    }

    /**
     * Fires off a lifecycle event to all registered extension packages.
     * <p>
     * This should only be used INTERNALLY.
     */
    public void fireLifecycleEvent(final Object source, final LifecycleEventType eventType) {
        for (final JqtiExtensionPackage<?> extensionPackage : extensionPackages) {
            extensionPackage.lifecycleEvent(source, eventType);
        }
    }

    //---------------------------------------------------------------------
    // Custom operators and interactions

    /**
     * Returns the {@link JqtiExtensionPackage} providing the customInteraction having the
     * given class attribute, or null if no extension package has been registered to provide this
     * interaction.
     *
     * @param interactionClassName
     * @return
     */
    public JqtiExtensionPackage<?> getJqtiExtensionPackageImplementingInteraction(final String interactionClassName) {
        for (final JqtiExtensionPackage<?> extensionPackage : extensionPackages) {
            if (extensionPackage.implementsCustomInteraction(interactionClassName)) {
                return extensionPackage;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <E extends JqtiExtensionPackage<E>> E getJqtiExtensionPackageImplementingInteraction(final CustomInteraction<E> customInteraction) {
        if (customInteraction instanceof UnsupportedCustomInteraction) {
            return (E) FallbackExtensionPackage.getInstance();
        }
        /* NB: customInteraction/@class can contain multiple values. We ignore anything other than one value */
        final List<String> classes = customInteraction.getClassAttr();
        if (classes.size()!=1) {
            return null;
        }
        return (E) getJqtiExtensionPackageImplementingInteraction(classes.get(0));
    }

    @SuppressWarnings("unchecked")
    public <E extends JqtiExtensionPackage<E>> E getJqtiExtensionPackageImplementingOperator(final CustomOperator<E> customOperator) {
        if (customOperator instanceof UnsupportedCustomOperator) {
            return (E) FallbackExtensionPackage.getInstance();
        }
        final String classAttr = customOperator.getClassAttr();
        return (E) getJqtiExtensionPackageImplementingOperator(classAttr);
    }

    public JqtiExtensionPackage<?> getJqtiExtensionPackageImplementingOperator(final String operatorClassName) {
        for (final JqtiExtensionPackage<?> extensionPackage : extensionPackages) {
            if (extensionPackage.implementsCustomOperator(operatorClassName)) {
                return extensionPackage;
            }
        }
        return null;
    }

    public CustomInteraction<?> createCustomInteraction(final XmlNode parentObject, final String interactionClassName) {
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
                + "(extensionPackages=" + extensionPackages
                + ")";
    }
}
