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
package uk.ac.ed.ph.jqtiplus.xmlutils;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.EntityResourceResolver;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.LoadSaveResourceResolver;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Helper class that makes it easy to parse XML into a DOM and optionally schema validate
 * it against a set of prescribed schemas. This supports:
 * <ul>
 *   <li>Specifying a set of supported schema</li>
 *   <li>Using a {@link ResourceLocator} to locate schemas</li>
 *   <li>Using a {@link ResourceLocator} to locate DTD entity files</li>
 *   <li>Using a {@link ResourceLocator} to locate your XML source files</li>
 *   <li>Optional caching of schemas</li>
 *   <li>DOM is enriched with location information via a user Object</li>
 * </ul>
 * This suits the way QTI works, but also has uses in other domains.
 *
 * <h2>Implementation notes</h2>
 *
 * The XML parsing process performs a SAX parse followed by a DOM tree build,
 * filling the resulting tree with SAX {@link Locator} information, which makes
 * later error reporting richer.
 *
 * @see XmlReadResult
 * @author David McKain
 */
public final class XmlResourceReader {

    private static final Logger logger = LoggerFactory.getLogger(XmlResourceReader.class);

    /** Name of the DOM "user object" where SAX {@link Locator} information will be stowed while parsing */
    public static final String LOCATION_INFORMATION_NAME = "locationInformation";

    //--------------------------------------------------

    /**
     * {@link ResourceLocator} used to locate schema resources.
     * <p>
     * Must not be null.
     */
    public final ResourceLocator schemaResourceLocator;

    /**
     * Map containing details of each schema registered with this reader.
     * Keys are namespace URI, value is schema URI.
     * <p>
     * This may be null or empty, to indicate that there are no registered schemas
     * (and therefore schema validation won't work!).
     * <p>
     * The default is null,
     */
    private final Map<String, String> registeredSchemaMap;

    /**
     * Optional {@link SchemaCache} that will be used to cache compiled schemas.
     * <p>
     * This may be null, which will prevent any caching from happening.
     */
    private final SchemaCache schemaCache;

    /**
     * Resolver used to look up schema resources
     */
    private final LoadSaveResourceResolver schemaResourceResolver;


    public XmlResourceReader(final ResourceLocator schemaResourceLocator) {
        this(schemaResourceLocator, null, null);
    }

    public XmlResourceReader(final ResourceLocator schemaResourceLocator, final Map<String, String> registeredSchemaMapTemplate,
            final SchemaCache schemaCache) {
        Assert.notNull(schemaResourceLocator, "schemaResourceLocator");
        this.schemaResourceLocator = schemaResourceLocator;
        this.registeredSchemaMap = registeredSchemaMapTemplate != null ? Collections.unmodifiableMap(registeredSchemaMapTemplate) : null;
        this.schemaCache = schemaCache;

        /* Set up special resource resolver based on schemaResourceLocator */
        this.schemaResourceResolver = new LoadSaveResourceResolver(schemaResourceLocator);
    }

    public ResourceLocator getParserResourceLocator() {
        return schemaResourceLocator;
    }

    public Map<String, String> getRegisteredSchemaMap() {
        return registeredSchemaMap;
    }

    public SchemaCache getSchemaCache() {
        return schemaCache;
    }

    //--------------------------------------------------

