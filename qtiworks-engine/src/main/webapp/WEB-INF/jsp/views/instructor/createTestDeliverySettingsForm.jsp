<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Form for creating a new Test Delivery settings

Model:

testDeliverySettingsTemplate - form backing template

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Create new Test Delivery Settings">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/instructor/')}">QTIWorks Dashboard</a> &#xbb;
    <a href="${utils:internalLink(pageContext, '/web/instructor/deliverysettings')}">Your Delivery Settings</a> &#xbb;
  </nav>
  <h2>Create Test Delivery Settings</h2>

  <div class="hints">
    This form lets you create a new set of test delivery settings for you to use.
  </div>

  <form:form cssClass="deliverySettings" method="post" acceptCharset="UTF-8" commandName="testDeliverySettingsTemplate">

    <%-- Show any form validation errors discovered --%>
    <form:errors element="div" cssClass="formErrors" path="*"/>

    <fieldset>
      <legend>Metadata</legend>
      <div class="stdFormRow">
        <div class="grid_2">
          <label for="title">Title:</label>
        </div>
        <div class="grid_5">
          <form:input path="title" size="30" type="input" cssClass="expandy" />
        </div>
        <div class="grid_5">
          <aside>
            The title helps you organise your settings. It is not shown to candidates
         </aside>
      </div>
      <div class="clear"></div>
    </fieldset>

    <fieldset>
      <legend>Display Controls</legend>
      <div class="stdFormRow">
        <div class="grid_4">
          <label for="authorMode">Author Mode?</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="authorMode" value="true" /> Yes
            <form:radiobutton path="authorMode" value="false" /> No
          </div>
        </div>
        <div class="grid_5">
          <aside>
            If 'Yes', additional debugging information will be shown while
            the assessment is being delivered, assisting question authors.
            It will also be possible to access the assessment source, result
            and state XML.
          </aside>
        </div>
      </div>
      <div class="clear"></div>
    </fieldset>
    <div class="stdFormRow">
      <div class="grid_4">
        <label for="submit">Hit "Create" to save these new settings</label>
      </div>
      <div class="grid_3">
        <input name="submit" type="submit" value="Create"/>
      </div>
    </div>
    <div class="clear"></div>

  </form:form>

</page:page>
