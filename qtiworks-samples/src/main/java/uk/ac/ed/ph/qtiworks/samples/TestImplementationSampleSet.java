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

    private static final QtiSampleSet instance = new QtiSampleSet(
            "Test Implementation Test Samples",
            "Set of sample assessmentTests used to test our test implementation",
            /* My sample tests (originally based on the "George" sample tests from QTIEngine) */
            new QtiSampleAssessment(ASSESSMENT_TEST, TEST_WORK_IN_PROGRESS, "testimplementation/dave/simple-nonlinear-individual.xml",
                    new String[] { "addition-feedback.xml", "choice-feedback.xml", "choice-min.xml" }
            ),
            new QtiSampleAssessment(ASSESSMENT_TEST, TEST_WORK_IN_PROGRESS, "testimplementation/dave/simple-nonlinear-simultaneous.xml",
                    new String[] { "addition-feedback.xml", "choice-feedback.xml", "choice-min.xml" }
            ),
            new QtiSampleAssessment(ASSESSMENT_TEST, TEST_WORK_IN_PROGRESS, "testimplementation/dave/simple-linear-individual.xml",
                    new String[] { "addition-feedback.xml", "choice-feedback.xml", "choice-min.xml" }
            ),
            new QtiSampleAssessment(ASSESSMENT_TEST, TEST_WORK_IN_PROGRESS, "testimplementation/dave/simple-linear-simultaneous.xml",
                    new String[] { "addition-feedback.xml", "choice-feedback.xml", "choice-min.xml" }
            ),
            new QtiSampleAssessment(ASSESSMENT_TEST, TEST_WORK_IN_PROGRESS, "testimplementation/dave/validation-nonlinear-individual.xml",
                    new String[] { "choice-min.xml" }
            ),
            new QtiSampleAssessment(ASSESSMENT_TEST, TEST_WORK_IN_PROGRESS, "testimplementation/dave/validation-linear-individual.xml",
                    new String[] { "choice-min.xml" }
            ),
            new QtiSampleAssessment(ASSESSMENT_TEST, TEST_WORK_IN_PROGRESS, "testimplementation/dave/test-nonlinear-individual.xml",
                    new String[] { "addition-feedback.xml", "addition-no-feedback.xml", "item03.xml", "item04.xml", "item05.xml", "item06.xml" }
            ),
            new QtiSampleAssessment(ASSESSMENT_TEST, TEST_WORK_IN_PROGRESS, "testimplementation/dave/test-nonlinear-simultaneous.xml",
                    new String[] { "addition-feedback.xml", "addition-no-feedback.xml", "item03.xml", "item04.xml", "item05.xml", "item06.xml" }
            ),
            new QtiSampleAssessment(ASSESSMENT_TEST, TEST_WORK_IN_PROGRESS, "testimplementation/dave/test-linear-individual.xml",
                    new String[] { "addition-feedback.xml", "addition-no-feedback.xml", "item03.xml", "item04.xml", "item05.xml", "item06.xml" }
            ),
            new QtiSampleAssessment(ASSESSMENT_TEST, TEST_WORK_IN_PROGRESS, "testimplementation/dave/test-linear-simultaneous.xml",
                    new String[] { "addition-feedback.xml", "addition-no-feedback.xml", "item03.xml", "item04.xml", "item05.xml", "item06.xml" }
            ),
            new QtiSampleAssessment(ASSESSMENT_TEST, TEST_WORK_IN_PROGRESS, "testimplementation/dave/simple-two-testParts.xml",
                    new String[] { "addition-feedback.xml", "choice-feedback.xml" }
            ),
            new QtiSampleAssessment(ASSESSMENT_TEST, TEST_WORK_IN_PROGRESS, "testimplementation/dave/degenerate-testPart.xml",
                    new String[] { "addition-feedback.xml" }
            ),
            new QtiSampleAssessment(ASSESSMENT_TEST, TEST_WORK_IN_PROGRESS, "testimplementation/dave/degenerate-multiple-testParts.xml",
                    new String[] { "addition-feedback.xml" }
            ),
            new QtiSampleAssessment(ASSESSMENT_TEST, TEST_WORK_IN_PROGRESS, "testimplementation/dave/test-testFeedback.xml",
                    new String[] { "addition-feedback.xml" }
            ),
            /* Southampton's "Web Developer Test" (taken and slightly modified from QTIEngine) */
            new QtiSampleAssessment(ASSESSMENT_TEST, TEST_WORK_IN_PROGRESS, "testimplementation/WebDeveloperTest1/template_test1.xml",
                    new String[] { "question1.xml", "question2.xml", "question3.xml", "question4.xml", "question5.xml", "question6.xml", "question7.xml", "question8.xml", "question9.xml" },
                    new String[] { "directory.jpg" }
            ),
            /* Graham Smith's French Test */
            new QtiSampleAssessment(ASSESSMENT_TEST, TEST_WORK_IN_PROGRESS, "testimplementation/GrahamSmith-French-01/ghfrenchtest01a.xml",
                    new String[] {
                        "ghfrench-01-01.xml", "ghfrench-01-erpt.xml", "ghfrench-01-irpt.xml", "ghfrench-01-rept.xml",
                        "ghfrench-01-vapt1.xml", "ghfrench-01-vapt2.xml", "ghfrench-01-vaux.xml", "ghfrench-01-vaux1rem.xml",
                        "ghfrench-01-vaux2rem.xml", "ghfrench-01-vauxp1.xml", "ghfrench-01-vauxp2.xml", "ghfrench-01-vauxpt1.xml",
                        "ghfrench-01-vauxpt2.xml", "ghfrench-01-verp1.xml", "ghfrench-01-verp2.xml", "ghfrench-01-verprem.xml",
                        "ghfrench-01-virp1.xml", "ghfrench-01-virp2.xml", "ghfrench-01-virprem.xml", "ghfrench-01-vrep1.xml",
                        "ghfrench-01-vrep2.xml", "ghfrench-01-vreprem.xml"
                    },
                    new String[0]
            )
    );

    private TestImplementationSampleSet() {
        /* No constructor */
    }

    public static QtiSampleSet instance() {
        return instance;
    }
}
