/* Copyright (c) 2012, University of Edinburgh.
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

import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSessionState;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Encapsulates parameters for controlling "external" aspects of the rendering process.
 *
 * @author David McKain
 */
public final class ItemRenderingRequest {

    @NotNull
    private RenderingMode renderingMode;

    @NotNull
    private ResourceLocator assessmentResourceLocator;

    @NotNull
    private URI assessmentResourceUri;

    @NotNull
    @Valid
    private RenderingOptions renderingOptions;

    /** Current state of the candidate's session */
    @NotNull
    private CandidateSessionState candidateSessionState;

    @NotNull
    private ItemSessionState itemSessionState;

    private Map<Identifier, ResponseData> responseInputs;

    private Set<Identifier> badResponseIdentifiers;

    private Set<Identifier> invalidResponseIdentifiers;

    private boolean closeAllowed;
    private boolean resetAllowed;
    private boolean reinitAllowed;
    private boolean solutionAllowed;
    private boolean sourceAllowed;
    private boolean resultAllowed;

    //----------------------------------------------------

    public RenderingMode getRenderingMode() {
        return renderingMode;
    }

    public void setRenderingMode(final RenderingMode renderingMode) {
        this.renderingMode = renderingMode;
    }


    public ResourceLocator getAssessmentResourceLocator() {
        return assessmentResourceLocator;
    }

    public void setAssessmentResourceLocator(final ResourceLocator assessmentResourceLocator) {
        this.assessmentResourceLocator = assessmentResourceLocator;
    }


    public URI getAssessmentResourceUri() {
        return assessmentResourceUri;
    }

    public void setAssessmentResourceUri(final URI assessmentResourceUri) {
        this.assessmentResourceUri = assessmentResourceUri;
    }


    public RenderingOptions getRenderingOptions() {
        return renderingOptions;
    }

    public void setRenderingOptions(final RenderingOptions renderingOptions) {
        this.renderingOptions = renderingOptions;
    }


    public CandidateSessionState getCandidateSessionState() {
        return candidateSessionState;
    }

    public void setCandidateSessionState(final CandidateSessionState candidateSessionState) {
        this.candidateSessionState = candidateSessionState;
    }


    public ItemSessionState getItemSessionState() {
        return itemSessionState;
    }

    public void setItemSessionState(final ItemSessionState itemSessionState) {
        this.itemSessionState = itemSessionState;
    }


    public Map<Identifier, ResponseData> getResponseInputs() {
        return responseInputs;
    }

    public void setResponseInputs(final Map<Identifier, ResponseData> responseInputs) {
        this.responseInputs = responseInputs;
    }


    public Set<Identifier> getBadResponseIdentifiers() {
        return badResponseIdentifiers;
    }

    public void setBadResponseIdentifiers(final Set<Identifier> badResponseIdentifiers) {
        this.badResponseIdentifiers = badResponseIdentifiers;
    }


    public Set<Identifier> getInvalidResponseIdentifiers() {
        return invalidResponseIdentifiers;
    }

    public void setInvalidResponseIdentifiers(final Set<Identifier> invalidResponseIdentifiers) {
        this.invalidResponseIdentifiers = invalidResponseIdentifiers;
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


    public boolean isSourceAllowed() {
        return sourceAllowed;
    }

    public void setSourceAllowed(final boolean sourceAllowed) {
        this.sourceAllowed = sourceAllowed;
    }


    public boolean isResultAllowed() {
        return resultAllowed;
    }

    public void setResultAllowed(final boolean resultAllowed) {
        this.resultAllowed = resultAllowed;
    }

    //----------------------------------------------------

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
