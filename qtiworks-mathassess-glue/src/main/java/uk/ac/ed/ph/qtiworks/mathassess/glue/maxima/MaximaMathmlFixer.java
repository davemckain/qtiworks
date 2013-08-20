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
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.qtiworks.mathassess.glue.maxima;

import uk.ac.ed.ph.qtiworks.mathassess.glue.MathAssessCasException;

import uk.ac.ed.ph.snuggletex.SnuggleRuntimeException;
import uk.ac.ed.ph.snuggletex.internal.util.XMLUtilities;
import uk.ac.ed.ph.snuggletex.utilities.MathMLUtilities;
import uk.ac.ed.ph.snuggletex.utilities.SaxonTransformerFactoryChooser;
import uk.ac.ed.ph.snuggletex.utilities.SimpleStylesheetCache;
import uk.ac.ed.ph.snuggletex.utilities.StylesheetCache;
import uk.ac.ed.ph.snuggletex.utilities.StylesheetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Fixes up the raw MathML produced by Maxima output into a form roughly equivalent to the
 * Presentation MathML produced by SnuggleTeX.
 *
 * (This is a blatant rip-off of the code that does the same for ASCIIMathML output in SnuggleTeX!)
 *
 * FIXME: The Lisp module that generates the raw MathML is buggy and sometimes produces XML that
 * is not well formed. (For example, raw angle brackets in text.) In these cases, the approach taken
 * here is obviously not going to work. Ideally, I need to fix or write a more appropriate version
 * of the <tt>mathml.lisp</tt> module.
 *
 * @author David McKain
 */
final class MaximaMathmlFixer {

    private static final Logger logger = LoggerFactory.getLogger(MaximaMathmlFixer.class);

    /** Package to load various resources from */
    private static final String RESOURCE_PACKAGE = "uk/ac/ed/ph/qtiworks/mathassess/glue/maxima";

    /** "Base" location for the XSLT stylesheets used here */
    private static final String BASE_XSLT_URI = "classpath:/" + RESOURCE_PACKAGE;

    /** Location of the initial XSLT for fixing up ASCIIMathML */
    private static final String FIXER_XSLT_URI = BASE_XSLT_URI + "/maxima-output-fixer.xsl";

    /** Location of the resource providing {@link #entitySubstitutionProperties} */
    private static final String ENTITY_SUBSTITUTION_PROPERTIES_RESOURCE = RESOURCE_PACKAGE + "/maxima-entity-substitutions.properties";

    /** XSLT stylesheet manager (from SnuggleTeX) */
    private final StylesheetManager stylesheetManager;

    /**
     * Properties mapping XML character entity names to substitutions.
     * (I'm doing it this way as that mathml.lisp module doesn't output a lot of entities
     * so it's not worth pulling in the various entity files that form part of the MathML DTD.)
     */
    private final Properties entitySubstitutionProperties;

    /**
     * Creates a new up-converter using a simple internal cache.
     * <p>
     * Use this constructor if you don't use XSLT yourself. In this case, you'll want your
     * instance of this class to be reused as much as possible to get the benefits of caching.
     */
    public MaximaMathmlFixer() {
        this(new SimpleStylesheetCache());
    }

    /**
     * Creates a new up-converter using the given {@link StylesheetCache} to cache internal XSLT
     * stylesheets.
     * <p>
     * Use this constructor if you do your own XSLT caching that you want to integrate in, or
     * if the default doesn't do what you want.
     */
    public MaximaMathmlFixer(final StylesheetCache stylesheetCache) {
        this.stylesheetManager = new StylesheetManager(SaxonTransformerFactoryChooser.getInstance(), stylesheetCache);
        this.entitySubstitutionProperties = loadEntitySubstitutionProperties();
    }

    private Properties loadEntitySubstitutionProperties() {
        final Properties result = new Properties();
        final InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(ENTITY_SUBSTITUTION_PROPERTIES_RESOURCE);
        if (resourceStream==null) {
            throw new MathAssessCasException("Could not locate entity substitutions resource within ClassPath at "
                    + ENTITY_SUBSTITUTION_PROPERTIES_RESOURCE);
        }
        try {
            result.load(resourceStream);
        }
        catch (final IOException e) {
            throw new MathAssessCasException("Could not read in entity substitutions resource at "
                    + ENTITY_SUBSTITUTION_PROPERTIES_RESOURCE);
        }
        return result;
    }

    private Document fixMaximaMathmlOutput(final Document document) {
        final Document resultDocument = XMLUtilities.createNSAwareDocumentBuilder().newDocument();
        try {
            /* Create required XSLT */
            final Templates upconverterStylesheet = stylesheetManager.getCompiledStylesheet(FIXER_XSLT_URI);
            final Transformer upconverter = upconverterStylesheet.newTransformer();

            /* Do the transform */
            upconverter.transform(new DOMSource(document), new DOMResult(resultDocument));
        }
        catch (final TransformerException e) {
            throw new SnuggleRuntimeException("Fixing failed", e);
        }
        return resultDocument;
    }

    public Document fixMaximaMathmlOutput(final String maximaMathmlOutput) {
        /* The Maxima MathML module outputs certain characters as XML entities (e.g. &pi;),
         * while some are left in the same Maxima format (e.g. %alpha).
         * We'll replace these now */
        final Pattern searchPattern = Pattern.compile("(&\\w+;|%\\w+)");
        final Matcher searchMatcher = searchPattern.matcher(maximaMathmlOutput);
        final StringBuffer replacementBuilder = new StringBuffer();
        String entityName, entityReplacement;
        while (searchMatcher.find()) {
            entityName = searchMatcher.group();
            entityReplacement = entitySubstitutionProperties.getProperty(entityName);
            if (entityReplacement==null) {
                throw new MathAssessCasException("No substitution specified for symbol " + entityName);
            }
            searchMatcher.appendReplacement(replacementBuilder, entityReplacement);
        }
        searchMatcher.appendTail(replacementBuilder);

        /* Now parse the MathML */
        final String entityFreeMathmlString = replacementBuilder.toString();
        Document mathmlDocument;
        try {
            mathmlDocument = MathMLUtilities.parseMathMLDocumentString(entityFreeMathmlString);
        }
        catch (final Exception e) {
            logger.debug("Could not parse Maxima MathML output; raw output was {}; entity fixed was {}", maximaMathmlOutput, entityFreeMathmlString);
            throw new MathAssessCasException("Failed to parse raw MathML output from Maxima", e);
        }

        /* The fix up */
        return fixMaximaMathmlOutput(mathmlDocument);
    }
}
