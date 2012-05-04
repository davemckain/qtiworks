<%--

Formats the parsing errors within a

uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseResult

as a nice table.

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ tag body-content="empty" %>
<%@ taglib prefix="utils" uri="http://www.ph.ed.ac.uk/utils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="xmlParseResult" required="true" type="uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseResult" %>

<h4>Error summary</h4>
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
<p>
  (Please note that line/column numbers usually correspond to the end of the
  XML open/closing tag, so are intended to be helpful rather than exact!)
</p>
