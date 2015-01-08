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
package uk.ac.ed.ph.jqtiplus.node.content.variable;

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerOrVariableRefAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.AbstractFlowBodyElement;
import uk.ac.ed.ph.jqtiplus.node.content.basic.FlowStatic;
import uk.ac.ed.ph.jqtiplus.node.content.basic.InlineStatic;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.IntegerOrVariableRef;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

/**
 * This is the only way how to show variables to actor.
 *
 * FIXME: Need to support validation of number formats, printing using all number formats, validation
 * of additonal attributes, printing using additional attributes etc.
 *
 * @author Jonathon Hare
 */
public final class PrintedVariable extends AbstractFlowBodyElement implements FlowStatic, InlineStatic, TextOrVariable {

    private static final long serialVersionUID = 1096970249266471727L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "printedVariable";

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    /** Name of format attribute in xml schema. */
    public static final String ATTR_FORMAT_NAME = "format";

    /** Name of powerForm attribute in xml schema. */
    public static final String ATTR_POWER_FORM_NAME = "powerForm";

    /** Name of base attribute in xml schema. */
    public static final String ATTR_BASE_NAME = "base";

    /** Name of index attribute in xml schema. */
    public static final String ATTR_INDEX_NAME = "index";

    /** Name of delimiter attribute in xml schema. */
    public static final String ATTR_DELIMTER_NAME = "delimiter";

    /** Default value of delimiter attribute. */
    public static final String ATTR_DELMITER_DEFAULT_VALUE = ";";

    /** Name of field attribute in xml schema. */
    public static final String ATTR_FIELD_NAME = "field";

    /** Name of mapping indicator attribute in xml schema. */
    public static final String ATTR_MAPPING_INDICATOR_NAME = "mappingIndicator";

    /** Default value of mappingIndicator attribute. */
    public static final String ATTR_MAPPING_INDICATOR_DEFAULT_VALUE = "=";

    /** Default value of base attribute. */
    public static final int ATTR_BASE_DEFAULT_VALUE = 10;

    public PrintedVariable(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME, true));
        getAttributes().add(new StringAttribute(this, ATTR_FORMAT_NAME, false));
        getAttributes().add(new BooleanAttribute(this, ATTR_POWER_FORM_NAME, false));
        getAttributes().add(new IntegerOrVariableRefAttribute(this, ATTR_BASE_NAME, new IntegerOrVariableRef(ATTR_BASE_DEFAULT_VALUE), false));
        getAttributes().add(new IntegerOrVariableRefAttribute(this, ATTR_INDEX_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_DELIMTER_NAME, ATTR_DELMITER_DEFAULT_VALUE, false));
        getAttributes().add(new StringAttribute(this, ATTR_FIELD_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_MAPPING_INDICATOR_NAME, ATTR_MAPPING_INDICATOR_DEFAULT_VALUE, false));
    }


    public Identifier getIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    public void setIdentifier(final Identifier identifier) {
        getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }


    public String getFormat() {
        return getAttributes().getStringAttribute(ATTR_FORMAT_NAME).getComputedValue();
    }

    public void setFormat(final String format) {
        getAttributes().getStringAttribute(ATTR_FORMAT_NAME).setValue(format);
    }


    public Boolean getPowerForm() {
        return getAttributes().getBooleanAttribute(ATTR_POWER_FORM_NAME).getComputedValue();
    }

    public void setPowerForm(final Boolean powerForm) {
        getAttributes().getBooleanAttribute(ATTR_POWER_FORM_NAME).setValue(powerForm);
    }


    public IntegerOrVariableRef getBase() {
        return getAttributes().getIntegerOrVariableRefAttribute(ATTR_BASE_NAME).getComputedValue();
    }

    public void setBase(final IntegerOrVariableRef base) {
        getAttributes().getIntegerOrVariableRefAttribute(ATTR_BASE_NAME).setValue(base);
    }


    public IntegerOrVariableRef getIndex() {
        return getAttributes().getIntegerOrVariableRefAttribute(ATTR_INDEX_NAME).getComputedValue();
    }

    public void setIndex(final IntegerOrVariableRef base) {
        getAttributes().getIntegerOrVariableRefAttribute(ATTR_BASE_NAME).setValue(base);
    }


    public String getDelimiter() {
        return getAttributes().getStringAttribute(ATTR_DELIMTER_NAME).getComputedValue();
    }

    public void setDelimiter(final String delimiter) {
        getAttributes().getStringAttribute(ATTR_DELIMTER_NAME).setValue(delimiter);
    }


    public String getField() {
        return getAttributes().getStringAttribute(ATTR_FIELD_NAME).getComputedValue();
    }

    public void setField(final String field) {
        getAttributes().getStringAttribute(ATTR_FIELD_NAME).setValue(field);
    }


    public String getMappingIndicator() {
        return getAttributes().getStringAttribute(ATTR_MAPPING_INDICATOR_NAME).getComputedValue();
    }

    public void setMappingIndicator(final String mappingIndicator) {
        getAttributes().getStringAttribute(ATTR_MAPPING_INDICATOR_NAME).setValue(mappingIndicator);
    }


    @Override
    public void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final Identifier identifier = getIdentifier();
        if (identifier != null) {
            final VariableDeclaration variableDeclaration = context.checkLocalVariableReference(this, identifier);
            context.checkVariableType(this, variableDeclaration, VariableType.TEMPLATE, VariableType.OUTCOME);
        }
    }
}
