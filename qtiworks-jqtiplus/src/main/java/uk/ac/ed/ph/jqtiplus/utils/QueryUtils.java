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
package uk.ac.ed.ph.jqtiplus.utils;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.attribute.Attribute;
import uk.ac.ed.ph.jqtiplus.attribute.ForeignAttribute;
import uk.ac.ed.ph.jqtiplus.group.NodeGroup;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.block.ForeignElement;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.CustomOperator;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.CustomInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.RootObjectLookup;

import java.util.HashSet;
import java.util.Set;

/**
 * This class will provide some utility methods to perform deep queries or searches
 * of a {@link XmlNode} tree.
 * 
 * @author David McKain
 */
public final class QueryUtils {
    
    public static Set<JqtiExtensionPackage> findExtensionsUsed(ResolvedAssessmentItem resolvedItem) {
        Set<JqtiExtensionPackage> resultSet = findExtensionsWithin(resolvedItem.getItemLookup().extractAssumingSuccessful());
        RootObjectLookup<ResponseProcessing> rpTemplateLookup = resolvedItem.getResolvedResponseProcessingTemplateLookup();
        if (rpTemplateLookup!=null) {
            resultSet.addAll(findExtensionsWithin(rpTemplateLookup.extractAssumingSuccessful()));
        }
        return resultSet;        
    }
    
    /**
     * Finds all {@link JqtiExtensionPackage}s used by the given {@link XmlNode}s and their
     * child Nodes.
     * 
     * @param node
     */
    public static Set<JqtiExtensionPackage> findExtensionsWithin(XmlNode... nodes) {
        final Set<JqtiExtensionPackage> resultSet = new HashSet<JqtiExtensionPackage>();
        for (XmlNode node : nodes) {
            walkTree(node, new TreeWalkNodeHandler() {
                @Override
                public boolean handleNode(XmlNode node) {
                    if (node instanceof CustomOperator) {
                        resultSet.add(((CustomOperator) node).getJqtiExtensionPackage());
                    }
                    else if (node instanceof CustomInteraction) {
                        resultSet.add(((CustomInteraction) node).getJqtiExtensionPackage());
                    }
                    /* Keep descending */
                    return true;
                }
            });
        }
        return resultSet;
    }
    
    public static ForeignNamespaceSummary findForeignNamespaces(XmlNode... nodes) {
        final Set<String> elementNamespaceUris = new HashSet<String>();
        final Set<String> attributeNamespaceUris = new HashSet<String>();
        for (XmlNode node : nodes) {
            walkTree(node, new TreeWalkNodeHandler() {
                @Override
                public boolean handleNode(XmlNode node) {
                    /* Consider node itself */
                    if (node instanceof uk.ac.ed.ph.jqtiplus.node.content.mathml.Math) {
                        elementNamespaceUris.add(QtiConstants.MATHML_NAMESPACE_URI);
                    }
                    else if (node instanceof ForeignElement) {
                        elementNamespaceUris.add(((ForeignElement) node).getNamespaceUri());
                    }
                    /* Now do attributes */
                    for (Attribute<?> attribute : node.getAttributes()) {
                        if (attribute instanceof ForeignAttribute) {
                            attributeNamespaceUris.add(attribute.getNamespaceUri());
                        }
                    }
                    
                    /* Keep descending */
                    return true;
                }
            });
        }
        return new ForeignNamespaceSummary(elementNamespaceUris, attributeNamespaceUris);
    }
    
    public static void walkTree(XmlNode startNode, TreeWalkNodeHandler handler) {
        ConstraintUtilities.ensureNotNull(startNode);
        ConstraintUtilities.ensureNotNull(handler);
        doWalkTree(startNode, handler);
    }
    
    private static void doWalkTree(XmlNode currentNode, TreeWalkNodeHandler handler) {
        boolean descend = handler.handleNode(currentNode);
        if (descend) {
            for (NodeGroup nodeGroup : currentNode.getNodeGroups()) {
                for (XmlNode childNode : nodeGroup.getChildren()) {
                    doWalkTree(childNode, handler);
                }
            }
        }        
    }
 
}
