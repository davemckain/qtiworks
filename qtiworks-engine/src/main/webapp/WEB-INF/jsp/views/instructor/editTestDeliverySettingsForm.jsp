<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Form for creating a new Test Delivery settings

Model:

testDeliverySettings - current settings
testDeliverySettingsTemplate - form backing template

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="View/Edit Test Delivery Settings">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/instructor/')}">QTIWorks Dashboard</a> &#xbb;
    <a href="${utils:internalLink(pageContext, '/web/instructor/deliverysettings')}">Your Delivery Settings</a> &#xbb;
  </nav>
  <h2>Test Delivery Settings '${fn:escapeXml(testDeliverySettings.title)}'</h2>

  <div class="hints">
    <p>
      The current values for these settings are shown below. You can make changes to these if required.
    </p>
    <p>
      (Functionality for deleting these settings will appear shortly!)
    </p>
  </div>

  <%-- TODO: Cut & paste below. Ack! --%>
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
      <div class="stdFormRow">
        <div class="grid_4">
          <label for="public">Share with others?</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="public" value="true" /> Yes
            <form:radiobutton path="public" value="false" /> No
          </div>
        </div>
        <div class="grid_5">
          <aside>
            This allows other people to use these settings.
            (Not fully implemeneted yet!)
          </aside>
        </div>
      </div>
      <div class="clear"></div>
    </fieldset>

    <fieldset>
      <legend>Display Controls</legend>
      <div class="stdFormRow">
        <div class="grid_2">
          <label for="prompt">Prompt:</label>
        </div>
        <div class="grid_5">
          <form:textarea path="prompt" rows="5" type="input" cssClass="expandy" />
        </div>
        <div class="grid_5">
          <aside>
            This is an optional prompt that will be displayed to candidates just
            before the test body is shown.
          </aside>
        </div>
      </div>
      <div class="clear"></div>
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
            the test is being delivered, assisting question authors.
          </aside>
        </div>
      </div>
      <div class="clear"></div>
    </fieldset>
    <div class="stdFormRow">
      <div class="grid_4">
        <label for="submit">Hit "Save" to save these revised settings</label>
      </div>
      <div class="grid_3">
        <input name="submit" type="submit" value="Save"/>
      </div>
    </div>
    <div class="clear"></div>

  </form:form>

</page:page>

