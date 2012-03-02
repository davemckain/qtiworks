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
package uk.ac.ed.ph.jqtiplus.xmlutils;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Unified implementation of common "resolver" interfaces used in
 * various parts of the JAXP APIs.
 * <p>
 * This uses your chosen implementation of {@link ResourceLocator} to actually locate the resolved resources.
 * <p>
 * By default, all of the resolve methods work as specified if the resolve resource cannot or will not be handled by your {@link ResourceLocator}. This
 * behaviour can be changed by setting the various <tt>setFailOnMissedXXX</tt> to true, which will instead result in a {@link XmlResourceReaderException} (or
 * {@link TransformerException} in the case of {@link URIResolver}). This can be useful if all of the resources that you will be searching for are under
 * complete control of the system, as this can indicate a misconfiguration or missing resource.
 * 
 * @see ResourceLocator
 * @author David McKain
 */
public final class UnifiedXmlResourceResolver implements EntityResolver, URIResolver, LSResourceResolver {

    private static final Logger logger = LoggerFactory.getLogger(UnifiedXmlResourceResolver.class);

    private ResourceLocator resourceLocator;
    private boolean failOnMissedEntityResolution;
    private boolean failOnMissedURIResolution;
    private boolean failOnMissedLRResourceResolution;
    private boolean failOnMissedLoad;

    //-------------------------------------------

    public UnifiedXmlResourceResolver() {
        this(null);
    }

    public UnifiedXmlResourceResolver(ResourceLocator resoureLocator) {
        setResourceLocator(resoureLocator);
    }

    public ResourceLocator getResourceLocator() {
        return resourceLocator;
    }

    public void setResourceLocator(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
    }


    public boolean isFailOnMissedEntityResolution() {
        return failOnMissedEntityResolution;
    }

    public void setFailOnMissedEntityResolution(boolean failOnMissedEntityResolution) {
        this.failOnMissedEntityResolution = failOnMissedEntityResolution;
    }


    public boolean isFailOnMissedURIResolution() {
        return failOnMissedURIResolution;
    }

    public void setFailOnMissedURIResolution(boolean failOnMissedURIResolution) {
        this.failOnMissedURIResolution = failOnMissedURIResolution;
    }


    public boolean isFailOnMissedLRResourceResolution() {
        return failOnMissedLRResourceResolution;
    }

    public void setFailOnMissedLRResourceResolution(boolean failOnMissedLRResourceResolution) {
        this.failOnMissedLRResourceResolution = failOnMissedLRResourceResolution;
    }


    public boolean isFailOnMissedLoad() {
        return failOnMissedLoad;
    }

    public void setFailOnMissedLoad(boolean failOnMissedLoad) {
        this.failOnMissedLoad = failOnMissedLoad;
    }

