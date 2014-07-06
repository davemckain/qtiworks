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

import uk.ac.ed.ph.qtiworks.domain.entities.LtiDomain;
import uk.ac.ed.ph.qtiworks.manager.services.ManagerServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Imports {@link LtiDomain} data from a CSV file
 *
 * @author David McKain
 */
public final class ImportLtiDomainsAction extends ManagerAction {

    private static final Logger logger = LoggerFactory.getLogger(ImportLtiDomainsAction.class);

    private File ltiDomainsCsv;

    @Override
    public String[] getActionSummary() {
        return new String[] {
                "Imports LTI domain data into the system using data from a CSV file.",
                "CSV format: consumerKey,sharedSecret",
                "(We suggest using the domain name of the tool consumer as the consumerKey)"
        };
    }

    @Override
    public String getActionParameterSummary() {
        return "<ltiDomainsCsv.csv>";
    }

    @Override
    public String validateParameters(final List<String> parameters) {
        if (parameters.size()!=1) {
            return "Required parameter: path to LTI domain CSV";
        }
        final String ltiDomainsImportPath = parameters.get(0);
        ltiDomainsCsv = new File(ltiDomainsImportPath);
        if (!ltiDomainsCsv.isFile()) {
            return "Path " + ltiDomainsImportPath + " not found";
        }
        return null;
    }

    @Override
    public void run(final ApplicationContext applicationContext, final List<String> parameters)
            throws UnsupportedEncodingException, FileNotFoundException {
        logger.info("Importing LTI domain data from {}", ltiDomainsCsv);
        final ManagerServices managerServices = applicationContext.getBean(ManagerServices.class);
        final BufferedReader importReader = new BufferedReader(new InputStreamReader(new FileInputStream(ltiDomainsCsv), "UTF-8"));
        String line;
        String[] fields;
        try {
            /* (Cheapo CSV parse) */
            while ((line = importReader.readLine())!=null) {
                line = line.trim();
                if (line.length()==0 || line.startsWith("#")) {
                    /* Skip empty lines or comments (# ...) */
                    continue;
                }
                fields = line.split(",\\s*", -1);
                handleLtiDomain(managerServices, fields);
            }
        }
        catch (final Exception e) {
            logger.error("Unexpected Exception reading in " + ltiDomainsCsv, e);
        }
        finally {
            IOUtils.closeQuietly(importReader);
        }
    }

    private void handleLtiDomain(final ManagerServices managerServices, final String[] fields) {
        if (fields.length!=2) {
            logger.warn("Expected 2 fields per line: ignoring " + Arrays.toString(fields));
        }
        final String consumerKey = fields[0];
        final String sharedSecret = fields[1];
        managerServices.createOrUpdateLtiDomain(consumerKey, sharedSecret);
    }
}