    /**
     * FIXME: This currently calls the {@link ResourceLocator} to read the input *twice*. We may
     * want to save the initial input to a temp file if the stream is not something that can be
     * quickly re-read (e.g. HTTP!)
     *
     * @param systemId system ID of the XML resource to read
     * @param inputResourceLocator resource locator that will find the XML to be read
     * @param entityResourceLocator resource locator that will load in any entities/DTD stuff
     *   encountered
     * @param schemaValidating whether to perform schema validation or not.
     *
     * @throws XmlResourceNotFoundException if the XML resource with the given System ID cannot be
     *             located using the given {@link ResourceLocator}
     * @throws XmlResourceReaderException if an unexpected Exception occurred parsing and/or validating the XML, or
     *             if any of the required schemas could not be located.
     */
    public XmlReadResult read(final URI systemId, final ResourceLocator inputResourceLocator,
            final ResourceLocator entityResourceLocator, final boolean schemaValidating)
            throws XmlResourceNotFoundException {
        Assert.notNull(systemId, "systemId");
        Assert.notNull(inputResourceLocator, "inputResourceLocator");
        Assert.notNull(entityResourceLocator, "entityResourceLocator");

        try {
            logger.debug("read({}, {}, {}, {}) starting", new Object[] { systemId, inputResourceLocator, entityResourceLocator, schemaValidating });
            final XmlReadResult result = doRead(systemId, inputResourceLocator, entityResourceLocator, schemaValidating);
            logger.debug("read({}, {}, {}, {}) => {}", new Object[] { systemId, inputResourceLocator, entityResourceLocator, schemaValidating, result });
            return result;
        }
        catch (final XmlResourceNotFoundException e) {
            logger.debug("read({}, {}, {}, {}) => {}", new Object[] { systemId, inputResourceLocator, entityResourceLocator, schemaValidating, e });
            throw e;
        }
        catch (final Exception e) {
            logger.debug("read({}, {}, {}, {}) => UNEXPECTED EXCEPTION {}", new Object[] { systemId, inputResourceLocator, entityResourceLocator, schemaValidating, e });
            if (e instanceof XmlResourceReaderException) {
                throw (XmlResourceReaderException) e;
            }
            throw new XmlResourceReaderException("Unexpected Exception parsing or validating XML at system ID " + systemId, e);
        }
    }

    private XmlReadResult doRead(final URI systemId, final ResourceLocator inputResourceLocator,
            final ResourceLocator entityResourceLocator, final boolean schemaValidating)
            throws XmlResourceNotFoundException, ParserConfigurationException, SAXException, IOException {
        final String systemIdString = systemId.toString();
        boolean parsed = false;
        boolean validated = false;
        final List<String> supportedSchemaNamespaces = new ArrayList<String>();
        final List<String> unsupportedSchemaNamespaces = new ArrayList<String>();

        final InputErrorHandler inputErrorHandler = new InputErrorHandler();

        /* Create the DOM Document that will be built up here */
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        final DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
        documentBuilder.setErrorHandler(inputErrorHandler);
        final Document document = documentBuilder.newDocument();

        /* Set up SAX EntityResolver, which will record locator failures appropriately */
        final FailureEntityResolver failureEntityResolver = new FailureEntityResolver(entityResourceLocator);

        /* Create and configure SAX parser */
        final SAXParserFactory spFactory = SAXParserFactory.newInstance();
        spFactory.setNamespaceAware(true);
        spFactory.setValidating(false);
        spFactory.setXIncludeAware(true);
        spFactory.setFeature("http://xml.org/sax/features/validation", false);
        spFactory.setFeature("http://xml.org/sax/features/external-general-entities", true);
        spFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
        spFactory.setFeature("http://xml.org/sax/features/lexical-handler/parameter-entities", false);
        final XMLReader xmlReader = spFactory.newSAXParser().getXMLReader();
        xmlReader.setErrorHandler(inputErrorHandler);
        xmlReader.setEntityResolver(failureEntityResolver);

        /* Parse input and convert to a DOM containing SAX Locator information */
        logger.trace("XML parse of {} starting", systemIdString);
        final InputSource inputSource = new InputSource();
        inputSource.setByteStream(ensureLocateInput(systemId, inputResourceLocator));
        inputSource.setSystemId(systemIdString);

        final SimpleDomBuilderHandler handler = new SimpleDomBuilderHandler(document);
        xmlReader.setContentHandler(handler);
        try {
            xmlReader.parse(inputSource); /* Fatal errors will cause SAXParseException */
        }
        catch (final SAXParseException e) {
            /* Fatal parsing error */
        }

        /* We'll consider successful parsing to be no errors or fatal errors, and no unresolved
         * entities */
        final List<String> unresolvedEntitySystemIds = failureEntityResolver.getUnresolvedEntitySystemIds();
        parsed = inputErrorHandler.fatalErrors.isEmpty() && inputErrorHandler.errors.isEmpty()
                && unresolvedEntitySystemIds.isEmpty();
        logger.debug("XML parse of {} success? {}", systemIdString, parsed);

        if (parsed && schemaValidating) {
            /* Work out which schema(s) to use */
            logger.trace("Deciding which schemas to use to validate {}", systemIdString);
            final Element rootElement = document.getDocumentElement();
            final String schemaLocation = rootElement.getAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation");
            final List<String> schemaUris = new ArrayList<String>();
            if (schemaLocation.length() != 0) {
                /* Document declares schema(s) to use. Make sure we support each one */
                final String[] schemaData = schemaLocation.trim().split("\\s+");
                for (int i = 0; i < schemaData.length; i += 2) { /* (ns1 uri1 ns2 uri2 ...) */
                    final String schemaNamespaceUri = schemaData[i];
                    final String schemaUri = getRegisteredSchemaLocation(schemaNamespaceUri);
                    if (schemaUri != null) {
                        supportedSchemaNamespaces.add(schemaNamespaceUri);
                        schemaUris.add(schemaUri);
                    }
                    else {
                        logger.trace("Schema with namespace " + schemaNamespaceUri + " declared in schemaLocation is not registered with this reader");
                        unsupportedSchemaNamespaces.add(schemaNamespaceUri);
                    }
                }
            }
            else {
                /* No schema declared in the document, so use namespace of root element */
                final String schemaNamespaceUri = rootElement.getNamespaceURI();
                final String schemaUri = getRegisteredSchemaLocation(rootElement.getNamespaceURI());
                if (schemaUri != null) {
                    supportedSchemaNamespaces.add(schemaNamespaceUri);
                    schemaUris.add(schemaUri);
                }
                else {
                    logger.trace("Schema with namespace " + schemaNamespaceUri + " inferred from that of document element is not registered with this reader");
                    unsupportedSchemaNamespaces.add(schemaNamespaceUri);
                }
            }

            /* Validate (if at least supported schemas was used and no unsupported schemas) */
            if (!schemaUris.isEmpty() && unsupportedSchemaNamespaces.isEmpty()) {
                logger.trace("Will validate {} against schemas {}", systemIdString, schemaUris);
                final Schema schema = getSchema(schemaUris);

                /* Now validate. Note that we read in the input again, as this will let the parser provide source
                 * information to the schema validator. (I couldn't work out a way of passing source information
                 * when validating from a DOM.)
                 */
                logger.trace("Schema validaton of {} starting", systemIdString);
                final StreamSource input = new StreamSource(ensureLocateInput(systemId, inputResourceLocator), systemIdString);
                final Validator validator = schema.newValidator();
                validator.setResourceResolver(schemaResourceResolver);
                validator.setErrorHandler(inputErrorHandler);
                validator.validate(input);
                validated = true;
                logger.debug("Schema validation of {} finished", systemIdString);
            }
            else {
                logger.debug("No schema validation was performed as {} supported and {} unsupported schemas were detected",
                        schemaUris.size(), unsupportedSchemaNamespaces.size());
            }
        }

        /* Build up result */
        final XmlParseResult xmlParseResult = new XmlParseResult(systemId, parsed, validated,
                inputErrorHandler.warnings, inputErrorHandler.errors, inputErrorHandler.fatalErrors,
                unresolvedEntitySystemIds, supportedSchemaNamespaces, unsupportedSchemaNamespaces);
        return new XmlReadResult(parsed ? document : null, xmlParseResult);
    }

