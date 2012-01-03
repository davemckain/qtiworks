/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.control2;

import uk.ac.ed.ph.jqtiplus.control.JQTIExtensionPackage;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Once created, all properties of this manager are unmodifiable and safe to use by multiple threads.
 *
 * @author  David McKain
 * @version $Revision$
 */
public final class JQTIExtensionManager {
    
    private static final Logger logger = LoggerFactory.getLogger(JQTIExtensionManager.class);
    
    private final List<JQTIExtensionPackage> extensionPackages;
    private final Map<String, String> extensionSchemaMap;
    
    public JQTIExtensionManager(JQTIExtensionPackage... jqtiExtensionPackages) {
        this(Arrays.asList(jqtiExtensionPackages));
    }
    
    public JQTIExtensionManager(List<JQTIExtensionPackage> jqtiExtensionPackages) {
        this.extensionPackages = Collections.unmodifiableList(jqtiExtensionPackages);
        this.extensionSchemaMap = Collections.unmodifiableMap(buildExtensionSchemaMap());
    }
    
    public List<JQTIExtensionPackage> getExtensionPackages() {
        return extensionPackages;
    }
    
    public Map<String, String> getExtensionSchemaMap() {
        return extensionSchemaMap;
    }
    
    private Map<String, String> buildExtensionSchemaMap() {
        Map<String, String> result = new HashMap<String, String>();
        for (JQTIExtensionPackage extensionPackage : extensionPackages) {
            result.putAll(extensionPackage.getSchemaInformation());
        }
        return result;
    }
    
    //---------------------------------------------------------------------
    
    public CustomInteraction createCustomInteraction(XmlNode parentObject, String interactionClass) {
        CustomInteraction result = null;
        for (JQTIExtensionPackage extensionPackage : extensionPackages) {
            result = extensionPackage.createCustomInteraction(parentObject, interactionClass);
            if (result!=null) {
                logger.debug("Created customInteraction of class {} using package {}", interactionClass, extensionPackage);
                return result;
            }
        }
        logger.warn("customInteraction of class {} not supported by any registered package. Using placeholder", interactionClass);
        return new UnsupportedCustomInteraction(parentObject);
    }
    
    public CustomOperator createCustomOperator(ExpressionParent expressionParent, String operatorClass) {
        CustomOperator result;
        for (JQTIExtensionPackage extensionPackage : extensionPackages) {
            result = extensionPackage.createCustomOperator(expressionParent, operatorClass);
            if (result!=null) {
                logger.debug("Created customOperator of class {} using package {}", operatorClass, extensionPackage);
                return result;
            }
        }
        logger.warn("customOperator of class {} not supported by any registered package. Using placeholder", operatorClass);
        return new UnsupportedCustomOperator(expressionParent);
    }
    
    //---------------------------------------------------------------------
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(extensionPackages=" + extensionPackages
            + ")";
    }
}
