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
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.reading.QtiModelBuildingError;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.validation.TestValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David McKain
 */
public final class AssessmentTestManager implements TestValidationContext {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentTestManager.class);

    private final QTIObjectManager qtiObjectManager;

    private final AssessmentTest test;

    private final Map<URI, AssessmentItemManager> resolvedItemManagers;

    public AssessmentTestManager(QTIObjectManager qtiObjectManager, AssessmentTest assessmentTest) {
        ConstraintUtilities.ensureNotNull(qtiObjectManager, "qtiObjectManager");
        ConstraintUtilities.ensureNotNull(assessmentTest, "assessmentItem");
        this.qtiObjectManager = qtiObjectManager;
        this.test = assessmentTest;
        this.resolvedItemManagers = new HashMap<URI, AssessmentItemManager>();
    }

    public QTIObjectManager getQTIObjectManager() {
        return qtiObjectManager;
    }

    @Override
    public AssessmentObject getOwner() {
        return test;
    }

    @Override
    public AssessmentTest getTest() {
        return test;
    }

    //-------------------------------------------------------------------

    public AbstractValidationResult validateTest() {
        logger.info("Performing JQTI validation on " + this);
        return test.validate(this);
    }

    //-------------------------------------------------------------------

    @Override
    public VariableDeclaration resolveVariableReference(VariableReferenceIdentifier variableReferenceIdentifier) {
        VariableDeclaration declaration = null;
        final Identifier localIdentifier = variableReferenceIdentifier.getLocalIdentifier();

        /* (In tests, we allow both local and item references) */
        if (localIdentifier != null) {
            /* Referring to another test variable */
            declaration = test.getVariableDeclaration(localIdentifier);
        }
        else {
            /* It's a special ITEM.VAR reference */
            final Identifier itemRefIdentifier = variableReferenceIdentifier.getAssessmentItemRefIdentifier();
            final Identifier itemVarIdentifier = variableReferenceIdentifier.getAssessmentItemItemVariableIdentifier();
            final AssessmentItemRef itemRef = test.lookupItemRef(itemRefIdentifier);
            if (itemRef != null) {
                final AssessmentItem item = resolveItem(itemRef).getItem();
                declaration = item.getVariableDeclaration(itemRef.resolveVariableMapping(itemVarIdentifier));
            }
        }
        return declaration;
    }

    @Override
    public AssessmentItemManager resolveItem(AssessmentItemRef assessmentItemRef) {
        final URI baseUri = test.getSystemId();
        final URI rawHref = assessmentItemRef.getHref();
        final URI resolvedItemUri = baseUri.resolve(rawHref);

        AssessmentItemManager result = resolvedItemManagers.get(resolvedItemUri);
        if (result == null) {
            final AssessmentItem assessmentItem = resolveItem(resolvedItemUri, rawHref);
            result = new AssessmentItemManager(qtiObjectManager, assessmentItem);
            resolvedItemManagers.put(resolvedItemUri, result);
        }
        return result;
    }

    private AssessmentItem resolveItem(URI resolvedItemUri, URI rawHref) {
        logger.info("Requesting the referenced assessmentItem with href={} resolved to URI {}", rawHref, resolvedItemUri);
        try {
            final QTIReadResult<AssessmentItem> qtiReadResult = qtiObjectManager.getQTIObject(resolvedItemUri, AssessmentItem.class);
            final XMLParseResult xmlParseResult = qtiReadResult.getXMLParseResult();
            if (xmlParseResult.isValidated() && !xmlParseResult.isSchemaValid()) {
                throw new QTIXMLReferencingException("Schema validation failed on referenced assessmentItem at URI " + resolvedItemUri, xmlParseResult);
            }
            final List<QtiModelBuildingError> qtiParseErrors = qtiReadResult.getQTIParseErrors();
            if (!qtiParseErrors.isEmpty()) {
                throw new QTIXMLReferencingException("JQTI Object construction failed on responseProcessing template", xmlParseResult, qtiParseErrors);
            }
            logger.info("Resolved assessmentItem with href {} to {}", rawHref, qtiReadResult);
            return qtiReadResult.getJQTIObject();
        }
        catch (final QTIXMLResourceNotFoundException e) {
            throw new QTIXMLReferencingException("Could not load referenced assessmentItem from URI " + resolvedItemUri, e);
        }
    }

    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(qtiObjectManager=" + qtiObjectManager
                + ",test=" + test
                + ")";
    }
}
