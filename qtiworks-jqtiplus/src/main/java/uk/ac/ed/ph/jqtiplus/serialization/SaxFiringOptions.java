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
package uk.ac.ed.ph.jqtiplus.serialization;

import uk.ac.ed.ph.jqtiplus.QtiProfile;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import java.io.Serializable;

/**
 * Encapsulates immutable options for serializing QTI Object by firing SAX events.
 *
 * @see QtiSaxDocumentFirer
 *
 * @author David McKain
 */
public final class SaxFiringOptions implements Serializable {

    private static final long serialVersionUID = -7438651199833877599L;

    private final NamespacePrefixMappings preferredPrefixMappings;
    private final boolean omitSchemaLocation;
    private final QtiProfile qtiProfile;


    public SaxFiringOptions() {
        this(false, QtiProfile.QTI_21_CORE, null);
    }

    public SaxFiringOptions(final QtiProfile qtiProfile) {
        this(false, qtiProfile, null);
    }

    public SaxFiringOptions(final boolean omitSchemaLocation) {
        this(omitSchemaLocation, QtiProfile.QTI_21_CORE, null);
    }

    public SaxFiringOptions(final boolean omitSchemaLocation, final QtiProfile qtiProfile) {
        this(omitSchemaLocation, qtiProfile, null);
    }

    public SaxFiringOptions(final boolean omitSchemaLocation, final QtiProfile qtiProfile, final NamespacePrefixMappings namespacePrefixMappings) {
        this.preferredPrefixMappings = new NamespacePrefixMappings(namespacePrefixMappings);
        this.omitSchemaLocation = omitSchemaLocation;
        this.qtiProfile = qtiProfile;
    }

    public NamespacePrefixMappings getPreferredPrefixMappings() {
        return preferredPrefixMappings;
    }

    public boolean isOmitSchemaLocation() {
        return omitSchemaLocation;
    }

    public QtiProfile getQtiProfile() {
        return qtiProfile;
    }

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }

}
