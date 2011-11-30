/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.types;

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;

/**
 * Encapsulates the special case of QTI "identifiers" that may contain a single
 * period character within them, which is used in test outcome processing to
 * refer to variables within particular items.
 * 
 * @see VariableReferenceIdentifier
 * 
 * @author  David McKain
 * @version $Revision: 2781 $
 */
public final class VariableReferenceIdentifier {

    private String value;

    private final Identifier localIdentifier;

    private final Identifier assessmentItemRefIdentifier;

    private final Identifier assessmentItemItemVariableIdentifier;

    /**
     * @throws QTIParseException if value is not a valid identifier (definition)
     */
    public VariableReferenceIdentifier(String value) {
        if (value != null) {
            value = value.trim();
        }

        if (value == null || value.length() == 0) {
            throw new QTIParseException("Invalid reference identifier '" + value + "'. Must not be empty or blank.");
        }

        /* First character. */
        if (!Character.isLetter(value.codePointAt(0)) && value.charAt(0) != '_') {
            throw new QTIParseException("Invalid reference identifier '" + value + "'. First character '" + value.charAt(0) + "' is not valid.");
        }

        /* Rest of characters. */
        int dotPos = -1;
        for (int i=1; i<value.length(); i++) {
            if (value.charAt(i) == '.') {
                if (dotPos!=-1) {
                    throw new QTIParseException("Invalid reference identifier '" + value + "'. Only one period (.) character is allowed in this identifier.");
                }
                dotPos = i;
            }
            else if (!Character.isLetterOrDigit(value.codePointAt(i)) && value.charAt(i) != '_' && value.charAt(i) != '-') {
                throw new QTIParseException("Invalid reference identifier '" + value + "'. Character '" + value.charAt(i) + "' at position " + (i + 1)
                        + " is not valid.");
            }
        }
        this.value = value;
        if (dotPos == -1) {
            this.localIdentifier = new Identifier(value, false);
            this.assessmentItemRefIdentifier = null;
            this.assessmentItemItemVariableIdentifier = null;
        }
        else {
            this.localIdentifier = null;
            this.assessmentItemRefIdentifier = new Identifier(value.substring(0, dotPos), false);
            this.assessmentItemItemVariableIdentifier = new Identifier(value.substring(dotPos + 1));
        }
    }
    
    /**
     * @see Identifier#toVariableReferenceIdentifier()
     */
    VariableReferenceIdentifier(Identifier identifier) {
        this.localIdentifier = identifier;
        this.assessmentItemItemVariableIdentifier = null;
        this.assessmentItemRefIdentifier = null;
        this.value = identifier.toString();
    }

    public Identifier getLocalIdentifier() {
        return localIdentifier;
    }

    public Identifier getAssessmentItemRefIdentifier() {
        return assessmentItemRefIdentifier;
    }

    public Identifier getAssessmentItemItemVariableIdentifier() {
        return assessmentItemItemVariableIdentifier;
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
        if (!(obj instanceof VariableReferenceIdentifier)) {
            return false;
        }
        VariableReferenceIdentifier other = (VariableReferenceIdentifier) obj;
        return value.equals(other.value);
    }
}
