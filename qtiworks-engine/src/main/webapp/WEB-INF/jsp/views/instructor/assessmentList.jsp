<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Lists Assessments owned by caller

Model:

assessmentList
assessmentRouting: aid -> action -> URL
instructorAssessmentRouting

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Your Assessments">

  <h2>Your Assessments</h2>

  <c:choose>
    <c:when test="${!empty assessmentList}">
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Title</th>
            <th>Created</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="assessment" items="${assessmentList}">
            <tr>
              <td><c:out value="${assessment.name}"/></td>
              <td><c:out value="${assessment.title}"/></td>
              <td><c:out value="${assessment.creationTime}"/></td>
              <td><a href="${utils:escapeLink(assessmentRouting[assessment.id]['show'])}">Show</a></td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <p>You have not uploaded any assessments yet</p>
    </c:otherwise>
  </c:choose>

  <h3>Actions</h3>
  <ul>
    <li><a href="${utils:escapeLink(instructorAssessmentRouting['uploadAssessment'])}">Upload a new assessment</a></li>
  </ul>

</page:page>

