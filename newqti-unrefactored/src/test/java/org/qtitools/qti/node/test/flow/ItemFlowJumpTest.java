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
package org.qtitools.qti.node.test.flow;

import static org.junit.Assert.fail;

import uk.ac.ed.ph.jqtiplus.exception.QTIItemFlowException;
import uk.ac.ed.ph.jqtiplus.exception.QTIRuntimeException;
import uk.ac.ed.ph.jqtiplus.node.expression.general.BaseValue;
import uk.ac.ed.ph.jqtiplus.node.test.AbstractPart;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.BranchRule;
import uk.ac.ed.ph.jqtiplus.node.test.ControlObject;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests implementation of jump (branchRule) validation. Tests every possible combination.
 * <ol>
 * <li>loads given test</li>
 * <li>loads list of all valid targets for given nodes (sources)</li>
 * <li>tries to jump from every given node (source) to every node in test including itself</li>
 * <li>if target of jump is in valid targets list -> expects no errors or warnings during validation</li>
 * <li>if target of jump is not in valid targets list -> expects exactly one error (QTIItemFlowException) and no warning during validation</li>
 * </ol>
 */
@RunWith(Parameterized.class)
public class ItemFlowJumpTest {
    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        // One row is definition of all valid targets for one source.
        // In one row: First column is source (jump from). Next columns are targets (jump to).
        // It is possible that source has no valid targets (it is not possible to jump anywhere from this source).
        // (-> there is only one column in row)
        return Arrays.asList(new Object[][] { {"ItemFlow-jump-01.xml", new String[][] {
                new String[] {"P01"}, // It is not possible to jump anywhere from testPart P01.
                new String[] {"S01", "S02", "S03", "I02", "I03", "I04", "I05"},
                new String[] {"S02", "S03", "I05"}, // From section S02 you can jump to section S03 or item I05.
                new String[] {"S03"},
                new String[] {"I01", "S02", "S03", "I02", "I03", "I04", "I05"},
                new String[] {"I02", "S03", "I03", "I04", "I05"},
                new String[] {"I03", "S03", "I04", "I05"},
                new String[] {"I04", "S03", "I05"},
                new String[] {"I05"},
            }}, {"ItemFlow-jump-02.xml", new String[][] {
                new String[] {"P01", "P02", "P03", "P04", "P05"},
                new String[] {"P02", "P03", "P04", "P05"},
                new String[] {"P03", "P04", "P05"},
                new String[] {"P04", "P05"},
                new String[] {"P05"},
                //new String[] {"S01"},
                //new String[] {"S02"},
                //new String[] {"S03"},
                //new String[] {"S04"},
                new String[] {"S05"},
                //new String[] {"I01"},
                //new String[] {"I02"},
                //new String[] {"I03"},
                //new String[] {"I04"},
                new String[] {"I05"},
            }}, {"ItemFlow-jump-03.xml", new String[][] {
                new String[] {"P01", "P02", "P03"},
                new String[] {"P02", "P03"},
                new String[] {"P03"},
                //new String[] {"S01", "S02", "S03", "I04", "I05", "I06"},
                //new String[] {"S02", "S03", "I06"},
                //new String[] {"S03"},
                //new String[] {"S04", "S05", "S06", "S07", "S08", "S09", "I08", "I09", "I10", "I11", "I12", "I13"},
                //new String[] {"S05", "S09", "I13"},
                //new String[] {"S06", "S07", "S08", "S09", "I10", "I11", "I12", "I13"},
                //new String[] {"S07", "S09", "I13"},
                //new String[] {"S08", "S09", "I13"},
                //new String[] {"S09"},
                new String[] {"S10", "S11", "S12", "I16", "I17", "I18", "I19"},
                new String[] {"S11", "S12", "I17", "I18", "I19"},
                new String[] {"S12"},
                //new String[] {"I01", "S02", "S03", "I02", "I03", "I04", "I05", "I06"},
                //new String[] {"I02", "S02", "S03", "I03", "I04", "I05", "I06"},
                //new String[] {"I03", "S02", "S03", "I04", "I05", "I06"},
                //new String[] {"I04", "S03", "I05", "I06"},
                //new String[] {"I05", "S03", "I06"},
                //new String[] {"I06"},
                //new String[] {"I07", "S05", "S06", "S07", "S08", "S09", "I08", "I09", "I10", "I11", "I12", "I13"},
                //new String[] {"I08", "S07", "S08", "S09", "I09", "I10", "I11", "I12", "I13"},
                //new String[] {"I09", "S07", "S08", "S09", "I10", "I11", "I12", "I13"},
                //new String[] {"I10", "S07", "S08", "S09", "I11", "I12", "I13"},
                //new String[] {"I11", "S09", "I12", "I13"},
                //new String[] {"I12", "S09", "I13"},
                //new String[] {"I13"},
                new String[] {"I14", "S11", "S12", "I15", "I16", "I17", "I18", "I19"},
                new String[] {"I15", "S11", "S12", "I16", "I17", "I18", "I19"},
                new String[] {"I16", "S12", "I17", "I18", "I19"},
                new String[] {"I17", "I18", "I19"},
                new String[] {"I18", "I19"},
                new String[] {"I19"},
            }}
        });
    }

    private String fileName;
    private String[][] testMoves;

    /**
     * Constructs jump test.
     *
     * @param fileName file name of loaded test
     * @param testMoves definition of valid jumps for loaded test
     */
    public ItemFlowJumpTest(String fileName, String[][] testMoves) {
        this.fileName = fileName;
        this.testMoves = testMoves;
    }

    @Test
    public void test() {
        AssessmentTest test = new AssessmentTest();
        test.load(getClass().getResource("jump/" + fileName), jqtiController);

        List<String> allTargets = getAllTargets(test);

        for (String[] nodeMoves : testMoves) {
            String source = nodeMoves[0];

            // Tests valid moves.
            List<String> validTargets = new ArrayList<String>();
            validTargets.addAll(Arrays.asList(nodeMoves));
            validTargets.remove(source);

            for (String target : validTargets)
                testJump(test, source, target, 0, 0);

            // Tests invalid moves.
            List<String> invalidTargets = new ArrayList<String>();
            invalidTargets.addAll(allTargets);
            invalidTargets.removeAll(validTargets);

            for (String target : invalidTargets)
                testJump(test, source, target, 1, 0);
        }
    }

    /**
     * Gets list of all identifiers for given parent (including parent).
     * If parent is test, returns list of identifiers (test + all test parts + all section + all items).
     * This method calls itself in recursion.
     *
     * @param parent given parent
     * @return list of all identifiers for given parent (including parent)
     */
    private List<String> getAllTargets(ControlObject parent) {
        List<String> targets = new ArrayList<String>();

        targets.add(parent.getIdentifier());

        for (ControlObject child : parent.getChildren())
            targets.addAll(getAllTargets(child));

        return targets;
    }

    /**
     * Tests one jump in given test.
     * <ol>
     * <li>injects jump (branchRule) into test</li>
     * <li>validates test</li>
     * <li>evaluates validation</li>
     * <li>if validation failed -> throws exception</li>
     * <li>if validation succeeded -> removes injected jump (branchRule) from test</li>
     * </ol>
     *
     * @param test given test
     * @param source source of jump
     * @param target target of jump
     * @param expectedErrorsCount number of expected errors during validation
     * @param expectedWarningsCount number of expected warnings during validation
     */
    private void testJump(AssessmentTest test, String source, String target, int expectedErrorsCount, int expectedWarningsCount) {
        addBranchRule(test, source, target);

        ValidationResult result = test.validate(context, this);

        // if (result.getErrors().size() != expectedErrorsCount || result.getWarnings().size() != expectedWarningsCount)
        if (result.getErrors().size() != expectedErrorsCount)
            fail(createMessage(source, target, expectedErrorsCount, result.getErrors().size(), expectedWarningsCount, result.getWarnings().size()));

        if (expectedErrorsCount == 1) {
            QTIRuntimeException exception = result.getErrors().get(0).createException();
            if (!exception.getClass().equals(QTIItemFlowException.class))
                fail("Unexpected exception, expected<" + QTIItemFlowException.class.getName() +
                        "> but was<" + exception.getClass().getName() + "> with message: " + exception.getMessage());
        }

        removeBranchRule(test, source);
    }

    /**
     * Injects jump (branchRule) into given test.
     *
     * @param test given test
     * @param source source of jump
     * @param target target of jump
     */
    private void addBranchRule(AssessmentTest test, String source, String target) {
        AbstractPart node = (AbstractPart) test.lookupDescendentOrSelf(source);

        BranchRule rule = new BranchRule(node);
        node.getBranchRules().add(rule);
        rule.setTarget(target);

        BaseValue condition = new BaseValue(rule);
        rule.setExpression(condition);
        condition.setBaseTypeAttrValue(BaseType.BOOLEAN);
        condition.setSingleValue(BooleanValue.TRUE);
    }

    /**
     * Removes all jumps (branchRules) from given source in given test.
     *
     * @param test given test
     * @param source source of jump
     */
    private void removeBranchRule(AssessmentTest test, String source) {
        AbstractPart node = (AbstractPart) test.lookupDescendentOrSelf(source);
        node.getBranchRules().clear();
    }

    /**
     * Creates exception message in case of failed validation.
     *
     * @param source source of jump
     * @param target target of jump
     * @param expectedErrorsCount number of expected errors during validation
     * @param errorsCount number of found errors during validation
     * @param expectedWarningsCount number of expected warnings during validation
     * @param warningsCount number of found warnings during validation
     * @return created exception message
     */
    private String createMessage
        ( String source
        , String target
        , int expectedErrorsCount
        , int errorsCount
        , int expectedWarningsCount
        , int warningsCount ) {
        StringBuilder builder = new StringBuilder();

        builder.append("Testing jump from ");
        builder.append(source);
        builder.append(" to ");
        builder.append(target);
        builder.append(" failed. Expected errors count: ");
        builder.append(expectedErrorsCount);
        builder.append(", found: ");
        builder.append(errorsCount);
        builder.append(". Expected warnings count: ");
        builder.append(expectedWarningsCount);
        builder.append(", found: ");
        builder.append(warningsCount);
        builder.append(".");

        return builder.toString();
    }
}
