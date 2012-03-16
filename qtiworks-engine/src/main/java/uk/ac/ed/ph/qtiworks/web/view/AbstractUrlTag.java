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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * Slightly different version of the JSTL c:url tag that behaves slightly nicer.
 * 
 * @author David McKain
 */
abstract class AbstractUrlTag extends SimpleTagSupport {
    
    private String var;
    private String pageName;
    private String pathInfo;
    private String fragment;
    
    /** Query parameters, stored separately as names and then values */
    private final List<String> params;
    
    public AbstractUrlTag() {
        this.params = new ArrayList<String>();
    }
    
    void addParam(String paramName, String paramValue) {
        params.add(paramName);
        params.add(paramValue);
    }
    
    /**
     * Subclass should override this to construct the resulting URL String from
     * the accumulated tag data.
     */
    protected abstract String createUrl(final HttpServletRequest request, Page page,
            final String pathInfo, final String fragment, final Object[] params);
    
    @Override
    public void doTag() throws JspException, IOException {
        /* Make sure page Name corresponds to something sensible */
        Page page = ViewUtilities.decodePathName(pageName);
        
        /* Clear up list of parameters then descend into element body */
        this.params.clear();
        JspFragment body = getJspBody();
        if (body!=null) {
            body.invoke(null);
        }
        
        /* Get at the underlying HTTP request */
        HttpServletRequest request = null;
        try {
            request = (HttpServletRequest) ((PageContext) getJspContext()).getRequest();
        }
        catch (ClassCastException e) {
            throw new JspException("The <url/> tag only works in an HTTP servlet environment");
        }
        
        /* Build up the URL */
        String url = createUrl(request, page, pathInfo, fragment,
                params.toArray(new Object[params.size()]));
        
        /* Store result in attribute */
        getJspContext().setAttribute(var, url);
    }


    public void setVar(String var) {
        this.var = var;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }
}
