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

import uk.ac.ed.ph.jqtiplus.exception.QTIEvaluationException;
import uk.ac.ed.ph.jqtiplus.exception.QTINodeGroupException;
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
import uk.ac.ed.ph.jqtiplus.group.result.IdentificationGroup;
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
import uk.ac.ed.ph.jqtiplus.group.test.TimeLimitGroup;
import uk.ac.ed.ph.jqtiplus.group.test.VariableMappingGroup;
import uk.ac.ed.ph.jqtiplus.group.test.WeightGroup;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
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
import uk.ac.ed.ph.jqtiplus.node.outcome.processing.OutcomeElse;
import uk.ac.ed.ph.jqtiplus.node.outcome.processing.OutcomeElseIf;
import uk.ac.ed.ph.jqtiplus.node.outcome.processing.OutcomeIf;
import uk.ac.ed.ph.jqtiplus.node.outcome.processing.OutcomeProcessing;
import uk.ac.ed.ph.jqtiplus.node.outcome.processing.OutcomeRule;
import uk.ac.ed.ph.jqtiplus.node.result.CandidateComment;
import uk.ac.ed.ph.jqtiplus.node.result.CandidateResponse;
import uk.ac.ed.ph.jqtiplus.node.result.Context;
import uk.ac.ed.ph.jqtiplus.node.result.Identification;
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
import uk.ac.ed.ph.jqtiplus.node.test.TimeLimit;
import uk.ac.ed.ph.jqtiplus.node.test.VariableMapping;
import uk.ac.ed.ph.jqtiplus.node.test.Weight;
import uk.ac.ed.ph.jqtiplus.validation.Validatable;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Container for all node groups of one node.
 * 
 * @author Jiri Kajaba
 */
public class NodeGroupList implements Validatable, Iterable<NodeGroup> {

    private static final long serialVersionUID = 4649998181277985510L;

    private static Logger logger = LoggerFactory.getLogger(NodeGroupList.class);

    /** Parent (node) of this container. */
    private final XmlNode parent;

    /** Children (groups) of this container. */
    private final List<NodeGroup> groups;

    /**
     * Constructs container.
     * 
     * @param parent parent of constructed container
     */
    public NodeGroupList(XmlNode parent) {
        this.parent = parent;
        this.groups = new ArrayList<NodeGroup>();
    }

