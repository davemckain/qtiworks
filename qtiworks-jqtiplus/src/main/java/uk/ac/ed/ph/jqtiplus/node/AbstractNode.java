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
package uk.ac.ed.ph.jqtiplus.node;

import uk.ac.ed.ph.jqtiplus.attribute.Attribute;
import uk.ac.ed.ph.jqtiplus.attribute.AttributeList;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.group.NodeGroup;
import uk.ac.ed.ph.jqtiplus.group.NodeGroupList;
import uk.ac.ed.ph.jqtiplus.node.content.BodyElement;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceReader;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlSourceLocationInformation;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Base implementation of {@link QtiNode}.
 *
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
public abstract class AbstractNode implements QtiNode {

    private static final long serialVersionUID = -1930032796629418277L;

    /** Parent of this node. */
    private final QtiNode parent;

    /** Name of this QTI class, as defined in the spec */
    private final String qtiClassName;

    /** Attributes of this node. */
    private final AttributeList attributes;

    /** Node groups of this node (contains all its children). */
    private final NodeGroupList nodeGroups;

    /** Information about the location of this Node in the original source XML, if loaded that way */
    private XmlSourceLocationInformation sourceLocation;

    public AbstractNode(final QtiNode parent, final String qtiClassName) {
        this.parent = parent;
        this.qtiClassName = qtiClassName;
        this.attributes = new AttributeList(this);
        this.nodeGroups = new NodeGroupList(this);
        this.sourceLocation = null;
    }

    @Override
    public QtiNode getParent() {
        return parent;
    }

    @Override
    public <E extends QtiNode> E searchNearestAncestor(final Class<E> ancestorClass) {
        QtiNode ancestor = getParent();
        while (ancestor!=null) {
            if (ancestorClass.isInstance(ancestor)) {
                return ancestorClass.cast(ancestor);
            }
            ancestor = ancestor.getParent();
        }
        return null;
    }

    @Override
    public <E extends QtiNode> E searchNearestAncestorOrSelf(final Class<E> ancestorClass) {
        if (ancestorClass.isInstance(this)) {
            return ancestorClass.cast(this);
        }
        return searchNearestAncestor(ancestorClass);
    }

    @Override
    public final String getQtiClassName() {
        return qtiClassName;
    }

