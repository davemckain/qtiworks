/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.exception2;

import uk.ac.ed.ph.jqtiplus.exception.QTIRuntimeException;

/**
 * Base class for {@link Exception}s related to the JQTI Object model.
 * 
 * @author  David McKain
 * @version $Revision$
 */
public abstract class QTIModelException extends QTIRuntimeException {

    private static final long serialVersionUID = -357903886560490898L;

    public QTIModelException() {
        super();
    }

    public QTIModelException(String message, Throwable cause) {
        super(message, cause);
    }

    public QTIModelException(String message) {
        super(message);
    }

    public QTIModelException(Throwable cause) {
        super(cause);
    }

}
