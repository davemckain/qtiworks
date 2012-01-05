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

import uk.ac.ed.ph.jqtiplus.exception.QTIItemFlowException;
import uk.ac.ed.ph.jqtiplus.exception.QTIRuntimeException;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ItemFlowTPTest extends ItemFlowTest {
    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            // preCondition {"ItemFlow-pre-01.xml", null, null, new String[][] {{"I01"}, {"I02"}, {"I03"}}}, {"ItemFlow-pre-02.xml", null, null, new String[][] {{"I01"}, {"I02"}, {"I03"}}}, {"ItemFlow-pre-03.xml", null, null, new String[][] {}}, {"ItemFlow-pre-04.xml", null, null, new String[][] {{"I02"}}}, {"ItemFlow-pre-05.xml", null, null, new String[][] {{"I01"}, {"I03"}}}, {"ItemFlow-pre-06.xml", null, null, new String[][] {{"I01"}, {"I03"}}},
            // branchRule {"ItemFlow-jump-01.xml", null, null, new String[][] {{"I01"}, {"I03"}, {"I04"}, {"I05"}}}, {"ItemFlow-jump-02.xml", null, null, new String[][] {{"I01"}, {"I05"}}}, {"ItemFlow-jump-03.xml", null, null, new String[][] {{"I01"}, {"I03"}, {"I04"}, {"I05"}}}, {"ItemFlow-jump-back-01.xml", null, QTIItemFlowException.class, new String[][] {{"I01"}, {"I03"}}}, {"ItemFlow-jump-back-02.xml", null, QTIItemFlowException.class, new String[][] {{"I01"}, {"I03"}}}, {"ItemFlow-jump-back-03.xml", null, QTIItemFlowException.class, new String[][] {{"I01"}, {"I03"}, {"I05"}}}, {"ItemFlow-jump-back-04.xml", null, QTIItemFlowException.class, new String[][] {{"I01"}, {"I03"}, {"I05"}}}, {"ItemFlow-jump-error-01.xml", null, QTIItemFlowException.class, new String[][] {{"I01"}}}, {"ItemFlow-jump-error-02.xml", null, QTIItemFlowException.class, new String[][] {{"I01"}}}, {"ItemFlow-jump-error-03.xml", null, QTIItemFlowException.class, new String[][] {{"I01"}}}, {"ItemFlow-jump-error-04.xml", null, QTIItemFlowException.class, new String[][] {{"I01"}}}, {"ItemFlow-jump-special-01.xml", null, null, new String[][] {{"I01"}}}, {"ItemFlow-jump-special-02.xml", null, QTIItemFlowException.class, new String[][] {{"I01"}}}, {"ItemFlow-jump-special-03.xml", null, QTIItemFlowException.class, new String[][] {{"I01"}}}, {"ItemFlow-jump-special-04.xml", null, null, new String[][] {{"I01"}, {"I03"}, {"I05"}}},
            // preCondition + branchRule {"ItemFlow-full-01.xml", null, null, new String[][] {{"I02"}, {"I04"}, {"I08"}, {"I09"}}},
        });
    }

    public ItemFlowTPTest(String fileName, Class<? extends QTIRuntimeException> hasNextException, Class<? extends QTIRuntimeException> nextException, String[][] identifiers) {
        super("tp/" + fileName, new Step[] {});
    }
}
