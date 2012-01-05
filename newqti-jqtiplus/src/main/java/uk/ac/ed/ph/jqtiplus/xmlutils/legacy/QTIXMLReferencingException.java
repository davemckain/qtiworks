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
package uk.ac.ed.ph.jqtiplus.xmlutils.legacy;

import uk.ac.ed.ph.jqtiplus.exception.QTIRuntimeException;
import uk.ac.ed.ph.jqtiplus.io.reading.QTIModelBuildingError;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;

import java.util.List;

/**
 * This Exception is thrown if a referenced XML resource (e.g. a {@link ResponseProcessing} template) could not be successfully resolved and instantiated.
 * 
 * @author David McKain
 */
public class QTIXMLReferencingException extends QTIRuntimeException {

    private static final long serialVersionUID = 5628758708191965953L;

    /** Result of reading in XML, if we got that far */
    private final XMLParseResult xmlReadResult;

    /** QTI Parse errors, if we got that far */
    private final List<QTIModelBuildingError> qtiParseErrors;

    public QTIXMLReferencingException(String message, Throwable cause) {
        this(message, null, null, cause);
    }

    public QTIXMLReferencingException(String message, XMLParseResult xmlParseResult) {
        this(message, xmlParseResult, null, null);
    }

    public QTIXMLReferencingException(String message, XMLParseResult xmlParseResult, List<QTIModelBuildingError> qtiParseErrors) {
        this(message, xmlParseResult, qtiParseErrors, null);
    }

    private QTIXMLReferencingException(String message, XMLParseResult xmlParseResult, List<QTIModelBuildingError> qtiParseErrors, Throwable cause) {
        super(message, cause);
        this.xmlReadResult = xmlParseResult;
        this.qtiParseErrors = qtiParseErrors;
    }

    public XMLParseResult getXMLParseResult() {
        return xmlReadResult;
    }

    public List<QTIModelBuildingError> getQtiParseErrors() {
        return qtiParseErrors;
    }
}
