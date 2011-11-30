/* $Id: TestValidationContext.java 2778 2011-08-17 13:29:05Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.control;

import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.xmlutils.AssessmentItemManager;

/**
 * @author  David McKain
 * @version $Revision: 2778 $
 */
public interface TestValidationContext extends ValidationContext {
    
    /** Returns owning AssessmentTest */
    AssessmentTest getTest();
    
    /**
     * Returns an {@link AssessmentItemManager} for the {@link AssessmentItem} referred to by
     * the given {@link AssessmentItemRef} in the current test.
     */
    AssessmentItemManager resolveItem(AssessmentItemRef assessmentItemRef);

}
