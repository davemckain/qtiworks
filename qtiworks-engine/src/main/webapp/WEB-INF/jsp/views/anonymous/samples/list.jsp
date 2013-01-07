<%@ page trimDirectiveWhitespaces="true"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Lists samples to try out

Model attributes:

sampleAssessmentMap (SampleCategory -> List<Assessment>)

--%>
<page:page title="Public QTI Samples">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/public/')}">Demos</a> &#xbb;
  </nav>
  <h2>QTI examples</h2>

  <div class="hints">
    <p>
      Here are a selection of example QTI assessment items that showcase many of the things you can do with QTI.
      (We shall add some tests once we QTIWorks fully supports them.)
    </p>
  </div>

  <c:forEach var="entry" items="${sampleAssessmentMap}">
    <c:set var="sampleCategory" value="${entry.key}"/>
    <c:set var="assessmentList" value="${entry.value}"/>
    <div class="sampleList">
      <h3><c:out value="${sampleCategory.title}"/></h3>
      <div class="hints">
        ${fn:escapeXml(sampleCategory.description)}
      </div>
      <ul class="sampleList">
        <c:forEach var="assessment" items="${assessmentList}" varStatus="loopStatus">
          <li>
            <c:if test="${loopStatus.index%2 == 0}">
              <div class="clear"></div>
            </c:if>
            <div class="grid_1">
              <div class="sampleIndex">${loopStatus.index + 1}</div>
            </div>
            <div class="grid_1 launch">
              <%-- Play option TODO: Create template for this --%>
              <c:url var="playUrl" value="/web/anonymous/samples/${assessment.id}"/>
              <form action="${playUrl}" method="post">
                <button type="submit" class="playButton">Try</button>
              </form>
            </div>
            <div class="grid_4">
              <h4><c:out value="${assessment.name}"/></h4>
              <span class="title"><c:out value="${assessment.title}"/></span>
            </div>
          </li>
        </c:forEach>
      </ul>
      <div class="clear"></div>
    </div>
  </c:forEach>

</page:page>
