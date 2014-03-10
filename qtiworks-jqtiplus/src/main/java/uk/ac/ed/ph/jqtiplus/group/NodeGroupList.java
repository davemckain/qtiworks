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
import uk.ac.ed.ph.jqtiplus.exception.QtiNodeGroupException;
import uk.ac.ed.ph.jqtiplus.group.block.InteractionGroup;
import uk.ac.ed.ph.jqtiplus.group.content.BlockGroup;
import uk.ac.ed.ph.jqtiplus.group.content.BlockStaticGroup;
import uk.ac.ed.ph.jqtiplus.group.content.FlowGroup;
import uk.ac.ed.ph.jqtiplus.group.content.FlowStaticGroup;
import uk.ac.ed.ph.jqtiplus.group.content.InlineGroup;
import uk.ac.ed.ph.jqtiplus.group.content.InlineStaticGroup;
import uk.ac.ed.ph.jqtiplus.group.content.ObjectFlowGroup;
import uk.ac.ed.ph.jqtiplus.group.content.ObjectGroup;
import uk.ac.ed.ph.jqtiplus.group.content.TextOrVariableGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.list.DlElementGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.list.LiGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.CaptionGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.ColGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.ColgroupGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.TableCellGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.TbodyGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.TfootGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.TheadGroup;
import uk.ac.ed.ph.jqtiplus.group.content.xhtml.table.TrGroup;
import uk.ac.ed.ph.jqtiplus.group.expression.ExpressionGroup;
import uk.ac.ed.ph.jqtiplus.group.item.CorrectResponseGroup;
import uk.ac.ed.ph.jqtiplus.group.item.ItemBodyGroup;
import uk.ac.ed.ph.jqtiplus.group.item.ModalFeedbackGroup;
import uk.ac.ed.ph.jqtiplus.group.item.StylesheetGroup;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.PositionObjectInteractionGroup;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.PromptGroup;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.choice.GapChoiceGroup;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.choice.GapImgGroup;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.choice.InlineChoiceGroup;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.choice.SimpleAssociableChoiceGroup;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.choice.SimpleChoiceGroup;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.choice.SimpleMatchSetGroup;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.graphic.AssociableHotspotGroup;
import uk.ac.ed.ph.jqtiplus.group.item.interaction.graphic.HotspotChoiceGroup;
import uk.ac.ed.ph.jqtiplus.group.item.response.declaration.AreaMapEntryGroup;
import uk.ac.ed.ph.jqtiplus.group.item.response.declaration.AreaMappingGroup;
import uk.ac.ed.ph.jqtiplus.group.item.response.declaration.MapEntryGroup;
import uk.ac.ed.ph.jqtiplus.group.item.response.declaration.MappingGroup;
import uk.ac.ed.ph.jqtiplus.group.item.response.declaration.ResponseDeclarationGroup;
import uk.ac.ed.ph.jqtiplus.group.item.response.processing.ResponseElseGroup;
import uk.ac.ed.ph.jqtiplus.group.item.response.processing.ResponseElseIfGroup;
import uk.ac.ed.ph.jqtiplus.group.item.response.processing.ResponseIfGroup;
import uk.ac.ed.ph.jqtiplus.group.item.response.processing.ResponseProcessingGroup;
import uk.ac.ed.ph.jqtiplus.group.item.response.processing.ResponseRuleGroup;
import uk.ac.ed.ph.jqtiplus.group.item.template.declaration.TemplateDeclarationGroup;
import uk.ac.ed.ph.jqtiplus.group.item.template.processing.TemplateElseGroup;
import uk.ac.ed.ph.jqtiplus.group.item.template.processing.TemplateElseIfGroup;
import uk.ac.ed.ph.jqtiplus.group.item.template.processing.TemplateIfGroup;
import uk.ac.ed.ph.jqtiplus.group.item.template.processing.TemplateProcessingGroup;
import uk.ac.ed.ph.jqtiplus.group.item.template.processing.TemplateProcessingRuleGroup;
import uk.ac.ed.ph.jqtiplus.group.item.template.processing.TemplateRuleGroup;
import uk.ac.ed.ph.jqtiplus.group.outcome.declaration.DefaultValueGroup;
import uk.ac.ed.ph.jqtiplus.group.outcome.declaration.InterpolationTableEntryGroup;
import uk.ac.ed.ph.jqtiplus.group.outcome.declaration.LookupTableGroup;
import uk.ac.ed.ph.jqtiplus.group.outcome.declaration.MatchTableEntryGroup;
import uk.ac.ed.ph.jqtiplus.group.outcome.declaration.OutcomeDeclarationGroup;
import uk.ac.ed.ph.jqtiplus.group.outcome.processing.OutcomeElseGroup;
import uk.ac.ed.ph.jqtiplus.group.outcome.processing.OutcomeElseIfGroup;
import uk.ac.ed.ph.jqtiplus.group.outcome.processing.OutcomeIfGroup;
import uk.ac.ed.ph.jqtiplus.group.outcome.processing.OutcomeProcessingGroup;
import uk.ac.ed.ph.jqtiplus.group.outcome.processing.OutcomeRuleGroup;
import uk.ac.ed.ph.jqtiplus.group.result.CandidateCommentGroup;
import uk.ac.ed.ph.jqtiplus.group.result.CandidateResponseGroup;
import uk.ac.ed.ph.jqtiplus.group.result.ContextGroup;
import uk.ac.ed.ph.jqtiplus.group.result.ItemResultGroup;
import uk.ac.ed.ph.jqtiplus.group.result.ItemVariableGroup;
import uk.ac.ed.ph.jqtiplus.group.result.SessionIdentifierGroup;
import uk.ac.ed.ph.jqtiplus.group.result.TestResultGroup;
import uk.ac.ed.ph.jqtiplus.group.shared.FieldValueGroup;
import uk.ac.ed.ph.jqtiplus.group.test.AssessmentSectionGroup;
import uk.ac.ed.ph.jqtiplus.group.test.BranchRuleGroup;
import uk.ac.ed.ph.jqtiplus.group.test.ItemSessionControlGroup;
import uk.ac.ed.ph.jqtiplus.group.test.OrderingGroup;
import uk.ac.ed.ph.jqtiplus.group.test.PreConditionGroup;
import uk.ac.ed.ph.jqtiplus.group.test.RubricBlockGroup;
import uk.ac.ed.ph.jqtiplus.group.test.SectionPartGroup;
import uk.ac.ed.ph.jqtiplus.group.test.SelectionGroup;
import uk.ac.ed.ph.jqtiplus.group.test.TemplateDefaultGroup;
import uk.ac.ed.ph.jqtiplus.group.test.TestFeedbackGroup;
import uk.ac.ed.ph.jqtiplus.group.test.TestPartGroup;
import uk.ac.ed.ph.jqtiplus.group.test.TimeLimitsGroup;
import uk.ac.ed.ph.jqtiplus.group.test.VariableMappingGroup;
import uk.ac.ed.ph.jqtiplus.group.test.WeightGroup;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.BodyElement;
import uk.ac.ed.ph.jqtiplus.node.content.ItemBody;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Block;
import uk.ac.ed.ph.jqtiplus.node.content.basic.BlockStatic;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Flow;
import uk.ac.ed.ph.jqtiplus.node.content.basic.FlowStatic;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Inline;
import uk.ac.ed.ph.jqtiplus.node.content.basic.InlineStatic;
import uk.ac.ed.ph.jqtiplus.node.content.basic.ObjectFlow;
import uk.ac.ed.ph.jqtiplus.node.content.variable.RubricBlock;
import uk.ac.ed.ph.jqtiplus.node.content.variable.TextOrVariable;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.list.Li;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.object.Object;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Caption;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Col;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Colgroup;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.TableCell;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Tbody;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Tfoot;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Thead;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Tr;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.item.CorrectResponse;
import uk.ac.ed.ph.jqtiplus.node.item.ModalFeedback;
import uk.ac.ed.ph.jqtiplus.node.item.Stylesheet;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.PositionObjectInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Prompt;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.GapChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.GapImg;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.InlineChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleAssociableChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleChoice;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleMatchSet;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.graphic.AssociableHotspot;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.graphic.HotspotChoice;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.AreaMapEntry;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.AreaMapping;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.MapEntry;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.Mapping;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseElse;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseElseIf;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseIf;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseRule;
import uk.ac.ed.ph.jqtiplus.node.item.template.declaration.TemplateDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateElse;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateElseIf;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateIf;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateProcessing;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateProcessingRule;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateRule;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.InterpolationTableEntry;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.LookupTable;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.MatchTableEntry;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.result.CandidateComment;
import uk.ac.ed.ph.jqtiplus.node.result.CandidateResponse;
import uk.ac.ed.ph.jqtiplus.node.result.Context;
import uk.ac.ed.ph.jqtiplus.node.result.ItemResult;
import uk.ac.ed.ph.jqtiplus.node.result.ItemVariable;
import uk.ac.ed.ph.jqtiplus.node.result.SessionIdentifier;
import uk.ac.ed.ph.jqtiplus.node.result.TestResult;
import uk.ac.ed.ph.jqtiplus.node.shared.FieldValue;
import uk.ac.ed.ph.jqtiplus.node.shared.declaration.DefaultValue;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.node.test.BranchRule;
import uk.ac.ed.ph.jqtiplus.node.test.ItemSessionControl;
import uk.ac.ed.ph.jqtiplus.node.test.Ordering;
import uk.ac.ed.ph.jqtiplus.node.test.PreCondition;
import uk.ac.ed.ph.jqtiplus.node.test.SectionPart;
import uk.ac.ed.ph.jqtiplus.node.test.Selection;
import uk.ac.ed.ph.jqtiplus.node.test.TemplateDefault;
import uk.ac.ed.ph.jqtiplus.node.test.TestFeedback;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.node.test.TimeLimits;
import uk.ac.ed.ph.jqtiplus.node.test.VariableMapping;
import uk.ac.ed.ph.jqtiplus.node.test.Weight;
import uk.ac.ed.ph.jqtiplus.node.test.outcome.processing.OutcomeElse;
import uk.ac.ed.ph.jqtiplus.node.test.outcome.processing.OutcomeElseIf;
import uk.ac.ed.ph.jqtiplus.node.test.outcome.processing.OutcomeIf;
import uk.ac.ed.ph.jqtiplus.node.test.outcome.processing.OutcomeProcessing;
import uk.ac.ed.ph.jqtiplus.node.test.outcome.processing.OutcomeRule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Container for all node groups contained (owned) by a particular {@link QtiNode}.
 *
 * @author Jiri Kajaba
 */
