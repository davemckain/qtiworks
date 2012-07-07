<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Lists ItemDeliverySettings owned by caller

Model:

itemDeliverySettingsList
itemDeliverySettingsRouting: dsid -> action -> URL
instructorAssessmentRouting

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Your Item Delivery Settings">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/instructor/')}">QTIWorks Dashboard</a> &#xbb;
  </nav>
  <h2>Your Item Delivery Settings</h2>

  <div class="hints">
    <p>
      "Delivery Settings" are reusable sets of options for specifying how your assessmnts should
      be delivered. You can use this to control things that fall outside the scope of QTI, such as
      whether candidates can request a solution,
      how many attempts should be allowed, etc.
    </p>
  </div>

  <c:choose>
    <c:when test="${!empty itemDeliverySettingsList}">
      <table class="assessmentList">
        <thead>
          <tr>
            <th></th>
            <th>Details</th>
            <th>Created</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="itemDeliverySettings" items="${itemDeliverySettingsList}" varStatus="loopStatus">
            <tr>
              <td align="center">
                <div class="workflowStep">${loopStatus.index + 1}</div>
              </td>
              <td>
                <h4>
                  <a href="${utils:escapeLink(itemDeliverySettingsRouting[itemDeliverySettings.id]['show'])}">
                    <c:out value="${itemDeliverySettings.title}"/>
                  </a>
                </h4>
                <span class="title">"${fn:escapeXml(itemDeliverySettings.prompt)}"</span>
              </td>
              <td class="center">
                <c:out value="${utils:formatDayDateAndTime(itemDeliverySettings.creationTime)}"/>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <p>You have not created any Item Delivery Configurations yet.</p>
    </c:otherwise>
  </c:choose>

  <h4>Actions</h4>
  <ul>
    <li><a href="${utils:escapeLink(instructorAssessmentRouting['createItemDeliverySettings'])}">Create new Item Delivery Configuration</a></li>
  </ul>

</page:page>

