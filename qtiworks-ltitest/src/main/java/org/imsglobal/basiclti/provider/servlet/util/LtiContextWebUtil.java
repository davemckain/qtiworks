package org.imsglobal.basiclti.provider.servlet.util;

/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2011 IMS GLobal Learning Consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. 
 *
 **********************************************************************************/
import javax.servlet.http.HttpSession;

import org.imsglobal.basiclti.provider.api.LtiContext;

/**
 * Helper class that fetches the {@link LtiContext} from the session.
 * @author Roland Groen (roland@edia.nl)
 *
 */
public class LtiContextWebUtil {
	/**
	 * Gets the {@link LtiContext}, if set.
	 * @param session
	 * @return the {@link LtiContext} available in the sessio, null if not set.
	 */
	public static LtiContext getBasicLtiContext(HttpSession session) {
		return (LtiContext) session.getAttribute(getSessionAttributeName());
	}

	protected static String getSessionAttributeName() {
	    return "session-" + LtiContext.class.getName();
    }

	/**
	 * Sets the {@link LtiContext} in the session, set to null to delete
	 * @param session
	 * @param ltiContext
	 */
	public static void setBasicLtiContext(HttpSession session, LtiContext ltiContext) {
		if (ltiContext != null) {
			session.setAttribute(getSessionAttributeName(), ltiContext);
		} else {
			session.removeAttribute(getSessionAttributeName());
		}
	}
}
