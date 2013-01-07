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

/**
 * Sample set containing examples of the MathAssess extensions
 *
 * @author David McKain
 */
public final class UpmcSampleSet {
    
    private static final QtiSampleSet instance = new QtiSampleSet("UPMC examples",
            "Mathematics examples from Université Pierre et Marie Curie (Paris)",
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/addition.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/addition2entier.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/carre2.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/carre2g.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/exercise1.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/exercise2.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/exercise3.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/exercise4.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/exercise5.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/relatif3.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/sexercice1.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/sexercice13.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/sexercice14.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/sexercice2.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/sexercice6.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/sexercice7.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/wexercice7.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/wexercice8.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/wexercise1.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/wexercise2.xml"),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "upmc/wexercise3.xml")
    );
    
    private UpmcSampleSet() {
        /* No constructor */
    }
    
    public static QtiSampleSet instance() {
        return instance;
    }
}
