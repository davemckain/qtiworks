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
 * The navigation mode determines the general paths that the candidate may take.
 * <p>
 * linear - restricts the candidate to attempt each item in turn.
 * Once the candidate moves on they are not permitted to return.
 * <p>
 * nonlinear - the candidate is free to navigate to any item in the test at any time.
 * 
 * @author Jiri Kajaba
 */
public enum NavigationMode
{
    /**
     * Restricts the candidate to attempt each item in turn.
     */
    LINEAR("linear"),

    /**
     * The candidate is free to navigate to any item in the test at any time.
     */
    NONLINEAR("nonlinear");

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "navigationMode";

    private static Map<String, NavigationMode> navigationModes;

    static
    {
        navigationModes = new HashMap<String, NavigationMode>();

        for (NavigationMode navigationMode : NavigationMode.values())
            navigationModes.put(navigationMode.navigationMode, navigationMode);
    }

    private String navigationMode;

    private NavigationMode(String navigationMode)
    {
        this.navigationMode = navigationMode;
    }

    @Override
    public String toString()
    {
        return navigationMode;
    }

    /**
     * Returns parsed <code>NavigationMode</code> from given <code>String</code>.
     *
     * @param navigationMode <code>String</code> representation of <code>NavigationMode</code>
     * @return parsed <code>NavigationMode</code> from given <code>String</code>
     * @throws QTIParseException if given <code>String</code> is not valid <code>NavigationMode</code>
     */
    public static NavigationMode parseNavigationMode(String navigationMode)
    {
        NavigationMode result = navigationModes.get(navigationMode);

        if (result == null)
            throw new QTIParseException("Invalid " + CLASS_TAG + " '" + navigationMode + "'.");

        return result;
    }
}
