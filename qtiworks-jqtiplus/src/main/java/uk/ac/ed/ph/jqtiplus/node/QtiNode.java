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

import uk.ac.ed.ph.jqtiplus.attribute.AttributeList;
import uk.ac.ed.ph.jqtiplus.group.NodeGroupList;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlSourceLocationInformation;

import java.io.Serializable;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Base interface for all "nodes" in the QTI object model.
 *
 * @author Jiri Kajaba (original XmlNode/XmlObject interfaces in JQTI)
 * @author David McKain (refactored)
 */
public interface QtiNode extends Serializable, Iterable<QtiNode> {

    /**
     * Gets parent of this node, or null if this is a {@link RootNode}
     */
    QtiNode getParent();

    /**
     * Finds the nearest ancestor of this Node having the given class
     * (not counting this Node itself). Returns the ancestor if found,
     * otherwise null.
     *
     * @see #searchNearestAncestorOrSelf(Class)
     */
    <E extends QtiNode> E searchNearestAncestor(final Class<E> ancestorClass);

    /**
     * Returns this Node if it is of the given ancestorClass. Otherwise it
     * finds the nearest ancestor of this Node having the given class
     *
     * @see #searchNearestAncestor(Class)
     */
    <E extends QtiNode> E searchNearestAncestorOrSelf(final Class<E> ancestorClass);

    /**
     * Gets root of this node, returning the node itself if it is already
     * a {@link RootNode}
     */
    RootNode getRootNode();

    /**
     * Gets root of this node, expecting it to be the given subclass of {@link RootNode},
     * or null if the root of this node is not of the requested type.
     */
    <E extends RootNode> E getRootNode(Class<E> rootClass);

    /**
     * Gets list (container) of all attributes.
     *
     * @return list (container) of all attributes of this node
     */
    AttributeList getAttributes();

    /**
     * Gets list (container) of all child groups.
     * <p>
     * For example AssessmentTest contains (one) testPart group. And this (one) group contains all testParts.
     * <p>
     * Group approach is useful for defining some rules to all testParts (for example required number of testParts in AssessmentTest).
     *
     * @return list (container) of all child group
     */
    NodeGroupList getNodeGroups();

    /**
     * Gets QTI class name of this node (as used in the specification).
     */
    String getQtiClassName();

    /**
     * Provides contextual information about the Node when it has been loaded from XML,
     * otherwise returns null.
     */
    XmlSourceLocationInformation getSourceLocation();

    /**
     * Returns a partial XPath expression representing "this" Node. Normally, this would just be: <tt>qtiClassName[position]</tt> but subclasses can override this
     * to give more useful information if required.
     * <p>
     * This property is mutable while the Node tree is being modified. It may be assumed to be constant once the tree has been finalised.
     */
    String computeXPathComponent();

    /**
     * Computes and returns a pseudo XPath expression that can be used to navigate to this Node.
     * (The expression will probably be over-verbose!)
     *
     * NOTE: This uses the form {nsURI}qtiClassName for non-QTI elements, so is not
     * a "proper" XPath.
     */
    String computeXPath();

    /**
     * This is a convenience method to allow easy determination of whether a node has any children.
     *
     * @return true if the node has any children, false if the node has no children.
     */
    boolean hasChildNodes();

    /**
     * Loads this node from given DOM source {@link Element}.
     */
    void load(Element sourceElement, LoadingContext context);

    /**
     * Validate this {@link QtiNode} and descends downwards.
     */
    void validate(final ValidationContext context);

    /** Callback used to serialize this Nodes. Do not call directly. */
    void fireSaxEvents(QtiSaxDocumentFirer qtiSaxDocumentFirer)
            throws SAXException;

}