public final class NodeGroupList implements Serializable, Iterable<NodeGroup<?,?>> {

    private static final long serialVersionUID = 4649998181277985510L;

    /** Parent (node) of this container. */
    private final QtiNode parent;

    /** Children (groups) of this container. */
    private final List<NodeGroup<?,?>> groups;

    public NodeGroupList(final QtiNode parent) {
        this.parent = parent;
        this.groups = new ArrayList<NodeGroup<?,?>>();
    }

    /**
     * Gets parent (owner) of this container.
     *
     * @return parent of this container
     */
    public QtiNode getParent() {
        return parent;
    }

    /**
     * Gets number of groups in this container.
     *
     * @return number of groups in this container
     */
    public int size() {
        return groups.size();
    }

    @Override
    public Iterator<NodeGroup<?,?>> iterator() {
        return groups.iterator();
    }

    /**
     * Adds given group into this container
     * <p>
     * (In JQTI+, this no longer checks duplicities in group names.)
     *
     * @see #addSafe(int, NodeGroup)
     *
     * @param group given group
     */
    public void add(final NodeGroup<?,?> group) {
        groups.add(group);
    }

    /**
     * Adds given group into this container at given position.
     * <p>
     * (In JQTI+, this no longer checks duplicities in group names.)
     *
     * @param index position
     * @param group given group
     */
    public void add(final int index, final NodeGroup<?,?> group) {
        groups.add(index, group);
    }

