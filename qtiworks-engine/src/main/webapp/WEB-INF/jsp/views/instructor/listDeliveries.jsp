<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Lists Deliveries for a given Assignment

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Assessment Deliveries">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['dashboard'])}">QTIWorks Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Your Assessments</a> &#xbb;
      <a href="${utils:escapeLink(assessmentRouting['show'])}">
        ${fn:escapeXml(utils:formatAssessmentFileName(assessmentPackage))}
        [${fn:escapeXml(assessmentPackage.title)}]
      </a> &#xbb;
    </nav>
    <h2>Assessment Deliveries</h2>
    <div class="hints">
      <p>
        This page shows the Deliveries that you have created for this Assessment and lets you create
        new Deliveries.
      </p>
    </div>
  </header>

  <table class="listTable">
    <thead>
      <th></th>
      <th>Title</th>
      <th>Available to candidates?</th>
      <th>Selected Delivery Settings</th>
    </thead>
    <tbody>
      <c:forEach var="delivery" items="${deliveryList}" varStatus="loopStatus">
        <tr>
          <td align="center">
            <div class="bigStatus">${loopStatus.index + 1}</div>
          </td>
          <td align="center">
            <a href="${utils:escapeLink(deliveryListRouting[delivery.id]['show'])}"><c:out value="${delivery.title}"/></a>
          </td>
          <td align="center">
            ${delivery.open ? 'Yes' : 'No' }
          </td>
          <td align="center">
            <c:choose>
              <c:when test="${empty deliverySettings}">
                (Using QTIWorks default Delivery Settings)
              </c:when>
              <c:otherwise>
                Using '${fn:escapeXml(deliverySettings.title)}'
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
      </c:forEach>
      <tr>
        <td class="plus"></td>
        <td colspan="3" align="center" class="actions">
          <a href="${utils:escapeLink(assessmentRouting['createDelivery'])}">Create new Delivery</a>
        </td>
      </tr>
    </tbody>
  </table>

</page:page>
