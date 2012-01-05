/* $Id: MathAssessExtensionPackage.java 2775 2011-08-15 07:59:08Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package org.qtitools.mathassess;

import uk.ac.ed.ph.jqtiplus.control.JQTIExtensionPackage;
import uk.ac.ed.ph.jqtiplus.control.LifecycleEventType;
import uk.ac.ed.ph.jqtiplus.exception.QTIEvaluationException;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.CustomOperator;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.CustomInteraction;

import org.qtitools.mathassess.tools.glue.extras.pooling.PooledQTIMaximaSessionManager;
import org.qtitools.mathassess.tools.qticasbridge.maxima.QTIMaximaSession;

import uk.ac.ed.ph.jacomax.JacomaxConfigurationException;
import uk.ac.ed.ph.jacomax.JacomaxSimpleConfigurator;
import uk.ac.ed.ph.jacomax.MaximaConfiguration;
import uk.ac.ed.ph.snuggletex.utilities.StylesheetCache;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the {@link CustomOperator}s and {@link CustomInteraction}s that make up
 * the package of MathAssess QTI extensions.
 * 
 * @author  David McKain
 * @version $Revision: 2775 $
 */
public final class MathAssessExtensionPackage implements JQTIExtensionPackage {
    
    private static final Logger logger = LoggerFactory.getLogger(MathAssessExtensionPackage.class);
    
    private final ThreadLocal<QTIMaximaSession> sessionThreadLocal;
    private final Map<String, String> schemaInformation;
    
    private StylesheetCache stylesheetCache;
    private PooledQTIMaximaSessionManager pooledQTIMaximaSessionManager;
    
    public MathAssessExtensionPackage() {
        this.sessionThreadLocal = new ThreadLocal<QTIMaximaSession>();
        this.schemaInformation = new HashMap<String, String>();
        this.schemaInformation.put(MathAssessConstants.MATHASSESS_NAMESPACE_URI, MathAssessConstants.MATHASSESS_SCHEMA_LOCATION);   
    }
    
    public StylesheetCache getStylesheetCache() {
        return stylesheetCache;
    }

    public void setStylesheetCache(StylesheetCache stylesheetCache) {
        this.stylesheetCache = stylesheetCache;
    }
    
    public void init() {
        try {
            MaximaConfiguration maximaConfiguration = JacomaxSimpleConfigurator.configure();
            
            pooledQTIMaximaSessionManager = new PooledQTIMaximaSessionManager();
            pooledQTIMaximaSessionManager.setMaximaConfiguration(maximaConfiguration);
            pooledQTIMaximaSessionManager.setStylesheetCache(stylesheetCache);
            pooledQTIMaximaSessionManager.init();
            
            logger.info("Created {} to handle communication with Maxima for MathAssess extensions", PooledQTIMaximaSessionManager.class.getSimpleName());
        }
        catch (JacomaxConfigurationException e) {
            pooledQTIMaximaSessionManager = null;
            logger.warn("Failed to obtain a MaximaConfiguration and/or {}. MathAssess extensions will not work and this package should NOT be used", PooledQTIMaximaSessionManager.class.getSimpleName());
        }
    }
    
    public void shutdown() {
        if (pooledQTIMaximaSessionManager!=null) {
            pooledQTIMaximaSessionManager.shutdown();
        }
    }
    
    //------------------------------------------------------------------------
    
    @Override
    public Map<String,String> getSchemaInformation() {
        return schemaInformation;
    }
    
    @Override
    public CustomOperator createCustomOperator(ExpressionParent expressionParent, String operatorClassName) {
        if (MathAssessConstants.CAS_COMPARE_CLASS.equals(operatorClassName)) {
            return new CasCompare(this, expressionParent);
        }
        else if (MathAssessConstants.CAS_CONDITION_CLASS.equals(operatorClassName)) {
            return new CasCondition(this, expressionParent);
        }
        else if (MathAssessConstants.CAS_PROCESS_CLASS.equals(operatorClassName)) {
            return new CasProcess(this, expressionParent);
        }
        else if (MathAssessConstants.SCRIPT_RULE_CLASS.equals(operatorClassName)) {
            return new ScriptRule(this, expressionParent);
        }
        return null;
    }

    @Override
    public CustomInteraction createCustomInteraction(XmlNode parentObject, String interactionClassName) {
        if (MathAssessConstants.MATH_ENTRY_INTERACTION_CLASS.equals(interactionClassName)) {
            return new MathEntryInteraction(this, parentObject);
        }
        return null;
    }
    
    //------------------------------------------------------------------------
    
    @Override
    public void lifecycleEvent(Object controller, LifecycleEventType eventType) {
        logger.info("Received lifecycle event " + eventType);
        switch (eventType) {
            case ITEM_INITIALISATION_STARTING:
            case ITEM_RESPONSE_PROCESSING_STARTING:
                /* Rather than creating a Maxima session at this point that may not be used,
                 * we'll wait until it is first needed.
                 */
                break;
                
            case ITEM_INITIALISATION_FINISHED:
            case ITEM_RESPONSE_PROCESSING_FINISHED:
                releaseMaximaSessionForThread();
                break;
                
            default:
                break;
        }
    }
    
    public QTIMaximaSession obtainMaximaSessionForThread() {
        QTIMaximaSession maximaSession = sessionThreadLocal.get();
        if (maximaSession==null) {
            if (pooledQTIMaximaSessionManager!=null) {
                logger.info("Obtaining new maxima session from pool for this request");
                /* Need to get a new session from pool */
                maximaSession = pooledQTIMaximaSessionManager.obtainSession();
                sessionThreadLocal.set(maximaSession);
            }
            else {
                throw new QTIEvaluationException("The MathAssess extensions package could not be configured to communicate with Maxima. This package should not have been used in this case");
            }
        }
        return maximaSession;
    }
    
    private void releaseMaximaSessionForThread() {
        QTIMaximaSession maximaSession = sessionThreadLocal.get();
        if (maximaSession!=null && pooledQTIMaximaSessionManager!=null) {
            logger.info("Finished with maxima session for this request - returning to pool");
            pooledQTIMaximaSessionManager.returnSession(maximaSession);
            sessionThreadLocal.set(null);
        }
    }
}
