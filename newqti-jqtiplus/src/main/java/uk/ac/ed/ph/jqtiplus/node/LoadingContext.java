/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.node;

import uk.ac.ed.ph.jqtiplus.control2.JQTIExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;

import org.w3c.dom.Element;

/**
 * FIXME: Document this!
 * 
 * @author  David McKain
 */
public interface LoadingContext {
    
    JQTIExtensionManager getJQTIExtensionManager();
    
    void parseError(QTIParseException exception, Element element);

}
