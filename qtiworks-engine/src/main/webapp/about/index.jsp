<%@ page trimDirectiveWhitespaces="true"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Top page for public functionality

--%>
<page:page title="About QTIWorks">

  <h2>About QTIWorks</h2>

  <h3>Overview</h3>
  <p>
    QTIWorks is an open-source tool for delivering Question &amp; Test
    Interoperability (QTI) v2.1 assessment items and tests. It was
    developed as part of the JISC QTIDI project as a next-generation
    advancement of the existing QTIEngine and MathAssessEngine tools.  It
    supports almost all of the QTI 2.1 specification, as well as the MathAssess
    QTI extensions. A "Learning Tools Interoperability" (LTI) v1.1 connector
    makes it possible to use QTIWorks to deliver assessments within popular
    learning systems such as BlackBoard and Moodle.
  </p>
  <ul>
    <li><a href="http://www.imsglobal.org/question/">IMS Question &amp; Test Interoperability (QTI) Specification</a></li>
    <li><a href="http://www.imsglobal.org/lti/">IMS Learning Tools Interoperability (LTI) Specification</a></li>
  </ul>

  <h3>Development</h3>
  <p>
    QTIWorks is open source and released under a BSD license. The project was incubated in GitHub,
    whence you can find the source code and development documentation.
  </p>
  <p>
    The software was developed within the School of Physics and Astronomy at the University of Edinburgh.
    The principal architect and developer was David McKain.
  </p>
  <p>
    QTIWorks is a reworking of the earlier MathAssessEngine and QTIEngine
    projects, the latter of which was developed by the University of
    Southampton. The most visible part of it is a Java web application, which
    makes extensive use of the Spring Framework. At the core of QTIWorks is a
    significantly refactored version of Southampton's JQTI library, called JQTI+.
  </p>
  <ul>
    <li><a href="https://github.com/davemckain/qtiworks">QTIWorks on GitHub</a></li>
    <li><a href="https://www.ph.ed.ac.uk">School of Physics and Astronomy at the University of Edinburgh</a></li>
  </ul>

  <h3>Funding</h3>
  <p>
    QTIWorks was funded by as part of the QTI Delivery Integration project,
    funded by JISC. We are very grateful to JISC for its support and funding
    for this and related projects.
  </p>
  <ul>
    <li><a href="https://www.jisc.ac.uk/whatwedo/programmes/elearning/assessmentandfeedback/qtidi.aspx">QTI Delivery Integration (QTIDI)</a></li>
    <li><a href="https://www.jisc.ac.uk">JISC</a></li>
  </ul>

</page:page>
