<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Lists DeliverySettings associated with LTI context

Additional Model attrs:

deliverySettingsList
deliverySettingsListRouting: dsid -> action -> URL

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="Delivery Settings manager">

  <nav class="breadcrumbs">
    <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">This Assessment launch</a></li> &#xbb;
  </nav>
  <h2>Delivery Settings manager</h2>

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
            <th colspan="2"></th>
            <th>Details</th>
            <th>For</th>
            <th>Created</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="deliverySettings" items="${deliverySettingsList}" varStatus="loopStatus">
            <c:set var="deliverySettingsRouting" value="${deliverySettingsListRouting[deliverySettings.id]}"/>
            <tr>
              <td align="center">
                <div class="workflowStep">${loopStatus.index + 1}</div>
              </td>
              <td align="center" class="launch">
                <form action="${deliverySettingsRouting['select']}" method="post">
                  <button type="submit" class="playButton">Use these DeliverySettings</button>
                </form>
              </td>
              <td>
                <h4>
                  <a href="${utils:escapeLink(deliverySettingsRouting['showOrEdit'])}">
                    <c:out value="${deliverySettings.title}"/>
                  </a>
                </h4>
                <c:if test="${deliverySettings.class.simpleName=='ItemDeliverySettings' && !empty deliverySettings.prompt}">
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
    <li><a href="${utils:escapeLink(primaryRouting['createTestDeliverySettings'])}">Create new Delivery Settings for running an Assessment Test</a></li>
    <li><a href="${utils:escapeLink(primaryRouting['createItemDeliverySettings'])}">Create new Delivery Settings for running a single Assessment Item</a></li>
  </ul>

</page:ltipage>
