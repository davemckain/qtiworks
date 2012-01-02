/* $Id: JQTIExtensionPackage.java 2766 2011-07-21 17:02:08Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.control;

import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.CustomOperator;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.CustomInteraction;

import java.util.Map;

/**
 * FIXME: Document this!
 * 
 * @author  David McKain
 * @version $Revision: 2766 $
 */
public interface JQTIExtensionPackage extends LifecycleListener {
    
    /**
     * Return a Map of schema information for this package, in the form NS -> URI.
     * <p>
     * A null or empty result is legal here.
     */
    Map<String, String> getSchemaInformation();
    
    /**
     * Instantiate and return a new {@link CustomOperator} corresponding to the given class name,
     * returning null if this package does not support the stated class.
     * 
     * @param expressionParent
     * @param operatorClassName
     * @return
     */
    CustomOperator createCustomOperator(ExpressionParent expressionParent, String operatorClassName);
    
    /**
     * Instantiate and return a new {@link CustomInteraction} corresponding to the given class name,
     * returning null if this package does not support the stated class.
     */
    CustomInteraction createCustomInteraction(XmlNode parentObject, String interactionClassName);
    
}
