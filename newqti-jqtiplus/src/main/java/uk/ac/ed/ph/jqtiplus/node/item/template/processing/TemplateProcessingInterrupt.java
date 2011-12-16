/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.node.item.template.processing;

import uk.ac.ed.ph.jqtiplus.exception.QTIProcessingInterrupt;

/**
 * Interrupt exception thrown to stop the normal flow of template processing, which
 * happens if {@link ExitTemplate} is encountered or if a {@link TemplateConstraint} fails.
 *
 * @author  David McKain
 * @version $Revision$
 */
public final class TemplateProcessingInterrupt extends QTIProcessingInterrupt {

	private static final long serialVersionUID = -5065976029182961590L;
	
	public static enum InterruptType {
		EXIT_TEMPLATE,
		TEMPLATE_CONSTRAINT_FAILURE,
		;
	}
	
	private final InterruptType interruptType;
	
	public TemplateProcessingInterrupt(InterruptType interruptType) {
		super();
		this.interruptType = interruptType;
	}
	
	public InterruptType getInterruptType() {
		return interruptType;
	}
}
