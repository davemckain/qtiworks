/* Copyright (c) 2012, University of Edinburgh.
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

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Encapsulates the diagnostic results of parsing (and optionally schema-validating) XML
 * 
 * @author David McKain
 */
public final class XMLParseResult implements Serializable, ErrorHandler {

    private static final long serialVersionUID = -6558013135849907488L;

    private final URI systemId;
    private boolean parsed;
    private boolean validated;
    private final List<SAXParseException> warnings;
    private final List<SAXParseException> errors;
    private final List<SAXParseException> fatalErrors;
    private final List<String> supportedSchemaNamespaces;
    private final List<String> unsupportedSchemaNamespaces;

    public XMLParseResult(URI systemId) {
        this.systemId = systemId;
        this.parsed = false;
        this.validated = false;
        this.warnings = new ArrayList<SAXParseException>();
        this.errors = new ArrayList<SAXParseException>();
        this.fatalErrors = new ArrayList<SAXParseException>();
        this.supportedSchemaNamespaces = new ArrayList<String>();
        this.unsupportedSchemaNamespaces = new ArrayList<String>();
    }

    public URI getSystemId() {
        return systemId;
    }


    public boolean isParsed() {
        return parsed;
    }

    public void setParsed(boolean parsed) {
        this.parsed = parsed;
    }


    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
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
                && unsupportedSchemaNamespaces.isEmpty();
    }

    //---------------------------------------------------------

    @Override
    public void warning(SAXParseException exception) {
        warnings.add(exception);
    }

    @Override
    public void error(SAXParseException exception) {
        errors.add(exception);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXParseException {
        fatalErrors.add(exception);
        throw exception;
    }

    //---------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(systemId=" + systemId
                + ",parsed=" + parsed
                + ",validated=" + validated
                + ",schemaValid=" + isSchemaValid()
                + ",warnings=" + warnings
                + ",errors=" + errors
                + ",fatalErrors=" + fatalErrors
                + ",supportedSchemaNamespaces=" + supportedSchemaNamespaces
                + ",unsupportedSchemaNamespaces=" + unsupportedSchemaNamespaces
                + ")";
    }
}
