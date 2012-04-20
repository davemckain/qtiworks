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
package uk.ac.ed.ph.jqtiplus.node.item.interaction;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.exception2.ResponseBindingException;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Block;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Flow;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CustomInteraction
 *
 * FIXME: We're using the 'class' attribute to specify the interaction being used. We should change the validation process
 * to require only a *single* value.
 *
 * @param <E> {@link JqtiExtensionPackage} providing the implementation of this interaction
 *
 * @author David McKain (new API)
 * @author Jonathon Hare (original)
 */
public abstract class CustomInteraction<E extends JqtiExtensionPackage<E>> extends Interaction implements Block, Flow {

    private static final Logger logger = LoggerFactory.getLogger(CustomInteraction.class);

    private static final long serialVersionUID = 4937420907911035196L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "customInteraction";

    public CustomInteraction(final XmlNode parent) {
        super(parent, QTI_CLASS_NAME);
    }

    @Override
    public final List<? extends XmlNode> getChildren() {
        return null;
    }

    @Override
    public final void validate(final ValidationContext context) {
        super.validate(context);
        final E jqtiExtensionPackage = getOwningExtensionPackage(context);
        if (jqtiExtensionPackage!=null) {
            validateCustomInteractionAttributes(jqtiExtensionPackage, context);
        }
        else {
            context.add(new ValidationError(this, "customInteraction with class " + getClassAttr() + " is not supported"));
        }
    }

    protected abstract void validateCustomInteractionAttributes(final E jqtiExtensionPackaage, final ValidationContext context);

    @Override
    public final void bindResponse(final ItemSessionController itemSessionController, final ResponseData responseData) throws ResponseBindingException {
        final E jqtiExtensionPackage = getOwningExtensionPackage(itemSessionController);
        if (jqtiExtensionPackage!=null) {
            bindResponse(jqtiExtensionPackage, itemSessionController, responseData);
        }
        else {
            logger.debug("JqtiExtensionPackage owning this customInteraction is not registered, so not binding");
        }
    }

    protected abstract void bindResponse(E jqtiExtensionPackage, ItemSessionController itemSessionController, ResponseData responseData)
            throws ResponseBindingException;

    @Override
    public final boolean validateResponse(final ItemSessionController itemSessionController, final Value responseValue) {
        final E jqtiExtensionPackage = getOwningExtensionPackage(itemSessionController);
        if (jqtiExtensionPackage!=null) {
            return validateResponse(jqtiExtensionPackage, itemSessionController, responseValue);
        }
        else {
            logger.debug("JqtiExtensionPackage owning this customInteraction is not registered, so returning false");
            return false;
        }
    }

    protected abstract boolean validateResponse(E jqtiExtensionPackage, ItemSessionController itemController, Value responseValue);

    protected final E getOwningExtensionPackage(final ItemSessionController itemSessionController) {
        return itemSessionController.getJqtiExtensionManager().getJqtiExtensionPackageImplementingInteraction(this);
    }

    protected final E getOwningExtensionPackage(final ValidationContext context) {
        return context.getJqtiExtensionManager().getJqtiExtensionPackageImplementingInteraction(this);
    }
}
