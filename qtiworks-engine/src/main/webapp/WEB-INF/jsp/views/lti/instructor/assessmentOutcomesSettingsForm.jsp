<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

LTI result outcome settings form

Additional model attrs:

assessmentLtiOutcomesSettingsTemplate (form backing object)
outcomeDeclarationList

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="LTI assessment outcomes settings">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">Assessment Launch Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Assessment Library</a> &#xbb;
      <a href="${utils:escapeLink(assessmentRouting['show'])}">Assessment ${fn:escapeXml(assessment.name)}</a>
    </nav>
    <h2>LTI assessment outcomes settings</h2>
    <div class="hints">
      <p>
        This form lets you select which QTI outcome variable to use for returning results to LTI Tool Consumers
        when running this Assessment. You must provide this information if you want QTIWorks to return results
        for this Assessment.
      </p>
    </div>
  </header>

  <c:choose>
    <c:when test="${!empty outcomeDeclarationList}">
      <form:form cssClass="deliverySettings" method="post" acceptCharset="UTF-8" commandName="assessmentLtiOutcomesSettingsTemplate">

        <%-- Show any form validation errors discovered --%>
        <form:errors element="div" cssClass="formErrors" path="*"/>

        <fieldset>
          <legend>Outcome Variable Selection</legend>
          <div class="stdFormRow">
            <div class="grid_1">
              <div class="workflowStep">1<span class="required">*</span></div>
            </div>
            <div class="grid_3">
              <label for="title">Selected outcome variable:</label>
            </div>
            <div class="grid_3">
              <form:select path="resultOutcomeIdentifier" id="resultOutcomeIdentifier">
                <c:forEach var="outcomeDeclaration" items="${outcomeDeclarationList}">
                  <form:option value="${outcomeDeclaration.identifier}"/>
                </c:forEach>
              </form:select>
            </div>
            <div class="grid_5">
              <aside>
                Choose which outcome variable you want to use as the 'result' of this Assessment.
                Only QTI variables with single cardinality and float baseType are listed here.
              </aside>
            </div>
          </div>
          <div class="clear"></div>
        </fieldset>

        <fieldset>
          <legend>Score normalisation</legend>
            <div class="stdFormRow">
              <div class="grid_1">
                <div class="workflowStep">2<span class="required">*</span></div>
              </div>
              <div class="grid_3">
                <label for="prompt">Minimum possible score:</label>
              </div>
              <div class="grid_3">
               <form:input path="resultMinimum" id="resultMinimum"/>
              </div>
              <div class="grid_5">
                <aside>
                  LTI requires scores to be normalised between 0.0 and 1.0. In order for
                  us to do this for you, you need to specify the minimum and maximum possible scores.
                </aside>
              </div>
            </div>
            <div class="clear"></div>
            <div class="stdFormRow">
              <div class="grid_1">
                <div class="workflowStep">3<span class="required">*</span></div>
              </div>
              <div class="grid_3">
                <label for="prompt">Maximum possible score:</label>
              </div>
              <div class="grid_3">
               <form:input path="resultMaximum" id="resultMaximum"/>
              </div>
            </div>
            <div class="clear"></div>
          </legend>
        </fieldset>
        <fieldset>
          <div class="stdFormRow">
            <div class="grid_1">
              <div class="workflowStep">3<span class="required">*</span></div>
            </div>
            <div class="grid_3">
              <label for="submit">Hit "Save" to save these settings</label>
            </div>
            <div class="grid_8">
              <input name="submit" type="submit" value="Save"/>
            </div>
          </div>
          <div class="clear"></div>
        </fieldset>
        <div class="hints">
          (<span class="required">*</span> denotes a required field.)
        </div>
      </form:form>
    </c:when>
    <c:when test="${outcomeDeclarationList==null}">
      <p class="errorMessage">
        This Assessment's package data need fixed before you can perform this action.
      </p>
    </c:when>
    <c:otherwise>
      <p class="errorMessage">
        This Assessment does not define any outcome variables with single cardinality
        and float baseType, so no results can be returned here.
      </p>
    </c:otherwise>
  </c:choose>

</page:ltipage>
