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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "stomp/AS IS" AND
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

import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment.Feature;

/**
 * Sample set containing examples of the MathAssess extensions
 *
 * @author David McKain
 */
public final class StompSampleSet {
    
    private static final QtiSampleSet instance = new QtiSampleSet("SToMP examples",
            "stomp/Examples from Dick Bacon's SToMP system",
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/a_mcq_dkG2.xml", new String[] { "ststyle.css" }, Feature.NOT_SCHEMA_VALID, Feature.NOT_FULLY_VALID),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/adap3_3pSM.xml", new String[] { "ststyle.css" }, Feature.NOT_SCHEMA_VALID, Feature.NOT_FULLY_VALID),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/baseG.xml", new String[] { "ststyle.css" }, Feature.NOT_FULLY_VALID),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/binary_2GS.xml", new String[] { "ststyle.css" }, Feature.NOT_SCHEMA_VALID, Feature.NOT_FULLY_VALID),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/chc_inlineGS.xml", new String[] { "ststyle.css" }),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/chc_mcqS.xml", new String[] { "ststyle.css" }),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/chc_mulS.xml", new String[] { "ststyle.css" }),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/chc_pairingGS.xml", new String[] { "ststyle.css" }),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/digholeG.xml", new String[] { "ststyle.css" }),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/graphic_associate.xml", new String[] { "ststyle.css" }),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/graphic_gap_matchGS.xml", new String[] { "ststyle.css" }),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/graphic_orderG.xml", new String[] { "ststyle.css" }),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/hot_accel_2G.xml", new String[] { "ststyle.css" }, Feature.NOT_FULLY_VALID),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/hotspotsS.xml", new String[] { "ststyle.css" }),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/hottext.xml", new String[] { "ststyle.css" }),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/meanGS.xml", new String[] { "ststyle.css" }),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/nel1_1pg.xml", new String[] { "ststyle.css" }, Feature.NOT_SCHEMA_VALID, Feature.NOT_FULLY_VALID),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/precisionGS.xml", new String[] { "ststyle.css" }),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/slider2.xml", new String[] { "ststyle.css" }),
            new QtiSampleAssessment(DeliveryStyle.MATHASSESS_STANDARD, "stomp/xchoiceS.xml", new String[] { "ststyle.css" })
    );
    
    private StompSampleSet() {
        /* No constructor */
    }
    
    public static QtiSampleSet instance() {
        return instance;
    }
}
