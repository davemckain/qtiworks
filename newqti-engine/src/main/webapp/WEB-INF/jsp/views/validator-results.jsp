<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--

Validator submission form

Model attributes:

assessmentPackage
validationResult

--%>
<%-- Generate Header --%>
<c:set var="title" value="QTI Validator" />
<%@ include file="/WEB-INF/jsp/includes/header.jspf" %>

<h2>Validation Result</h2>

<p>You uploaded a ${assessmentPackage.assessmentType}, packaged as
${assessmentPackage.packaging}.</p>

<c:set var="resolvedObject" value="${validationResult.resolvedAssessmentObject}"/>
<c:set var="objectLookup" value="${resolvedObject.objectLookup}"/>
<c:set var="xmlResourceNotFoundException" value="${objectLookup.notFoundException}"/>
<c:set var="badResourceException" value="${objectLookup.badResourceException}"/>

<c:choose>
  <c:when test="${xmlResourceNotFoundException==null}">
    <h3 class="success">XML lookup succeeded</h3>
    <p>
      XML resource at <c:out value="${objectLookup.systemId}"/> was successfully
      located within your submitted content package.
    </p>
  </c:when>
  <c:otherwise>
    <h3 class="success">XML lookup failed</h3>
    <p>
      Could not find XML resource at <c:out value="${objectLookup.systemId}"/> within your
      submitted content package.
    </p>
  </c:otherwise>
</c:choose>

<c:if test="${badResourceException!=null}">
  <c:set var="xmlParseResult" value="${badResourceException.xmlParseResult}"/>
  <c:if test="${!xmlParseResult.schemaValid}">
    <%-- XML parse or validation failure --%>
    <c:choose>
      <c:when test="${!xmlParseResult.parsed}">
        <h3 class="fail">XML parsing failed</h3>
        <p>The QTI XML was not successfully parsed.</p>
      </c:when>
      <c:otherwise>
        <h3 class="success">XML parsing success</h3>
        <p>The QTI XML was successfully parsed.</p>
        <h3 class="fail">Schema validation failed</h3>
        <c:choose>
          <c:when test="${!empty xmlParseResult.unsupportedSchemaNamespaces}">
            <p>The XML used the following <em>unsupported</em> shema namespaces:</p>
            <ul>
              <c:forEach var="ns" items="${xmlParseResult.unsupportedSchemaNamespaces}">
                <li><c:out value="${ns}"/></li>
              </c:forEach>
            </ul>
            <p>
              Schema validation was therefore not performed in this case.
            </p>
          </c:when>
          <c:otherwise>
            <p>
              The QTI XML was not successfully validated against our golden copies of
              the following supported schemas:
            </p>
            <c:if test="${!empty xmlParseResult.supportedSchemaNamespaces}">
              <ul>
                <c:forEach var="ns" items="${xmlParseResult.supportedSchemaNamespaces}">
                  <li><c:out value="${ns}"/></li>
                </c:forEach>
              </ul>
            </c:if>
          </c:otherwise>
        </c:choose>
      </c:otherwise>
    </c:choose>
    <c:if test="${!empty xmlParseResult.fatalErrors || !empty xmlParseResult.errors || !empty xmlParseResult.warnings}">
      <h3>Error summary</h3>
      <table>
        <thead>
          <tr>
            <td>Severity</td>
            <td>Line number</td>
            <td>Column number</td>
            <td>Error message</td>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="i" items="${xmlParseResult.fatalErrors}">
            <tr>
              <td>Fatal Error</td>
              <td>${i.lineNumber}</td>
              <td>${i.columnNumber}</td>
              <td><c:out value="${i.message}"/></td>
            </tr>
          </c:forEach>
          <c:forEach var="i" items="${xmlParseResult.errors}">
            <tr>
              <td>Error</td>
              <td>${i.lineNumber}</td>
              <td>${i.columnNumber}</td>
              <td><c:out value="${i.message}"/></td>
            </tr>
          </c:forEach>
          <c:forEach var="i" items="${xmlParseResult.warnings}">
            <tr>
              <td>Warning</td>
              <td>${i.lineNumber}</td>
              <td>${i.columnNumber}</td>
              <td><c:out value="${i.message}"/></td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
      (Please note that line/column numbers usually correspond to the end of the
      XML open/closing tag, so are intended to be helpful rather than exact!)
    </c:if>
  </c:if>

  <c:set var="modelErrors" value="${badResourceException.qtiModelBuildingErrors}"/>
  <c:if test="${!empty modelErrors}">
    <%-- JQTI model build errors --%>
    <h3 class="success">XML parsing success</h3>
    <p>The QTI XML was successfully parsed.</p>
    <h3>Schema validation success</h3>
    <p>
      The QTI XML was successfully validated against our golden copies of
      the following supported schemas:
    </p>
    <c:if test="${!empty xmlParseResult.supportedSchemaNamespaces}">
      <ul>
        <c:forEach var="ns" items="${xmlParseResult.supportedSchemaNamespaces}">
          <li><c:out value="${ns}"/></li>
        </c:forEach>
      </ul>
    </c:if>
    <h3 class="fail">JQTI model building failure</h3>
    <p>
    We could not build a JQTI model from your XML. This indicates a problem with
    your XML that could not be picked up by the schema validation process, such
    as inappropriate content of &lt;value&gt; elements.
    </p>
    <table>
      <thead>
        <tr>
          <th>Line number</th>
          <th>Column number</th>
          <th>XML element name</th>
          <th>Error message</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="e" items="${modelErrors}">
          <tr>
           <td>${e.elementLocation.lineNumber}</td>
           <td>${e.elementLocation.columnNumber}</td>
           <td>${e.elementLocalName}</td>
           <td><c:out value="${e.exception.message}"/></td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </c:if>
