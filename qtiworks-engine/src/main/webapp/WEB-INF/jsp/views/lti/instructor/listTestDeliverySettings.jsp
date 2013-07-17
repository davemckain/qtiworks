<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Lists Test DeliverySettings associated with LTI context

Additional Model attrs:

deliverySettingsList
deliverySettingsListRouting: dsid -> action -> URL

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="Test Delivery Settings">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">Assessment Launch Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['deliverySettingsManager'])}">Delivery Settings Manager</a> &#xbb;
    </nav>
    <h2>Test Delivery Settings</h2>
    <div class="hints">
      <p>
        Your Delivery Settings for running Assessment Tests are shown below.
      </p>
    </div>
  </header>
  <table class="listTable">
    <thead>
      <tr>
        <th colspan="2"></th>
        <th>Name</th>
        <th>Created</th>
        <th></th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="deliverySettings" items="${deliverySettingsList}" varStatus="loopStatus">
        <c:set var="deliverySettingsRouting" value="${deliverySettingsListRouting[deliverySettings.id]}"/>
        <c:set var="areBeingUsed" value="${!empty thisAssessment && !empty theseDeliverySettings && theseDeliverySettings.id==deliverySettings.id}"/>
        <tr class="${areBeingUsed ? 'selected' : ''}">
          <td class="bigStatus">${loopStatus.index + 1}</td>
          <td align="center" class="actions">
            <c:if test="${!empty thisAssessment && thisAssessment.assessmentType==deliverySettings.assessmentType}">
              <c:choose>
                <c:when test="${!empty theseDeliverySettings && theseDeliverySettings.id==deliverySettings.id}">
                  Using these Delivery Settings for this launch
                </c:when>
                <c:otherwise>
                  <page:postLink path="${deliverySettingsRouting['select']}" title="Use for this launch"/>
                </c:otherwise>
              </c:choose>
            </c:if>
          </td>
          <td align="center">
            <h4>
              <a href="${utils:escapeLink(deliverySettingsRouting['showOrEdit'])}">
                <c:out value="${deliverySettings.title}"/>
              </a>
            </h4>
          </td>
          <td class="center">
            <c:out value="${utils:formatDateAndTime(deliverySettings.creationTime)}"/>
          </td>
          <td class="center">
            <div class="scary actions">
              <page:postLink path="${deliverySettingsRouting['delete']}" confirm="Are you sure?" title="Delete these Delivery Settings"/>
            </div>
          </td>
        </tr>
      </c:forEach>
      <tr>
        <td class="plus"></td>
        <td colspan="2" class="actions">
          <a href="${utils:escapeLink(primaryRouting['createTestDeliverySettings'])}">Create new Delivery Settings for running an Assessment Test</a>
        </td>
        <td colspan="2"></td>
      </tr>
    </tbody>
  </table>
  <div class="floatRight">
    <a href="${utils:escapeLink(primaryRouting['deliverySettingsManager'])}">Back to Delivery Settings Manager</a>
  </div>
</page:ltipage>
