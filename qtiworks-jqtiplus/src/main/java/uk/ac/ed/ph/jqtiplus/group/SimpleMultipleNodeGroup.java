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
package uk.ac.ed.ph.jqtiplus.group;

import uk.ac.ed.ph.jqtiplus.node.QtiNode;

/**
 * Base class for {@link NodeGroup}s that can contain zero or more children, all of the same type.
 *
 * @author David McKain
 */
public abstract class SimpleMultipleNodeGroup<P extends QtiNode, C extends QtiNode> extends AbstractNodeGroup<P,C> {

    private static final long serialVersionUID = 3006947671579885917L;

    /** Constructor appropriate for when an arbitrary number of children are permitted */
    public SimpleMultipleNodeGroup(final P parent, final String childQtiClass) {
        super(parent, childQtiClass, 0, null);
    }

    public SimpleMultipleNodeGroup(final P parent, final String childQtiClass, final int minimum, final int maximum) {
        super(parent, childQtiClass, minimum, maximum);
    }

    public SimpleMultipleNodeGroup(final P parent, final String childQtiClass, final int minimum, final Integer maximum) {
        super(parent, childQtiClass, minimum, maximum);
    }

    @Override
    public final boolean supportsQtiClass(final String qtiClassName) {
        return name.equals(qtiClassName);
    }

    @Override
    public final boolean isComplexContent() {
        return false;
    }

    @Override
    public final C create(final String qtiClassName) {
        return create();
    }

    public abstract C create();
}
