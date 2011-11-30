/* $Id: ClassPathHTTPResourceLocator.java 2754 2011-07-14 16:14:04Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils;

import java.io.InputStream;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link ResourceLocator} that looks for HTTP resources
 * in the ClassPath using a simple naming mechanism.
 * <p>
 * This is used to load "provided" resources, such as schemas, DTDs, standard
 * resource processing templates etc.
 * 
 * @author  David McKain
 * @version $Revision: 2754 $
 */
public final class ClassPathHTTPResourceLocator implements ResourceLocator {

    private static final long serialVersionUID = 6287482860619405237L;

    private static final Logger logger = LoggerFactory.getLogger(ClassPathHTTPResourceLocator.class);

    public static final String DEFAULT_BASE_PATH = "uk/ac/ed/ph/jqtiplus";
    
    private String basePath;
    
    public ClassPathHTTPResourceLocator() {
        this(DEFAULT_BASE_PATH);
    }
    
    public ClassPathHTTPResourceLocator(String basePath) {
        this.basePath = basePath;
    }
    

    public String getBasePath() {
        return basePath;
    }
    
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
    
    // -------------------------------------------

    public InputStream findResource(final URI systemIdUri) {
        String scheme = systemIdUri.getScheme();
        if ("http".equals(scheme)) {
            String relativeSystemId = systemIdUri.toString().substring("http://".length());
            String resultingPath = basePath + "/" + relativeSystemId;
            return loadResource(systemIdUri, resultingPath);
        }
        return null;
    }

    private InputStream loadResource(final URI systemIdUri, final String resourcePath) {
        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (resourceStream!=null) {
            logger.debug("Successful locate of HTTP resource with URI {}  in ClassPath at {}", systemIdUri, resourcePath);
        }
        else {
            logger.warn("Failed to locate HTTP resource with URI {} in ClassPath at {}", systemIdUri, resourcePath);
        }
        return resourceStream;
    }
    
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(basePath=" + basePath
                + ")";
    }
}
