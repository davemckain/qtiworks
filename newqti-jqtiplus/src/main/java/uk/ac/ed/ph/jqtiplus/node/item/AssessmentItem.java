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
package uk.ac.ed.ph.jqtiplus.node.item;

import uk.ac.ed.ph.jqtiplus.JQTI;
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
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.IdentifiableObject;
import uk.ac.ed.ph.jqtiplus.node.content.ItemBody;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.item.template.declaration.TemplateDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateProcessing;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;

import java.net.URI;
import java.util.List;

/**
 * AssessmentItem encompasses the information that is presented to A candidate and information about how to score the item.
 * Scoring takes place when candidate responses are transformed into outcomes by response processing rules. It is sometimes
 * desirable to have several different items that appear the same to the candidate but which are scored differently.
 * In this specification, these are distinct items by definition and must therefore have distinct identifiers.
 * 
 * @author Jonathon Hare
 * @author Jiri Kajaba
 */
public class AssessmentItem extends AbstractNode implements AssessmentObject {

    private static final long serialVersionUID = 4723748473878175232L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "assessmentItem";

    /** Name of title attribute in xml schema. */
    public static final String ATTR_TITLE_NAME = "title";

    /** Name of label attribute in xml schema. */
    public static final String ATTR_LABEL_NAME = "label";

    /** Default value of label attribute. */
    public static final String ATTR_LABEL_DEFAULT_VALUE = null;

    /** Name of lang attribute in xml schema. */
    public static final String ATTR_LANG_NAME = "lang";

    /** Default value of lang attribute. */
    public static final String ATTR_LANG_DEFAULT_VALUE = null;

    /** Name of adaptive attribute in xml schema. */
    public static final String ATTR_ADAPTIVE_NAME = "adaptive";

    /** Name of timeDependant attribute in xml schema. */
    public static final String ATTR_TIME_DEPENDENT_NAME = "timeDependent";

    /** Name of toolName attribute in xml schema. */
    public static final String ATTR_TOOL_NAME_NAME = "toolName";

    /** Default value of toolName attribute. */
    public static final String ATTR_TOOL_NAME_DEFAULT_VALUE = null;

    /** Name of toolVersion attribute in xml schema. */
    public static final String ATTR_TOOL_VERSION_NAME = "toolVersion";

    /** Default value of toolVersion attribute. */
    public static final String ATTR_TOOL_VERSION_DEFAULT_VALUE = null;

    /** Name of completion status built-in variable. */
    public static final String VARIABLE_COMPLETION_STATUS = "completionStatus";

    /** Name of completion status built-in variable. */
    public static final Identifier VARIABLE_COMPLETION_STATUS_IDENTIFIER = new Identifier("completionStatus", false);

    /** Value of completion status built-in variable. */
    public static final String VALUE_ITEM_IS_NOT_ATTEMPTED = "not_attempted";

    /** Value of completion status built-in variable. */
    public static final String VALUE_ITEM_IS_UNKNOWN = "unknown";

    /** Value of completion status built-in variable. */
    public static final String VALUE_ITEM_IS_COMPLETED = "completed";

    /** Name of number of attempts built-in variable. */
    public static final String VARIABLE_NUMBER_OF_ATTEMPTS = "numAttempts";

    public static final Identifier VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER = new Identifier("numAttempts");

    /** Name of duration built-in variable. */
    public static final String VARIABLE_DURATION_NAME = "duration";

    public static final Identifier VARIABLE_DURATION_NAME_IDENTIFIER = new Identifier("duration");

    private URI systemId;

    private final OutcomeDeclaration completionStatusOutcomeDeclaration;

    private final ResponseDeclaration numAttemptsResponseDeclaration;

    private final ResponseDeclaration durationResponseDeclaration;

