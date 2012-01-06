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
package uk.ac.ed.ph.jqtiplus.reading;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLParseResult;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

/**
 * Encapsulates the result of attempting to read an arbitrary QTI {@link RootNode} from XML.
 * 
 * @author David McKain
 */
public final class QtiReadResult implements Serializable {

    private static final long serialVersionUID = -7443481710269376503L;
    
    final URI systemId;
    final XMLParseResult xmlParseResult;
    final RootNode qtiObject;
    final List<QtiModelBuildingError> qtiModelBuildingErrors;
    
    QtiReadResult(URI systemId, XMLParseResult xmlParseResult) {
        this(systemId, xmlParseResult, null, null);
    }

    QtiReadResult(URI systemId, XMLParseResult xmlParseResult, RootNode qtiObject, List<QtiModelBuildingError> qtiModelBuildingErrors) {
        this.systemId = systemId;
        this.qtiObject = qtiObject;
        this.xmlParseResult = xmlParseResult;
        this.qtiModelBuildingErrors = qtiModelBuildingErrors;
    }
    
    public boolean isSuccessful() {
        return qtiObject!=null && qtiModelBuildingErrors.isEmpty();
    }
    
    public URI getSystemId() {
        return systemId;
    }
    
    public RootNode getResolvedQtiObject() {
        return qtiObject;
    }
    
    @ObjectDumperOptions(DumpMode.DEEP)
    public XMLParseResult getXMLParseResult() {
        return xmlParseResult;
    }

    public List<QtiModelBuildingError> getQtiModelBuildingErrors() {
        return qtiModelBuildingErrors;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + ",systemId=" + systemId
                + ",qtiObject=" + qtiObject
                + ",xmlParseResult=" + xmlParseResult
                + ",qtiModelBuildingErrors=" + qtiModelBuildingErrors
                + ")";
    }
}
