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
            "These are the standard QTI 2.1 reference examples that can be downloaded from the IMS website, "
            + "as well as a few additional ones that have been been useful to try out some other features.",
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_ADAPTIVE, "ims/adaptive_template.xml", new String[] { "ims/images/red_door.png", "ims/images/green_door.png", "ims/images/blue_door.png", "ims/images/open_goat.png", "ims/images/open_car.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_ADAPTIVE, "ims/adaptive.xml", new String[] { "ims/images/red_door.png", "ims/images/green_door.png", "ims/images/blue_door.png", "ims/images/open_goat.png", "ims/images/open_car.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/associate.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/choice.xml", new String[] { "ims/images/sign.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/choice_fixed.xml", new String[] { "ims/images/sign.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/choice_multiple_chocolade.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/choice_multiple.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/drawing.xml", new String[] { "ims/images/house.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_NO_RESPONSE_PROCESSING, "ims/extended_text.xml", new String[] { "ims/images/postcard.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_ADAPTIVE, "ims/feedback_adaptive.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_FEEDBACK, "ims/feedback.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/gap_match.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/graphic_associate.xml", new String[] { "ims/images/ukair.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/graphic_gap_match.xml", new String[] { "ims/images/ukairtags.png", "ims/images/CBG.png", "ims/images/EBG.png", "ims/images/EDI.png", "ims/images/GLA.png", "ims/images/MAN.png", "ims/images/MCH.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/graphic_order.xml", new String[] { "ims/images/ukair.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_FEEDBACK, "ims/hint.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/hotspot.xml", new String[] { "ims/images/ukair.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/hottext.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD,  "ims/inline_choice.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_NO_RESPONSE_PROCESSING, "ims/likert.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/match.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/math.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_NO_RESPONSE_PROCESSING, "ims/nested_object.xml", new String[] { "ims/images/postcard.eps", "ims/images/postcard.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/order_partial_scoring.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/order.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/position_object.xml", new String[] { "ims/images/uk.png", "ims/images/airport.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/select_point.xml", new String[] { "ims/images/uk.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/shufflechoice.xml", new String[] { "ims/images/sign.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/shufflegap_match.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/shuffleinline_choice.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/slider.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD_TEMPLATED, "ims/template_image.xml", new String[] { "ims/images/plane.png", "ims/images/train.png", "ims/images/bus.png" }),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD_TEMPLATED, "ims/template.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/text_entry1.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/text_entry2.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_STANDARD, "ims/text_entry.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_FEEDBACK,  "ims/towns.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_NO_RESPONSE_PROCESSING,"ims/upload_composite.xml"),
            new QtiSampleAssessment(AssessmentObjectType.ASSESSMENT_ITEM, DeliveryStyle.IMS_NO_RESPONSE_PROCESSING, "ims/upload.xml")
    );
    
    private StandardQtiSampleSet() {
        /* No constructor */
    }
    
    public static QtiSampleSet instance() {
        return instance;
    }
}
