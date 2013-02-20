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
package uk.ac.ed.ph.jqtiplus.reading;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.provision.BadResourceException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseResult;

import java.net.URI;
import java.util.List;

/**
 * Thrown by {@link QtiObjectReader} when the process of parsing XML, (validating the XML),
 * constructing a JQTI Object model {@link RootNode} and then checking the results does not
 * finish successfully.
 *
 * @author David McKain
 */
public final class QtiXmlInterpretationException extends BadResourceException {

    private static final long serialVersionUID = 5190957743384561923L;

    public static enum InterpretationFailureReason {
        XML_PARSE_FAILED,
        XML_SCHEMA_VALIDATION_FAILED,
        UNSUPPORTED_ROOT_NODE,
        JQTI_MODEL_BUILD_FAILED,
        WRONG_RESULT_TYPE,
        ;
    }

    private final InterpretationFailureReason interpretationFailureReason;
    private final Class<? extends RootNode> requiredResultClass;
    private final XmlParseResult xmlParseResult;
    private final RootNode rootNode;
    private final List<QtiModelBuildingError> qtiModelBuildingErrors;

    QtiXmlInterpretationException(final InterpretationFailureReason interpretationFailureReason, final String message,
            final Class<? extends RootNode> requiredResultClass,
            final XmlParseResult xmlParseResult) {
        this(interpretationFailureReason, message, requiredResultClass, xmlParseResult, null, null);
    }

    QtiXmlInterpretationException(final InterpretationFailureReason interpretationFailureReason, final String message,
            final Class<? extends RootNode> requiredResultClass,
            final XmlParseResult xmlParseResult, final RootNode rootNode,
            final List<QtiModelBuildingError> qtiModelBuildingErrors) {
        super(message);
        this.interpretationFailureReason = interpretationFailureReason;
        this.requiredResultClass = requiredResultClass;
        this.xmlParseResult = xmlParseResult;
        this.rootNode = rootNode;
        this.qtiModelBuildingErrors = qtiModelBuildingErrors;
    }

    public InterpretationFailureReason getInterpretationFailureReason() {
        return interpretationFailureReason;
    }

    public URI getSystemId() {
        return xmlParseResult.getSystemId();
    }

    public Class<? extends RootNode> getRequiredResultClass() {
        return requiredResultClass;
    }

    @ObjectDumperOptions(DumpMode.DEEP)
    public XmlParseResult getXmlParseResult() {
        return xmlParseResult;
    }

    public RootNode getRootNode() {
        return rootNode;
    }

    @ObjectDumperOptions(DumpMode.DEEP)
    public List<QtiModelBuildingError> getQtiModelBuildingErrors() {
        return qtiModelBuildingErrors;
    }
}