    /**
     * SAFELY adds given group into this container, checking duplicities in
     * group names.
     * <p>
     * This used to be the default in JQTI, but wastes processor cycles for the
     * implementation of the core QTI spec. I've kept it in in case it might be
     * useful for someone adding a (non-standard) extension to the spec.
     *
     * @param group given group
     * @throws QtiNodeGroupException if container already contains group with same name
     */
    public void addSafe(final int index, final NodeGroup<?,?> group) {
        for (final NodeGroup<?,?> child : groups) {
            if (child.getName().equals(group.getName())) {
                throw new QtiNodeGroupException("Duplicate node group name: " + group.computeXPath());
            }
        }

        groups.add(index, group);
    }

    /**
     * Loads group's children from given source DOM {@link Element}.
     * <ul>
     * <li>Unsupported (unknown) children are skipped.</li>
     * <li>Wrong order of children is ignored (children are loaded in correct order).</li>
     * </ul>
     *
     * @param element source DOM {@link Element}
     * @param context current {@link LoadingContext} callback
     */
    public void load(final Element element, final LoadingContext context) {
        for (final NodeGroup<?,?> group : groups) {
            group.getChildren().clear();
        }
        final NodeList childNodes = element.getChildNodes();
        for (int i=0; i<childNodes.getLength(); i++) {
            final Node childNode = childNodes.item(i);
            boolean childLoaded = false;
            for (final NodeGroup<?,?> group : groups) {
                if (group.loadChildIfSupported(childNode, context)) {
                    childLoaded = true;
                    break;
                }
            }
            if (!childLoaded) {
                /* No NodeGroup supports this child */
                if (childNode.getNodeType()==Node.TEXT_NODE && childNode.getNodeValue().trim().isEmpty()) {
                    /* Whitespace node, so we'll ignore this */
                }
                else {
                    /* Register error */
                    final String childName = childNode.getNodeType()==Node.ELEMENT_NODE ? childNode.getLocalName() : "(text)";
                    context.modelBuildingError(new QtiIllegalChildException(parent, childName), childNode);
                }
            }
        }
    }

    /**
     * Removed all groups (children) from this container.
     */
    public void clear() {
        groups.clear();
    }

    /**
     * Gets group at given index.
     *
     * @param index index of requested group
     * @return group at given index
     */
    public NodeGroup<?,?> get(final int index) {
        return groups.get(index);
    }

    /**
     * Gets group with given name.
     *
     * @param name name of requested group
     * @return group with given name
     * @throws QtiNodeGroupException if group is not found
     */
    public NodeGroup<?,?> get(final String name) {
        for (final NodeGroup<?,?> child : groups) {
            if (child.getName().equals(name) || child.supportsQtiClass(name)) {
                return child;
            }
        }
        throw new QtiNodeGroupException("Cannot find node group with name " + name);
    }

