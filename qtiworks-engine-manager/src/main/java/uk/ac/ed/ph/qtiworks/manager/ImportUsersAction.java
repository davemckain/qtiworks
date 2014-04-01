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

import uk.ac.ed.ph.qtiworks.domain.entities.SystemUser;
import uk.ac.ed.ph.qtiworks.domain.entities.UserRole;
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
 * Imports users from a CSV file
 *
 * @author David McKain
 */
public final class ImportUsersAction extends ManagerAction {

    private static final Logger logger = LoggerFactory.getLogger(ImportUsersAction.class);

    private File userImportCsv;

    @Override
    public String[] getActionSummary() {
    	return new String[] {
    			"Imports standalone instructor users into the system using data from a CSV file.",
    			"CSV format: loginName,firstName,lastName,emailAddress,password[,sysAdmin?(t|f)]",
    			"sysAdmin field is optional and should be 't' for true, otherwise treated as false (default)."
    	};
    }

    @Override
    public String getActionParameterSummary() {
        return "<importFile.csv>";
    }

    @Override
    public String validateParameters(final List<String> parameters) {
        if (parameters.size()!=1) {
            return "Required parameter: path to user import CSV";
        }
        final String userImportPath = parameters.get(0);
        userImportCsv = new File(userImportPath);
        if (!userImportCsv.isFile()) {
            return "Path " + userImportPath + " not found";
        }
        return null;
    }

    @Override
    public void run(final ApplicationContext applicationContext, final List<String> parameters)
            throws UnsupportedEncodingException, FileNotFoundException {
        logger.info("Importing users from {}", userImportCsv);

        final BufferedReader importReader = new BufferedReader(new InputStreamReader(new FileInputStream(userImportCsv), "UTF-8"));
        String line;
        String[] fields;
        final ManagerServices managerServices = applicationContext.getBean(ManagerServices.class);
        int usersCreatedCount = 0;
        try {
            /* (Cheapo CSV parse) */
            while ((line = importReader.readLine())!=null) {
                line = line.trim();
                if (line.length()==0 || line.startsWith("#")) {
                    /* Skip empty lines or comments (# ...) */
                    continue;
                }
                fields = line.split(",\\s*", -1);
                if (handleUserLine(managerServices, fields)) {
                    ++usersCreatedCount;
                }
            }
        }
        catch (final Exception e) {
            logger.error("Unexpected Exception reading in " + userImportCsv, e);
        }
        finally {
            IOUtils.closeQuietly(importReader);
        }
        logger.info("Created {} new user(s)", usersCreatedCount);
    }

    private boolean handleUserLine(final ManagerServices managerServices, final String[] fields) {
        if (fields.length<5) {
            logger.warn("Expected 5 or 6 fields per line: ignoring " + Arrays.toString(fields));
            return false;
        }
        final UserRole userRole = UserRole.INSTRUCTOR;
        final String loginName = fields[0];
        final String firstName = fields[1];
        final String lastName = fields[2];
        final String emailAddress = fields[3];
        final String password = fields[4];
        final boolean sysAdmin = (fields.length==6 && "t".equals(fields[5]));

        final SystemUser created = managerServices.maybeCreateSystemUser(userRole,
                loginName, firstName, lastName, emailAddress, password, sysAdmin);
        return created!=null;
    }

}
