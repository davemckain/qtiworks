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
package uk.ac.ed.ph.jqtiplus.node.test;

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;

import java.util.HashMap;
import java.util.Map;

/**
 * The view in which the parent object is to be shown.
 * 
 * @author Jiri Kajaba
 */
public enum View {
    /** Author. */
    AUTHOR("author"),

    /** Candidate. */
    CANDIDATE("candidate"),

    /** Proctor or invigilator. */
    PROCTOR("proctor"),

    /** Scorer. */
    SCORER("scorer"),

    /** Test constructor. */
    TEST_CONSTRUCTOR("testConstructor"),

    /** Tutor. */
    TUTOR("tutor");

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "view";

    private static Map<String, View> views;

    static {
        views = new HashMap<String, View>();

        for (final View view : View.values()) {
            views.put(view.view, view);
        }
    }

    private String view;

    private View(String view) {
        this.view = view;
    }

    @Override
    public String toString() {
        return view;
    }

    /**
     * Returns parsed <code>View</code> from given <code>String</code>.
     * 
     * @param view <code>String</code> representation of <code>View</code>
     * @return parsed <code>View</code> from given <code>String</code>
     * @throws QTIParseException if given <code>String</code> is not valid <code>View</code>
     */
    public static View parseView(String view) {
        final View result = views.get(view);

        if (result == null) {
            throw new QTIParseException("Invalid " + CLASS_TAG + " '" + view + "'.");
        }

        return result;
    }
}
