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
package uk.ac.ed.ph.qtiworks.samples;

import static uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType.ASSESSMENT_TEST;
import static uk.ac.ed.ph.qtiworks.samples.DeliveryStyle.TEST_WORK_IN_PROGRESS;

/**
 * Sample assessmentTests used to test the implementation of tests.
 *
 * @author David McKain
 */
public final class TestImplementationSampleSet {
    
    private static final QtiSampleSet instance = new QtiSampleSet("Test Implementation Test Samples",
            "Set of sample assessmentTests used to test our implementation",
            new QtiSampleAssessment(ASSESSMENT_TEST, TEST_WORK_IN_PROGRESS, "testimplementation/WebDeveloperTest1/template_test1.xml",
                    new String[] { "question1.xml", "question2.xml", "question3.xml", "question4.xml", "question5.xml", "question6.xml", "question7.xml", "question8.xml", "question9.xml" },
                    new String[] { "directory.jpg" })
    );
    
    private TestImplementationSampleSet() {
        /* No constructor */
    }
    
    public static QtiSampleSet instance() {
        return instance;
    }
}
