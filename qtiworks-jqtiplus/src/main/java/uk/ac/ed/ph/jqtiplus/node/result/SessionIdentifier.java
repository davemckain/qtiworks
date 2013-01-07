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
package uk.ac.ed.ph.jqtiplus.node.result;

import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.UriAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;

import java.net.URI;

/**
 * The system that creates the result (for example, the test delivery system) should assign a session identifier that it
 * can use to identify the session. Subsequent systems that process the result might assign their own identifier to the
 * session which should be added to the context if the result is modified and exported for transport again.
 *
 * @author Jiri Kajaba
 */
public final class SessionIdentifier extends AbstractNode implements ResultNode {

    private static final long serialVersionUID = 29615834277891621L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "sessionIdentifier";

    /** Name of sourceID attribute in xml schema. */
    public static final String ATTR_SOURCE_ID_NAME = "sourceID";

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    public SessionIdentifier(final Context parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new UriAttribute(this, ATTR_SOURCE_ID_NAME, true));
        getAttributes().add(new StringAttribute(this, ATTR_IDENTIFIER_NAME, true));
    }


    public URI getSourceId() {
        return getAttributes().getUriAttribute(ATTR_SOURCE_ID_NAME).getComputedValue();
    }

    public void setSourceId(final URI sourceId) {
        getAttributes().getUriAttribute(ATTR_SOURCE_ID_NAME).setValue(sourceId);
    }


    public String getIdentifier() {
        return getAttributes().getStringAttribute(ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    public void setIdentifier(final String identifier) {
        getAttributes().getStringAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }
}
