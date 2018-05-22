<%@ page trimDirectiveWhitespaces="true"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

'Get access' page

--%>
<page:page title="Requesting access to QTIWorks">

  <h2>Requesting access to QTIWorks</h2>

  <p>
    There are two methods for getting access to use all of QTIWorks' functionality
    for your assessments.
  </p>

  <h3>1. LTI Connection from your Virtual Learning Environment</h3>
  <p>
    QTIWorks can be launched from any institutional Virtual Learning
    Environment (or similar system/tool) that supports (Basic) LTI 1.1
    domain-level launches. This is the best way of making QTIWorks available to
    you and your colleagues at institutional level.
  </p>
  <p>
    In order to set this up, you will need to have your tool registered to use
    this installation of QTIWorks. Please contact the administrator of this
    instance of QTIWorks
    (<a href="mailto:${qtiWorksDeploymentSettings.adminEmailAddress}"><c:out value="${qtiWorksDeploymentSettings.adminName}"/></a>)
    in the first instance, providing the domain name of your tool.
    (You will almost certainly need to get your VLE administrator to do this
    for you.)
  </p>
  <p>
    Please also make sure you have read details about
    <a href="${utils:escapeLink(qtiWorksDeploymentSettings.privacyPolicyUrl)}">privacy, security &amp; cookies</a>
    in QTIWorks.
  </p>

  <h3>2. Request an individual QTIWorks account</h3>
  <p>
    Individual accounts can be set up to let you use QTIWorks without (or
    outside of) a virtual learning environment.  Candidate assessment is still
    expected to be set up as (single) LTI web links, so you will still need a
    VLE (or other tool) that supports (Basic) LTI 1.1 link-level launches if
    you want to do any real assessment.
  </p>
  <p>
    Please contact the administrator of this instance of QTIWorks
    (<a href="mailto:${qtiWorksDeploymentSettings.adminEmailAddress}"><c:out value="${qtiWorksDeploymentSettings.adminName}"/></a>)
    if you would like an individual account created for you.
  </p>
  <p>
    Please also make sure you have read details about
    <a href="${utils:escapeLink(qtiWorksDeploymentSettings.privacyPolicyUrl)}">privacy, security &amp; cookies</a>
    in QTIWorks.
  </p>

  <h2>Deleting your QTIWorks account and/or LTI Connections</h2>

  <p>
    If you have previously been given a QTIWorks individual account and/or LTI Connection,
    then you may request the deletion of all of your data at any time by emailing the
    administrator of this instance of QTIWorks
    (<a href="mailto:${qtiWorksDeploymentSettings.adminEmailAddress}"><c:out value="${qtiWorksDeploymentSettings.adminName}"/></a>).
  </p>

</page:page>
