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
package uk.ac.ed.ph.jqtiplus.utils.contentpackaging;

import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.xmlutils.CustomUriScheme;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceReader;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.FileSandboxResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.NullResourceLocator;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Helper class for extracting IMS content packages
 *
 * TODO: This does NOT validate packages (at least for the time being...)
 *
 * TODO: This is *overly lax* in resolving relative files within CC packages. (The CC spec
 * defines some rules for what is permitted.)
 *
 * @author David McKain
 */
public final class QtiContentPackageExtractor {

    private static final Logger logger = LoggerFactory.getLogger(QtiContentPackageExtractor.class);

    public static final CustomUriScheme PACKAGE_URI_SCHEME = new CustomUriScheme("this-content-package");

    /** Name of IMS manifest file */
    public static final String IMS_MANIFEST_FILE_NAME = "imsmanifest.xml";

    /** <tt>cp:resource/@type</tt> for supported QTI items. */
    private static final String[] ITEM_TYPES = {
        "imsqti_item_xmlv2p1", /* (Correct for QTI 2.1) */
        "imsqti_item_xml_v2p1" /* (Compatibility for old aqurate) */
    };

    /** <tt>cp:resource/@type</tt> for supported QTI tests. */
    private static final String[] TEST_TYPES = {
        "imsqti_test_xmlv2p1", /* (Correct for QTI 2.1) */
    };

    private final XmlResourceReader xmlResourceReader;
    private final FileSandboxResourceLocator packageResourceLocator;

    public QtiContentPackageExtractor(final File packageSandboxDirectory) {
        ConstraintUtilities.ensureNotNull(packageSandboxDirectory);
        this.xmlResourceReader = new XmlResourceReader(new NullResourceLocator()); /* (Not doing schema validation so no XSDs to register) */
        this.packageResourceLocator = new FileSandboxResourceLocator(PACKAGE_URI_SCHEME, packageSandboxDirectory);
    }

    public QtiContentPackageSummary parse() throws XmlResourceNotFoundException, ImsManifestException {
        /* First parse the "top" IMS manifest, which should always be present */
        final ImsManifestReadResult manifest = readManifestFile(IMS_MANIFEST_FILE_NAME);
        Set<String> testResourceHrefs = null;
        Set<String> itemResourceHrefs = null;
        Set<String> fileHrefs = null;
        if (manifest.isUnderstood()) {
            /* Extract items & tests */
            itemResourceHrefs = getResolvedResourceHrefsByTypes(manifest, ITEM_TYPES);
            testResourceHrefs = getResolvedResourceHrefsByTypes(manifest, TEST_TYPES);
            fileHrefs = getResolvedFileHrefs(manifest);
        }
        return new QtiContentPackageSummary(manifest, testResourceHrefs, itemResourceHrefs, fileHrefs);
    }

    private Set<String> getResolvedResourceHrefsByTypes(final ImsManifestReadResult manifest, final String... types) {
        final Set<String> result = new HashSet<String>();
        final Map<String, List<ContentPackageResource>> resourcesByTypeMap = manifest.getResourcesByTypeMap();
        for (final String type : types) {
            final List<ContentPackageResource> resourcesByType = resourcesByTypeMap.get(type);
            if (resourcesByType!=null) {
                for (final ContentPackageResource resource : resourcesByType) {
                    final URI resourceUri = resource.getHref();
                    final URI resolvedUri = manifest.getXmlParseResult().getSystemId().resolve(resourceUri);
                    result.add(PACKAGE_URI_SCHEME.uriToPath(resolvedUri));
                }
            }

        }
        return result;
    }

    private Set<String> getResolvedFileHrefs(final ImsManifestReadResult manifest) {
        final Set<String> result = new HashSet<String>();
        for (final ContentPackageResource resource : manifest.getResourceList()) {
            for (final URI fileHref : resource.getFileHrefs()) {
                final URI resolvedUri = manifest.getXmlParseResult().getSystemId().resolve(fileHref);
                result.add(PACKAGE_URI_SCHEME.uriToPath(resolvedUri));
            }
        }
        return result;
    }

