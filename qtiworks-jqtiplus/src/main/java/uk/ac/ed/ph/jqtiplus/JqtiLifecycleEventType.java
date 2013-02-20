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
package uk.ac.ed.ph.jqtiplus;

/**
 * Enumerates the different types of JQTI+ lifecycle events fired off
 * during assessment processing.
 *
 *
 * @see JqtiLifecycleListener
 *
 * @author David McKain
 */
public enum JqtiLifecycleEventType {

    /** {@link JqtiExtensionManager} initialisation */
    MANAGER_INITIALISED,

    /** {@link JqtiExtensionManager} destruction */
    MANAGER_DESTROYED,

    /** (Item) Template processing is about to start */
    ITEM_TEMPLATE_PROCESSING_STARTING,

    /** (Item) Template processing has ended */
    ITEM_TEMPLATE_PROCESSING_FINISHED,

    /** (Item) Response processing is about to start */
    ITEM_RESPONSE_PROCESSING_STARTING,

    /** (Item) Response processing has ended */
    ITEM_RESPONSE_PROCESSING_FINISHED,

    /** Test initialisation is about to start */
    TEST_INITIALISATION_STARTING,

    /** Test initialisation has ended */
    TEST_INITIALISATION_FINISHED,

    /** (Test) Outcome processing is about to start */
    TEST_OUTCOME_PROCESSING_STARTING,

    /** (Test) Outcome processing has ended */
    TEST_OUTCOME_PROCESSING_FINISHED,

}
