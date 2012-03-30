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
package uk.ac.ed.ph.jqtiplus.attribute;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.BaseTypeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.CardinalityAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.NavigationModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.OrientationAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.ParamTypeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.RoundingModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.SessionStatusAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.ShapeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.SubmissionModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.TableCellScopeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.TestFeedbackAccessAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.TextFormatAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.ToleranceModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.ViewMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.enumerate.VisibilityModeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.CoordsAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.DateAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.DurationAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.FloatAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.FloatMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.LongAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.SingleValueAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.UriAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.VariableReferenceIdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QtiAttributeException;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.validation.Validatable;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Container for all attributes of one node.
 * 
 * @author Jiri Kajaba
 */
public final class AttributeList implements Validatable, Iterable<Attribute<?>> {

    private static final long serialVersionUID = 4537124098886951888L;

    /** Owner (node) of these attributes. */
    private final XmlNode owner;

    /** Children (attributes) of this container. */
    private final List<Attribute<?>> attributes;

    /**
     * Constructs container.
     * 
     * @param owner parent of constructed container
     */
    public AttributeList(XmlNode owner) {
        ConstraintUtilities.ensureNotNull(owner);
        this.owner = owner;
        this.attributes = new ArrayList<Attribute<?>>();
    }

    /**
     * Gets owner of this container.
     * 
     * @return parent of this container
     */
    public XmlNode getOwner() {
        return owner;
    }

    /**
     * Gets number of attributes in this container.
     * 
     * @return number of attributes in this container
     */
    public int size() {
        return attributes.size();
    }

    /**
     * Adds given attribute into this container.
     * Checks duplicities in attribute's names.
     * 
     * @param attribute given attribute
     * @throws QtiAttributeException if container already contains attribute
     *             with same name
     */
    public void add(Attribute<?> attribute) {
        for (final Attribute<?> child : attributes) {
            if (child.getLocalName().equals(attribute.getLocalName())) {
                final QtiAttributeException ex = new QtiAttributeException("Duplicate attribute name: " + attribute.computeXPath());
                throw ex;
            }
        }

        attributes.add(attribute);
    }

    /**
     * Removes given attribute from this container.
     * 
     * @param attribute given attribute
     */
    public void remove(Attribute<?> attribute) {
        for (final Attribute<?> child : attributes) {
            if (child.getLocalName().equals(attribute.getLocalName())) {
                attributes.remove(child);
                break;
            }
        }
    }

    /**
     * Adds given attribute into this container at given position.
     * Checks duplicities in attribute's names.
     * 
     * @param index position
     * @param attribute attribute
     * @throws QtiAttributeException if container already contains attribute
     *             with same name
     */
    public void add(int index, Attribute<?> attribute) throws QtiAttributeException {
        attributes.add(index, attribute);
    }

    /**
     * Loads attribute's values from given source node.
     * If there is a foreign attribute, it creates new optional
     * ForeignAttribute with its foreign property set.
     * 
     * @param element source node
     */
    public void load(Element element, LoadingContext context) {
        /* First clear existing attributes */
        for (int i = 0; i < attributes.size(); i++) {
            final Attribute<?> attribute = attributes.get(i);
            if (attribute instanceof ForeignAttribute) {
                /* Foreign attribute, so remove to add in again */
                attributes.remove(i);
            }
            else {
                /* Supported attribute, so clear for setting later */
                attribute.load(element, (String) null, context);
            }
        }

        /* Set set values from element */
        for (int i = 0; i < element.getAttributes().getLength(); i++) {
            Node attributeNode = element.getAttributes().item(i);
            String localName = attributeNode.getLocalName();
            String namespaceUri = attributeNode.getNamespaceURI();
            if (namespaceUri==null) {
                namespaceUri = "";
            }
            
            if (XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(namespaceUri)) {
                /* (xsi attributes get ignored in our model) */
            }
            else {
                Attribute<?> attribute = get(localName, namespaceUri, true);
                if (attribute == null) {
                    /* Foreign attribute, so create new */
                    attribute = new ForeignAttribute(owner, localName, namespaceUri);
                    attributes.add(attribute);
                }
                /* Load value into attribute */
                attribute.load(element, attributeNode, context);
            }
        }
    }

    /**
     * Removed all attributes (children) from this container.
     */
    public void clear() {
        attributes.clear();
    }

