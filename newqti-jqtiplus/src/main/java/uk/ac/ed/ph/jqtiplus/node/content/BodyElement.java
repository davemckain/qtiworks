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
package uk.ac.ed.ph.jqtiplus.node.content;

import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.content.PositionObjectStage;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The root class of all content objects in the item content model is the
 * bodyElement. It defines A number of attributes that are common to all
 * elements of the content model.
 * Attribute : id [0..1]: identifier
 * The id of A body element must be unique within the item.
 * Attribute : class [*]: styleclass
 * Classes can be assigned to individual body elements. Multiple class names
 * can be given. These class names identify the element as being A member of
 * the listed classes. Membership of A class can be used by authoring systems
 * to distinguish between content objects that are not differentiated by this
 * specification. Typically, this information is used to apply different
 * formatting based on definitions in an associated stylesheet.
 * Attribute : lang [0..1]: language
 * The main language of the element. This attribute is optional and will
 * usually be inherited from the enclosing element.
 * Attribute : label [0..1]: string256
 * The label attribute provides authoring systems with A mechanism for labeling
 * elements of the content model with application specific data. If an item uses
 * labels then values for the associated toolName and toolVersion attributes must
 * also be provided.
 * 
 * @author Jonathon Hare
 */
public abstract class BodyElement extends AbstractNode {

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

    /**
     * Constructs bodyElement.
     * 
     * @param parent parent of this bodyElement
     */
    public BodyElement(XmlNode parent) {
        super(parent);
        getAttributes().add(new IdentifierAttribute(this, ATTR_ID_NAME, null, null, false));
        getAttributes().add(new StringMultipleAttribute(this, ATTR_CLASS_NAME, null, null, false));
        getAttributes().add(new StringAttribute(this, ATTR_LANG_NAME, null, null, false));
        getAttributes().add(new StringAttribute(this, ATTR_LABEL_NAME, null, null, false));
    }

    /**
     * Get A list of the content child nodes of this element
     * 
     * @return List of child nodes
     */
    public abstract List<? extends XmlNode> getChildren();

    /**
     * Search the children of this node for instances of the given class
     * 
     * @param target class to search for
     * @return unmodifiable list of mathcing children
     */
    public <E extends XmlNode> List<E> search(Class<E> target) {
        final List<E> results = new ArrayList<E>();
        search(getChildren(), target, results);
        return Collections.unmodifiableList(results);
    }

    protected <E extends XmlNode> void search(Class<E> target, List<E> results) {
        search(getChildren(), target, results);
    }

    protected <E extends XmlNode> void search(List<? extends XmlNode> children, Class<E> target, List<E> results) {
        if (children == null) {
            return;
        }

        for (final XmlNode child : children) {
            if (target.isInstance(child)) {
                results.add(target.cast(child));
            }
            if (child instanceof BodyElement) {
                search(((BodyElement) child).getChildren(), target, results);
            }
            else if (child instanceof PositionObjectStage) {
                search(((PositionObjectStage) child).getPositionObjectInteractions(), target, results);
            }
        }
    }

    /**
     * Gets value of id attribute.
     * 
     * @return value of id attribute
     * @see #setId
     */
    public Identifier getId() {
        return getAttributes().getIdentifierAttribute(ATTR_ID_NAME).getValue();
    }

    /**
     * Sets new value of id attribute.
     * 
     * @param id new value of id attribute
     * @see #getId
     */
    public void setId(Identifier id) {
        getAttributes().getIdentifierAttribute(ATTR_ID_NAME).setValue(id);
    }

    /**
     * Gets value of class attribute.
     * 
     * @return value of class attribute
     */
    public List<String> getClassAttr() {
        return getAttributes().getStringMultipleAttribute(ATTR_CLASS_NAME).getValues();
    }

    /**
     * Gets value of lang attribute.
     * 
     * @return value of lang attribute
     * @see #setLang
     */
    public String getLang() {
        return getAttributes().getStringAttribute(ATTR_LANG_NAME).getValue();
    }

