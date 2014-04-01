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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.qtiworks.manager;

import uk.ac.ed.ph.qtiworks.config.QtiWorksProfiles;
import uk.ac.ed.ph.qtiworks.manager.services.SampleResourceImporter;
import uk.ac.ed.ph.qtiworks.services.FilespaceManager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Bootstraps the database schema and imports the sample items
 *
 * @author David McKain
 */
public final class BootstrapAction extends ManagerAction {

    private static final Logger logger = LoggerFactory.getLogger(BootstrapAction.class);

    @Override
    public String[] getActionSummary() {
        return new String[] {
        		"Bootstraps the QTIWorks database and imports sample assessments.",
        		"WARNING! Any existing data will be deleted!"
        };
    }

    @Override
    public String getSpringProfileName() {
        return QtiWorksProfiles.BOOTSTRAP;
    }

    @Override
    public void beforeApplicationContextInit() {
        logger.warn("QTIWorks database is being bootstrapped. Any existing data will be deleted!!!");
        logger.warn("Make sure you have created the QTIWorks database already. Refer to the documentation for help");
    }

    @Override
    public void run(final ApplicationContext applicationContext, final List<String> parameters) {
        /* Delete filesystem data too */
        logger.info("Deleting any existing user data from filesystem");
        final FilespaceManager filespaceManager = applicationContext.getBean(FilespaceManager.class);
        filespaceManager.deleteAllUserData();

        logger.info("Importing QTI samples");
        final SampleResourceImporter sampleResourceImporter = applicationContext.getBean(SampleResourceImporter.class);
        sampleResourceImporter.updateQtiSamples();

        logger.info("QTIWorks database bootstrap has completed successfully");
    }
}
