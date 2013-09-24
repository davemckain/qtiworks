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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.group.accessibility.companion;

import uk.ac.ed.ph.jqtiplus.exception.QtiIllegalChildException;
import uk.ac.ed.ph.jqtiplus.group.ComplexNodeGroup;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.Protractor;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.RadialIncrementSystem;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.RadialIncrementSystemSI;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.RadialIncrementSystemUS;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Group for {@link RadialIncrementSystemSI} and {@link RadialIncrementSystemUS}, found within {@link Protractor}.
 *
 * @author Zack Pierce
 */
public class RadialIncrementSystemGroup extends ComplexNodeGroup<Protractor, RadialIncrementSystem> {

    private static final long serialVersionUID = 825691341306815922L;

    private static Set<String> radialIncrementSystemNames;

    static {
        final HashSet<String> names = new HashSet<String>();
        names.add(RadialIncrementSystemSI.QTI_CLASS_NAME);
        names.add(RadialIncrementSystemUS.QTI_CLASS_NAME);
        radialIncrementSystemNames = ObjectUtilities.unmodifiableSet(names);
    }

    public RadialIncrementSystemGroup(final Protractor parent) {
        super(parent, RadialIncrementSystem.DISPLAY_NAME, radialIncrementSystemNames, 1, 1);
    }

    @Override
    public RadialIncrementSystem create(final String qtiClassName) {
        if (RadialIncrementSystemSI.QTI_CLASS_NAME.equals(qtiClassName)) {
            return new RadialIncrementSystemSI(getParent());
        }
        else if (RadialIncrementSystemUS.QTI_CLASS_NAME.equals(qtiClassName)) {
            return new RadialIncrementSystemUS(getParent());
        }
        throw new QtiIllegalChildException(getParent(), qtiClassName);
    }

    public RadialIncrementSystemSI getRadialIncrementSystemSI() {
        return QueryUtils.findFirstShallowInstance(RadialIncrementSystemSI.class, getChildren());
    }

    public RadialIncrementSystemUS getRadialIncrementSystemUS() {
        return QueryUtils.findFirstShallowInstance(RadialIncrementSystemUS.class, getChildren());
    }

}
