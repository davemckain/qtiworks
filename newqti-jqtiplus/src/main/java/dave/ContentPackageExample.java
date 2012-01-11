/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.jqtiplus.utils.ContentPackageExtractor;

import java.io.File;

/**
 * Content Package extraction dev/test
 *
 * @author David McKain
 */
public class ContentPackageExample {
    
    public static void main(String[] args) throws Exception {
        File packageBaseDirectory = new File("src/main/runtime/Aardvark-cannon");
        ContentPackageExtractor extractor = new ContentPackageExtractor(packageBaseDirectory);
        extractor.parse();
    }
}
