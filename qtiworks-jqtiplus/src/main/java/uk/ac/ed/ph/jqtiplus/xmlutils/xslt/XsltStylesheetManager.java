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
package uk.ac.ed.ph.jqtiplus.xmlutils.xslt;

import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ChainedResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
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
 * FIXME: Document this type!
 * 
 * This class is thread-safe.
 *
 * @author David McKain
 */
public final class XsltStylesheetManager {
    
    private final XsltStylesheetCache stylesheetCache;
    private final ResourceLocator customXsltResourceLocator;
    private final ResourceLocator xsltResourceLocator;
    private final XsltResourceResolver xsltResourceResolver;
    
    /**
     * Creates a new {@link XsltStylesheetManager} with no {@link XsltStylesheetCache} and no custom
     * {@link ResourceLocator} for locating your own XSLT resources.
     */
    public XsltStylesheetManager() {
        this(null, null);
    }
    
    /**
     * Creates a new {@link XsltStylesheetManager} with the specified @link StylesheetCache}
     * and custom {@link ResourceLocator} for locating your own XSLT resources.
     */
    public XsltStylesheetManager(ResourceLocator customXsltResourceLocator, XsltStylesheetCache cache) {
        this.customXsltResourceLocator = customXsltResourceLocator;
        this.stylesheetCache = cache;
        
        ResourceLocator internalXsltResourceLocator = new ClassPathResourceLocator();
        ResourceLocator resultingXsltResourceLocator;
        if (customXsltResourceLocator!=null) {
            resultingXsltResourceLocator = new ChainedResourceLocator(customXsltResourceLocator, internalXsltResourceLocator);
        }
        else {
            resultingXsltResourceLocator = customXsltResourceLocator;
        }
        this.xsltResourceLocator = resultingXsltResourceLocator;
        this.xsltResourceResolver = new XsltResourceResolver(resultingXsltResourceLocator);
    }
    
    //----------------------------------------------------------

    /**
     * Returns the {@link XsltStylesheetCache} for this manager, which may be null.
     */
    public XsltStylesheetCache getStylesheetCache() {
        return stylesheetCache;
    }
    
    /**
     * Returns the custom XSLT {@link ResourceLocator} for this manager, which may be null.
     */
    public ResourceLocator getCustomXsltResourceLocator() {
        return customXsltResourceLocator;
    }

    //----------------------------------------------------------
    
    /**
     * Obtains the XSLT stylesheet at the given ClassPathURI, using the {@link XsltStylesheetCache}
     * (if set) to cache stylesheets for efficiency.
     * 
     * @param xsltUri location of the XSLT stylesheet in the ClassPath, following the
     *   URI scheme in {@link ClassPathURIResolver}.
     *   
     * @return compiled XSLT stylesheet.
     */
    public Templates getCompiledStylesheet(final URI xsltUri) {
        ConstraintUtilities.ensureNotNull(xsltUri, "xsltUri");
        Templates result;
        if (stylesheetCache==null) {
            result = compileStylesheet(xsltUri);
        }
        else {
            synchronized(stylesheetCache) {
                result = stylesheetCache.getStylesheet(xsltUri.toString());
                if (result==null) {
                    result = compileStylesheet(xsltUri);
                    stylesheetCache.putStylesheet(xsltUri.toString(), result);
                }
            }
        }
        return result;
    }
    
    public TransformerHandler getCompiledStylesheetHandler(final URI xsltUri) {
        ConstraintUtilities.ensureNotNull(xsltUri, "xsltUri");
        TransformerHandler transformerHandler;
        try {
            transformerHandler = getSaxTransformerFactory().newTransformerHandler(getCompiledStylesheet(xsltUri));
        }
        catch (TransformerConfigurationException e) {
            throw new QtiSerializationException("Unexpected failure instantiating TransformerHandler " + xsltUri, e);
        }
        return transformerHandler;
    }
    
