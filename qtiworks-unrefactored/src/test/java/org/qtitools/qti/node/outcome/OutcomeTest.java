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
package org.qtitools.qti.node.outcome;

import static org.junit.Assert.assertEquals;

import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.OrderedValue;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
import uk.ac.ed.ph.jqtiplus.value.StringValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class OutcomeTest {

    private static final MultipleValue multipleValue;
    private static final OrderedValue orderedValue;
    private static final RecordValue recordValue;

    static {
        multipleValue = MultipleValue.emptyValue();
        multipleValue.add(new StringValue("DEFAULT 1"));
        multipleValue.add(new StringValue("DEFAULT 2"));
        multipleValue.add(new StringValue("DEFAULT 3"));

        orderedValue = OrderedValue.emptyValue();
        orderedValue.add(new StringValue("DEFAULT 1"));
        orderedValue.add(new StringValue("DEFAULT 2"));
        orderedValue.add(new StringValue("DEFAULT 3"));

        recordValue = RecordValue.emptyRecord();
        recordValue.add("IDENTIFIER_1", new StringValue("DEFAULT 1"));
        recordValue.add("IDENTIFIER_2", new StringValue("DEFAULT 2"));
        recordValue.add("IDENTIFIER_3", new StringValue("DEFAULT 3"));
    }

    private static class Outcome {

        private final String name;
        private final Value expectedValue;

        public Outcome(String name, Value expectedValue) {
            this.name = name;
            this.expectedValue = expectedValue;
        }

        public String getName() {
            return name;
        }

        public Value getExpectedValue() {
            return expectedValue;
        }
    }

    /**
     * Creates test data for this test.
     * 
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { 
                { "Outcome-default-01.xml", new Outcome[] {
                        new Outcome("Outcome01", NullValue.INSTANCE),
                        new Outcome("Outcome02", new StringValue("DEFAULT")),
                        new Outcome("Outcome03", multipleValue),
                        new Outcome("Outcome04", orderedValue),
                        new Outcome("Outcome05", recordValue),
                    }
                }, 
                { "Outcome-default-02.xml", new Outcome[] {
                        new Outcome("Outcome01", NullValue.INSTANCE),
                        new Outcome("Outcome02", new StringValue("DEFAULT")),
                        new Outcome("Outcome03", multipleValue),
                        new Outcome("Outcome04", orderedValue),
                        new Outcome("Outcome05", recordValue),
                    } 
                }, 
                { "Outcome-set-01.xml", new Outcome[] {
                        new Outcome("Outcome01", NullValue.INSTANCE),
                        new Outcome("Outcome02", new StringValue("DEFAULT")),
                        new Outcome("Outcome03", new StringValue("VALUE")),
                    } 
                },
                { "Outcome-lookup-match-01.xml", new Outcome[] {
                        new Outcome("Outcome01", NullValue.INSTANCE),
                        new Outcome("Outcome02", new StringValue("DEFAULT")),
                        new Outcome("Outcome03", new StringValue("VALUE 1")),
                        new Outcome("Outcome04", new StringValue("VALUE 2")),
                        new Outcome("Outcome05", new StringValue("VALUE 3")),
                        new Outcome("Outcome06", new StringValue("VALUE DEFAULT")),
                        new Outcome("Outcome07", new StringValue("VALUE DEFAULT")),
                    }
                },
                { "Outcome-lookup-interpolation-01.xml", new Outcome[] {
                        new Outcome("Outcome01", NullValue.INSTANCE),
                        new Outcome("Outcome02", new StringValue("DEFAULT")),
                        new Outcome("Outcome03", new StringValue("VALUE 3")),
                        new Outcome("Outcome04", new StringValue("VALUE 3")),
                        new Outcome("Outcome05", new StringValue("VALUE 2")),
                        new Outcome("Outcome06", new StringValue("VALUE 2")),
                        new Outcome("Outcome07", new StringValue("VALUE 2")),
                        new Outcome("Outcome08", new StringValue("VALUE 1")),
                        new Outcome("Outcome09", new StringValue("VALUE 1")),
                        new Outcome("Outcome10", new StringValue("VALUE 1")),
                        new Outcome("Outcome11", new StringValue("VALUE DEFAULT")),
                        new Outcome("Outcome12", new StringValue("VALUE DEFAULT")),
                        new Outcome("Outcome13", new StringValue("VALUE DEFAULT")),
                    } 
                }, 
                { "Outcome-condition-01.xml", new Outcome[] {
                        new Outcome("Outcome01", NullValue.INSTANCE),
                        new Outcome("Outcome02", new StringValue("DEFAULT")),
                        new Outcome("Outcome03", NullValue.INSTANCE),
                        new Outcome("Outcome04", new StringValue("IF")),
                        new Outcome("Outcome05", new StringValue("ELSE")),
                        new Outcome("Outcome06", new StringValue("IF")),
                        new Outcome("Outcome07", new StringValue("ELSE")),
                        new Outcome("Outcome08", new StringValue("ELSE IF")),
                        new Outcome("Outcome09", new StringValue("IF")),
                        new Outcome("Outcome10", new StringValue("IF")),
                        new Outcome("Outcome11", new StringValue("ELSE")),
                        new Outcome("Outcome12", new StringValue("ELSE IF 2")),
                        new Outcome("Outcome13", new StringValue("ELSE IF 1")),
                        new Outcome("Outcome14", new StringValue("IF")),
                    } 
                },
        });
    }

    private final String fileName;
    private final Outcome[] outcomes;

    public OutcomeTest(String fileName, Outcome[] outcomes) {
        this.fileName = fileName;
        this.outcomes = outcomes;
    }

    @Test
    public void test() {
        final AssessmentTest test = new AssessmentTest();
        test.load(getClass().getResource(fileName), jqtiController);

        test.processOutcome();

        for (final Outcome outcome : outcomes) {
            final Value value = test.getOutcomeValue(outcome.getName());
            assertEquals(outcome.getExpectedValue(), value);
        }
    }
}
