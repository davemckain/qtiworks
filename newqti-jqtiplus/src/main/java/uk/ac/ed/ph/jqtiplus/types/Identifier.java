/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.types;

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;


/**
 * Encapsulates a QTI "identifier" in its most common use cases.
 * <p>
 * The period character (.) is not allowed here; "identifiers" with periods are
 * only allowed in certain {@link Expression}s during outcome processing and are
 * represented by a different type for clarify.
 * <p>
 * CombiningChars and Extenders are not currently supported!
 * 
 * @author  David McKain
 * @version $Revision: 2781 $
 */
public final class Identifier implements Comparable<Identifier> {

    private String value;

    /**
     * @throws QTIParseException if value is not a valid identifier
     */
    public Identifier(String value) {
        this(value, true);
    }

    /**
     * @throws QTIParseException if value is not a valid identifier
     */
    public Identifier(String value, boolean verify) {
        if (verify) {
            verifyIdentifier(value);
        }
        this.value = value;
    }
    
    public VariableReferenceIdentifier toVariableReferenceIdentifier() {
        return new VariableReferenceIdentifier(this);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Identifier)) {
            return false;
        }
        Identifier other = (Identifier) obj;
        return value.equals(other.value);
    }
    
    public int compareTo(Identifier other) {
        return value.compareTo(other.value);
    }

    /**
     * @throws QTIParseException if <code>String</code> representation of
     *             <code>identifier</code> is not valid
     */
    private static void verifyIdentifier(String value) {
        if (value != null) {
            value = value.trim();
        }

        if (value == null || value.length() == 0) {
            throw new QTIParseException("Invalid identifier '" + value + "'. Must not be null or blank.");
        }

        /* First character. */
        if (!Character.isLetter(value.codePointAt(0)) && value.charAt(0) != '_') {
            throw new QTIParseException("Invalid identifier '" + value + "'. First character '" + value.charAt(0) + "' is not valid.");
        }

        /* Rest of characters. */
        for (int i = 1; i < value.length(); i++) {
            if (value.charAt(i) == '.') {
                throw new QTIParseException("Invalid identifier '" + value + "'. JQTI does not permit period (.) characters in this identifier.");
            }
            if (!Character.isLetterOrDigit(value.codePointAt(i)) && value.charAt(i) != '_' && value.charAt(i) != '-') {
                throw new QTIParseException("Invalid identifier '" + value + "'. Character '" + value.charAt(i) + "' at position " + (i + 1) + " is not valid.");
            }
        }
    }
}