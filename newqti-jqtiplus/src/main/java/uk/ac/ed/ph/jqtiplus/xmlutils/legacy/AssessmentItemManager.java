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

import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.reading.QtiModelBuildingError;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlReaderException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Not thread-safe
 * FIXME: Document this!
 * 
 * @author David McKain
 */
public final class AssessmentItemManager implements ItemValidationContext {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentItemManager.class);

    private final QTIObjectManager qtiObjectManager;

    private final AssessmentItem item;

    private ResponseProcessing resolvedResponseProcessing;

    public AssessmentItemManager(QTIObjectManager qtiObjectManager, AssessmentItem assessmentItem) {
        ConstraintUtilities.ensureNotNull(qtiObjectManager, "qtiObjectManager");
        ConstraintUtilities.ensureNotNull(assessmentItem, "assessmentItem");
        this.qtiObjectManager = qtiObjectManager;
        this.item = assessmentItem;
        this.resolvedResponseProcessing = null;
    }

    public QTIObjectManager getQTIObjectManager() {
        return qtiObjectManager;
    }

    @Override
    public AssessmentObject getOwner() {
        return item;
    }

    @Override
    public AssessmentItem getItem() {
        return item;
    }

    //-------------------------------------------------------------------

    public AbstractValidationResult validateItem() {
        logger.info("Performing JQTI validation on " + this);
        final AbstractValidationResult result = new AbstractValidationResult(item);
        item.validate(this, result);
        return result;
    }

    //-------------------------------------------------------------------

    @Override
    public VariableDeclaration resolveVariableReference(VariableReferenceIdentifier variableReferenceIdentifier) {
        VariableDeclaration declaration = null;
        final Identifier localIdentifier = variableReferenceIdentifier.getLocalIdentifier();

        /* (In Items, we only allow local references) */
        if (localIdentifier != null) {
            declaration = item.getVariableDeclaration(localIdentifier);
        }
        return declaration;
    }

    /**
     * @throws QTIXMLReferencingException
     * @throws XmlReaderException
     */
    @Override
    public ResponseProcessing getResolvedResponseProcessing() {
        if (resolvedResponseProcessing == null) {
            resolvedResponseProcessing = resolveResponseProcessing();
        }
        return resolvedResponseProcessing;
    }

    private ResponseProcessing resolveResponseProcessing() {
        final ResponseProcessing responseProcessing = item.getResponseProcessing();
        if (responseProcessing == null) {
            /* No responseProcessing */
            return null;
        }
        if (!responseProcessing.getResponseRules().isEmpty()) {
            /* Processing already resolved */
            return responseProcessing;
        }
        QTIReadResult<ResponseProcessing> qtiReadResult = null;
        QTIXMLResourceNotFoundException firstNotFoundException = null;
        final List<URI> attemptedUris = new ArrayList<URI>();
        URI templateUri = responseProcessing.getTemplate();
        if (templateUri != null) {
            attemptedUris.add(templateUri);
            try {
                qtiReadResult = resolveRPTemplate(templateUri);
            }
            catch (final QTIXMLResourceNotFoundException e) {
                firstNotFoundException = e;
            }
        }
        if (qtiReadResult == null) {
            templateUri = responseProcessing.getTemplateLocation();
            if (templateUri != null) {
                attemptedUris.add(templateUri);
                try {
                    qtiReadResult = resolveRPTemplate(templateUri);
                }
                catch (final QTIXMLResourceNotFoundException e) {
                    if (firstNotFoundException == null) {
                        firstNotFoundException = e;
                    }
                }
            }
        }
        if (qtiReadResult == null) {
            throw new QTIXMLReferencingException("Could not load responseProcessing template from URI(s) " + attemptedUris, firstNotFoundException);
        }
        final XMLParseResult xmlParseResult = qtiReadResult.getXMLParseResult();
        if (xmlParseResult.isValidated() && !xmlParseResult.isSchemaValid()) {
            throw new QTIXMLReferencingException("Schema validation failed on resolved responseProcessing template", xmlParseResult);
        }
        final List<QtiModelBuildingError> qtiParseErrors = qtiReadResult.getQTIParseErrors();
        if (!qtiParseErrors.isEmpty()) {
            throw new QTIXMLReferencingException("JQTI Object construction failed on responseProcessing template", xmlParseResult, qtiParseErrors);
        }
        logger.info("Resolved responseProcessing template using href {} to {}", templateUri, qtiReadResult);
        return qtiReadResult.getJQTIObject();
    }

    /**
     * @throws QTIXMLResourceNotFoundException
     */
    private QTIReadResult<ResponseProcessing> resolveRPTemplate(URI templateUri) {
        logger.debug("Attempting to request a responseProcessing template with URI {} from {}", templateUri, QTIObjectManager.class.getSimpleName());
        final URI baseUri = item.getSystemId();
        final URI resolved = baseUri.resolve(templateUri.toString());

        /* We'll normally do schema validation on the template, unless it's one of the standard templates, which
         * will be assumed to be valid.
         */
        return qtiObjectManager.getQTIObject(resolved, ResponseProcessing.class);
    }

    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(qtiObjectManager=" + qtiObjectManager
                + ",item=" + item
                + ",resolvedResponseProcessing=" + resolvedResponseProcessing
                + ")";
    }
}
