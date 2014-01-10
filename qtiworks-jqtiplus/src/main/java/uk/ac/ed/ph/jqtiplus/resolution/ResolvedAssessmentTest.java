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
package uk.ac.ed.ph.jqtiplus.resolution;

import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.types.ComplexReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Wraps up the lookup of an {@link AssessmentTest} and all of the unique
 * {@link AssessmentItem}s it refers to, as well as some other useful information.
 *
 * @author David McKain
 */
public final class ResolvedAssessmentTest extends ResolvedAssessmentObject<AssessmentTest> {

    private static final long serialVersionUID = -8302050952592265206L;

    /** {@link AssessmentTest} lookup */
    private final RootNodeLookup<AssessmentTest> testLookup;

    /** List of all {@link AssessmentItemRef}s in the test, in test order */
    private final List<AssessmentItemRef> assessmentItemRefs;

    /** Resolved System ID for each {@link AssessmentItemRef} */
    private final Map<AssessmentItemRef, URI> systemIdByItemRefMap;

    /** Maps resolved System ID to applicable {@link AssessmentItemRef} */
    private final Map<URI, List<AssessmentItemRef>> itemRefsBySystemIdMap;

    /** {@link ResolvedAssessmentItem} for each unique item System ID. */
    private final Map<URI, ResolvedAssessmentItem> resolvedAssessmentItemBySystemIdMap;

    public ResolvedAssessmentTest(final RootNodeLookup<AssessmentTest> testLookup,
            final List<AssessmentItemRef> assessmentItemRefs,
            final Map<AssessmentItemRef, URI> systemIdByItemRefMap,
            final Map<URI, List<AssessmentItemRef>> itemRefsBySystemIdMap,
            final Map<URI, ResolvedAssessmentItem> resolvedAssessmentItemMap) {
        super(testLookup);
        this.testLookup = testLookup;
        this.assessmentItemRefs = Collections.unmodifiableList(assessmentItemRefs);
        this.systemIdByItemRefMap = Collections.unmodifiableMap(systemIdByItemRefMap);
        this.itemRefsBySystemIdMap = Collections.unmodifiableMap(itemRefsBySystemIdMap);
        this.resolvedAssessmentItemBySystemIdMap = Collections.unmodifiableMap(resolvedAssessmentItemMap);
    }

    @Override
    public AssessmentObjectType getType() {
        return AssessmentObjectType.ASSESSMENT_TEST;
    }

    public RootNodeLookup<AssessmentTest> getTestLookup() {
        return testLookup;
    }

    public List<AssessmentItemRef> getAssessmentItemRefs() {
        return assessmentItemRefs;
    }

    public Map<AssessmentItemRef, URI> getSystemIdByItemRefMap() {
        return systemIdByItemRefMap;
    }

    public Map<URI, List<AssessmentItemRef>> getItemRefsBySystemIdMap() {
        return itemRefsBySystemIdMap;
    }

    @ObjectDumperOptions(DumpMode.TO_STRING)
    public Map<URI, ResolvedAssessmentItem> getResolvedAssessmentItemBySystemIdMap() {
        return resolvedAssessmentItemBySystemIdMap;
    }

    public ResolvedAssessmentItem getResolvedAssessmentItem(final AssessmentItemRef itemRef) {
        final URI systemId = systemIdByItemRefMap.get(itemRef);
        return systemId!=null ? resolvedAssessmentItemBySystemIdMap.get(systemId) : null;
    }

    //-------------------------------------------------------------------

    /**
     * Resolves the given variable reference, within this test only.
     * <p>
     * The resolution returns a {@link List} of matching {@link VariableDeclaration}s, or null if the
     * test lookup was unsuccessful. The List will ideally contain 1 element, but may contain 0
     * elements (if no match is found) or more than 1 element (if there are multiple {@link VariableDeclaration}s
     * having the same identifier.
     * <p>
     * (Note that while tests normally only contain outcome variables, there is also the built-in
     * <code>duration</code> variable, which is a response variable. Hence this returns a
     * {@link VariableDeclaration} rather than anything more specific.)
     *
     * @param variableReferenceIdentifier
     */
    public List<VariableDeclaration> resolveTestVariable(final Identifier variableReferenceIdentifier) {
        if (!testLookup.wasSuccessful()) {
            return null;
        }
        return tryTestVariableDeclaration(variableReferenceIdentifier);
    }

    /**
     * Resolves the given variable reference using the given simple {@link Identifier}.
     * <p>
     * This type of reference will always resolve a local test variable.
     * <p>
     * The resolution will give a single result unless there are duplicate variables having
     * the same identifier (in which case the test is not considered valid).
     * <p>
     * Return null if the test lookup was unsuccessful.
     * <p>
     * (Note that while tests normally only contain outcome variables, there is also the built-in
     * <code>duration</code> variable, which is a response variable. Hence this returns a
     * {@link VariableDeclaration} rather than anything more specific.)
     *
     * @param variableReferenceIdentifier
     *
     * @see #resolveVariableReference(ComplexReferenceIdentifier)
     */
    public List<ResolvedTestVariableReference> resolveVariableReference(final Identifier variableReferenceIdentifier) {
        if (!testLookup.wasSuccessful()) {
            return null;
        }
        final List<ResolvedTestVariableReference> result = new ArrayList<ResolvedTestVariableReference>();
        final List<VariableDeclaration> variableDeclarations = tryTestVariableDeclaration(variableReferenceIdentifier);
        for (final VariableDeclaration variableDeclaration : variableDeclarations) {
            result.add(new ResolvedTestVariableReference(variableDeclaration));
        }
        return result;
    }

