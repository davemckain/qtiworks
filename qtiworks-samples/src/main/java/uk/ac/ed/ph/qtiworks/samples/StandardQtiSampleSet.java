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

/**
 * Sample set based on the IMS samples
 *
 * @author David McKain
 */
public final class StandardQtiSampleSet {
    
    private static final QtiSampleSet instance = new QtiSampleSet("Standard QTI 2.1 reference examples",
            "These are the standard QTI 2.1 reference examples that can be downloaded from the IMS website, "
            + "as well as a few additional ones that have been been useful to try out some other features.",
            new QtiSampleAssessment(DeliveryStyle.IMS_ADAPTIVE, "ims/adaptive_template.xml", new String[] { "images/red_door.png", "images/green_door.png", "images/blue_door.png", "images/open_goat.png", "images/open_car.png" }),
            new QtiSampleAssessment(DeliveryStyle.IMS_ADAPTIVE, "ims/adaptive.xml", new String[] { "images/red_door.png", "images/green_door.png", "images/blue_door.png", "images/open_goat.png", "images/open_car.png" }),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/associate.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/choice.xml", new String[] { "images/sign.png" }),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/choice_fixed.xml", new String[] { "images/sign.png" }),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/choice_multiple_chocolade.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/choice_multiple.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/drawing.xml", new String[] { "images/house.png" }),
            new QtiSampleAssessment(DeliveryStyle.IMS_NO_RESPONSE_PROCESSING, "ims/extended_text.xml", new String[] { "images/postcard.png" }),
            new QtiSampleAssessment(DeliveryStyle.IMS_ADAPTIVE, "ims/feedback_adaptive.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_FEEDBACK, "ims/feedback.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/gap_match.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/graphic_associate.xml", new String[] { "images/ukair.png" }),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/graphic_gap_match.xml", new String[] { "images/ukairtags.png", "images/CBG.png", "images/EBG.png", "images/EDI.png", "images/GLA.png", "images/MAN.png", "images/MCH.png" }),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/graphic_order.xml", new String[] { "images/ukair.png" }),
            new QtiSampleAssessment(DeliveryStyle.IMS_FEEDBACK, "ims/hint.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/hotspot.xml", new String[] { "images/ukair.png" }),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/hottext.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/inline_choice.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_NO_RESPONSE_PROCESSING, "ims/likert.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/match.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/math.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_NO_RESPONSE_PROCESSING, "ims/nested_object.xml", new String[] { "images/postcard.eps", "images/postcard.png" }),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/order_partial_scoring.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/order.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/position_object.xml", new String[] { "images/uk.png", "images/airport.png" }),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/select_point.xml", new String[] { "images/uk.png" }),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/shufflechoice.xml", new String[] { "images/sign.png" }),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/shufflegap_match.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/shuffleinline_choice.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/slider.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD_TEMPLATED, "ims/template_image.xml", new String[] { "images/plane.png", "images/train.png", "images/bus.png" }),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD_TEMPLATED, "ims/template.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/text_entry1.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/text_entry2.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_STANDARD, "ims/text_entry.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_FEEDBACK, "ims/towns.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_NO_RESPONSE_PROCESSING, "ims/upload_composite.xml"),
            new QtiSampleAssessment(DeliveryStyle.IMS_NO_RESPONSE_PROCESSING, "ims/upload.xml")
    );
    
    private StandardQtiSampleSet() {
        /* No constructor */
    }
    
    public static QtiSampleSet instance() {
        return instance;
    }
}
