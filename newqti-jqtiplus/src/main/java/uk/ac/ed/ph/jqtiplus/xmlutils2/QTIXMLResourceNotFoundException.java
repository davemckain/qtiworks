/* $Id: QTIXMLResourceNotFoundException.java 2766 2011-07-21 17:02:08Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils2;

import uk.ac.ed.ph.jqtiplus.exception.QTIException;
import uk.ac.ed.ph.jqtiplus.xmlutils.ResourceLocator;

/**
 * Exception thrown when a required XML resource cannot be found.
 * 
 * @author  David McKain
 * @version $Revision: 2766 $
 */
public class QTIXMLResourceNotFoundException extends QTIException {

    private static final long serialVersionUID = 4325972690545164979L;
    
    private final ResourceLocator resourceLocator;
    private final String systemId;
    
    public QTIXMLResourceNotFoundException(final ResourceLocator resourceLocator, final String systemId) {
        super("Could not locate required XML resource with systemId " + systemId + " using ResourceLocator " + resourceLocator);
        this.resourceLocator = resourceLocator;
        this.systemId = systemId;
    }
    
    public ResourceLocator getResourceLocator() {
        return resourceLocator;
    }
    
    public String getSystemId() {
        return systemId;
    }
}
