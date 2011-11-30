/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
*/

package uk.ac.ed.ph.jqtiplus.node.test;

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;

import java.util.HashMap;
import java.util.Map;


/**
 * Test feedback is shown to the candidate either directly following outcome processing (during the test)
 * or at the end of the testPart or assessmentTest as appropriate (referred to as atEnd).
 * <p>
 * Possible values: during, atEnd
 * 
 * @author Jiri Kajaba
 */
public enum TestFeedbackAccess
{
    /** Feedback is shown during test/testPart. */
    DURING("during"),

    /** Feedback is shown at the end of test/testPart. */
    AT_END("atEnd");

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "access";

    private static Map<String, TestFeedbackAccess> testFeedbackAccesses;

    static
    {
        testFeedbackAccesses = new HashMap<String, TestFeedbackAccess>();

        for (TestFeedbackAccess testFeedbackAccess : TestFeedbackAccess.values())
            testFeedbackAccesses.put(testFeedbackAccess.testFeedbackAccess, testFeedbackAccess);
    }

    private String testFeedbackAccess;

    private TestFeedbackAccess(String testFeedbackAccess)
    {
        this.testFeedbackAccess = testFeedbackAccess;
    }

    @Override
    public String toString()
    {
        return testFeedbackAccess;
    }

    /**
     * Returns parsed <code>TestFeedbackAccess</code> from given <code>String</code>.
     *
     * @param testFeedbackAccess <code>String</code> representation of <code>TestFeedbackAccess</code>
     * @return parsed <code>TestFeedbackAccess</code> from given <code>String</code>
     * @throws QTIParseException if given <code>String</code> is not valid <code>TestFeedbackAccess</code>
     */
    public static TestFeedbackAccess parseTestFeedbackAccess(String testFeedbackAccess) throws QTIParseException
    {
        TestFeedbackAccess result = testFeedbackAccesses.get(testFeedbackAccess);

        if (result == null)
            throw new QTIParseException("Invalid " + CLASS_TAG + " '" + testFeedbackAccess + "'.");

        return result;
    }
}
