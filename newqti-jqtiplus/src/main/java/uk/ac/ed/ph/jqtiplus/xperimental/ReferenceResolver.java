/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xperimental;

import uk.ac.ed.ph.jqtiplus.node.RootNode;

import java.net.URI;

/**
 * @author  David McKain
 * @version $Revision$
 */
public interface ReferenceResolver {
    
    /**
     * E.G. Would be used to resolved response processing, referenced items etc.
     * 
     * At this level of abstraction, either the resolution succeeds or it does not, so we either return
     * a result Object (which may contain further diagnostic/validation information), or we return null.
     * 
     * @param baseObject
     * @param href
     * @return
     */
    <E extends RootNode> ResolutionResult<E> resolve(RootNode baseObject, URI href, Class<E> resultClass);

}
