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
package uk.ac.ed.ph.jqtiplus.node;

import uk.ac.ed.ph.jqtiplus.provision.BadResourceException;
import uk.ac.ed.ph.jqtiplus.provision.RootNodeProvider;

/**
 * Defines the amount of "richness" that is provided in a JQTI Object Model.
 * <p>
 * This is mainly used when instantiating resources to control how much work should be done.
 * 
 * THIS IS STILL SLIGHLTY EXPERIMENTAL!
 *
 * @author David McKain
 */
public enum ModelRichness {
    
    /**
     * FIXME: Not yet implemented!
     * 
     * This might be useful during processing... it would omit presentational stuff from the
     * model and focus only on logic. Resources would need to be reloaded for subsequent
     * presentation though...
     */
    EXECUTION_ONLY,
    
    /**
     * Indicates that the model is known to be valid, so implementors can bypass checks
     * such as schema validation.
     */
    FULL_ASSUMED_VALID,
    
    /**
     * Indicates that the resource is going to be fully validated. Implementors of
     * {@link RootNodeProvider} should do anything else that supports this, such as
     * schema validation of XML resources. Failure of this lower-level validation should
     * cause Object provision to fail, with lower level errors reported via 
     * {@link BadResourceException}.
     */
    FOR_VALIDATION,
    ;

}
