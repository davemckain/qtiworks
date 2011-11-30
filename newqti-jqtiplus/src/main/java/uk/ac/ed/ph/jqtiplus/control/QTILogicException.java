/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.control;

import uk.ac.ed.ph.jqtiplus.exception.QTIRuntimeException;

public class QTILogicException extends QTIRuntimeException {

    private static final long serialVersionUID = -6501850546437845744L;

    public QTILogicException(String message) {
        super(message);
    }
}
