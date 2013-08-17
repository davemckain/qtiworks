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

import uk.ac.ed.ph.jqtiplus.exception.QtiIllegalChildException;
import uk.ac.ed.ph.jqtiplus.group.expression.ExpressionGroup;
import uk.ac.ed.ph.jqtiplus.group.test.TestPartGroup;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

import java.io.Serializable;
import java.util.List;

import org.w3c.dom.Node;

/**
 * Container a particular class of {@link QtiNode}s.
 * <p>
 * For example: {@link TestPartGroup} (group for testParts), {@link ExpressionGroup} (group for expressions).
 *
 * @param <P> type of parent {@link QtiNode}
 * @param <C> type of child {@link QtiNode}
 *
 * @author Jiri Kajaba (original)
 * @author David McKain (refactored)
 */
public interface NodeGroup<P extends QtiNode, C extends QtiNode> extends Serializable, Iterable<C> {

    /**
     * Gets parent node of group.
     *
     * @return parent node of group
     */
    P getParent();

    /**
     * Gets name of group.
     * <p>
     * Name of group is typically QTI class name of its children (if it is same for all children).
     * <p>
     * For example: name of {@link TestPartGroup} is testPart.
     * <p>
     * If group can contain children with different QTI class name, name is display name of abstract parent.
     * <p>
     * For example: name of {@link ExpressionGroup} is expression (expression is not QTI class name for any node).
     *
     * @return name of group
     */
    String getName();

    /**
     * Returns a full (but somewhat pseudo) XPath 2.0 expression that can be used to navigate to this NodeGroup.
     * <p>
     * This is intended for debugging and information purposes rather than anything else. A fictitious XPath 2.0 function is used to select the abstract QTI
     * class represented by this NodeGroup.
     */
    String computeXPath();

    /**
     * Returns true if group can contain children with different QTI class name; false otherwise.
     *
     * @return true if group can contain children with different QTI class name; false otherwise
     */
    boolean isComplexContent();

    /**
     * Returns whether this NodeGroup supports (i.e. contains) {@link QtiNode}s of the given
     * QTI Class Names.
     *
     * For example: SectionPartNodegroups supports assessmentSection and assessmentItemRef.
     *
     * @return true if this group contains {@link QtiNode}s of the given class name, false
     *   otherwise
     */
    boolean supportsQtiClass(String qtiClassName);

    /**
     * Gets mutable list of all children.
     *
     * @return list of all children
     */
    List<C> getChildren();

    /**
     * Returns the required minimum number of children. (0 = no restriction)
     *
     * @return required minimum number of children or null
     */
    int getMinimum();

    /**
     * Returns the allowed maximum number of children, using null to mean "unlimited"
     *
     * @return allowed maximum number of children or null
     */
    Integer getMaximum();

    /**
     * Loads data from the given DOM {@link Node} if this {@link NodeGroup}
     * supports it. Returns true if the Node is supported, false otherwise.
     * <p>
     * (Each DOM Node should be supported by 1 {@link NodeGroup} owned by
     * a given parent {@link QtiNode}).
     *
     * @param childNode
     * @param context
     * @return true if the childNode DOM Node was loaded into this {@link NodeGroup},
     *   false otherwise.
     */
    boolean loadChildIfSupported(final Node childNode, final LoadingContext context);

    /**
     * Creates child with given QTI class name.
     * <p>
     * Parameter qtiClassName is needed only if group can contain children with different QTI class names (otherwise it is ignored).
     *
     * @param qtiClassName QTI class name
     * @return created child
     * @throws QtiIllegalChildException if the given qtiClassName is not appropriate
     */
    C create(String qtiClassName);

    /**
     * Validates this group, recursively descending into children.
     */
    void validate(final ValidationContext context);
}
