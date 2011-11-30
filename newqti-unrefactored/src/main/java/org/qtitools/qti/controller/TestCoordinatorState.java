/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

 *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
 */

package org.qtitools.qti.controller;

import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This encapsulates the current test state that was previously mixed within the
 * {@link TestCoordinator} itself. Unlike {@link TestCoordinator}, this Object is
 * {@link Serializable} so can be stored, for example, within an HTTP session, which
 * is how MathAssessEngine currently works.
 * 
 * @author  David McKain
 * @version $Revision: 2775 $
 */
public final class TestCoordinatorState implements Serializable {

    private static final long serialVersionUID = 2984470592691047799L;

    private final AssessmentTestController testController;
    private final Map<AssessmentItemRef, Map<String, Value>> testPartItems;
    private String cachedRenderedContent; 
    private String flash;

    public TestCoordinatorState(AssessmentTestController testController) {
        this.testController = testController;
        this.testPartItems = new HashMap<AssessmentItemRef, Map<String, Value>>();
    }
    
    
    public AssessmentTestController getTestController() {
        return testController;
    }
    
    
    public Map<AssessmentItemRef, Map<String, Value>> getTestPartItems() {
        return testPartItems;
    }

    
    public String getCachedRenderedContent() {
        return cachedRenderedContent;
    }

    public void setCachedRenderedContent(String cachedRenderedContent) {
        this.cachedRenderedContent = cachedRenderedContent;
    }


    public String getFlash() {
        return flash;
    }

    public void setFlash(String flash) {
        this.flash = flash;
    }
}
