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
package org.qtitools.qti.node.test;

import static org.junit.Assert.assertEquals;

import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class LookupItemsTest {

    /**
     * Creates test data for this test.
     * 
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // null identifier, null or empty include and exclude categories
                // (all items)
                { "LookupItems-01.xml", null, null, null
                        , new String[] { "I01", "I02", "I03", "I04", "I05", "I06", "I07", "I08", "I09" } },
                { "LookupItems-01.xml", null, new String[] {}, null
                        , new String[] { "I01", "I02", "I03", "I04", "I05", "I06", "I07", "I08", "I09" } },
                { "LookupItems-01.xml", null, null, new String[] {}
                        , new String[] { "I01", "I02", "I03", "I04", "I05", "I06", "I07", "I08", "I09" } },
                { "LookupItems-01.xml", null, new String[] {}, new String[] {}
                        , new String[] { "I01", "I02", "I03", "I04", "I05", "I06", "I07", "I08", "I09" } },
                // not null identifier (from test to items), null include and
                // exclude categories
                { "LookupItems-01.xml", "Test", null, null
                        , new String[] { "I01", "I02", "I03", "I04", "I05", "I06", "I07", "I08", "I09" } }, { "LookupItems-01.xml", "P01", null, null
                        , new String[] { "I01", "I02", "I03", "I04", "I05", "I06", "I07", "I08", "I09" } },
                { "LookupItems-01.xml", "S01", null, null, new String[] { "I01" } }, { "LookupItems-01.xml", "S02", null, null
                        , new String[] { "I02", "I03", "I04", "I05", "I06", "I07", "I08" } },
                { "LookupItems-01.xml", "S03", null, null, new String[] { "I02", "I03" } },
                { "LookupItems-01.xml", "S04", null, null, new String[] { "I05", "I06", "I07", "I08" } },
                { "LookupItems-01.xml", "S05", null, null, new String[] { "I06", "I07" } },
                { "LookupItems-01.xml", "S06", null, null, new String[] { "I09" } }, { "LookupItems-01.xml", "I01", null, null, new String[] { "I01" } },
                { "LookupItems-01.xml", "I02", null, null, new String[] { "I02" } }, { "LookupItems-01.xml", "I03", null, null, new String[] { "I03" } },
                { "LookupItems-01.xml", "I04", null, null, new String[] { "I04" } }, { "LookupItems-01.xml", "I05", null, null, new String[] { "I05" } },
                { "LookupItems-01.xml", "I06", null, null, new String[] { "I06" } }, { "LookupItems-01.xml", "I07", null, null, new String[] { "I07" } },
                { "LookupItems-01.xml", "I08", null, null, new String[] { "I08" } }, { "LookupItems-01.xml", "I09", null, null, new String[] { "I09" } },
                // not used identifier (empty result)
                { "LookupItems-01.xml", "XXX", null, null, new String[] {} },
                // null identifier, not empty include categories, null exclude
                // categories
                { "LookupItems-01.xml", null, new String[] { "A", "B", "C" }, null
                        , new String[] { "I01", "I02", "I03", "I05", "I06", "I08", "I09" } }, { "LookupItems-01.xml", null, new String[] { "A", "B" }, null
                        , new String[] { "I01", "I02", "I05", "I06", "I08", "I09" } }, { "LookupItems-01.xml", null, new String[] { "A", "C" }, null
                        , new String[] { "I01", "I02", "I03", "I05", "I06", "I09" } }, { "LookupItems-01.xml", null, new String[] { "B", "C" }, null
                        , new String[] { "I01", "I02", "I03", "I05", "I06", "I08", "I09" } }, { "LookupItems-01.xml", null, new String[] { "A" }, null
                        , new String[] { "I01", "I02", "I05", "I09" } }, { "LookupItems-01.xml", null, new String[] { "B" }, null
                        , new String[] { "I02", "I05", "I06", "I08", "I09" } }, { "LookupItems-01.xml", null, new String[] { "C" }, null
                        , new String[] { "I01", "I02", "I03", "I06", "I09" } },
                // null identifier, null include categories, not empty exclude
                // categories
                { "LookupItems-01.xml", null, null, new String[] { "A", "B", "C" }
                        , new String[] { "I04", "I07" } }, { "LookupItems-01.xml", null, null, new String[] { "A", "B" }
                        , new String[] { "I03", "I04", "I07" } }, { "LookupItems-01.xml", null, null, new String[] { "A", "C" }
                        , new String[] { "I04", "I07", "I08" } }, { "LookupItems-01.xml", null, null, new String[] { "B", "C" }
                        , new String[] { "I04", "I07" } }, { "LookupItems-01.xml", null, null, new String[] { "A" }
                        , new String[] { "I03", "I04", "I06", "I07", "I08" } }, { "LookupItems-01.xml", null, null, new String[] { "B" }
                        , new String[] { "I01", "I03", "I04", "I07" } }, { "LookupItems-01.xml", null, null, new String[] { "C" }
                        , new String[] { "I04", "I05", "I07", "I08" } },
                // null identifier, not empty include and exclude categories
                { "LookupItems-01.xml", null, new String[] { "A", "B" }, new String[] { "C" }
                        , new String[] { "I05", "I08" } }, { "LookupItems-01.xml", null, new String[] { "A", "C" }, new String[] { "B" }
                        , new String[] { "I01", "I03" } }, { "LookupItems-01.xml", null, new String[] { "B", "C" }, new String[] { "A" }
                        , new String[] { "I03", "I06", "I08" } }, { "LookupItems-01.xml", null, new String[] { "A" }, new String[] { "B", "C" }
                        , new String[] {} }, { "LookupItems-01.xml", null, new String[] { "B" }, new String[] { "A", "C" }
                        , new String[] { "I08" } }, { "LookupItems-01.xml", null, new String[] { "C" }, new String[] { "A", "B" }
                        , new String[] { "I03" } }, { "LookupItems-01.xml", null, new String[] { "A" }, new String[] { "B" }
                        , new String[] { "I01" } }, { "LookupItems-01.xml", null, new String[] { "A" }, new String[] { "C" }
                        , new String[] { "I05" } }, { "LookupItems-01.xml", null, new String[] { "B" }, new String[] { "C" }
                        , new String[] { "I05", "I08" } }, { "LookupItems-01.xml", null, new String[] { "B" }, new String[] { "A" }
                        , new String[] { "I06", "I08" } }, { "LookupItems-01.xml", null, new String[] { "C" }, new String[] { "A" }
                        , new String[] { "I03", "I06" } }, { "LookupItems-01.xml", null, new String[] { "C" }, new String[] { "B" }
                        , new String[] { "I01", "I03" } },
                // null identifier, same include and exclude category (empty
                // result)
                { "LookupItems-01.xml", null, new String[] { "A" }, new String[] { "A" }, new String[] {} },
        });
    }

    private final String fileName;

    private final String identifier;

    private List<String> includeCategories;

    private List<String> excludeCategories;

    private final List<String> expectedItemIdentifiers;

    public LookupItemsTest(String fileName
            , String identifier
            , String[] includeCategories
            , String[] excludeCategories
            , String[] expectedItemIdentifiers) {
        this.fileName = fileName;
        this.identifier = identifier;

        if (includeCategories != null) {
            this.includeCategories = Arrays.asList(includeCategories);
        }

        if (excludeCategories != null) {
            this.excludeCategories = Arrays.asList(excludeCategories);
        }

        this.expectedItemIdentifiers = expectedItemIdentifiers != null
                ? Arrays.asList(expectedItemIdentifiers) : new ArrayList<String>();
    }

    @Test
    public void test() {
        final AssessmentTest test = new AssessmentTest();
        test.load(getClass().getResource(fileName), jqtiController);

        final List<AssessmentItemRef> items = test.lookupItemRefs(identifier, includeCategories, excludeCategories);

        final List<String> itemIdentifiers = new ArrayList<String>();
        for (final AssessmentItemRef item : items) {
            itemIdentifiers.add(item.getIdentifier());
        }

        assertEquals(expectedItemIdentifiers, itemIdentifiers);
    }
}
