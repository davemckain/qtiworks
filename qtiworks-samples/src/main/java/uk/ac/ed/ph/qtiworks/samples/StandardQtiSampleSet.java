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

import uk.ac.ed.ph.qtiworks.samples.QtiSampleResource.Feature;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleResource.Type;


/**
 * Sample set based on the IMS samples
 *
 * @author David McKain
 */
public final class StandardQtiSampleSet {
    
    private static final QtiSampleSet instance = new QtiSampleSet(
            new QtiSampleResource(Type.ITEM, "ims/adaptive_template.xml"),
            new QtiSampleResource(Type.ITEM, "ims/adaptive.xml"),
            new QtiSampleResource(Type.ITEM, "ims/associate.xml"),
            new QtiSampleResource(Type.ITEM, "ims/choice_doctype.xml", Feature.NOT_SCHEMA_VALID, Feature.NOT_FULLY_VALID),
            new QtiSampleResource(Type.ITEM, "ims/choice_fixed.xml"),
            new QtiSampleResource(Type.ITEM, "ims/choice_multiple_chocolade.xml"),
            new QtiSampleResource(Type.ITEM, "ims/choice_multiple.xml"),
            new QtiSampleResource(Type.ITEM, "ims/choice.xml"),
            new QtiSampleResource(Type.ITEM, "ims/drawing.xml"),
            new QtiSampleResource(Type.ITEM, "ims/extended_text.xml"),
            new QtiSampleResource(Type.ITEM, "ims/feedback_adaptive.xml"),
            new QtiSampleResource(Type.ITEM, "ims/feedback.xml"),
            new QtiSampleResource(Type.ITEM, "ims/gap_match.xml"),
            new QtiSampleResource(Type.ITEM, "ims/graphic_associate.xml"),
            new QtiSampleResource(Type.ITEM, "ims/graphic_gap_match.xml"),
            new QtiSampleResource(Type.ITEM, "ims/graphic_order.xml"),
            new QtiSampleResource(Type.ITEM, "ims/hint.xml"),
            new QtiSampleResource(Type.ITEM, "ims/hotspot.xml"),
            new QtiSampleResource(Type.ITEM, "ims/hottext.xml"),
            new QtiSampleResource(Type.ITEM, "ims/inline_choice.xml"),
            new QtiSampleResource(Type.ITEM, "ims/likert.xml"),
            new QtiSampleResource(Type.ITEM, "ims/match.xml"),
            new QtiSampleResource(Type.ITEM, "ims/math.xml"),
            new QtiSampleResource(Type.ITEM, "ims/nested_object.xml"),
            new QtiSampleResource(Type.ITEM, "ims/order_partial_scoring.xml"),
            new QtiSampleResource(Type.ITEM, "ims/order.xml"),
            new QtiSampleResource(Type.ITEM, "ims/position_object.xml"),
            new QtiSampleResource(Type.ITEM, "ims/select_point.xml"),
            new QtiSampleResource(Type.ITEM, "ims/shufflechoice.xml"),
            new QtiSampleResource(Type.ITEM, "ims/shufflegap_match.xml"),
            new QtiSampleResource(Type.ITEM, "ims/shuffleinline_choice.xml"),
            new QtiSampleResource(Type.ITEM, "ims/slider.xml"),
            new QtiSampleResource(Type.ITEM, "ims/template_image.xml"),
            new QtiSampleResource(Type.ITEM, "ims/template.xml"),
            new QtiSampleResource(Type.ITEM, "ims/text_entry1.xml"),
            new QtiSampleResource(Type.ITEM, "ims/text_entry2.xml"),
            new QtiSampleResource(Type.ITEM, "ims/text_entry.xml"),
            new QtiSampleResource(Type.ITEM, "ims/towns.xml", Feature.NOT_SCHEMA_VALID, Feature.NOT_FULLY_VALID),
            new QtiSampleResource(Type.ITEM, "ims/upload_composite.xml"),
            new QtiSampleResource(Type.ITEM, "ims/upload.xml")
    );
    
    public static QtiSampleSet instance() {
        return instance;
    }

}
