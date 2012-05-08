<%@ page trimDirectiveWhitespaces="true"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Lists samples to try out

Model attributes:

demoSampleCollection (QTISampleCollection)

--%>
<page:page title="QTI Samples">

  <h2>Sample items</h2>

  <c:forEach var="qtiSampleSet" items="${demoSampleCollection.qtiSampleSets}" varStatus="sampleStatus">
    <h3><c:out value="${qtiSampleSet.title}"/></h3>
    <ul>
      <c:forEach var="qtiSampleResource" items="${qtiSampleSet.qtiSampleResources}" varStatus="resourceStatus">
        <li>
          <a href="<c:url value='/dispatcher/newSession/${sampleStatus.index}/${resourceStatus.index}'/>">
            <c:out value="${qtiSampleResource.relativePath}"/>
          </a>
        </li>
      </c:forEach>
    </ul>
  </c:forEach>

</page:page>
