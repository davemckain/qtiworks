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

import uk.ac.ed.ph.jqtiplus.group.SimpleSingleNodeGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.Description;

/**
 * Group for description element.
 *
 * <p>TODO : Consider consolidation of the NormalizedStringElement wrapping groups</p>
 *
 * @author Zack Pierce
 */
public class DescriptionGroup extends SimpleSingleNodeGroup<QtiNode, Description> {

    private static final long serialVersionUID = 167401634462574740L;

    public DescriptionGroup(final QtiNode parent, final boolean required) {
        super(parent, Description.QTI_CLASS_NAME, required);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ed.ph.jqtiplus.group.SimpleSingleNodeGroup#create()
     */
    @Override
    public Description create() {
        return new Description(getParent());
    }

    public Description getDescription() {
        return getChild();
    }

    public void setDescription(final Description description) {
        setChild(description);
    }

    public String getDescriptionText() {
        final Description description = this.getDescription();
        if (description != null) {
            return description.getTextContent();
        }
        return null;
    }

    public void setDescriptionText(final String descriptionText) {
        Description description = this.getDescription();
        if (description == null) {
            description = new Description(getParent());
            this.setDescription(description);
        }
        description.setTextContent(descriptionText);
    }

}
