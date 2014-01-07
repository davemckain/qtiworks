<%--

This fragment formats the result of schema validation

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

--%>
<%@ tag body-content="empty" %>
<%@ taglib prefix="utils" uri="http://www.ph.ed.ac.uk/utils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="validator" tagdir="/WEB-INF/tags/validator" %>
<%@ attribute name="rootNodeLookup" required="true" type="uk.ac.ed.ph.jqtiplus.resolution.RootNodeLookup" %>

<c:set var="qtiXmlObjectReadResult" value="${rootNodeLookup.rootNodeHolder}"/>
<c:set var="qtiXmlInterpretationException" value="${rootNodeLookup.badResourceException}"/>
<c:set var="xmlParseResult" value="${qtiXmlObjectReadResult!=null ? qtiXmlObjectReadResult.xmlParseResult : (qtiXmlInterpretationException!=null ? qtiXmlInterpretationException.xmlParseResult : null)}"/>
<c:choose>
  <c:when test="${xmlParseResult==null || !xmlParseResult.parsed}">
    <%-- Did not get this far --%>
  </c:when>
  <c:when test="${xmlParseResult.schemaValid}">
    <div class="resultPanel success">
      <h4>Schema validation success</h4>
      <div class="details">
        <c:choose>
          <c:when test="${fn:length(xmlParseResult.supportedSchemaNamespaces)==1}">
            <%-- If only one schema, then must have been the QTI 2.1 (or 2.0) schema --%>
            <p>
              The QTI XML was successfully validated against our golden copy of
              the IMS QTI schema.
            </p>
          </c:when>
          <c:otherwise>
            <p>
              The QTI XML was successfully validated against our golden copies of
              the schemas targeting the following namespaces:
            </p>
            <ul>
              <c:forEach var="ns" items="${xmlParseResult.supportedSchemaNamespaces}">
                <li><code><c:out value="${ns}"/></code></li>
              </c:forEach>
            </ul>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </c:when>
  <c:otherwise>
    <div class="resultPanel failure">
      <h4>Schema validation failed</h4>
      <div class="details">
        <c:choose>
          <c:when test="${!empty xmlParseResult.unsupportedSchemaNamespaces}">
            <p>The QTI XML declared the following schema namespace(s) that are not supported by this system:</p>
            <ul>
              <c:forEach var="ns" items="${xmlParseResult.unsupportedSchemaNamespaces}">
                <li><code><c:out value="${ns}"/></code></li>
              </c:forEach>
            </ul>
            <p>
              Schema validation was therefore not performed in this case.
            </p>
          </c:when>
          <c:when test="${fn:length(xmlParseResult.supportedSchemaNamespaces)==1}">
            <p>
              The QTI XML was not successfully validated against our golden copy of
              the IMS QTI schema.
            </p>
            <p>
              Errors are listed below:
            </p>
            <validator:xmlParseErrors xmlParseResult="${xmlParseResult}"/>
          </c:when>
          <c:otherwise>
            <p>
              The QTI XML was not successfully validated against our golden copies of
              the schemas targeting the following namespaces:
            </p>
            <ul>
              <c:forEach var="ns" items="${xmlParseResult.supportedSchemaNamespaces}">
                <li><code><c:out value="${ns}"/></code></li>
              </c:forEach>
            </ul>
            <p>
              Errors are listed below:
            </p>
            <validator:xmlParseErrors xmlParseResult="${xmlParseResult}"/>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </c:otherwise>
</c:choose>
