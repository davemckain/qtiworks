/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils;

import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.ls.LSInput;

/**
 * Default implementation of {@link LSInput}.
 * 
 * @author  David McKain
 * @version $Revision: 2712 $
 */
public class LSInputImpl implements LSInput {
    
    private String baseURI;
    private InputStream byteStream;
    private boolean certifiedText;
    private Reader characterStream;
    private String encoding;
    private String publicId;
    private String stringData;
    private String systemId;
    
    /**
     * @return the baseURI
     */
    public String getBaseURI() {
        return baseURI;
    }
    /**
     * @param baseURI the baseURI to set
     */
    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }
    /**
     * @return the byteStream
     */
    public InputStream getByteStream() {
        return byteStream;
    }
    /**
     * @param byteStream the byteStream to set
     */
    public void setByteStream(InputStream byteStream) {
        this.byteStream = byteStream;
    }
    /**
     * @return the certifiedText
     */
    public boolean getCertifiedText() {
        return certifiedText;
    }
    /**
     * @param certifiedText the certifiedText to set
     */
    public void setCertifiedText(boolean certifiedText) {
        this.certifiedText = certifiedText;
    }
    
    /**
     * @return the characterStream
     */
    public Reader getCharacterStream() {
        return characterStream;
    }
    /**
     * @param characterStream the characterStream to set
     */
    public void setCharacterStream(Reader characterStream) {
        this.characterStream = characterStream;
    }
    /**
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }
    /**
     * @param encoding the encoding to set
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    /**
     * @return the publicId
     */
    public String getPublicId() {
        return publicId;
    }
    /**
     * @param publicId the publicId to set
     */
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }
    /**
     * @return the stringData
     */
    public String getStringData() {
        return stringData;
    }
    /**
     * @param stringData the stringData to set
     */
    public void setStringData(String stringData) {
        this.stringData = stringData;
    }
    /**
     * @return the systemId
     */
    public String getSystemId() {
        return systemId;
    }
    /**
     * @param systemId the systemId to set
     */
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName()
            + "(baseURI=" + baseURI
            + ",publicId=" + publicId
            + ",systemId=" + systemId
            + ",encoding=" + encoding
            + ",certifiedText=" + certifiedText
            + ",byteStream=" + byteStream
            + ",characterStream=" + characterStream
            + ",stringData=" + stringData
            + ")";
    }
}
