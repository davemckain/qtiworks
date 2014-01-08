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
package uk.ac.ed.ph.jqtiplus.types;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.Pair;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper class to bind certain QTI data types to and from Strings.
 *
 * @author David McKain
 */
public final class DataTypeBinder {

    public static boolean parseBoolean(final String string) {
        Assert.notNull(string);

        if (string.equals("true") || string.equals("1")) {
            return true;
        }
        else if (string.equals("false") || string.equals("0")) {
            return false;
        }
        else {
            throw new QtiParseException("Invalid boolean '" + string + "'");
        }
    }

    public static String toString(final boolean value) {
        return value ? "true" : "false";
    }

    //--------------------------------------------

    public static Date parseDate(final String string) {
        Assert.notNull(string);
        try {
            return createDateFormat().parse(string);
        }
        catch (final ParseException ex) {
            throw new QtiParseException("Invalid date '" + string + "'");
        }
    }

    public static String toString(final Date date) {
        return createDateFormat().format(date);
    }

    /**
     * Date formatting pattern.
     * NB: DateFormat is not Thread safe, so don't cache the results of this across Threads!
     */
    private static final DateFormat createDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    }

    //--------------------------------------------

    public static int parseInteger(final String string) {
        return parseInteger(string, 10);
    }

    public static int parseInteger(final String string, final int radix) {
        Assert.notNull(string);

        // Removes + sign because of Integer.parseInt cannot handle it.
        String s = string;
        if (string.startsWith("+")) {
            s  = string.substring(1);
            if (s.length() == 0 || !Character.isDigit(s.codePointAt(0))) {
                throw new QtiParseException("Invalid integer '" + string + "'");
            }
        }

        try {
            return Integer.parseInt(s, radix);
        }
        catch (final NumberFormatException e) {
            throw new QtiParseException("Invalid integer '" + s + "'", e);
        }
    }

    public static String toString(final int value) {
        return Integer.toString(value);
    }

    //--------------------------------------------

    public static long parseLong(final String string) {
        Assert.notNull(string);

        String s = string;
        if (string.startsWith("+")) {
            s  = string.substring(1);
            if (s.length() == 0 || !Character.isDigit(s.codePointAt(0))) {
                throw new QtiParseException("Invalid integer '" + string + "'");
            }
        }
        try {
            return Long.parseLong(s);
        }
        catch (final NumberFormatException e) {
            throw new QtiParseException("Invalid long '" + s + "'", e);
        }
    }

    public static String toString(final long value) {
        return Long.toString(value);
    }

    //--------------------------------------------

    public static double parseFloat(final String string) {
        Assert.notNull(string);

        double result;
        if (string.equals("INF")) {
            result = Double.POSITIVE_INFINITY;
        }
        else if (string.equals("-INF")) {
            result = Double.NEGATIVE_INFINITY;
        }
        else if (string.equals("NaN")) {
            result = Double.NaN;
        }
        else {
            try {
                result = Double.parseDouble(string);
            }
            catch (final NumberFormatException e) {
                throw new QtiParseException("Invalid float '" + string + "'", e);
            }
        }
        return result;
    }

    public static String toString(final double value) {
        return Double.toString(value);
    }

    //--------------------------------------------

    public static Pair<Identifier, Identifier> parsePair(final String string) {
        Assert.notNull(string);

        final String[] parts = string.split(" ", 3);
        if (parts.length != 2) {
            throw new QtiParseException("Invalid pair '" + string + "': Number of parts is not valid");
        }

        try {
            final Identifier first = Identifier.parseString(parts[0]);
            final Identifier second = Identifier.parseString(parts[1]);
            return new Pair<Identifier, Identifier>(first, second);
        }
        catch (final QtiParseException e) {
            throw new QtiParseException("Invalid pair '" + string + "'", e);
        }
    }

    public static String toString(final Identifier first, final Identifier second) {
        return first.toString() + " " + second.toString();
    }

    //--------------------------------------------

    public static int[] parsePoint(final String string) {
        Assert.notNull(string);

        final String[] parts = string.split(" ", 3);
        if (parts.length != 2) {
            throw new QtiParseException("Invalid point '" + string + "': Number of parts is not valid");
        }

        try {
            final int horizontalValue = parseInteger(parts[0]);
            final int verticalValue = parseInteger(parts[1]);
            return new int[] { horizontalValue, verticalValue };
        }
        catch (final QtiParseException ex) {
            throw new QtiParseException("Invalid point '" + string + "'", ex);
        }
    }

    public static String toString(final int horizontalValue, final int verticalValue) {
        return horizontalValue + " " + verticalValue;
    }

    //--------------------------------------------

    public static URI parseUri(final String string) {
        Assert.notNull(string);

        if (string.isEmpty()) {
            throw new QtiParseException("uri must not be empty");
        }
        try {
            return new URI(string);
        }
        catch (final URISyntaxException e) {
            throw new QtiParseException("Invalid uri '" + string + "'", e);
        }
    }

    public static String toString(final URI uri) {
        return uri.toString();
    }

}
