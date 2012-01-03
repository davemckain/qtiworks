/* $Id: ClassPathResourceLocator.java 2754 2011-07-14 16:14:04Z davemckain $
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
 * Implementation of {@link ResourceLocator} that looks for resources within
 * a specified base directory in the ClassPath, using a pseudo-URL scheme
 * <tt>classpath:/path/to/resource.txt</tt>
 * <p>
 * For example, <tt>classpath:/hello.txt</tt> looks in the ClassPath for
 * <tt>[basePath]/hello.txt</tt>
 * <p>
 * Note that the '/' after the ':' is important here. I have enforced this to
 * make sure the URI resolution works nicely.
 * 
 * @author  David McKain
 * @version $Revision: 2754 $
 */
public final class ClassPathResourceLocator implements ResourceLocator {

    private static final long serialVersionUID = 6287482860619405237L;

    private static final Logger logger = LoggerFactory.getLogger(ClassPathResourceLocator.class);

    public static final String CLASSPATH_SCHEME_NAME = "classpath";
    
    /** basePath to search in. null is treated as blank */
    private String basePath;

    // -------------------------------------------
    
    public ClassPathResourceLocator() {
        this(null);
    }
    
    public ClassPathResourceLocator(String basePath) {
        this.basePath = basePath;
    }
    
    public String getBasePath() {
        return basePath;
    }
    
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    // -------------------------------------------

    @Override
    public InputStream findResource(final URI systemIdUri) {
        String scheme = systemIdUri.getScheme();
        InputStream result = null;
        if (CLASSPATH_SCHEME_NAME.equals(scheme)) {
        	String systemIdAfterScheme = systemIdUri.toString().substring(CLASSPATH_SCHEME_NAME.length());
        	if (systemIdAfterScheme.startsWith(":/")) {
                String resultingPath = basePath!=null ? basePath + systemIdAfterScheme.substring(1) : systemIdAfterScheme.substring(2);
                result = loadResource(systemIdUri, resultingPath);
        	}
        	else {
        		logger.warn("ClassPath URI must be of the form " + CLASSPATH_SCHEME_NAME + ":/path");
        	}
        }
        return result;
    }

    private InputStream loadResource(final URI systemIdUri, final String resourcePath) {
        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (resourceStream!=null) {
            logger.debug("Successful locate of ClassPath resource with URI {}  in ClassPath at {}", systemIdUri, resourcePath);
        }
        else {
            logger.warn("Failed to locate ClassPath resource with URI {} in ClassPath at {}", systemIdUri, resourcePath);
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
