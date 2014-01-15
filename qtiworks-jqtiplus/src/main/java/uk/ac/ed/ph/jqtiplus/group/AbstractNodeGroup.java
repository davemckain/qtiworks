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
package uk.ac.ed.ph.jqtiplus.group;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception.QtiIllegalChildException;
import uk.ac.ed.ph.jqtiplus.exception.QtiModelException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.TextRun;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Partial implementation of {@link NodeGroup}
 *
 * @param <P> type of parent {@link QtiNode}
 * @param <C> type of child {@link QtiNode}
 *
 * @author Jiri Kajaba (original)
 * @author David McKain (refactored)
 */
public abstract class AbstractNodeGroup<P extends QtiNode, C extends QtiNode> implements NodeGroup<P,C> {

    private static final long serialVersionUID = 903238011893494959L;

    protected final P parent;
    protected final String name;
    protected final List<C> children;
    protected final int minimum;
    protected final Integer maximum;

    public AbstractNodeGroup(final P parent, final String name, final int minimum, final int maximum) {
        this(parent, name, minimum, Integer.valueOf(maximum));
    }

    public AbstractNodeGroup(final P parent, final String name, final int minimum, final Integer maximum) {
        Assert.notNull(parent);
        Assert.notNull(name);
        this.parent = parent;
        this.name = name;
        this.children = new ArrayList<C>();
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public P getParent() {
        return parent;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String computeXPath() {
        return getParent().computeXPath() + "/*[jqti:node-group(\"" + name + "\")]";
    }

    @Override
    public abstract boolean isComplexContent();

    @Override
    public abstract boolean supportsQtiClass(final String qtiClassName);

    @Override
    public Iterator<C> iterator() {
        return children.iterator();
    }

    @Override
    public List<C> getChildren() {
        return children;
    }

    @Override
    public int getMinimum() {
        return minimum;
    }

    @Override
    public Integer getMaximum() {
        return maximum;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean loadChildIfSupported(final Node childNode, final LoadingContext context) {
        boolean handled = false;
        try {
            final short nodeType = childNode.getNodeType();
            if (nodeType==Node.ELEMENT_NODE && supportsQtiClass(childNode.getLocalName())) {
                final C child = createChild((Element) childNode, context.getJqtiExtensionManager());
                child.load((Element) childNode, context);
                children.add(child);
                handled = true;
            }
            else if (nodeType==Node.TEXT_NODE && supportsQtiClass(TextRun.DISPLAY_NAME)) {
                final TextRun child = (TextRun) create(TextRun.DISPLAY_NAME);
                child.load((Text) childNode);
                children.add((C) child);
                handled = true;
            }
        }
        catch (final QtiModelException e) {
            context.modelBuildingError(e, childNode);
            handled = true;
        }
        return handled;
    }

    /**
     * @throws QtiIllegalChildException
     */
    @SuppressWarnings("unchecked")
    private C createChild(final Element childElement, final JqtiExtensionManager jqtiExtensionManager) {
        final String localName = childElement.getLocalName();
        C child;
        if ("customOperator".equals(localName)) {
            /* See if required operator has been registered and instantiate if it so */
            final ExpressionParent expressionParent = (ExpressionParent) parent;
            final String operatorClass = childElement.getAttribute("class");
            child = (C) jqtiExtensionManager.createCustomOperator(expressionParent, operatorClass);
        }
        else if ("customInteraction".equals(localName)) {
            final String interactionClass = childElement.getAttribute("class");
            child = (C) jqtiExtensionManager.createCustomInteraction(parent, interactionClass);
        }
        else {
            child = create(localName);
        }
        return child;
    }

    @Override
    public void validate(final ValidationContext context) {
        if (children.size() < minimum) {
            context.fireValidationError(parent, "Not enough children: " + name + ". Expected at least: " + minimum + ", but found: " + children.size());
        }
        if (maximum != null && children.size() > maximum.intValue()) {
            context.fireValidationError(parent, "Too many children: " + name + ". Allowed maximum: " + maximum + ", but found: " + children.size());
        }
        for (final QtiNode child : children) {
            child.validate(context);
        }
    }
}