    /**
     * Obtains the schema compiled from the given list of URIs, using a cached version if
     * possible.
     */
    private Schema getSchema(final List<String> schemaUris) {
        Schema result = null;
        final String key = schemaUris.toString();
        if (schemaCache!=null) {
            synchronized (schemaCache) {
                result = schemaCache.getSchema(key);
                if (result!=null) {
                    logger.debug("Schema cache hit for URIs {} yielded {}", key, result);
                }
                else {
                    result = compileSchema(schemaUris);
                    schemaCache.putSchema(key, result);
                    logger.debug("Schema cache miss for URIs {} stored {}", key, result);
                }
            }
        }
        else {
            logger.debug("No schema caching configured, so compiling new schema for {}", key);
            result = compileSchema(schemaUris);
        }
        return result;
    }

    /**
     * Compiles a schema from the given list of URIs.
     */
    private Schema compileSchema(final List<String> schemaUris) {
        logger.trace("Compiling schema(s) with URI(s) {}", schemaUris);
        final Source[] schemaSources = new Source[schemaUris.size()];
        for (int i = 0; i < schemaSources.length; i++) {
            final String schemaUri = schemaUris.get(i);
            final InputStream schemaStream = schemaResourceLocator.findResource(URI.create(schemaUri));
            if (schemaStream==null) {
                final String message = "schemaResourceLocator failed to locate schema with URI " + schemaUri;
                logger.debug(message);
                throw new XmlResourceReaderException(message);
            }
            schemaSources[i] = new StreamSource(schemaStream, schemaUri);
        }
        final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        sf.setResourceResolver(schemaResourceResolver);
        sf.setErrorHandler(new SchemaParsingErrorHandler(schemaUris));
        try {
            return sf.newSchema(schemaSources);
        }
        catch (final SAXException e) {
            throw new XmlResourceReaderException("Unexpected Exception compiling schema(s) with URI(s)" + schemaUris, e);
        }
    }

