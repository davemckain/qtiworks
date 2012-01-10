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

import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.node.RootObject;
import uk.ac.ed.ph.jqtiplus.provision.BadResourceException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseResult;

import java.net.URI;
import java.util.List;

/**
 * Thrown by {@link QtiXmlObjectReader} when the process of parsing XML, (validating the XML),
 * constructing a JQTI Object model {@link RootObject} and then checking the results does not
 * finish successfully.
 *
 * @author David McKain
 */
public final class QtiXmlInterpretationException extends BadResourceException {
    
    private static final long serialVersionUID = 5190957743384561923L;
    
    private final URI systemId;
    private final ModelRichness requiredModelRichness;
    private final Class<? extends RootObject> requiredResultClass;
    private final XmlParseResult xmlParseResult;
    private final RootObject rootObject;
    private final List<QtiModelBuildingError> qtiModelBuildingErrors;
    
    QtiXmlInterpretationException(String message, URI systemId, ModelRichness modelRichness, Class<? extends RootObject> requiredResultClass, XmlParseResult xmlParseResult) {
        this(message, systemId, modelRichness, requiredResultClass, xmlParseResult, null, null);
    }
    
    QtiXmlInterpretationException(String message, URI systemId, ModelRichness modelRichness, Class<? extends RootObject> requiredResultClass, XmlParseResult xmlParseResult,
            RootObject rootObject, List<QtiModelBuildingError> qtiModelBuildingErrors) {
        super(message);
        this.systemId = systemId;
        this.requiredModelRichness = modelRichness;
        this.requiredResultClass = requiredResultClass;
        this.xmlParseResult = xmlParseResult;
        this.rootObject = rootObject;
        this.qtiModelBuildingErrors = qtiModelBuildingErrors;
    }

    public URI getSystemId() {
        return systemId;
    }

    public ModelRichness getModelRichness() {
        return requiredModelRichness;
    }
    
    public Class<? extends RootObject> getRequiredResultClass() {
        return requiredResultClass;
    }

    public XmlParseResult getXmlParseResult() {
        return xmlParseResult;
    }
    
    public RootObject getRootObject() {
        return rootObject;
    }

    public List<QtiModelBuildingError> getQtiModelBuildingErrors() {
        return qtiModelBuildingErrors;
    }
}