</c:if>

<c:set var="qtiXmlObjectReadResult" value="${objectLookup.rootObjectHolder}"/>
<c:if test="${qtiXmlObjectReadResult!=null}">
  <c:set var="xmlParseResult" value="${qtiXmlObjectReadResult.xmlParseResult}"/>
  <%-- Successful lookup --%>
  <h3 class="success">XML parsing success</h3>
  <p>The QTI XML was successfully parsed.</p>
  <h3 class="success">Schema validation success</h3>
  <p>
    The QTI XML was successfully validated against our golden copies of
    the following supported schemas:
  </p>
  <c:if test="${!empty xmlParseResult.supportedSchemaNamespaces}">
    <ul>
      <c:forEach var="ns" items="${xmlParseResult.supportedSchemaNamespaces}">
        <li><c:out value="${ns}"/></li>
      </c:forEach>
    </ul>
  </c:if>
  <h3 class="success">JQTI model building succeeded</h3>
  <p>
    A JQTI model was successfully constructed from your XML.
  </p>
  <c:choose>
    <c:when test="${empty validationResult.errors && empty validationResult.warnings}">
      <h3 class="success">Model validation succeeded</h3>
      <p>
        Further validation of your QTI detected some errors and/or warnings.
      </p>
    </c:when>
    <c:otherwise>
      <h3 class="fail">Model validation failed</h3>
      <p>
        Further validation of your QTI detected some errors and/or warnings.
      </p>
      <table>
        <thead>
          <tr>
            <th>Severity</th>
            <th>Node</th>
            <th>Line number</th>
            <th>Column number</th>
            <th>Message</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="i" items="${validationResult.errors}">
            <tr>
             <td>Error</td>
             <td>${i.node.classTag}</td>
             <td>${i.node.sourceLocation.lineNumber}</td>
             <td>${i.node.sourceLocation.columnNumber}</td>
             <td><c:out value="${i.message}"/></td>
            </tr>
          </c:forEach>
          <c:forEach var="i" items="${validationResult.warnings}">
            <tr>
             <td>Warning</td>
             <td>${i.node.classTag}</td>
             <td>${i.node.sourceLocation.lineNumber}</td>
             <td>${i.node.sourceLocation.columnNumber}</td>
             <td><c:out value="${i.message}"/></td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:otherwise>
  </c:choose>
</c:if>

<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>
