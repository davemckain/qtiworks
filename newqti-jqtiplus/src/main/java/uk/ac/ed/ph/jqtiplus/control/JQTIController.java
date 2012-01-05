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
package uk.ac.ed.ph.jqtiplus.control;

import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.CustomOperator;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.UnsupportedCustomOperator;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.CustomInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.UnsupportedCustomInteraction;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FIXME: Rename and document this! It's not really a controller at all; it just keeps track of what extensions are registered
 * and things like that.
 * FIXME: Make note about thread safety. This Object should become read-only before being used in any processing since it
 * will shared by a number of Threads
 * 
 * @author David McKain
 */
@Deprecated
public final class JQTIController {

    private static final Logger logger = LoggerFactory.getLogger(JQTIController.class);

    private final List<JQTIExtensionPackage> extensionPackages;

    public JQTIController() {
        this.extensionPackages = new ArrayList<JQTIExtensionPackage>();
    }

    public List<JQTIExtensionPackage> getExtensionPackages() {
        return extensionPackages;
    }

    //---------------------------------------------------------------------

    public CustomInteraction createCustomInteraction(XmlNode parentObject, String interactionClass) {
        CustomInteraction result = null;
        for (final JQTIExtensionPackage extensionPackage : extensionPackages) {
            result = extensionPackage.createCustomInteraction(parentObject, interactionClass);
            if (result != null) {
                logger.debug("Created customInteraction of class {} using package {}", interactionClass, extensionPackage);
                return result;
            }
        }
        logger.warn("customInteraction of class {} not supported by any registered package. Using placeholder", interactionClass);
        return new UnsupportedCustomInteraction(parentObject);
    }

    public CustomOperator createCustomOperator(ExpressionParent expressionParent, String operatorClass) {
        CustomOperator result;
        for (final JQTIExtensionPackage extensionPackage : extensionPackages) {
            result = extensionPackage.createCustomOperator(expressionParent, operatorClass);
            if (result != null) {
                logger.debug("Created customOperator of class {} using package {}", operatorClass, extensionPackage);
                return result;
            }
        }
        logger.warn("customOperator of class {} not supported by any registered package. Using placeholder", operatorClass);
        return new UnsupportedCustomOperator(expressionParent);
    }

    //---------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(extensionPackages=" + extensionPackages
                + ")";
    }
}
