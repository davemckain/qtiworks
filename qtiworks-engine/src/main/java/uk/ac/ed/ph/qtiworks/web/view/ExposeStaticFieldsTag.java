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
package uk.ac.ed.ph.qtiworks.web.view;

import uk.ac.ed.ph.qtiworks.EngineException;
import uk.ac.ed.ph.qtiworks.web.WebUtilities;


import java.util.Map;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * Exposes the static fields of the Class having the given name into the JSP context
 * using the given target attribute name.
 * <p>
 * NOTE: This is does work if there is no attribute with the given target name already
 * set, allowing the tag to be used multiple times in one JSP without wasting time recalculating
 * details.
 *
 * @author David McKain
 */
public final class ExposeStaticFieldsTag extends SimpleTagSupport {
    
    private String className;
    private String targetName;
    
    @Override
    public void doTag() {
        JspContext jspContext = getJspContext();
        if (jspContext.getAttribute(targetName)==null) {
            Class<?> globalClass;
            try {
                globalClass = Class.forName(className);
            }
            catch (ClassNotFoundException e) {
                throw new EngineException("Could not find class " + className, e);
            }
            Map<String, Object> staticFields = WebUtilities.exposeStaticFields(globalClass);
            jspContext.setAttribute(targetName, staticFields);
        }
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
}
