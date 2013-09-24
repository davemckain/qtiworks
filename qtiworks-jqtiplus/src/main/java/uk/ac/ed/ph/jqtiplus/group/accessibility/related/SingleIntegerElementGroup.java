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
package uk.ac.ed.ph.jqtiplus.group.accessibility.related;

import uk.ac.ed.ph.jqtiplus.group.SimpleSingleNodeGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.accessibility.SingleIntegerElement;

/**
 * @author Zack Pierce
 */
public class SingleIntegerElementGroup extends SimpleSingleNodeGroup<QtiNode, SingleIntegerElement> {

    private static final long serialVersionUID = -2669119907664947913L;

    public SingleIntegerElementGroup(final QtiNode parent, final String localName, final boolean required) {
        super(parent, localName, required);
    }

    @Override
    public SingleIntegerElement create() {
        return new SingleIntegerElement(getParent(), getName());
    }

    public SingleIntegerElement getSingleIntegerElement() {
        return getChild();
    }

    public void setSingleIntegerElement(final SingleIntegerElement singleIntegerElement) {
        setChild(singleIntegerElement);
    }

    /**
     * @return The Integer value of the child SupportOrder, or null if no child exists
     */
    public Integer getInteger() {
        final SingleIntegerElement element = getSingleIntegerElement();
        return element != null ? element.getValue() : null;
    }

    /**
     * Sets the Integer value of the child SupportOrder. If no such child previously
     * existed, this convenience method will create and internally add a new instance.
     * @param integer
     */
    public void setInteger(final Integer integer) {
        SingleIntegerElement element = getSingleIntegerElement();
        if (element == null) {
            element = new SingleIntegerElement(getParent(), getName());
            setChild(element);
        }
        element.setValue(integer);
    }

}
