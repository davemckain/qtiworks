<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Lists ItemDeliveryOptions owned by caller

Model:

itemDeliveryOptionsList
itemDeliveryOptionsRouting: doid -> action -> URL
instructorAssessmentRouting

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Your Item Delivery Configurations">

  <h2>Your Item Delivery Configurations</h2>

  <c:choose>
    <c:when test="${!empty itemDeliveryOptions}">
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="itemDeliveryOptions" items="${itemDeliveryOptionsList}">
            <tr>
              <td><c:out value="${itemDeliveryOptions.title}"/></td>
              <td><a href="${utils:escapeLink(itemDeliveryOptionsRouting[itemDeliveryOptions.id]['show'])}">Show / Edit</a></td>
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
    <li><a href="${utils:escapeLink(instructorAssessmentRouting['createItemDeliveryOptions'])}">Create new Item Delivery Configuration</a></li>
  </ul>

</page:page>

