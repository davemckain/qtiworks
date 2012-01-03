/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.io.reading;

import uk.ac.ed.ph.jqtiplus.exception2.QTIModelException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLSourceLocationInformation;

import org.w3c.dom.Element;

/**
 * FIXME: Document this!
 * 
 * @author  David McKain
 * @version $Revision$
 */
public final class QTIModelBuildingError {
    
    private final QTIModelException exception;
    private final Element element;
    private final XMLSourceLocationInformation location;
    
    public QTIModelBuildingError(QTIModelException exception, Element element, XMLSourceLocationInformation location) {
        this.exception = exception;
        this.element = element;
        this.location = location;
    }

    public QTIModelException getException() {
        return exception;
    }
    
    public Element getElement() {
        return element;
    }

    public XMLSourceLocationInformation getLocation() {
        return location;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(exception=" + exception
                + ",element=" + element
                + ",location=" + location
                + ")";
    }
}