    //-------------------------------------------
    // DTD resolution

    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        if (logger.isDebugEnabled()) {
            logger.debug("resolveEntity(publicId={}, systemId={})", publicId, systemId);
        }
        final URI systemIdUri = makeURI(systemId, "Bad systemId URI {}");
        if (systemIdUri == null) {
            return null;
        }
        InputSource result = null;
        final InputStream stream = resourceLocator.findResource(systemIdUri);
        if (stream != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("resolveEntity() succeeded for publicId={}, systemId={}", publicId, systemId);
            }
            result = new InputSource(stream);
            result.setPublicId(publicId);
            result.setSystemId(systemId);

        }
        else {
            maybeFail(failOnMissedEntityResolution, "resolveEntity() could not resolve publicId=" + publicId + ", systemId=" + systemId);
            return null;
        }
        return result;
    }


    //-------------------------------------------
    // XSD resolution

    @Override
    public LSInput resolveResource(String type, String namespaceUri, String publicId,
            String systemId, String baseUriString) {
        if (logger.isDebugEnabled()) {
            logger.debug("resolveResouce(type={}, nsUri={}, publicId={}, systemId={}, baseUri={}",
                    new Object[] { type, namespaceUri, publicId, systemId, baseUriString });
        }
        if (systemId == null) {
            return null;
        }
        URI resolvedUri = makeURI(systemId, "Bad systemId URI {}");
        if (resolvedUri == null) {
            return null;
        }
        if (baseUriString != null) {
            final URI baseUri = makeURI(baseUriString, "Bad base URI {}");
            if (baseUri == null) {
                return null;
            }
            resolvedUri = baseUri.resolve(resolvedUri);
        }
        LSInputImpl result = null;
        final InputStream stream = resourceLocator.findResource(resolvedUri);
        if (stream != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("resolveResource() succeeded for publicId={}, systemId={}, baseUri={}",
                        new Object[] { publicId, systemId, baseUriString });
            }
            result = new LSInputImpl();
            result.setByteStream(stream);
            result.setPublicId(publicId);
            result.setSystemId(systemId);
            result.setBaseURI(baseUriString);
        }
        else {
            maybeFail(failOnMissedLRResourceResolution, "resolveResource() could not resolve publicId=" + publicId
                    + ", systemId=" + systemId
                    + ", baseUri=" + baseUriString);
            return null;
        }
        return result;
    }

    //-------------------------------------------
    // URI resolution for XSLT

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        logger.debug("resolve(href={}, base={}", href, base);

        /* Handle the special document('') case as normal */
        if (href.equals("")) {
            return null;
        }

        /* Resolve href against base URI */
        URI baseUri;
        try {
            baseUri = new URI(base);
        }
        catch (final URISyntaxException e) {
            throw new TransformerException("Could not convert base=" + base + " into a URI", e);
        }
        final URI resolvedUri = baseUri.resolve(href);

        /* Now load resource */
        final Source result = loadResourceAsSource(resolvedUri);
        if (result == null) {
            final String message = "resolve() could not resolve href=" + href + ", base=" + base + ", resolved=" + resolvedUri;
            logger.warn(message);
            if (failOnMissedURIResolution) {
                throw new TransformerException(message);
            }
        }
        else if (logger.isDebugEnabled()) {
            logger.debug("resolve() successfully located resolved resource at {}", resolvedUri);
        }
        return result;
    }

    //-------------------------------------------

    public InputStream loadResourceAsStream(String systemId) {
        logger.debug("loadResourceAsStream(systemId={})", systemId);

        final URI systemIdUri = makeURI(systemId, "Bad systemId URI {}");
        if (systemIdUri == null) {
            return null;
        }

        final InputStream result = resourceLocator.findResource(systemIdUri);
        if (result == null) {
            maybeFail(failOnMissedLoad, "loadResourceAsStream() could not load resource at systemId=" + systemId);
            return null;
        }
        else if (logger.isDebugEnabled()) {
            logger.debug("loadResourceAsStream() successful on systemId={}", systemId);
        }
        return result;
    }

    public Source loadResourceAsSource(String systemId) {
        logger.debug("loadResourceAsSource(systemId={})", systemId);

        final URI systemIdUri = makeURI(systemId, "Bad systemId URI {}");
        if (systemIdUri == null) {
            return null;
        }

        final Source result = loadResourceAsSource(systemIdUri);
        if (result == null) {
            maybeFail(failOnMissedLoad, "loadResourceAsSource() could not load resource at systemId=" + systemId);
            return null;
        }
        else if (logger.isDebugEnabled()) {
            logger.debug("loadResourceAsSource() successful on systemId={}", systemId);
        }
        return result;
    }

    private Source loadResourceAsSource(final URI systemIdUri) {
        Source result = null;
        final InputStream resourceStream = resourceLocator.findResource(systemIdUri);
        if (resourceStream != null) {
            result = new StreamSource(resourceStream, systemIdUri.toString());
        }
        return result;
    }

    //-------------------------------------------

    private static URI makeURI(String uriString, String failureMessageTemplate) {
        try {
            return new URI(uriString);
        }
        catch (final URISyntaxException e) {
            logger.warn(failureMessageTemplate, uriString);
            return null;
        }
    }

    private static void maybeFail(boolean shouldFail, String message) {
        logger.warn(message);
        if (shouldFail) {
            throw new XmlResourceReaderException(message);
        }
    }
}
