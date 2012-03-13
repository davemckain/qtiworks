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
package uk.ac.ed.ph.jqtiplus.xmlutils.xslt;

import javax.xml.transform.Templates;

/**
 * (Ported from SnuggleTeX)
 * 
 * Encapsulates a simple cache for the internal XSLT stylesheets used by SnuggleTeX.
 * This can be used if you want SnuggleTeX to integrate with some kind of XSLT caching mechanism
 * (e.g. your own).
 * <p>
 * A {@link SnuggleEngine} creates a default implementation of this that caches stylesheets
 * over the lifetime of the {@link SnuggleEngine} Object, which is reasonable. If you want
 * to change this, create your own implementation and attach it to your {@link SnuggleEngine}.
 * <p>
 * You can use the {@link SimpleStylesheetCache} in your own applications if you want to.
 * 
 * <h2>Internal Note</h2>
 * 
 * (I'm not currently enforcing that implementations of this should be thread-safe. Therefore, make
 * sure that you synchronise correctly when accessing an instance of this cache. You would normally
 * just use a {@link XsltStylesheetManager} instance to do this safely.)
 * 
 * @see SimpleStylesheetCache
 *
 * @author  David McKain
 * @version $Revision: 662 $
 */
public interface XsltStylesheetCache {
   
    /**
     * Tries to retrieve an XSLT stylesheet from the cache having the given key.
     * <p>
     * Return a previously cached {@link Templates} or null if your cache doesn't want to cache
     * this or if it does not contain the required result.
     */
    Templates getStylesheet(String key);
    
    /**
     * Instructs the cache that it might want to store the given XSLT stylesheet corresponding
     * to the given key.
     * <p>
     * Implementations can safely choose to do absolutely nothing here if they want.
     */
    void putStylesheet(String key, Templates stylesheet);

}
