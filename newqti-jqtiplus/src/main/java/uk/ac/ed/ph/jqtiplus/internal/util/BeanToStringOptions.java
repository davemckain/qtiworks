/* $Id: BeanToStringOptions.java 2764 2011-07-21 16:08:31Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.internal.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Trivial annotation that can be applied to a <tt>getX()</tt> method to prevent its
 * details from being listed by {@link ObjectUtilities#beanToString(Object)}.
 *
 * @author  David McKain
 * @version $Revision: 2764 $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeanToStringOptions {
    
    PropertyOptions value() default PropertyOptions.SHOW_FULL;

}