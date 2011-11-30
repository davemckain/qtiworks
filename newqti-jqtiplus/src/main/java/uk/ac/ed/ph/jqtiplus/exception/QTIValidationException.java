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

import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.validation.ValidationItem;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;


/**
 * This exception is used for propagating validation problems.
 * This class is equivalent to <code>QTIEvaluationException</code> for validation.
 * 
 * @author Jiri Kajaba
 */
public class QTIValidationException extends QTIRuntimeException {

    private static final long serialVersionUID = -1647639383413827616L;
    
    private final ValidationResult validationResult;

    /**
     * Constructs A new exception with the specified detailed message.
     * Detailed message is created from given ValidationItem.
     *
     * @param item ValidationItem
     */
    public QTIValidationException(ValidationItem item) {
        super(createMessage(item));
        this.validationResult = new ValidationResult();
        validationResult.add(item);
    }

    /**
     * Constructs A new exception with the specified detailed message.
     * Detailed message is created from given ValidationResult.
     *
     * @param result ValidationResult
     */
    public QTIValidationException(ValidationResult result) {
        super(createMessage(result));
        this.validationResult = result;
    }
    
    public ValidationResult getValidationResult() {
        return validationResult;
    }

    /**
     * Creates exception message from given ValidationItem.
     *
     * @param item ValidationItem
     * @return exception message from given ValidationItem
     */
    private static String createMessage(ValidationItem item) {
        return "Validation failed." + XmlNode.NEW_LINE + item.toString();
    }

    /**
     * Creates exception message from given ValidationResult.
     *
     * @param result ValidationResult
     * @return exception message from given ValidationResult
     */
    private static String createMessage(ValidationResult result) {
        return "Validation failed." + XmlNode.NEW_LINE + result.toString();
    }
}
