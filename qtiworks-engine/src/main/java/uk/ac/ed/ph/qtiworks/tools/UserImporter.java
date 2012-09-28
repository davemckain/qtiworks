/* Copyright (c) 2012, University of Edinburgh.
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
package uk.ac.ed.ph.qtiworks.tools;

import uk.ac.ed.ph.qtiworks.config.BaseServicesConfiguration;
import uk.ac.ed.ph.qtiworks.config.JpaProductionConfiguration;
import uk.ac.ed.ph.qtiworks.config.ServicesConfiguration;
import uk.ac.ed.ph.qtiworks.tools.services.BootstrapServices;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Helper to import a list of users into the system
 *
 * @author David McKain
 */
public final class UserImporter {

    private static final Logger logger = LoggerFactory.getLogger(UserImporter.class);

    public static void main(final String[] args) throws Exception {
        if (args.length!=1) {
            System.err.println("Required argument: location of CSV file containing user data");
            System.exit(1);
        }

        final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(JpaProductionConfiguration.class, BaseServicesConfiguration.class, ServicesConfiguration.class);
        ctx.refresh();
        final BootstrapServices bootstrapServices = ctx.getBean(BootstrapServices.class);

        final String importFile = args[0];
        final BufferedReader importReader = new BufferedReader(new InputStreamReader(new FileInputStream(importFile), "UTF-8"));
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
                handleUserLine(bootstrapServices, fields);
            }
        }
        catch (final Exception e) {
            logger.error("Unexpected Exception reading in " + importFile, e);
        }
        finally {
            IOUtils.closeQuietly(importReader);
        }
        ctx.close();
    }

    public static void handleUserLine(final BootstrapServices bootstrapServices, final String[] fields) {
        if (fields.length!=6) {
            logger.warn("Expected 6 fields per line: ignoring " + Arrays.toString(fields));
            return;
        }
        final String loginName = fields[0];
        final String firstName = fields[1];
        final String lastName = fields[2];
        final String emailAddress = fields[3];
        final boolean sysAdmin = "t".equals(fields[4]);
        final String password = fields[5];

        bootstrapServices.createInstructorUser(loginName, firstName, lastName, emailAddress, sysAdmin, password);
    }
}
