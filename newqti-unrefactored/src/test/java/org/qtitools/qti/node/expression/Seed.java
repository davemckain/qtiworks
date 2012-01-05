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
package org.qtitools.qti.node.expression;

import java.util.Random;

/**
 * Utility class for <code>RandomInteger</code> class tests.
 * Returns seeds for given requested random numbers.
 * 
 * @see org.qtitools.qti.expression.general.RandomIntegerAcceptTest
 */
public class Seed {

    private final int minimum;

    private final int maximum;

    private final int step;

    public Seed(int minimum, int maximum, int step) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.step = step;
    }

    public Integer[] run() {
        final Integer[] seeds = new Integer[(maximum - minimum) / step + 1];
        int empty = seeds.length;
        int seed = 0;
        while (empty > 0) {
            final Random randomGenerator = new Random(seed);
            final int randomNumber = randomGenerator.nextInt((maximum - minimum) / step + 1);
            if (seeds[randomNumber] == null) {
                seeds[randomNumber] = seed;
                empty--;
            }
            seed++;
        }

        return seeds;
    }

    public static void main(String[] args) {
        // int minimum = Integer.parseInt(args[0]);
        // int maximum = Integer.parseInt(args[1]);
        // int step = Integer.parseInt(args[2]);

        final int minimum = -3;
        final int maximum = 4;
        final int step = 1;

        final Seed seed = new Seed(minimum, maximum, step);
        final Integer[] seeds = seed.run();

        System.out.println("Minimum = " + minimum + ", Maximum = " + maximum + ", Step = " + step + ", Count = " + seeds.length);
        for (int i = 0; i < seeds.length; i++) {
            System.out.println("Random number = " + i + ", Seed = " + seeds[i]);
        }
    }
}
