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
package uk.ac.ed.ph.jqtiplus.node;

import uk.ac.ed.ph.jqtiplus.attribute.AttributeList;
import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.group.NodeGroup;
import uk.ac.ed.ph.jqtiplus.group.NodeGroupList;
import uk.ac.ed.ph.jqtiplus.node.content.BodyElement;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.BranchRule;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxFiringContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.AttributeValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceReader;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlSourceLocationInformation;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Parent of all xml nodes.
 *
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
public abstract class AbstractNode implements XmlNode {

    private static final long serialVersionUID = -1930032796629418277L;

    /** Parent of this node. */
    private final XmlNode parent;

    private final String localName;

    /** Attributes of this node. */
    private final AttributeList attributes;

    /** Node groups of this node (contains all its children). */
    private final NodeGroupList nodeGroups;

    /** Information about the location of this Node in the original source XML, if loaded that way */
    private XmlSourceLocationInformation sourceLocation;

    public AbstractNode(final XmlNode parent, final String localName) {
        this.parent = parent;
        this.localName = localName;
        this.attributes = new AttributeList(this);
        this.nodeGroups = new NodeGroupList(this);
        this.sourceLocation = null;
    }

    @Override
    public XmlNode getParent() {
        return parent;
    }

    @Override
    public final String getLocalName() {
        return localName;
    }

