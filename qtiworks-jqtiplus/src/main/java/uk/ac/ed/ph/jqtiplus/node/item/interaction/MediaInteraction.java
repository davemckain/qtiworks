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
package uk.ac.ed.ph.jqtiplus.node.item.interaction;

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.IntegerAttribute;
import uk.ac.ed.ph.jqtiplus.group.content.ObjectGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.object.Object;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.InteractionBindingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * The media interaction allows more control over the way the
 * candidate interacts with a time-based media object and allows
 * the number of times the media object was experienced to be
 * reported in the value of the associated response variable,
 * which must be of base-type integer and single cardinality.
 * Attribute : autostart [1]: boolean
 * The autostart attribute determines if the media object should
 * begin as soon as the candidate starts the attempt (true) or if
 * the media object should be started under the control of the
 * candidate (false).
 * Attribute : minPlays [0..1]: integer = 0
 * The minPlays attribute indicates that the media object should
 * be played a minimum number of times by the candidate. The
 * techniques required to enforce this will vary from system to
 * system, in some systems it may not be possible at all. By default
 * there is no minimum. Failure to play the media object the minimum
 * number of times constitutes an invalid response.
 * Attribute : maxPlays [0..1]: integer = 0
 * The maxPlays attribute indicates that the media object can be
 * played at most maxPlays times - it must not be possible for the
 * candidate to play the media object more than maxPlay times. A
 * value of 0 (the default) indicates that there is no limit.
 * Attribute : loop [0..1]: boolean = false
 * The loop attribute is used to set continuous play mode. In
 * continuous play mode, once the media object has started to play
 * it should play continuously (subject to maxPlays).
 * Contains : object [1]
 * The media object itself.
 *
 * @author Jonathon Hare
 */
public final class MediaInteraction extends BlockInteraction {

    private static final long serialVersionUID = -1273962848944879873L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "mediaInteraction";

    /** Name of autostart attribute in xml schema. */
    public static final String ATTR_AUTOSTART_NAME = "autostart";

    /** Name of minPlays attribute in xml schema. */
    public static final String ATTR_MIN_PLAYS_NAME = "minPlays";

    /** Default value of minPlays attribute. */
    public static final int ATTR_MIN_PLAYS_DEFAULT_VALUE = 0;

    /** Name of maxPlays attribute in xml schema. */
    public static final String ATTR_MAX_PLAYS_NAME = "maxPlays";

    /** Default value of maxPlays attribute. */
    public static final int ATTR_MAX_PLAYS_DEFAULT_VALUE = 0;

    /** Name of loop attribute in xml schema. */
    public static final String ATTR_LOOP_NAME = "loop";

    /** Default value of loop attribute. */
    public static final boolean ATTR_LOOP_DEFAULT_VALUE = false;

    public MediaInteraction(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new BooleanAttribute(this, ATTR_AUTOSTART_NAME, true));
        getAttributes().add(new IntegerAttribute(this, ATTR_MIN_PLAYS_NAME, ATTR_MIN_PLAYS_DEFAULT_VALUE, false));
        getAttributes().add(new IntegerAttribute(this, ATTR_MAX_PLAYS_NAME, ATTR_MAX_PLAYS_DEFAULT_VALUE, false));
        getAttributes().add(new BooleanAttribute(this, ATTR_LOOP_NAME, ATTR_LOOP_DEFAULT_VALUE, false));

        getNodeGroups().add(new ObjectGroup(this, true));
    }


    public boolean getAutostart() {
        return getAttributes().getBooleanAttribute(ATTR_AUTOSTART_NAME).getComputedNonNullValue();
    }

    public void setAutostart(final Boolean autostart) {
        getAttributes().getBooleanAttribute(ATTR_AUTOSTART_NAME).setValue(autostart);
    }


    public int getMinPlays() {
        return getAttributes().getIntegerAttribute(ATTR_MIN_PLAYS_NAME).getComputedNonNullValue();
    }

    public void setMinPlays(final Integer minPlays) {
        getAttributes().getIntegerAttribute(ATTR_MIN_PLAYS_NAME).setValue(minPlays);
    }


    public int getMaxPlays() {
        return getAttributes().getIntegerAttribute(ATTR_MAX_PLAYS_NAME).getComputedNonNullValue();
    }

    public void setMaxPlays(final Integer maxPlays) {
        getAttributes().getIntegerAttribute(ATTR_MAX_PLAYS_NAME).setValue(maxPlays);
    }


    public boolean getLoop() {
        return getAttributes().getBooleanAttribute(ATTR_LOOP_NAME).getComputedNonNullValue();
    }


    public void setLoop(final Boolean loop) {
        getAttributes().getBooleanAttribute(ATTR_LOOP_NAME).setValue(loop);
    }


    public Object getObject() {
        return getNodeGroups().getObjectGroup().getObject();
    }

    public void setObject(final Object object) {
        getNodeGroups().getObjectGroup().setObject(object);
    }

    @Override
    protected void validateThis(final ValidationContext context, final ResponseDeclaration responseDeclaration) {
        if (responseDeclaration!=null) {
            if (responseDeclaration.getBaseType() != null && !responseDeclaration.getBaseType().isInteger()) {
                context.fireValidationError(this, "Response variable must have integer base type");
            }

            if (!responseDeclaration.getCardinality().isSingle()) {
                context.fireValidationError(this, "Response variable must have single cardinality");
            }
        }
    }

    @Override
    public boolean validateResponse(final InteractionBindingContext interactionBindingContext, final Value responseValue) {
        /* We assume anything is valid here */
        return true;
    }
}
