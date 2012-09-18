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
package uk.ac.ed.ph.jqtiplus.node.expression;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent of all expressions with random values.
 *
 * @author Jiri Kajaba
 */
public abstract class RandomExpression extends AbstractExpression {

    private static final long serialVersionUID = -3110092399048367667L;

    private static Logger logger = LoggerFactory.getLogger(RandomExpression.class);

    private static Random randomGenerator = new Random();

    public RandomExpression(final ExpressionParent parent, final String qtiClassName) {
        super(parent, qtiClassName);
    }

    /**
     * Gets value of seed attribute.
     *
     * @return value of seed attribute
     */
    protected abstract Long getSeedAttributeValue();

    /**
     * Generates long seed for random generator.
     * <ol>
     * <li>returns value of seed attribute if defined</li>
     * <li>returns null otherwise</li>
     * </ol>
     *
     * @param depth depth of current expression in expression tree (root's depth = 0)
     * @return long seed for random generator
     */
    private Long getSeed(final int depth) {
        final Long seed = getSeedAttributeValue();
        return seed;
    }

    /**
     * Gets a random number generator.
     * Generator will be created with seed if provided, otherwise the default generator is returned
     *
     * @param depth depth of current expression in expression tree (root's depth = 0)
     */
    protected Random getRandomGenerator(final int depth) {
        final Long seed = getSeed(depth);

        return seed!=null ? new Random(seed) : randomGenerator;
    }
}
