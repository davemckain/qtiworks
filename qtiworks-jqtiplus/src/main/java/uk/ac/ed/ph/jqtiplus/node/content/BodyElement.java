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
package uk.ac.ed.ph.jqtiplus.node.content;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Content;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.util.List;

import javax.xml.XMLConstants;

/**
 * The root class of all content objects in the item content model is the
 * bodyElement. It defines a number of attributes that are common to all
 * elements of the content model.
 * Attribute : id [0..1]: identifier
 * The id of a body element must be unique within the item.
 * Attribute : class [*]: styleclass
 * Classes can be assigned to individual body elements. Multiple class names
 * can be given. These class names identify the element as being a member of
 * the listed classes. Membership of a class can be used by authoring systems
 * to distinguish between content objects that are not differentiated by this
 * specification. Typically, this information is used to apply different
 * formatting based on definitions in an associated stylesheet.
 * Attribute : lang [0..1]: language
 * The main language of the element. This attribute is optional and will
 * usually be inherited from the enclosing element.
 * Attribute : label [0..1]: string256
 * The label attribute provides authoring systems with a mechanism for labeling
 * elements of the content model with application specific data. If an item uses
 * labels then values for the associated toolName and toolVersion attributes must
 * also be provided.
 *
 * @author Jonathon Hare
 */
public abstract class BodyElement extends AbstractNode implements Content {

    private static final long serialVersionUID = 876241954731607171L;

    /** Display name of this class. */
    public static final String DISPLAY_NAME = "bodyElement";

    /** Name of id attribute in xml schema. */
    public static final String ATTR_ID_NAME = "id";

    /** Name of class attribute in xml schema. */
    public static final String ATTR_CLASS_NAME = "class";

    /** Name of lang attribute in xml schema. */
    public static final String ATTR_LANG_NAME = "lang";

    /** Name of label attribute in xml schema. */
    public static final String ATTR_LABEL_NAME = "label";

    public BodyElement(final QtiNode parent, final String qtiClassName) {
        super(parent, qtiClassName);
        getAttributes().add(new IdentifierAttribute(this, ATTR_ID_NAME, false));
        getAttributes().add(new StringMultipleAttribute(this, ATTR_CLASS_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_LANG_NAME, XMLConstants.XML_NS_URI, null, false));
        getAttributes().add(new StringAttribute(this, ATTR_LABEL_NAME, false));
    }

    public Identifier getId() {
        return getAttributes().getIdentifierAttribute(ATTR_ID_NAME).getComputedValue();
    }

    public void setId(final Identifier id) {
        getAttributes().getIdentifierAttribute(ATTR_ID_NAME).setValue(id);
    }


    public List<String> getClassAttr() {
        return getAttributes().getStringMultipleAttribute(ATTR_CLASS_NAME).getComputedValue();
    }

    public void setClassAttr(final List<String> value) {
        getAttributes().getStringMultipleAttribute(ATTR_CLASS_NAME).setValue(value);
    }


    public String getLang() {
        return getAttributes().getStringAttribute(ATTR_LANG_NAME).getComputedValue();
    }

    public void setLang(final String lang) {
        getAttributes().getStringAttribute(ATTR_LANG_NAME).setValue(lang);
    }


    public String getLabel() {
        return getAttributes().getStringAttribute(ATTR_LABEL_NAME).getComputedValue();
    }

    public void setLabel(final String label) {
        getAttributes().getStringAttribute(ATTR_LABEL_NAME).setValue(label);
    }
}
