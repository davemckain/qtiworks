/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2012-2013, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.qtiworks.examples;

import uk.ac.ed.ph.jqtiplus.SimpleJqtiFacade;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.net.URI;

/**
 * Example of invoking validation.
 * <p>
 * (Not documented very well yet. Sorry)
 *
 * @author David McKain
 */
public final class ValidationExample {

    public static void main(final String[] args) throws Exception {
        final ResourceLocator inputResourceLocator = new ClassPathResourceLocator();
        final URI inputUri = URI.create("classpath:/variableRefs.xml");

        System.out.println("Reading and validating");
        final SimpleJqtiFacade simpleJqtiFacade = new SimpleJqtiFacade();
        final ItemValidationResult result = simpleJqtiFacade.loadResolveAndValidateItem(inputResourceLocator, inputUri);
        System.out.println("Validation result: " + ObjectDumper.dumpObject(result, DumpMode.DEEP));
    }
}