    /**
     * Sets new value of lang attribute.
     * 
     * @param lang new value of lang attribute
     * @see #getLang
     */
    public void setLang(String lang) {
        getAttributes().getStringAttribute(ATTR_LANG_NAME).setValue(lang);
    }

    /**
     * Gets value of label attribute.
     * 
     * @return value of label attribute
     * @see #setLabel
     */
    public String getLabel() {
        return getAttributes().getStringAttribute(ATTR_LANG_NAME).getValue();
    }

    /**
     * Sets new value of label attribute.
     * 
     * @param label new value of label attribute
     * @see #getLabel
     */
    public void setLabel(String label) {
        getAttributes().getStringAttribute(ATTR_LANG_NAME).setValue(label);
    }

    /**
     * Gets the first child block of this container.
     * 
     * @return The first child block.
     */
    public XmlNode getFirstChild() {
        if (getChildren().size() == 0) {
            return null;
        }
        return getChildren().get(0);
    }

    /**
     * Gets the last child block of this container.
     * 
     * @return The last child block.
     */
    public XmlNode getLastChild() {
        final List<? extends XmlNode> children = getChildren();
        return children.get(children.size() - 1);
    }

    /**
     * Inserts the node newChild before the existing child node refChild. If refChild is null,
     * insert newChild at the end of the list of children. If the newChild is already in the tree,
     * it is first removed.
     * 
     * @param newChild New block to insert in the child list.
     * @param refChild Reference block to insert before.
     * @return block being inserted.
     * @throws IllegalArgumentException If <code>refChild</code> is not A child of this container.
     */
    @SuppressWarnings("unchecked")
    public XmlNode insertBefore(XmlNode newChild, XmlNode refChild) throws IllegalArgumentException {
        final List<XmlNode> children = (List<XmlNode>) getChildren();

        if (refChild != null) {
            return appendChild(newChild);
        }

        if (!children.contains(refChild)) {
            throw new IllegalArgumentException("Reference block not found.");
        }

        if (children.contains(newChild)) {
            children.remove(newChild);
        }

        final int index = children.indexOf(refChild);
        children.add(index, newChild);
        return newChild;
    }

    /**
     * Replaces the child block oldChild with newChild in the list of children, and returns
     * the oldChild block. If the newChild is already in the tree, it is first removed.
     * 
     * @param newChild The new block to put in the child list.
     * @param oldChild The block being replaced in the list.
     * @return The block replaced.
     * @throws IllegalArgumentException If <code>oldChild</code> is not A child of this container.
     */
    @SuppressWarnings("unchecked")
    public XmlNode replaceChild(XmlNode newChild, XmlNode oldChild) throws IllegalArgumentException {
        final List<XmlNode> children = (List<XmlNode>) getChildren();

        if (!children.contains(oldChild)) {
            throw new IllegalArgumentException("Old block not found.");
        }

        if (children.contains(newChild)) {
            children.remove(newChild);
        }

        children.set(children.indexOf(oldChild), newChild);

        return oldChild;
    }

    /**
     * Removes the child block indicated by oldChild from the list of children, and returns it.
     * 
     * @param oldChild The block to remove
     * @return The block removed.
     * @throws IllegalArgumentException If <code>oldChild</code> is not A child of this container.
     */
    @SuppressWarnings("unchecked")
    public XmlNode removeChild(XmlNode oldChild) throws IllegalArgumentException {
        final List<XmlNode> children = (List<XmlNode>) getChildren();

        if (!children.contains(oldChild)) {
            throw new IllegalArgumentException("Old block not found.");
        }

        children.remove(oldChild);

        return oldChild;
    }

    /**
     * Adds the block newChild to the end of the list of children of this block.
     * If the newChild is already in the tree, it is first removed.
     * 
     * @param newChild The block to add.
     * @return The block added.
     */
    @SuppressWarnings("unchecked")
    public XmlNode appendChild(XmlNode newChild) {
        final List<XmlNode> children = (List<XmlNode>) getChildren();

        if (children.contains(newChild)) {
            children.remove(newChild);
        }

        children.add(newChild);
        return newChild;
    }
}