    @Override
    public XmlSourceLocationInformation getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(final XmlSourceLocationInformation sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    @Override
    public AttributeList getAttributes() {
        return attributes;
    }

    @Override
    public RootNode getRootNode() {
        QtiNode node = this;
        while (node.getParent() != null) {
            node = node.getParent();
        }
        return (RootNode) node;
    }

    @Override
    public <E extends RootNode> E getRootNode(final Class<E> rootClass) {
        final QtiNode root = getRootNode();
        E result = null;
        if (rootClass.isInstance(root)) {
            result = rootClass.cast(root);
        }
        return result;
    }

    @Override
    public boolean hasChildNodes() {
        for (final NodeGroup<?,?> nodeGroup : getNodeGroups()) {
            if (nodeGroup.getChildren().size() > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public NodeGroupList getNodeGroups() {
        return nodeGroups;
    }

    /**
     * Provides a read-only iterates over all child Nodes.
     * <p>
     * This Node's children (via nodeGroups) MUST NOT be modified while this iterator is being used!
     * <p>
     * This is new in JQTI+, it used to be restricted to {@link BodyElement}, which did not
     * provide enough coverage of the model.
     */
    @Override
    public Iterator<QtiNode> iterator() {
        return new ChildNodeIterator();
    }

    @Override
    public void load(final Element sourceElement, final LoadingContext context) {
        /* Extract SAX Locator data stowed away by XmlResourceReader, if used */
        this.sourceLocation = XmlResourceReader.extractLocationInformation(sourceElement);

        /* Load attributes */
        loadAttributes(sourceElement, context);

        /* Load children */
        loadChildren(sourceElement, context);
    }

    /**
     * Loads all attributes from given xml source.
     *
     * @param element xml source
     */
    protected void loadAttributes(final Element element, final LoadingContext context) {
        attributes.load(element, context);
    }

    /**
     * Reads all children nodes and/or content from given xml source.
     * Every subclass must implement its own children nodes and/or content reading.
     * If there are no children nodes and content do nothing (you don't even need to override this method).
     *
     * @param element xml source
     */
    protected void loadChildren(final Element element, final LoadingContext context) {
        nodeGroups.load(element, context);
    }

    @Override
    public void fireSaxEvents(final QtiSaxDocumentFirer qtiSaxDocumentFirer) throws SAXException {
        qtiSaxDocumentFirer.fireStartQtiElement(this);
        fireBodySaxEvents(qtiSaxDocumentFirer);
        qtiSaxDocumentFirer.fireEndQtiElement(this);
    }

    protected void fireBodySaxEvents(final QtiSaxDocumentFirer qtiSaxDocumentFirer) throws SAXException {
        for (final NodeGroup<?,?> nodeGroup : nodeGroups) {
            for (final QtiNode childNode : nodeGroup.getChildren()) {
                childNode.fireSaxEvents(qtiSaxDocumentFirer);
            }
        }
    }

    @Override
    public String computeXPathComponent() {
        final QtiNode parentNode = getParent();
        int position = 1;
        if (parentNode != null) {
            SEARCH: for (final NodeGroup<?,?> nodeGroup : parentNode.getNodeGroups()) {
                position = 1;
                for (final QtiNode child : nodeGroup.getChildren()) {
                    if (child == this) {
                        break SEARCH;
                    }
                    if (qtiClassName.equals(child.getQtiClassName())) {
                        position++;
                    }
                }
            }
            return qtiClassName + "[" + position + "]";
        }
        return qtiClassName;
    }

    @Override
    public final String computeXPath() {
        final StringBuilder pathBuilder = new StringBuilder();
        buildXPath(pathBuilder, this);
        return pathBuilder.toString();
    }

    private void buildXPath(final StringBuilder pathBuilder, final QtiNode node) {
        if (pathBuilder.length() > 0) {
            pathBuilder.insert(0, "/");
        }
        if (node != null) {
            pathBuilder.insert(0, node.computeXPathComponent());
            buildXPath(pathBuilder, node.getParent());
        }
    }

    @Override
    public final void validate(final ValidationContext context) {
        /* Do basic checking on individual Attributes */
        attributes.validateBasic(context);

        /* Perform additional validation relevant to this Node */
        validateThis(context);

        /* Validate children */
        validateChildren(context);
    }

    /**
     * Subclasses should fill in to perform any additional validation
     * required for this {@link AbstractNode}. This might involve complex
     * validation of attributes, or extraction of data from other {@link AbstractNode}s
     * in the hierarchy.
     * <p>
     * The {@link Attribute}s of this Node will have had basic checks made before
     * this method has been called, so implementations don't have to repeat this
     * but should still treat attributes defensively.
     * <p>
     * Subclasses should be aware that children of this {@link AbstractNode}
     * will *not* have been validated at the time this method is called, so
     * validation logic should be suitably defensive.
     */
    protected void validateThis(@SuppressWarnings("unused") final ValidationContext context) {
        /* Subclasses should override as required */
    }

    /**
     * Validates children (body) of this node.
     * <p>
     * Subclasses should only override this if they have special children.
     */
    protected void validateChildren(final ValidationContext context) {
        for (int i = 0; i < nodeGroups.size(); i++) {
            final NodeGroup<?,?> nodeGroup = nodeGroups.get(i);
            nodeGroup.validate(context);
        }
    }

    /** Helper method to validate a unique identifier (definition) attribute */
    protected void validateUniqueIdentifier(final ValidationContext context, final IdentifierAttribute identifierAttribute, final Identifier identifier) {
        if (identifier != null) {
            if (!validateUniqueIdentifier(getRootNode(), identifier)) {
                context.fireAttributeValidationError(identifierAttribute, "Duplicate identifier " + identifier);
            }
        }
    }

    private boolean validateUniqueIdentifier(final QtiNode parentNode, final Object identifier) {
        if (parentNode != this && parentNode instanceof UniqueNode) {
            final Object parentIdentifier = ((UniqueNode<?>) parentNode).getIdentifier();
            if (identifier.equals(parentIdentifier)) {
                return false;
            }
        }

        final NodeGroupList groups = parentNode.getNodeGroups();
        for (int i = 0; i < groups.size(); i++) {
            final NodeGroup<?,?> group = groups.get(i);
            for (final QtiNode child : group.getChildren()) {
                if (!validateUniqueIdentifier(child, identifier)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "<" + qtiClassName + ">@" + hashCode()
                + "(xPath=" + computeXPath()
                + ",location=" + sourceLocation
                + ")";
    }

    /**
     * Read-only iterator over all children of the current Node.
     */
    protected class ChildNodeIterator implements Iterator<QtiNode> {

        private int currentGroupIndex;
        private int currentChildIndexInGroup;

        public ChildNodeIterator() {
            this.currentGroupIndex = firstNonEmptyGroupIndex(0);
            this.currentChildIndexInGroup = 0;
        }

        @Override
        public boolean hasNext() {
            if (currentGroupIndex==-1) {
                return false;
            }
            boolean hasNext;
            final NodeGroup<?,?> currentGroup = nodeGroups.get(currentGroupIndex);
            final List<? extends QtiNode> currentGroupChildren = currentGroup.getChildren();
            if (currentChildIndexInGroup < currentGroupChildren.size()) {
                /* Children left within current group */
                hasNext = true;
            }
            else {
                /* No more in this group. Are there any in later groups? */
                hasNext = (firstNonEmptyGroupIndex(currentGroupIndex + 1) != -1);
            }
            return hasNext;
        }

        @Override
        public QtiNode next() {
            QtiNode result;
            if (currentGroupIndex==-1) {
                throw new NoSuchElementException();
            }
            NodeGroup<?,?> currentGroup = nodeGroups.get(currentGroupIndex);
            List<? extends QtiNode> currentGroupChildren = currentGroup.getChildren();
            if (currentChildIndexInGroup < currentGroupChildren.size()) {
                /* Children left within current group */
                result = currentGroupChildren.get(currentChildIndexInGroup);
                currentChildIndexInGroup++;
            }
            else {
                /* No more in this group. Move to next non-empty group */
                currentGroupIndex = firstNonEmptyGroupIndex(currentGroupIndex + 1);
                if (currentGroupIndex==-1) {
                    throw new NoSuchElementException();
                }
                currentGroup = nodeGroups.get(currentGroupIndex);
                currentGroupChildren = currentGroup.getChildren();
                result = currentGroupChildren.get(0);
                currentChildIndexInGroup = 1;
            }
            return result;
        }

        private int firstNonEmptyGroupIndex(final int startIndex) {
            for (int searchIndex = startIndex; searchIndex < nodeGroups.size(); searchIndex++) {
                final NodeGroup<?,?> group = nodeGroups.get(searchIndex);
                if (group.getChildren().size() > 0) {
                    return searchIndex;
                }
            }
            return -1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Child nodes may not be modified via iterators");
        }
    }

}
