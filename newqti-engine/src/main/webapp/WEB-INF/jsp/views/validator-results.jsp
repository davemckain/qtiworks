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

<h2>Validation Result</h2>
<pre>
<c:out value="${result}"/>
</pre>

<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>
