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
<page:page title="Your Item Delivery Configurations">

  <h2>Your Item Delivery Configurations</h2>

  <c:choose>
    <c:when test="${!empty itemDeliverySettingsList}">
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="itemDeliverySettings" items="${itemDeliverySettingsList}">
            <tr>
              <td><c:out value="${itemDeliverySettings.title}"/></td>
              <td><a href="${utils:escapeLink(itemDeliverySettingsRouting[itemDeliverySettings.id]['show'])}">Show / Edit</a></td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <p>You have not created any Item Delivery Configurations yet.</p>
    </c:otherwise>
  </c:choose>

  <h3>Actions</h3>
  <ul>
    <li><a href="${utils:escapeLink(instructorAssessmentRouting['createItemDeliverySettings'])}">Create new Item Delivery Configuration</a></li>
  </ul>

</page:page>