    private Templates compileStylesheet(final URI xsltUri) {
        TransformerFactory transformerFactory = getTransformerFactory();
        Source resolved;
        try {
            InputStream resolvedStream = xsltResourceLocator.findResource(xsltUri);
            if (resolvedStream==null) {
                throw new QtiSerializationException("Could not locate XSLT resource at system ID " + xsltUri);
            }
            resolved = new StreamSource(resolvedStream, xsltUri.toString());
            return transformerFactory.newTemplates(resolved);
        }
        catch (TransformerConfigurationException e) {
            throw new QtiSerializationException("Could not compile stylesheet at " + xsltUri, e);
        }
    }
    
    public Templates getCompiledStylesheetDriver(final List<URI> xsltUris) {
        Templates result;
        if (stylesheetCache==null) {
            result = compileStylesheetDriver(xsltUris);
        }
        else {
            String cacheKey = "xslt-driver(" + StringUtilities.join(xsltUris, ",") + ")";
            synchronized(stylesheetCache) {
                result = stylesheetCache.getStylesheet(cacheKey);
                if (result==null) {
                    result = compileStylesheetDriver(xsltUris);
                    stylesheetCache.putStylesheet(cacheKey, result);
                }
            }
        }
        return result;
    }
    
    private Templates compileStylesheetDriver(final List<URI> xsltUris) {
        /* Build up driver XSLT that simply imports the required stylesheets */
        TransformerFactory transformerFactory = getTransformerFactory();
        StringBuilder xsltBuilder = new StringBuilder("<stylesheet version='1.0' xmlns='http://www.w3.org/1999/XSL/Transform'>\n");
        for (URI importUri : xsltUris) {
            xsltBuilder.append("<import href='").append(importUri.toString()).append("'/>\n");
        }
        xsltBuilder.append("</stylesheet>");
        String xslt = xsltBuilder.toString();
        
        /* Now compile and return result */
        try {
            return transformerFactory.newTemplates(new StreamSource(new StringReader(xslt)));
        }
        catch (TransformerConfigurationException e) {
            throw new QtiSerializationException("Could not compile stylesheet driver " + xslt, e);
        }
    }

    //----------------------------------------------------------
    
    public Transformer getSerializer(final XsltSerializationOptions serializationOptions) {
        /* Create serializer */
        Transformer serializer;
        try {
            serializer = getTransformerFactory().newTransformer();
        }
        catch (TransformerConfigurationException e) {
            throw new QtiSerializationException("Unexpected failure instantiating default serializer", e);
        }

        /* Then configure it as per options */
        return configureSerializer(serializer, serializationOptions);
    }
    
    public TransformerHandler getSerializerHandler(final XsltSerializationOptions serializationOptions) {
        /* Create serializer */
        TransformerHandler serializerHandler;
        try {
            serializerHandler = getSaxTransformerFactory().newTransformerHandler();
        }
        catch (TransformerConfigurationException e) {
            throw new QtiSerializationException("Unexpected failure instantiating default serializer", e);
        }

        /* Then configure it as per options */
        return configureSerializerHandler(serializerHandler, serializationOptions);
    }
    
    public Transformer getSerializer(final URI serializerUri, final XsltSerializationOptions serializationOptions) {
        ConstraintUtilities.ensureNotNull(serializerUri, "serializerUri");
        /* Create serializer */
        Transformer serializer;
        try {
            serializer = getCompiledStylesheet(serializerUri).newTransformer();
        }
        catch (TransformerConfigurationException e) {
            throw new QtiSerializationException("Unexpected failure instantiating serializer " + serializerUri, e);
        }

        /* Then configure it as per options */
        return configureSerializer(serializer, serializationOptions);
    }

    
    public TransformerHandler getSerializerHandler(final URI serializerUri, final XsltSerializationOptions serializationOptions) {
        ConstraintUtilities.ensureNotNull(serializerUri, "serializerUri");
        TransformerHandler serializerHandler;
        try {
            serializerHandler = getSaxTransformerFactory().newTransformerHandler(getCompiledStylesheet(serializerUri));
        }
        catch (TransformerConfigurationException e) {
            throw new QtiSerializationException("Unexpected failure instantiating serializing TransformerHandler " + serializerUri, e);
        }

        /* Then configure it as per options */
        return configureSerializerHandler(serializerHandler, serializationOptions);
    }
    
