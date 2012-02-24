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
package uk.ac.ed.ph.jqtiplus.node.result;

import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;

import org.w3c.dom.Element;

/**
 * An optional comment supplied by the candidate (see allowComment).
 * 
 * @author Jiri Kajaba
 */
public class CandidateComment extends AbstractNode {

    private static final long serialVersionUID = 3338529928447111381L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "candidateComment";

    /** Text content of this block. */
    private String textContent;

    /**
     * Constructs block.
     * 
     * @param parent parent of constructed block
     */
    public CandidateComment(ItemResult parent) {
        super(parent, QTI_CLASS_NAME);
    }

    /**
     * Constructs block and initialise its text content.
     * 
     * @param parent parent of constructed block
     * @param textContent text content of constructed block
     */
    public CandidateComment(ItemResult parent, String textContent) {
        super(parent, QTI_CLASS_NAME);
        this.textContent = textContent;
    }

    /**
     * Gets text content of this block.
     * 
     * @return text content of this block
     * @see #setTextContent
     */
    public String getTextContent() {
        return textContent;
    }

    /**
     * Sets new text content of this block.
     * 
     * @param textContent new text content of this block
     * @see #getTextContent
     */
    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    @Override
    protected void loadChildren(Element element, LoadingContext context) {
        textContent = element.getTextContent();
    }

    @Override
    protected String bodyToXmlString(int depth, boolean printDefaultAttributes) {
        return textContent != null ? escapeForXmlString(textContent, false) : "";
    }
}
