<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--

Validator submission form

--%>
<%-- Generate Header --%>
<c:set var="title" value="QTI Validator" />
<%@ include file="/WEB-INF/jsp/includes/header.jspf" %>

 <form:form id="validatorForm" method="post" acceptCharset="UTF-8" enctype="multipart/form-data">
  <dl>
    <dt><label for="submissionFile">Select File to upload:</label></dt>
    <dd>
      &#x200a;<input type="file" id="upload" name="upload">
    </dd>
  </dl>
  <div class="controls">
    <input id="submit" name="submit" type="submit" value="Submit">
  </div>
</form:form>

<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>
