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
import uk.ac.ed.ph.jqtiplus.exception.QTIAttributeException;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.validation.Validatable;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Container for all attributes of one node.
 * 
 * @author Jiri Kajaba
 */
public class AttributeList implements Validatable, Iterable<Attribute> {

    private static final long serialVersionUID = 4537124098886951888L;

    /**
     * Separator between two attributes (valid only if attributes are printed on
     * one line).
     */
    public static final String ATTRIBUTES_SEPARATOR = " ";

    /** Line wrap limit while printing attributes. */
    public static final int LINE_WRAP_LIMIT = 100;

    /** Logger. */
    private static Logger logger = LoggerFactory.getLogger(AttributeList.class);

    /** Parent (node) of this container. */
    private final XmlNode parent;

    /** Children (attributes) of this container. */
    private final List<Attribute> attributes;

    /**
     * Constructs container.
     * 
     * @param parent parent of constructed container
     */
    public AttributeList(XmlNode parent) {
        ConstraintUtilities.ensureNotNull(parent);
        this.parent = parent;
        this.attributes = new ArrayList<Attribute>();
    }

    /**
     * Gets parent of this container.
     * 
     * @return parent of this container
     */
    public XmlNode getParent() {
        return parent;
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
     * @throws QTIAttributeException if container already contains attribute
     *             with same name
     */
    public void add(Attribute attribute) throws QTIAttributeException {
        for (final Attribute child : attributes) {
            if (child.getName().equals(attribute.getName())) {
                final QTIAttributeException ex = new QTIAttributeException("Duplicate attribute name: " + attribute.computeXPath());
                logger.error(ex.getMessage());
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
    public void remove(Attribute attribute) {
        for (final Attribute child : attributes) {
            if (child.getName().equals(attribute.getName())) {
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
     * @throws QTIAttributeException if container already contains attribute
     *             with same name
     */
    public void add(int index, Attribute attribute) throws QTIAttributeException {
        attributes.add(index, attribute);
    }

    /**
     * Loads attribute's values from given source node.
     * If there is unsupported (unknown) attribute, it creates new optional
     * StringAttribute with set unsupported flag.
     * 
     * @param element source node
     */
    public void load(Element element, LoadingContext context) {
        for (int i = 0; i < attributes.size(); i++) {
            final Attribute attribute = attributes.get(i);
            if (attribute.isSupported()) {
                attribute.load(element, (String) null, context);
            }
            else {
                attributes.remove(i);
            }
        }

        for (int i = 0; i < element.getAttributes().getLength(); i++) {
            Attribute attribute = get(element.getAttributes().item(i).getNodeName(), true);
            if (attribute == null) {
                attribute = new StringAttribute(parent, element.getAttributes().item(i).getNodeName(), null);
                ((StringAttribute) attribute).setSupported(false);
                attributes.add(attribute);
            }
            attribute.load(element, element.getAttributes().item(i), context);
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
        for (final Attribute attribute : attributes) {
            if (attribute.getName().equals(name)) {
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
    public Attribute get(int index) {
        return attributes.get(index);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public Attribute get(String name) throws QTIAttributeException {
        return get(name, false);
    }

    /**
     * Gets attribute with given name or null (if attribute is not found).
     * Silent parameter is useful for support of unknown attributes.
     * 
     * @param name name of requested attribute
     * @param silent if exception should be thrown in case attribute is not
     *            found
     * @return attribute with given name
     * @throws QTIAttributeException if silent is false and if attribute is not
     *             found
     */
    private Attribute get(String name, boolean silent) throws QTIAttributeException {
        for (final Attribute attribute : attributes) {
            if (attribute.getName().equals(name)) {
                return attribute;
            }
        }

        if (silent) {
            return null;
        }
        final String xPath = parent.computeXPath() + "/@" + name;
        throw new QTIAttributeException("Cannot find attribute: " + xPath);
    }

    @Override
    public Iterator<Attribute> iterator() {
        return attributes.iterator();
    }

    /**
     * Prints attributes into string.
     * 
     * @param depth left indent (used only if printed line is too long)
     * @param printDefaultValues if true, prints all attributes; if false,
     *            prints only attributes with not default values
     * @return printed attributes
     */
    public String toXmlString(int depth, boolean printDefaultValues) {
        int length = 0;

        final List<String> strings = new ArrayList<String>();

        for (final Attribute attribute : attributes) {
            final String string = attribute.toXmlString(printDefaultValues);
            if (string.length() > 0) {
                length += string.length();
                strings.add(string);
            }
        }

        final StringBuilder builder = new StringBuilder();

        for (final String string : strings) {
            if (length < LINE_WRAP_LIMIT) {
                builder.append(ATTRIBUTES_SEPARATOR);
            }
            else {
                builder.append(XmlNode.NEW_LINE + AbstractNode.getIndent(depth) + XmlNode.INDENT);
            }

            builder.append(string);
        }

        return builder.toString();
    }

    @Override
    public void validate(ValidationContext context, AbstractValidationResult result) {
        for (final Attribute attribute : attributes) {
            attribute.validate(context, result);
        }
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public BaseTypeAttribute getBaseTypeAttribute(String name) throws QTIAttributeException {
        return (BaseTypeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public BooleanAttribute getBooleanAttribute(String name) throws QTIAttributeException {
        return (BooleanAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public CardinalityAttribute getCardinalityAttribute(String name) throws QTIAttributeException {
        return (CardinalityAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public DateAttribute getDateAttribute(String name) throws QTIAttributeException {
        return (DateAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public DurationAttribute getDurationAttribute(String name) throws QTIAttributeException {
        return (DurationAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public FloatAttribute getFloatAttribute(String name) throws QTIAttributeException {
        return (FloatAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public FloatMultipleAttribute getFloatMultipleAttribute(String name) throws QTIAttributeException {
        return (FloatMultipleAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public IdentifierAttribute getIdentifierAttribute(String name) throws QTIAttributeException {
        return (IdentifierAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public IdentifierMultipleAttribute getIdentifierMultipleAttribute(String name) throws QTIAttributeException {
        return (IdentifierMultipleAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public IntegerAttribute getIntegerAttribute(String name) throws QTIAttributeException {
        return (IntegerAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public CoordsAttribute getCoordsAttribute(String name) throws QTIAttributeException {
        return (CoordsAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public LongAttribute getLongAttribute(String name) throws QTIAttributeException {
        return (LongAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public NavigationModeAttribute getNavigationModeAttribute(String name) throws QTIAttributeException {
        return (NavigationModeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public RoundingModeAttribute getRoundingModeAttribute(String name) throws QTIAttributeException {
        return (RoundingModeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public SessionStatusAttribute getSessionStatusAttribute(String name) throws QTIAttributeException {
        return (SessionStatusAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public ShapeAttribute getShapeAttribute(String name) throws QTIAttributeException {
        return (ShapeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public SingleValueAttribute getSingleValueAttribute(String name) throws QTIAttributeException {
        return (SingleValueAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public StringAttribute getStringAttribute(String name) throws QTIAttributeException {
        return (StringAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public StringMultipleAttribute getStringMultipleAttribute(String name) throws QTIAttributeException {
        return (StringMultipleAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public SubmissionModeAttribute getSubmissionModeAttribuye(String name) throws QTIAttributeException {
        return (SubmissionModeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public TestFeedbackAccessAttribute getTestFeedbackAttribute(String name) throws QTIAttributeException {
        return (TestFeedbackAccessAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public ToleranceModeAttribute getToleranceModeAttribute(String name) throws QTIAttributeException {
        return (ToleranceModeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public UriAttribute getUriAttribute(String name) throws QTIAttributeException {
        return (UriAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public ViewMultipleAttribute getViewMultipleAttribute(String name) throws QTIAttributeException {
        return (ViewMultipleAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public VisibilityModeAttribute getVisibilityModeAttribute(String name) throws QTIAttributeException {
        return (VisibilityModeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public OrientationAttribute getOrientationAttribute(String name) throws QTIAttributeException {
        return (OrientationAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public ParamTypeAttribute getParamTypeAttribute(String name) throws QTIAttributeException {
        return (ParamTypeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public TableCellScopeAttribute getTableCellScopeAttribute(String name) throws QTIAttributeException {
        return (TableCellScopeAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public TextFormatAttribute getTextFormatAttribute(String name) throws QTIAttributeException {
        return (TextFormatAttribute) get(name);
    }

    /**
     * Gets attribute with given name.
     * 
     * @param name name of requested attribute
     * @return attribute with given name
     * @throws QTIAttributeException if attribute is not found
     */
    public VariableReferenceIdentifierAttribute getVariableReferenceIdentifierAttribute(String name) throws QTIAttributeException {
        return (VariableReferenceIdentifierAttribute) get(name);
    }
}
