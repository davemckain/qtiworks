<%--

Copyright (c) 2012-2013, The University of Edinburgh.
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
            the test is being delivered, assisting question authors.
          </aside>
        </div>
      </div>
      <div class="clear"></div>
    </fieldset>
    <fieldset>
      <legend>Permitted geek / debugging actions</legend>
      <div class="stdFormRow">
        <div class="grid_4">
          <label for="allowSource">View the assessmentTest XML</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="allowSource" value="true" /> Yes
            <form:radiobutton path="allowSource" value="false" /> No
          </div>
        </div>
        <div class="grid_5">
          <aside>
            If 'Yes', a button will be made available to show the source XML.
            This only really makes sense during authoring and debugging.
          </aside>
        </div>
      </div>
      <div class="clear"></div>
      <div class="stdFormRow">
        <div class="grid_4">
          <label for="allowResult">View the &lt;assessmentResult&gt; XML</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="allowResult" value="true" /> Yes
            <form:radiobutton path="allowResult" value="false" /> No
          </div>
        </div>
        <div class="grid_5">
          <aside>
            If 'Yes', a button will be provided to generate an &lt;assessmentResult&gt;
            XML file corresponding to the current state of the test session.
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

