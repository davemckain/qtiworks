/* Copyright (c) 2012-2013, University of Edinburgh.
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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.qtiworks.web;

import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility bean that clears "dangerous" values from {@link ThreadLocal}s when the
 * {@link #purgeBlacklistedThreadLocals()} method is called.
 * <p>
 * This should be called during webapp shutdown to ensure that any application-created
 * {@link ThreadLocal}s values are purged in order to prevent memory leaks.
 * <p>
 * The decision as to whether a value should be purged is based on the static
 * {@link #THREADLOCAL_VALUE_BLACKLIST}. Add regexps to this to suit this webapp as required.
 * <p>
 * (The code here is based on the <tt>checkThreadLocalsForLeaks()</tt> method added in Tomcat 7's
 * <tt>WebappClassLoader</tt> class.)
 *
 * @author Remy Maucherat
 * @author Craig R. McClanahan
 * @author David McKain
 */
public class ThreadLocalCleaner {

    private final Logger logger = LoggerFactory.getLogger(ThreadLocalCleaner.class);

    /**
     * Array of regexps to be matched against the Class names of values stored in {@link ThreadLocal}s.
     * Any matches will be removed from the {@link ThreadLocal} when {@link #purgeBlacklistedThreadLocals()}
     * is called.
     * <p>
     * Add any additional regexps to this array as required.
     */
    private static final String[] THREADLOCAL_VALUE_BLACKLIST = new String[] {
        "net\\.sf\\.saxon\\.sort\\.LRUCache",
        "\\[Lorg\\.springframework\\.cglib\\.proxy\\.Callback;",
        "uk\\.ac\\.ed\\.ph.*"
    };

    public void purgeBlacklistedThreadLocals() {
        /* Compile blacklist regexps */
        final List<Pattern> blacklistPatterns = new ArrayList<Pattern>();
        for (final String blacklistRegexp : THREADLOCAL_VALUE_BLACKLIST) {
            blacklistPatterns.add(Pattern.compile(blacklistRegexp));
        }
        try {
            /* Get at the classes and fields we need to access */
            final Class<?> threadLocalMapClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
            final Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
            threadLocalsField.setAccessible(true);
            final Field inheritableThreadLocalsField = Thread.class.getDeclaredField("inheritableThreadLocals");
            inheritableThreadLocalsField.setAccessible(true);
            final Field tableField = threadLocalMapClass.getDeclaredField("table");
            tableField.setAccessible(true);

            /* Now check each Thread */
            for (final Thread thread : Thread.getAllStackTraces().keySet()) {
                logger.trace("+ THREAD {}", thread.getName());
                Object threadLocalMap = threadLocalsField.get(thread);
                purgeBlacklistedThreadLocalsInMap(thread, threadLocalMap, tableField, blacklistPatterns);

                threadLocalMap = inheritableThreadLocalsField.get(thread);
                purgeBlacklistedThreadLocalsInMap(thread, threadLocalMap, tableField, blacklistPatterns);
            }
        }
        catch (final Exception e) {
            logger.error("Unexpected error clearing ThreadLocals - possible memory leak may occur", e);
        }
    }

    private void purgeBlacklistedThreadLocalsInMap(final Thread thread, final Object threadLocalMap,
            final Field tableField, final List<Pattern> blacklistPatterns) throws Exception {
        if (threadLocalMap==null) {
            return;
        }
        logger.trace("--+ THREAD LOCAL MAP {}", threadLocalMap);
        final Object[] table = (Object[]) tableField.get(threadLocalMap);
        if (table==null) {
            return;
        }
        for (final Object entry : table) {
            if (entry==null) {
                continue;
            }
            @SuppressWarnings("unchecked")
            final ThreadLocal<?> threadLocal = ((Reference<ThreadLocal<?>>) entry).get();
            final Field valueField = entry.getClass().getDeclaredField("value");
            valueField.setAccessible(true);
            final Object value = valueField.get(entry);
            logger.trace("----+ ENTRY: {} THREADLOCAL: {} VALUE: {}",
                    entry, threadLocal, (value!=null ? value.getClass().getName() : ""));
            if (threadLocal!=null && value!=null && isBlacklistedValue(value, blacklistPatterns)) {
                logger.info("Removing ThreadLocal {} with value {} in Thread {}", threadLocal, value, thread);
                final Method removeMethod = threadLocalMap.getClass().getDeclaredMethod("remove", ThreadLocal.class);
                removeMethod.setAccessible(true);
                removeMethod.invoke(threadLocalMap, threadLocal);
            }
        }
    }

    private boolean isBlacklistedValue(final Object value, final List<Pattern> blacklistPatterns) {
        final String valueClassName = value.getClass().getName();
        for (final Pattern blacklistPattern : blacklistPatterns) {
            if (blacklistPattern.matcher(valueClassName).matches()) {
                return true;
            }
        }
        return false;
    }
}