    /**
     * Gets group supporting the given QTI class name.
     *
     * @param qtiClassName name of requested group
     *
     * @return group with given name
     * @throws QtiNodeGroupException if group is not found
     */
    public NodeGroup<?,?> getGroupSupporting(final String qtiClassName) {
        for (final NodeGroup<?,?> child : groups) {
            if (child.supportsQtiClass(qtiClassName)) {
                return child;
            }
        }
        throw new QtiNodeGroupException("Cannot find node group supporting " + qtiClassName);
    }

    /**
     * Gets expression group.
     *
     * @return expression group
     * @throws QtiNodeGroupException if group is not found
     */
    public ExpressionGroup getExpressionGroup() {
        return (ExpressionGroup) get(Expression.DISPLAY_NAME);
    }

    /**
     * Gets correctResponse group.
     *
     * @return correctResponse group
     * @throws QtiNodeGroupException if group is not found
     */
    public CorrectResponseGroup getCorrectResponseGroup() {
        return (CorrectResponseGroup) get(CorrectResponse.QTI_CLASS_NAME);
    }

    /**
     * Gets responseDeclaration group.
     *
     * @return responseDeclaration group
     * @throws QtiNodeGroupException if group is not found
     */
    public ResponseDeclarationGroup getResponseDeclarationGroup() {
        return (ResponseDeclarationGroup) get(ResponseDeclaration.QTI_CLASS_NAME);
    }

    /**
     * Gets templateDeclaration group.
     *
     * @return templateDeclaration group
     * @throws QtiNodeGroupException if group is not found
     */
    public TemplateDeclarationGroup getTemplateDeclarationGroup() {
        return (TemplateDeclarationGroup) get(TemplateDeclaration.QTI_CLASS_NAME);
    }

    /**
     * Gets defaultValue group.
     *
     * @return defaultValue group
     * @throws QtiNodeGroupException if group is not found
     */
    public DefaultValueGroup getDefaultValueGroup() {
        return (DefaultValueGroup) get(DefaultValue.QTI_CLASS_NAME);
    }

    /**
     * Gets interpolationTableEntry group.
     *
     * @return interpolationTableEntry group
     * @throws QtiNodeGroupException if group is not found
     */
    public InterpolationTableEntryGroup getInterpolationTableEntryGroup() {
        return (InterpolationTableEntryGroup) get(InterpolationTableEntry.QTI_CLASS_NAME);
    }

    /**
     * Gets lookupTable group.
     *
     * @return lookupTable group
     * @throws QtiNodeGroupException if group is not found
     */
    public LookupTableGroup getLookupTableGroup() {
        return (LookupTableGroup) get(LookupTable.DISPLAY_NAME);
    }

    /**
     * Gets matchTableEntry group.
     *
     * @return matchTableEntry group
     * @throws QtiNodeGroupException if group is not found
     */
    public MatchTableEntryGroup getMatchTableEntryGroup() {
        return (MatchTableEntryGroup) get(MatchTableEntry.QTI_CLASS_NAME);
    }

    /**
     * Gets outcomeDeclaration group.
     *
     * @return outcomeDeclaration group
     * @throws QtiNodeGroupException if group is not found
     */
    public OutcomeDeclarationGroup getOutcomeDeclarationGroup() {
        return (OutcomeDeclarationGroup) get(OutcomeDeclaration.QTI_CLASS_NAME);
    }

    /**
     * Gets outcomeElse group.
     *
     * @return outcomeElse group
     * @throws QtiNodeGroupException if group is not found
     */
    public OutcomeElseGroup getOutcomeElseGroup() {
        return (OutcomeElseGroup) get(OutcomeElse.QTI_CLASS_NAME);
    }

    /**
     * Gets outcomeElseIf group.
     *
     * @return outcomeElseIf group
     * @throws QtiNodeGroupException if group is not found
     */
    public OutcomeElseIfGroup getOutcomeElseIfGroup() {
        return (OutcomeElseIfGroup) get(OutcomeElseIf.QTI_CLASS_NAME);
    }

    /**
     * Gets outcomeIf group.
     *
     * @return outcomeIf group
     * @throws QtiNodeGroupException if group is not found
     */
    public OutcomeIfGroup getOutcomeIfGroup() {
        return (OutcomeIfGroup) get(OutcomeIf.QTI_CLASS_NAME);
    }

    /**
     * Gets outcomeProcessing group.
     *
     * @return outcomeProcessing group
     * @throws QtiNodeGroupException if group is not found
     */
    public OutcomeProcessingGroup getOutcomeProcessingGroup() {
        return (OutcomeProcessingGroup) get(OutcomeProcessing.QTI_CLASS_NAME);
    }

    /**
     * Gets outcomeRule group.
     *
     * @return outcomeRule group
     * @throws QtiNodeGroupException if group is not found
     */
    public OutcomeRuleGroup getOutcomeRuleGroup() {
        return (OutcomeRuleGroup) get(OutcomeRule.DISPLAY_NAME);
    }

