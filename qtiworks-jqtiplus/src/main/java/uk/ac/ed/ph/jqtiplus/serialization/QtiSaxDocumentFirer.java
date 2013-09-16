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
package uk.ac.ed.ph.jqtiplus.serialization;

import uk.ac.ed.ph.jqtiplus.ExtensionNamespaceInfo;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.QtiProfile;
import uk.ac.ed.ph.jqtiplus.attribute.Attribute;
import uk.ac.ed.ph.jqtiplus.attribute.ForeignAttribute;
import uk.ac.ed.ph.jqtiplus.group.accessibility.AccessibilityNode;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
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

import javax.xml.XMLConstants;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Fires a QTI {@link QtiNode} as a standalone SAX Document.
 * <p>
 * (Also includes functionality for mixing QTI and non-QTI SAX events
 * together, which is used by the QTIWorks engine.)
 *
 * <h2>Usage</h2>
 *
 * Not thread safe, not reusable.
 *
 * @author David McKain
 */
public final class QtiSaxDocumentFirer {

    /** Default XML schema instance NS prefix */
    public static final String DEFAULT_XSI_PREFIX = "xsi";

    private final JqtiExtensionManager jqtiExtensionManager;
    private final ContentHandler targetHandler;
    private final SaxFiringOptions saxFiringOptions;
    private final NamespacePrefixMappings requiredPrefixMappings;
    private final Map<String, String> schemaLocationMap;

    /**
     * Tracks changes in the default namespace.
     *
     * Key is {@link Object} (usually a {@link QtiNode}) where the default namespace change occurs
     * Value is the *previous* namespace URI (which is null before the first element is fired)
     */
    private final Map<Object, String> defaultNamespaceChangeMap;

    /** Current default namespace URI - used for prefix-less serialization in combination with defaultNamespaceChangeMap */
    private String currentDefaultNamespaceUri;

    /** Gets set once we have fired the document Element so that we know not to fire off xsi:schemaLocation again */
    private boolean doneStartDocumentElement;

    /** QTI namespace of document root*/
    private String rootQtiNamespace;

    /**
     * The namespace Uri of the most recently started element.
     */
    private String lastOpenedNamespaceUri;

    private int currentQtiDepth;

