/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.node.test.SectionPart;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.io.Serializable;

/**
 * Composite of an {@link Identifier} and an integer, used to uniquely refer to instances of
 * {@link SectionPart}s. We need this to accommodate the case of selection without replacement.
 * 
 * @author  David McKain
 * @version $Revision$
 */
public final class SectionPartStateKey implements Serializable {
    
    private static final long serialVersionUID = 4455522269963629406L;
    
    /** Identifier used to refer to SectionPart in the enclosing AssessmentTest */
    private final Identifier identifier;
    private final int siblingIndex;
    
    public SectionPartStateKey(final Identifier identifier, final int siblingIndex) {
        this.identifier = identifier;
        this.siblingIndex = siblingIndex;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public int getSiblingIndex() {
        return siblingIndex;
    }
    
    @Override
    public String toString() {
        return identifier.toString() + "@" + siblingIndex;
    }
    
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SectionPartStateKey)) {
            return false;
        }
        SectionPartStateKey other = (SectionPartStateKey) obj;
        return toString().equals(other.toString()); /* (String rep is suitably unique) */
    }
}
