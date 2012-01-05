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
package uk.ac.ed.ph.qtiengine.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

/**
 * Based on Southampton's original "ContentPackage" class. This unpacks an
 * IMS Content Package containing QTI items/tests, or an IMS Common Cartridge Assessment,
 * returning information about the QTI resources within.
 * 
 * @author Jonathon Hare
 * @author David McKain
 */
public class ContentPackageExtractor {
    
    private static final String MANIFEST_FILE_NAME = "imsmanifest.xml";
    
    /** <tt>cp:resource/@type</tt> for Common Cartridge Assessments */
    public static final String CC_ASSESSMENT_TYPE = "imsqti_xmlv2p1/imscc_xmlv1p1/assessment";
    
    /** <tt>cp:resource/@type</tt> for supported QTI items. */
    public static final String[] ITEM_TYPES = {
        "imsqti_item_xmlv2p1", /* (Correct for QTI 2.1) */
        "imsqti_item_xmlv2p0", /* (Correct for QTI 2.0) */
        "imsqti_item_xml_v2p1" /* (Compatibility for old aqurate) */
    };
    
    /** <tt>cp:resource/@type</tt> for supported QTI tests. */
    public static final String[] TEST_TYPES = {
        "imsqti_test_xmlv2p1", /* (Correct for QTI 2.1) */
        "imsqti_test_xmlv2p0"  /* (QTI 2.0) */
    };
    
    /** 
     * <tt>cp:resource/@type</tt> that "old" QTIEngine responds to. This will
     * be used as a fallback if no match is found for {@link #TEST_TYPES}
     */
    private static final String OLD_QTIENGINE_TEST_TYPE = "imsqti_assessment_xmlv2p1";

    
    private final ZipInputStream zipInputStream;
    private final File destinationDirectory;
    
    private File containerManifestFile;
    private File qtiManifestFile;
    private boolean isCommonCartridge;
    private List<String> testResourceHrefs;
    private List<String> itemResourceHrefs;
    private List<String> specialResourceHrefs;
    private boolean unpacked;
    
    public ContentPackageExtractor(ZipInputStream zipInputStream, File destinationDirectory) {
        this.zipInputStream = zipInputStream;
        this.destinationDirectory = destinationDirectory;
    }

    public ContentPackageExtractor(InputStream inputStream, File destinationDirectory) {
        this(new ZipInputStream(inputStream), destinationDirectory);
    }

    public void unpack() {
        unpack(false);
    }

    public void unpack(boolean deleteDestDirectoryFirst) {
        try {
            if (destinationDirectory.exists()) {
                if (deleteDestDirectoryFirst) {
                    IOUtilities.recursivelyDelete(destinationDirectory);
                }
                else {
                    throw new ContentPackageException("Destination directory already exists.");
                }
            }
            IOUtilities.ensureDirectoryCreated(destinationDirectory);

            /* Extract ZIP contents */
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                File destFile = new File(destinationDirectory, zipEntry.getName());
                if (!zipEntry.isDirectory()) {
                    IOUtilities.ensureFileCreated(destFile);
                    IOUtilities.transfer(zipInputStream, new FileOutputStream(destFile), false, true);
                    zipInputStream.closeEntry();
                }
            }

            zipInputStream.close();
        }
        catch (IOException ex) {
            throw new ContentPackageException(ex);
        }
        
        /* Extract "container" manifest, make sure it exists and look up resource data to
         * see whether it's a "standard" manifest for a package container QTI items/tests,
         * or a common cartridge manifest that points to a second manifest containing the
         * actual QTI items/tests.
         */
        containerManifestFile = new File(destinationDirectory, MANIFEST_FILE_NAME);
        IMSManifestHandler containerHandler = readManifestFile(containerManifestFile, MANIFEST_FILE_NAME);
        specialResourceHrefs = new ArrayList<String>();
        specialResourceHrefs.add(MANIFEST_FILE_NAME);
        if (containerHandler.getResourceTypes().contains(CC_ASSESSMENT_TYPE)) {
            /* This is a "Common Cartridge" package. In this case, there should be a singular <file>
             * pointing to where the QTI manifest lives */
            isCommonCartridge = true;
            Set<String> fileHrefs = containerHandler.getFileHrefs();
            if (fileHrefs.size()!=1) {
                throw new ContentPackageException("Expected Common Cartridge manifest to contain exactly 1 <file> element, but got " + fileHrefs.size());
            }
            String qtiManifestHref = fileHrefs.iterator().next();
            specialResourceHrefs.add(qtiManifestHref);
            qtiManifestFile = new File(destinationDirectory, qtiManifestHref);
            
            /* Now parse actual QTI manifest and resolve item/test locations against top of bundle */
            IMSManifestHandler qtiManifestHandler = readManifestFile(qtiManifestFile, qtiManifestHref);
            testResourceHrefs = resolveHrefs(qtiManifestHandler.getResourceHrefsForTypes(TEST_TYPES), qtiManifestHref);
            itemResourceHrefs = resolveHrefs(qtiManifestHandler.getResourceHrefsForTypes(ITEM_TYPES), qtiManifestHref);
        }
        else {
            /* Expect this manifest to declare item(s) and/or test(s) */
            isCommonCartridge = false;
            qtiManifestFile = containerManifestFile;
            
            /* Extract item(s) */
            itemResourceHrefs = containerHandler.getResourceHrefsForTypes(ITEM_TYPES);
            
            /* Extract test(s). This one is slightly more complicated as we support the legacy type from
             * the old QTIEngine as a fallback */
            testResourceHrefs = containerHandler.getResourceHrefsForTypes(TEST_TYPES);
            if (testResourceHrefs.isEmpty()) {
                /* Failing that, fall back to old QTIEngine */
                testResourceHrefs = containerHandler.getResourceHrefsForTypes(OLD_QTIENGINE_TEST_TYPE);
            }
        }
        specialResourceHrefs.addAll(testResourceHrefs);
        specialResourceHrefs.addAll(itemResourceHrefs);

