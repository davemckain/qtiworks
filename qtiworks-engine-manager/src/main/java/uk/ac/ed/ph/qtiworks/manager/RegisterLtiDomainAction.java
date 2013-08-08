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
import uk.ac.ed.ph.qtiworks.manager.services.ManagerServices;
import uk.ac.ed.ph.qtiworks.services.ServiceUtilities;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Records a new LTI domain, generating a shared secret for it.
 *
 * @author David McKain
 */
public final class RegisterLtiDomainAction extends ManagerAction {

	public static final int LTI_SHARED_SECRET_MIN_LENGTH = 8;

	private static final Logger logger = LoggerFactory.getLogger(RegisterLtiDomainAction.class);

	@Override
	public String getActionSummary() {
		return "Registers a new LTI domain";
	}

    @Override
    public String getActionParameterSummary() {
        return "<tcDomainName> (<sharedSecret>)";
    }

    @Override
    public String validateParameters(final List<String> parameters) {
    	final int parameterCount = parameters.size();
        if (parameterCount==0 || parameterCount>2) {
            return "Parameters: <tool consumer domain name> (<shared secret>)";
        }
        return null;
    }

	@Override
	public void run(final ApplicationContext applicationContext, final List<String> parameters) {
	    final ManagerServices managerServices = applicationContext.getBean(ManagerServices.class);

	    final String tcDomainName = parameters.get(0);
	    final String sharedSecret;
	    if (parameters.size()==2) {
	    	sharedSecret = parameters.get(1);
	    }
	    else {
	    	sharedSecret = ServiceUtilities.createRandomAlphanumericToken(DomainConstants.LTI_SECRET_LENGTH);
	    }
	    if (sharedSecret.length()<LTI_SHARED_SECRET_MIN_LENGTH || sharedSecret.length()>DomainConstants.LTI_SECRET_LENGTH) {
	    	logger.error("Shared secret must be between {} and {} characters", LTI_SHARED_SECRET_MIN_LENGTH, DomainConstants.LTI_SECRET_LENGTH);
	    	return;
	    }
	    if (managerServices.createOrUpdateLtiDomain(tcDomainName, sharedSecret)) {
	    	logger.info("Registerd new LTI domain. Consumer key is {}. Shared secret is {}", tcDomainName, sharedSecret);
	    }
    }
}