    /**
     * Gets candidateComment group.
     *
     * @return candidateComment group
     * @throws QtiNodeGroupException if group is not found
     */
    public CandidateCommentGroup getCandidateCommentGroup() {
        return (CandidateCommentGroup) get(CandidateComment.QTI_CLASS_NAME);
    }

    /**
     * Gets context group.
     *
     * @return context group
     * @throws QtiNodeGroupException if group is not found
     */
    public ContextGroup getContextGroup() {
        return (ContextGroup) get(Context.QTI_CLASS_NAME);
    }

    /**
     * Gets itemResult group.
     *
     * @return itemResult group
     * @throws QtiNodeGroupException if group is not found
     */
    public ItemResultGroup getItemResultGroup() {
        return (ItemResultGroup) get(ItemResult.QTI_CLASS_NAME);
    }

    /**
     * Gets itemVariable group.
     *
     * @return itemVariable group
     * @throws QtiNodeGroupException if group is not found
     */
    public ItemVariableGroup getItemVariableGroup() {
        return (ItemVariableGroup) get(ItemVariable.DISPLAY_NAME);
    }

    /**
     * Gets sessionIdentifier group.
     *
     * @return sessionIdentifier group
     * @throws QtiNodeGroupException if group is not found
     */
    public SessionIdentifierGroup getSessionIdentifierGroup() {
        return (SessionIdentifierGroup) get(SessionIdentifier.QTI_CLASS_NAME);
    }

    /**
     * Gets testResult group.
     *
     * @return testResult group
     * @throws QtiNodeGroupException if group is not found
     */
    public TestResultGroup getTestResultGroup() {
        return (TestResultGroup) get(TestResult.QTI_CLASS_NAME);
    }

    /**
     * Gets fieldValue group.
     *
     * @return fieldValue group
     * @throws QtiNodeGroupException if group is not found
     */
    public FieldValueGroup getFieldValueGroup() {
        return (FieldValueGroup) get(FieldValue.QTI_CLASS_NAME);
    }

    /**
     * Gets assessmentSection group.
     *
     * @return assessmentSection group
     * @throws QtiNodeGroupException if group is not found
     */
    public AssessmentSectionGroup getAssessmentSectionGroup() {
        return (AssessmentSectionGroup) get(AssessmentSection.QTI_CLASS_NAME);
    }

    /**
     * Gets branchRule group.
     *
     * @return branchRule group
     * @throws QtiNodeGroupException if group is not found
     */
    public BranchRuleGroup getBranchRuleGroup() {
        return (BranchRuleGroup) get(BranchRule.QTI_CLASS_NAME);
    }

    /**
     * Gets itemSessionControl group.
     *
     * @return itemSessionControl group
     * @throws QtiNodeGroupException if group is not found
     */
    public ItemSessionControlGroup getItemSessionControlGroup() {
        return (ItemSessionControlGroup) get(ItemSessionControl.QTI_CLASS_NAME);
    }

    /**
     * Gets ordering group.
     *
     * @return ordering group
     * @throws QtiNodeGroupException if group is not found
     */
    public OrderingGroup getOrderingGroup() {
        return (OrderingGroup) get(Ordering.QTI_CLASS_NAME);
    }

    /**
     * Gets preCondition group.
     *
     * @return preCondition group
     * @throws QtiNodeGroupException if group is not found
     */
    public PreConditionGroup getPreConditionGroup() {
        return (PreConditionGroup) get(PreCondition.QTI_CLASS_NAME);
    }

    /**
     * Gets rubricBlock group.
     *
     * @return rubricBlock group
     * @throws QtiNodeGroupException if group is not found
     */
    public RubricBlockGroup getRubricBlockGroup() {
        return (RubricBlockGroup) get(RubricBlock.QTI_CLASS_NAME);
    }

    /**
     * Gets sectionPart group.
     *
     * @return sectionPart group
     * @throws QtiNodeGroupException if group is not found
     */
    public SectionPartGroup getSectionPartGroup() {
        return (SectionPartGroup) get(SectionPart.DISPLAY_NAME);
    }

    /**
     * Gets selection group.
     *
     * @return selection group
     * @throws QtiNodeGroupException if group is not found
     */
    public SelectionGroup getSelectionGroup() {
        return (SelectionGroup) get(Selection.QTI_CLASS_NAME);
    }

    /**
     * Gets templateDefault group.
     *
     * @return templateDefault group
     * @throws QtiNodeGroupException if group is not found
     */
    public TemplateDefaultGroup getTemplateDefaultGroup() {
        return (TemplateDefaultGroup) get(TemplateDefault.QTI_CLASS_NAME);
    }

    /**
     * Gets testFeedback group.
     *
     * @return testFeedback group
     * @throws QtiNodeGroupException if group is not found
     */
    public TestFeedbackGroup getTestFeedbackGroup() {
        return (TestFeedbackGroup) get(TestFeedback.QTI_CLASS_NAME);
    }

    /**
     * Gets testPart group.
     *
     * @return testPart group
     * @throws QtiNodeGroupException if group is not found
     */
    public TestPartGroup getTestPartGroup() {
        return (TestPartGroup) get(TestPart.QTI_CLASS_NAME);
    }

