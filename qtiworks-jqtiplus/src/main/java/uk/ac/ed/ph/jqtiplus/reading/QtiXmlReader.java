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
package uk.ac.ed.ph.jqtiplus.reading;

import uk.ac.ed.ph.jqtiplus.ExtensionNamespaceInfo;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.xmlutils.SchemaCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceReader;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceReaderException;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ChainedResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathHttpResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Wraps around {@link XmlResourceReader} to provide unified reader for QTI (and
 * some related IMS) XML resources.
 * <p>
 * It uses the following conventions:
 *
 * <ul>
 *   <li>
 *     Schema resources are loaded using {@link #JQTIPLUS_PARSER_RESOURCE_LOCATOR}.
 *     Extensions can make schemas available by adding them to the ClassPath in the appropriate
 *     place.
 *   </li>
 *   <li>
 *     Entities and DTD resources are loaded by first trying {@link #JQTIPLUS_PARSER_RESOURCE_LOCATOR},
 *     then the supplied XML input {@link ResourceLocator}. (This allows certain core DTD files
 *     to be maintained internally for performance reasons, before using the standard input locator.
 *   </li>
 * </ul>
 *
 * @author David McKain
 */
public final class QtiXmlReader {

    /**
     * Base path within ClassPath to search in for schema and
     * {@link ClassPathHttpResourceLocator} instance of {@link #parserResourceLocator}.
     */
    public static final String JQTIPLUS_PARSER_RESOURCE_CLASSPATH_BASE_PATH = "uk/ac/ed/ph/jqtiplus/xml-catalog";

    /**
     * Default {@link ResourceLocator} that will be used to locate schemas (and DTDs). This searches within
     * the ClassPath under {@link DEFAULT_PARSER_RESOURCE_CLASSPATH_BASE_PATH}.
     */
    public static final ResourceLocator JQTIPLUS_PARSER_RESOURCE_LOCATOR = new ClassPathHttpResourceLocator(JQTIPLUS_PARSER_RESOURCE_CLASSPATH_BASE_PATH);

    private final JqtiExtensionManager jqtiExtensionManager;

    /** Delegating {@link XmlResourceReader} */
    private final XmlResourceReader xmlResourceReader;

    public QtiXmlReader() {
        this(new JqtiExtensionManager(), null);
    }

    public QtiXmlReader(final JqtiExtensionManager jqtiExtensionManager) {
        this(jqtiExtensionManager, null);
    }

    public QtiXmlReader(final JqtiExtensionManager jqtiExtensionManager, final SchemaCache schemaCache) {
        ConstraintUtilities.ensureNotNull(jqtiExtensionManager, "jqtiExtensionManager");

        /* Merge extension schemas with core QTI 2.1 schema */
        final Map<String, String> resultingSchemaMapTemplate = new HashMap<String, String>();
        for (final Entry<String, ExtensionNamespaceInfo> entry : jqtiExtensionManager.getExtensionNamepaceInfoMap().entrySet()) {
            resultingSchemaMapTemplate.put(entry.getKey(), entry.getValue().getSchemaUri());
        }
        resultingSchemaMapTemplate.put(QtiConstants.QTI_21_NAMESPACE_URI, QtiConstants.QTI_21_SCHEMA_LOCATION);

        this.jqtiExtensionManager = jqtiExtensionManager;
        this.xmlResourceReader = new XmlResourceReader(JQTIPLUS_PARSER_RESOURCE_LOCATOR, resultingSchemaMapTemplate, schemaCache);
    }

    public JqtiExtensionManager getJqtiExtensionManager() {
        return jqtiExtensionManager;
    }

    public SchemaCache getSchemaCacheMap() {
        return xmlResourceReader.getSchemaCache();
    }

    //--------------------------------------------------

    /**
     * @throws XmlResourceNotFoundException if the XML resource with the given System ID cannot be
     *             located using the given {@link ResourceLocator}
     * @throws XmlResourceReaderException if an unexpected Exception occurred parsing and/or validating the XML, or
     *             if any of the required schemas could not be located.
     */
    public XmlReadResult read(final URI systemIdUri, final ResourceLocator inputResourceLocator, final boolean schemaValidating)
            throws XmlResourceNotFoundException {
        final ResourceLocator entityResourceLocator = new ChainedResourceLocator(JQTIPLUS_PARSER_RESOURCE_LOCATOR, inputResourceLocator);
        return xmlResourceReader.read(systemIdUri, inputResourceLocator, entityResourceLocator, schemaValidating);
    }

    /**
     * Creates a new {@link QtiXmlObjectReader} from this reader and the given
     * input {@link ResourceLocator}.
     */
    public QtiXmlObjectReader createQtiXmlObjectReader(final ResourceLocator inputResourceLocator) {
        return new QtiXmlObjectReader(this, inputResourceLocator);
    }

    //--------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(jqtiExtensionManager=" + jqtiExtensionManager
                + ",schemaCache=" + getSchemaCacheMap()
                + ")";
    }
}