    public Transformer getSerializerDriver(final List<URI> serializerUris, final XsltSerializationOptions serializationOptions) {
        ConstraintUtilities.ensureNotNull(serializerUris, "serializerUris");
        Transformer serializer;
        try {
            serializer = getCompiledStylesheetDriver(serializerUris).newTransformer();
        }
        catch (TransformerConfigurationException e) {
            throw new QtiSerializationException("Unexpected failure instantiating serializer driver from " + serializerUris, e);
        }

        /* Then configure it as per options */
        return configureSerializer(serializer, serializationOptions);
    }
    
    public TransformerHandler getSerializerDriverHandler(final List<URI> serializerUris, final XsltSerializationOptions serializationOptions) {
        /* Create serializer */
        ConstraintUtilities.ensureNotNull(serializerUris, "serializerUris");
        TransformerHandler serializerHandler;
        try {
            serializerHandler = getSaxTransformerFactory().newTransformerHandler(getCompiledStylesheetDriver(serializerUris));
        }
        catch (TransformerConfigurationException e) {
            throw new QtiSerializationException("Unexpected failure instantiating serializer driver from " + serializerUris, e);
        }

        /* Then configure it as per options */
        return configureSerializerHandler(serializerHandler, serializationOptions);
    }
    
    private TransformerHandler configureSerializerHandler(TransformerHandler serializerHandler, final XsltSerializationOptions serializationOptions) {
        configureSerializer(serializerHandler.getTransformer(), serializationOptions);
        return serializerHandler;
    }
    
    private Transformer configureSerializer(Transformer serializer, final XsltSerializationOptions serializationOptions) {
        /* Then configure it as per options */
        final boolean supportsXSLT20 = XsltFactoryUtilities.supportsXSLT20(serializer);
        if (serializationOptions!=null) {
            XsltSerializationMethod serializationMethod = serializationOptions.getSerializationMethod();
            if (serializationMethod==XsltSerializationMethod.XHTML && !supportsXSLT20) {
                /* Really want XHTML serialization, but we don't have an XSLT 2.0 processor
                 * so downgrading to XML.
                 */
                serializationMethod = XsltSerializationMethod.XML;
            }
            serializer.setOutputProperty(OutputKeys.METHOD, serializationMethod.getName());
            serializer.setOutputProperty(OutputKeys.INDENT, StringUtilities.toYesNo(serializationOptions.isIndenting()));
            if (serializationOptions.isIndenting()) {
                XsltFactoryUtilities.setIndentation(serializer, serializationOptions.getIndent());
            }
            serializer.setOutputProperty(OutputKeys.ENCODING, serializationOptions.getEncoding());
            serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, StringUtilities.toYesNo(!serializationOptions.isIncludingXMLDeclaration()));
            if (serializationOptions.getDoctypePublic()!=null) {
                serializer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, serializationOptions.getDoctypePublic());
            }
            if (serializationOptions.getDoctypeSystem()!=null) {
                serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, serializationOptions.getDoctypeSystem());
            }  
        }
        return serializer;
    }
    
    //----------------------------------------------------------
    
    private TransformerFactory getTransformerFactory() {
        /* Choose appropriate TransformerFactory implementation */
        TransformerFactory transformerFactory = XsltFactoryUtilities.createJAXPTransformerFactory();
        
        /* Configure URIResolver */
        transformerFactory.setURIResolver(xsltResourceResolver);
        return transformerFactory;
    }
    
    private SAXTransformerFactory getSaxTransformerFactory() {
        /* Choose appropriate TransformerFactory implementation */
        TransformerFactory transformerFactory = XsltFactoryUtilities.createJAXPTransformerFactory();
        XsltFactoryUtilities.requireFeature(transformerFactory, SAXTransformerFactory.FEATURE);
        
        /* Configure URIResolver */
        transformerFactory.setURIResolver(xsltResourceResolver);
        return (SAXTransformerFactory) transformerFactory;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(stylesheetCache=" + stylesheetCache
                + ",customXsltResourceLocator=" + customXsltResourceLocator
                + ")";
    }
}
