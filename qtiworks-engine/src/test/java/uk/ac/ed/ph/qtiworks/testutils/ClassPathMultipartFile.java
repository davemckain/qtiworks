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
package uk.ac.ed.ph.qtiworks.testutils;

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Trivial implementation of {@link MultipartFile} that loads a single resource using the
 * {@link ClassLoader}. This is not efficient or clever, but useful for testing.
 *
 * @author David McKain
 */
public final class ClassPathMultipartFile implements MultipartFile {

    private final String path;
    private final String contentType;

    public ClassPathMultipartFile(final String path, final String contentType) {
        Assert.notNull(path, "path");
        this.path = path;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return path.replaceFirst(".+/", "");
    }

    @Override
    public String getOriginalFilename() {
        return path;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return getSize()==0;
    }

    @Override
    public long getSize() {
        return getBytes().length;
    }

    @Override
    public byte[] getBytes() {
        final InputStream inputStream = getInputStream();
        try {
            return IOUtils.toByteArray(getInputStream());
        }
        catch (final IOException e) {
            throw new QtiWorksRuntimeException("Failed to read data from " + path);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    public InputStream getInputStream() {
        final InputStream result = getClass().getClassLoader().getResourceAsStream(path);
        if (result==null) {
            throw new QtiWorksRuntimeException("Failed to locate resource at path " + path
                    + " using ClassLoader " + getClass().getClassLoader());
        }
        return result;
    }

    @Override
    public void transferTo(final File dest) {
        final InputStream inputStream = getInputStream();
        try {
            FileUtils.copyInputStreamToFile(inputStream, dest);
        }
        catch (final IOException e) {
            throw new QtiWorksRuntimeException("Failed to transfer file " + path + " to " + dest, e);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

}