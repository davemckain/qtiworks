/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.control;

import uk.ac.ed.ph.jqtiplus.node.XmlObject;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.CustomOperator;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.UnsupportedCustomOperator;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.CustomInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.UnsupportedCustomInteraction;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FIXME: Rename and document this! It's not really a controller at all; it just keeps track of what extensions are registered
 * and things like that.
 * 
 * FIXME: Make note about thread safety. This Object should become read-only before being used in any processing since it
 * will shared by a number of Threads
 *
 * @author  David McKain
 * @version $Revision$
 */
public final class JQTIController {
    
    private static final Logger logger = LoggerFactory.getLogger(JQTIController.class);
    
    private final List<JQTIExtensionPackage> extensionPackages;
    
    public JQTIController() {
        this.extensionPackages = new ArrayList<JQTIExtensionPackage>();
    }
    
    public List<JQTIExtensionPackage> getExtensionPackages() {
        return extensionPackages;
    }
    
    //---------------------------------------------------------------------

    public CustomInteraction createCustomInteraction(XmlObject parentObject, String interactionClass) {
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
