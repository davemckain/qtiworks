/* Copyright (c) 2012-2013, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.reading;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.testutils.UnitTestHelper;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

/**
 * Basic tests for {@link QtiObjectReader}
 *
 * @author David McKain
 */
public final class QtiObjectReaderTest {

    private QtiObjectReader qtiObjectReader;
    private URI choiceUri;

    @Before
    public void before() {
        qtiObjectReader = UnitTestHelper.createUnitTestQtiObjectReader(false);
        choiceUri = UnitTestHelper.createTestResourceUri("running/choice.xml");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLookupNullUri() throws Exception {
        qtiObjectReader.lookupRootNode(null, AssessmentItem.class);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLookupNullResultClass() throws Exception {
        qtiObjectReader.lookupRootNode(choiceUri, null);
    }

    @Test
    public void testLookupChoiceItemGood() throws Exception {
        final QtiObjectReadResult<AssessmentItem> result = qtiObjectReader.lookupRootNode(choiceUri, AssessmentItem.class);

        assertNotNull(result);
        assertEquals(QtiConstants.QTI_21_NAMESPACE_URI, result.getQtiNamespaceUri());
        assertEquals(AssessmentItem.class, result.getRequestedRootNodeClass());
        assertNotNull(result.getXmlParseResult());
        assertNotNull(result.getRootNode());
        assertEquals(AssessmentItem.class, result.getRootNode().getClass());
    }

    @Test
    public void testLookupChoiceItemVagueRoot() throws Exception {
        final QtiObjectReadResult<RootNode> result = qtiObjectReader.lookupRootNode(choiceUri);

        assertNotNull(result);
        assertEquals(RootNode.class, result.getRequestedRootNodeClass());
        assertEquals(AssessmentItem.class, result.getRootNode().getClass());
    }

    @Test(expected=XmlResourceNotFoundException.class)
    public void testLookupNotFound() throws Exception {
        qtiObjectReader.lookupRootNode(UnitTestHelper.createTestResourceUri("notfound.xml"));
    }

    @Test(expected=QtiXmlInterpretationException.class)
    public void testLookupChoiceItemWrongRoot() throws Exception {
        qtiObjectReader.lookupRootNode(choiceUri, AssessmentTest.class);
    }
}