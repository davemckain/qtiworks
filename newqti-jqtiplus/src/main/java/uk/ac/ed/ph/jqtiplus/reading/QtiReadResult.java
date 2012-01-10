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
import uk.ac.ed.ph.jqtiplus.resolution.ModelRichness;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceHolder;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLParseResult;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

/**
 * FIXME: Document this type!
 * 
 * @author David McKain
 */
public final class QtiReadResult<E extends RootNode> implements ResourceHolder<E>, Serializable {

    private static final long serialVersionUID = -6470500039269477402L;

    private final URI systemId;
    private final ModelRichness modelRichness;
    private final XMLParseResult xmlParseResult;
    private final RootNode qtiObject;
    private final List<QtiModelBuildingError> qtiModelBuildingErrors;
    private final Class<E> requiredResultClass;
    
    QtiReadResult(URI systemId, ModelRichness modelRichness, Class<E> requiredClass, XMLParseResult xmlParseResult) {
        this(systemId, modelRichness, requiredClass, xmlParseResult, null, null);
    }
    
    QtiReadResult(URI systemId, ModelRichness modelRichness, Class<E> requiredClass, XMLParseResult xmlParseResult,
            RootNode qtiObject, List<QtiModelBuildingError> qtiModelBuildingErrors) {
        this.requiredResultClass = requiredClass;
        this.modelRichness = modelRichness;
        this.systemId = systemId;
        this.qtiObject = qtiObject;
        this.xmlParseResult = xmlParseResult;
        this.qtiModelBuildingErrors = qtiModelBuildingErrors;
    }

    public boolean isRequiredResultClass() {
        return qtiObject!=null && requiredResultClass.isInstance(qtiObject);
    }

    public boolean isSuccessful() {
        return isRequiredResultClass() && qtiModelBuildingErrors.isEmpty();
    }

    @Override
    public ModelRichness getModelRichness() {
        return modelRichness;
    }
    
    @Override
    public URI getSystemId() {
        return systemId;
    }
    
    @ObjectDumperOptions(DumpMode.DEEP)
    public XMLParseResult getXmlParseResult() {
        return xmlParseResult;
    }
    
    public RootNode getQtiObject() {
        return qtiObject;
    }
    
    public List<QtiModelBuildingError> getQtiModelBuildingErrors() {
        return qtiModelBuildingErrors;
    }

    public Class<E> getRequiredClass() {
        return requiredResultClass;
    }
    
    @Override
    public E getRequiredQtiObject() {
        return requiredResultClass.cast(qtiObject);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(systemId=" + systemId
                + ",modelRichness=" + modelRichness
                + ",requiredResultClass=" + requiredResultClass
                + ",qtiObject=" + qtiObject
                + ",xmlParseResult=" + xmlParseResult
                + ",qtiModelBuildingErrors=" + qtiModelBuildingErrors
                + ")";
    }

}
