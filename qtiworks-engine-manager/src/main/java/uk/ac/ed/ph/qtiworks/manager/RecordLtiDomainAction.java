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

import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiDomain;
import uk.ac.ed.ph.qtiworks.services.base.ServiceUtilities;
import uk.ac.ed.ph.qtiworks.services.dao.LtiDomainDao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Records a new LTI domain, generating a shared secret for it.
 *
 * @author David McKain
 */
public final class RecordLtiDomainAction extends ManagerAction {

	private static final Logger logger = LoggerFactory.getLogger(RecordLtiDomainAction.class);

	@Override
	public String getActionSummary() {
		return "Records a new LTI domain and outputs a shared secret for it";
	}

    @Override
    public String getActionParameterSummary() {
        return "<tcDomainName>";
    }

    @Override
    public String validateParameters(final List<String> parameters) {
        if (parameters.size()!=1) {
            return "Required parameter: <tool consumer domain name>";
        }
        return null;
    }

	@Override
	public void run(final ApplicationContext applicationContext, final List<String> parameters) {
	    final LtiDomainDao ltiDomainDao = applicationContext.getBean(LtiDomainDao.class);

	    final String tcDomainName = parameters.get(0);
	    final String sharedSecret = ServiceUtilities.createRandomAlphanumericToken(DomainConstants.LTI_SECRET_LENGTH);
	    final LtiDomain ltiDomain = new LtiDomain();
	    ltiDomain.setConsumerKey(tcDomainName);
	    ltiDomain.setConsumerSecret(sharedSecret);

	    ltiDomainDao.persist(ltiDomain);
	    logger.info("Stored new LTI domain. Consumer key is {}. Shared secret is {}", tcDomainName, sharedSecret);
    }
}
