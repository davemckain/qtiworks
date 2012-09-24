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
package uk.ac.ed.ph.jqtiplus.validation;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.notification.ModelNotification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Partial base class for result of validation. Container of validation items.
 *
 * @author Jiri Kajaba
 * @authro David McKain
 */
public abstract class AbstractValidationResult implements Serializable {

    private static final long serialVersionUID = 7987550924957601153L;

    private final List<ModelNotification> modelNotifications;

    /** Container of all errors. */
    private final List<ValidationError> errors;

    /** Container of all warnings. */
    private final List<ValidationWarning> warnings;

    /**
     * Container of all validation items.
     * <p>
     * Every validation item (error, warning, info) is in this container and in its type container. Containers are kept synchronised all the time.
     * <p>
     * Do not add items directly getErrors().add(ERROR), use add(ERROR) method instead of it!
     */
    private final List<ValidationItem> allItems;

    /**
     * Constructs validation result container.
     */
    public AbstractValidationResult() {
        this.modelNotifications = new ArrayList<ModelNotification>();
        this.errors = new ArrayList<ValidationError>();
        this.warnings = new ArrayList<ValidationWarning>();
        this.allItems = new ArrayList<ValidationItem>();
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Gets all errors of this container.
     * <p>
     * Do not add errors directly getErrors().add(ERROR), use add(ERROR) method instead of it!
     *
     * @return all errors of this container
     * @see #getErrors(QtiNode)
     */
    public List<ValidationError> getErrors() {
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
    public List<ValidationError> getErrors(final QtiNode source) {
        return get(errors, source);
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    /**
     * Gets all warnings of this container.
     * <p>
     * Do not add warnings directly getWarnings().add(WARNING), use add(WARNING) method instead of it!
     *
     * @return all warnings of this container
     * @see #getWarnings(QtiNode)
     */
    public List<ValidationWarning> getWarnings() {
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
    @ObjectDumperOptions(DumpMode.DEEP)
    public List<ValidationWarning> getWarnings(final QtiNode source) {
        return get(warnings, source);
    }

    /**
     * Gets all validation items (error, warning, info) of this container.
     * <p>
     * Do not add validation items directly getAllItems().add(ITEM), use add(ITEM) method instead of it!
     *
     * @return all validation items (error, warning, info) of this container
     * @see #getAllItems(QtiNode)
     */
    @ObjectDumperOptions(DumpMode.IGNORE)
    public List<ValidationItem> getAllItems() {
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
    public List<ValidationItem> getAllItems(final QtiNode source) {
        return get(allItems, source);
    }

    /**
     * Gets all validation items from given source list of validation items for given source node.
     *
     * @param items source list of validation items
     * @param source given source node
     * @return all validation items from given source list of validation items for given source node
     */
    private static <E extends ValidationItem> List<E> get(final List<E> items, final QtiNode source) {
        final List<E> result = new ArrayList<E>();
        for (final E item : items) {
            if (item.getNode() == source) {
                result.add(item);
            }
        }
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
    public void add(final ValidationItem item) {
        Assert.notNull(item);
        if (item instanceof ValidationError) {
            errors.add((ValidationError) item);
        }
        else if (item instanceof ValidationWarning) {
            warnings.add((ValidationWarning) item);
        }
        else {
            throw new IllegalArgumentException("Unsupported validation item: " + item.getClass().getName());
        }
        allItems.add(item);
    }

    public void addAll(final Iterable<ValidationItem> items) {
        for (final ValidationItem item : items) {
            add(item);
        }
    }

    public void add(final ModelNotification modelNotification) {
        modelNotifications.add(modelNotification);
    }

}
