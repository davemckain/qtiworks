/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
*/

package uk.ac.ed.ph.jqtiplus.group;

import uk.ac.ed.ph.jqtiplus.control.JQTIController;
import uk.ac.ed.ph.jqtiplus.control.ToRemove;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.XmlObject;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;


import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parent of all groups.
 * 
 * @author Jiri Kajaba
 */
public abstract class AbstractNodeGroup implements NodeGroup {
    
    private static final long serialVersionUID = 903238011893494959L;
    
    private final XmlNode parent;
    private final String name;
    private final List<String> supportedClasses;
    private final List<XmlNode> children;
    private final Integer minimum;
    private final Integer maximum;

    /**
     * Constructs group with maximum set to 1.
     * <p>
     * This is convenient constructor for group with only one child.
     *
     * @param parent parent of created group
     * @param name name of created group
     * @param required if true, minimum is set to 1, if false, minimum is set to 0
     */
    public AbstractNodeGroup(XmlNode parent, String name, boolean required) {
        this(parent, name, required ? 1 : 0, 1);
    }

    /**
     * Constructs group.
     *
     * @param parent parent of created group
     * @param name name of created group
     * @param minimum minimum required children of created group
     * @param maximum maximum allowed children of created group
     */
    public AbstractNodeGroup(XmlNode parent, String name, Integer minimum, Integer maximum) {
        ConstraintUtilities.ensureNotNull(parent);
        ConstraintUtilities.ensureNotNull(name);
        this.parent = parent;
        this.name = name;
        this.children = new ArrayList<XmlNode>();
        this.minimum = minimum;
        this.maximum = maximum;

        supportedClasses = new ArrayList<String>();
        supportedClasses.add(name);
    }

    public XmlNode getParent()
    {
        return parent;
    }

    public String getName()
    {
        return name;
    }
    
    public String computeXPath() {
        return getParent().computeXPath() + "/*[jqti:node-group(\"" + name + "\")]";
    }
    
    public boolean isGeneral()
    {
        return false;
    }

    public List<String> getAllSupportedClasses()
    {
        return supportedClasses;
    }

@ToRemove
//    public List<String> getCurrentSupportedClasses(int index)
//    {
//        return supportedClasses;
//    }

    /**
     * Gets first child or null.
     * This is convenient method for groups only with one child (maximum = 1).
     *
     * @return first child or null
     * @see #setChild
     */
    public XmlNode getChild()
    {
        return children.size() != 0 ? children.get(0) : null;
    }

    /**
     * Sets new child.
     * <p>
     * Removes all children from list first!
     * <p>
     * This method should be used only on groups with one child (maximum = 1),
     * because it clears list before setting new child.
     *
     * @param child new child
     * @see #getChild
     */
    protected void setChild(XmlNode child)
    {
        children.clear();
        if (child != null) children.add(child);
    }

    public List<XmlNode> getChildren()
    {
        return children;
    }

    public Integer getMinimum()
    {
        return minimum;
    }

    public Integer getMaximum()
    {
        return maximum;
    }

    public void load(JQTIController jqtiController, Element node) {
        NodeList childNodes = node.getChildNodes();
        for (int i=0; i<childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType()==Node.ELEMENT_NODE && getAllSupportedClasses().contains(childNode.getLocalName())) {
                XmlNode child = createChild(jqtiController, (Element) childNode);
                children.add(child);
                child.load(jqtiController, (Element) childNode);
            }
        }
    }
    
    protected XmlNode createChild(JQTIController jqtiController, Element childElement) {
        String localName = childElement.getLocalName();
        XmlNode result;
        if ("customOperator".equals(localName)) {
            /* See if required operator has been registered and instantiate if it so */
            ExpressionParent expressionParent = (ExpressionParent) getParent();
            String operatorClass = childElement.getAttribute("class");
            result = jqtiController.createCustomOperator(expressionParent, operatorClass);
        }
        else if ("customInteraction".equals(localName)) {
            XmlObject parentObject = (XmlObject) getParent();
            String interactionClass = childElement.getAttribute("class");
            result = jqtiController.createCustomInteraction(parentObject, interactionClass);
        }
        else {
            result = create(localName);
        }
        return result;
    }
    
    public String toXmlString(int depth, boolean printDefaultAttributes)
    {
        StringBuilder builder = new StringBuilder();

        for (XmlNode child : children)
            builder.append(child.toXmlString(depth, printDefaultAttributes));

        return builder.toString();
    }

    public ValidationResult validate(ValidationContext context)
    {
        ValidationResult result = new ValidationResult();

        if (minimum != null && children.size() < minimum)
            result.add(new ValidationError(parent, "Not enough children: " + name + ". Expected at least: " + minimum + ", but found: " + children.size()));

        if (maximum != null && children.size() > maximum)
            result.add(new ValidationError(parent, "Too many children: " + name + ". Allowed maximum: " + maximum + ", but found: " + children.size()));

        return result;
    }
}
