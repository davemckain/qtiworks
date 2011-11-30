/* $Id: ChainedResourceLocator.java 2754 2011-07-14 16:14:04Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils;

import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FIXME: Should this fail if we can't load the required resource?
 * 
 * @author  David McKain
 * @version $Revision: 2754 $
 */
public final class ChainedResourceLocator implements ResourceLocator {
    
    private static final long serialVersionUID = -8464840262292852214L;
    
    private static final Logger logger = LoggerFactory.getLogger(ChainedResourceLocator.class);
    
    private final ResourceLocator[] resourceLocators;
    
    public ChainedResourceLocator(final ResourceLocator... resourceLocators) {
        this.resourceLocators = resourceLocators;
    }
    
    public InputStream findResource(URI systemIdUri) {
        for (ResourceLocator resourceLocator : resourceLocators) {
            logger.debug("Trying {} using ResourceLocator {}", systemIdUri, resourceLocator);
            InputStream result = resourceLocator.findResource(systemIdUri);
            if (result!=null) {
                logger.debug("Success with ResourceLocator {}", resourceLocator);
                return result;
            }
            logger.debug("No success with ResourceLocator {}", resourceLocator);
        }
        return null;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(resourceLocators=" + Arrays.toString(resourceLocators)
            + ")";
    }
}
