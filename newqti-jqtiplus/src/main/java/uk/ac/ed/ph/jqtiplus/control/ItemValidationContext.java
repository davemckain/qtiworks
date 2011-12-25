/* $Id: ItemValidationContext.java 2749 2011-07-08 08:43:51Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.control;

import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.xperimental.ReferencingException;

/**
 * @author  David McKain
 * @version $Revision: 2749 $
 */
public interface ItemValidationContext extends ValidationContext {
    
    AssessmentItem getItem();
    
    /**
     * Returns the resolved {@link ResponseProcessing} fragment.
     * @throws ReferencingException 
     */
    ResponseProcessing getResolvedResponseProcessing()
            throws ReferencingException;

}
