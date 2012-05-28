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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Wraps up a {@link File} as a {@link MultipartFile}, which might be useful for
 * testing or non-web use.
 *
 * @author David McKain
 */
public class MultipartFileWrapper implements MultipartFile {

    private final File file;
    private final String contentType;

    public MultipartFileWrapper(final File file, final String contentType) {
        this.file = file;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getOriginalFilename() {
        return getName();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return !file.exists() || file.length()==0;
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] result;
        if (file.exists()) {
            result = FileUtils.readFileToByteArray(file);
        }
        else {
            result = new byte[0];
        }
        return result;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream result;
        if (file.exists()) {
            result = new FileInputStream(file);
        }
        else {
            result = new ByteArrayInputStream(new byte[0]);
        }
        return result;
    }

    @Override
    public void transferTo(final File dest) throws IOException, IllegalStateException {
        InputStream inputStream = null;
        try {
            inputStream = getInputStream();
            FileUtils.copyInputStreamToFile(getInputStream(), dest);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
