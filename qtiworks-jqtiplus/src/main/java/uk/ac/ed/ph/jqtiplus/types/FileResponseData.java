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
package uk.ac.ed.ph.jqtiplus.types;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import java.io.File;
import java.util.Arrays;

/**
 * Encapsulates file-based response data
 *
 * @author David McKain
 */
public final class FileResponseData implements ResponseData {

    private static final long serialVersionUID = -7780168487104250697L;

    private final File file;
    private final String contentType;
    private final String fileName;

    public FileResponseData(final File file, final String contentType, final String fileName) {
        Assert.notNull(file, "file");
        Assert.notNull(contentType, "contentType");
        Assert.notNull(fileName, "fileName");
        this.file = file;
        this.contentType = contentType;
        this.fileName = fileName;
    }

    @Override
    public ResponseDataType getType() {
        return ResponseDataType.FILE;
    }

    public File getFile() {
        return file;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(file=" + file
                + ",contentType=" + contentType
                + ",fileName=" + fileName
                + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof FileResponseData)) {
            return false;
        }
        final FileResponseData other = (FileResponseData) obj;
        return file.equals(other.file)
                && contentType.equals(other.contentType)
                && fileName.equals(other.fileName);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                file,
                contentType,
                fileName
        });
    }
}
