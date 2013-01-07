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
package uk.ac.ed.ph.jqtiplus.node.result;

import uk.ac.ed.ph.jqtiplus.attribute.value.DateAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.group.result.ItemVariableGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.IdentifiableNode;

import java.util.Date;
import java.util.List;

/**
 * Parent of testResult and itemResult.
 *
 * @author Jiri Kajaba
 */
public abstract class AbstractResult extends AbstractNode implements IdentifiableNode<String> {

    private static final long serialVersionUID = -7547227519468855801L;

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    /** Name of datestamp attribute in xml schema. */
    public static final String ATTR_DATE_STAMP_NAME = "datestamp";

    public AbstractResult(final AssessmentResult parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getAttributes().add(new StringAttribute(this, ATTR_IDENTIFIER_NAME, true));
        getAttributes().add(new DateAttribute(this, ATTR_DATE_STAMP_NAME, true));

        getNodeGroups().add(new ItemVariableGroup(this));
    }

    @Override
    public String getIdentifier() {
        return getAttributes().getStringAttribute(ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    @Override
    public void setIdentifier(final String identifier) {
        getAttributes().getStringAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }


    public Date getDateStamp() {
        return getAttributes().getDateAttribute(ATTR_DATE_STAMP_NAME).getComputedValue();
    }

    public void setDateStamp(final Date dateStamp) {
        getAttributes().getDateAttribute(ATTR_DATE_STAMP_NAME).setValue(dateStamp);
    }


    public List<ItemVariable> getItemVariables() {
        return getNodeGroups().getItemVariableGroup().getItemVariables();
    }


    @Override
    public final String computeXPathComponent() {
        final String identifier = getIdentifier();
        if (identifier != null) {
            return getQtiClassName() + "[@identifier=\"" + identifier + "\"]";
        }
        return super.computeXPathComponent();
    }
}
