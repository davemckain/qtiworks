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

package uk.ac.ed.ph.jqtiplus.validation;

import uk.ac.ed.ph.jqtiplus.node.XmlNode;

import java.util.ArrayList;
import java.util.List;


/**
 * Result of validation. Container of validation items.
 * 
 * @author Jiri Kajaba
 */
public class ValidationResult
{
    /** Container of all errors. */
    private List<ValidationError> errors;

    /** Container of all warnings. */
    private List<ValidationWarning> warnings;

    /** Container of all infos. */
    private List<ValidationInfo> infos;

    /**
     * Container of all validation items.
     * <p>
     * Every validation item (error, warning, info) is in this container and in its type container.
     * Containers are kept synchronised all the time.
     * <p>
     * Do not add items directly getErrors().add(ERROR), use add(ERROR) method instead of it!
     */
    private List<ValidationItem> allItems;

    /**
     * Constructs validation result container.
     */
    public ValidationResult()
    {
        errors = new ArrayList<ValidationError>();
        warnings = new ArrayList<ValidationWarning>();
        infos = new ArrayList<ValidationInfo>();
        allItems = new ArrayList<ValidationItem>();
    }

    /**
     * Gets all errors of this container.
     * <p>
     * Do not add errors directly getErrors().add(ERROR), use add(ERROR) method instead of it!
     *
     * @return all errors of this container
     * @see #getErrors(XmlNode)
     */
    public List<ValidationError> getErrors()
    {
        return errors;
    }

    /**
     * Gets all errors of this container for given source node.
     * <p>
     * Convenient method for obtaining only errors related to one node.
     *
     * @param source given source node
     * @return all errors of this container for given source node
     * @see #getErrors()
     */
    @SuppressWarnings ("unchecked")
    public List<ValidationError> getErrors(XmlNode source)
    {
        return (List<ValidationError>) get(errors, source);
    }

    /**
     * Gets all warnings of this container.
     * <p>
     * Do not add warnings directly getWarnings().add(WARNING), use add(WARNING) method instead of it!
     *
     * @return all warnings of this container
     * @see #getWarnings(XmlNode)
     */
    public List<ValidationWarning> getWarnings()
    {
        return warnings;
    }

    /**
     * Gets all warnings of this container for given source node.
     * <p>
     * Convenient method for obtaining only warnings related to one node.
     *
     * @param source given source node
     * @return all warnings of this container for given source node
     * @see #getWarnings()
     */
    @SuppressWarnings ("unchecked")
    public List<ValidationWarning> getWarnings(XmlNode source)
    {
        return (List<ValidationWarning>) get(warnings, source);
    }

    /**
     * Gets all infos of this container.
     * <p>
     * Do not add infos directly getInfos().add(INFO), use add(INFO) method instead of it!
     *
     * @return all infos of this container
     * @see #getInfos(XmlNode)
     */
    public List<ValidationInfo> getInfos()
    {
        return infos;
    }

    /**
     * Gets all infos of this container for given source node.
     * <p>
     * Convenient method for obtaining only infos related to one node.
     *
     * @param source given source node
     * @return all infos of this container for given source node
     * @see #getInfos()
     */
    @SuppressWarnings ("unchecked")
    public List<ValidationInfo> getInfos(XmlNode source)
    {
        return (List<ValidationInfo>) get(infos, source);
    }

    /**
     * Gets all validation items (error, warning, info) of this container.
     * <p>
     * Do not add validation items directly getAllItems().add(ITEM), use add(ITEM) method instead of it!
     *
     * @return all validation items (error, warning, info) of this container
     * @see #getAllItems(XmlNode)
     */
    public List<ValidationItem> getAllItems()
    {
        return allItems;
    }

    /**
     * Gets all validation items (error, warning, info) of this container for given source node.
     * <p>
     * Convenient method for obtaining only validation items (error, warning, info) related to one node.
     *
     * @param source given source node
     * @return all validation items (error, warning, info) of this container for given source node
     * @see #getAllItems()
     */
    @SuppressWarnings ("unchecked")
    public List<ValidationItem> getAllItems(XmlNode source)
    {
        return (List<ValidationItem>) get(allItems, source);
    }

    /**
     * Gets all validation items from given source list of validation items for given source node.
     *
     * @param items source list of validation items
     * @param source given source node
     * @return all validation items from given source list of validation items for given source node
     */
    private List<? extends ValidationItem> get(List<? extends ValidationItem> items, XmlNode source)
    {
        List<ValidationItem> result = new ArrayList<ValidationItem>();

        for (ValidationItem item : items)
            if (item.getNode() == source)
                result.add(item);

        return result;
    }

    /**
     * Adds validation item into this container.
     * <ol>
     * <li>adds validation item into its type list (for example if item is error: <code>getErrors().add(ITEM)</code>)</li>
     * <li>adds validation item into allItems list (<code>getAllItems().add(ITEM)</code>)
     * </ol>
     * Using of this method is preferred way how to insert new item into this container.
     *
     * @param item item to be added
     */
    public void add(ValidationItem item)
    {
        if (item == null)
            return;

        if (item instanceof ValidationError)
            errors.add((ValidationError) item);
        else if (item instanceof ValidationWarning)
            warnings.add((ValidationWarning) item);
        else if (item instanceof ValidationInfo)
            infos.add((ValidationInfo) item);
        else
            throw new AssertionError("Unsupported validation item: " + item.getClass().getName());

        allItems.add(item);
    }

    /**
     * Adds all validation items from given container into this container.
     *
     * @param result container with validation items to be added
     */
    public void add(ValidationResult result)
    {
        for (ValidationItem item : result.getAllItems())
            add(item);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        if (toString(builder, errors) + toString(builder, warnings) + toString(builder, infos)==0) {
        	builder.append("Validation succeeded with no errors, warnings or information items");
        }
        return builder.toString();
    }

    /**
     * Prints given list of validation items into string.
     *
     * @param items given list of validation items
     * @return string with printed validation items
     */
    private int toString(StringBuilder builder, List<? extends ValidationItem> items)
    {
        int index = 1;
        for (ValidationItem item : items)
        {
            builder.append(index++);
            builder.append(") ");
            builder.append(item.toString());
            builder.append(XmlNode.NEW_LINE);
        }
        return items.size();
    }
}
