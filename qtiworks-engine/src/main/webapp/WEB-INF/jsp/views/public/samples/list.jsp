<%@ page trimDirectiveWhitespaces="true"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Lists samples to try out

Model attributes:

sampleAssessmentMap (SampleCategory -> List<AssessmentPackage>)

--%>
<page:page title="Public QTI Samples">

  <h2>Public QTI samples</h2>

  <c:forEach var="entry" items="${sampleAssessmentMap}">
    <c:set var="sampleCategory" value="${entry.key}"/>
    <c:set var="assessmentPackageList" value="${entry.value}"/>
    <h3><c:out value="${sampleCategory.title}"/></h3>
    <ul>
      <c:forEach var="assessmentPackage" items="${assessmentPackageList}">
        <c:set var="assessment" value="${assessmentPackage.assessment}"/>
        <c:set var="delivery" value="${assessmentPackage.defaultDelivery}"/>
        <li>
          <c:out value="${assessment.title}"/>

          <%-- Play option TODO: Create template for this --%>
          <c:url var="playUrl" value="/web/public/delivery/${delivery.id}"/>
          <form action="${playUrl}" method="post">
            <input type="submit" value="Play">
          </form>
        </li>
      </c:forEach>
    </ul>
  </c:forEach>

</page:page>
