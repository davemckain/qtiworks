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
 * Example of invoking validation on a number of example assessmentItems.
 * This demonstrates the different kinds of validation outcomes you can expect to find.
 * <p>
 * The XML files for these can be found under <code>src/main/resources/validation</code>
 *
 * <h3>How to run</h3>
 *
 * You can run this via Maven as follows:
 * <pre>
 * mvn exec:java -Dexec.mainClass=uk.ac.ed.ph.qtiworks.examples.ValidationExample
 * </pre>
 * You should also be able to run this inside your favourite IDE if you have loaded the QTIWorks
 * source code into it.
 *
 * @author David McKain
 */
public final class ValidationExample {

    public static void main(final String[] args) throws Exception {
        runExample("classpath:/validation/variableRefs.xml"); /* See src/main/resources/validation/variableRefs.xml */
        runExample("classpath:/validation/notSchemaValid.xml"); /* Etc... */
        runExample("classpath:/validation/modelBuildFailure.xml");
        runExample("classpath:/validation/additionalValidationFailure.xml");
    }

    private static void runExample(final String location) {
        final ResourceLocator inputResourceLocator = new ClassPathResourceLocator();
        final URI inputUri = URI.create(location);

        System.out.println("Reading and validating assessment at " + location);
        final SimpleJqtiFacade simpleJqtiFacade = new SimpleJqtiFacade();
        final ItemValidationResult result = simpleJqtiFacade.loadResolveAndValidateItem(inputResourceLocator, inputUri);
        System.out.println("Validation result: " + ObjectDumper.dumpObject(result, DumpMode.DEEP));

    }
}