    public QtiSaxDocumentFirer(final JqtiExtensionManager jqtiExtensionManager,
            final ContentHandler targetHandler,
            final SaxFiringOptions saxFiringOptions) {
        this.jqtiExtensionManager = jqtiExtensionManager;
        this.targetHandler = targetHandler;
        this.saxFiringOptions = saxFiringOptions;
        this.requiredPrefixMappings = new NamespacePrefixMappings();
        this.schemaLocationMap = new HashMap<String, String>();

        /* Register xsi: prefix now, even if we're not going to use it */
        requiredPrefixMappings.registerStrict(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, DEFAULT_XSI_PREFIX);

        /* Clear state */
        this.defaultNamespaceChangeMap = new HashMap<Object, String>();
        this.currentDefaultNamespaceUri = null;
        this.doneStartDocumentElement = false;
        this.rootQtiNamespace = null;
        this.currentQtiDepth = 0;
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

    public void prepareFor(final QtiNode node) {
        final NamespacePrefixMappings preferredPrefixMappings = saxFiringOptions.getPreferredPrefixMappings();
        final boolean omitSchemaLocation = saxFiringOptions.isOmitSchemaLocation();

        /* See if caller wants the NS for this node prefixed */
        rootQtiNamespace = getNodeNamespaceUri(node);
        final String nodePreferredPrefix = preferredPrefixMappings.getPrefix(rootQtiNamespace);
        if (nodePreferredPrefix!=null) {
            requirePrefixMapping(rootQtiNamespace, nodePreferredPrefix);
        }

        /* Next traverse the JQTI model to work out what namespaces will be required in the resulting XML */
        final NamespacePreparationHandler handler = new NamespacePreparationHandler(getQtiProfile());
        QueryUtils.walkTree(handler, node);

        /* Put extension prefix mappings in scope */
        final Set<JqtiExtensionPackage<?>> usedExtensionPackages = handler.getUsedExtensionPackages();
        for (final JqtiExtensionPackage<?> usedExtensionPackage : usedExtensionPackages) {
            for (final Entry<String, ExtensionNamespaceInfo> entry : usedExtensionPackage.getNamespaceInfoMap().entrySet()) {
                final String nsUri = entry.getKey();
                final ExtensionNamespaceInfo extensionNamespaceInfo = entry.getValue();
                final String defaultPrefix = extensionNamespaceInfo.getDefaultPrefix();
                requirePrefixMapping(nsUri, defaultPrefix);
            }
        }

        /* Register attribute NS prefixes */
        final Set<String> attributeNamespaceUris = handler.getAttributeNamespaceUris();
        for (final String nsUri : attributeNamespaceUris) {
            requirePrefixMapping(nsUri);
        }

        /* See if caller wants any foreign elements to be prefixed */
        final Set<String> nonQtiElementNamespaceUris = handler.getNonQtiElementNamespaceUris();
        for (final String nsUri : nonQtiElementNamespaceUris) {
            final String preferredPrefix = preferredPrefixMappings.getPrefix(nsUri);
            if (preferredPrefix!=null) {
                /* Caller wants prefix */
                requirePrefixMapping(nsUri, preferredPrefix);
            }
        }

        /* Record QTI schema information for this Node */
        if (!omitSchemaLocation) {
            /* We are *always* writing out the location of the root namespace, usually QTI 2.1, but possibly a profile-specific URI */
            final String schemaLocation = getSchemaLocation(rootQtiNamespace);
            if (schemaLocation!=null) {
                schemaLocationMap.put(rootQtiNamespace, schemaLocation);
            }

            /* Now record schema information for extensions */
            for (final JqtiExtensionPackage<?> usedExtensionPackage : usedExtensionPackages) {
                for (final ExtensionNamespaceInfo extensionNamespaceInfo : usedExtensionPackage.getNamespaceInfoMap().values()) {
                    schemaLocationMap.put(extensionNamespaceInfo.getNamespaceUri(), extensionNamespaceInfo.getSchemaLocationUri());
                }
            }
        }
    }

    //-----------------------------------------------

    public void fireSaxDocument(final QtiNode node) throws SAXException {
        /* Prepare for this Node */
        prepareFor(node);

        /* Start document and put prefixes in scope */
        fireStartDocumentAndPrefixMappings();

        /* Fire off Node and its descendants */
        node.fireSaxEvents(this);

        /* Remove namespace prefixes from scope and end document */
        fireEndDocumentAndPrefixMappings();
    }

    public void fireStartDocumentAndPrefixMappings() throws SAXException {
        targetHandler.startDocument();

        /* Put namespace prefixes in scope */
        for (final Entry<String, String> entry : requiredPrefixMappings.entrySet()) {
            final String prefix = entry.getKey();
            final String namespaceUri = entry.getValue();
            if (!namespaceUri.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI) || !(saxFiringOptions.isOmitSchemaLocation() || schemaLocationMap.isEmpty())) {
                targetHandler.startPrefixMapping(prefix, namespaceUri);
            }
        }
    }

    public void fireEndDocumentAndPrefixMappings() throws SAXException {
        /* Remove namespace prefixes from scope */
        for (final Entry<String, String> entry : requiredPrefixMappings.entrySet()) {
            final String prefix = entry.getKey();
            final String namespaceUri = entry.getValue();
            if (!namespaceUri.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI) || !(saxFiringOptions.isOmitSchemaLocation() || schemaLocationMap.isEmpty())) {
                targetHandler.endPrefixMapping(prefix);
            }
        }

