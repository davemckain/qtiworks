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
 * Default POJO implementation of {@link LSInput}.
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
    
    public String getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    
    public InputStream getByteStream() {
        return byteStream;
    }

    public void setByteStream(InputStream byteStream) {
        this.byteStream = byteStream;
    }

    
    public boolean getCertifiedText() {
        return certifiedText;
    }

    public void setCertifiedText(boolean certifiedText) {
        this.certifiedText = certifiedText;
    }
    

    public Reader getCharacterStream() {
        return characterStream;
    }

    public void setCharacterStream(Reader characterStream) {
        this.characterStream = characterStream;
    }

    
    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    
    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    
    public String getStringData() {
        return stringData;
    }

    public void setStringData(String stringData) {
        this.stringData = stringData;
    }

    
    public String getSystemId() {
        return systemId;
    }

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
