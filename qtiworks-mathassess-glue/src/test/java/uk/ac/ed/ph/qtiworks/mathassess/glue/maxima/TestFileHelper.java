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
package uk.ac.ed.ph.qtiworks.mathassess.glue.maxima;

import uk.ac.ed.ph.snuggletex.internal.util.IOUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Trivial helper class to read in test file data
 *
 * @author David McKain
 */
public final class TestFileHelper {

    /**
     * Reads in the given "single line" test file, assuming it is of the format
     * <pre>
     * single line input
     * 1 or more lines of output
     * ==== (divider token, at least 4 characters)
     * ...
     * </pre>
     * 
     * Returns a List of [input,output] pairs.
     * 
     * @throws Exception
     */
    public static Collection<String[]> readAndParseSingleLineInputTestResource(String resourceName) throws Exception {
        String testData = ensureGetResource(resourceName);
        testData = testData.replaceAll("(?m)^#.*$(\\s+)(^|$)", "");
        String[] testItems = testData.split("(?m)\\s*^={4,}\\s*");
        Collection<String[]> result = new ArrayList<String[]>(testItems.length);
        for (String testItem : testItems) {
            result.add(testItem.split("\n+", 2));
        }
        return result;
    }
    
    /**
     * Reads in the given "multiple line" test file, assuming it is of the format
     * <pre>
     * 1 or more lines of output input
     * ---- (divider token, at least 4 characters)
     * 1 or more lines of output
     * ==== (divider token, at least 4 characters)
     * ...
     * </pre>
     * 
     * Returns a List of [input,output] pairs.
     * 
     * @throws Exception
     */
    public static Collection<String[]> readAndParseMultiLineInputTestResource(String resourceName) throws Exception {
        String testData = ensureGetResource(resourceName);
        testData = testData.replaceAll("(?m)^#.*$(\\s+)(^|$)", "");
        String[] testItems = testData.split("(?m)\\s*^={4,}\\s*");
        Collection<String[]> result = new ArrayList<String[]>(testItems.length);
        for (String testItem : testItems) {
            result.add(testItem.split("(?m)\\s*-{4,}\\s*", 2));
        }
        return result;
    }
    
    private static String ensureGetResource(String resourceName) throws IOException {
        InputStream resourceStream = TestFileHelper.class.getClassLoader().getResourceAsStream(resourceName);
        if (resourceStream==null) {
            throw new RuntimeException("Could not load Resource '" + resourceName
                    + "' via ClassLoader - check the ClassPath!");
        }
        return IOUtilities.readUnicodeStream(resourceStream);
    }
}
