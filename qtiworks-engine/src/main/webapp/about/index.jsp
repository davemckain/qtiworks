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
    QTIWorks is a new open-source tool for delivering Question &amp; Test
    Interoperability (QTI) v2.1 assessment items and tests. It is being
    developed as part of the JISC QTIDI project as a next-generation
    advancement of the existing QTIEngine and MathAssessEngine tools.  It will
    support almost all of the QTI 2.1 specification, as well as the MathAssess
    QTI extensions. A "Learning Tools Interoperability" (LTI) connector will
    make it possible to use QTIWorks to deliver assessments within popular
    learning systems such as BlackBoard and Moodle.
  </p>
  <ul>
    <li><a href="http://www.imsglobal.org/question/">IMS Question &amp; Test Interoperability (QTI) Specification</a></li>
    <li><a href="http://www.imsglobal.org/toolsinteroperability2.cfm">IMS Learning Tools Interoperability (LTI) Specification</a></li>
  </ul>

  <h3>Development</h3>
  <p>
    QTIWorks is open source and released under a BSD license. The project is currently being incubated in GitHub,
    whence you can find the source code and development documentation.
  </p>
  <p>
    The software is being developed within the School of Physics and Astronomy at the University of Edinburgh.
    The principal architect and developer is David McKain.
  </p>
  <p>
    QTIWorks is a reworking of the earlier MathAssessEngine and QTIEngine
    projects, the latter of which was developed by the University of
    Southampton. The functional part of it is a Java web application, which
    makes extensive use of the Spring Framework. At the core of QTIWorks is a
    significantly refactored version of Southampton's JQTI library, called JQTI+.
  </p>
  <ul>
    <li><a href="https://github.com/davemckain/qtiworks">QTIWorks on GitHub</a></li>
    <li><a href="http://www.ph.ed.ac.uk">School of Physics and Astronomy at the University of Edinburgh</a></li>
    <li><a href="http://www2.ph.ed.ac.uk/MathAssessEngine">MathAssessEngine</a></li>
    <li><a href="http://qtiengine.qtitools.org">QTIEngine</a></li>
  </ul>

  <h3>Status</h3>
  <p>
    QTIWorks currently supports the delivery of QTI 2.1 assessment items, covering a large subset
    of the QTI 2.1 specification, as well as the MathAssess extensions. The LTI connector is due to be
    developed during Q3 2012. Support for assessment tests is expected to be
    refactored from MathAssessEngine during Q4 2012.
  </p>
  <p>
    We are using a fairly rapid and iterative development cycle, with regular development releases being
    made available. We are also intending to regularly update the QTI Support Blog with development news,
    but sometimes that gets squeezed in favour of doing development work!
  </p>
  <ul>
    <li><a href="http://qtisupport.blogspot.co.uk">QTI Support Blog</a></li>
    <li><a href="${utils:internalLink(pageContext, '/release-notes.jsp')}">QTIWorks Release Notes</a></li>
  </ul>

  <h3>Funding</h3>
  <p>
    QTIWorks is being funded by as part of the QTI Delivery Integration
    project, funded by JISC. We are very grateful to JISC for its support and funding for
    this and relatated projects.
  </p>
  <ul>
    <li><a href="http://www.jisc.ac.uk/whatwedo/programmes/elearning/assessmentandfeedback/qtidi.aspx">QTI Delivery Integration (QTIDI)</a></li>
    <li><a href="http://www.jisc.ac.uk">JISC</a></li>
  </ul>

</page:page>
