<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Model:

assessment
assessmentStatusReport
assessmentRouting (action -> URL)
primaryRouting (action -> URL)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<c:set var="nonTerminatedCandidateRoleSessionCount" value="${assessmentStatusReport.nonTerminatedCandidateRoleSessionCount}" scope="request"/>
<page:page title="Replace Assessment Package Content">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['dashboard'])}">QTIWorks Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Assessment Manager</a> &#xbb;
    </nav>
    <h2>
      <span class="assessmentLabel">Assessment&#xa0;${utils:formatAssessmentType(assessment)}</span>
      <a href="${utils:escapeLink(assessmentRouting['show'])}">${fn:escapeXml(utils:formatAssessmentFileName(assessmentPackage))}</a>
      &#xbb; Replace Package Content
    </h2>
    <div class="hints">
      <p>
        This lets you upload new QTI to replace what we have already stored in the system. You'll probably
        want to do this when trying out and/or debugging your own assessments that you are writing or generating
        in another system.
      </p>
    </div>
  </header>

  <c:if test="${nonTerminatedCandidateRoleSessionCount>0}">
    <p class="warningMessage">
      <c:choose>
        <c:when test="${nonTerminatedCandidateRoleSessionCount>1}">
          There are ${nonTerminatedCandidateRoleSessionCount} candidate sessions running on this Assessment.
          These will be terminated if you continue replacing the Assessment Package content.
        </c:when>
        <c:otherwise>
          There is ${nonTerminatedCandidateRoleSessionCount} candidate session running on this Assessment.
          This will be terminated if you continue replacing the Assessment Package content.
        </c:otherwise>
      </c:choose>
    </p>
  </c:if>

  <form:form method="post" acceptCharset="UTF-8" enctype="multipart/form-data" commandName="uploadAssessmentPackageCommand"
    onsubmit="return ${nonTerminatedCandidateRoleSessionCount}==0 || confirm('Are you sure? The will terminate ${nonTerminatedCandidateRoleSessionCount} running candidate session(s).')">

    <%-- Show any form validation errors discovered --%>
    <form:errors element="div" cssClass="formErrors" path="*"/>

    <fieldset>
      <div class="grid_1">
        <div class="bigStatus">1</div>
      </div>
      <div class="grid_5">
        <label for="file">Select a Content Package ZIP file or Assessment Item XML file to upload and store:</label>
        <br/>
        <form:input path="file" type="file"/>
      </div>
      <div class="grid_6">
        <aside>
          <p>
            As before, you may upload any of the following:
          </p>
          <ul>
            <li>An IMS Content Package containing a QTI 2.1 Assessment Item plus any related resources, such as images, response processing templates...</li>
            <li>An IMS Content Package containing a QTI 2.1 Assessment Test, its Assessment Items, plus any related resources.</li>
            <li>A self-contained QTI 2.1 Assessment Item XML file.</li>
          </ul>
          <p>
            Please additiionally note that you MUST upload the same 'type' of
            assessment. I.e. you must replace an item with an item or a test with
            a test.
          <p>
        </aside>
      </div>
    </fieldset>
    <div class="clear"></div>
    <fieldset>
      <div class="grid_1">
        <div class="bigStatus">2</div>
      </div>
      <div class="grid_11">
        <label for="submit">Hit "Replace Assessment Package Content"</label>
        <br/>
        <input id="submit" name="submit" type="submit" value="Replace Assessment Package Content"/>
      </div>
    </fieldset>
  </form:form>
  <p class="floatRight">
    <a href="${utils:escapeLink(assessmentRouting['show'])}">Cancel and return to Assessment</a>
  </p>


</page:page>