    /**
     * Gets timeLimits group.
     *
     * @return timeLimits group
     * @throws QtiNodeGroupException if group is not found
     */
    public TimeLimitsGroup getTimeLimitsGroup() {
        return (TimeLimitsGroup) get(TimeLimits.QTI_CLASS_NAME);
    }

    /**
     * Gets variableMapping group.
     *
     * @return variableMapping group
     * @throws QtiNodeGroupException if group is not found
     */
    public VariableMappingGroup getVariableMappingGroup() {
        return (VariableMappingGroup) get(VariableMapping.QTI_CLASS_NAME);
    }

    /**
     * Gets weight group.
     *
     * @return weight group
     * @throws QtiNodeGroupException if group is not found
     */
    public WeightGroup getWeightGroup() {
        return (WeightGroup) get(Weight.QTI_CLASS_NAME);
    }


    /**
     * Gets templateElse group.
     *
     * @return templateElse group
     * @throws QtiNodeGroupException if group is not found
     */
    public TemplateElseGroup getTemplateElseGroup() {
        return (TemplateElseGroup) get(TemplateElse.QTI_CLASS_NAME);
    }

    /**
     * Gets templateElseIf group.
     *
     * @return templateElseIf group
     * @throws QtiNodeGroupException if group is not found
     */
    public TemplateElseIfGroup getTemplateElseIfGroup() {
        return (TemplateElseIfGroup) get(TemplateElseIf.QTI_CLASS_NAME);
    }

    /**
     * Gets templateIf group.
     *
     * @return templateIf group
     * @throws QtiNodeGroupException if group is not found
     */
    public TemplateIfGroup getTemplateIfGroup() {
        return (TemplateIfGroup) get(TemplateIf.QTI_CLASS_NAME);
    }

    /**
     * Gets templateProcessing group.
     *
     * @return templateProcessing group
     * @throws QtiNodeGroupException if group is not found
     */
    public TemplateProcessingGroup getTemplateProcessingGroup() {
        return (TemplateProcessingGroup) get(TemplateProcessing.QTI_CLASS_NAME);
    }

    /**
     * Gets templateProcessingRule group.
     *
     * @return templateProcessing group
     * @throws QtiNodeGroupException if group is not found
     */
    public TemplateProcessingRuleGroup getTemplateProcessingRuleGroup() {
        return (TemplateProcessingRuleGroup) get(TemplateProcessingRule.DISPLAY_NAME);
    }

    /**
     * Gets templateRule group.
     *
     * @return templateRule group
     * @throws QtiNodeGroupException if group is not found
     */
    public TemplateRuleGroup getTemplateRuleGroup() {
        return (TemplateRuleGroup) get(TemplateRule.DISPLAY_NAME);
    }

    /**
     * Gets responseRule group.
     *
     * @return responseRule group
     * @throws QtiNodeGroupException if group is not found
     */
    public ResponseRuleGroup getResponseRuleGroup() {
        return (ResponseRuleGroup) get(ResponseRule.DISPLAY_NAME);
    }

    /**
     * Gets itemBody group.
     *
     * @return itemBody group
     * @throws QtiNodeGroupException if group is not found
     */
    public ItemBodyGroup getItemBodyGroup() {
        return (ItemBodyGroup) get(ItemBody.QTI_CLASS_NAME);
    }

    /**
     * Gets responseElse group.
     *
     * @return responseElse group
     * @throws QtiNodeGroupException if group is not found
     */
    public ResponseElseGroup getResponseElseGroup() {
        return (ResponseElseGroup) get(ResponseElse.QTI_CLASS_NAME);
    }

    /**
     * Gets responseElseIf group.
     *
     * @return responseElseIf group
     * @throws QtiNodeGroupException if group is not found
     */
    public ResponseElseIfGroup getResponseElseIfGroup() {
        return (ResponseElseIfGroup) get(ResponseElseIf.QTI_CLASS_NAME);
    }

    /**
     * Gets responseIf group.
     *
     * @return responseIf group
     * @throws QtiNodeGroupException if group is not found
     */
    public ResponseIfGroup getResponseIfGroup() {
        return (ResponseIfGroup) get(ResponseIf.QTI_CLASS_NAME);
    }

    /**
     * Gets responseProcessing group.
     *
     * @return responseProcessing group
     * @throws QtiNodeGroupException if group is not found
     */
    public ResponseProcessingGroup getResponseProcessingGroup() {
        return (ResponseProcessingGroup) get(ResponseProcessing.QTI_CLASS_NAME);
    }

    /**
     * Gets mapping group.
     *
     * @return mapping group
     * @throws QtiNodeGroupException if group is not found
     */
    public MappingGroup getMappingGroup() {
        return (MappingGroup) get(Mapping.QTI_CLASS_NAME);
    }

    /**
     * Gets mapEntry group.
     *
     * @return mapEntry group
     * @throws QtiNodeGroupException if group is not found
     */
    public MapEntryGroup getMapEntryGroup() {
        return (MapEntryGroup) get(MapEntry.QTI_CLASS_NAME);
    }