    /**
     * Resolves the given variable reference using the given {@link ComplexReferenceIdentifier},
     * which supports references to both test variables and referenced item variables using 'dotted'
     * notation defined in the discussion of the <code>variable</code> class in the QTI information model.
     * <p>
     * The resolution will give a single result unless there are duplicate variables having
     * the same identifier (in which case the test is not considered valid).
     * <p>
     * Return null if the test lookup was unsuccessful.
     * <p>
     * (Note that while tests normally only contain outcome variables, there is also the built-in
     * <code>duration</code> variable, which is a response variable. Hence this returns a
     * {@link VariableDeclaration} rather than anything more specific.)
     *
     * @param variableReferenceIdentifier
     */
    public List<ResolvedTestVariableReference> resolveVariableReference(final ComplexReferenceIdentifier variableReferenceIdentifier) {
        if (!testLookup.wasSuccessful()) {
            return null;
        }
        final List<ResolvedTestVariableReference> result = new ArrayList<ResolvedTestVariableReference>();

        /* Split the reference across any '.' characters */
        final String[] components = variableReferenceIdentifier.toString().split("\\.");
        if (components.length==1) {
            /* No dots, so it must be a local test variable reference */
            final List<VariableDeclaration> variableDeclarations = tryTestVariableDeclaration(Identifier.parseString(components[0]));
            for (final VariableDeclaration variableDeclaration : variableDeclarations) {
                result.add(new ResolvedTestVariableReference(variableDeclaration));
            }
        }
        else if (components.length==2) {
            /* It's a ITEMREF.ITEMVAR reference (hopefully) */
            final Identifier itemRefIdentifier = Identifier.assumedLegal(components[0]);
            final Identifier itemVarIdentifier = Identifier.assumedLegal(components[1]);
            for (final AssessmentItemRef itemRef : assessmentItemRefs) {
                if (itemRefIdentifier.equals(itemRef.getIdentifier())) {
                    final List<VariableDeclaration> itemVariableDeclarations = tryItemVariableDeclaration(itemRef, itemVarIdentifier);
                    if (itemVariableDeclarations!=null) {
                        for (final VariableDeclaration itemVariableDeclaration : itemVariableDeclarations) {
                            result.add(new ResolvedTestVariableReference(itemRef, itemVariableDeclaration));
                        }
                    }
                }
            }
        }
        else if (components.length==3) {
            /* It's an ITEMREF.n.ITEMVAR reference (hopefully) */
            try {
                final Integer instanceNumber = Integer.parseInt(components[1]);
                final Identifier itemRefIdentifier = Identifier.assumedLegal(components[0]);
                final Identifier itemVarIdentifier = Identifier.assumedLegal(components[2]);
                for (final AssessmentItemRef itemRef : assessmentItemRefs) {
                    if (itemRefIdentifier.equals(itemRef.getIdentifier())) {
                        final List<VariableDeclaration> itemVariableDeclarations = tryItemVariableDeclaration(itemRef, itemVarIdentifier);
                        if (itemVariableDeclarations!=null) {
                            for (final VariableDeclaration itemVariableDeclaration : itemVariableDeclarations) {
                                result.add(new ResolvedTestVariableReference(itemRef, itemVariableDeclaration, instanceNumber));
                            }
                        }
                    }
                }
            }
            catch (final NumberFormatException e) {
                /* Not an instance number, so ignore */
            }
        }
        return result;
    }

    private List<VariableDeclaration> tryTestVariableDeclaration(final Identifier possibleTestVariableIdentifier) {
        final AssessmentTest test = testLookup.extractAssumingSuccessful();
        final List<VariableDeclaration> result = new ArrayList<VariableDeclaration>();
        if (possibleTestVariableIdentifier.equals(QtiConstants.VARIABLE_DURATION_IDENTIFIER)) {
            result.add(test.getDurationResponseDeclaration());
        }
        else {
            for (final OutcomeDeclaration outcomeDeclaration : test.getOutcomeDeclarations()) {
                if (outcomeDeclaration.getIdentifier().equals(possibleTestVariableIdentifier)) {
                    result.add(outcomeDeclaration);
                }
            }
        }
        return result;
    }

    private List<VariableDeclaration> tryItemVariableDeclaration(final AssessmentItemRef itemRef, final Identifier possibleItemVariableIdentifier) {
        final Identifier possibleMappedItemVarIdentifier = itemRef.resolveVariableMapping(possibleItemVariableIdentifier);
        final ResolvedAssessmentItem resolvedItem = getResolvedAssessmentItem(itemRef);
        return resolvedItem.resolveVariableReference(possibleMappedItemVarIdentifier);
    }

    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(testLookup=" + testLookup
                + ",systemIdByItemRefMap=" + systemIdByItemRefMap
                + ",itemRefsBySystemIdMap=" + itemRefsBySystemIdMap
                + ",resolvedAssessmentItemMap=" + resolvedAssessmentItemBySystemIdMap
                + ")";
    }
}
