/* Copyright (c) 2012-2013, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.xmlutils;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import org.xml.sax.SAXParseException;

/**
 * Encapsulates the diagnostic results of parsing (and optionally schema-validating) XML
 * 
 * @author David McKain
 */
public final class XmlParseResult implements Serializable {

    private static final long serialVersionUID = -6558013135849907488L;

    private final URI systemId;
    private final boolean parsed;
    private final boolean validated;
    private final List<SAXParseException> warnings;
    private final List<SAXParseException> errors;
    private final List<SAXParseException> fatalErrors;
    private final List<String> unresolvedEntitySystemIds;
    private final List<String> supportedSchemaNamespaces;
    private final List<String> unsupportedSchemaNamespaces;

    public XmlParseResult(URI systemId, boolean parsed, boolean validated,
            List<SAXParseException> warnings, List<SAXParseException> errors,
            List<SAXParseException> fatalErrors, List<String> unresolvedEntitySystemIds,
            List<String> supportedSchemaNamespaces, List<String> unsupportedSchemaNamespaces) {
        this.systemId = systemId;
        this.parsed = parsed;
        this.validated = validated;
        this.warnings = ObjectUtilities.unmodifiableList(warnings);
        this.errors = ObjectUtilities.unmodifiableList(errors);
        this.fatalErrors = ObjectUtilities.unmodifiableList(fatalErrors);
        this.unresolvedEntitySystemIds = ObjectUtilities.unmodifiableList(unresolvedEntitySystemIds);
        this.supportedSchemaNamespaces = ObjectUtilities.unmodifiableList(supportedSchemaNamespaces);
        this.unsupportedSchemaNamespaces = ObjectUtilities.unmodifiableList(unsupportedSchemaNamespaces);
    }

    public URI getSystemId() {
        return systemId;
    }


    public boolean isParsed() {
        return parsed;
    }

    public boolean isValidated() {
        return validated;
    }

    public List<SAXParseException> getWarnings() {
        return warnings;
    }

    public List<SAXParseException> getErrors() {
        return errors;
    }

    public List<SAXParseException> getFatalErrors() {
        return fatalErrors;
    }
    
    public List<String> getUnresolvedEntitySystemIds() {
        return unresolvedEntitySystemIds;
    }

    public List<String> getSupportedSchemaNamespaces() {
        return supportedSchemaNamespaces;
    }

    public List<String> getUnsupportedSchemaNamespaces() {
        return unsupportedSchemaNamespaces;
    }

    public boolean isSchemaValid() {
        return validated
                && fatalErrors.isEmpty()
                && errors.isEmpty()
                && warnings.isEmpty()
                && unresolvedEntitySystemIds.isEmpty()
                && unsupportedSchemaNamespaces.isEmpty();
    }

    //---------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(systemId=" + systemId
                + ",parsed=" + parsed
                + ",validated=" + validated
                + ",schemaValid=" + isSchemaValid()
                + ",warnings=" + warnings
                + ",errors=" + errors
                + ",fatalErrors=" + fatalErrors
                + ",unresolvedEntitySystemIds=" + unresolvedEntitySystemIds
                + ",supportedSchemaNamespaces=" + supportedSchemaNamespaces
                + ",unsupportedSchemaNamespaces=" + unsupportedSchemaNamespaces
                + ")";
    }
}
