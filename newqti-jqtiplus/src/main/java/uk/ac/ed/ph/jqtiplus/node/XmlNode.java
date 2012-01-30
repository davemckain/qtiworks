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
import uk.ac.ed.ph.jqtiplus.group.NodeGroupList;
import uk.ac.ed.ph.jqtiplus.validation.Validatable;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlSourceLocationInformation;

import org.w3c.dom.Element;

/**
 * Parent of all xml nodes.
 * 
 * @author Jiri Kajaba
 */
public interface XmlNode extends Validatable {

    /** New line string. Depends on OS settings. */
    static final String NEW_LINE = System.getProperty("line.separator");

    /** Default indent. */
    static final String INDENT = "  ";

    XmlSourceLocationInformation getSourceLocation();

    /**
     * Gets parent of this node or null (if node is root; for example AssessmentTest).
     * <p>
     * While testing some nodes (for example expressions) don't have properly set parent, but it is usable only for testing. (Some nodes cannot exists without
     * parent even for testing).
     * 
     * @return parent of this node or null (if node is root; for example AssessmentTest)
     */
    XmlNode getParent();

    /**
     * Gets root of this node or node itself (if node is root; for example AssessmentTest).
     * <p>
     * While testing some nodes (for example expressions) don't have properly set parent, but it is usable only for testing. (Some nodes cannot exists without
     * parent even for testing).
     * 
     * @return root of this node or node itself (if node is root; for example AssessmentTest)
     */
    RootObject getRootObject();

    <E extends RootObject> E getRootObject(Class<E> rootClass);

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
     * Loads this node from given DOM source {@link Element}.
     */
    void load(Element sourceElement, LoadingContext context);

    /**
     * Prints this node and all its children into string.
     * <p>
     * Calls toXmlString(0, false).
     * 
     * @return xml string of this node and all its children
     * @see #toXmlString(int, boolean)
     */
    String toXmlString();

    /**
     * Prints this node and all its children into string.
     * 
     * @param depth indent (0 = no indent)
     * @param printDefaultAttributes whether print attribute's default values
     * @return xml string of this node and all its children
     * @see #toXmlString()
     */
    String toXmlString(int depth, boolean printDefaultAttributes);
    
    /**
     * Gets the XML local name of this node.
     */
    String getLocalName();

    /**
     * Gets the XML namespace URI for this Node.
     */
    String getNamespaceUri();

    /**
     * Gets QTI class name of this node (as used in the specification).
     * This always returns the same as {@link #getLocalName()}.
     */
    String getClassTag();

    /**
     * Returns a partial XPath expression representing "this" Node. Normally, this would just be: <tt>classTag[position]</tt> but subclasses can override this
     * to give more useful information if required.
     * <p>
     * This property is mutable while the Node tree is being modified. It may be assumed to be constant once the tree has been finalised.
     */
    String computeXPathComponent();

    /**
     * Computes and returns a pseudo XPath expression that can be used to navigate to this Node.
     * (The expression will probably be over-verbose!)
     * 
     * NOTE: This uses the form {nsURI}localName for non-QTI elements, so is not
     * a "proper" XPath.
     */
    String computeXPath();

    /**
     * This is a convenience method to allow easy determination of whether a node has any children.
     * 
     * @return true if the node has any children, false if the node has no children.
     */
    boolean hasChildNodes();

}
