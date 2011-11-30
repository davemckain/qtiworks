/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;

/**
 * This interface is used by {@link UnifiedXMLResourceResolver} to actually
 * <strong>locate</strong> the resulting XML resources.
 * 
 * @author  David McKain
 * @version $Revision: 2720 $
 */
public interface ResourceLocator extends Serializable {
    
    /**
     * Implementations should return an {@link InputStream} corresponding to the
     * XML resource having the given System ID (passed as a URI), or null if they
     * can't locate the required resource or won't handle the given URI.
     * 
     * @param systemIdUri
     * @return
     */
    InputStream findResource(final URI systemIdUri);

}
