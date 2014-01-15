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
package uk.ac.ed.ph.jqtiplus.reading;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import uk.ac.ed.ph.jqtiplus.testutils.UnitTestHelper;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;

import java.net.URI;

import org.junit.Test;

/**
 * Tests for the {@link QtiXmlReader}
 *
 * @author David McKain
 */
public class QtiXmlReaderTest {

    @Test(expected=IllegalArgumentException.class)
    public void testReadNullUri() throws Exception {
        new QtiXmlReader().read(new ClassPathResourceLocator(), null, false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testReadNullLocator() throws Exception {
        new QtiXmlReader().read(null, makeSystemId("choice.xml"), false);
    }

    @Test(expected=XmlResourceNotFoundException.class)
    public void testReadNotFound() throws Exception {
        readUnitTestFile("notfound.xml", false);
    }

    @Test
    public void testGoodReadNoValidate() throws Exception {
        final String fileName = "choice.xml";
        final XmlReadResult result = readUnitTestFile(fileName, false);
        final XmlParseResult parseResult = result.getXmlParseResult();

        assertNotNull(result.getDocument());
        assertEquals(makeSystemId(fileName), parseResult.getSystemId());
        assertTrue(parseResult.isParsed());
        assertFalse(parseResult.isValidated());
        assertFalse(parseResult.isSchemaValid());
        assertEquals(0, parseResult.getFatalErrors().size());
        assertEquals(0, parseResult.getErrors().size());
        assertEquals(0, parseResult.getWarnings().size());
        assertEquals(0, parseResult.getSupportedSchemaNamespaces().size());
        assertEquals(0, parseResult.getUnsupportedSchemaNamespaces().size());
    }

    @Test
    public void testGoodReadAndValidate() throws Exception {
        final String fileName = "choice.xml";
        final XmlReadResult result = readUnitTestFile(fileName, true);
        final XmlParseResult parseResult = result.getXmlParseResult();

        assertNotNull(result.getDocument());
        assertEquals(makeSystemId("choice.xml"), parseResult.getSystemId());
        assertTrue(parseResult.isParsed());
        assertTrue(parseResult.isValidated());
        assertTrue(parseResult.isSchemaValid());
        assertEquals(0, parseResult.getFatalErrors().size());
        assertEquals(0, parseResult.getErrors().size());
        assertEquals(0, parseResult.getWarnings().size());
        assertEquals(1, parseResult.getSupportedSchemaNamespaces().size()); /* (QTI 2.1) */
        assertEquals(0, parseResult.getUnsupportedSchemaNamespaces().size());
    }

    @Test
    public void testReadIllFormed() throws Exception {
        final String fileName = "illformed.xml";
        final XmlReadResult result = readUnitTestFile(fileName, false);
        final XmlParseResult parseResult = result.getXmlParseResult();

        assertNull(result.getDocument());
        assertEquals(makeSystemId(fileName), parseResult.getSystemId());
        assertFalse(parseResult.isParsed());
        assertFalse(parseResult.isValidated());
        assertFalse(parseResult.isSchemaValid());
        assertEquals(1, parseResult.getFatalErrors().size());
        assertEquals(0, parseResult.getErrors().size());
        assertEquals(0, parseResult.getWarnings().size());
    }

    @Test
    public void testReadNotValid() throws Exception {
        final String fileName = "invalid.xml";
        final XmlReadResult result = readUnitTestFile(fileName, true);
        final XmlParseResult parseResult = result.getXmlParseResult();

        assertNotNull(result.getDocument());
        assertEquals(makeSystemId(fileName), parseResult.getSystemId());
        assertTrue(parseResult.isParsed());
        assertTrue(parseResult.isValidated());
        assertFalse(parseResult.isSchemaValid());
        assertEquals(0, parseResult.getFatalErrors().size());
        assertEquals(1, parseResult.getErrors().size()); /* (Schema validation error) */
        assertEquals(0, parseResult.getWarnings().size());
        assertEquals(1, parseResult.getSupportedSchemaNamespaces().size()); /* (QTI 2.1) */
        assertEquals(0, parseResult.getUnsupportedSchemaNamespaces().size());
    }

    @Test
    public void testReadNotQTI() throws Exception {
        final String fileName = "imsmanifest.xml"; /* (It's a Content Package manifest!) */
        final XmlReadResult result = readUnitTestFile(fileName, true);
        final XmlParseResult parseResult = result.getXmlParseResult();

        assertNotNull(result.getDocument());
        assertEquals(makeSystemId(fileName), parseResult.getSystemId());
        assertTrue(parseResult.isParsed());
        assertFalse(parseResult.isValidated());
        assertFalse(parseResult.isSchemaValid());
        assertEquals(0, parseResult.getFatalErrors().size());
        assertEquals(0, parseResult.getErrors().size());
        assertEquals(0, parseResult.getWarnings().size());
        assertEquals(0, parseResult.getSupportedSchemaNamespaces().size());
        assertEquals(2, parseResult.getUnsupportedSchemaNamespaces().size()); /* (CP + MD schemas) */
    }

    @Test
    public void testReadUnsupportedSchema() throws Exception {
        final String fileName = "unsupported.xml";
        final XmlReadResult result = readUnitTestFile(fileName, true);
        final XmlParseResult parseResult = result.getXmlParseResult();

        assertNotNull(result.getDocument());
        assertEquals(makeSystemId(fileName), parseResult.getSystemId());
        assertTrue(parseResult.isParsed());
        assertFalse(parseResult.isValidated());
        assertFalse(parseResult.isSchemaValid());
        assertEquals(0, parseResult.getFatalErrors().size());
        assertEquals(0, parseResult.getErrors().size());
        assertEquals(0, parseResult.getWarnings().size());
        assertEquals(1, parseResult.getSupportedSchemaNamespaces().size()); /* (QTI 2.1) */
        assertEquals(1, parseResult.getUnsupportedSchemaNamespaces().size()); /* (Unsupported) */
    }

    //-------------------------------

    private XmlReadResult readUnitTestFile(final String testFilePath, final boolean schemaValiadating)
            throws XmlResourceNotFoundException {
        final QtiXmlReader reader = UnitTestHelper.createUnitTestQtiXmlReader();
        final URI testFileUri = makeSystemId(testFilePath);
        return reader.read(UnitTestHelper.createTestFileResourceLocator(), testFileUri, schemaValiadating);
    }

    private URI makeSystemId(final String testFileName) {
        return UnitTestHelper.createTestResourceUri("reading/" + testFileName);
    }

}