<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Form for creating a new Item Delivery settings

Model:

itemDeliverySettings - form backing template

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Create new Item Delivery Configuration">

  <h2>Create Item Delivery Configuration</h2>

  <form:form method="post" acceptCharset="UTF-8" commandName="itemDeliverySettings">
    <form:errors element="div" cssClass="formErrors" path="*"/>
    (<span class="required">*</span> denotes a required field.)

    <fieldset>
      <legend>Management</legend>
      <div>
        <label for="title">Title:<span class="required">*</span></label>
        <form:input path="title" type="input" />
      </div>
    </fieldset>
    <fieldset>
      <legend>Display Controls</legend>
      <div>
        <label for="prompt">Optional prompt shown to candidate:</label>
        <form:input path="prompt" type="input" />
      </div>
      <div>
        <label for="maxAttempts">Max Attempts (0=unlimited)<span class="required">*</span></label>
        <form:input path="maxAttempts" type="number" min="0" />
      </div>
      <div>
        <label for="authorMode">Author Mode?</label>
        <form:radiobutton path="authorMode" value="true" /> Yes
        <form:radiobutton path="authorMode" value="false" /> No
      </div>
    </fieldset>
    <fieldset>
      <legend>While interacting, the candidate is allowed to...</legend>
      <div>
        <label for="allowClose">Close her session explicitly</label>
        <form:radiobutton path="allowClose" value="true" /> Yes
        <form:radiobutton path="allowClose" value="false" /> No
      </div>
      <div>
        <label for="allowReinitWhenInteracting">Reinitialise her session</label>
        <form:radiobutton path="allowReinitWhenInteracting" value="true" /> Yes
        <form:radiobutton path="allowReinitWhenInteracting" value="false" /> No
      </div>
      <div>
        <label for="allowResetWhenInteracting">Reset her session</label>
        <form:radiobutton path="allowResetWhenInteracting" value="true" /> Yes
        <form:radiobutton path="allowResetWhenInteracting" value="false" /> No
      </div>
      <div>
        <label for="allowSolutionWhenInteracting">Request a model solution</label>
        <form:radiobutton path="allowSolutionWhenInteracting" value="true" /> Yes
        <form:radiobutton path="allowSolutionWhenInteracting" value="false" /> No
      </div>
    </fieldset>
    <fieldset>
      <legend>Once session is closed, the candidate is allowed to...</legend>
      <div>
        <label for="allowReinitWhenClosed">Reinitialise her session and try again</label>
        <form:radiobutton path="allowReinitWhenClosed" value="true" /> Yes
        <form:radiobutton path="allowReinitWhenClosed" value="false" /> No
      </div>
      <div>
        <label for="allowResetWhenClosed">Reset her session and try again</label>
        <form:radiobutton path="allowResetWhenClosed" value="true" /> Yes
        <form:radiobutton path="allowResetWhenClosed" value="false" /> No
      </div>
      <div>
        <label for="allowSolutionWhenClosed">Request a model solution</label>
        <form:radiobutton path="allowSolutionWhenClosed" value="true" /> Yes
        <form:radiobutton path="allowSolutionWhenClosed" value="false" /> No
      </div>
      <div>
        <label for="allowPlayback">Play back / review interactions</label>
        <form:radiobutton path="allowPlayback" value="true" /> Yes
        <form:radiobutton path="allowPlayback" value="false" /> No
      </div>
    </fieldset>
    <fieldset>
      <legend>Permitted geek / debugging actions</legend>
      <div>
        <label for="allowSource">View the assessmentItem XML</label>
        <form:radiobutton path="allowSource" value="true" /> Yes
        <form:radiobutton path="allowSource" value="false" /> No
      </div>
      <div>
        <label for="allowResult">Generate an assessmentResult XML</label>
        <form:radiobutton path="allowResult" value="true" /> Yes
        <form:radiobutton path="allowResult" value="false" /> No
      </div>
    </fieldset>

    <input type="submit" value="Create" />

  </form:form>

</page:page>
