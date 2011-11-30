/* $Id: SectionPartState.java 2782 2011-08-18 16:17:17Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.node.test.SectionPart;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.util.List;

/**
 * Encapsulates the runtime state of a {@link SectionPart}
 * 
 * @author  David McKain
 * @version $Revision: 2782 $
 */
public abstract class SectionPartState extends AbstractPartState {

    private static final long serialVersionUID = 5181218545848801081L;

    protected final int siblingIndex;
    protected final SectionPartStateKey sectionPartStateKey;
    
    public SectionPartState(AssessmentTestState testState, Identifier identifier, int siblingIndex, List<? extends SectionPartState> childStates) {
        super(testState, identifier, childStates);
        this.siblingIndex = siblingIndex;
        this.sectionPartStateKey = new SectionPartStateKey(identifier, siblingIndex);
    }
    
    public int getSiblingIndex() {
        return siblingIndex;
    }

    public SectionPartStateKey getSectionPartStateKey() {
        return sectionPartStateKey;
    }
}
