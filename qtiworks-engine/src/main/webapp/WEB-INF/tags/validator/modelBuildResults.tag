<%--

This fragment formats the results of JQTI model building

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
<c:choose>
  <c:when test="${qtiXmlObjectReadResult!=null}">
    <div class="resultPanel success">
      <h4>JQTI+ model building success</h4>
      <div class="details">
        <p>
          A JQTI+ model was successfully constructed from the XML.
        </p>
      </div>
    </div>
  </c:when>
  <c:when test="${qtiXmlInterpretationException!=null}">
    <c:set var="failureReason" value="${qtiXmlInterpretationException.interpretationFailureReason}"/>
    <c:choose>
      <c:when test="${failureReason=='XML_PARSE_FAILED' || failureReason=='XML_SCHEMA_VALIDATION_FAILED'}">
        <%-- Did not get as far as model building, so say nothing --%>
      </c:when>
      <c:when test="${failureReason=='UNSUPPORTED_ROOT_NODE'}">
        <div class="resultPanel failure">
          <h4>Wrong XML</h4>
          <div class="details">
            <p>
              This XML was expected to be a QTI ${qtiXmlInterpretationException.requiredResultClass.simpleName}
              but it only contained a fragment of QTI content.
            </p>
          </div>
        </div>
      </c:when>
      <c:when test="${failureReason=='WRONG_RESULT_TYPE'}">
        <div class="resultPanel failure">
          <h4>Wrong QTI Content</h4>
          <div class="details">
            <p>
              This XML was expected to be a QTI ${qtiXmlInterpretationException.requiredResultClass.simpleName}
              but it was actually a ${qtiXmlInterpretationException.rootNode.class.simpleName}.
            </p>
          </div>
        </div>
      </c:when>
      <c:when test="${failureReason=='JQTI_MODEL_BUILD_FAILED'}">
        <c:set var="modelErrors" value="${qtiXmlInterpretationException.qtiModelBuildingErrors}"/>
        <div class="resultPanel failure">
          <h4>JQTI model building failure</h4>
          <div class="details">
            <p>
            We could not build a JQTI+ model from your XML. This indicates a problem with
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
          </div>
        </div>
      </c:when>
    </c:choose>
  </c:when>
</c:choose>
