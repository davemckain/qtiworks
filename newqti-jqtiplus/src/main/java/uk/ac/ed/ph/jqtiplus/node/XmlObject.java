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

package uk.ac.ed.ph.jqtiplus.node;

import uk.ac.ed.ph.jqtiplus.control.ToRemove;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;


/**
 * Parent of all xml objects.
 * 
 * @author Jiri Kajaba
 */
public interface XmlObject extends XmlNode
{
    /**
     * Gets parent of this object or null (if object is root; for example AssessmentTest).
     * <p>
     * While testing some objects (for example expressions) don't have properly set parent, but it is usable only for testing.
     * (Some objects cannot exists without parent even for testing).
     *
     * @return parent of this object or null (if object is root; for example AssessmentTest)
     */
    public XmlObject getParent();

/* Parents used to be mutable, but only to facilitate ordering in tests. This is smelly so is removed now */
@ToRemove
//    /**
//     * Sets the parent of this object.
//     * @param parent Parent object to set
//     */
//    public void setParent(XmlObject parent);

    /**
     * Gets root assessmentTest or null (if root is different type).
     *
     * @return root assessmentTest or null (if root is different type)
     */
    public AssessmentTest getParentTest();

    /**
     * Gets root assessmentItem or null (if root is different type).
     *
     * @return root assessmentItem or null (if root is different type)
     */
    public AssessmentItem getParentItem();

    /**
     * Gets root assessmentResult or null (if root is different type).
     *
     * @return root assessmentResult or null (if root is different type)
     */
    public AssessmentResult getParentResult();
}
