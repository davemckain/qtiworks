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
package uk.ac.ed.ph.qtiworks.utils;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple extension of Java 1.4's {@link LinkedHashMap} that makes its inherent ability
 * to be an LRU cache a bit more prominent.
 *
 * @param <K> key type
 * @param <V> value type
 *
 * @author David McKain
 */
public class LruHashMap<K,V> extends LinkedHashMap<K,V> {

    private static final long serialVersionUID = 4050206323365198133L;
    private static final Logger logger = LoggerFactory.getLogger(LruHashMap.class);

    private static final int DEFAULT_MAXIMUM_SIZE = 10;

    /** Maximum size of cache */
    private int maxSize = DEFAULT_MAXIMUM_SIZE;

    /** Number of elements that have been purged from cache */
    private int purgeCount = 0;

    public LruHashMap() {
        super();
    }

    public LruHashMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public LruHashMap(final int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    protected final boolean removeEldestEntry(final Entry<K,V> eldest) {
        final boolean shouldRemove = (maxSize > 0 && size() > maxSize);
        if (shouldRemove) {
            logger.info("Removing eldest entry " + eldest.getKey());
            purgeCount++;
        }
        return shouldRemove;
    }

    /**
     * @return Returns the maximum number of stylesheets cached by this cache.
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Sets the maximum number of stylesheets held by this cache. This will be
     * enforced the next time a stylesheet is added to the cache.
     * <p>
     * If maxSize <= 0 then the cache will be unbounded.
     */
    public void setMaxSize(final int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Returns the number of elements that have been purged from this Map due
     * to size issues. This can be useful for auditing purposes.
     *
     * @return number of elements that have been removed from the Map to accommodate
     *   the maximum size.
     */
    public int getPurgeCount() {
        return this.purgeCount;
    }

}
