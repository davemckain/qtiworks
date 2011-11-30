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

package uk.ac.ed.ph.jqtiplus.node.test;

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;

import java.util.HashMap;
import java.util.Map;


/**
 * The VisibilityMode determines how the visibility of the feedback is controlled.
 * <p>
 * SHOW_IF_MATCH - the feedback is hidden by default and shown only if the associated outcome variable
 * matches, or contains, the value of the identifier attribute.
 * <p>
 * HIDE_IF_MATCH - the feedback is shown by default and hidden only if the associated outcome variable
 * matches, or contains, the value of the identifier attribute.
 * <p>
 * This class is referred as showHide in specification.
 * 
 * @author Jiri Kajaba
 */
public enum VisibilityMode
{
    /** Feedback is shown only if outcome variable matches the value of identifier attribute. */
    SHOW_IF_MATCH("show"),

    /** Feedback is shown only if outcome variable doesn't match the value of identifier attribute. */
    HIDE_IF_MATCH("hide");

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "showHide";

    private static Map<String, VisibilityMode> visibilityModes;

    static
    {
        visibilityModes = new HashMap<String, VisibilityMode>();

        for (VisibilityMode visibilityMode : VisibilityMode.values())
            visibilityModes.put(visibilityMode.visibilityMode, visibilityMode);
    }

    private String visibilityMode;

    private VisibilityMode(String visibilityMode)
    {
        this.visibilityMode = visibilityMode;
    }

    @Override
    public String toString()
    {
        return visibilityMode;
    }

    /**
     * Returns parsed <code>VisibilityMode</code> from given <code>String</code>.
     *
     * @param visibilityMode <code>String</code> representation of <code>VisibilityMode</code>
     * @return parsed <code>VisibilityMode</code> from given <code>String</code>
     * @throws QTIParseException if given <code>String</code> is not valid <code>VisibilityMode</code>
     */
    public static VisibilityMode parseVisibilityMode(String visibilityMode) throws QTIParseException
    {
        VisibilityMode result = visibilityModes.get(visibilityMode);

        if (result == null)
            throw new QTIParseException("Invalid " + CLASS_TAG + " '" + visibilityMode + "'.");

        return result;
    }
}
