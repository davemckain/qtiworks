<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--

Lists samples to try out

Model attributes:

demoSampleCollection (QTISampleCollection)

--%>
<c:set var="title" value="QTI Samples" />
<%@ include file="/WEB-INF/jsp/includes/header.jspf" %>

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

<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>
