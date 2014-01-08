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
package uk.ac.ed.ph.jqtiplus.xmlutils.xslt;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ChainedResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.NullResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.XsltResourceResolver;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

/**
 * Convenient facade for managing the caching and compilation
 * of XSLT stylesheets. Mainly intended for internal use, but
 * is handy enough to be useful in other places. (E.g. the MathAssess
 * extensions use it for managing its XSLT stylesheets.)
 *
 * @see XsltStylesheetCache
 * @see XsltResourceResolver
 * @see ResourceLocator
 *
 * This class is thread-safe.
 *
 * @author David McKain
 */
public final class XsltStylesheetManager {

    private final XsltStylesheetCache xsltStylesheetCache;
    private final ResourceLocator xsltResourceLocator;
    private final XsltResourceResolver xsltResourceResolver;

    /**
     * Creates a new {@link XsltStylesheetManager} using the given {@link ResourceLocator}
     * to load XSLT resources and with no {@link XsltStylesheetCache}
     *
     * @param xsltResourceLocator {@link ResourceLocator} to be used for reading XSLT resources,
     *   which must not be null.
     */
    public XsltStylesheetManager(final ResourceLocator xsltResourceLocator) {
        this(xsltResourceLocator, null);
    }

    /**
     * Creates a new {@link XsltStylesheetManager} with the specified {@link XsltStylesheetCache}
     * and custom {@link ResourceLocator} for locating your own XSLT resources.
     *
     * @param xsltResourceLocator {@link ResourceLocator} to be used for reading XSLT resources,
     *   which must not be null.
     * @param xsltStylesheetCache optional {@link XsltStylesheetCache} for caching compiled stylesheets
     */
    public XsltStylesheetManager(final ResourceLocator xsltResourceLocator, final XsltStylesheetCache xsltStylesheetCache) {
        Assert.notNull(xsltResourceLocator, "xsltResourceLocator");
        this.xsltResourceLocator = xsltResourceLocator;
        this.xsltStylesheetCache = xsltStylesheetCache;
        this.xsltResourceResolver = new XsltResourceResolver(xsltResourceLocator);
    }

    /**
     * Convenient static factory method for creating a simple serialization {@link Transformer}
     * configured using the given {@link XsltSerializationOptions} and no runtime URI resolution.
     */
    public static Transformer createSerializer(final XsltSerializationOptions xsltSerializationOptions) {
        return new XsltStylesheetManager(NullResourceLocator.getInstance()).getSerializer(null, xsltSerializationOptions);
    }

    /**
     * Convenient static factory method for creating a simple serialization {@link TransformerHandler}
     * configured using the given {@link XsltSerializationOptions}
     */
    public static TransformerHandler createSerializerHandler(final XsltSerializationOptions xsltSerializationOptions) {
        return new XsltStylesheetManager(NullResourceLocator.getInstance()).getSerializerHandler(null, xsltSerializationOptions);
    }

    //----------------------------------------------------------

    /**
     * Returns the XSLT {@link ResourceLocator} for this manager, which will not be null
     */
    public ResourceLocator getXsltResourceLocator() {
        return xsltResourceLocator;
    }

    /**
     * Returns the {@link XsltStylesheetCache} for this manager, which may be null.
     */
    public XsltStylesheetCache getStylesheetCache() {
        return xsltStylesheetCache;
    }

    //----------------------------------------------------------

    /**
     * Configures the given {@link Transformer} so that it can use the provided {@link ResourceLocator}
     * to locate XML resources when the stylesheet is being run.
     * <p>
     * (This will actually set up a {@link ChainedResourceLocator} that falls back to the
     * configured {@link #xsltResourceLocator}.)
     */
    public void configureRuntimeUriResolution(final Transformer transformer, final ResourceLocator runtimeResourceLocator) {
        Assert.notNull(transformer, "transformer");
        if (runtimeResourceLocator!=null) {
            final ChainedResourceLocator rendererResourceLocator = new ChainedResourceLocator(runtimeResourceLocator, xsltResourceLocator);
            transformer.setURIResolver(new XsltResourceResolver(rendererResourceLocator));
        }
        else {
            transformer.setURIResolver(xsltResourceResolver); /* (This should already have been set, but no harm done) */
        }
    }

    /**
     * Obtains the compiled XSLT stylesheet {@link Templates}s Object at the given URI,
     * using the {@link XsltStylesheetCache} (if set) to cache stylesheets for efficiency.
     *
     * @param xsltUri location of the XSLT stylesheet, located using the
     *   {@link #getXsltResourceLocator()}
     *
     * @return compiled XSLT stylesheet.
     */
    public Templates getCompiledStylesheet(final URI xsltUri) {
        Assert.notNull(xsltUri, "xsltUri");
        Templates result;
        if (xsltStylesheetCache==null) {
            result = compileStylesheet(xsltUri);
        }
        else {
            synchronized(xsltStylesheetCache) {
                result = xsltStylesheetCache.getStylesheet(xsltUri.toString());
                if (result==null) {
                    result = compileStylesheet(xsltUri);
                    xsltStylesheetCache.putStylesheet(xsltUri.toString(), result);
                }
            }
        }
        return result;
    }

