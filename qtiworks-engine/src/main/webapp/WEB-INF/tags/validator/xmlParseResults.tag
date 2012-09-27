<%--

Formats the results of XML parsing.

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ tag body-content="empty" %>
<%@ taglib prefix="utils" uri="http://www.ph.ed.ac.uk/utils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="validator" tagdir="/WEB-INF/tags/validator" %>
<%@ attribute name="rootNodeLookup" required="true" type="uk.ac.ed.ph.jqtiplus.resolution.RootNodeLookup" %>

<c:set var="qtiXmlObjectReadResult" value="${rootNodeLookup.rootNodeHolder}"/>
<c:set var="qtiXmlInterpretationException" value="${rootNodeLookup.badResourceException}"/>
<c:set var="xmlParseResult" value="${qtiXmlObjectReadResult!=null ? qtiXmlObjectReadResult.xmlParseResult : (qtiXmlInterpretationException!=null ? qtiXmlInterpretationException.xmlParseResult : null)}"/>
<c:choose>
  <c:when test="${xmlParseResult==null}">
    <%-- Did not get this far --%>
  </c:when>
  <c:otherwise>
    <c:choose>
      <c:when test="${xmlParseResult.parsed}">
        <div class="resultPanel success">
          <h4>XML parse success</h4>
          <div class="details">
            <p>The QTI XML was successfully parsed.</p>
          </div>
        </div>
      </c:when>
      <c:otherwise>
        <div class="resultPanel failure">
          <h4>XML parse failed</h4>
          <div class="details">
            <c:choose>
              <c:when test="${empty xmlParseResult.unresolvedEntitySystemIds}">
                <p>The QTI XML was not successfully parsed. Error details are listed below:</p>
                <validator:xmlParseErrors xmlParseResult="${xmlParseResult}"/>
              </c:when>
              <c:otherwise>
                <p>
                  The QTI XML was not successfully parsed as it referred to some external
                  entities (e.g. DTD resources) that we chose not to resolve:
                </p>
                <ul>
                  <c:forEach var="systemId" items="${xmlParseResult.unresolvedEntitySystemIds}">
                    <li><c:out value="${utils:extractContentPackagePath(systemId)}"/></li>
                  </c:forEach>
                </ul>
                <p>
                  (This is not technically a validation error, but referring to external entities
                  like this is a bad idea so is not supported by QTI Works.)
                </p>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
      </c:otherwise>
    </c:choose>
  </c:otherwise>
</c:choose>
