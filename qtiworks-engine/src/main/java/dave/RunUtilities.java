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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package dave;

import uk.ac.ed.ph.qtiworks.rendering.AbstractRenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.AuthorViewRenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.ItemRenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.SerializationMethod;
import uk.ac.ed.ph.qtiworks.rendering.TestRenderingOptions;

/**
 * FIXME: Document this type
 *
 * @author David McKain
 */
public final class RunUtilities {

    public static TestRenderingOptions createTestRenderingOptions() {
        final TestRenderingOptions result = new TestRenderingOptions();
        setBaseOptions(result);
        result.setTestPartNavigationUrl("/test-part-navigation");
        result.setSelectTestItemUrl("/select-item");
        result.setAdvanceTestItemUrl("/finish-item");
        result.setEndTestPartUrl("/end-test-part");
        result.setReviewTestPartUrl("/review-test-part");
        result.setReviewTestItemUrl("/review-item");
        result.setShowTestItemSolutionUrl("/item-solution");
        result.setAdvanceTestPartUrl("/advance-test-part");
        result.setExitTestUrl("/exit-test");
        return result;
    }

    public static ItemRenderingOptions createItemRenderingOptions() {
        final ItemRenderingOptions result = new ItemRenderingOptions();
        setBaseOptions(result);
        result.setEndUrl("/close");
        result.setSoftResetUrl("/reset-soft");
        result.setHardResetUrl("/reset-hard");
        result.setSolutionUrl("/solution");
        result.setExitUrl("/terminate");
        return result;
    }

    public static AuthorViewRenderingOptions createAuthorViewRenderingOptions() {
        final AuthorViewRenderingOptions result = new AuthorViewRenderingOptions();
        setBaseOptions(result);
        return result;
    }

    private static void setBaseOptions(final AbstractRenderingOptions result) {
        result.setSerializationMethod(SerializationMethod.HTML5_MATHJAX);
        result.setServeFileUrl("/file");
        result.setResponseUrl("/response");
        result.setAuthorViewUrl("/author-view");
        result.setSourceUrl("/source");
        result.setStateUrl("/state");
        result.setResultUrl("/result");
        result.setValidationUrl("/validation");
    }

}
