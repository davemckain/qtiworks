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
package uk.ac.ed.ph.jqtiplus.node.accessibility;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.group.accessibility.AccessibilityNode;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer;
import uk.ac.ed.ph.jqtiplus.types.Stringifiable;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * FIXME: Document this type
 *
 * @author Zack Pierce
 */
public abstract class SingleEnumerateElement<V extends Enum<V> & Stringifiable> extends AbstractNode implements
        AccessibilityNode {

    private static final long serialVersionUID = -8448200082436631640L;

    private V value;
    private final V defaultValue;

    public SingleEnumerateElement(final QtiNode parent, final String qtiClassName, final V defaultValue) {
        super(parent, qtiClassName);
        this.defaultValue = defaultValue;
    }

    public abstract V parseQtiString(String rawValue);

    /*
     * (non-Javadoc)
     *
     * @see uk.ac.ed.ph.jqtiplus.node.AbstractNode#load(org.w3c.dom.Element, uk.ac.ed.ph.jqtiplus.node.LoadingContext)
     */
    @Override
    public final void load(final Element sourceElement, final LoadingContext context) {
        super.load(sourceElement, context);
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.ac.ed.ph.jqtiplus.node.AbstractNode#loadChildren(org.w3c.dom.Element,
     * uk.ac.ed.ph.jqtiplus.node.LoadingContext)
     */
    @Override
    protected void loadChildren(final Element element, final LoadingContext context) {
        final String rawValue = element.getTextContent();
        if (rawValue != null) {
            try {
                this.value = parseQtiString(rawValue.trim());
            }
            catch (final QtiParseException ex) {
                this.value = null;
                context.modelBuildingError(ex, element);
            }
        }
        else {
            this.value = null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.ac.ed.ph.jqtiplus.node.AbstractNode#loadAttributes(org.w3c.dom.Element,
     * uk.ac.ed.ph.jqtiplus.node.LoadingContext)
     */
    @Override
    protected void loadAttributes(final Element element, final LoadingContext context) {
        /* No attributes to load */
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.ac.ed.ph.jqtiplus.node.AbstractNode#hasChildNodes()
     */
    @Override
    public boolean hasChildNodes() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * uk.ac.ed.ph.jqtiplus.node.AbstractNode#fireBodySaxEvents(uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer)
     */
    @Override
    protected void fireBodySaxEvents(final QtiSaxDocumentFirer qtiSaxDocumentFirer) throws SAXException {
        qtiSaxDocumentFirer.fireText(value.toQtiString());
    }

    /**
     * Returns true if this attribute's value has been explicitly set.
     * <p>
     * This is equivalent to {@link #getValue()} returning non-null.
     */
    public boolean isSet() {
        return value != null;
    }

    /**
     * Returns the default value of the attribute, which is the effective value used if the
     * attribute has not been explicitly set. This will be null if there is no default value.
     * The result of this should be assumed immutable.
     */
    public V getDefaultValue() {
        return defaultValue;
    }

    /**
     * Gets the explicitly-set value of this element, returning null if this
     * element has not been explicitly set. The value should be assumed immutable.
     * <p>
     * This will return null if the value has not been explicitly set. This is
     * different from the original JQTI behaviour!
     *
     * @see #getComputedValue()
     *
     * @return value of attribute
     */
    public V getValue() {
        return value;
    }

    /**
     * Gets the "computed" value of this element, which is defined to be the
     * explicitly-set value (if not null), or the default value.
     * <p>
     * Note that if there is no default value, then this will return null.
     * <p>
     */
    public V getComputedValue() {
        return value != null ? value : defaultValue;
    }

    /**
     * Sets the value of this element.
     *
     * @param value new value of element, which may be null to indicate that the element's
     * value should be unset.
     */
    public void setValue(final V value) {
        this.value = value;
    }

}