    /**
     * Gets areaMapping group.
     *
     * @return areaMapping group
     * @throws QtiNodeGroupException if group is not found
     */
    public AreaMappingGroup getAreaMappingGroup() {
        return (AreaMappingGroup) get(AreaMapping.QTI_CLASS_NAME);
    }

    /**
     * Gets areaMapEntry group.
     *
     * @return areaMapEntry group
     * @throws QtiNodeGroupException if group is not found
     */
    public AreaMapEntryGroup getAreaMapEntryGroup() {
        return (AreaMapEntryGroup) get(AreaMapEntry.QTI_CLASS_NAME);
    }


    /**
     * Gets prompt group.
     *
     * @return prompt group
     * @throws QtiNodeGroupException if group is not found
     */
    public PromptGroup getPromptGroup() {
        return (PromptGroup) get(Prompt.QTI_CLASS_NAME);
    }

    /**
     * Gets simpleChoice group.
     *
     * @return simpleChoice group
     * @throws QtiNodeGroupException if group is not found
     */
    public SimpleChoiceGroup getSimpleChoiceGroup() {
        return (SimpleChoiceGroup) get(SimpleChoice.QTI_CLASS_NAME);
    }

    /**
     * Gets interaction group.
     *
     * @return interaction group
     * @throws QtiNodeGroupException if group is not found
     */
    public InteractionGroup getInteractionGroup() {
        return (InteractionGroup) get(Interaction.DISPLAY_NAME);
    }

    /**
     * Gets inlineChoice group.
     *
     * @return inlineChoice group
     * @throws QtiNodeGroupException if group is not found
     */
    public InlineChoiceGroup getInlineChoiceGroup() {
        return (InlineChoiceGroup) get(InlineChoice.QTI_CLASS_NAME);
    }

    /**
     * Gets inline group.
     *
     * @return inline group
     * @throws QtiNodeGroupException if group is not found
     */
    public InlineGroup getInlineGroup() {
        return (InlineGroup) get(Inline.DISPLAY_NAME);
    }

    /**
     * Gets block group.
     *
     * @return block group
     * @throws QtiNodeGroupException if group is not found
     */
    public BlockGroup getBlockGroup() {
        return (BlockGroup) get(Block.DISPLAY_NAME);
    }

    /**
     * Gets li group.
     *
     * @return li group
     * @throws QtiNodeGroupException if group is not found
     */
    public LiGroup getLiGroup() {
        return (LiGroup) get(Li.QTI_CLASS_NAME);
    }

    /**
     * Gets dlElement group.
     *
     * @return dlElement group
     * @throws QtiNodeGroupException if group is not found
     */
    public DlElementGroup getDlElementGroup() {
        return (DlElementGroup) get(BodyElement.DISPLAY_NAME);
    }

    /**
     * Gets flow group.
     *
     * @return flow group
     * @throws QtiNodeGroupException if group is not found
     */
    public FlowGroup getFlowGroup() {
        return (FlowGroup) get(Flow.DISPLAY_NAME);
    }

    /**
     * Gets objectFlow group.
     *
     * @return objectFlow group
     * @throws QtiNodeGroupException if group is not found
     */
    public ObjectFlowGroup getObjectFlowGroup() {
        return (ObjectFlowGroup) get(ObjectFlow.DISPLAY_NAME);
    }

    /**
     * Gets Col group.
     *
     * @return Col group
     * @throws QtiNodeGroupException if group is not found
     */
    public ColGroup getColGroup() {
        return (ColGroup) get(Col.QTI_CLASS_NAME);
    }

    /**
     * Gets Colgroup group.
     *
     * @return Colgroup group
     * @throws QtiNodeGroupException if group is not found
     */
    public ColgroupGroup getColgroupGroup() {
        return (ColgroupGroup) get(Colgroup.QTI_CLASS_NAME);
    }

    /**
     * Gets Caption group.
     *
     * @return Caption group
     * @throws QtiNodeGroupException if group is not found
     */
    public CaptionGroup getCaptionGroup() {
        return (CaptionGroup) get(Caption.QTI_CLASS_NAME);
    }

    /**
     * Gets Tbody group.
     *
     * @return Tbody group
     * @throws QtiNodeGroupException if group is not found
     */
    public TbodyGroup getTbodyGroup() {
        return (TbodyGroup) get(Tbody.QTI_CLASS_NAME);
    }

    /**
     * Gets Tfoot group.
     *
     * @return Tfoot group
     * @throws QtiNodeGroupException if group is not found
     */
    public TfootGroup getTfootGroup() {
        return (TfootGroup) get(Tfoot.QTI_CLASS_NAME);
    }

    /**
     * Gets Thead group.
     *
     * @return Thead group
     * @throws QtiNodeGroupException if group is not found
     */
    public TheadGroup getTheadGroup() {
        return (TheadGroup) get(Thead.QTI_CLASS_NAME);
    }