        targetHandler.endDocument();
    }

    /**
     * Requires at least one previous call to {@link QtiSaxDocumentFirer#prepareFor(QtiNode)}
     *
     * @param node
     * @throws SAXException
     */
    public void fireStartQtiElement(final AbstractNode node) throws SAXException {
        /* Build up attributes */
        final AttributesImpl xmlAttributes = new AttributesImpl();
        for (final Attribute<?> attribute : node.getAttributes()) {
            if (attribute.isRequired() || attribute.isSet()) {
                final String localName = attribute.getLocalName();
                // TODO - handle potential mismatch between the namespace/prefixes used during read and the desired output serialization namespaces
                final String namespaceUri = attribute.getNamespaceUri();
                final String qName = requiredPrefixMappings.getQName(namespaceUri, localName);
                xmlAttributes.addAttribute(namespaceUri, localName, qName,
                        "CDATA", attribute.valueToQtiString());
            }
        }

        // TODO : update to use more flexible profile namespace selection
        if (currentQtiDepth==0) {
            /* This is the opening a new QTI subtree. Decide on correct namespace for native QTI elements */
            rootQtiNamespace = getQtiProfile().getNamespaceForInstance(node);
        }

        /* Decide on element namespace */
        final String elementNamespaceUri = getNodeNamespaceUri(node);

        /* Fire element */
        fireStartElement(node, node.getQtiClassName(), elementNamespaceUri, xmlAttributes);
        ++currentQtiDepth;
    }

    public void fireStartElement(final Object object, final String localName, final String namespaceUri, final AttributesImpl attributes) throws SAXException {
        final AttributesImpl localAttributes = attributes != null ? attributes : new AttributesImpl();
        lastOpenedNamespaceUri = namespaceUri;
        final String prefix = requiredPrefixMappings.getPrefix(namespaceUri);
        String qName;
        if (prefix!=null) {
            /* This element will be prefixed */
            qName = prefix + ":" + localName;
        }
        else {
            /* Not using prefixes */
            qName = localName;

            /* See if we need to change the default namespace */
            if (currentDefaultNamespaceUri==null || !currentDefaultNamespaceUri.equals(namespaceUri)) {
                /* We're changing the default namespace, so register a prefix mapping and keep track
                 * of this Object so we know to end the mapping later */
                targetHandler.startPrefixMapping("", namespaceUri);
                defaultNamespaceChangeMap.put(object, currentDefaultNamespaceUri);
                currentDefaultNamespaceUri = namespaceUri;
            }
        }

        /* Maybe fire xsi:schemaLocation */
        if (!doneStartDocumentElement && !saxFiringOptions.isOmitSchemaLocation() && !schemaLocationMap.isEmpty()) {
            /* Add xsi:schemaLocation attribute */
            localAttributes.addAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation",
                    requiredPrefixMappings.getQName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation"),
                    "CDATA", createSchemaLocationAttributeValue());
        }

        /* Start element */
        targetHandler.startElement(namespaceUri, localName, qName, localAttributes);
        doneStartDocumentElement = true;
    }

    private String createSchemaLocationAttributeValue() {
        final StringBuilder attrBuilder = new StringBuilder();
        boolean doneFirst = false;
        for (final Entry<String, String> schemaEntry : schemaLocationMap.entrySet()) {
            if (doneFirst) {
                attrBuilder.append(' ');
            }
            attrBuilder.append(schemaEntry.getKey())
                .append(' ')
                .append(schemaEntry.getValue());
            doneFirst = true;
        }
        return attrBuilder.toString();
    }

    public void fireEndElement(final Object object, final String localName, final String namespaceUri) throws SAXException {
        /* Is this element prefixed? */
        final String prefix = requiredPrefixMappings.getPrefix(namespaceUri);
        String qName;
        if (prefix!=null) {
            /* This element will be prefixed */
            qName = prefix + ":" + localName;
        }
        else {
            /* Element is not prefixed */
            qName = localName;
        }

        /* End element */
        targetHandler.endElement(namespaceUri, localName, qName);

        /* See if we're ending a default namespace change */
        if (prefix==null && defaultNamespaceChangeMap.containsKey(object)) {
            targetHandler.endPrefixMapping("");
            currentDefaultNamespaceUri = defaultNamespaceChangeMap.get(object);
            defaultNamespaceChangeMap.remove(object);
        }
    }

    public void fireEndQtiElement(final AbstractNode node) throws SAXException {
        fireEndElement(node, node.getQtiClassName(), getNodeNamespaceUri(node));
        if (--currentQtiDepth==0) {
            rootQtiNamespace = null;
        }
    }

    public void fireText(final String string) throws SAXException {
        targetHandler.characters(string.toCharArray(), 0, string.length());
    }

    /**
     * Fires a textual element, namespaced the same as the last opened XML tag.
     * @param localName
     * @param content
     */
    public void fireSimpleElement(final String localName, final String content) throws SAXException {
        this.fireStartElement(content, localName, lastOpenedNamespaceUri, null);
        this.fireText(content);
        this.fireEndElement(content, localName, lastOpenedNamespaceUri);
    }

    //-----------------------------------------------

    public QtiProfile getQtiProfile() {
        return this.saxFiringOptions.getQtiProfile();
    }

    private String getNodeNamespaceUri(final QtiNode node) {
        final QtiProfile profile = getQtiProfile();
        if (rootQtiNamespace != null) {
            profile.getNamespaceForInstance(node, rootQtiNamespace);
        }
        return profile.getNamespaceForInstance(node);
    }

    private String getSchemaLocation(final String namespaceUri) {
        return getQtiProfile().getSchemaLocationForNamespace(namespaceUri);
    }

    //-----------------------------------------------

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
        private final QtiProfile qtiProfile;

        private NamespacePreparationHandler(final QtiProfile qtiProfile) {
            this.qtiProfile = qtiProfile;
        }


        @Override
        public boolean handleNode(final QtiNode node) {
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
            if (node instanceof AccessibilityNode) {
                nonQtiElementNamespaceUris.add(qtiProfile.getAccessibilityNamespace());
            }

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
