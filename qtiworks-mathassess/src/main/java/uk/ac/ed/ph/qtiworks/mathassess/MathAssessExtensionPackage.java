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
package uk.ac.ed.ph.qtiworks.mathassess;

import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.MATHASSESS_DEFAULT_NAMESPACE_PREFIX;
import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.MATHASSESS_NAMESPACE_URI;
import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.MATHASSESS_SCHEMA_LOCATION;

import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.MaximaLaunchHelper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.QtiMaximaProcess;
import uk.ac.ed.ph.qtiworks.mathassess.pooling.QtiMaximaProcessPoolManager;

import uk.ac.ed.ph.jqtiplus.ExtensionNamespaceInfo;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.JqtiLifecycleEventType;
import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.CustomOperator;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.CustomInteraction;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltStylesheetCache;

import uk.ac.ed.ph.jacomax.JacomaxRuntimeException;
import uk.ac.ed.ph.jacomax.MaximaConfiguration;
import uk.ac.ed.ph.snuggletex.utilities.StylesheetCache;

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
    private final ThreadLocal<QtiMaximaProcess> sessionThreadLocal;
    private final XsltStylesheetCache xsltStylesheetCache;
    private final StylesheetCache snuggleStylesheetCache;
    private final Set<String> customOperatorClasses;
    private final Set<String> customInteractionClasses;

    private QtiMaximaProcessPoolManager qtiMaximaProcessPoolManager;

    public MathAssessExtensionPackage(final XsltStylesheetCache xsltStylesheetCache) {
        this.xsltStylesheetCache = xsltStylesheetCache;
        this.snuggleStylesheetCache = new XsltStylesheetCacheAdapter(xsltStylesheetCache);

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
        this.sessionThreadLocal = new ThreadLocal<QtiMaximaProcess>();
    }

    public StylesheetCache getStylesheetCache() {
        return snuggleStylesheetCache;
    }

    public XsltStylesheetCache getXsltStylesheetCache() {
        return xsltStylesheetCache;
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
    public CustomInteraction<MathAssessExtensionPackage> createCustomInteraction(final QtiNode parentObject, final String interactionClassName) {
        if (MathAssessConstants.MATH_ENTRY_INTERACTION_CLASS.equals(interactionClassName)) {
            return new MathEntryInteraction(parentObject);
        }
        return null;
    }

    //------------------------------------------------------------------------

    @Override
    public void lifecycleEvent(final Object source, final JqtiLifecycleEventType eventType) {
        logger.trace("Received lifecycle event {}", eventType);
        switch (eventType) {
            case MANAGER_INITIALISED:
                startMaximaPool();
                break;

            case MANAGER_DESTROYED:
                closeMaximaPool();
                break;

            case ITEM_TEMPLATE_PROCESSING_STARTING:
            case ITEM_RESPONSE_PROCESSING_STARTING:
            case TEST_OUTCOME_PROCESSING_STARTING:
                /* Rather than creating a Maxima process at this point that may
                 * not be used,
                 * we'll wait until it is first needed. */
                break;

            case ITEM_TEMPLATE_PROCESSING_FINISHED:
            case ITEM_RESPONSE_PROCESSING_FINISHED:
            case TEST_OUTCOME_PROCESSING_FINISHED:
                releaseMaximaSessionForThread();
                break;

            default:
                break;
        }
    }

    private void startMaximaPool() {
        final MaximaConfiguration maximaConfiguration = MaximaLaunchHelper.tryMaximaConfiguration();
        if (maximaConfiguration==null) {
            logger.warn("Failed to obtain a MaximaConfiguration. MathAssess extensions will not work and this package should NOT be used.");
            return;
        }
        try {
            qtiMaximaProcessPoolManager = new QtiMaximaProcessPoolManager();
            qtiMaximaProcessPoolManager.setMaximaConfiguration(maximaConfiguration);
            qtiMaximaProcessPoolManager.setStylesheetCache(snuggleStylesheetCache);
            qtiMaximaProcessPoolManager.init();

            logger.info("MathAssessExtensionPackage successfully initiated using {} to handle communication with Maxima for MathAssess extensions", QtiMaximaProcessPoolManager.class.getSimpleName());
        }
        catch (final JacomaxRuntimeException e) {
            qtiMaximaProcessPoolManager = null;
            logger.warn("Failed to start the {}. MathAssess extensions will not work and this package should NOT be used",
                    QtiMaximaProcessPoolManager.class.getSimpleName());
        }
    }

    private void closeMaximaPool() {
        if (qtiMaximaProcessPoolManager != null) {
            logger.info("Closing {}", qtiMaximaProcessPoolManager);
            try {
                qtiMaximaProcessPoolManager.shutdown();
            }
            catch (final JacomaxRuntimeException e) {
                /* We'll log this but allow things to continue, as pool closure would normally happen on application exit */
                logger.warn("Failed to close the {}.", QtiMaximaProcessPoolManager.class.getSimpleName());
            }
        }
    }

    // ------------------------------------------------------------------------

    public QtiMaximaProcess obtainMaximaSessionForThread() {
        QtiMaximaProcess maximaSession = sessionThreadLocal.get();
        if (maximaSession == null) {
            if (qtiMaximaProcessPoolManager != null) {
                logger.debug("Obtaining new maxima process from pool for this request");
                /* Need to get a new process from pool */
                maximaSession = qtiMaximaProcessPoolManager.obtainProcess();
                sessionThreadLocal.set(maximaSession);
            }
            else {
                throw new QtiLogicException("The MathAssess extensions package could not be configured to communicate with Maxima. This package should not have been used in this case");
            }
        }
        return maximaSession;
    }

    private void releaseMaximaSessionForThread() {
        final QtiMaximaProcess maximaSession = sessionThreadLocal.get();
        if (maximaSession != null && qtiMaximaProcessPoolManager != null) {
            logger.debug("Finished with maxima process for this request - returning to pool");
            qtiMaximaProcessPoolManager.returnProcess(maximaSession);
            sessionThreadLocal.set(null);
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(displayName=" + getDisplayName()
                + ",stylesheetCache=" + xsltStylesheetCache
                + ",sessionThreadLocal=" + sessionThreadLocal
                + ",qtiMaximaProcessPoolManager=" + qtiMaximaProcessPoolManager
                + ")";
    }
}
