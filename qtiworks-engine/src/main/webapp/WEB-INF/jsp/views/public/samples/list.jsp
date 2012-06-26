<%@ page trimDirectiveWhitespaces="true"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Lists samples to try out

Model attributes:

sampleAssessmentMap (SampleCategory -> List<Assessment>)

--%>
<page:page title="Public QTI Samples">

  <h2>Public QTI samples</h2>

  <c:forEach var="entry" items="${sampleAssessmentMap}">
    <c:set var="sampleCategory" value="${entry.key}"/>
    <c:set var="assessmentList" value="${entry.value}"/>
    <h3><c:out value="${sampleCategory.title}"/></h3>
    <table class="samples">
      <tbody>
        <c:forEach var="assessment" items="${assessmentList}" varStatus="loopStatus">
          <tr>
            <td>
              ${loopStatus.index + 1}
            </td>
            <td>
              <%-- Play option TODO: Create template for this --%>
              <c:url var="playUrl" value="/web/public/samples/${assessment.id}"/>
              <form action="${playUrl}" method="post" class="playAssessment">
                <input type="submit" value="Try out">
              </form>
            </td>
            <td>
              <h4><c:out value="${assessment.name}"/></h4>
              <span class="title"><c:out value="${assessment.title}"/></span>
            </td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </c:forEach>

</page:page>
