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
package uk.ac.ed.ph.jqtiplus.serialization2;

import uk.ac.ed.ph.jqtiplus.ExtensionNamespaceInfo;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.attribute.Attribute;
import uk.ac.ed.ph.jqtiplus.attribute.ForeignAttribute;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.block.ForeignElement;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.CustomOperator;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.CustomInteraction;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;
import uk.ac.ed.ph.jqtiplus.utils.TreeWalkNodeHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Fires a QTI {@link XmlNode} as a standalone SAX Document
 *
 * TODO-LATER: Would be nice to be able to specify how to output MathML elements, i.e. using a
 * prefix instead of changing the default namespace. Need to do the same for apip:accessibility, if we
 * eventually add support for that.
 *
 * @author David McKain
 */
/*
 * Thinking for new version:
 *
 * - takes a NSPrefixMappings with caller's *preferences*
 * - caller decides whether to set schema locations
 * - caller should decide which NS prefix mappings should be explicitly defined at start of document
 * - caller would need to declare which XmlNode(s) are going to be fired so that extensions, foreign elements, APIP and MathML can be checked
 *   and, if found, the required prefix mappings be put in scope at the start of the document.
 */
public final class QtiSaxDocumentFirer2 {

    private final JqtiExtensionManager jqtiExtensionManager;
    private final ContentHandler targetHandler;
    private final NamespacePrefixMappings prefixMappingPreferences;
    private final NamespacePrefixMappings requiredPrefixMappings;
    private final Map<String, String> schemaLocationMap;

    public QtiSaxDocumentFirer2(final JqtiExtensionManager jqtiExtensionManager, final ContentHandler targetHandler, final NamespacePrefixMappings prefixMappingPreferences) {
        this.jqtiExtensionManager = jqtiExtensionManager;
        this.targetHandler = targetHandler;
        this.prefixMappingPreferences = new NamespacePrefixMappings(prefixMappingPreferences);
        this.requiredPrefixMappings = new NamespacePrefixMappings();
        this.schemaLocationMap = new HashMap<String, String>();
    }

    /* (Will be put in scope at start of document) */
    public void requirePrefixMapping(final String namespaceUri, final String requestedPrefix) {
        if (!requiredPrefixMappings.isNamespaceUriRegistered(namespaceUri)) {
            requiredPrefixMappings.registerLax(namespaceUri, requestedPrefix);
        }
    }

    public void requirePrefixMapping(final String namespaceUri) {
        requirePrefixMapping(namespaceUri, "ns");
    }

    public void prepareFor(final XmlNode node) {
        /* Firstly, work out what namespaces will be required in the resulting XML */
        final NamespacePreparationHandler handler = new NamespacePreparationHandler();
        QueryUtils.walkTree(handler, node);

        /* Put extension prefix mappings in scope */
        final Set<JqtiExtensionPackage<?>> usedExtensionPackages = handler.getUsedExtensionPackages();
        for (final JqtiExtensionPackage<?> usedExtensionPackage : usedExtensionPackages) {
            for (final Entry<String, ExtensionNamespaceInfo> entry : usedExtensionPackage.getNamespaceInfoMap().entrySet()) {
                final String nsUri = entry.getKey();
                final ExtensionNamespaceInfo extensionNamespaceInfo = entry.getValue();
                final String defaultPrefix = extensionNamespaceInfo.getDefaultPrefix();
                requiredPrefixMappings.registerLax(nsUri, defaultPrefix);
            }
        }

        /* Put foreign attribute NS prefixes in scope */
        final Set<String> attributeNamespaceUris = handler.getAttributeNamespaceUris();
        for (final String nsUri : attributeNamespaceUris) {
            requirePrefixMapping(nsUri);
        }

        /* See if caller wants any foreign elements to be prefixed */
        final Set<String> nonQtiElementNamespaceUris = handler.getNonQtiElementNamespaceUris();
        for (final String nsUri : nonQtiElementNamespaceUris) {
            final String preferredPrefix = prefixMappingPreferences.getPrefix(nsUri);
            if (preferredPrefix!=null) {
                /* Caller wants prefix */
                requirePrefixMapping(nsUri, preferredPrefix);
            }
        }

        /* Record QTI schema information for this Node */
        /* NB: We are *always* writing out QTI 2.1 */
        /* TODO: <assessmentResult> is different! */
        schemaLocationMap.put(QtiConstants.QTI_21_NAMESPACE_URI, QtiConstants.QTI_21_SCHEMA_LOCATION);

        /* Now record schema information for extensions */
        for (final JqtiExtensionPackage<?> usedExtensionPackage : usedExtensionPackages) {
            for (final ExtensionNamespaceInfo extensionNamespaceInfo : usedExtensionPackage.getNamespaceInfoMap().values()) {
                schemaLocationMap.put(extensionNamespaceInfo.getNamespaceUri(), extensionNamespaceInfo.getSchemaUri());
            }
        }
    }

    //-----------------------------------------------

    public void fireStartDocument(final boolean fireSchemaLocationInfo) {
        /* I AM HERE! FINISH ME! */
    }


    //-----------------------------------------------

