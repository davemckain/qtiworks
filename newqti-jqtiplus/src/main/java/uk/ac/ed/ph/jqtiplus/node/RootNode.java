/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.node;

import uk.ac.ed.ph.jqtiplus.control.ToRefactor;

import java.net.URI;

/**
 * Marker interface for a "root" QTI Node.
 * <p>
 * Currently houses some dodgy constants that really need to be implemented better.
 * 
 * @author  David McKain
 * @version $Revision$
 */
public interface RootNode extends XmlObject {
    
    /** Header of xml file. */
    public static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    
    /** Name of xmlns attribute in xml schema. */
    @ToRefactor
    public static final String ATTR_DEFAULT_NAME_SPACE_NAME = "xmlns";
    
    /** Value of xmlns attribute. */
    @ToRefactor
    public static final String ATTR_DEFAULT_NAME_SPACE_VALUE = "http://www.imsglobal.org/xsd/imsqti_v2p1";

    /** Name of xmlns:xsi attribute in xml schema. */
    @ToRefactor
    public static final String ATTR_XSI_NAME_SPACE_NAME = "xmlns:xsi";
    
    /** Value of xmlns:xsi attribute. */
    @ToRefactor
    public static final String ATTR_XSI_NAME_SPACE_VALUE = "http://www.w3.org/2001/XMLSchema-instance";

    /** Name of xsi:schemaLocation attribute in xml schema. */
    @ToRefactor
    public static final String ATTR_XSI_SCHEMA_LOCATION_NAME = "xsi:schemaLocation";
    
    /** Value of xsi:schemaLocation attribute. */
    @ToRefactor
    public static final String ATTR_XSI_SCHEMA_LOCATION_VALUE = "http://www.imsglobal.org/xsd/imsqti_v2p1 imsqti_v2p1.xsd";
    
    /** Returns the systemId of this tree, if loaded from a URI, null otherwise */
    URI getSystemId();
    
    /** Sets the systemId for this tree */
    void setSystemId(URI systemId);

}
