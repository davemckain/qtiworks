<%--

This fragment formats the model validation items recorded within an
AssessmentObjectValidationResult

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

--%>
<%@ tag body-content="empty" %>
<%@ taglib prefix="utils" uri="http://www.ph.ed.ac.uk/utils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="validator" tagdir="/WEB-INF/tags/validator" %>
<%@ attribute name="validationResult" required="true" type="uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult" %>

<c:set var="resolvedAssessmentObject" value="${validationResult.resolvedAssessmentObject}"/>
<c:set var="rootNodeLookup" value="${resolvedAssessmentObject.rootNodeLookup}"/>
<c:set var="qtiXmlObjectReadResult" value="${rootNodeLookup.rootNodeHolder}"/>
<c:choose>
  <c:when test="${qtiXmlObjectReadResult==null}">
    <%-- Did not get this far --%>
  </c:when>
  <c:when test="${empty validationResult.modelValidationErrors && empty validationResult.modelValidationWarnings}">
    <div class="resultPanel success">
      <h4>JQTI+ validation succeeded</h4>
      <div class="details">
        <p>
          Additional validation of the resulting JQTI+ model of your XML was successful.
        </p>
      </div>
    </div>
  </c:when>
  <c:otherwise>
    <div class="resultPanel ${validationResult.validationWarningsOnly ? 'warnings' : 'failure'}">
      <h4>JQTI+ validation ${validationResult.validationWarningsOnly ? 'warnings' : 'failure'}</h4>
      <div class="details">
        <p>
          Additional validation of the resulting JQTI+ model detected some
          errors and/or warnings:
        </p>
        <table>
          <thead>
            <tr>
              <th class="center">Severity</th>
              <th class="center">Node</th>
              <th class="center">Line number</th>
              <th class="center">Column number</th>
              <th>Message</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="i" items="${validationResult.modelValidationErrors}">
              <c:set var="node" value="${i.qtiNode}"/>
              <tr>
               <td class="center">Error</td>
               <td class="center">${node!=null ? node.qtiClassName : 'Not available'}</td>
               <td class="center">${node!=null ? node.sourceLocation.lineNumber : '-'}</td>
               <td class="center">${node!=null ? node.sourceLocation.columnNumber : '-'}</td>
               <td><c:out value="${i.message}"/></td>
              </tr>
            </c:forEach>
            <c:forEach var="i" items="${validationResult.modelValidationWarnings}">
              <c:set var="node" value="${i.qtiNode}"/>
              <tr>
               <td class="center">Warning</td>
               <td class="center">${node!=null ? node.qtiClassName : 'Not available'}</td>
               <td class="center">${node!=null ? node.sourceLocation.lineNumber : '-'}</td>
               <td class="center">${node!=null ? node.sourceLocation.columnNumber : '-'}</td>
               <td><c:out value="${i.message}"/></td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
  </c:otherwise>
</c:choose>
