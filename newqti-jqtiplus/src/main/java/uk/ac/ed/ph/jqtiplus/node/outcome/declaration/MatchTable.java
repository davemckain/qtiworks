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

package uk.ac.ed.ph.jqtiplus.node.outcome.declaration;

import uk.ac.ed.ph.jqtiplus.exception.QTIBaseTypeException;
import uk.ac.ed.ph.jqtiplus.group.outcome.declaration.MatchTableEntryGroup;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;

import java.util.List;


/**
 * A matchTable transforms A source integer by finding the first matchTableEntry with an exact match to the source.
 * 
 * @author Jiri Kajaba
 */
public class MatchTable extends LookupTable
{
    private static final long serialVersionUID = 1L;
    
    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "matchTable";

    /**
     * Creates object.
     *
     * @param parent parent of this object
     */
    public MatchTable(OutcomeDeclaration parent)
    {
        super(parent);

        getNodeGroups().add(new MatchTableEntryGroup(this));
    }

    @Override
    public String getClassTag()
    {
        return CLASS_TAG;
    }

    @Override
    public List<? extends LookupTableEntry> getLookupEntries()
    {
        return getMatchEntries();
    }

    /**
     * Gets matchTableEntry children.
     *
     * @return matchTableEntry children
     */
    public List<MatchTableEntry> getMatchEntries()
    {
        return getNodeGroups().getMatchTableEntryGroup().getEntries();
    }

    @Override
    public SingleValue getTargetValue(NumberValue sourceValue)
    {
        if (sourceValue != null)
        {
            if (!(sourceValue instanceof IntegerValue))
                throw new QTIBaseTypeException(computeXPath() + " Invalid baseType: " + sourceValue.getBaseType());

            for (MatchTableEntry entry : getMatchEntries())
            {
                if (entry.getSourceValue().intValue() == sourceValue.intValue())
                    return entry.getTargetValue();
            }
        }

        return super.getTargetValue(sourceValue);
    }
}
