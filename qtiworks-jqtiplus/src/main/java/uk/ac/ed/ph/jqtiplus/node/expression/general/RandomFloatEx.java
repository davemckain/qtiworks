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
package uk.ac.ed.ph.jqtiplus.node.expression.general;

import uk.ac.ed.ph.jqtiplus.attribute.value.LongAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;

/**
 * Extends randomFloat expression - supports seed attribute.
 * <p>
 * This expression should be used only for special purposes when you need repeatability (not for real assessments).
 * 
 * @author Jiri Kajaba
 */
public class RandomFloatEx extends RandomFloat {

    private static final long serialVersionUID = -2184877054673297858L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "randomFloatEx";

    /** Name of seed attribute in xml schema. */
    public static final String ATTR_SEED_NAME = "seed";

    /** Default value of seed attribute. */
    public static final Long ATTR_SEED_DEFAULT_VALUE = null;

    public RandomFloatEx(ExpressionParent parent) {
        super(parent);

        getAttributes().add(new LongAttribute(this, ATTR_SEED_NAME, ATTR_SEED_DEFAULT_VALUE));
    }

    @Override
    public Long getSeedAttributeValue() {
        return getAttributes().getLongAttribute(ATTR_SEED_NAME).getValue();
    }

    /**
     * Sets new value of seed attribute.
     * 
     * @param seed new value of seed attribute
     * @see #getSeedAttributeValue
     */
    public void setSeedAttributeValue(Long seed) {
        getAttributes().getLongAttribute(ATTR_SEED_NAME).setValue(seed);
    }

    @Override
    public boolean isVariable() {
        return true;
    }
}
