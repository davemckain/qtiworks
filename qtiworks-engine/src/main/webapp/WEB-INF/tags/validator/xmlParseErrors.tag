<%--

Formats the parsing errors within a

uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseResult

as a nice table.

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

--%>
<%@ tag body-content="empty" %>
<%@ taglib prefix="utils" uri="http://www.ph.ed.ac.uk/utils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="xmlParseResult" required="true" type="uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseResult" %>

<table>
  <thead>
    <tr>
      <th>Severity</th>
      <th>Line number</th>
      <th>Column number</th>
      <th>Error message</th>
    </tr>
  </thead>
  <tbody>
    <c:forEach var="i" items="${xmlParseResult.fatalErrors}">
      <tr>
        <td class="center">Fatal Error</td>
        <td class="center">${i.lineNumber}</td>
        <td class="center">${i.columnNumber}</td>
        <td><c:out value="${i.message}"/></td>
      </tr>
    </c:forEach>
    <c:forEach var="i" items="${xmlParseResult.errors}">
      <tr>
        <td class="center">Error</td>
        <td class="center">${i.lineNumber}</td>
        <td class="center">${i.columnNumber}</td>
        <td><c:out value="${i.message}"/></td>
      </tr>
    </c:forEach>
    <c:forEach var="i" items="${xmlParseResult.warnings}">
      <tr>
        <td class="center">Warning</td>
        <td class="center">${i.lineNumber}</td>
        <td class="center">${i.columnNumber}</td>
        <td><c:out value="${i.message}"/></td>
      </tr>
    </c:forEach>
  </tbody>
</table>
<p>
  (Please note that line/column numbers usually correspond to the end of the
  XML open/closing tag, so are intended to be helpful rather than exact!)
</p>
