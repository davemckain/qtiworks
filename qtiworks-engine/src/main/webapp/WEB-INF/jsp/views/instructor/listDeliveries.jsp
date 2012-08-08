<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Lists Deliveries for a given Assignment

Model:

assessment
itemDeliveryList
assessmentRouting: action -> URL
deliveryListRouting: did -> action -> URL
instructorAssessmentRouting

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Assessment Deliveries">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/instructor/')}">QTIWorks Dashboard</a> &#xbb;
    <a href="${utils:escapeLink(instructorAssessmentRouting['listAssessments'])}">Your assessments</a> &#xbb;
    <a href="${utils:escapeLink(assessmentRouting['show'])}">Assessment '${fn:escapeXml(assessment.name)}'</a>
  </nav>
  <h2>Assessment Deliveries</h2>

  <c:choose>
    <c:when test="${!empty itemDeliveryList}">
      <table class="assessmentList">
        <thead>
          <th></th>
          <th>Title</th>
          <th>Open?</th>
          <th>Delivery Settings</th>
        </thead>
        <tbody>
          <c:forEach var="itemDelivery" items="${itemDeliveryList}" varStatus="loopStatus">
            <tr>
              <td align="center">
                <div class="workflowStep">${loopStatus.index + 1}</div>
              </td>
              <td>
                <a href="${utils:escapeLink(deliveryListRouting[itemDelivery.id]['show'])}"><c:out value="${itemDelivery.title}"/></a>
              </td>
              <td>
                ${itemDelivery.open ? 'Yes' : 'No' }
              </td>
              <td>
                ${fn:escapeXml(itemDelivery.itemDeliverySettings.title)}
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
      <form action="${utils:escapeLink(assessmentRouting['createDelivery'])}" method="post">
        <input type="submit" value="Create Delivery">
      </form>
    </li>
  </ul>

</page:page>