    /**
     * Gets Tr group.
     *
     * @return Tr group
     * @throws QtiNodeGroupException if group is not found
     */
    public TrGroup getTrGroup() {
        return (TrGroup) get(Tr.QTI_CLASS_NAME);
    }

    /**
     * Gets TableCell group.
     *
     * @return TableCell group
     * @throws QtiNodeGroupException if group is not found
     */
    public TableCellGroup getTableCellGroup() {
        return (TableCellGroup) get(TableCell.DISPLAY_NAME);
    }

    /**
     * Gets inlineStatic group.
     *
     * @return inlineStatic group
     * @throws QtiNodeGroupException if group is not found
     */
    public InlineStaticGroup getInlineStaticGroup() {
        return (InlineStaticGroup) get(InlineStatic.DISPLAY_NAME);
    }

    /**
     * Gets flowStatic group.
     *
     * @return flowStatic group
     * @throws QtiNodeGroupException if group is not found
     */
    public FlowStaticGroup getFlowStaticGroup() {
        return (FlowStaticGroup) get(FlowStatic.DISPLAY_NAME);
    }

    /**
     * Gets textOrVariable group.
     *
     * @return textOrVariable group
     * @throws QtiNodeGroupException if group is not found
     */
    public TextOrVariableGroup getTextOrVariableGroup() {
        return (TextOrVariableGroup) get(TextOrVariable.DISPLAY_NAME);
    }

    /**
     * Gets blockStatic group.
     *
     * @return textOrVariable group
     * @throws QtiNodeGroupException if group is not found
     */
    public BlockStaticGroup getBlockStaticGroup() {
        return (BlockStaticGroup) get(BlockStatic.DISPLAY_NAME);
    }

    /**
     * Gets simpleAssociableChoice group.
     *
     * @return simpleAssociableChoice group
     * @throws QtiNodeGroupException if group is not found
     */
    public SimpleAssociableChoiceGroup getSimpleAssociableChoiceGroup() {
        return (SimpleAssociableChoiceGroup) get(SimpleAssociableChoice.QTI_CLASS_NAME);
    }

    /**
     * Gets object group.
     *
     * @return object group
     * @throws QtiNodeGroupException if group is not found
     */
    public ObjectGroup getObjectGroup() {
        return (ObjectGroup) get(Object.QTI_CLASS_NAME);
    }

    /**
     * Gets gapChoice group.
     *
     * @return gapChoice group
     * @throws QtiNodeGroupException if group is not found
     */
    public GapChoiceGroup getGapChoiceGroup() {
        return (GapChoiceGroup) get(GapChoice.DISPLAY_NAME);
    }

    /**
     * Gets associableHotspot group.
     *
     * @return associableHotspot group
     * @throws QtiNodeGroupException if group is not found
     */
    public AssociableHotspotGroup getAssociableHotspotGroup() {
        return (AssociableHotspotGroup) get(AssociableHotspot.QTI_CLASS_NAME);
    }

    /**
     * Gets gapImg group.
     *
     * @return gapImg group
     * @throws QtiNodeGroupException if group is not found
     */
    public GapImgGroup getGapImgGroup() {
        return (GapImgGroup) get(GapImg.QTI_CLASS_NAME);
    }

    /**
     * Gets hotspotChoice group.
     *
     * @return hotspotChoice group
     * @throws QtiNodeGroupException if group is not found
     */
    public HotspotChoiceGroup getHotspotChoiceGroup() {
        return (HotspotChoiceGroup) get(HotspotChoice.QTI_CLASS_NAME);
    }

    /**
     * Gets simpleMatchSet group.
     *
     * @return simpleMatchSet group
     * @throws QtiNodeGroupException if group is not found
     */
    public SimpleMatchSetGroup getSimpleMatchSetGroup() {
        return (SimpleMatchSetGroup) get(SimpleMatchSet.QTI_CLASS_NAME);
    }

    /**
     * Gets positionObjectInteraction group.
     *
     * @return positionObjectInteraction group
     * @throws QtiNodeGroupException if group is not found
     */
    public PositionObjectInteractionGroup getPositionObjectInteractionGroup() {
        return (PositionObjectInteractionGroup) get(PositionObjectInteraction.QTI_CLASS_NAME);
    }

    /**
     * Gets candidateResponse group.
     *
     * @return candidateResponse group
     * @throws QtiNodeGroupException if group is not found
     */
    public CandidateResponseGroup getCandidateResponseGroup() {
        return (CandidateResponseGroup) get(CandidateResponse.QTI_CLASS_NAME);
    }

    /**
     * Gets stylesheet group.
     *
     * @return stylesheet group
     * @throws QtiNodeGroupException if group is not found
     */
    public StylesheetGroup getStylesheetGroup() {
        return (StylesheetGroup) get(Stylesheet.QTI_CLASS_NAME);
    }

    /**
     * Gets modalFeedback group.
     *
     * @return modalFeedback group
     * @throws QtiNodeGroupException if group is not found
     */
    public ModalFeedbackGroup getModalFeedbackGroup() {
        return (ModalFeedbackGroup) get(ModalFeedback.QTI_CLASS_NAME);
    }
}
