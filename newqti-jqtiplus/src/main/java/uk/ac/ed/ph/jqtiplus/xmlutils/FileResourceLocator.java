/* $Id: FileResourceLocator.java 2720 2011-06-21 14:15:26Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Loads resources having a <tt>file:</tt> URI
 *
 * @author  David McKain
 * @version $Revision: 2720 $
 */
public class FileResourceLocator implements ResourceLocator {
    
    private static final long serialVersionUID = -6023672582874907755L;
    
    private static final Logger logger = LoggerFactory.getLogger(FileResourceLocator.class);
    
    public InputStream findResource(URI systemIdUri) {
        if ("file".equals(systemIdUri.getScheme())) {
            try {
                return new FileInputStream(new File(systemIdUri));
            }
            catch (Exception e) {
                logger.warn("File " + systemIdUri + " does not exist");
                return null;
            }
        }
        return null;
    }
}