    /**
     * Constructs assessmentItem.
     */
    public AssessmentItem() {
        super(null); // Item doesn't have any parent.

        getAttributes().add(new StringAttribute(this, IdentifiableObject.ATTR_IDENTIFIER_NAME));

        getAttributes().add(new StringAttribute(this, ATTR_TITLE_NAME));
        getAttributes().add(new StringAttribute(this, ATTR_LABEL_NAME, ATTR_LABEL_DEFAULT_VALUE));
        getAttributes().add(new StringAttribute(this, ATTR_LANG_NAME, ATTR_LANG_DEFAULT_VALUE));
        getAttributes().add(new BooleanAttribute(this, ATTR_ADAPTIVE_NAME));
        getAttributes().add(new BooleanAttribute(this, ATTR_TIME_DEPENDENT_NAME));
        getAttributes().add(new StringAttribute(this, ATTR_TOOL_NAME_NAME, ATTR_TOOL_NAME_DEFAULT_VALUE));
        getAttributes().add(new StringAttribute(this, ATTR_TOOL_VERSION_NAME, ATTR_TOOL_VERSION_DEFAULT_VALUE));

        getNodeGroups().add(new ResponseDeclarationGroup(this));
        getNodeGroups().add(new OutcomeDeclarationGroup(this));
        getNodeGroups().add(new TemplateDeclarationGroup(this));

        getNodeGroups().add(new TemplateProcessingGroup(this)); // templateProcessing [0..1]
        getNodeGroups().add(new StylesheetGroup(this));         // stylesheet [0..*]
        getNodeGroups().add(new ItemBodyGroup(this));             // itemBody [0..1]
        getNodeGroups().add(new ResponseProcessingGroup(this)); // responseProcessing [0..1]
        getNodeGroups().add(new ModalFeedbackGroup(this));         // modalFeedback [*]

        //create a special declaration for the internal completionStatus variable
        completionStatusOutcomeDeclaration = new OutcomeDeclaration(this);
        completionStatusOutcomeDeclaration.setIdentifier(VARIABLE_COMPLETION_STATUS_IDENTIFIER);
        completionStatusOutcomeDeclaration.setCardinality(Cardinality.SINGLE);
        completionStatusOutcomeDeclaration.setBaseType(BaseType.IDENTIFIER);

        //create a special declaration for the internal numAttempts variable
        numAttemptsResponseDeclaration = new ResponseDeclaration(this);
        numAttemptsResponseDeclaration.setIdentifier(VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER);
        numAttemptsResponseDeclaration.setCardinality(Cardinality.SINGLE);
        numAttemptsResponseDeclaration.setBaseType(BaseType.INTEGER);

        //create a special declaration for the internal duration variable
        durationResponseDeclaration = new ResponseDeclaration(this);
        durationResponseDeclaration.setIdentifier(VARIABLE_DURATION_NAME_IDENTIFIER);
        durationResponseDeclaration.setCardinality(Cardinality.SINGLE);
        durationResponseDeclaration.setBaseType(BaseType.FLOAT);
    }