        /* Finished unpacking */
        this.unpacked = true;
    }
    
    private List<String> resolveHrefs(Collection<String> hrefs, String baseHref) {
        List<String> result = new ArrayList<String>();
        URI baseUri = hrefToURI(baseHref);
        for (String href : hrefs) {
            result.add(baseUri.resolve(href).toString());
        }
        return result;
    }
    
    private URI hrefToURI(String href) {
        try {
            return new URI(href);
        }
        catch (URISyntaxException e) {
            throw new ContentPackageException("HREF " + href + " is not a valid URI", e);
        }
    }
    
    private IMSManifestHandler readManifestFile(File manifestFile, String manifestLocation) {
        if (!manifestFile.exists()) {
            throw new ContentPackageException("Could not find IMS Manifest file at " + manifestLocation);
        }
        InputSource manifestInput = new InputSource(manifestFile.getAbsolutePath());
        IMSManifestHandler cpResourceFinder = new IMSManifestHandler();
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(true);
            saxParserFactory.newSAXParser().parse(manifestInput, cpResourceFinder);
        }
        catch (Exception ex) {
            throw new ContentPackageException("Could not parse IMS Manifest file at " + manifestLocation, ex);
        }
        return cpResourceFinder;
    }

    public File getDestinationDirectory() {
        return destinationDirectory;
    }

    public File getContainerManifestFile() {
        ensureUnpacked();
        return containerManifestFile;
    }
    
    public File getQTIManifestFile() {
        ensureUnpacked();
        return qtiManifestFile;
    }
    
    public boolean isCommonCartridge() {
        ensureUnpacked();
        return isCommonCartridge;
    }
    
    public File getResource(String href) {
        return new File(destinationDirectory, href);
    }
    
    public List<String> getSpecialResourceHrefs() {
        ensureUnpacked();
        return specialResourceHrefs;
    }
    
    public List<String> getItemResourceHrefs() {
        ensureUnpacked();
        return itemResourceHrefs;
    }

    public List<String> getTestResourceHrefs() {
        ensureUnpacked();
        return testResourceHrefs;
    }
    
    private void ensureUnpacked() {
        if (!unpacked) {
            throw new IllegalStateException("Package has not been unpacked");
        }
    }
    
    public static void main(String[] args) throws Exception {
        System.out.println("ContentPackage reading started.");

        // File sourceFile = new
        // File("C:/Home/jk2/asdel/QTI/qtiv2p1pd2/examples/test_package_minfiles/test_package_minfiles.zip");
        // File destDirectory = new File("C:/temp/cpg");

        File sourceFile = new File("basicQTI.zip");
        File destDirectory = new File("/tmp/content-packaging");

        if (args.length > 0)
            sourceFile = new File(args[0]);

        if (args.length > 1)
            destDirectory = new File(args[1]);

        System.out.println("Source = '" + sourceFile + "'");

        ContentPackageExtractor extractor = new ContentPackageExtractor(new FileInputStream(sourceFile), destDirectory);
        extractor.unpack(true);
        
        System.out.println("Destination = '" + extractor.getDestinationDirectory() + "'");
        System.out.println("Common Cartridge? " + extractor.isCommonCartridge());
        System.out.println("Container Manifest = '" + extractor.getContainerManifestFile() + "'");
        System.out.println("QTI Manifest = '" + extractor.getContainerManifestFile() + "'");

        for (String test : extractor.getTestResourceHrefs()) {
            System.out.println("Test: '" + test + "' unpacked to " + extractor.getResource(test));
//            System.out.print("Validating " + test + " ...");
//            XmlUtils.validate(test);
//            System.out.println(" done");
        }

        for (String item : extractor.getItemResourceHrefs()) {
            System.out.println("Item: '" + item + "' unpacked to " + extractor.getResource(item));
//            System.out.print("Validating " + item + " ...");
//            XmlUtils.validate(item);
//            System.out.println(" done");
        }

        System.out.println("ContentPackage reading finished.");
    }
}
