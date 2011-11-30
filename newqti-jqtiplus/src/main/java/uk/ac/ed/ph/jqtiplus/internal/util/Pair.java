/* $Id: Pair.java 2764 2011-07-21 16:08:31Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.internal.util;

/**
 * Trivial utility class describing a pair/tuple, which can occasionally be
 * useful.
 * 
 * @param <E> type for the first element of the pair
 * @param <F> type for the second element of the pair
 *
 * @author  David McKain
 * @version $Revision: 2764 $
 */
public class Pair<E,F> {
    
    private final E first;
    private final F second;
    
    public Pair(E first, F second) {
        this.first = first;
        this.second = second;
    }
    
    public E getFirst() {
        return first;
    }
    
    public F getSecond() {
        return second;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName()
            + "(" + first
            + "," + second
            + ")";
    }
}
