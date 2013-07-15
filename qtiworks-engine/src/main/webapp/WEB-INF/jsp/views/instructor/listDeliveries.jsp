<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Lists Deliveries for a given Assignment

Model:

assessment
deliveryList
assessmentRouting: action -> URL
deliveryListRouting: did -> action -> URL
primaryRouting

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Assessment Deliveries">

  <nav class="breadcrumbs">
    <a href="${utils:escapeLink(primaryRouting['dashboard'])}">QTIWorks Dashboard</a> &#xbb;
    <a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Your assessments</a> &#xbb;
    <a href="${utils:escapeLink(assessmentRouting['show'])}">Assessment '${fn:escapeXml(assessment.name)}'</a>
  </nav>
  <h2>Assessment Deliveries</h2>

  <c:choose>
    <c:when test="${!empty deliveryList}">
      <table class="listTable">
        <thead>
          <th></th>
          <th>Title</th>
          <th>Open?</th>
          <th>Delivery Settings</th>
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
                ${fn:escapeXml(delivery.deliverySettings.title)}
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <p>You have not created any Deliveries yet.</p>
    </c:otherwise>
  </c:choose>

  <h4>Actions</h4>
  <ul class="menu">
    <li>
      <page:postLink path="${utils:escapeLink(assessmentRouting['createDelivery'])}" title="Create Delivery"/>
    </li>
  </ul>

</page:page>
