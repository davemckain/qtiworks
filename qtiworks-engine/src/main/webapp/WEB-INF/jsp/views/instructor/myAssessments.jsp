<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Lists Assessments owned by caller

Model:

assessmentList

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="title" value="My Assessments" />
<%@ include file="/WEB-INF/jsp/includes/header.jspf" %>

<h2>My Assessments</h2>

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
            <td><a href="<c:url value='/web/instructor/assessment/${assessment.id}'/>">Show</a></td>
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
  <li><a href="uploadAssessment">Upload a new assessment</a></li>
</ul>


<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>