    public TransformerHandler getCompiledStylesheetHandler(final URI xsltUri, final ResourceLocator runtimeResourceLocator) {
        Assert.notNull(xsltUri, "xsltUri");
        TransformerHandler transformerHandler;
        try {
            transformerHandler = getSaxTransformerFactory().newTransformerHandler(getCompiledStylesheet(xsltUri));
        }
        catch (final TransformerConfigurationException e) {
            throw new QtiSerializationException("Unexpected failure instantiating TransformerHandler " + xsltUri, e);
        }
        if (runtimeResourceLocator!=null) {
            configureRuntimeUriResolution(transformerHandler.getTransformer(), runtimeResourceLocator);
        }
        return transformerHandler;
    }

    private Templates compileStylesheet(final URI xsltUri) {
        final TransformerFactory transformerFactory = getTransformerFactory();
        Source resolved;
        try {
            final InputStream resolvedStream = xsltResourceLocator.findResource(xsltUri);
            if (resolvedStream==null) {
                throw new QtiSerializationException("Could not locate XSLT resource at system ID " + xsltUri);
            }
            resolved = new StreamSource(resolvedStream, xsltUri.toString());
            return transformerFactory.newTemplates(resolved);
        }
        catch (final TransformerConfigurationException e) {
            throw new QtiSerializationException("Could not compile stylesheet at " + xsltUri, e);
        }
    }

    public Templates getCompiledStylesheetDriver(final List<URI> xsltUris) {
        Templates result;
        if (xsltStylesheetCache==null) {
            result = compileStylesheetDriver(xsltUris);
        }
        else {
            final String cacheKey = "xslt-driver(" + StringUtilities.join(xsltUris, ",") + ")";
            synchronized(xsltStylesheetCache) {
                result = xsltStylesheetCache.getStylesheet(cacheKey);
                if (result==null) {
                    result = compileStylesheetDriver(xsltUris);
                    xsltStylesheetCache.putStylesheet(cacheKey, result);
                }
            }
        }
        return result;
    }

    private Templates compileStylesheetDriver(final List<URI> xsltUris) {
        /* Build up driver XSLT that simply imports the required stylesheets */
        final TransformerFactory transformerFactory = getTransformerFactory();
        final StringBuilder xsltBuilder = new StringBuilder("<stylesheet version='1.0' xmlns='http://www.w3.org/1999/XSL/Transform'>\n");
        for (final URI importUri : xsltUris) {
            xsltBuilder.append("<import href='").append(importUri.toString()).append("'/>\n");
        }
        xsltBuilder.append("</stylesheet>");
        final String xslt = xsltBuilder.toString();

        /* Now compile and return result */
        try {
            return transformerFactory.newTemplates(new StreamSource(new StringReader(xslt)));
        }
        catch (final TransformerConfigurationException e) {
            throw new QtiSerializationException("Could not compile stylesheet driver " + xslt, e);
        }
    }

    //----------------------------------------------------------

    public Transformer getSerializer(final ResourceLocator runtimeResourceLocator, final XsltSerializationOptions xsltSerializationOptions) {
        /* Create serializer */
        Transformer serializer;
        try {
            serializer = getTransformerFactory().newTransformer();
        }
        catch (final TransformerConfigurationException e) {
            throw new QtiSerializationException("Unexpected failure instantiating default serializer", e);
        }

        /* Then configure it as per options */
        configureRuntimeUriResolution(serializer, runtimeResourceLocator);
        return configureSerializer(serializer, runtimeResourceLocator, xsltSerializationOptions);
    }

    public TransformerHandler getSerializerHandler(final ResourceLocator runtimeResourceLocator, final XsltSerializationOptions xsltSerializationOptions) {
        /* Create serializer */
        TransformerHandler serializerHandler;
        try {
            serializerHandler = getSaxTransformerFactory().newTransformerHandler();
        }
        catch (final TransformerConfigurationException e) {
            throw new QtiSerializationException("Unexpected failure instantiating default serializer", e);
        }

        /* Then configure it as per options */
        return configureSerializerHandler(serializerHandler, runtimeResourceLocator, xsltSerializationOptions);
    }

    public Transformer getSerializer(final URI serializerUri, final ResourceLocator runtimeResourceLocator, final XsltSerializationOptions xsltSerializationOptions) {
        Assert.notNull(serializerUri, "serializerUri");
        /* Create serializer */
        Transformer serializer;
        try {
            serializer = getCompiledStylesheet(serializerUri).newTransformer();
        }
        catch (final TransformerConfigurationException e) {
            throw new QtiSerializationException("Unexpected failure instantiating serializer " + serializerUri, e);
        }

        /* Then configure it as per options */
        return configureSerializer(serializer, runtimeResourceLocator, xsltSerializationOptions);
    }

