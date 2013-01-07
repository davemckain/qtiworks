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
package org.qtitools.qti.node.test.flow;

import static org.junit.Assert.assertEquals;

import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;


public class State {

    private final boolean testPresented;

    private final boolean testFinished;

    // private String testPart;
    // private Boolean testPartPresented;
    // private Boolean testPartFinished;
    private final String itemRef;

    private final Boolean itemRefPresented;

    private final Boolean itemRefFinished;

    public State(boolean testPresented
            , boolean testFinished
            , String testPart
            , Boolean testPartPresented
            , Boolean testPartFinished
            , String itemRef
            , Boolean itemRefPresented
            , Boolean itemRefFinished) {
        this.testPresented = testPresented;
        this.testFinished = testFinished;
        // this.testPart = testPart;
        // this.testPartPresented = testPartPresented;
        // this.testPartFinished = testPartFinished;
        this.itemRef = itemRef;
        this.itemRefPresented = itemRefPresented;
        this.itemRefFinished = itemRefFinished;
    }

    public void check(ItemFlow flow) {
        final AssessmentTest test = flow.getTest();
        assertEquals(testPresented, test.isPresented());
        assertEquals(testFinished, test.isFinished());

        // TestPart testPart = flow.getCurrentTestPart();
        // assertEquals(this.testPart, (testPart != null) ?
        // testPart.getIdentifier() : null);
        // assertEquals(this.testPartPresented, (testPart != null) ?
        // testPart.isPresented() : null);
        // assertEquals(this.testPartFinished, (testPart != null) ?
        // testPart.isFinished() : null);

        final AssessmentItemRef itemRef = flow.getCurrentItemRef();
        assertEquals(this.itemRef, itemRef != null ? itemRef.getIdentifier() : null);
        assertEquals(this.itemRefPresented, itemRef != null ? itemRef.isPresented() : null);
        assertEquals(this.itemRefFinished, itemRef != null ? itemRef.isFinished() : null);
    }
}
