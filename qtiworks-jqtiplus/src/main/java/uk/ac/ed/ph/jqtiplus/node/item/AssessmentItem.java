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
package uk.ac.ed.ph.jqtiplus.node.item;

import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.group.item.ItemBodyGroup;
import uk.ac.ed.ph.jqtiplus.group.item.ModalFeedbackGroup;
import uk.ac.ed.ph.jqtiplus.group.item.StylesheetGroup;
import uk.ac.ed.ph.jqtiplus.group.item.response.declaration.ResponseDeclarationGroup;
import uk.ac.ed.ph.jqtiplus.group.item.response.processing.ResponseProcessingGroup;
import uk.ac.ed.ph.jqtiplus.group.item.template.declaration.TemplateDeclarationGroup;
import uk.ac.ed.ph.jqtiplus.group.item.template.processing.TemplateProcessingGroup;
import uk.ac.ed.ph.jqtiplus.group.outcome.declaration.OutcomeDeclarationGroup;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.IdentifiableNode;
import uk.ac.ed.ph.jqtiplus.node.content.ItemBody;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.item.template.declaration.TemplateDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateProcessing;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;

import java.net.URI;
import java.util.List;

import javax.xml.XMLConstants;

/**
 * AssessmentItem encompasses the information that is presented to a candidate and information about how to score the item.
 * Scoring takes place when candidate responses are transformed into outcomes by response processing rules. It is sometimes
 * desirable to have several different items that appear the same to the candidate but which are scored differently.
 * In this specification, these are distinct items by definition and must therefore have distinct identifiers.
 *
 * @author Jonathon Hare
 * @author Jiri Kajaba
 * @author David McKain
 */
public class AssessmentItem extends AbstractNode implements AssessmentObject {

    private static final long serialVersionUID = 4723748473878175232L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "assessmentItem";

    /** Name of title attribute in xml schema. */
    public static final String ATTR_TITLE_NAME = "title";

    /** Name of label attribute in xml schema. */
    public static final String ATTR_LABEL_NAME = "label";

    /** Name of lang attribute in xml schema. */
    public static final String ATTR_LANG_NAME = "lang";

    /** Name of adaptive attribute in xml schema. */
    public static final String ATTR_ADAPTIVE_NAME = "adaptive";

    /** Name of timeDependant attribute in xml schema. */
    public static final String ATTR_TIME_DEPENDENT_NAME = "timeDependent";

    /** Name of toolName attribute in xml schema. */
    public static final String ATTR_TOOL_NAME_NAME = "toolName";

    /** Name of toolVersion attribute in xml schema. */
    public static final String ATTR_TOOL_VERSION_NAME = "toolVersion";

    /** (Implicit) declaration for <code>completionStatus</code> */
    private final OutcomeDeclaration completionStatusOutcomeDeclaration;

    /** (Implicit) declaration for <code>numAttempts</code> */
    private final ResponseDeclaration numAttemptsResponseDeclaration;

    /** (Implicit) declaraiton for <code>duration</code> */
    private final ResponseDeclaration durationResponseDeclaration;

    /** System ID of this RootNode (optional) */
    private URI systemId;

