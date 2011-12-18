/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.node;

import uk.ac.ed.ph.jqtiplus.control.JQTIController;
import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;

import org.w3c.dom.Element;

/**
 * @author  David McKain
 */
public interface LoadingContext {
    
    JQTIController getJQTIController();
    
    void parseError(QTIParseException exception, Element element);

}
