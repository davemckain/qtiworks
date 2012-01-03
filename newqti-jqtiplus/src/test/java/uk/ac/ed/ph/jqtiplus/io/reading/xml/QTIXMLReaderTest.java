/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.io.reading.xml;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import uk.ac.ed.ph.jqtiplus.xmlutils.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.ResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLParseResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLReaderException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLResourceNotFoundException;

import java.io.InputStream;
import java.net.URI;

import org.junit.Test;

/**
 * @author  David McKain
 * @version $Revision$
 */
@SuppressWarnings("static-method")
public class QTIXMLReaderTest {
    
    @Test(expected=XMLResourceNotFoundException.class)
    public void testReadNotFound() throws Exception {
        readTestFile("notfound.xml", false);
    }
    
    @Test
    public void testGoodReadNoValidate() throws Exception {
        String fileName = "choice.xml";
        XMLReadResult result = readTestFile(fileName, false);
        XMLParseResult parseResult = result.getXMLParseResult();
        
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
        String fileName = "choice.xml";
        XMLReadResult result = readTestFile(fileName, true);
        XMLParseResult parseResult = result.getXMLParseResult();
        
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
        String fileName = "illformed.xml";
        XMLReadResult result = readTestFile(fileName, false);
        XMLParseResult parseResult = result.getXMLParseResult();
        
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
        String fileName = "invalid.xml";
        XMLReadResult result = readTestFile(fileName, true);
        XMLParseResult parseResult = result.getXMLParseResult();
        
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
        String fileName = "imsmanifest.xml"; /* (It's a Content Package manifest!) */
        XMLReadResult result = readTestFile(fileName, true);
        XMLParseResult parseResult = result.getXMLParseResult();
        
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
        String fileName = "unsupported.xml";
        XMLReadResult result = readTestFile(fileName, true);
        XMLParseResult parseResult = result.getXMLParseResult();
        
        assertNotNull(result.getDocument());
        assertEquals(makeSystemId(fileName), parseResult.getSystemId());
        assertTrue(parseResult.isParsed());
        assertTrue(parseResult.isValidated());
        assertFalse(parseResult.isSchemaValid());
        assertEquals(0, parseResult.getFatalErrors().size());
        assertEquals(0, parseResult.getErrors().size());
        assertEquals(0, parseResult.getWarnings().size());
        assertEquals(1, parseResult.getSupportedSchemaNamespaces().size()); /* (QTI 2.1) */
        assertEquals(1, parseResult.getUnsupportedSchemaNamespaces().size()); /* (Unsupported) */
    }
    
    @Test(expected=XMLReaderException.class)
    public void testBadSchemaClassPath() throws Exception {
        QTIXMLReader reader = new QTIXMLReader(new NoResourceLocator());
        ResourceLocator inputResourceLocator = new ClassPathResourceLocator();
        reader.read(makeSystemId("choice.xml"), inputResourceLocator, true);
    }
    
    //-------------------------------
    
    static class NoResourceLocator implements ResourceLocator {
        private static final long serialVersionUID = -3305449197115182185L;

        @Override
        public InputStream findResource(URI systemIdUri) {
            return null;
        }
    }
    
    private XMLReadResult readTestFile(String testFileName, boolean schemaValiadating) throws XMLResourceNotFoundException {
        QTIXMLReader reader = new QTIXMLReader();
        ResourceLocator inputResourceLocator = new ClassPathResourceLocator();
        return reader.read(makeSystemId(testFileName), inputResourceLocator, schemaValiadating);
    }
    
    private URI makeSystemId(String testFileName) {
        return URI.create("classpath:/uk/ac/ed/ph/jqtiplus/io/reading/xml/" + testFileName);
    }

}