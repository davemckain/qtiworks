<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Form for creating a new Item Delivery settings

Model:

itemDeliverySettingsTemplate - form backing template

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Create new Item Delivery Settings">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/instructor/')}">QTIWorks Dashboard</a> &#xbb;
    <a href="${utils:internalLink(pageContext, '/web/instructor/deliverysettings')}">Your Delivery Settings</a> &#xbb;
  </nav>
  <h2>Create Item Delivery Settings</h2>

  <div class="hints">
    This form lets you create a new set of item delivery settings for you to use.
  </div>

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
          </aside>
        </div>
      </div>
      <div class="clear"></div>
    </fieldset>
    <fieldset>
      <legend>While interacting, the candidate is allowed to...</legend>
      <div class="stdFormRow">
        <div class="grid_4">
          <label for="allowClose">Close the session?</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="allowClose" value="true" /> Yes
            <form:radiobutton path="allowClose" value="false" /> No
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
          <label for="allowReinitWhenInteracting">Reinitialise the session?</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="allowReinitWhenInteracting" value="true" /> Yes
            <form:radiobutton path="allowReinitWhenInteracting" value="false" /> No
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
          <label for="allowResetWhenInteracting">Reset the session?</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="allowResetWhenInteracting" value="true" /> Yes
            <form:radiobutton path="allowResetWhenInteracting" value="false" /> No
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
          <label for="allowSolutionWhenInteracting">Request a model solution?</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="allowSolutionWhenInteracting" value="true" /> Yes
            <form:radiobutton path="allowSolutionWhenInteracting" value="false" /> No
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
          <label for="allowReinitWhenClosed">Reinitialise the session and try again</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="allowReinitWhenClosed" value="true" /> Yes
            <form:radiobutton path="allowReinitWhenClosed" value="false" /> No
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
          <label for="allowResetWhenClosed">Reset the session and try again</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="allowResetWhenClosed" value="true" /> Yes
            <form:radiobutton path="allowResetWhenClosed" value="false" /> No
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
          <label for="allowSolutionWhenClosed">Request a model solution</label>
        </div>
        <div class="grid_3">
          <div class="booleanButtons">
            <form:radiobutton path="allowSolutionWhenClosed" value="true" /> Yes
            <form:radiobutton path="allowSolutionWhenClosed" value="false" /> No
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
    <fieldset>
      <legend>Permitted geek / debugging actions</legend>
      <div class="stdFormRow">
        <div class="grid_4">
          <label for="allowSource">View the assessmentItem XML</label>
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
            XML file corresponding to the current state of the item session.
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
