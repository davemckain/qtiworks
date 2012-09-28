<%--

This fragment formats the result of locating a QTI resource.

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ tag body-content="empty" %>
<%@ taglib prefix="utils" uri="http://www.ph.ed.ac.uk/utils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="validator" tagdir="/WEB-INF/tags/validator" %>
<%@ attribute name="rootNodeLookup" required="true" type="uk.ac.ed.ph.jqtiplus.resolution.RootNodeLookup" %>

<c:set var="xmlResourceNotFoundException" value="${rootNodeLookup.notFoundException}"/>
<c:set var="qtiXmlInterpretationException" value="${rootNodeLookup.badResourceException}"/>
<c:choose>
  <c:when test="${xmlResourceNotFoundException==null}">
    <div class="resultPanel success">
      <h4>XML successfully found</h4>
      <div class="details">
        <p>
          The XML resource at path
          <b><c:out value="${utils:extractContentPackagePath(rootNodeLookup.systemId)}"/></b>
          was successfully located within your submitted content package.
        </p>
      </div>
    </div>
  </c:when>
  <c:otherwise>
    <div class="resultPanel failure">
      <h4>XML not found</h4>
      <div class="details">
        <p>
          We could not locate the XML resource at path
          <b><c:out value="${utils:extractContentPackagePath(rootNodeLookup.systemId)}"/></b>
          within your submitted content package.
        </p>
      </div>
    </div>
  </c:otherwise>
</c:choose>
