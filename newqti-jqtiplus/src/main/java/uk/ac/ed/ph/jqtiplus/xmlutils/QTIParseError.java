/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils;

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.io.reading.xml.XMLSourceLocationInformation;

import org.w3c.dom.Element;

/**
 * @author  David McKain
 * @version $Revision$
 */
public final class QTIParseError {
    
    private final QTIParseException exception;
    private final Element element;
    private final XMLSourceLocationInformation location;
    
    public QTIParseError(QTIParseException exception, Element element, XMLSourceLocationInformation location) {
        this.exception = exception;
        this.element = element;
        this.location = location;
    }

    public QTIParseException getException() {
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
