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

package uk.ac.ed.ph.jqtiplus.node.item.interaction;

import uk.ac.ed.ph.jqtiplus.control.AssessmentItemController;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.group.content.ObjectGroup;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.XmlObject;
import uk.ac.ed.ph.jqtiplus.node.content.xhtml.object.Object;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * The drawing interaction allows the candidate to use a common set of drawing 
 * tools to modify a given graphical image (the canvas). It must be bound to a 
 * response variable with base-type file and single cardinality. The result is 
 * a file in the same format as the original image.
 * 
 * Contains : object [1]
 * The image that acts as the canvas on which the drawing takes place is given 
 * as an object which must be of an image type, as specified by the type attribute.
 * 
 * @author Jonathon Hare
 *
 */
public class DrawingInteraction extends BlockInteraction {
    private static final long serialVersionUID = 1L;

    /** Name of this class in xml schema. */
    public static String CLASS_TAG = "drawingInteraction";

    /**
     * Construct new interaction.
     *  
     * @param parent Parent node
     */
    public DrawingInteraction(XmlObject parent) {
        super(parent);

        getNodeGroups().add(new ObjectGroup(this, true));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    /**
     * Gets an unmodifiable list of the child elements. Use the other
     * methods on DrawingInteraction to add children to the correct group.
     */
    @Override
    public List<? extends XmlNode> getChildren() {
        List<XmlNode> children = new ArrayList<XmlNode>();
        
        children.addAll(super.getChildren());
        children.add(getNodeGroups().getObjectGroup().getObject());
        
        return Collections.unmodifiableList(children);
    }
    
    /**
     * Gets object child.
     *
     * @return object child
     * @see #setObject
     */
    public Object getObject() {
        return getNodeGroups().getObjectGroup().getObject();
    }

    /**
     * Sets new object child.
     *
     * @param object new object child
     * @see #getObject
     */
    public void setObject(Object object)
    {
        getNodeGroups().getObjectGroup().setObject(object);
    }
    
    @Override
    public ValidationResult validate(ValidationContext context) {
        ValidationResult result = super.validate(context);
        
        if (getResponseIdentifier() != null)
        {
            ResponseDeclaration declaration = getResponseDeclaration();
            if (declaration != null && declaration.getBaseType() != null && !declaration.getBaseType().isFile())
                result.add(new ValidationError(this, "Response variable must have file base type"));
            
            if (declaration != null && declaration.getCardinality() != null && !declaration.getCardinality().isSingle())
                result.add(new ValidationError(this, "Response variable must have single cardinality"));
        }
        
        if (getObject() != null && getObject().getType() !=null && !getObject().getType().startsWith("image/"))
            result.add(new ValidationError(this, "Object child must have an image type"));
        
        return result;    
    }

    @Override
    public boolean validateResponse(AssessmentItemController itemController, Value responseValue) {
        /* We assume anything is valid here */
        return true;
    }
}
