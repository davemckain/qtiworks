/* $Id: Operation.java 2642 2011-04-28 12:40:08Z davemckain $
*
* Copyright (c) 2011, The University of Edinburgh.
* All Rights Reserved
*/
package uk.ac.ed.ph.jqtiplus.node.expression.operator.math;

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;

import java.util.HashMap;
import java.util.Map;


/**
 * Enumerates all of the constants supported by the <tt>mathConstant</tt> operator.
 * 
 * @author   David McKain
 * @revision $Revision: 2642 $
 */
enum MathConstantTarget {
    
    PI("pi", Math.PI),
    E("e", Math.E),
    
    ;
    
    private static Map<String, MathConstantTarget> constants;
    static {
        constants = new HashMap<String, MathConstantTarget>();
        for (MathConstantTarget operation : MathConstantTarget.values())
            constants.put(operation.getName(), operation);
    }
    
    private final String name;
    private final double value;
    
    private MathConstantTarget(final String name, final double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }
    
    public double getValue() {
        return value;
    }

    public static MathConstantTarget parseConstant(String name) {
        MathConstantTarget result = constants.get(name);
        if (result == null) {
            throw new QTIParseException("Invalid mathConstant " + name);
        }
        return result;
    }
}