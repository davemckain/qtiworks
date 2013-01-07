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
package uk.ac.ed.ph.jqtiplus.xmlutils.locators;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter implementation of {@link URIResolver} that uses a {@link ResourceLocator} to
 * locate the required resources.
 * 
 * @see ResourceLocator
 * @author David McKain
 */
public final class XsltResourceResolver implements URIResolver {

    private static final Logger logger = LoggerFactory.getLogger(XsltResourceResolver.class);

    private final ResourceLocator resourceLocator;

    //-------------------------------------------

    public XsltResourceResolver(ResourceLocator resoureLocator) {
        this.resourceLocator = resoureLocator;
    }

    public ResourceLocator getResourceLocator() {
        return resourceLocator;
    }

    //-------------------------------------------

    @Override
    public Source resolve(String href, String base) {
        logger.trace("resolve(href={}, base={}", href, base);

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
            logger.trace("base URI {} is not a valid URI - returning null", base);
            return null;
        }
        final URI resolvedUri = baseUri.resolve(href);
        
        final InputStream resultStream = resourceLocator.findResource(resolvedUri);
        if (resultStream==null) {
            /* Failed to locate */
            if (logger.isTraceEnabled()) {
                logger.trace("resolve() did not find resource at {} after resolving href={} against base={} - returning null",
                        new Object[] { resolvedUri, href, base } );  
            }
            return null;
        }
        
        /* Success */
        if (logger.isTraceEnabled()) {
            logger.trace("resolve() successfully found resource at {} after resolving href={} against base={}",
                    new Object[] { resolvedUri, href, base } );
        }
        return new StreamSource(resultStream, resolvedUri.toString());
    }

    //-------------------------------------------
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
            + "(resourceLocator=" + resourceLocator
            + ")";
    }
}
