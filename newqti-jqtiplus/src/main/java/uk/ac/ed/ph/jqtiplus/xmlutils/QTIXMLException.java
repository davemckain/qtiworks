/* $Id: QTIXMLException.java 2766 2011-07-21 17:02:08Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils;

import uk.ac.ed.ph.jqtiplus.exception.QTIRuntimeException;

/**
 * Runtime exception thrown when unexpected XML errors occur.
 * <p>
 * This should not be used for failures that arise as a result
 * of client input. 
 * 
 * @author  David McKain
 * @version $Revision: 2766 $
 */
public class QTIXMLException extends QTIRuntimeException {

    private static final long serialVersionUID = -8067028187234814860L;

    public QTIXMLException() {
        super();
    }

    public QTIXMLException(String message, Throwable cause) {
        super(message, cause);
    }

    public QTIXMLException(String message) {
        super(message);
    }

    public QTIXMLException(Throwable cause) {
        super(cause);
    }
}
