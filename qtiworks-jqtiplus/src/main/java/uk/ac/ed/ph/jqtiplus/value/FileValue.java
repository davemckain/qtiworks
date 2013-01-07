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
package uk.ac.ed.ph.jqtiplus.value;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.types.FileResponseData;

import java.io.File;

/**
 * Implementation of <code>BaseType</code> file value.
 * <p>
 * This is a completely different implementation from JQTI, which stored files as a big String!
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author David McKain
 */
public final class FileValue extends SingleValue {

    private static final long serialVersionUID = 7627842431496721671L;

    private File file;
    private String contentType;
    private String fileName;

    public FileValue(final File file, final String contentType, final String fileName) {
        Assert.notNull(file);
        Assert.notNull(contentType);
        Assert.notNull(fileName);
        this.file = file;
        this.contentType = contentType;
        this.fileName = fileName;
    }

    public FileValue(final FileResponseData fileResponseData) {
        this.file = fileResponseData.getFile();
        this.contentType = fileResponseData.getContentType();
        this.fileName = fileResponseData.getFileName();
    }

    @Override
    public BaseType getBaseType() {
        return BaseType.FILE;
    }

    public File getFile() {
        return file;
    }

    public void setFile(final File file) {
        this.file = file;
    }

    public String getContentType() {
        return contentType;
    }


    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public FileResponseData toFileResponseData() {
        return new FileResponseData(file, contentType, fileName);
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof FileValue)) {
            return false;
        }
        final FileValue other = (FileValue) object;
        return file.equals(other.file)
                && contentType.equals(other.contentType)
                && fileName.equals(other.fileName);
    }

    @Override
    public int hashCode() {
        return (file.getAbsolutePath() + " " + contentType + " " + fileName).hashCode();
    }

    @Override
    public String toQtiString() {
        return file.getPath();
    }
}