    /**
     * Trivial extension of {@link EntityResourceResolver} that handles failed
     * resolutions by recording the offending systemId then simply returning an
     * empty document.
     */
    static class FailureEntityResolver extends EntityResourceResolver {

        private final List<String> unresolvedEntitySystemIds;

        public FailureEntityResolver(final ResourceLocator resourceLocator) {
            super(resourceLocator);
            this.unresolvedEntitySystemIds = new ArrayList<String>();
        }

        public List<String> getUnresolvedEntitySystemIds() {
            return unresolvedEntitySystemIds;
        }

        @Override
        public InputSource onMiss(final String publicId, final String systemId) {
            unresolvedEntitySystemIds.add(systemId);
            final InputSource emptyStringSource = new InputSource(new StringReader(""));
            emptyStringSource.setSystemId(systemId);
            return emptyStringSource;
        }
    }

    /**
     * {@link ErrorHandler} used when parsing user input, which simply records
     * everything.
     */
    public static final class InputErrorHandler implements ErrorHandler {

        final List<SAXParseException> warnings;
        final List<SAXParseException> errors;
        final List<SAXParseException> fatalErrors;

        public InputErrorHandler() {
            this.warnings = new ArrayList<SAXParseException>();
            this.errors = new ArrayList<SAXParseException>();
            this.fatalErrors = new ArrayList<SAXParseException>();
        }

        @Override
        public void warning(final SAXParseException exception) {
            warnings.add(exception);
        }

        @Override
        public void error(final SAXParseException exception) {
            errors.add(exception);
        }

        @Override
        public void fatalError(final SAXParseException exception) throws SAXParseException {
            fatalErrors.add(exception);
            throw exception;
        }
    }

    /**
     * {@link ErrorHandler} used when parsing internal schemas, which is basically as strict
     * and quick to fail as possible as our internal schemas should be well behaved!
     */
    private static class SchemaParsingErrorHandler implements ErrorHandler {

        private final List<String> schemaUris;

        public SchemaParsingErrorHandler(final List<String> schemaUris) {
            this.schemaUris = schemaUris;
        }

        @Override
        public void error(final SAXParseException e) {
            fail("error", e);
        }

        @Override
        public void fatalError(final SAXParseException e) {
            fail("fatalError", e);
        }

        @Override
        public void warning(final SAXParseException e) {
            fail("warning", e);
        }

        private void fail(final String errorLevel, final SAXParseException e) {
            throw new XmlResourceReaderException("Unexpected SAXParseException at level "
                    + errorLevel
                    + " when parsing schema(s) with URI(s) "
                    + schemaUris, e);
        }

    }

    private static InputStream ensureLocateInput(final URI systemId, final ResourceLocator inputResourceLocator)
            throws XmlResourceNotFoundException {
        logger.trace("Attempting to locate XML resource at {} using locator {}", systemId, inputResourceLocator);
        final InputStream inputStream = inputResourceLocator.findResource(systemId);
        if (inputStream == null) {
            logger.debug("Could not locate and open XML at system ID {} using locator {}", systemId, inputResourceLocator);
            throw new XmlResourceNotFoundException(inputResourceLocator, systemId);
        }
        return inputStream;
    }

    public static XmlSourceLocationInformation extractLocationInformation(final Node elementOrTextNode) {
        XmlSourceLocationInformation result = null;
        final Object locationData = elementOrTextNode.getUserData(LOCATION_INFORMATION_NAME);
        if (locationData != null && locationData instanceof XmlSourceLocationInformation) {
            result = (XmlSourceLocationInformation) locationData;
        }
        return result;
    }

    private String getRegisteredSchemaLocation(final String namespaceUri) {
        return registeredSchemaMap != null ? registeredSchemaMap.get(namespaceUri) : null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(schemaResourceLocator=" + schemaResourceLocator
                + ",registeredSchemaMap=" + registeredSchemaMap
                + ",schemaCache=" + schemaCache
                + ")";
    }
}
