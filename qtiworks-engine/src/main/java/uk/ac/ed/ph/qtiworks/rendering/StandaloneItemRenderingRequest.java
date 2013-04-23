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
package uk.ac.ed.ph.qtiworks.rendering;

import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;

import javax.validation.constraints.NotNull;

/**
 * Encapsulates the required data for rendering the current state of a standalone
 * item.
 *
 * @author David McKain
 */
public final class StandaloneItemRenderingRequest extends AbstractRenderingRequest<ItemRenderingOptions> {

    /** Selected {@link RenderingMode} */
    @ToRefactor
    private RenderingMode renderingMode;

    /** Required {@link ItemSessionState} to be rendered */
    @NotNull
    private ItemSessionState itemSessionState;

    private String prompt;
    private boolean closeAllowed;
    private boolean resetAllowed;
    private boolean reinitAllowed;
    private boolean solutionAllowed;
    private boolean candidateCommentAllowed;

    //----------------------------------------------------

    public RenderingMode getRenderingMode() {
        return renderingMode;
    }

    public void setRenderingMode(final RenderingMode renderingMode) {
        this.renderingMode = renderingMode;
    }


    public ItemSessionState getItemSessionState() {
        return itemSessionState;
    }

    public void setItemSessionState(final ItemSessionState itemSessionState) {
        this.itemSessionState = itemSessionState;
    }


    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(final String prompt) {
        this.prompt = prompt;
    }


    public boolean isCloseAllowed() {
        return closeAllowed;
    }

    public void setCloseAllowed(final boolean closeAllowed) {
        this.closeAllowed = closeAllowed;
    }


    public boolean isResetAllowed() {
        return resetAllowed;
    }

    public void setResetAllowed(final boolean resetAllowed) {
        this.resetAllowed = resetAllowed;
    }


    public boolean isReinitAllowed() {
        return reinitAllowed;
    }

    public void setReinitAllowed(final boolean reinitAllowed) {
        this.reinitAllowed = reinitAllowed;
    }


    public boolean isSolutionAllowed() {
        return solutionAllowed;
    }

    public void setSolutionAllowed(final boolean solutionAllowed) {
        this.solutionAllowed = solutionAllowed;
    }


    public boolean isCandidateCommentAllowed() {
        return candidateCommentAllowed;
    }

    public void setCandidateCommentAllowed(final boolean candidateCommentAllowed) {
        this.candidateCommentAllowed = candidateCommentAllowed;
    }
}
