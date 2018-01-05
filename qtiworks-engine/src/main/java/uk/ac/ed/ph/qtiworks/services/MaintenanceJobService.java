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
package uk.ac.ed.ph.qtiworks.services;

import uk.ac.ed.ph.qtiworks.domain.DomainConstants;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Houses various maintenance jobs.
 * <p>
 * These are invoked in the running engine via by the {@link ScheduledService},
 * and can also be invoked offline via the QTIWorks Engine Manager.
 * <p>
 * This is NO authorisation at this level.
 *
 * @author David McKain
 */
@Service
public class MaintenanceJobService {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceJobService.class);

    @Resource
    private DataDeletionService dataDeletionService;

    @Resource
    private FilespaceManager filespaceManager;

    @Resource
    private LtiOutcomeService ltiOutcomeService;

    //-------------------------------------------------

    /** Invokes routine maintenance jobs */
    public void runMaintenanceJobs() {
        logger.trace("runMaintenanceJobs() invoked");
        final long beforeTimestamp = System.currentTimeMillis();

        purgeTransientData(beforeTimestamp);
        purgeOldNonces(beforeTimestamp);
        dataDeletionService.purgeOrphanedLtiCandidateUsers();
        purgeEmptyStoreDirectories();

        final long afterTimestamp = System.currentTimeMillis();
        final long duration = afterTimestamp - beforeTimestamp;
        logger.debug("runMaintenanceJobs() completed in {}ms", duration);
    }

    /**
     * Purges all anonymous users and transient deliveries that were created more than
     * {@link DomainConstants#TRANSIENT_DATA_LIFETIME} milliseconds ago. All associated data is removed.
     */
    private void purgeTransientData(final long currentTimestamp) {
        final Date creationTimeThreshold = new Date(currentTimestamp - DomainConstants.TRANSIENT_DATA_LIFETIME);
        dataDeletionService.purgeTransientData(creationTimeThreshold);
    }

    /**
     * Purges OAuth nonces for LTI launches that were created more than
     * {@link DomainConstants#OAUTH_TIMESTAMP_MAX_AGE} milliseconds ago.
     */
    private void purgeOldNonces(final long currentTimestamp) {
        final Date nonceThreshold = new Date(currentTimestamp - DomainConstants.OAUTH_TIMESTAMP_MAX_AGE);
        dataDeletionService.purgeOldNonces(nonceThreshold);
    }

    /**
     * Purges empty directories in the QTIWorks filestore
     */
    private void purgeEmptyStoreDirectories() {
        final int deletedCount = filespaceManager.purgeEmptyStoreDirectories();
        if (deletedCount > 0) {
            logger.info("Purged {} empty filestore directories", deletedCount);
        }
    }
}
