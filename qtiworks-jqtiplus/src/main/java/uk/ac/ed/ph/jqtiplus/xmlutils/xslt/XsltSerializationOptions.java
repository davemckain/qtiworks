/* $Id: SerializationOptions.java 662 2011-01-11 12:35:21Z davemckain $
 *
 * Copyright (c) 2008-2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils.xslt;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import java.io.Serializable;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;

/**
 * Encapsulates options for serializing XML (using XSLT).
 *
 * (Reused and refactored from SnuggleTeX)
 *
 * @author David McKain
 */
public final class XsltSerializationOptions implements Serializable {

    private static final long serialVersionUID = -6028136863012443751L;

    /** Default encoding to use */
    public static final String DEFAULT_ENCODING = "UTF-8";

    /** Default indent to use when {@link #isIndenting()} returns true */
    public static final int DEFAULT_INDENT = 2;

    private XsltSerializationMethod serializationMethod;
    private String encoding;
    private boolean indenting;
    private int indent;
    private boolean includingXMLDeclaration;
    private String doctypePublic;
    private String doctypeSystem;

    public XsltSerializationOptions() {
        this.serializationMethod = XsltSerializationMethod.XML;
        this.encoding = DEFAULT_ENCODING;
        this.indent = DEFAULT_INDENT;
    }

    /**
     * Gets the {@link XsltSerializationMethod} to use when generating the final output.
     * <p>
     * Default is {@link XsltSerializationMethod#XML}.
     * This must not be null.
     * <p>
     * Note that {@link XsltSerializationMethod#XHTML} is only supported properly if you are using
     * an XSLT 2.0 processor; otherwise it reverts to {@link XsltSerializationMethod#XML}
     */
    public XsltSerializationMethod getSerializationMethod() {
        return serializationMethod;
    }

    /**
     * Sets the {@link XsltSerializationMethod} to use when generating the final output.
     * This must not be null.
     * <p>
     * Note that {@link XsltSerializationMethod#XHTML} is only supported properly if you are using
     * an XSLT 2.0 processor; otherwise it reverts to {@link XsltSerializationMethod#XML}
     *
     * @param serializationMethod {@link XsltSerializationMethod} to use, which must not be null.
     */
    public void setSerializationMethod(final XsltSerializationMethod serializationMethod) {
        Assert.notNull(serializationMethod, "serializationMethod");
        this.serializationMethod = serializationMethod;
    }


    /**
     * Gets the encoding for the resulting serialized XML.
     * <p>
     * Default is {@link #DEFAULT_ENCODING}.
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the encoding for the resulting serialized XML.
     * <p>
     * Must not be null.
     *
     * @param encoding encoding to use, which must be non-null and recognised by the XSLT
     *   {@link TransformerFactory} that will end up doing the serialization.
     */
    public void setEncoding(final String encoding) {
        Assert.notNull(encoding, "encoding");
        this.encoding = encoding;
    }


    /**
     * Returns whether the resulting XML will be indented or not.
     * (This depends on how clever the underlying XSLT engine will be!)
     * <p>
     * Default is false.
     */
    public boolean isIndenting() {
        return indenting;
    }

    /**
     * Sets whether the resulting XML will be indented or not.
     * (This depends on how clever the underlying XSLT engine will be!)
     *
     * @param indenting true to indent, false otherwise.
     */
    public void setIndenting(final boolean indenting) {
        this.indenting = indenting;
    }


    /**
     * Returns the indentation level to use when {@link #isIndenting()} returns true.
     * (This is currently only supported if your underlying XSLT process is either Saxon
     * or Xalan. This will be the case by if you have chosen to either use Saxon or use
     * the default processor that ships with your Java platform.)
     * <p>
     * This must be a non-negative integer.
     * The default value is {@link #DEFAULT_INDENT}.
     */
    public int getIndent() {
        return indent;
    }

    /**
     * Sets the indentation level to use when {@link #isIndenting()} returns true.
     * (This is currently only supported if your underlying XSLT process is either Saxon
     * or Xalan. This will be the case by if you have chosen to either use Saxon or use
     * the default processor that ships with your Java platform.)
     * <p>
     * This must be a non-negative integer.
     * The default value is {@link #DEFAULT_INDENT}.
     */
    public void setIndent(final int indent) {
        if (indent<0) {
            throw new IllegalArgumentException("indent must be non-negative");
        }
        this.indent = indent;
    }


    /**
     * Gets whether to include an XML declaration on the resulting output.
     * Default is false.
     */
    public boolean isIncludingXMLDeclaration() {
        return includingXMLDeclaration;
    }

    /**
     * Sets whether to include an XML declaration on the resulting output.
     *
     * @param includingXMLDeclaration true to include an XML declaration, false otherwise.
     */
    public void setIncludingXMLDeclaration(final boolean includingXMLDeclaration) {
        this.includingXMLDeclaration = includingXMLDeclaration;
    }


    /**
     * Gets the public identifier to use in the resulting DOCTYPE declaration,
     * as described in {@link OutputKeys#DOCTYPE_PUBLIC}.
     * <p>
     * Default is null
     */
    public String getDoctypePublic() {
        return doctypePublic;
    }

    /**
     * Sets the public identifier to use in the resulting DOCTYPE declaration,
     * as described in {@link OutputKeys#DOCTYPE_PUBLIC}.
     *
     * @param doctypePublic public identifier to use, null for no identifier.
     */
    public void setDoctypePublic(final String doctypePublic) {
        this.doctypePublic = doctypePublic;
    }


    /**
     * Gets the system identifier to use in the resulting DOCTYPE declaration,
     * as described in {@link OutputKeys#DOCTYPE_SYSTEM}.
     * <p>
     * Default is null
     */
    public String getDoctypeSystem() {
        return doctypeSystem;
    }

    /**
     * Sets the system identifier to use in the resulting DOCTYPE declaration,
     * as described in {@link OutputKeys#DOCTYPE_SYSTEM}.
     *
     * @param doctypeSystem system identifier to use, null for no identifier.
     */
    public void setDoctypeSystem(final String doctypeSystem) {
        this.doctypeSystem = doctypeSystem;
    }


    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}