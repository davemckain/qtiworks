<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Form for editing Assessment properties

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Edit Assessment properties">

  <h2>Edit Assessment properties</h2>

  <form:form method="post" acceptCharset="UTF-8" commandName="updateAssessmentCommand">
    <form:errors element="div" cssClass="error" path="*"/>
    (<span class="required">*</span> denotes a required field.)

    <div>
      <label for="title">Name:<span class="required">*</span></label>
      <form:input path="name" type="input" />
    </div>
    <div>
      <label for="title">Title:<span class="required">*</span></label>
      <form:input path="title" type="input" />
    </div>
    <div>
      <label for="public">Visible to public?</label>
      <form:radiobutton path="public" value="true" /> Yes
      <form:radiobutton path="public" value="false" /> No
    </div>

    <input type="submit" value="Save" />

  </form:form>

</page:page>
