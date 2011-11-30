/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
*/

package uk.ac.ed.ph.jqtiplus.exception;

/**
 * Superclass of all unchecked exceptions.
 * <p>
 * It is abstract class because you should <em>never</em> use this class directly. For example:
 * </p>
 * <pre>
 * throw new QTIRuntimeException()
 * </pre>
 * (this is not possible anyway)
 * <p>
 * And you should <em>never</em> use this class in method header also. For example:
 * </p>
 * <pre>
 * void someMethod() throws QTIRuntimeException
 * </pre>
 * <p>
 * Only two legal usages are as superclass of all unchecked exceptions and in catch block,
 * when you don't need to distinguish between different exceptions types. For example:
 * </p>
 * <pre>
 * try
 * {
 *   ... some code here ...
 * }
 * catch (QTIRuntimeException ex)
 * {
 *   ... some code here ...
 * }
 * </pre>
 * <p>
 * We have decided to use only unchecked exceptions in JQTI library. There is big discussion on Internet about
 * checked/unchecked exceptions (see Google).
 *
 * @see uk.ac.ed.ph.jqtiplus.exception.QTIException
 * @author Jiri Kajaba
 */
public abstract class QTIRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs A new exception with <code>null</code> as its detailed message.
     */
    public QTIRuntimeException()
    {
        super();
    }

    /**
     * Constructs A new exception with the specified detailed message.
     *
     * @param message the detail message
     */
    public QTIRuntimeException(String message)
    {
        super(message);
    }

    /**
     * Constructs A new exception with the specified detailed message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public QTIRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs A new exception with the specified cause.
     * If cause is not <code>null</code> detailed message is set from this cause.
     *
     * @param cause the cause
     */
    public QTIRuntimeException(Throwable cause)
    {
        super(cause);
    }
}
