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
package org.qtitools.mathassess;

import static org.qtitools.mathassess.MathAssessConstants.MATHASSESS_DEFAULT_NAMESPACE_PREFIX;
import static org.qtitools.mathassess.MathAssessConstants.MATHASSESS_NAMESPACE_URI;
import static org.qtitools.mathassess.MathAssessConstants.MATHASSESS_SCHEMA_LOCATION;

import uk.ac.ed.ph.jqtiplus.ExtensionNamespaceInfo;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.LifecycleEventType;
import uk.ac.ed.ph.jqtiplus.exception.QtiEvaluationException;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.CustomOperator;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.CustomInteraction;

import uk.ac.ed.ph.jacomax.JacomaxConfigurationException;
import uk.ac.ed.ph.jacomax.JacomaxSimpleConfigurator;
import uk.ac.ed.ph.jacomax.MaximaConfiguration;
import uk.ac.ed.ph.snuggletex.utilities.StylesheetCache;

import org.qtitools.mathassess.tools.glue.extras.pooling.PooledQTIMaximaSessionManager;
import org.qtitools.mathassess.tools.qticasbridge.maxima.QTIMaximaSession;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the {@link CustomOperator}s and {@link CustomInteraction}s that make
 * up the package of MathAssess QTI extensions.
 * <p>
 * Note the lifecycle {@link #startMaximaPool()} and {@link #shutdown()} methods here.
 *
 * @author David McKain
 */
public final class MathAssessExtensionPackage implements JqtiExtensionPackage<MathAssessExtensionPackage> {

    private static final Logger logger = LoggerFactory.getLogger(MathAssessExtensionPackage.class);

    public static final String DISPLAY_NAME = "MathAssess QTI Extensions";

    private final Map<String, ExtensionNamespaceInfo> namespaceInfoMap;
    private final ThreadLocal<QTIMaximaSession> sessionThreadLocal;
    private final StylesheetCache stylesheetCache;
    private final Set<String> customOperatorClasses;
    private final Set<String> customInteractionClasses;

    private PooledQTIMaximaSessionManager pooledQTIMaximaSessionManager;

    public MathAssessExtensionPackage(final StylesheetCache stylesheetCache) {
        this.stylesheetCache = stylesheetCache;

        /* Build up namespace info */
        final ExtensionNamespaceInfo extensionNamespaceInfo = new ExtensionNamespaceInfo(MATHASSESS_NAMESPACE_URI, MATHASSESS_SCHEMA_LOCATION, MATHASSESS_DEFAULT_NAMESPACE_PREFIX);
        final Map<String, ExtensionNamespaceInfo> namespaceInfoMapSource = new HashMap<String, ExtensionNamespaceInfo>();
        namespaceInfoMapSource.put(extensionNamespaceInfo.getNamespaceUri(), extensionNamespaceInfo);
        this.namespaceInfoMap = ObjectUtilities.unmodifiableMap(namespaceInfoMapSource);

        /* Document supported operators & interactions */
        this.customInteractionClasses = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
                MathAssessConstants.MATH_ENTRY_INTERACTION_CLASS
        )));
        this.customOperatorClasses = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
                MathAssessConstants.CAS_COMPARE_CLASS,
                MathAssessConstants.CAS_CONDITION_CLASS,
                MathAssessConstants.CAS_PROCESS_CLASS,
                MathAssessConstants.SCRIPT_RULE_CLASS
        )));

        /* Create ThreadLocal for communicating with maxima */
        this.sessionThreadLocal = new ThreadLocal<QTIMaximaSession>();
    }

    public StylesheetCache getStylesheetCache() {
        return stylesheetCache;
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public Map<String,ExtensionNamespaceInfo> getNamespaceInfoMap() {
        return namespaceInfoMap;
    }

    @Override
    public boolean implementsCustomInteraction(final String interactionClassName) {
        return customInteractionClasses.contains(interactionClassName);
    }

    @Override
    public boolean implementsCustomOperator(final String operatorClassName) {
        return customOperatorClasses.contains(operatorClassName);
    }

    @Override
    public Set<String> getImplementedCustomInteractionClasses() {
        return customInteractionClasses;
    }

    @Override
    public Set<String> getImplementedCustomOperatorClasses() {
        return customOperatorClasses;
    }

    @Override
    public CustomOperator<MathAssessExtensionPackage> createCustomOperator(final ExpressionParent expressionParent, final String operatorClassName) {
        if (MathAssessConstants.CAS_COMPARE_CLASS.equals(operatorClassName)) {
            return new CasCompare(expressionParent);
        }
        else if (MathAssessConstants.CAS_CONDITION_CLASS.equals(operatorClassName)) {
            return new CasCondition(expressionParent);
        }
        else if (MathAssessConstants.CAS_PROCESS_CLASS.equals(operatorClassName)) {
            return new CasProcess(expressionParent);
        }
        else if (MathAssessConstants.SCRIPT_RULE_CLASS.equals(operatorClassName)) {
            return new ScriptRule(expressionParent);
        }
        return null;
    }

    @Override
    public CustomInteraction<MathAssessExtensionPackage> createCustomInteraction(final XmlNode parentObject, final String interactionClassName) {
        if (MathAssessConstants.MATH_ENTRY_INTERACTION_CLASS.equals(interactionClassName)) {
            return new MathEntryInteraction(parentObject);
        }
        return null;
    }


    // ------------------------------------------------------------------------

    @Override
    public void lifecycleEvent(final Object source, final LifecycleEventType eventType) {
        logger.trace("Received lifecycle event {}", eventType);
        switch (eventType) {
            case MANAGER_INITIALISED:
                startMaximaPool();
                break;

            case MANAGER_DESTROYED:
                closeMaximaPool();
                break;

            case ITEM_INITIALISATION_STARTING:
            case ITEM_RESPONSE_PROCESSING_STARTING:
                /* Rather than creating a Maxima session at this point that may
                 * not be used,
                 * we'll wait until it is first needed. */
                break;

            case ITEM_INITIALISATION_FINISHED:
            case ITEM_RESPONSE_PROCESSING_FINISHED:
                releaseMaximaSessionForThread();
                break;

            default:
                break;
        }
    }

    private void startMaximaPool() {
        try {
            final MaximaConfiguration maximaConfiguration = JacomaxSimpleConfigurator.configure();

            pooledQTIMaximaSessionManager = new PooledQTIMaximaSessionManager();
            pooledQTIMaximaSessionManager.setMaximaConfiguration(maximaConfiguration);
            pooledQTIMaximaSessionManager.setStylesheetCache(stylesheetCache);
            pooledQTIMaximaSessionManager.init();

            logger.info("MathAssessExtensionPackage successfully initiated using {} to handle communication with Maxima for MathAssess extensions", PooledQTIMaximaSessionManager.class.getSimpleName());
        }
        catch (final JacomaxConfigurationException e) {
            pooledQTIMaximaSessionManager = null;
            logger.warn("Failed to obtain a MaximaConfiguration and/or {}. MathAssess extensions will not work and this package should NOT be used",
                    PooledQTIMaximaSessionManager.class.getSimpleName());
        }
    }

    private void closeMaximaPool() {
        if (pooledQTIMaximaSessionManager != null) {
            logger.info("Closing {}", pooledQTIMaximaSessionManager);
            pooledQTIMaximaSessionManager.shutdown();
        }
    }

    // ------------------------------------------------------------------------

    public QTIMaximaSession obtainMaximaSessionForThread() {
        QTIMaximaSession maximaSession = sessionThreadLocal.get();
        if (maximaSession == null) {
            if (pooledQTIMaximaSessionManager != null) {
                logger.info("Obtaining new maxima session from pool for this request");
                /* Need to get a new session from pool */
                maximaSession = pooledQTIMaximaSessionManager.obtainSession();
                sessionThreadLocal.set(maximaSession);
            }
            else {
                throw new QtiEvaluationException(
                        "The MathAssess extensions package could not be configured to communicate with Maxima. This package should not have been used in this case");
            }
        }
        return maximaSession;
    }

    private void releaseMaximaSessionForThread() {
        final QTIMaximaSession maximaSession = sessionThreadLocal.get();
        if (maximaSession != null && pooledQTIMaximaSessionManager != null) {
            logger.info("Finished with maxima session for this request - returning to pool");
            pooledQTIMaximaSessionManager.returnSession(maximaSession);
            sessionThreadLocal.set(null);
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(displayName=" + getDisplayName()
                + ",stylesheetCache=" + stylesheetCache
                + ",sessionThreadLocal=" + sessionThreadLocal
                + ",pooledQTIMaximaSessionManager=" + pooledQTIMaximaSessionManager
                + ")";
    }
}
