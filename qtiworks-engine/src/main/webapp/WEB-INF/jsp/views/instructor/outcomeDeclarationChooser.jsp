<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

LTI result Outcome Declaration selector

Additional model attrs:

assessment
outcomeDeclarationList
assessmentRouting (action -> URL)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Select LTI result outcome declaration">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/instructor/')}">QTIWorks Dashboard</a> &#xbb;
    <a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Your assessments</a> &#xbb;
    <a href="${utils:escapeLink(assessmentRouting['show'])}">Assessment '${fn:escapeXml(assessment.name)}'</a>
  </nav>
  <h2>LTI result outcome declaration selection</h2>

  <c:choose>
    <c:when test="${!empty outcomeDeclarationList}">
      <p class="hints">
        Select which QTI outcome variable to use for returning results to LTI Tool Consumers
        when running this Assessment.
      </p>
      <form:form method="post" acceptCharset="UTF-8" commandName="assessmentLtiOutcomesSettingsTemplate">

        <%-- Show any form validation errors discovered --%>
        <form:errors element="div" cssClass="formErrors" path="*"/>

        <ul>
          <%-- FINISH ME! --%>
          <c:forEach var="outcomeDeclaration" items="${outcomeDeclarationList}">
            <c:set var="normalMinimum" value="${outcomeDeclaration.normalMinimum}"/>
            <c:set var="normalMaximum" value="${outcomeDeclaration.normalMaximum}"/>
            <li>
            <form:radiobutton id="outcome_${outcomeDeclaration.identifier}" path="resultOutcomeIdentifier" value="${outcomeDeclaration.identifier}"/>
                ${outcomeDeclaration.identifier}
              <script>
                $('#outcome_${outcomeDeclaration.identifier}').click(function() {
                  $('#resultMinimum').val('Hello');
                  $('#resultMaximum').val('There');
                });
              </script>
            </li>
          </c:forEach>
        </ul>

        Minimum possible score: <form:input path="resultMinimum" id="resultMinimum"/>
        Maximum possible score: <form:input path="resultMaximum" id="resultMaximum"/>
      </form:form>
    </c:when>
    <c:otherwise>
      <p class="error">
        This Assessment's package content need fixed before you can perform this action.
      </p>
    </c:otherwise>
  </c:choose>

</page:page>
