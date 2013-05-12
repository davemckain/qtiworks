<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Form for creating a new Item Delivery settings

Model:

itemDeliverySettings - current settings
itemDeliverySettingsTemplate - form backing template

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="View/Edit Item Delivery Settings">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/instructor/')}">QTIWorks Dashboard</a> &#xbb;
    <a href="${utils:internalLink(pageContext, '/web/instructor/deliverysettings')}">Your Delivery Settings</a> &#xbb;
  </nav>
  <h2>Item Delivery Settings '${fn:escapeXml(itemDeliverySettings.title)}'</h2>

  <div class="hints">
    <p>
      The current values for these settings are shown below. You can make changes to these if required.
    </p>
    <p>
      (Functionality for deleting these settings will appear shortly!)
    </p>
  </div>

  <%-- TODO: Cut & paste below. Ack! --%>
  <form:form cssClass="deliverySettings" method="post" acceptCharset="UTF-8" commandName="itemDeliverySettingsTemplate">

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
        <div class="grid_2">
          <label for="prompt">Prompt:</label>
        </div>
        <div class="grid_5">
          <form:textarea path="prompt" rows="5" type="input" cssClass="expandy" />
        </div>
        <div class="grid_5">
          <aside>
            This is an optional prompt that will be displayed to candidates just
            before the item body is shown.
          </aside>
        </div>
      </div>
      <div class="clear"></div>
      <div class="stdFormRow">
        <div class="grid_4">
          <label for="maxAttempts">Max Attempts:</label>
        </div>
        <div class="grid_3">
          <form:input path="maxAttempts" type="xnumber" min="0" />
        </div>
        <div class="grid_5">
          <aside>
            Specify the maximum number of attempts allowed, using 0 to indicate that
            there should be no limit.
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
            the item is being delivered, assisting question authors.
            It will also be possible to access the assessment source, result
            and state XML.
          </aside>
        </div>
      </div>
      <div class="clear"></div>
    </fieldset>
    <fieldset>
      <legend>While interacting, the candidate is allowed to...</legend>
      <div class="stdFormRow">
        <div class="grid_4">
          <label for="allowEnd">Close the session?</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="allowEnd" value="true" /> Yes
            <form:radiobutton path="allowEnd" value="false" /> No
          </div>
        </div>
        <div class="grid_5">
          <aside>
            If 'Yes', a button will be added allowing the candidate to
            end the attempt and close the session, moving it into the closed
            state.
          </aside>
        </div>
      </div>
      <div class="clear"></div>
      <div class="stdFormRow">
        <div class="grid_4">
          <label for="allowHardResetWhenOpen">Reinitialise the session?</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="allowHardResetWhenOpen" value="true" /> Yes
            <form:radiobutton path="allowHardResetWhenOpen" value="false" /> No
          </div>
        </div>
        <div class="grid_5">
          <aside>
            If 'Yes', a button will be added allowing the candidate to
            reinitialise the session by resetting all variables and starting
            template processing again. Randomised questions would therefore have
            their randomised values regenerated.
          </aside>
        </div>
      </div>
      <div class="clear"></div>
      <div class="stdFormRow">
        <div class="grid_4">
          <label for="allowSoftResetWhenOpen">Reset the session?</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="allowSoftResetWhenOpen" value="true" /> Yes
            <form:radiobutton path="allowSoftResetWhenOpen" value="false" /> No
          </div>
        </div>
        <div class="grid_5">
          <aside>
            If 'Yes', a button will be added allowing the candidate to
            reset the session back to the state it was in immediately after
            template processing. This therefore clears response variables, but
            keeps existing randomised template values intact.
          </aside>
        </div>
      </div>
      <div class="clear"></div>
      <div class="stdFormRow">
        <div class="grid_4">
          <label for="allowSolutionWhenOpen">Request a model solution?</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="allowSolutionWhenOpen" value="true" /> Yes
            <form:radiobutton path="allowSolutionWhenOpen" value="false" /> No
          </div>
        </div>
        <div class="grid_5">
          <aside>
            If 'Yes', the candidate will be allowed to request a model solution while
            interacting with the question.
          </aside>
        </div>
      </div>
      <div class="clear"></div>
      <div class="stdFormRow">
        <div class="grid_4">
          <label for="allowCandidateComment">Submit a comment?</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="allowCandidateComment" value="true" /> Yes
            <form:radiobutton path="allowCandidateComment" value="false" /> No
          </div>
        </div>
        <div class="grid_5">
          <aside>
            If 'Yes', the candidate will be allowed to submit an optional
            comment, as described in the "allowComment" attribute of the
            QTI itemSessionControl class.
          </aside>
        </div>
      </div>
      <div class="clear"></div>
    </fieldset>
    <fieldset>
      <legend>Once session is closed, the candidate is allowed to...</legend>
      <div class="stdFormRow">
        <div class="grid_4">
          <label for="allowHardResetWhenEnded">Reinitialise the session and try again</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="allowHardResetWhenEnded" value="true" /> Yes
            <form:radiobutton path="allowHardResetWhenEnded" value="false" /> No
          </div>
        </div>
        <div class="grid_5">
          <aside>
            If 'Yes', the candidate will be allowed to reinitialise the session (as described above)
            and try again.
          </aside>
        </div>
      </div>
      <div class="clear"></div>
      <div class="stdFormRow">
        <div class="grid_4">
          <label for="allowSoftResetWhenEnded">Reset the session and try again</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="allowSoftResetWhenEnded" value="true" /> Yes
            <form:radiobutton path="allowSoftResetWhenEnded" value="false" /> No
          </div>
        </div>
        <div class="grid_5">
          <aside>
            If 'Yes', the candidate will be allowed to reset the session (as described above)
            and try again.
          </aside>
        </div>
      </div>
      <div class="clear"></div>
      <div class="stdFormRow">
        <div class="grid_4">
          <label for="allowSolutionWhenEnded">Request a model solution</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="allowSolutionWhenEnded" value="true" /> Yes
            <form:radiobutton path="allowSolutionWhenEnded" value="false" /> No
          </div>
        </div>
        <div class="grid_5">
          <aside>
            If 'Yes', the candidate will be allowed to request a model solution
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
