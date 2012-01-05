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
package org.qtitools.qti.node.test.flow;

import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;

import java.io.Serializable;

/**
 * Item flow interface.
 * 
 * @author Jiri Kajaba
 */
public interface ItemFlow extends Serializable {
    /**
     * Returns true if there are no more item references to be presented; false otherwise.
     * <p>
     * Convenient method for {@code getTest().isFinished()}.
     *
     * @return true if there are no more item references to be presented; false otherwise
     */
    public boolean isFinished();

    /**
     * Gets assessment test of this item flow.
     *
     * @return assessment test of this item flow
     */
    public AssessmentTest getTest();

    /**
     * Gets parent test part of current item reference (can be null).
     * <p>
     * Result is null before test starts and after test finishes.
     *
     * @return parent test part of current item reference (can be null)
     */
    public TestPart getCurrentTestPart();

    /**
     * Gets current item reference (can be null).
     * <p>
     * Result is null before test starts and after test finishes.
     *
     * @return current item reference (can be null)
     */
    public AssessmentItemRef getCurrentItemRef();

    /**
     * Returns true if there is any previous item reference in current test part; false otherwise.
     * <p>
     * Previous item reference means any item reference which was presented before current item reference.
     * <p>
     * Returns false before test starts and after test finishes (current test part is null).
     *
     * @param includeFinished whether consider already finished item references
     * @return true if there is any previous item reference in current test part; false otherwise
     */
    public boolean hasPrevItemRef(boolean includeFinished);

    /**
     * Gets first previous item reference in current test part (can be null).
     * <p>
     * First previous item reference means item reference with the highest lower presented time than current item reference.
     * (First left item reference from current item reference on time axis.)
     *
     * @param includeFinished whether consider already finished item references
     * @return first previous item reference in current test part (can be null)
     */
    public AssessmentItemRef getPrevItemRef(boolean includeFinished);

    /**
     * Returns true if there is any next item reference in current test part; false otherwise.
     * <p>
     * Next item reference means any item reference which was (or will be) presented after current item reference.
     * <p>
     * Returns false before test starts and after test finishes (current test part is null).
     * <p>
     * In linear individual mode this method can be called only when current item is finished!
     *
     * @param includeFinished whether consider already finished item references
     * @return true if there is any next item reference in current test part; false otherwise
     */
    public boolean hasNextItemRef(boolean includeFinished);

    /**
     * Gets first next item reference in test (can be null).
     * <p>
     * First next item reference means item reference with the lowest higher presented time than current item reference.
     * (First right item reference from current item reference on time axis.)
     * <p>
     * This is the only one method which can cross boundary of current test part.
     * Once boundary is crossed, there is no way how to go back!
     * <p>
     * In linear individual mode this method can be called only when current item is finished!
     *
     * @param includeFinished whether consider already finished item references
     * @return first next item reference in test (can be null)
     */
    public AssessmentItemRef getNextItemRef(boolean includeFinished);
}
