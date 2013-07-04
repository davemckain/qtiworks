<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Lists DeliverySettings owned by caller

Model:

deliverySettingsList
deliverySettingsListRouting: dsid -> action -> URL
primaryRouting

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Your Delivery Settings">

  <nav class="breadcrumbs">
    <a href="${utils:escapeLink(primaryRouting['dashboard'])}">QTIWorks Dashboard</a> &#xbb;
  </nav>
  <h2>Your Delivery Settings</h2>

  <div class="hints">
    <p>
      "Delivery Settings" are reusable sets of options for specifying how your assessmnts should
      be delivered. You can use this to control things that fall outside the scope of QTI, such as
      whether candidates can request a solution,
      how many attempts should be allowed, etc.
    </p>
  </div>

  <c:choose>
    <c:when test="${!empty deliverySettingsList}">
      <table class="assessmentList">
        <thead>
          <tr>
            <th></th>
            <th>Details</th>
            <th>For</th>
            <th>Created</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="deliverySettings" items="${deliverySettingsList}" varStatus="loopStatus">
            <tr>
              <td align="center">
                <div class="workflowStep">${loopStatus.index + 1}</div>
              </td>
              <td>
                <h4>
                  <a href="${utils:escapeLink(deliverySettingsListRouting[deliverySettings.id]['showOrEdit'])}">
                    <c:out value="${deliverySettings.title}"/>
                  </a>
                </h4>
                <c:if test="${deliverySettings['class'].simpleName=='ItemDeliverySettings' && !empty deliverySettings.prompt}">
                  <span class="title">"${fn:escapeXml(utils:trimSentence(deliverySettings.prompt, 200))}"</span>
                </c:if>
              </td>
              <td align="center">
                ${utils:formatAssessmentType(deliverySettings.assessmentType)}
              </td>
              <td class="center">
                <c:out value="${utils:formatDayDateAndTime(deliverySettings.creationTime)}"/>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <p>You have not created any Delivery Settings yet.</p>
    </c:otherwise>
  </c:choose>

  <h4>Actions</h4>
  <ul>
    <li><a href="${utils:escapeLink(primaryRouting['createItemDeliverySettings'])}">Create new Item Delivery Settings</a></li>
    <li><a href="${utils:escapeLink(primaryRouting['createTestDeliverySettings'])}">Create new Test Delivery Settings</a></li>
  </ul>

</page:page>

