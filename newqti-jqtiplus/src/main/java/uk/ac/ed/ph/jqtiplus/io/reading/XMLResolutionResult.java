/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.io.reading;

import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.xperimental.ResolutionResult;

/**
 * FIXME: Document this!
 * 
 * @author  David McKain
 * @version $Revision$
 */
public final class XMLResolutionResult<E extends RootNode> implements ResolutionResult<E> {
    
    private static final long serialVersionUID = -5230096529031452028L;
    
    private final QTIReadResult<E> qtiReadResult;
    private final Throwable loadException;
    
    public XMLResolutionResult(QTIReadResult<E> qtiReadResult, Throwable loadException) {
        this.qtiReadResult = qtiReadResult;
        this.loadException = loadException;
    }
    
    public QTIReadResult<E> getQTIReadResult() {
        return qtiReadResult;
    }

    public Throwable getLoadException() {
        return loadException;
    }
    
    @Override
    public E getJQTIObject() {
        return qtiReadResult!=null ? qtiReadResult.getJQTIObject() : null;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
               + "(qtiReadResult=" + qtiReadResult
               + ",loadException=" + loadException
               + ")";
    }
}
