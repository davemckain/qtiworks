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

import java.util.List;

import org.springframework.context.ApplicationContext;

/**
 * Partial implementations of each manager "action" that we support.
 *
 * @author David McKain
 */
public abstract class ManagerAction {

	/**
	 * Return details about any required parameters
	 */
	public String getActionParameterSummary() {
		/* None usually required */
		return "";
	}

	/**
	 * Return a one line summary of usage for this action
	 */
	public abstract String getActionSummary();

	/**
	 * Returns name of the Spring <code>@Profile</code> to use.
	 */
	public String getSpringProfileName() {
		return QtiWorksProfiles.MANAGER;
	}

	/**
	 * Perform any action-specific validation on the user-provided parameters.
	 * Return null on success, otherwise an error message.
	 */
	public String validateParameters(@SuppressWarnings("unused") final List<String> parameters) {
		return null;
	}

	/**
	 * Override if you want to say or do something before the Spring
	 * ApplicationContext is set up.
	 */
	public void beforeApplicationContextInit() {
		/* Do nothing */
	}

	/**
	 * Put the action logic in here.
	 */
	public abstract void run(ApplicationContext applicationContext, List<String> parameters) throws Exception;

}