    /**
     * Returns true if this container contains specified attribute; false
     * otherwise.
     * 
     * @param name attribute's name
     * @return true if this container contains specified attribute; false
     *         otherwise
     */
    public boolean contains(String name) {
        for (final Attribute<?> attribute : attributes) {
            if (attribute.getLocalName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets attribute at given index.
     * 
     * @param index index of requested attribute
     * @return attribute at given index
     */
    public Attribute<?> get(int index) {
        return attributes.get(index);
    }

    /**
     * Gets attribute with given local name in no namespace.
     * 
     * @param localName name of requested attributes
     * @return requested attribute 
     * @throws QtiAttributeException if attribute is not found
     */
    public Attribute<?> get(String localName) {
        return get(localName, "", false);
    }
    
    /**
     * Gets attribute with given local name and namespace URI
     * 
     * @param localName name of requested attribute
     * @return requested attribute
     * @throws QtiAttributeException if attribute is not found
     */
    public Attribute<?> get(String localName, String namespaceUri) {
        return get(localName, namespaceUri, false);
    }

    /**
     * Gets attribute with given local name and namespace URI or null (if attribute is not found).
     * Silent parameter is useful for support of unknown attributes.
     * 
     * @param localName name of requested attribute
     * @param silent if exception should be thrown in case attribute is not
     *            found
     * @return attribute with given name
     * @throws QtiAttributeException if silent is false and if attribute is not
     *             found
     */
    private Attribute<?> get(String localName, String namespaceUri, boolean silent) {
        ConstraintUtilities.ensureNotNull(localName, "localName");
        ConstraintUtilities.ensureNotNull(namespaceUri, "namespaceUri");
        for (final Attribute<?> attribute : attributes) {
            if (attribute.getLocalName().equals(localName) && attribute.getNamespaceUri().equals(namespaceUri)) {
                return attribute;
            }
        }

        if (silent) {
            return null;
        }
        throw new QtiAttributeException("Cannot find attribute with namespace '" + namespaceUri + "' and local name '" + localName
                + "' in Node with XPath " + owner.computeXPath());
    }

    @Override
    public Iterator<Attribute<?>> iterator() {
        return attributes.iterator();
    }

    @Override
    public void validate(ValidationContext context) {
        for (final Attribute<?> attribute : attributes) {
            attribute.validate(context);
        }
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public BaseTypeAttribute getBaseTypeAttribute(String name) throws QtiAttributeException {
        return (BaseTypeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public BooleanAttribute getBooleanAttribute(String name) throws QtiAttributeException {
        return (BooleanAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public CardinalityAttribute getCardinalityAttribute(String name) throws QtiAttributeException {
        return (CardinalityAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public DateAttribute getDateAttribute(String name) throws QtiAttributeException {
        return (DateAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public DurationAttribute getDurationAttribute(String name) throws QtiAttributeException {
        return (DurationAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public FloatAttribute getFloatAttribute(String name) throws QtiAttributeException {
        return (FloatAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public FloatMultipleAttribute getFloatMultipleAttribute(String name) throws QtiAttributeException {
        return (FloatMultipleAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public IdentifierAttribute getIdentifierAttribute(String name) throws QtiAttributeException {
        return (IdentifierAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public IdentifierMultipleAttribute getIdentifierMultipleAttribute(String name) throws QtiAttributeException {
        return (IdentifierMultipleAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public IntegerAttribute getIntegerAttribute(String name) throws QtiAttributeException {
        return (IntegerAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public CoordsAttribute getCoordsAttribute(String name) throws QtiAttributeException {
        return (CoordsAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public LongAttribute getLongAttribute(String name) throws QtiAttributeException {
        return (LongAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public NavigationModeAttribute getNavigationModeAttribute(String name) throws QtiAttributeException {
        return (NavigationModeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public RoundingModeAttribute getRoundingModeAttribute(String name) throws QtiAttributeException {
        return (RoundingModeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public SessionStatusAttribute getSessionStatusAttribute(String name) throws QtiAttributeException {
        return (SessionStatusAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public ShapeAttribute getShapeAttribute(String name) throws QtiAttributeException {
        return (ShapeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public SingleValueAttribute getSingleValueAttribute(String name) throws QtiAttributeException {
        return (SingleValueAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public StringAttribute getStringAttribute(String name) throws QtiAttributeException {
        return (StringAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public StringMultipleAttribute getStringMultipleAttribute(String name) throws QtiAttributeException {
        return (StringMultipleAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public SubmissionModeAttribute getSubmissionModeAttribuye(String name) throws QtiAttributeException {
        return (SubmissionModeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public TestFeedbackAccessAttribute getTestFeedbackAttribute(String name) throws QtiAttributeException {
        return (TestFeedbackAccessAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public ToleranceModeAttribute getToleranceModeAttribute(String name) throws QtiAttributeException {
        return (ToleranceModeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public UriAttribute getUriAttribute(String name) throws QtiAttributeException {
        return (UriAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public ViewMultipleAttribute getViewMultipleAttribute(String name) throws QtiAttributeException {
        return (ViewMultipleAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public VisibilityModeAttribute getVisibilityModeAttribute(String name) throws QtiAttributeException {
        return (VisibilityModeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public OrientationAttribute getOrientationAttribute(String name) throws QtiAttributeException {
        return (OrientationAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public ParamTypeAttribute getParamTypeAttribute(String name) throws QtiAttributeException {
        return (ParamTypeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public TableCellScopeAttribute getTableCellScopeAttribute(String name) throws QtiAttributeException {
        return (TableCellScopeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public TextFormatAttribute getTextFormatAttribute(String name) throws QtiAttributeException {
        return (TextFormatAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QtiAttributeException if attribute is not found
     */
    public VariableReferenceIdentifierAttribute getVariableReferenceIdentifierAttribute(String name) throws QtiAttributeException {
        return (VariableReferenceIdentifierAttribute) get(name);
    }
}