    /**
     * Gets parent of this container.
     * 
     * @return parent of this container
     */
    public XmlNode getParent() {
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
    public Iterator<NodeGroup> iterator() {
        return groups.iterator();
    }

    /**
     * Adds given group into this container.
     * Checks duplicities in group's names.
     * 
     * @param group given group
     * @throws QTINodeGroupException if container already contains group with same name
     */
    public void add(NodeGroup group) {
        for (final NodeGroup child : groups) {
            if (child.getName().equals(group.getName())) {
                final QTINodeGroupException ex = new QTINodeGroupException("Duplicate node group name: " + group.computeXPath());
                logger.error(ex.getMessage());
                throw ex;
            }
        }

        groups.add(group);
    }

    /**
     * Adds given group into this container at given position.
     * Checks duplicities in group's names.
     * 
     * @param index position
     * @param group given group
     * @throws QTIEvaluationException if container already contains group with same name
     */
    public void add(int index, NodeGroup group) {
        groups.add(index, group);
    }

    /**
     * Loads group's children from given source node.
     * <ul>
     * <li>Unsupported (unknown) children are skipped.</li>
     * <li>Wrong order of children is ignored (children are loaded in correct order).</li>
     * </ul>
     * 
     * @param element source node
     */
    public void load(Element element, LoadingContext context) {
        for (final NodeGroup child : groups) {
            child.getChildren().clear();
            child.load(element, context);
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
    public NodeGroup get(int index) {
        return groups.get(index);
    }

    /**
     * Gets group with given name.
     * 
     * @param name name of requested group
     * @return group with given name
     * @throws QTINodeGroupException if group is not found
     */
    public NodeGroup get(String name) {
        for (final NodeGroup child : groups) {
            if (child.getName().equals(name) || child.getAllSupportedClasses().contains(name)) {
                return child;
            }
        }

        final QTINodeGroupException ex = new QTINodeGroupException("Cannot find node group: " + name);
        logger.error(ex.getMessage());
        throw ex;
    }

    /**
     * Prints groups into string.
     * 
     * @param depth left indent
     * @param printDefaultAttributes if true, prints all attributes; if false, prints only attributes with not default values
     * @return printed groups
     */
    public String toXmlString(int depth, boolean printDefaultAttributes) {
        final StringBuilder builder = new StringBuilder();

        for (final NodeGroup child : groups) {
            builder.append(child.toXmlString(depth, printDefaultAttributes));
        }

        return builder.toString();
    }

    @Override
    public void validate(ValidationContext context, AbstractValidationResult result) {
        for (final NodeGroup child : groups) {
            child.validate(context, result);
        }
    }

    /**
     * Gets expression group.
     * 
     * @return expression group
     * @throws QTINodeGroupException if group is not found
     */
    public ExpressionGroup getExpressionGroup() {
        return (ExpressionGroup) get(Expression.DISPLAY_NAME);
    }

    /**
     * Gets correctResponse group.
     * 
     * @return correctResponse group
     * @throws QTINodeGroupException if group is not found
     */
    public CorrectResponseGroup getCorrectResponseGroup() {
        return (CorrectResponseGroup) get(CorrectResponse.CLASS_TAG);
    }

    /**
     * Gets responseDeclaration group.
     * 
     * @return responseDeclaration group
     * @throws QTINodeGroupException if group is not found
     */
    public ResponseDeclarationGroup getResponseDeclarationGroup() {
        return (ResponseDeclarationGroup) get(ResponseDeclaration.CLASS_TAG);
    }

    /**
     * Gets templateDeclaration group.
     * 
     * @return templateDeclaration group
     * @throws QTINodeGroupException if group is not found
     */
    public TemplateDeclarationGroup getTemplateDeclarationGroup() {
        return (TemplateDeclarationGroup) get(TemplateDeclaration.CLASS_TAG);
    }

    /**
     * Gets defaultValue group.
     * 
     * @return defaultValue group
     * @throws QTINodeGroupException if group is not found
     */
    public DefaultValueGroup getDefaultValueGroup() {
        return (DefaultValueGroup) get(DefaultValue.CLASS_TAG);
    }

    /**
     * Gets interpolationTableEntry group.
     * 
     * @return interpolationTableEntry group
     * @throws QTINodeGroupException if group is not found
     */
    public InterpolationTableEntryGroup getInterpolationTableEntryGroup() {
        return (InterpolationTableEntryGroup) get(InterpolationTableEntry.CLASS_TAG);
    }

    /**
     * Gets lookupTable group.
     * 
     * @return lookupTable group
     * @throws QTINodeGroupException if group is not found
     */
    public LookupTableGroup getLookupTableGroup() {
        return (LookupTableGroup) get(LookupTable.DISPLAY_NAME);
    }

    /**
     * Gets matchTableEntry group.
     * 
     * @return matchTableEntry group
     * @throws QTINodeGroupException if group is not found
     */
    public MatchTableEntryGroup getMatchTableEntryGroup() {
        return (MatchTableEntryGroup) get(MatchTableEntry.CLASS_TAG);
    }

    /**
     * Gets outcomeDeclaration group.
     * 
     * @return outcomeDeclaration group
     * @throws QTINodeGroupException if group is not found
     */
    public OutcomeDeclarationGroup getOutcomeDeclarationGroup() {
        return (OutcomeDeclarationGroup) get(OutcomeDeclaration.CLASS_TAG);
    }

    /**
     * Gets outcomeElse group.
     * 
     * @return outcomeElse group
     * @throws QTINodeGroupException if group is not found
     */
    public OutcomeElseGroup getOutcomeElseGroup() {
        return (OutcomeElseGroup) get(OutcomeElse.CLASS_TAG);
    }

    /**
     * Gets outcomeElseIf group.
     * 
     * @return outcomeElseIf group
     * @throws QTINodeGroupException if group is not found
     */
    public OutcomeElseIfGroup getOutcomeElseIfGroup() {
        return (OutcomeElseIfGroup) get(OutcomeElseIf.CLASS_TAG);
    }

    /**
     * Gets outcomeIf group.
     * 
     * @return outcomeIf group
     * @throws QTINodeGroupException if group is not found
     */
    public OutcomeIfGroup getOutcomeIfGroup() {
        return (OutcomeIfGroup) get(OutcomeIf.CLASS_TAG);
    }

    /**
     * Gets outcomeProcessing group.
     * 
     * @return outcomeProcessing group
     * @throws QTINodeGroupException if group is not found
     */
    public OutcomeProcessingGroup getOutcomeProcessingGroup() {
        return (OutcomeProcessingGroup) get(OutcomeProcessing.CLASS_TAG);
    }

    /**
     * Gets outcomeRule group.
     * 
     * @return outcomeRule group
     * @throws QTINodeGroupException if group is not found
     */
    public OutcomeRuleGroup getOutcomeRuleGroup() {
        return (OutcomeRuleGroup) get(OutcomeRule.DISPLAY_NAME);
    }

    /**
     * Gets candidateComment group.
     * 
     * @return candidateComment group
     * @throws QTINodeGroupException if group is not found
     */
    public CandidateCommentGroup getCandidateCommentGroup() {
        return (CandidateCommentGroup) get(CandidateComment.CLASS_TAG);
    }

    /**
     * Gets context group.
     * 
     * @return context group
     * @throws QTINodeGroupException if group is not found
     */
    public ContextGroup getContextGroup() {
        return (ContextGroup) get(Context.CLASS_TAG);
    }

    /**
     * Gets identification group.
     * 
     * @return identification group
     * @throws QTINodeGroupException if group is not found
     */
    public IdentificationGroup getIdentificationGroup() {
        return (IdentificationGroup) get(Identification.CLASS_TAG);
    }

    /**
     * Gets itemResult group.
     * 
     * @return itemResult group
     * @throws QTINodeGroupException if group is not found
     */
    public ItemResultGroup getItemResultGroup() {
        return (ItemResultGroup) get(ItemResult.CLASS_TAG);
    }

    /**
     * Gets itemVariable group.
     * 
     * @return itemVariable group
     * @throws QTINodeGroupException if group is not found
     */
    public ItemVariableGroup getItemVariableGroup() {
        return (ItemVariableGroup) get(ItemVariable.DISPLAY_NAME);
    }

    /**
     * Gets sessionIdentifier group.
     * 
     * @return sessionIdentifier group
     * @throws QTINodeGroupException if group is not found
     */
    public SessionIdentifierGroup getSessionIdentifierGroup() {
        return (SessionIdentifierGroup) get(SessionIdentifier.CLASS_TAG);
    }

    /**
     * Gets testResult group.
     * 
     * @return testResult group
     * @throws QTINodeGroupException if group is not found
     */
    public TestResultGroup getTestResultGroup() {
        return (TestResultGroup) get(TestResult.CLASS_TAG);
    }

    /**
     * Gets fieldValue group.
     * 
     * @return fieldValue group
     * @throws QTINodeGroupException if group is not found
     */
    public FieldValueGroup getFieldValueGroup() {
        return (FieldValueGroup) get(FieldValue.CLASS_TAG);
    }

    /**
     * Gets assessmentSection group.
     * 
     * @return assessmentSection group
     * @throws QTINodeGroupException if group is not found
     */
    public AssessmentSectionGroup getAssessmentSectionGroup() {
        return (AssessmentSectionGroup) get(AssessmentSection.CLASS_TAG);
    }

    /**
     * Gets branchRule group.
     * 
     * @return branchRule group
     * @throws QTINodeGroupException if group is not found
     */
    public BranchRuleGroup getBranchRuleGroup() {
        return (BranchRuleGroup) get(BranchRule.CLASS_TAG);
    }

    /**
     * Gets itemSessionControl group.
     * 
     * @return itemSessionControl group
     * @throws QTINodeGroupException if group is not found
     */
    public ItemSessionControlGroup getItemSessionControlGroup() {
        return (ItemSessionControlGroup) get(ItemSessionControl.CLASS_TAG);
    }

    /**
     * Gets ordering group.
     * 
     * @return ordering group
     * @throws QTINodeGroupException if group is not found
     */
    public OrderingGroup getOrderingGroup() {
        return (OrderingGroup) get(Ordering.CLASS_TAG);
    }

    /**
     * Gets preCondition group.
     * 
     * @return preCondition group
     * @throws QTINodeGroupException if group is not found
     */
    public PreConditionGroup getPreConditionGroup() {
        return (PreConditionGroup) get(PreCondition.CLASS_TAG);
    }

    /**
     * Gets rubricBlock group.
     * 
     * @return rubricBlock group
     * @throws QTINodeGroupException if group is not found
     */
    public RubricBlockGroup getRubricBlockGroup() {
        return (RubricBlockGroup) get(RubricBlock.CLASS_TAG);
    }

    /**
     * Gets sectionPart group.
     * 
     * @return sectionPart group
     * @throws QTINodeGroupException if group is not found
     */
    public SectionPartGroup getSectionPartGroup() {
        return (SectionPartGroup) get(SectionPart.DISPLAY_NAME);
    }

    /**
     * Gets selection group.
     * 
     * @return selection group
     * @throws QTINodeGroupException if group is not found
     */
    public SelectionGroup getSelectionGroup() {
        return (SelectionGroup) get(Selection.CLASS_TAG);
    }

    /**
     * Gets templateDefault group.
     * 
     * @return templateDefault group
     * @throws QTINodeGroupException if group is not found
     */
    public TemplateDefaultGroup getTemplateDefaultGroup() {
        return (TemplateDefaultGroup) get(TemplateDefault.CLASS_TAG);
    }

    /**
     * Gets testFeedback group.
     * 
     * @return testFeedback group
     * @throws QTINodeGroupException if group is not found
     */
    public TestFeedbackGroup getTestFeedbackGroup() {
        return (TestFeedbackGroup) get(TestFeedback.CLASS_TAG);
    }

    /**
     * Gets testPart group.
     * 
     * @return testPart group
     * @throws QTINodeGroupException if group is not found
     */
    public TestPartGroup getTestPartGroup() {
        return (TestPartGroup) get(TestPart.CLASS_TAG);
    }

    /**
     * Gets timeLimit group.
     * 
     * @return timeLimit group
     * @throws QTINodeGroupException if group is not found
     */
    public TimeLimitGroup getTimeLimitGroup() {
        return (TimeLimitGroup) get(TimeLimit.CLASS_TAG);
    }

    /**
     * Gets variableMapping group.
     * 
     * @return variableMapping group
     * @throws QTINodeGroupException if group is not found
     */
    public VariableMappingGroup getVariableMappingGroup() {
        return (VariableMappingGroup) get(VariableMapping.CLASS_TAG);
    }

    /**
     * Gets weight group.
     * 
     * @return weight group
     * @throws QTINodeGroupException if group is not found
     */
    public WeightGroup getWeightGroup() {
        return (WeightGroup) get(Weight.CLASS_TAG);
    }


    /**
     * Gets templateElse group.
     * 
     * @return templateElse group
     * @throws QTINodeGroupException if group is not found
     */
    public TemplateElseGroup getTemplateElseGroup() {
        return (TemplateElseGroup) get(TemplateElse.CLASS_TAG);
    }

    /**
     * Gets templateElseIf group.
     * 
     * @return templateElseIf group
     * @throws QTINodeGroupException if group is not found
     */
    public TemplateElseIfGroup getTemplateElseIfGroup() {
        return (TemplateElseIfGroup) get(TemplateElseIf.CLASS_TAG);
    }

    /**
     * Gets templateIf group.
     * 
     * @return templateIf group
     * @throws QTINodeGroupException if group is not found
     */
    public TemplateIfGroup getTemplateIfGroup() {
        return (TemplateIfGroup) get(TemplateIf.CLASS_TAG);
    }

    /**
     * Gets templateProcessing group.
     * 
     * @return templateProcessing group
     * @throws QTINodeGroupException if group is not found
     */
    public TemplateProcessingGroup getTemplateProcessingGroup() {
        return (TemplateProcessingGroup) get(TemplateProcessing.CLASS_TAG);
    }

    /**
     * Gets templateProcessingRule group.
     * 
     * @return templateProcessing group
     * @throws QTINodeGroupException if group is not found
     */
    public TemplateProcessingRuleGroup getTemplateProcessingRuleGroup() {
        return (TemplateProcessingRuleGroup) get(TemplateProcessingRule.DISPLAY_NAME);
    }

    /**
     * Gets templateRule group.
     * 
     * @return templateRule group
     * @throws QTINodeGroupException if group is not found
     */
    public TemplateRuleGroup getTemplateRuleGroup() {
        return (TemplateRuleGroup) get(TemplateRule.DISPLAY_NAME);
    }

    /**
     * Gets responseRule group.
     * 
     * @return responseRule group
     * @throws QTINodeGroupException if group is not found
     */
    public ResponseRuleGroup getResponseRuleGroup() {
        return (ResponseRuleGroup) get(ResponseRule.DISPLAY_NAME);
    }

    /**
     * Gets itemBody group.
     * 
     * @return itemBody group
     * @throws QTINodeGroupException if group is not found
     */
    public ItemBodyGroup getItemBodyGroup() {
        return (ItemBodyGroup) get(ItemBody.CLASS_TAG);
    }

    /**
     * Gets responseElse group.
     * 
     * @return responseElse group
     * @throws QTINodeGroupException if group is not found
     */
    public ResponseElseGroup getResponseElseGroup() {
        return (ResponseElseGroup) get(ResponseElse.CLASS_TAG);
    }

    /**
     * Gets responseElseIf group.
     * 
     * @return responseElseIf group
     * @throws QTINodeGroupException if group is not found
     */
    public ResponseElseIfGroup getResponseElseIfGroup() {
        return (ResponseElseIfGroup) get(ResponseElseIf.CLASS_TAG);
    }

    /**
     * Gets responseIf group.
     * 
     * @return responseIf group
     * @throws QTINodeGroupException if group is not found
     */
    public ResponseIfGroup getResponseIfGroup() {
        return (ResponseIfGroup) get(ResponseIf.CLASS_TAG);
    }

    /**
     * Gets responseProcessing group.
     * 
     * @return responseProcessing group
     * @throws QTINodeGroupException if group is not found
     */
    public ResponseProcessingGroup getResponseProcessingGroup() {
        return (ResponseProcessingGroup) get(ResponseProcessing.CLASS_TAG);
    }

    /**
     * Gets mapping group.
     * 
     * @return mapping group
     * @throws QTINodeGroupException if group is not found
     */
    public MappingGroup getMappingGroup() {
        return (MappingGroup) get(Mapping.CLASS_TAG);
    }

    /**
     * Gets mapEntry group.
     * 
     * @return mapEntry group
     * @throws QTINodeGroupException if group is not found
     */
    public MapEntryGroup getMapEntryGroup() {
        return (MapEntryGroup) get(MapEntry.CLASS_TAG);
    }

    /**
     * Gets areaMapping group.
     * 
     * @return areaMapping group
     * @throws QTINodeGroupException if group is not found
     */
    public AreaMappingGroup getAreaMappingGroup() {
        return (AreaMappingGroup) get(AreaMapping.CLASS_TAG);
    }

    /**
     * Gets areaMapEntry group.
     * 
     * @return areaMapEntry group
     * @throws QTINodeGroupException if group is not found
     */
    public AreaMapEntryGroup getAreaMapEntryGroup() {
        return (AreaMapEntryGroup) get(AreaMapEntry.CLASS_TAG);
    }


    /**
     * Gets prompt group.
     * 
     * @return prompt group
     * @throws QTINodeGroupException if group is not found
     */
    public PromptGroup getPromptGroup() {
        return (PromptGroup) get(Prompt.CLASS_TAG);
    }

    /**
     * Gets simpleChoice group.
     * 
     * @return simpleChoice group
     * @throws QTINodeGroupException if group is not found
     */
    public SimpleChoiceGroup getSimpleChoiceGroup() {
        return (SimpleChoiceGroup) get(SimpleChoice.CLASS_TAG);
    }

    /**
     * Gets interaction group.
     * 
     * @return interaction group
     * @throws QTINodeGroupException if group is not found
     */
    public InteractionGroup getInteractionGroup() {
        return (InteractionGroup) get(Interaction.DISPLAY_NAME);
    }

    /**
     * Gets inlineChoice group.
     * 
     * @return inlineChoice group
     * @throws QTINodeGroupException if group is not found
     */
    public InlineChoiceGroup getInlineChoiceGroup() {
        return (InlineChoiceGroup) get(InlineChoice.CLASS_TAG);
    }

    /**
     * Gets inline group.
     * 
     * @return inline group
     * @throws QTINodeGroupException if group is not found
     */
    public InlineGroup getInlineGroup() {
        return (InlineGroup) get(Inline.DISPLAY_NAME);
    }

    /**
     * Gets block group.
     * 
     * @return block group
     * @throws QTINodeGroupException if group is not found
     */
    public BlockGroup getBlockGroup() {
        return (BlockGroup) get(Block.DISPLAY_NAME);
    }

    /**
     * Gets li group.
     * 
     * @return li group
     * @throws QTINodeGroupException if group is not found
     */
    public LiGroup getLiGroup() {
        return (LiGroup) get(Li.CLASS_TAG);
    }

    /**
     * Gets dlElement group.
     * 
     * @return dlElement group
     * @throws QTINodeGroupException if group is not found
     */
    public DlElementGroup getDlElementGroup() {
        return (DlElementGroup) get(BodyElement.DISPLAY_NAME);
    }

    /**
     * Gets flow group.
     * 
     * @return flow group
     * @throws QTINodeGroupException if group is not found
     */
    public FlowGroup getFlowGroup() {
        return (FlowGroup) get(Flow.DISPLAY_NAME);
    }

    /**
     * Gets objectFlow group.
     * 
     * @return objectFlow group
     * @throws QTINodeGroupException if group is not found
     */
    public ObjectFlowGroup getObjectFlowGroup() {
        return (ObjectFlowGroup) get(ObjectFlow.DISPLAY_NAME);
    }

    /**
     * Gets Col group.
     * 
     * @return Col group
     * @throws QTINodeGroupException if group is not found
     */
    public ColGroup getColGroup() {
        return (ColGroup) get(Col.CLASS_TAG);
    }

    /**
     * Gets Colgroup group.
     * 
     * @return Colgroup group
     * @throws QTINodeGroupException if group is not found
     */
    public ColgroupGroup getColgroupGroup() {
        return (ColgroupGroup) get(Colgroup.CLASS_TAG);
    }

    /**
     * Gets Caption group.
     * 
     * @return Caption group
     * @throws QTINodeGroupException if group is not found
     */
    public CaptionGroup getCaptionGroup() {
        return (CaptionGroup) get(Caption.CLASS_TAG);
    }

    /**
     * Gets Tbody group.
     * 
     * @return Tbody group
     * @throws QTINodeGroupException if group is not found
     */
    public TbodyGroup getTbodyGroup() {
        return (TbodyGroup) get(Tbody.CLASS_TAG);
    }

    /**
     * Gets Tfoot group.
     * 
     * @return Tfoot group
     * @throws QTINodeGroupException if group is not found
     */
    public TfootGroup getTfootGroup() {
        return (TfootGroup) get(Tfoot.CLASS_TAG);
    }

    /**
     * Gets Thead group.
     * 
     * @return Thead group
     * @throws QTINodeGroupException if group is not found
     */
    public TheadGroup getTheadGroup() {
        return (TheadGroup) get(Thead.CLASS_TAG);
    }

    /**
     * Gets Tr group.
     * 
     * @return Tr group
     * @throws QTINodeGroupException if group is not found
     */
    public TrGroup getTrGroup() {
        return (TrGroup) get(Tr.CLASS_TAG);
    }

    /**
     * Gets TableCell group.
     * 
     * @return TableCell group
     * @throws QTINodeGroupException if group is not found
     */
    public TableCellGroup getTableCellGroup() {
        return (TableCellGroup) get(TableCell.DISPLAY_NAME);
    }

    /**
     * Gets inlineStatic group.
     * 
     * @return inlineStatic group
     * @throws QTINodeGroupException if group is not found
     */
    public InlineStaticGroup getInlineStaticGroup() {
        return (InlineStaticGroup) get(InlineStatic.DISPLAY_NAME);
    }

    /**
     * Gets flowStatic group.
     * 
     * @return flowStatic group
     * @throws QTINodeGroupException if group is not found
     */
    public FlowStaticGroup getFlowStaticGroup() {
        return (FlowStaticGroup) get(FlowStatic.DISPLAY_NAME);
    }

    /**
     * Gets textOrVariable group.
     * 
     * @return textOrVariable group
     * @throws QTINodeGroupException if group is not found
     */
    public TextOrVariableGroup getTextOrVariableGroup() {
        return (TextOrVariableGroup) get(TextOrVariable.DISPLAY_NAME);
    }

    /**
     * Gets blockStatic group.
     * 
     * @return textOrVariable group
     * @throws QTINodeGroupException if group is not found
     */
    public BlockStaticGroup getBlockStaticGroup() {
        return (BlockStaticGroup) get(BlockStatic.DISPLAY_NAME);
    }

    /**
     * Gets simpleAssociableChoice group.
     * 
     * @return simpleAssociableChoice group
     * @throws QTINodeGroupException if group is not found
     */
    public SimpleAssociableChoiceGroup getSimpleAssociableChoiceGroup() {
        return (SimpleAssociableChoiceGroup) get(SimpleAssociableChoice.CLASS_TAG);
    }

    /**
     * Gets object group.
     * 
     * @return object group
     * @throws QTINodeGroupException if group is not found
     */
    public ObjectGroup getObjectGroup() {
        return (ObjectGroup) get(Object.CLASS_TAG);
    }

    /**
     * Gets gapChoice group.
     * 
     * @return gapChoice group
     * @throws QTINodeGroupException if group is not found
     */
    public GapChoiceGroup getGapChoiceGroup() {
        return (GapChoiceGroup) get(GapChoice.DISPLAY_NAME);
    }

    /**
     * Gets associableHotspot group.
     * 
     * @return associableHotspot group
     * @throws QTINodeGroupException if group is not found
     */
    public AssociableHotspotGroup getAssociableHotspotGroup() {
        return (AssociableHotspotGroup) get(AssociableHotspot.CLASS_TAG);
    }

    /**
     * Gets gapImg group.
     * 
     * @return gapImg group
     * @throws QTINodeGroupException if group is not found
     */
    public GapImgGroup getGapImgGroup() {
        return (GapImgGroup) get(GapImg.CLASS_TAG);
    }

    /**
     * Gets hotspotChoice group.
     * 
     * @return hotspotChoice group
     * @throws QTINodeGroupException if group is not found
     */
    public HotspotChoiceGroup getHotspotChoiceGroup() {
        return (HotspotChoiceGroup) get(HotspotChoice.CLASS_TAG);
    }

    /**
     * Gets simpleMatchSet group.
     * 
     * @return simpleMatchSet group
     * @throws QTINodeGroupException if group is not found
     */
    public SimpleMatchSetGroup getSimpleMatchSetGroup() {
        return (SimpleMatchSetGroup) get(SimpleMatchSet.CLASS_TAG);
    }

    /**
     * Gets positionObjectInteraction group.
     * 
     * @return positionObjectInteraction group
     * @throws QTINodeGroupException if group is not found
     */
    public PositionObjectInteractionGroup getPositionObjectInteractionGroup() {
        return (PositionObjectInteractionGroup) get(PositionObjectInteraction.CLASS_TAG);
    }

    /**
     * Gets candidateResponse group.
     * 
     * @return candidateResponse group
     * @throws QTINodeGroupException if group is not found
     */
    public CandidateResponseGroup getCandidateResponseGroup() {
        return (CandidateResponseGroup) get(CandidateResponse.CLASS_TAG);
    }

    /**
     * Gets stylesheet group.
     * 
     * @return stylesheet group
     * @throws QTINodeGroupException if group is not found
     */
    public StylesheetGroup getStylesheetGroup() {
        return (StylesheetGroup) get(Stylesheet.CLASS_TAG);
    }

    /**
     * Gets modalFeedback group.
     * 
     * @return modalFeedback group
     * @throws QTINodeGroupException if group is not found
     */
    public ModalFeedbackGroup getModalFeedbackGroup() {
        return (ModalFeedbackGroup) get(ModalFeedback.CLASS_TAG);
    }
}