    public AssessmentItem() {
        super(null, QTI_CLASS_NAME); // Item doesn't have any parent.

        getAttributes().add(new StringAttribute(this, IdentifiableNode.ATTR_IDENTIFIER_NAME, true));

        getAttributes().add(new StringAttribute(this, ATTR_TITLE_NAME, true));
        getAttributes().add(new StringAttribute(this, ATTR_LABEL_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_LANG_NAME, XMLConstants.XML_NS_URI, null, false));
        getAttributes().add(new BooleanAttribute(this, ATTR_ADAPTIVE_NAME, true));
        getAttributes().add(new BooleanAttribute(this, ATTR_TIME_DEPENDENT_NAME, true));
        getAttributes().add(new StringAttribute(this, ATTR_TOOL_NAME_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_TOOL_VERSION_NAME, false));

        getNodeGroups().add(new ResponseDeclarationGroup(this));
        getNodeGroups().add(new OutcomeDeclarationGroup(this));
        getNodeGroups().add(new TemplateDeclarationGroup(this));

        getNodeGroups().add(new TemplateProcessingGroup(this)); // templateProcessing [0..1]
        getNodeGroups().add(new StylesheetGroup(this));         // stylesheet [0..*]
        getNodeGroups().add(new ItemBodyGroup(this));             // itemBody [0..1]
        getNodeGroups().add(new ResponseProcessingGroup(this)); // responseProcessing [0..1]
        getNodeGroups().add(new ModalFeedbackGroup(this));         // modalFeedback [*]

        /* create a special declaration for the internal completionStatus variable */
        completionStatusOutcomeDeclaration = new OutcomeDeclaration(this);
        completionStatusOutcomeDeclaration.setIdentifier(QtiConstants.VARIABLE_COMPLETION_STATUS_IDENTIFIER);
        completionStatusOutcomeDeclaration.setCardinality(Cardinality.SINGLE);
        completionStatusOutcomeDeclaration.setBaseType(BaseType.IDENTIFIER);

        /* create a special declaration for the internal numAttempts variable */
        numAttemptsResponseDeclaration = new ResponseDeclaration(this);
        numAttemptsResponseDeclaration.setIdentifier(QtiConstants.VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER);
        numAttemptsResponseDeclaration.setCardinality(Cardinality.SINGLE);
        numAttemptsResponseDeclaration.setBaseType(BaseType.INTEGER);

        /* create a special declaration for the internal duration variable */
        durationResponseDeclaration = new ResponseDeclaration(this);
        durationResponseDeclaration.setIdentifier(QtiConstants.VARIABLE_DURATION_IDENTIFIER);
        durationResponseDeclaration.setCardinality(Cardinality.SINGLE);
        durationResponseDeclaration.setBaseType(BaseType.FLOAT);
    }

    @Override
    public AssessmentObjectType getType() {
        return AssessmentObjectType.ASSESSMENT_ITEM;
    }

    @Override
    public URI getSystemId() {
        return systemId;
    }

    @Override
    public void setSystemId(final URI systemId) {
        this.systemId = systemId;
    }