    /*
     * There are 2 use cases to consider:
     *
     * 1. Firing a JQTI Object. The resulting document namespace and schema location is then determined by the Object itself.
     * 2. Fire a custom wrapper around a JQTI Object. The document element's namespace and XSI are chosen by the caller.
     *
     * Namespaces:
     *
     * 1. Element namespaces will cause a change in default namespace, unless a prefix has been declared for them. Prefixes will be put in scope on the root element
     * 2. Attribute namespaces will always be done by prefix, which will always be put in scope on the root element.
     */
    public void fireSaxDocument(final XmlNode node) throws SAXException {
        /* Firstly, work out what namespaces will be required in the resulting XML */
        final NamespacePreparationHandler handler = new NamespacePreparationHandler();
        QueryUtils.walkTree(handler, node);

        /* Now start deciding what prefix mappings we'll make at the root */
        final NamespacePrefixMappings namespacePrefixMappings = new NamespacePrefixMappings();

        /* First, we'll reserve 'xsi' for schema instances */
        namespacePrefixMappings.registerSchemaInstanceMapping();

        /* FINISH REFACTORING BELOW */

        /* TODO!!!
         * Maybe next register prefix for MathML, if requested and used within document.
         * Then maybe register prefix for APIP, if requested and used within document.
         *
         * Tree search would have to
         * * find extensions used
         * * determine whether MathML is used
         * * determine whether APIP is used
         * * find foreign attributes
         */

        /* Next let each extension package that has been used have a shot at registering prefixes */
        final Set<JqtiExtensionPackage<?>> usedExtensionPackages = QueryUtils.findExtensionsWithin(jqtiExtensionManager, node);
        namespacePrefixMappings.registerExtensionPrefixMappings(usedExtensionPackages);

        /* Register prefixes for each foreign attribute in non-default namespace */
        namespacePrefixMappings.registerForeignAttributeNamespaces(node);

        final SaxEventFirer saxEventFirer = new SaxEventFirer(namespacePrefixMappings,
                createSchemaLocationMap(usedExtensionPackages), targetHandler, saxFiringOptions);

        /* Put namespace prefixes in scope and fire start of document */
        saxEventFirer.fireStartDocumentAndPrefixMappings();

        /* Create callback for nodes */
        final QtiSaxFiringContext saxFiringContext = new QtiSaxFiringContext(saxEventFirer, namespacePrefixMappings);

        /* Get document Node to fire itself off */
        node.fireSaxEvents(saxFiringContext);

        /* Remove namespace prefixes from scope and end document */
        saxEventFirer.fireEndDocumentAndPrefixMappings();
    }

    public static Map<String, String> createSchemaLocationMap(final Set<JqtiExtensionPackage<?>> usedExtensionPackages) {
        final Map<String, String> result = new HashMap<String, String>();

        /* First do QTI 2.1 namespace.
         * NB: We are ONLY writing out QTI 2.1 here.
         */
        /* TODO: If we add support for APIP, we'll need to change namespace here */
        result.put(QtiConstants.QTI_21_NAMESPACE_URI, QtiConstants.QTI_21_SCHEMA_LOCATION);

        /* Then do each extension */
        for (final JqtiExtensionPackage<?> jqtiExtensionPackage : usedExtensionPackages) {
            for (final ExtensionNamespaceInfo extensionNamespaceInfo : jqtiExtensionPackage.getNamespaceInfoMap().values()) {
                result.put(extensionNamespaceInfo.getNamespaceUri(), extensionNamespaceInfo.getSchemaUri());
            }
        }
        return result;
    }

    /**
     * Instance of {@link TreeWalkNodeHandler} that will find out what non-QTI namespaces
     * will be required when serializing a particular Object to XML.
     * <p>
     * NB: This does NOT include the required QTI namespace for the Object itself.
     */
    private final class NamespacePreparationHandler implements TreeWalkNodeHandler {

        private final Set<JqtiExtensionPackage<?>> usedExtensionPackages = new HashSet<JqtiExtensionPackage<?>>();
        private final Set<String> nonQtiElementNamespaceUris = new HashSet<String>();
        private final Set<String> attributeNamespaceUris = new HashSet<String>();

        @Override
        public boolean handleNode(final XmlNode node) {
            /* If Node is an extension element, then note which package that element belongs to */
            if (node instanceof CustomOperator) {
                final JqtiExtensionPackage<?> jqtiExtensionPackage = jqtiExtensionManager.getJqtiExtensionPackageImplementingOperator((CustomOperator<?>) node);
                if (jqtiExtensionPackage!=null) {
                    usedExtensionPackages.add(jqtiExtensionPackage);
                }
            }
            else if (node instanceof CustomInteraction) {
                final JqtiExtensionPackage<?> jqtiExtensionPackage = jqtiExtensionManager.getJqtiExtensionPackageImplementingInteraction((CustomInteraction<?>) node);
                if (jqtiExtensionPackage!=null) {
                    usedExtensionPackages.add(jqtiExtensionPackage);
                }
            }
            /* TODO: If we add support for apip:accessibility, check for it here */

            /* Check for use of MathML */
            if (node instanceof uk.ac.ed.ph.jqtiplus.node.content.mathml.Math) {
                nonQtiElementNamespaceUris.add(QtiConstants.MATHML_NAMESPACE_URI);
            }
            /* Find any foreign namespaces (NB: MathML will appear here too if used) */
            else if (node instanceof ForeignElement) {
                nonQtiElementNamespaceUris.add(((ForeignElement) node).getNamespaceUri());
            }
            /* Now find namespaced attributes */
            for (final Attribute<?> attribute : node.getAttributes()) {
                if (attribute instanceof ForeignAttribute) {
                    attributeNamespaceUris.add(attribute.getNamespaceUri());
                }
            }

            /* Keep descending */
            return true;
        }

        public Set<JqtiExtensionPackage<?>> getUsedExtensionPackages() {
            return usedExtensionPackages;
        }

        public Set<String> getNonQtiElementNamespaceUris() {
            return nonQtiElementNamespaceUris;
        }

        public Set<String> getAttributeNamespaceUris() {
            return attributeNamespaceUris;
        }
    }
}
