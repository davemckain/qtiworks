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
package uk.ac.ed.ph.qtiworks.samples;

import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;

/**
 * Sample set based on the IMS samples
 *
 * @author David McKain
 */
public final class StandardQtiSampleSet {
    
    private static final QtiSampleSet instance = new QtiSampleSet("Standard QTI 2.1 reference examples",
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/adaptive_template.xml", new String[] { "images/red_door.png", "images/green_door.png", "images/blue_door.png", "images/open_goat.png", "images/open_car.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/adaptive.xml", new String[] { "images/red_door.png", "images/green_door.png", "images/blue_door.png", "images/open_goat.png", "images/open_car.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/associate.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/choice.xml", new String[] { "images/sign.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/choice_fixed.xml", new String[] { "images/sign.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/choice_multiple_chocolade.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/choice_multiple.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/drawing.xml", new String[] { "images/house.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/extended_text.xml", new String[] { "images/postcard.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/feedback_adaptive.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/feedback.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/gap_match.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/graphic_associate.xml", new String[] { "images/ukair.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/graphic_gap_match.xml", new String[] { "images/ukairtags.png", "images/CBG.png", "images/EBG.png", "images/EDI.png", "images/GLA.png", "images/MAN.png", "images/MCH.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/graphic_order.xml", new String[] { "images/ukair.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/hint.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/hotspot.xml", new String[] { "images/ukair.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/hottext.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/inline_choice.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/likert.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/match.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/math.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/nested_object.xml", new String[] { "images/postcard.eps", "images/postcard.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/order_partial_scoring.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/order.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/position_object.xml", new String[] { "images/uk.png", "images/airport.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/select_point.xml", new String[] { "images/uk.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/shufflechoice.xml", new String[] { "images/sign.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/shufflegap_match.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/shuffleinline_choice.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/slider.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/template_image.xml", new String[] { "images/plane.png", "images/train.png", "images/bus.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/template.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/text_entry1.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/text_entry2.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/text_entry.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/towns.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/upload_composite.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, "ims/upload.xml")
    );
    
    private StandardQtiSampleSet() {
        /* No constructor */
    }
    
    public static QtiSampleSet instance() {
        return instance;
    }
}
