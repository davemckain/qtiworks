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
package uk.ac.ed.ph.jqtiplus.resolution;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /** List of all{@link AssessmentItemRef}s in the test, in test order. */
    private final List<AssessmentItemRef> itemRefs;

    /**
     * Lookup map for {@link AssessmentItemRef} by identifier. Valid tests should have one
     * entry in the value per key, but invalid tests might have multiple entries.
     */
    private final Map<Identifier, List<AssessmentItemRef>> itemRefsByIdentifierMap;

    /** Resolved System ID for each {@link AssessmentItemRef} */
    private final Map<AssessmentItemRef, URI> systemIdByItemRefMap;

    /** List of {@link AssessmentItemRef}s corresponding to each unique resolved item System ID */
    private final Map<URI, List<AssessmentItemRef>> itemRefsBySystemIdMap;

    /** {@link ResolvedAssessmentItem} for each unique item System ID. */
    private final Map<URI, ResolvedAssessmentItem> resolvedAssessmentItemMap;

    public ResolvedAssessmentTest(final ModelRichness modelRichness,
            final RootNodeLookup<AssessmentTest> testLookup,
            final List<AssessmentItemRef> itemRefs,
            final Map<Identifier, List<AssessmentItemRef>> itemRefsByIdentifierMap,
            final Map<AssessmentItemRef, URI> systemIdByItemRefMap,
            final Map<URI, List<AssessmentItemRef>> itemRefsBySystemIdMap,
            final Map<URI, ResolvedAssessmentItem> resolvedAssessmentItemMap) {
        super(modelRichness, testLookup);
        this.testLookup = testLookup;
        this.itemRefs = Collections.unmodifiableList(itemRefs);
        this.itemRefsByIdentifierMap = Collections.unmodifiableMap(itemRefsByIdentifierMap);
        this.systemIdByItemRefMap = Collections.unmodifiableMap(systemIdByItemRefMap);
        this.itemRefsBySystemIdMap = Collections.unmodifiableMap(itemRefsBySystemIdMap);
        this.resolvedAssessmentItemMap = Collections.unmodifiableMap(resolvedAssessmentItemMap);
    }

    @Override
    public AssessmentObjectType getType() {
        return AssessmentObjectType.ASSESSMENT_TEST;
    }

    public RootNodeLookup<AssessmentTest> getTestLookup() {
        return testLookup;
    }

    public List<AssessmentItemRef> getItemRefs() {
        return itemRefs;
    }

    public Map<AssessmentItemRef, URI> getSystemIdByItemRefMap() {
        return systemIdByItemRefMap;
    }

    public Map<Identifier, List<AssessmentItemRef>> getItemRefsByIdentifierMap() {
        return itemRefsByIdentifierMap;
    }

    public Map<URI, List<AssessmentItemRef>> getItemRefsBySystemIdMap() {
        return itemRefsBySystemIdMap;
    }

    @ObjectDumperOptions(DumpMode.TO_STRING)
    public Map<URI, ResolvedAssessmentItem> getResolvedAssessmentItemMap() {
        return resolvedAssessmentItemMap;
    }

    public ResolvedAssessmentItem getResolvedAssessmentItem(final AssessmentItemRef itemRef) {
        final URI systemId = systemIdByItemRefMap.get(itemRef);
        return systemId!=null ? resolvedAssessmentItemMap.get(systemId) : null;
    }

    //-------------------------------------------------------------------

    /**
     * Resolves the given variable reference, within this test only.
     * <p>
     * The resolution returns a {@link List} of matching {@link OutcomeDeclaration}s, or null if the
     * test lookup was unsuccessful. The List will ideally contain 1 element, but may contain 0
     * elements (if no match is found) or more than 1 element (if there are multiple {@link OutcomeDeclaration}s
     * having the same identifier.
     *
     * @param variableReferenceIdentifier
     */
    public List<OutcomeDeclaration> resolveTestVariable(final Identifier variableReferenceIdentifier) {
        if (!testLookup.wasSuccessful()) {
            return null;
        }
        return tryTestVariableDeclaration(variableReferenceIdentifier);
    }

    /**
     * Resolves the given variable reference, supporting references to both test variables and
     * referenced item variables using 'dotted' notation defined in the discussion of the
     * <code>variable</code> class in the QTI information model.
     * <p>
     * The resolution is not guaranteed to return a single result, even if identifiers are all
     * correctly unique, as the dotted notation is ambiguous. Therefore a List of matches is
     * returned; the first of these being considered the "best" option if the test is valid.
     * <p>
     * Return null if the test lookup was unsuccessful.
     *
     * @param variableReferenceIdentifier
     */
    public List<ResolvedTestVariableReference> resolveVariableReference(final Identifier variableReferenceIdentifier) {
        if (!testLookup.wasSuccessful()) {
            return null;
        }
        final String reference = variableReferenceIdentifier.toString();
        final int dotPos = reference.indexOf('.');
        final List<ResolvedTestVariableReference> result = new ArrayList<ResolvedTestVariableReference>();
        if (dotPos==-1) {
            /* Reference contains no dot, so *must* be referring to a variable within this test */
            final List<OutcomeDeclaration> outcomeDeclarations = tryTestVariableDeclaration(Identifier.parseString(reference));
            for (final OutcomeDeclaration outcomeDeclaration : outcomeDeclarations) {
                result.add(new ResolvedTestVariableReference(outcomeDeclaration));
            }
        }
        else {
            /* Identifier contains a dot, so will be of the form:
             *
             * TESTVAR (containing a dot)
             * ITEMREF.ITEMVAR
             * or ITEMREF.n.ITEMVAR
             *
             * However, both TESTVAR and ITEMVAR are identifiers so may themselves contains dots and numbers,
             * so there are possibly multiple resolutions. We return all possibilities, checked as follows
             * as follows:
             *
             * (1) All successful matches of the form ITEMREF.n.ITEMVAR, running through all ITEMREFS in the order listed in the test and checking for an item variable with identifier ITEMVAR.
             * (2) All successful matches of the form ITEMREF.ITEMVAR, running through all ITEMREFS in the order listed in the test and checking for an item variable with identifier ITEMVAR
             * (3) All successful matches of the form TESTVAR, checking for a test variable with identifier TESTVAR (containing a dot)
             */
            for (final AssessmentItemRef itemRef : itemRefs) { /*  (1) above */
                final Identifier itemRefIdentifier = itemRef.getIdentifier();
                final Pattern pattern = Pattern.compile("^" + itemRefIdentifier + "\\.(\\d+)\\.(\\p{L}.+)$");
                final Matcher matcher = pattern.matcher(itemRefIdentifier.toString());
                if (matcher.matches()) {
                    final Integer instanceNumber = Integer.valueOf(matcher.group(1));
                    final Identifier possibleItemVariableIdentifier = Identifier.assumedLegal(matcher.group(2));

                    final List<VariableDeclaration> itemVariableDeclarations = tryItemVariableDeclaration(itemRef, possibleItemVariableIdentifier);
                    if (itemVariableDeclarations!=null) {
                        for (final VariableDeclaration itemVariableDeclaration : itemVariableDeclarations) {
                            result.add(new ResolvedTestVariableReference(itemRef, itemVariableDeclaration, instanceNumber));
                        }
                    }
                }
            }
            for (final AssessmentItemRef itemRef : itemRefs) { /*  (2) above */
                final Identifier itemRefIdentifier = itemRef.getIdentifier();
                final Pattern pattern = Pattern.compile("^" + itemRefIdentifier + "\\.(\\p{L}.+)$");
                final Matcher matcher = pattern.matcher(itemRefIdentifier.toString());
                if (matcher.matches()) {
                    final Identifier possibleItemVariableIdentifier = Identifier.assumedLegal(matcher.group(1));

                    final List<VariableDeclaration> itemVariableDeclarations = tryItemVariableDeclaration(itemRef, possibleItemVariableIdentifier);
                    if (itemVariableDeclarations!=null) {
                        for (final VariableDeclaration itemVariableDeclaration : itemVariableDeclarations) {
                            result.add(new ResolvedTestVariableReference(itemRef, itemVariableDeclaration));
                        }
                    }
                }
            }
            /* (3) above */
            final List<OutcomeDeclaration> outcomeDeclarations = tryTestVariableDeclaration(Identifier.parseString(reference));
            for (final OutcomeDeclaration outcomeDeclaration : outcomeDeclarations) {
                result.add(new ResolvedTestVariableReference(outcomeDeclaration));
            }
        }
        return result;
    }

    private List<OutcomeDeclaration> tryTestVariableDeclaration(final Identifier possibleTestVariableIdentifier) {
        final AssessmentTest test = testLookup.extractAssumingSuccessful();
        final List<OutcomeDeclaration> result = new ArrayList<OutcomeDeclaration>();
        for (final OutcomeDeclaration outcomeDeclaration : test.getOutcomeDeclarations()) {
            if (outcomeDeclaration.getIdentifier().equals(possibleTestVariableIdentifier)) {
                result.add(outcomeDeclaration);
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
                + "(modelRichness=" + modelRichness
                + ",testLookup=" + testLookup
                + ",itemRefsByIdentifierMap=" + itemRefsByIdentifierMap
                + ",systemIdByItemRefMap=" + systemIdByItemRefMap
                + ",itemRefsBySystemIdMap=" + itemRefsBySystemIdMap
                + ",resolvedAssessmentItemMap=" + resolvedAssessmentItemMap
                + ")";
    }
}
