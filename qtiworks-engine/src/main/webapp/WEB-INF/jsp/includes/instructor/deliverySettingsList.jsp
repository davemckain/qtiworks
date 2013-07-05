<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

List of Delivery Settings (Item or Test)

Model:

deliverySettingsList - form backing template

--%>
<c:choose>
  <c:when test="${!empty deliverySettingsList}">
    <table class="listTable">
      <thead>
        <tr>
          <th></th>
          <th>Actions</th>
          <th>Name</th>
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
              <c:if test="${empty thisAssessment || thisAssessment.assessmentType==deliverySettings.assessmentType}">
                <page:buttonLink path="${deliverySettingsRouting['select']}" title="Use these Delivery Settings"/>
              </c:if>
            </td>
            <td>
              <h4>
                <a href="${utils:escapeLink(deliverySettingsRouting['showOrEdit'])}">
                  <c:out value="${deliverySettings.title}"/>
                </a>
              </h4>
              <c:if test="${deliverySettings['class'].simpleName=='ItemDeliverySettings' && !empty deliverySettings.prompt}">
                <span class="title">"${fn:escapeXml(utils:trimSentence(deliverySettings.prompt, 50))}"</span>
              </c:if>
            </td>
            <td class="center">
              <c:out value="${utils:formatDateAndTime(deliverySettings.creationTime)}"/>
            </td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </c:when>
  <c:otherwise>
    <p>You have not created any Delivery Settings for Items yet.</p>
  </c:otherwise>
</c:choose>
