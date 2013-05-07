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
package uk.ac.ed.ph.jqtiplus.types;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates string-based response data, which is represented as an array
 * of Strings.
 *
 * @author David McKain
 */
public final class StringResponseData implements ResponseData {

    private static final long serialVersionUID = -3969978393997321192L;

    private final List<String> responseData;

    public StringResponseData(final List<String> responseData) {
        Assert.notNull(responseData, "responseData");
        this.responseData = new ArrayList<String>(responseData);
    }

    public StringResponseData(final String... responseData) {
        if (responseData!=null) {
            this.responseData = new ArrayList<String>(Arrays.asList(responseData));
        }
        else {
            this.responseData = Collections.emptyList();
        }
    }

    @Override
    public ResponseDataType getType() {
        return ResponseDataType.STRING;
    }

    public List<String> getResponseData() {
        return Collections.unmodifiableList(responseData);
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(" + responseData + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof StringResponseData)) {
            return false;
        }
        final StringResponseData other = (StringResponseData) obj;
        return responseData.equals(other.responseData);
    }

    @Override
    public int hashCode() {
        return responseData.hashCode();
    }
}