    /**
     * Gets value of identifier attribute.
     *
     * @return value of identifier attribute
     * @see #setIdentifier
     */
    @Override
    public String getIdentifier() {
        return getAttributes().getStringAttribute(IdentifiableNode.ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    /**
     * Sets new value of identifier attribute.
     *
     * @param identifier new value of identifier attribute
     * @see #getIdentifier
     */
    @Override
    public void setIdentifier(final String identifier) {
        getAttributes().getStringAttribute(IdentifiableNode.ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    /**
     * Gets value of title attribute.
     *
     * @return value of title attribute
     * @see #setTitle
     */
    @Override
    public String getTitle() {
        return getAttributes().getStringAttribute(ATTR_TITLE_NAME).getComputedValue();
    }

    /**
     * Sets new value of title attribute.
     *
     * @param title new value of title attribute
     * @see #getTitle
     */
    @Override
    public void setTitle(final String title) {
        getAttributes().getStringAttribute(ATTR_TITLE_NAME).setValue(title);
    }

    /**
     * Gets value of label attribute.
     *
     * @return value of label attribute
     * @see #setLabel
     */
    public String getLabel() {
        return getAttributes().getStringAttribute(ATTR_LABEL_NAME).getComputedValue();
    }

    /**
     * Sets new value of label attribute.
     *
     * @param label new value of label attribute
     * @see #getLabel
     */
    public void setLabel(final String label) {
        getAttributes().getStringAttribute(ATTR_LABEL_NAME).setValue(label);
    }

    /**
     * Gets value of lang attribute.
     *
     * @return value of lang attribute
     * @see #setLang
     */
    public String getLang() {
        return getAttributes().getStringAttribute(ATTR_LANG_NAME).getComputedValue();
    }

    /**
     * Sets new value of lang attribute.
     *
     * @param lang new value of lang attribute
     * @see #getLang
     */
    public void setLang(final String lang) {
        getAttributes().getStringAttribute(ATTR_LANG_NAME).setValue(lang);
    }

    /**
     * Gets value of adaptive attribute.
     *
     * @return value of adaptive attribute
     * @see #setAdaptive
     */
    public boolean getAdaptive() {
        return getAttributes().getBooleanAttribute(ATTR_ADAPTIVE_NAME).getComputedNonNullValue();
    }

    /**
     * Sets new value of adaptive attribute.
     *
     * @param adaptive new value of adaptive attribute
     * @see #getAdaptive
     */
    public void setAdaptive(final Boolean adaptive) {
        getAttributes().getBooleanAttribute(ATTR_ADAPTIVE_NAME).setValue(adaptive);
    }

    /**
     * Gets value of timeDependent attribute.
     *
     * @return value of timeDependent attribute
     * @see #setTimeDependent
     */
    public boolean getTimeDependent() {
        return getAttributes().getBooleanAttribute(ATTR_TIME_DEPENDENT_NAME).getComputedNonNullValue();
    }

    /**
     * Sets new value of timeDependent attribute.
     *
     * @param timeDependent new value of timeDependent attribute
     * @see #getTimeDependent
     */
    public void setTimeDependent(final Boolean timeDependent) {
        getAttributes().getBooleanAttribute(ATTR_TIME_DEPENDENT_NAME).setValue(timeDependent);
    }

    /**
     * Gets value of toolName attribute.
     *
     * @return value of toolName attribute
     * @see #setToolName
     */
    @Override
    public String getToolName() {
        return getAttributes().getStringAttribute(ATTR_TOOL_NAME_NAME).getComputedValue();
    }

    /**
     * Sets new value of toolName attribute.
     *
     * @param toolName new value of toolName attribute
     * @see #getToolName
     */
    @Override
    public void setToolName(final String toolName) {
        getAttributes().getStringAttribute(ATTR_TOOL_NAME_NAME).setValue(toolName);
    }

    /**
     * Gets value of toolVersion attribute.
     *
     * @return value of toolVersion attribute
     * @see #setToolVersion
     */
    @Override
    public String getToolVersion() {
        return getAttributes().getStringAttribute(ATTR_TOOL_VERSION_NAME).getComputedValue();
    }

    /**
     * Sets new value of toolVersion attribute.
     *
     * @param toolVersion new value of toolVersion attribute
     * @see #getToolVersion
     */
    @Override
    public void setToolVersion(final String toolVersion) {
        getAttributes().getStringAttribute(ATTR_TOOL_VERSION_NAME).setValue(toolVersion);
    }

    /**
     * Gets stylesheet children.
     *
     * @return stylesheet children
     */
    public List<Stylesheet> getStylesheets() {
        return getNodeGroups().getStylesheetGroup().getStylesheets();
    }

    /**
     * Gets modalFeedback children.
     *
     * @return modalFeedback children
     */
    public List<ModalFeedback> getModalFeedbacks() {
        return getNodeGroups().getModalFeedbackGroup().getModalFeedbacks();
    }

    //---------------------------------------------------------------
    // Built-in variables

    /**
     * Returns {@link ResponseDeclaration} for the implicitly-defined
     * {@link QtiConstants#VARIABLE_DURATION_IDENTIFIER} variable
     */
    public ResponseDeclaration getDurationResponseDeclaration() {
        return durationResponseDeclaration;
    }

    /**
     * Returns {@link ResponseDeclaration} for the implicitly-defined
     * {@link QtiConstants#VARIABLE_NUMBER_OF_ATTEMPTS_NAME} variable
     */
    public ResponseDeclaration getNumAttemptsResponseDeclaration() {
        return numAttemptsResponseDeclaration;
    }

    /**
     * Returns {@link OutcomeDeclaration} for the implicitly-defined
     * {@link QtiConstants#VARIABLE_COMPLETION_STATUS_IDENTIFIER} variable
     */
    public OutcomeDeclaration getCompletionStatusOutcomeDeclaration() {
        return completionStatusOutcomeDeclaration;
    }

    //---------------------------------------------------------------

    /**
     * Gets all explicitly-defined outcomeDeclaration children.
     * NB: Doesn't include the implicitly-defined
     * {@link QtiConstants#VARIABLE_COMPLETION_STATUS_NAME} variable
     *
     * @return outcomeDeclaration children
     */
    @Override
    public List<OutcomeDeclaration> getOutcomeDeclarations() {
        return getNodeGroups().getOutcomeDeclarationGroup().getOutcomeDeclarations();
    }

    /**
     * Gets (first) explicitly-defined outcomeDeclaration with given identifier,
     * or null if no such declaration exists.
     * <p>
     * NB: Doesn't include the implicitly-defined
     * {@link QtiConstants#VARIABLE_COMPLETION_STATUS_NAME} variable
     *
     * @param identifier given identifier
     * @return outcomeDeclaration with given identifier or null
     *
     * @see #getCompletionStatusOutcomeDeclaration()
     */
    @Override
    public OutcomeDeclaration getOutcomeDeclaration(final Identifier identifier) {
        Assert.notNull(identifier);
        for (final OutcomeDeclaration declaration : getOutcomeDeclarations()) {
            if (identifier.equals(declaration.getIdentifier())) {
                return declaration;
            }
        }
        return null;
    }

    /**
     * Gets all responseDeclaration children.
     * <p>
     * NB: Doesn't include the implicitly-defined {@link QtiConstants#VARIABLE_DURATION_NAME}
     * and {@link QtiConstants#VARIABLE_NUMBER_OF_ATTEMPTS_NAME} variables.
     *
     * @return responseDeclaration children
     */
    public List<ResponseDeclaration> getResponseDeclarations() {
        return getNodeGroups().getResponseDeclarationGroup().getResponseDeclarations();
    }

    /**
     * Gets (first) explicitly-defined responseDeclaration with given identifier,
     * or null if no such variable is defined.
     * <p>
     * NB: Doesn't include the implicitly-defined {@link QtiConstants#VARIABLE_DURATION_NAME}
     * and {@link QtiConstants#VARIABLE_NUMBER_OF_ATTEMPTS_NAME} variables.
     *
     * @param identifier given identifier
     * @return responseDeclaration with given identifier or null
     */
    public ResponseDeclaration getResponseDeclaration(final Identifier identifier) {
        Assert.notNull(identifier);
        for (final ResponseDeclaration declaration : getResponseDeclarations()) {
            if (identifier.equals(declaration.getIdentifier())) {
                return declaration;
            }
        }
        return null;
    }

    /**
     * Gets all templateDeclaration children.
     *
     * @return templateDeclaration children
     */
    public List<TemplateDeclaration> getTemplateDeclarations() {
        return getNodeGroups().getTemplateDeclarationGroup().getTemplateDeclarations();
    }


    /**
     * Gets (first) templateDeclaration with given identifier,
     * or null if no such variable is defined.
     *
     * @param identifier given identifier
     * @return templateDeclaration with given identifier or null
     */
    public TemplateDeclaration getTemplateDeclaration(final Identifier identifier) {
        Assert.notNull(identifier);
        for (final TemplateDeclaration declaration : getTemplateDeclarations()) {
            if (identifier.equals(declaration.getIdentifier())) {
                return declaration;
            }
        }
        return null;
    }

    /**
     * Gets templateProcessing child.
     *
     * @return templateProcessing child
     * @see #setTemplateProcessing
     */
    public TemplateProcessing getTemplateProcessing() {
        return getNodeGroups().getTemplateProcessingGroup().getTemplateProcessing();
    }

    /**
     * Sets new templateProcessing child.
     *
     * @param templateProcessing new templateProcessing child
     * @see #getTemplateProcessing
     */
    public void setTemplateProcessing(final TemplateProcessing templateProcessing) {
        getNodeGroups().getTemplateProcessingGroup().setTemplateProcessing(templateProcessing);
    }

    /**
     * Gets responseProcessing child.
     *
     * @return templateProcessing child
     * @see #setTemplateProcessing
     */
    public ResponseProcessing getResponseProcessing() {
        return getNodeGroups().getResponseProcessingGroup().getResponseProcessing();
    }

    /**
     * Sets new responseProcessing child.
     *
     * @param responseProcessing new responseProcessing child
     * @see #getResponseProcessing
     */
    public void setResponseProcessing(final ResponseProcessing responseProcessing) {
        getNodeGroups().getResponseProcessingGroup().setResponseProcessing(responseProcessing);
    }

    /**
     * Gets itemBody child.
     *
     * @return itemBody child
     * @see #setItemBody
     */
    public ItemBody getItemBody() {
        return getNodeGroups().getItemBodyGroup().getItemBody();
    }

    /**
     * Sets new itemBody child.
     *
     * @param itemBody new itemBody child
     * @see #getItemBody
     */
    public void setItemBody(final ItemBody itemBody) {
        getNodeGroups().getItemBodyGroup().setItemBody(itemBody);
    }

    @Override
    public final String computeXPathComponent() {
        final String identifier = getIdentifier();
        if (identifier != null) {
            return getQtiClassName() + "[@identifier=\"" + identifier + "\"]";
        }
        return super.computeXPathComponent();
    }

    @Override
    public String toString() {
        return super.toString()
                + "(systemId=" + systemId
                + ")";
    }
}