    public TransformerHandler getSerializerHandler(final URI serializerUri, final ResourceLocator runtimeResourceLocator, final XsltSerializationOptions xsltSerializationOptions) {
        Assert.notNull(serializerUri, "serializerUri");
        TransformerHandler serializerHandler;
        try {
            serializerHandler = getSaxTransformerFactory().newTransformerHandler(getCompiledStylesheet(serializerUri));
        }
        catch (final TransformerConfigurationException e) {
            throw new QtiSerializationException("Unexpected failure instantiating serializing TransformerHandler " + serializerUri, e);
        }

        /* Then configure it as per options */
        return configureSerializerHandler(serializerHandler, runtimeResourceLocator, xsltSerializationOptions);
    }

    public Transformer getSerializerDriver(final List<URI> serializerUris, final ResourceLocator runtimeResourceLocator, final XsltSerializationOptions xsltSerializationOptions) {
        Assert.notNull(serializerUris, "serializerUris");
        Transformer serializer;
        try {
            serializer = getCompiledStylesheetDriver(serializerUris).newTransformer();
        }
        catch (final TransformerConfigurationException e) {
            throw new QtiSerializationException("Unexpected failure instantiating serializer driver from " + serializerUris, e);
        }

        /* Then configure it as per options */
        return configureSerializer(serializer, runtimeResourceLocator, xsltSerializationOptions);
    }

    public TransformerHandler getSerializerDriverHandler(final List<URI> serializerUris, final ResourceLocator runtimeResourceLocator, final XsltSerializationOptions xsltSerializationOptions) {
        /* Create serializer */
        Assert.notNull(serializerUris, "serializerUris");
        TransformerHandler serializerHandler;
        try {
            serializerHandler = getSaxTransformerFactory().newTransformerHandler(getCompiledStylesheetDriver(serializerUris));
        }
        catch (final TransformerConfigurationException e) {
            throw new QtiSerializationException("Unexpected failure instantiating serializer driver from " + serializerUris, e);
        }

        /* Then configure it as per options */
        return configureSerializerHandler(serializerHandler, runtimeResourceLocator, xsltSerializationOptions);
    }

    private TransformerHandler configureSerializerHandler(final TransformerHandler serializerHandler, final ResourceLocator runtimeResourceLocator, final XsltSerializationOptions xsltSerializationOptions) {
        configureSerializer(serializerHandler.getTransformer(), runtimeResourceLocator, xsltSerializationOptions);
        return serializerHandler;
    }

    private Transformer configureSerializer(final Transformer serializer, final ResourceLocator runtimeResourceLocator, final XsltSerializationOptions xsltSerializationOptions) {
        configureRuntimeUriResolution(serializer, runtimeResourceLocator);
        if (xsltSerializationOptions!=null) {
            final boolean supportsXSLT20 = XsltFactoryUtilities.supportsXSLT20(serializer);
            XsltSerializationMethod serializationMethod = xsltSerializationOptions.getSerializationMethod();
            if (serializationMethod==XsltSerializationMethod.XHTML && !supportsXSLT20) {
                /* Really want XHTML serialization, but we don't have an XSLT 2.0 processor
                 * so downgrading to XML.
                 */
                serializationMethod = XsltSerializationMethod.XML;
            }
            serializer.setOutputProperty(OutputKeys.METHOD, serializationMethod.getName());
            serializer.setOutputProperty(OutputKeys.INDENT, StringUtilities.toYesNo(xsltSerializationOptions.isIndenting()));
            if (xsltSerializationOptions.isIndenting()) {
                XsltFactoryUtilities.setIndentation(serializer, xsltSerializationOptions.getIndent());
            }
            serializer.setOutputProperty(OutputKeys.ENCODING, xsltSerializationOptions.getEncoding());
            serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, StringUtilities.toYesNo(!xsltSerializationOptions.isIncludingXMLDeclaration()));
            if (xsltSerializationOptions.getDoctypePublic()!=null) {
                serializer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, xsltSerializationOptions.getDoctypePublic());
            }
            if (xsltSerializationOptions.getDoctypeSystem()!=null) {
                serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, xsltSerializationOptions.getDoctypeSystem());
            }
        }
        return serializer;
    }

    //----------------------------------------------------------

    private TransformerFactory getTransformerFactory() {
        /* Choose appropriate TransformerFactory implementation */
        final TransformerFactory transformerFactory = XsltFactoryUtilities.createJAXPTransformerFactory();

        /* Configure URIResolver */
        transformerFactory.setURIResolver(xsltResourceResolver);
        return transformerFactory;
    }

    private SAXTransformerFactory getSaxTransformerFactory() {
        /* Choose appropriate TransformerFactory implementation */
        final TransformerFactory transformerFactory = XsltFactoryUtilities.createJAXPTransformerFactory();
        XsltFactoryUtilities.requireFeature(transformerFactory, SAXTransformerFactory.FEATURE);

        /* Configure URIResolver */
        transformerFactory.setURIResolver(xsltResourceResolver);
        return (SAXTransformerFactory) transformerFactory;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(xsltResourceLocator=" + xsltResourceLocator
                + ",xsltStylesheetCache=" + xsltStylesheetCache
                + ")";
    }
}
