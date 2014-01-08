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
import uk.ac.ed.ph.jqtiplus.attribute.value.ComplexReferenceIdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.CoordsAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.DateAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.DurationAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.FloatAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.FloatOrVariableRefAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.FloatOrVariableRefMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerOrVariableRefAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.LongAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.SingleValueAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringOrVariableRefAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.UriAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QtiAttributeException;
import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

import java.io.Serializable;
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
public final class AttributeList implements Serializable, Iterable<Attribute<?>> {

    private static final long serialVersionUID = 4537124098886951888L;

    /** Owner (node) of these attributes. */
    private final QtiNode owner;

    /** Children (attributes) of this container. */
    private final List<Attribute<?>> attributes;

    public AttributeList(final QtiNode owner) {
        Assert.notNull(owner);
        this.owner = owner;
        this.attributes = new ArrayList<Attribute<?>>();
    }

    /**
     * Gets owner of this container.
     *
     * @return parent of this container
     */
    public QtiNode getOwner() {
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
    public void add(final Attribute<?> attribute) {
        for (final Attribute<?> child : attributes) {
            if (child.getLocalName().equals(attribute.getLocalName()) && child.getNamespaceUri().equals(attribute.getNamespaceUri())) {
                throw new QtiAttributeException("Duplicate attribute name: " + attribute.computeXPath());
            }
        }
        attributes.add(attribute);
    }

    /**
     * Removes given attribute from this container.
     *
     * @param attribute given attribute
     */
    public void remove(final Attribute<?> attribute) {
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
    public void add(final int index, final Attribute<?> attribute) {
        attributes.add(index, attribute);
    }

    /**
     * Loads attribute's values from given source {@link Element}
     * If there is a foreign attribute, it creates new optional
     * ForeignAttribute with its foreign property set.
     *
     * @param element source {@link Element} to load attributes from
     */
    public void load(final Element element, final LoadingContext context) {
        /* First clear existing attributes */
        for (int i = 0; i < attributes.size(); i++) {
            final Attribute<?> attribute = attributes.get(i);
            if (attribute instanceof ForeignAttribute) {
                /* Foreign attribute, so remove to add in again */
                attributes.remove(i);
            }
            else {
                /* Supported attribute, so clear for setting later */
                attribute.setValue(null);
            }
        }

        /* Set set values from element */
        for (int i = 0; i < element.getAttributes().getLength(); i++) {
            final Node attributeNode = element.getAttributes().item(i);
            final String localName = attributeNode.getLocalName();
            String namespaceUri = attributeNode.getNamespaceURI();
            if (namespaceUri==null) {
                namespaceUri = "";
            }

            if (XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(namespaceUri)) {
                /* (xsi attributes get ignored in our model) */
            }
            else {
                Attribute<?> attribute = get(localName, namespaceUri, true);
                if (attribute==null) {
                    /* Foreign attribute, so create new */
                    attribute = new ForeignAttribute(owner, localName, namespaceUri);
                    attributes.add(attribute);
                }
                /* Load value into attribute */
                final String attributeValue = attributeNode.getNodeValue();
                loadAttribute(attribute, element, attributeValue, context);
            }
        }
    }

    private static final <V> void loadAttribute(final Attribute<V> attribute, final Element element, final String stringValue, final LoadingContext context) {
        Assert.notNull(stringValue, "stringValue");
        V value = null;
        try {
            value = attribute.parseDomAttributeValue(stringValue);
        }
        catch (final QtiParseException ex) {
            context.modelBuildingError(ex, element);
        }
        attribute.setValue(value);
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
    public boolean contains(final String name) {
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
    public Attribute<?> get(final int index) {
        return attributes.get(index);
    }

    /**
     * Gets attribute with given local name in no namespace.
     *
     * @param localName name of requested attributes
     * @return requested attribute
     * @throws QtiAttributeException if attribute is not found
     */
    public Attribute<?> get(final String localName) {
        return get(localName, "", false);
    }

    /**
     * Gets attribute with given local name and namespace URI
     *
     * @param localName name of requested attribute
     * @return requested attribute
     * @throws QtiAttributeException if attribute is not found
     */
    public Attribute<?> get(final String localName, final String namespaceUri) {
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
    private Attribute<?> get(final String localName, final String namespaceUri, final boolean silent) {
        Assert.notNull(localName, "localName");
        Assert.notNull(namespaceUri, "namespaceUri");
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

    public void validateBasic(final ValidationContext context) {
        for (final Attribute<?> attribute : attributes) {
            attribute.validateBasic(context);
        }
    }

    public BaseTypeAttribute getBaseTypeAttribute(final String name) {
        return (BaseTypeAttribute) get(name);
    }

    public BooleanAttribute getBooleanAttribute(final String name) {
        return (BooleanAttribute) get(name);
    }

    public CardinalityAttribute getCardinalityAttribute(final String name) {
        return (CardinalityAttribute) get(name);
    }

    public DateAttribute getDateAttribute(final String name) {
        return (DateAttribute) get(name);
    }

    public DurationAttribute getDurationAttribute(final String name) {
        return (DurationAttribute) get(name);
    }

    public FloatAttribute getFloatAttribute(final String name) {
        return (FloatAttribute) get(name);
    }

    public ComplexReferenceIdentifierAttribute getComplexReferenceIdentifierAttribute(final String name) {
        return (ComplexReferenceIdentifierAttribute) get(name);
    }

    public IdentifierAttribute getIdentifierAttribute(final String name) {
        return (IdentifierAttribute) get(name);
    }

    public IdentifierMultipleAttribute getIdentifierMultipleAttribute(final String name) {
        return (IdentifierMultipleAttribute) get(name);
    }

    public IntegerAttribute getIntegerAttribute(final String name) {
        return (IntegerAttribute) get(name);
    }

    public CoordsAttribute getCoordsAttribute(final String name) {
        return (CoordsAttribute) get(name);
    }

    public LongAttribute getLongAttribute(final String name) {
        return (LongAttribute) get(name);
    }

    public NavigationModeAttribute getNavigationModeAttribute(final String name) {
        return (NavigationModeAttribute) get(name);
    }

    public RoundingModeAttribute getRoundingModeAttribute(final String name) {
        return (RoundingModeAttribute) get(name);
    }

    public SessionStatusAttribute getSessionStatusAttribute(final String name) {
        return (SessionStatusAttribute) get(name);
    }

    public ShapeAttribute getShapeAttribute(final String name) {
        return (ShapeAttribute) get(name);
    }

    public SingleValueAttribute getSingleValueAttribute(final String name) {
        return (SingleValueAttribute) get(name);
    }

    public StringAttribute getStringAttribute(final String name) {
        return (StringAttribute) get(name);
    }

    public StringMultipleAttribute getStringMultipleAttribute(final String name) {
        return (StringMultipleAttribute) get(name);
    }

    public SubmissionModeAttribute getSubmissionModeAttribuye(final String name) {
        return (SubmissionModeAttribute) get(name);
    }

    public TestFeedbackAccessAttribute getTestFeedbackAttribute(final String name) {
        return (TestFeedbackAccessAttribute) get(name);
    }

    public ToleranceModeAttribute getToleranceModeAttribute(final String name) {
        return (ToleranceModeAttribute) get(name);
    }

    public UriAttribute getUriAttribute(final String name) {
        return (UriAttribute) get(name);
    }

    public UriAttribute getUriAttribute(final String localName, final String namespaceUri) {
        return (UriAttribute) get(localName, namespaceUri);
    }

    public ViewMultipleAttribute getViewMultipleAttribute(final String name) {
        return (ViewMultipleAttribute) get(name);
    }

    public VisibilityModeAttribute getVisibilityModeAttribute(final String name) {
        return (VisibilityModeAttribute) get(name);
    }

    public OrientationAttribute getOrientationAttribute(final String name) {
        return (OrientationAttribute) get(name);
    }

    public ParamTypeAttribute getParamTypeAttribute(final String name) {
        return (ParamTypeAttribute) get(name);
    }

    public TableCellScopeAttribute getTableCellScopeAttribute(final String name) {
        return (TableCellScopeAttribute) get(name);
    }

    public TextFormatAttribute getTextFormatAttribute(final String name) {
        return (TextFormatAttribute) get(name);
    }

    public IntegerOrVariableRefAttribute getIntegerOrVariableRefAttribute(final String name) {
        return (IntegerOrVariableRefAttribute) get(name);
    }

    public FloatOrVariableRefAttribute getFloatOrVariableRefAttribute(final String name) {
        return (FloatOrVariableRefAttribute) get(name);
    }

    public FloatOrVariableRefMultipleAttribute getFloatOrVariableRefMultipleAttribute(final String name) {
        return (FloatOrVariableRefMultipleAttribute) get(name);
    }

    public StringOrVariableRefAttribute getStringOrVariableRefAttribute(final String name) {
        return (StringOrVariableRefAttribute) get(name);
    }
}
