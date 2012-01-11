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
package uk.ac.ed.ph.jqtiplus.utils;

import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.xmlutils.FileSandboxResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceReader;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * FIXME: Document this type
 * 
 * TODO: This does NOT validate packages (at least for the time being...)
 * 
 * TODO: This is *overly lax* in resolving relative files within CC packages. (The CC spec
 * defines some rules for what is permitted.)
 *
 * @author David McKain
 */
public final class ContentPackageExtractor {
    
    private static final Logger logger = LoggerFactory.getLogger(ContentPackageExtractor.class);
    
    public static final String PACKAGE_URI_SCHEME = "this-content-package";
    
    private static final String MANIFEST_FILE_NAME = "imsmanifest.xml";
    
    private final File packageSandboxDirectory;
    private final XmlResourceReader xmlResourceReader;
    private final FileSandboxResourceLocator packageResourceLocator;
    
    public ContentPackageExtractor(File packageSandboxDirectory) {
        ConstraintUtilities.ensureNotNull(packageSandboxDirectory);
        this.packageSandboxDirectory = packageSandboxDirectory;
        this.xmlResourceReader = new XmlResourceReader(); /* (Not doing schema validation so no XSDs to register) */
        this.packageResourceLocator = new FileSandboxResourceLocator(PACKAGE_URI_SCHEME, packageSandboxDirectory);
    }
    
    public void parse() throws XmlResourceNotFoundException {
        /* First parse the "top" IMS manifest, which should always be present */
        ImsManifestReadResult manifestDetails = readManifestFile(createPackageUri(MANIFEST_FILE_NAME));
        
        /* Check namespace */
        
        /* Extract items & tests */
        
        /* TEMP */
        System.out.println(ObjectDumper.dumpObject(manifestDetails, DumpMode.DEEP));
        
    }
    
    private URI createPackageUri(String path) {
        return URI.create(PACKAGE_URI_SCHEME + ":/" + path);
    }

    /**
     * Attempts to read, parse and summarise the IMS Content Package manifest file at the given
     * URI.
     * <p>
     * This currently does NOT check namespaces, so probably permits any recent version of
     * Content Packaging to be considered as legal.
     * 
     * @param manifestSystemId
     * @throws XmlResourceNotFoundException if the manifest file could not be found
     */
    private ImsManifestReadResult readManifestFile(URI manifestSystemId) throws XmlResourceNotFoundException {
        /* Attempt to parse the manifest XML */
        logger.info("Reading manifest file at system ID {} using locator {}", manifestSystemId, packageResourceLocator);
        XmlReadResult xmlReadResult = xmlResourceReader.read(manifestSystemId, packageResourceLocator, false);
        XmlParseResult xmlParseResult = xmlReadResult.getXmlParseResult();
        
        /* If successful, extract information from the DOM */
        if (!xmlParseResult.isParsed()) {
            logger.warn("Manifest parse failed");
            return new ImsManifestReadResult(xmlParseResult);
        }
        
        /* Let's check that this looks like a proper manifest document.
         * NB: We're not presently checking namespaces!
         */
        Document document = xmlReadResult.getDocument();
        Element docElement = document.getDocumentElement();
        if (!"manifest".equals(docElement.getLocalName())) {
            logger.warn("Parsed manifest at system ID {} has root elemlent <{}> instead of <manifest>", manifestSystemId, docElement.getLocalName());
            return new ImsManifestReadResult(xmlParseResult);
        }
        
        /* Extract resources */
        String manifestNamespaceUri = docElement.getNamespaceURI();
        List<CpResource> resources = null;
        NodeList childNodes = docElement.getChildNodes();
        for (int i=0, size=childNodes.getLength(); i<size; i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType()==Node.ELEMENT_NODE && "resources".equals(item.getLocalName())) {
                resources = extractResources((Element) item);
                break;
            }
        }
        
        ImsManifestReadResult result = new ImsManifestReadResult(xmlParseResult, manifestNamespaceUri, resources);
        logger.info("Parsed of manifest at system ID {} yielded {}", manifestSystemId, result);
        return result;
    }
    
    private List<CpResource> extractResources(Element resourcesElement) {
        List<CpResource> resources = new ArrayList<CpResource>();
        NodeList childNodes = resourcesElement.getChildNodes();
        for (int i=0, size=childNodes.getLength(); i<size; i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType()==Node.ELEMENT_NODE && "resource".equals(childNode.getLocalName())) {
                Element resourceElement = (Element) childNode;
                String type = resourceElement.getAttribute("type");
                String href = resourceElement.getAttribute("href");
                List<String> fileHrefs = extractFileHrefs(resourceElement);
                resources.add(new CpResource(type, href, fileHrefs));
            }
        }
        return resources;
    }
    
    private List<String> extractFileHrefs(Element resourceElement) {
        List<String> hrefs = new ArrayList<String>();
        NodeList childNodes = resourceElement.getChildNodes();
        for (int i=0, size=childNodes.getLength(); i<size; i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType()==Node.ELEMENT_NODE && "file".equals(childNode.getLocalName())) {
                Element fileElement = (Element) childNode;
                String href = fileElement.getAttribute("href");
                hrefs.add(href);
            }
        }
        return hrefs;
    }
}