    @Override
    public final String getClassTag() {
        return localName;
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
    public RootObject getRootObject() {
        XmlNode node = this;
        while (node.getParent() != null) {
            node = node.getParent();
        }
        return (RootObject) node;
    }

    @Override
    public <E extends RootObject> E getRootObject(final Class<E> rootClass) {
        final XmlNode root = getRootObject();
        E result = null;
        if (rootClass.isInstance(root)) {
            result = rootClass.cast(root);
        }
        return result;
    }

    @Override
    public boolean hasChildNodes() {
        for (final NodeGroup<?> nodeGroup : getNodeGroups()) {
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
    public Iterator<XmlNode> iterator() {
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
    public void fireSaxEvents(final QtiSaxFiringContext saxFiringContext) throws SAXException {
        saxFiringContext.fireStartQtiElement(this);
        fireBodySaxEvents(saxFiringContext);
        saxFiringContext.fireEndQtiElement(this);
    }

    protected void fireBodySaxEvents(final QtiSaxFiringContext saxFiringContext) throws SAXException {
        for (final NodeGroup<?> nodeGroup : nodeGroups) {
            for (final XmlNode childNode : nodeGroup.getChildren()) {
                childNode.fireSaxEvents(saxFiringContext);
            }
        }
    }

    @Override
    public String computeXPathComponent() {
        final XmlNode parentNode = getParent();
        int position = 1;
        if (parentNode != null) {
            SEARCH: for (final NodeGroup<?> nodeGroup : parentNode.getNodeGroups()) {
                position = 1;
                for (final XmlNode child : nodeGroup.getChildren()) {
                    if (child == this) {
                        break SEARCH;
                    }
                    if (getClassTag().equals(child.getClassTag())) {
                        position++;
                    }
                }
            }
            return getClassTag() + "[" + position + "]";
        }
        return getClassTag();
    }

    @Override
    public final String computeXPath() {
        final StringBuilder pathBuilder = new StringBuilder();
        buildXPath(pathBuilder, this);
        return pathBuilder.toString();
    }

    private void buildXPath(final StringBuilder pathBuilder, final XmlNode node) {
        if (pathBuilder.length() > 0) {
            pathBuilder.insert(0, "/");
        }
        if (node != null) {
            pathBuilder.insert(0, node.computeXPathComponent());
            buildXPath(pathBuilder, node.getParent());
        }
    }

    @Override
    public void validate(final ValidationContext context) {
        validateAttributes(context);
        validateChildren(context);
    }

    /**
     * Validates attributes of this node.
     */
    protected void validateAttributes(final ValidationContext context) {
        attributes.validate(context);
    }

    /**
     * Validates children (body) of this node.
     */
    protected void validateChildren(final ValidationContext context) {
        for (int i = 0; i < nodeGroups.size(); i++) {
            final NodeGroup<?> node = nodeGroups.get(i);
            for (final XmlNode child : node.getChildren()) {
                child.validate(context);
            }
        }
    }

    /** Helper method to validate a unique identifier (definition) attribute */
    protected void validateUniqueIdentifier(final AbstractValidationResult result, final IdentifierAttribute identifierAttribute, final Identifier identifier) {
        if (identifier != null) {
            if (getRootObject(AssessmentTest.class) != null && BranchRule.isSpecial(identifier)) {
                result.add(new AttributeValidationError(identifierAttribute, "Cannot uses this special target as identifier: " + identifierAttribute));
            }
            if (!validateUniqueIdentifier(getRootObject(), identifier)) {
                result.add(new AttributeValidationError(identifierAttribute, "Duplicate identifier: " + identifierAttribute));
            }
        }
    }

    private boolean validateUniqueIdentifier(final XmlNode parent, final Object identifier) {
        if (parent != this && parent instanceof UniqueNode) {
            final Object parentIdentifier = ((UniqueNode<?>) parent).getIdentifier();
            if (identifier.equals(parentIdentifier)) {
                return false;
            }
        }

        final NodeGroupList groups = parent.getNodeGroups();
        for (int i = 0; i < groups.size(); i++) {
            final NodeGroup<?> group = groups.get(i);
            for (final XmlNode child : group.getChildren()) {
                if (!validateUniqueIdentifier(child, identifier)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Prints indent into xml string.
     *
     * @param depth depth in xml tree (root = 0)
     * @return xml string with printed indent
     */
    public static String getIndent(final int depth) {
        final StringBuilder builder = new StringBuilder();
        appendIndent(builder, depth);
        return builder.toString();
    }

    /**
     * Prints indent into xml string.
     *
     * @param depth depth in xml tree (root = 0)
     */
    public static void appendIndent(final StringBuilder builder, final int depth) {
        for (int i = 0; i < depth; i++) {
            builder.append(INDENT);
        }
    }

    /** (This used to be used to turn Nodes into XML, but it's now only required for generating pseudo XPaths */
    @ToRefactor
    protected static String escapeForXmlString(final String text, final boolean asAttribute) {
        final StringBuilder builder = new StringBuilder();
        for (final char c : text.toCharArray()) {
            switch (c) {
                case '<':
                    builder.append("&lt;");
                    break;

                case '>':
                    builder.append("&gt;");
                    break;

                case '&':
                    builder.append("&amp;");
                    break;

                case '"':
                    if (asAttribute) {
                        /* (We're always uk.ac.ed.ph.jqtiplus.writing attributes within double-quotes so need to escape in this case) */
                        builder.append("&quot;");
                    }
                    else {
                        builder.append('"');
                    }
                    break;

                default:
                    builder.append(c);
                    break;
            }
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "<" + getClassTag() + ">@" + hashCode()
                + "(xPath=" + computeXPath()
                + ",location=" + sourceLocation
                + ")";
    }

    /**
     * Read-only iterator over all children of the current Node.
     */
    protected class ChildNodeIterator implements Iterator<XmlNode> {

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
            NodeGroup<?> currentGroup = nodeGroups.get(currentGroupIndex);
            List<? extends XmlNode> currentGroupChildren = currentGroup.getChildren();
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
        public XmlNode next() {
            XmlNode result;
            if (currentGroupIndex==-1) {
                throw new NoSuchElementException();
            }
            NodeGroup<?> currentGroup = nodeGroups.get(currentGroupIndex);
            List<? extends XmlNode> currentGroupChildren = currentGroup.getChildren();
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

        private int firstNonEmptyGroupIndex(int startIndex) {
            for (int searchIndex = startIndex; searchIndex < nodeGroups.size(); searchIndex++) {
                NodeGroup<?> group = nodeGroups.get(searchIndex);
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
