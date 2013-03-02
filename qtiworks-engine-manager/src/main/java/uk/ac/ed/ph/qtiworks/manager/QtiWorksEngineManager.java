/* Copyright (c) 2012-2013, University of Edinburgh.
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
package uk.ac.ed.ph.qtiworks.manager;

import uk.ac.ed.ph.qtiworks.config.JpaBootstrapConfiguration;
import uk.ac.ed.ph.qtiworks.config.JpaProductionConfiguration;
import uk.ac.ed.ph.qtiworks.config.PropertiesConfiguration;
import uk.ac.ed.ph.qtiworks.config.QtiWorksApplicationContextHelper;
import uk.ac.ed.ph.qtiworks.config.ServicesConfiguration;
import uk.ac.ed.ph.qtiworks.manager.config.ManagerConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Simple offline manager for performing various actions on the QTIWorks engine database,
 * including the initial data bootstrap.
 *
 * @author David McKain
 */
public final class QtiWorksEngineManager {

    private static final Logger logger = LoggerFactory.getLogger(QtiWorksEngineManager.class);

    public static final String DEFAULT_DEPLOYMENT_PROPERTIES_NAME = "qtiworks-deployment.properties";

    private static final Map<String, ManagerAction> actionMap;
    static {
    	actionMap = new LinkedHashMap<String, ManagerAction>();
    	actionMap.put("bootstrap", new BootstrapAction());
    	actionMap.put("rebuildSchema", new RebuildSchemaAction());
    	actionMap.put("importUsers", new ImportUsersAction());
    	actionMap.put("reimportSamples", new ReimportSamplesAction());
    	actionMap.put("updateSamples", new UpdateSamplesAction());
    	actionMap.put("deleteUsers", new DeleteUsersAction());
    	actionMap.put("resetUsers", new ResetUsersAction());
    	actionMap.put("purgeAnonymousData", new PurgeAnonymousDataAction());
    	actionMap.put("adhoc", new AdhocAction());
    }

    private Resource deploymentPropertiesResource;
    private String actionName;
    private final List<String> actionParameters;
    private ManagerAction action;

    public QtiWorksEngineManager() {
    	this.deploymentPropertiesResource = null;
    	this.actionName = null;
    	this.actionParameters = new ArrayList<String>();
    }

    public String parseArguments(final String[] args) {
    	/* Parse arguments to work out action, config and parameters */
    	boolean expectingDeploymentConfig = false;
    	String deploymentPropertiesResourceUri = DEFAULT_DEPLOYMENT_PROPERTIES_NAME;
    	for (final String arg : args) {
    		if (expectingDeploymentConfig) {
    			deploymentPropertiesResourceUri = arg;
    			expectingDeploymentConfig = false;
    		}
    		else if ("-config".equals(arg)) {
    			expectingDeploymentConfig = true;
    		}
    		else if (actionName==null) {
    			actionName = arg;
    		}
    		else {
    			actionParameters.add(arg);
    		}
    	}
    	logger.debug("Parsed arguments and got: deploymentPropertiesResourceUri={}, actionName={}, actionParameters={}",
    			new Object[] { deploymentPropertiesResourceUri, actionName, actionParameters });

    	/* Check config location */
    	deploymentPropertiesResource = extractDeploymentPropertiesResource(deploymentPropertiesResourceUri);
    	if (deploymentPropertiesResource==null) {
    		return "Could not resolve QTIWorks deployment properties specified by " + deploymentPropertiesResourceUri;
    	}

    	/* Validate action */
    	if (actionName==null) {
    		return "No action selected";
    	}
    	action = actionMap.get(actionName);
    	if (action==null) {
    		return "Unknown action '" + actionName + "'";
    	}

    	/* Perform action-specific validation on arguments */
    	final String error = action.validateParameters(actionParameters);
    	if (error!=null) {
    		return error;
    	}

    	/* Success */
    	return null;
    }

    public void performAction() {
        /* Choose appropriate Spring profile */
        final String profileName = action.getSpringProfileName();

        /* Let action do any pre-setup work */
        action.beforeApplicationContextInit();

        /* Initialise ApplicationConetext */
        logger.debug("Setting up Spring ApplicationContext using profile '{}'", profileName);
        final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.getEnvironment().setActiveProfiles(profileName);
        QtiWorksApplicationContextHelper.registerConfigPropertySources(applicationContext, deploymentPropertiesResource);
        applicationContext.register(
        		PropertiesConfiguration.class,
        		JpaProductionConfiguration.class,
        		JpaBootstrapConfiguration.class,
        		ServicesConfiguration.class,
        		ManagerConfiguration.class
        );
        applicationContext.refresh();

        /* Now let action class do its work*/
        try {
        	action.run(applicationContext, actionParameters);
        }
        catch (final Exception e) {
        	logger.warn("Unexpected Exception performing action", e);
        }
        finally {
            applicationContext.close();
        }
    }

    private Resource extractDeploymentPropertiesResource(final String path) {
        /* First check if we were passed a file path */
        File tryFile = new File(path);
        if (tryFile.isFile()) {
        	logger.debug("Path {} successfully resolved to a file", path);
        	return new FileSystemResource(tryFile);
        }

        /* If not a file path, see if it's a File relative to the current directory */
        tryFile = new File(System.getProperty("user.dir"), path);
        if (tryFile.isFile()) {
        	logger.debug("Path {} successfully resolved to relative file {}", path, tryFile.getAbsolutePath());
        	return new FileSystemResource(tryFile);
        }

        /* See if the resource exists in the ClassPath */
        final ClassPathResource classPathResource = new ClassPathResource(path);
        if (classPathResource.exists()) {
        	logger.debug("Path {} successfully found in ClassPath", path);
        	return classPathResource;
        }

        /* No luck then, Ted? */
        return null;
    }

    public static void printUsage() {
		final String separator = System.getProperty("line.separator");
		System.out.println("QTIWorks Engine Manager" + separator + separator
				+ "Specify final required action as final a command final line argument,"
				+ separator
				+ "plus any further parameters required by chosen action."
				+ separator + separator
				+ "Manager will load your QTIWorks deployment properties from a file "
				+ separator
				+ DEFAULT_DEPLOYMENT_PROPERTIES_NAME
				+ separator
				+ "in the current directory, use -config <path> to specify an"
				+ "alternate location."
				+ separator + separator
				+ "Avilable actions are:"
				+ separator);
		for (final Entry<String, ManagerAction> actionEntry : actionMap.entrySet()) {
			final String actionKey = actionEntry.getKey();
			final ManagerAction action = actionEntry.getValue();
			System.out.println(actionKey
					+ " "
					+ action.getActionParameterSummary()
					+ separator
					+ "    "
					+ actionEntry.getValue().getActionSummary()
					+ separator);
		}
    }

    public static void main(final String[] args) {
    	if (args.length==0) {
    		/* No args provided, so print usage summary and exit */
    		printUsage();
    		System.exit(1);
    	}
    	final QtiWorksEngineManager qtiWorksEngineManager = new QtiWorksEngineManager();
    	final String errorMessage = qtiWorksEngineManager.parseArguments(args);
    	if (errorMessage!=null) {
    		System.err.println(errorMessage);
    		printUsage();
    		System.exit(1);
    	}
    	qtiWorksEngineManager.performAction();
    }

}
