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
package uk.ac.ed.ph.jqtiplus.exception;

/**
 * Superclass of all checked exceptions (currently not used anywhere).
 * <p>
 * It is abstract class because you should <em>never</em> use this class directly. For example:
 * </p>
 *
 * <pre>
 * throw new QTIException()
 * </pre>
 *
 * (this is not possible anyway)
 * <p>
 * And you should <em>never</em> use this class in method header also. For example:
 * </p>
 *
 * <pre>
 * void someMethod() throws QTIException
 * </pre>
 * <p>
 * Only two legal usages are as superclass of all checked exceptions and in catch block, when you don't need to distinguish between different exceptions types.
 * For example:
 * </p>
 *
 * <pre>
 * try
 * {
 *   ... some code here ...
 * }
 * catch (QTIException ex)
 * {
 *   ... some code here ...
 * }
 * </pre>
 * <p>
 * Checked exceptions are reserved for special purposes. In general case you should use unchecked exception instead.
 * </p>
 *
 * @see uk.ac.ed.ph.jqtiplus.exception.QtiRuntimeException
 * @author Jiri Kajaba
 */
public abstract class QtiException extends Exception {

    private static final long serialVersionUID = 4870005618050972243L;

    public QtiException() {
        super();
    }

    public QtiException(final String message) {
        super(message);
    }

    public QtiException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public QtiException(final Throwable cause) {
        super(cause);
    }
}
