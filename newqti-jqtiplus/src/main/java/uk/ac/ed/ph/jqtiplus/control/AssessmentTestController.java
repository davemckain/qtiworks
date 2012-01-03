/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.control;

import uk.ac.ed.ph.jqtiplus.state.AssessmentTestState;
import uk.ac.ed.ph.jqtiplus.state.ControlObjectState;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.AssessmentTestManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * THIS IS A MINIMISED BUT NON-FUNCTIONAL VERSION OF THE CLASS IN THE UNREFACTORED MODULE
 * THAT I HAVE INCLUDED TO MAKE THINGS COMPILE. DO NOT USE THIS CLASS!!
 * 
 * @author  David McKain
 * @version $Revision: 2802 $
 */
public class AssessmentTestController {

    protected static Logger logger = LoggerFactory.getLogger(AssessmentTestController.class);

    @SuppressWarnings("unused")
    public AssessmentTestController(AssessmentTestManager testManager, AssessmentTestState assessmentTestState, Timer timer) {
    }
    
    private void blowUp() {
        throw new QTILogicException("This class is a stub - the real version has not been completely refactored yet");
    }
    
    public Timer getTimer() {
        blowUp();
        return null;
    }
    
    public boolean passMaximumTimeLimit(@SuppressWarnings("unused") ControlObjectState<?> start) {
        blowUp();
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "()";
    }

}