    /**
     * Attempts to read, parse and summarise the IMS Content Package manifest file at the given
     * URI.
     * <p>
     * This currently does NOT check namespaces, so probably permits any recent version of
     * Content Packaging to be considered as legal.
     *
     * @param manifestHref href/path of the manifest within the package
     *
     * @throws XmlResourceNotFoundException if the manifest file could not be found
     * @throws ImsManifestException if the manifest could not be understood
     */
    private ImsManifestReadResult readManifestFile(final String manifestHref)
            throws XmlResourceNotFoundException, ImsManifestException {
        /* Attempt to parse the manifest XML */
        final URI manifestSystemId = PACKAGE_URI_SCHEME.pathToUri(manifestHref);
        logger.debug("Reading manifest file at system ID {} using locator {}", manifestSystemId, packageResourceLocator);
        final XmlReadResult xmlReadResult = xmlResourceReader.read(manifestSystemId, packageResourceLocator, packageResourceLocator, false);
        final XmlParseResult xmlParseResult = xmlReadResult.getXmlParseResult();

        /* If successful, extract debugrmation from the DOM */
        if (!xmlParseResult.isParsed()) {
            logger.debug("XML parse of IMS manifest at System ID {} failed: {}", manifestSystemId, xmlParseResult);
            throw new ImsManifestException("XML parse of IMS manifest file at " + manifestHref + " failed", xmlParseResult);
        }

        /* Let's check that this looks like a proper manifest document.
         * NB: We're not presently checking namespaces!
         */
        final Document document = xmlReadResult.getDocument();
        final Element docElement = document.getDocumentElement();
        if (!"manifest".equals(docElement.getLocalName())) {
            logger.debug("Parsed manifest at System ID {} has root element <{}> instead of <manifest>", manifestSystemId, docElement.getLocalName());
            throw new ImsManifestException("XML file at " + manifestHref
                    + " does not appear to be an IMS manifest - its root element is "
                    + docElement.getNodeName(), xmlParseResult);
        }

        /* Extract resources */
        final List<String> errorMessageBuilder = new ArrayList<String>();
        final String manifestNamespaceUri = docElement.getNamespaceURI();
        List<ContentPackageResource> resources = null;
        final NodeList childNodes = docElement.getChildNodes();
        for (int i=0, size=childNodes.getLength(); i<size; i++) {
            final Node item = childNodes.item(i);
            if (item.getNodeType()==Node.ELEMENT_NODE && "resources".equals(item.getLocalName())) {
                resources = extractResources((Element) item, errorMessageBuilder);
                break;
            }
        }

        /* Fail if any errors were discovered */
        if (!errorMessageBuilder.isEmpty()) {
            logger.debug("Parsed manifest at System ID {} contained some bad href attributes: {}", manifestSystemId, errorMessageBuilder);
            throw new ImsManifestException("Some href attributes within the manifest were not valid URIs",
                    xmlParseResult, errorMessageBuilder);
        }

        final ImsManifestReadResult result = new ImsManifestReadResult(manifestHref, xmlParseResult, manifestNamespaceUri, resources);
        logger.debug("Parsed of manifest at system ID {} yielded {}", manifestSystemId, result);
        return result;
    }

    private List<ContentPackageResource> extractResources(final Element resourcesElement, final List<String> errorMessageBuilder) {
        final List<ContentPackageResource> resources = new ArrayList<ContentPackageResource>();
        final NodeList childNodes = resourcesElement.getChildNodes();
        for (int i=0, size=childNodes.getLength(); i<size; i++) {
            final Node childNode = childNodes.item(i);
            if (childNode.getNodeType()==Node.ELEMENT_NODE && "resource".equals(childNode.getLocalName())) {
                final Element resourceElement = (Element) childNode;
                final String type = resourceElement.getAttribute("type");
                final String href = resourceElement.getAttribute("href"); /* (optional) */
                final List<URI> fileHrefs = extractFileHrefs(resourceElement, errorMessageBuilder);
                try {
                    resources.add(new ContentPackageResource(type, href!=null ? new URI(href) : null, fileHrefs));
                }
                catch (final URISyntaxException e) {
                    errorMessageBuilder.add("<resource> href attribute '" + href + "' is not a valid URI - ignoring");
                }
            }
        }
        return resources;
    }

    private List<URI> extractFileHrefs(final Element resourceElement, final List<String> errorMessageBuilder) {
        final List<URI> hrefs = new ArrayList<URI>();
        final NodeList childNodes = resourceElement.getChildNodes();
        for (int i=0, size=childNodes.getLength(); i<size; i++) {
            final Node childNode = childNodes.item(i);
            if (childNode.getNodeType()==Node.ELEMENT_NODE && "file".equals(childNode.getLocalName())) {
                final Element fileElement = (Element) childNode;
                final String href = fileElement.getAttribute("href"); /* (mandatory) */
                try {
                    hrefs.add(new URI(href));
                }
                catch (final URISyntaxException e) {
                    errorMessageBuilder.add("<file> href attribute '" + href + "' is not a valid URI - ignoring");
                }
            }
        }
        return hrefs;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(xmlResourceReader=" + xmlResourceReader
                + ",packageResourceLocator=" + packageResourceLocator
                + ")";
    }
}
