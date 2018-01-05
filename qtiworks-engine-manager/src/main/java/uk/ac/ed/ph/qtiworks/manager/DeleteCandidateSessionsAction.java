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

import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.manager.services.ManagerServices;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Deletes all {@link CandidateSession}s launched on the specifieid
 * {@link Delivery} from the system.
 *
 * @author David McKain
 */
public final class DeleteCandidateSessionsAction extends ManagerAction {

    private static final Logger logger = LoggerFactory.getLogger(DeleteCandidateSessionsAction.class);

    @Override
    public String[] getActionSummary() {
        return new String[] { "Deletes the CandidateSessions launched on the Deliveries having the given did(s)" };
    }

    @Override
    public String getActionParameterSummary() {
        return "<did> ...";
    }

    @Override
    public String validateParameters(final List<String> parameters) {
        if (parameters.isEmpty()) {
            return "Required parameters: <did> ...";
        }
        return null;
    }

    @Override
    public void run(final ApplicationContext applicationContext, final List<String> parameters) throws Exception {
        final ManagerServices managerServices = applicationContext.getBean(ManagerServices.class);
        int deletedCount = 0;
        for (final String parameter : parameters) {
            final Long did = Long.valueOf(parameter);
            final int deleted = managerServices.deleteCandidateSessions(did);
            if (deleted > 0) {
                deletedCount += deleted;
            }
        }
        logger.info("Deleted {} CandidateSession(s) from the system", deletedCount);
    }
}
