/* $Id: QTIXMLException.java 2766 2011-07-21 17:02:08Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils;

/**
 * Runtime exception thrown when unexpected XML errors occur.
 * <p>
 * This should not be used for failures that arise as a result
 * of client input. 
 * 
 * @author  David McKain
 * @version $Revision: 2766 $
 */
public class XMLReaderException extends RuntimeException {

    private static final long serialVersionUID = -8067028187234814860L;

    public XMLReaderException() {
        super();
    }

    public XMLReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public XMLReaderException(String message) {
        super(message);
    }

    public XMLReaderException(Throwable cause) {
        super(cause);
    }
}