    /**
     * Convenience constructor for assessmentItem.
     * Sets the JQTI toolName and toolVersion automatically.
     * 
     * @param identifier Value of the identifier attribute.
     * @param title Value of the title attribute.
     * @param adaptive Value of the adaptive attribute.
     * @param timeDependent Value of the timeDependent attribute.
     */
    public AssessmentItem(String identifier, String title, boolean adaptive, boolean timeDependent) {
        this();

        setIdentifier(identifier);
        setTitle(title);
        setAdaptive(adaptive);
        setTimeDependent(timeDependent);

        setToolName(JQTI.TOOL_NAME);
        setToolVersion(JQTI.TOOL_VERSION);
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    @Override
    public URI getSystemId() {
        return systemId;
    }

    @Override
    public void setSystemId(URI systemId) {
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
        return getAttributes().getStringAttribute(IdentifiableObject.ATTR_IDENTIFIER_NAME).getValue();
    }

    /**
     * Sets new value of identifier attribute.
     * 
     * @param identifier new value of identifier attribute
     * @see #getIdentifier
     */
    @Override
    public void setIdentifier(String identifier) {
        getAttributes().getStringAttribute(IdentifiableObject.ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    /**
     * Gets value of title attribute.
     * 
     * @return value of title attribute
     * @see #setTitle
     */
    public String getTitle() {
        return getAttributes().getStringAttribute(ATTR_TITLE_NAME).getValue();
    }

    /**
     * Sets new value of title attribute.
     * 
     * @param title new value of title attribute
     * @see #getTitle
     */
    public void setTitle(String title) {
        getAttributes().getStringAttribute(ATTR_TITLE_NAME).setValue(title);
    }

    /**
     * Gets value of label attribute.
     * 
     * @return value of label attribute
     * @see #setLabel
     */
    public String getLabel() {
        return getAttributes().getStringAttribute(ATTR_LABEL_NAME).getValue();
    }

    /**
     * Sets new value of label attribute.
     * 
     * @param label new value of label attribute
     * @see #getLabel
     */
    public void setLabel(String label) {
        getAttributes().getStringAttribute(ATTR_LABEL_NAME).setValue(label);
    }

    /**
     * Gets value of lang attribute.
     * 
     * @return value of lang attribute
     * @see #setLang
     */
    public String getLang() {
        return getAttributes().getStringAttribute(ATTR_LANG_NAME).getValue();
    }

    /**
     * Sets new value of lang attribute.
     * 
     * @param lang new value of lang attribute
     * @see #getLang
     */
    public void setLang(String lang) {
        getAttributes().getStringAttribute(ATTR_LANG_NAME).setValue(lang);
    }

    /**
     * Gets value of adaptive attribute.
     * 
     * @return value of adaptive attribute
     * @see #setAdaptive
     */
    public Boolean getAdaptive() {
        return getAttributes().getBooleanAttribute(ATTR_ADAPTIVE_NAME).getValue();
    }

    /**
     * Sets new value of adaptive attribute.
     * 
     * @param adaptive new value of adaptive attribute
     * @see #getAdaptive
     */
    public void setAdaptive(Boolean adaptive) {
        getAttributes().getBooleanAttribute(ATTR_ADAPTIVE_NAME).setValue(adaptive);
    }

    /**
     * Gets value of timeDependent attribute.
     * 
     * @return value of timeDependent attribute
     * @see #setTimeDependent
     */
    public Boolean getTimeDependent() {
        return getAttributes().getBooleanAttribute(ATTR_TIME_DEPENDENT_NAME).getValue();
    }

    /**
     * Sets new value of timeDependent attribute.
     * 
     * @param timeDependent new value of timeDependent attribute
     * @see #getTimeDependent
     */
    public void setTimeDependent(Boolean timeDependent) {
        getAttributes().getBooleanAttribute(ATTR_TIME_DEPENDENT_NAME).setValue(timeDependent);
    }

    /**
     * Gets value of toolName attribute.
     * 
     * @return value of toolName attribute
     * @see #setToolName
     */
    public String getToolName() {
        return getAttributes().getStringAttribute(ATTR_TOOL_NAME_NAME).getValue();
    }

    /**
     * Sets new value of toolName attribute.
     * 
     * @param toolName new value of toolName attribute
     * @see #getToolName
     */
    public void setToolName(String toolName) {
        getAttributes().getStringAttribute(ATTR_TOOL_NAME_NAME).setValue(toolName);
    }

    /**
     * Gets value of toolVersion attribute.
     * 
     * @return value of toolVersion attribute
     * @see #setToolVersion
     */
    public String getToolVersion() {
        return getAttributes().getStringAttribute(ATTR_TOOL_VERSION_NAME).getValue();
    }

    /**
     * Sets new value of toolVersion attribute.
     * 
     * @param toolVersion new value of toolVersion attribute
     * @see #getToolVersion
     */
    public void setToolVersion(String toolVersion) {
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
        return getNodeGroups().getModalFeedbackGroup().getModalFeedback();
    }

    //---------------------------------------------------------------

    @Override
    public VariableDeclaration getVariableDeclaration(Identifier identifier) {
        VariableDeclaration result = getResponseDeclaration(identifier);
        if (result == null) {
            result = getOutcomeDeclaration(identifier);
            if (result == null) {
                result = getTemplateDeclaration(identifier);
            }
        }
        return result;
    }

    /**
     * Gets outcomeDeclaration children.
     * NB: Doesn't include the implicitly-defined {@link #VARIABLE_COMPLETION_STATUS} variable
     * 
     * @return outcomeDeclaration children
     */
    @Override
    public List<OutcomeDeclaration> getOutcomeDeclarations() {
        return getNodeGroups().getOutcomeDeclarationGroup().getOutcomeDeclarations();
    }

    @Override
    public OutcomeDeclaration getOutcomeDeclaration(Identifier identifier) {
        return getOutcomeDeclaration(identifier.toString());
    }

    /**
     * Gets outcomeDeclaration with given identifier or null.
     * 
     * @param identifier given identifier
     * @return outcomeDeclaration with given identifier or null
     */
    public OutcomeDeclaration getOutcomeDeclaration(String identifier) {
        if (identifier.equals(VARIABLE_COMPLETION_STATUS)) {
            return completionStatusOutcomeDeclaration;
        }
        for (final OutcomeDeclaration declaration : getOutcomeDeclarations()) {
            if (identifier.equals(declaration.getIdentifier().toString())) {
                return declaration;
            }
        }
        return null;
    }

    /**
     * Gets responseDeclaration children.
     * NB: Doesn't include the implicitly-defined {@link #VARIABLE_DURATION_NAME} and {@link #VARIABLE_NUMBER_OF_ATTEMPTS} variables.
     * 
     * @return responseDeclaration children
     */
    public List<ResponseDeclaration> getResponseDeclarations() {
        return getNodeGroups().getResponseDeclarationGroup().getResponseDeclarations();
    }

    public ResponseDeclaration getResponseDeclaration(Identifier identifier) {
        return getResponseDeclaration(identifier.toString());
    }

    /**
     * Gets responseDeclaration with given identifier or null.
     * 
     * @param identifier given identifier
     * @return responseDeclaration with given identifier or null
     */
    public ResponseDeclaration getResponseDeclaration(String identifier) {
        if (identifier.equals(VARIABLE_NUMBER_OF_ATTEMPTS)) {
            return numAttemptsResponseDeclaration;
        }
        else if (identifier.equals(VARIABLE_DURATION_NAME)) {
            return durationResponseDeclaration;
        }
        for (final ResponseDeclaration declaration : getResponseDeclarations()) {
            if (identifier.equals(declaration.getIdentifier().toString())) {
                return declaration;
            }
        }
        return null;
    }

    /**
     * Gets templateDeclaration children.
     * 
     * @return templateDeclaration children
     */
    public List<TemplateDeclaration> getTemplateDeclarations() {
        return getNodeGroups().getTemplateDeclarationGroup().getTemplateDeclarations();
    }

    public TemplateDeclaration getTemplateDeclaration(Identifier identifier) {
        return getTemplateDeclaration(identifier.toString());
    }

    /**
     * Gets templateDeclaration with given identifier or null.
     * 
     * @param identifier given identifier
     * @return templateDeclaration with given identifier or null
     */
    public TemplateDeclaration getTemplateDeclaration(String identifier) {
        for (final TemplateDeclaration declaration : getTemplateDeclarations()) {
            if (identifier.equals(declaration.getIdentifier().toString())) {
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
    public void setTemplateProcessing(TemplateProcessing templateProcessing) {
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
    public void setResponseProcessing(ResponseProcessing responseProcessing) {
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
    public void setItemBody(ItemBody itemBody) {
        getNodeGroups().getItemBodyGroup().setItemBody(itemBody);
    }

    @Override
    public final String computeXPathComponent() {
        final String identifier = getIdentifier();
        if (identifier != null) {
            return getClassTag() + "[@identifier=\"" + identifier + "\"]";
        }
        return super.computeXPathComponent();
    }

    @Override
    public String toXmlString(int depth, boolean printDefaultAttributes) {
        final StringBuilder builder = new StringBuilder();

        builder.append(XML);
        builder.append(NEW_LINE);
        builder.append(super.toXmlString(depth, printDefaultAttributes));

        return builder.toString();
    }

    @Override
    public String toString() {
        return super.toString() + "(systemId=" + systemId + ")";
    }

    //---------------------------------------------------------------

    //    /**
    //     * Returns true if this item reference was correctly responded; 
    //     * Correctly responded means ALL defined responseVars match their associated correctResponse.
    //     * Returns null if any of the responseDeclarations don't have  correctResponses.
    //     *
    //     * @return true if this item reference was correctly responded; null if not all 
    //     * responseDeclarations contain correctResponses; false otherwise
    //     * @see #isIncorrect
    //     */
    //    @ToRefactor
    //    public Boolean isCorrect()
    //    {
    //        throw new QTILogicException("To be refactored");
    //        for (ResponseDeclaration responseDeclaration : getResponseDeclarations())
    //            if (responseDeclaration.getCorrectResponse() == null)
    //                return null;
    //        
    //        for (ResponseDeclaration responseDeclaration : getResponseDeclarations()) {
    //            if (!responseDeclaration.isCorrectResponse()) {
    //                return false;
    //            }
    //        }
    //
    //        return true;
    //    }
    //
    //    /**
    //     * Returns the number of correct responses 
    //     *
    //     * @return the number of correct responses 
    //     * @see #countIncorrect
    //     */
    //    @ToRefactor
    //    public int countCorrect()
    //    {
    //        throw new QTILogicException("To be refactored");
    //        int count = 0;
    //
    //        for (ResponseDeclaration responseDeclaration : getResponseDeclarations()) {
    //            if (responseDeclaration.isCorrectResponse() == Boolean.TRUE) {
    //                count++;
    //            }
    //        }
    //
    //        return count;
    //    }
    //    
    //    /**
    //     * Returns true if this item reference was incorrectly responded; 
    //     * Incorrectly responded means ANY defined responseVars didn't match their 
    //     * associated correctResponse.
    //     * 
    //     * Returns null if any of the responseDeclarations don't have correctResponses.
    //     *
    //     * @return true if this item reference was incorrectly responded; null if not all 
    //     * responseDeclarations contain correctResponses; false otherwise
    //     * @see #isCorrect
    //     */
    //    @ToRefactor
    //    public Boolean isIncorrect()
    //    {      
    //        throw new QTILogicException("To be refactored");
    //        for (ResponseDeclaration responseDeclaration : getResponseDeclarations())
    //            if (responseDeclaration.getCorrectResponse() == null)
    //                return null;
    //        
    //        for (ResponseDeclaration responseDeclaration : getResponseDeclarations()) {
    //            if (!responseDeclaration.isCorrectResponse()) {
    //                return true;
    //            }
    //        }
    //        
    //        return false;
    //    }
    //
    //    /**
    //     * Returns the number of incorrect responses 
    //     *
    //     * @return the number of incorrect responses 
    //     * @see #countIncorrect
    //     */
    //    @ToRefactor
    //    public int countIncorrect()
    //    {
    //        throw new QTILogicException("To be refactored");
    //        int count = 0;
    //
    //        for (ResponseDeclaration responseDeclaration : getResponseDeclarations()) {
    //            if (responseDeclaration.isCorrectResponse() == Boolean.FALSE) {
    //                count++;
    //            }
    //        }
    //
    //        return count;
    //    }
}
