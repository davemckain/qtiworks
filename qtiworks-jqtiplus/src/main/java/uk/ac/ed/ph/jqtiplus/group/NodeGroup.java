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
package uk.ac.ed.ph.jqtiplus.group;

import uk.ac.ed.ph.jqtiplus.exception2.QtiIllegalChildException;
import uk.ac.ed.ph.jqtiplus.group.expression.ExpressionGroup;
import uk.ac.ed.ph.jqtiplus.group.test.TestPartGroup;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.validation.Validatable;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRemove;

import java.io.Serializable;
import java.util.List;

import org.w3c.dom.Element;

/**
 * Container for one node type.
 * <p>
 * For example: {@link TestPartGroup} (group for testParts), {@link ExpressionGroup} (group for expressions).
 *
 * @author Jiri Kajaba
 */
public interface NodeGroup extends Validatable, Serializable, Iterable<XmlNode> {

    /**
     * Gets parent node of group.
     *
     * @return parent node of group
     */
    XmlNode getParent();

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
    boolean isGeneral();

    /**
     * Returns list of all possible QTI class names (all possible children in this group).
     * <p>
     * This list will not change in time (it contains every possible QTI class name).
     * <p>
     * For example: SectionPartNodegroups returns assessmentSection and assessmentItemRef.
     *
     * @return list of all possible QTI class names (all possible children in this group)
     */
    List<String> getAllSupportedClasses();

    /* (This was never used anywhere and was a pain to implement) */
    @ToRemove
    //    /**
    //     * Returns list of all currently possible QTI class names (all currently possible children in this group).
    //     * <p>
    //     * This list can change in time (it contains every possible QTI class name for current time (conditions)).
    //     * <p>
    //     * For example: expression delete can contain on first position only expressions which produce single value
    //     * and on second position only expressions which produce multiple or ordered cardinality.
    //     *
    //     * @param index index of children in group
    //     * @return list of all currently possible QTI class names (all currently possible children in this group)
    //     */
    //    List<String> getCurrentSupportedClasses(int index);

    /**
     * Gets list of all children.
     *
     * @return list of all children
     */
    List<XmlNode> getChildren();

    /**
     * Gets required minimum number of children or null.
     *
     * @return required minimum number of children or null
     */
    Integer getMinimum();

    /**
     * Gets allowed maximum number of children or null.
     *
     * @return allowed maximum number of children or null
     */
    Integer getMaximum();

    /**
     * Loads children from given source node (DOM).
     *
     * @param sourceElement source node (DOM)
     */
    void load(Element sourceElement, LoadingContext context);

    /**
     * Creates child with given QTI class name.
     * <p>
     * Parameter classTag is needed only if group can contain children with different QTI class names (otherwise it is ignored).
     *
     * @param classTag QTI class name
     * @return created child
     * @throws QtiIllegalChildException if the given classTag is not appropriate
     */
    XmlNode create(String classTag);
}
