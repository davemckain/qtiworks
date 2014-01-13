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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Adaptor implementation of {@link EntityResolver} that uses a {@link ResourceLocator} to
 * locate entities.
 * <p>
 * Override the {@link #onMiss(String, String)} method if you'd like to do custom handling
 * if an entity cannot be resolved.
 *
 * @see ResourceLocator
 * @author David McKain
 */
public class EntityResourceResolver implements EntityResolver {

    private static final Logger logger = LoggerFactory.getLogger(EntityResourceResolver.class);

    private final ResourceLocator resourceLocator;

    public EntityResourceResolver(final ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
    }

    public ResourceLocator getResourceLocator() {
        return resourceLocator;
    }

    //-------------------------------------------

    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) {
        logger.trace("resolveEntity(publicId={}, systemId={})", publicId, systemId);

        URI systemIdUri;
        try {
            systemIdUri = new URI(systemId);
        }
        catch (final URISyntaxException e) {
            logger.trace("System ID {} could not be parsed as a URI", systemId);
            return onMiss(publicId, systemId);
        }

        final InputStream stream = resourceLocator.findResource(systemIdUri);
        if (stream==null) {
            return onMiss(publicId, systemId);
        }

        logger.trace("resolveEntity() succeeded for publicId={}, systemId={}", publicId, systemId);
        final InputSource result = new InputSource(stream);
        result.setPublicId(publicId);
        result.setSystemId(systemId);
        return result;
    }

    /**
     * Subclasses can override this method if you would like to handle the case when the
     * required entity cannot be resolved and possibly provide a "fallback" {@link InputSource}.
     *
     * @param publicId The public identifier of the external entity
     *        being referenced, or null if none was supplied.
     * @param systemId The system identifier of the external entity
     *        being referenced.
     *
     * @return suitable fallback {@link InputSource}, or null.
     */
    public InputSource onMiss(final String publicId, final String systemId) {
        return null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
            + "(resourceLocator=" + resourceLocator
            + ")";
    }
}
