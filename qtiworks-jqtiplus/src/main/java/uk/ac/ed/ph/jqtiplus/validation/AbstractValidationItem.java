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
package uk.ac.ed.ph.jqtiplus.validation;

import uk.ac.ed.ph.jqtiplus.node.XmlNode;

import java.io.Serializable;

/**
 * Abstract partial implementation of ValidationItem.
 * 
 * @author Jiri Kajaba
 */
abstract class AbstractValidationItem implements ValidationItem, Serializable {

    private static final long serialVersionUID = -965289438371398086L;

    /** Source of this item. */
    private final Validatable source;

    /** Source node of this item. */
    private final XmlNode node;

    /** Message of this item. */
    private final String message;

    private final Throwable cause;

    /**
     * Constructs validation item.
     * 
     * @param source source of constructed item
     * @param node source node of constructed item
     * @param message message of constructed item
     */
    public AbstractValidationItem(Validatable source, XmlNode node, String message) {
        this(source, node, message, null);
    }

    public AbstractValidationItem(Validatable source, XmlNode node, String message, Throwable cause) {
        this.source = source;
        this.node = node;
        this.message = message;
        this.cause = cause;
    }

    @Override
    public Validatable getSource() {
        return source;
    }

    @Override
    public XmlNode getNode() {
        return node;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(source=" + source
                + ",node=" + node
                + ",message=" + message
                + ",cause=" + cause
                + ")";
    }
}
