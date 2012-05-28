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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;

import org.apache.commons.io.FileUtils;

/**
 * NOTE: Consider using Google Guava instead of this where possible.
 *
 * @author David McKain
 */
public final class IoUtilities {

    /** Buffer size when transferring streams */
    public static final int BUFFER_SIZE = 32 * 1024;

    /** Maximum size of characters we'll read from a text stream before complaining */
    public static final int MAX_TEXT_STREAM_SIZE = 1024 * 1024;

    //----------------------------------------------------------------------------

    /**
     * Simple method to ensure that a given directory exists. If the directory
     * does not exist then it is created, along with all required parents.
     *
     * @throws IOException if creation could not succeed for some reason.
     */
    public static File ensureDirectoryCreated(final File directory) throws IOException {
        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) {
                throw new IOException("Could not create directory " + directory);
            }
        }
        return directory;
    }

    /**
     * Simple method to ensure that a given File exists. If the File
     * does not exist then it is created, along with all required parent
     * directories.
     *
     * @throws IOException if creation could not succeed for some reason.
     */
    public static File ensureFileCreated(final File file) throws IOException {
        /* Make sure parent exists */
        final File parentDirectory = file.getParentFile();
        if (parentDirectory!=null) {
            ensureDirectoryCreated(parentDirectory);
        }
        /* Now create file */
        if (!file.isFile()) {
            if (!file.createNewFile()) {
                throw new IOException("Could not create file " + file);
            }
        }
        return file;
    }

    //----------------------------------------------------------------------------

    /**
     * Ensures that as many of the given {@link Closeable}s are closed. If any fail to close, the
     * first Exception is thrown after an attempt has been made to close the rest.
     * <p>
     * For convenience, any number of the stream may be null, in which case they will be ignored.
     *
     * @param streams "streams" to close
     * @throws IOException
     */
    public static void ensureClose(final Closeable... streams) throws IOException {
        IOException firstException = null;
        for (final Closeable stream : streams) {
            if (stream!=null) {
                try {
                    stream.close();
                }
                catch (final IOException e) {
                    firstException = e;
                }
            }
        }
        if (firstException!=null) {
            throw firstException;
        }
    }

    //----------------------------------------------------------------------------
    // Convenience methods for data transfers

    /**
     * "Transfers" data from the given InputStream to the given OutputStream,
     * closing both streams once the InputStream has been exhausted.
     * <p>
     * This will check to see if both streams are File streams and, if so, use the
     * {@link #transfer(FileInputStream, FileOutputStream)} version of this method instead.
     *
     * @param inStream
     * @param outStream
     * @throws IOException
     */
    public static void transfer(final InputStream inStream, final OutputStream outStream) throws IOException {
        if (inStream instanceof FileInputStream && outStream instanceof FileOutputStream) {
            transfer((FileInputStream) inStream, (FileOutputStream) outStream);
        }
        else {
            transfer(inStream, outStream, true);
        }
    }

    /**
     * Version of {@link #transfer(InputStream, OutputStream)} for File streams that uses
     * NIO to do a hopefully more efficient transfer.
     */
    public static void transfer(final FileInputStream fileInStream, final FileOutputStream fileOutStream)
            throws IOException {
        final FileChannel fileInChannel = fileInStream.getChannel();
        final FileChannel fileOutChannel = fileOutStream.getChannel();
        final long fileInSize = fileInChannel.size();
        try {
            final long transferred = fileInChannel.transferTo(0, fileInSize, fileOutChannel);
            if (transferred!=fileInSize) {
                /* Hmmm... need to rethink this algorithm if something goes wrong */
                throw new IOException("transfer() did not complete");
            }
        }
        finally {
            ensureClose(fileInChannel, fileOutChannel);
        }
    }

    /**
     * "Transfers" data from the given InputStream to the given OutputStream,
     * closing the InputStream afterwards. If the parameter closeOutputStream is true
     * then the OutputStream is closed too. If not, it will be flushed.
     *
     * @param inStream
     * @param outStream
     * @throws IOException
     */
    public static void transfer(final InputStream inStream, final OutputStream outStream, final boolean closeOutputStream)
            throws IOException {
        transfer(inStream, outStream, true, closeOutputStream);
    }


    /**
     * "Transfers" data from the given InputStream to the given OutputStream,
     * optionally closing the given input and output streams afterwards.
     * <p>
     * Even if not closing the output stream, it will still be flushed.
     *
     * @param inStream
     * @param outStream
     * @throws IOException
     */
    public static void transfer(final InputStream inStream, final OutputStream outStream,
            final boolean closeInputStream, final boolean closeOutputStream) throws IOException {
        final byte [] buffer = new byte[BUFFER_SIZE];
        int count;
        try {
            while ((count = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, count);
            }
        }
        finally {
            if (closeInputStream) {
                inStream.close();
            }
            if (closeOutputStream) {
                outStream.close();
            }
            else {
                outStream.flush();
            }
        }
    }

    //----------------------------------------------------------------------------
    // Reading methods

    /**
     * @see FileUtils#readFileToByteArray(File)
     */
    @Deprecated
    public static byte[] readBinaryStream(final InputStream stream) throws IOException {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        transfer(stream, outStream);
        return outStream.toByteArray();
    }

    /**
     * Reads all character data from the given Reader, returning a String
     * containing all of the data. The Reader will be buffered for efficiency and
     * will be closed once finished with.
     * Be careful reading in very large files - we will barf if MAX_FILE_SIZE is
     * passed as a safety precaution.
     *
     * @param reader source of string data
     * @return String representing the data read
     * @throws IOException
     */
    public static String readCharacterStream(final Reader reader) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        int size = 0;
        final StringBuilder result = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            size += line.length() + 1;
            if (size > MAX_TEXT_STREAM_SIZE) {
                throw new IOException("String data exceeds current maximum safe size ("
                        + MAX_TEXT_STREAM_SIZE + ")");
            }
            result.append(line).append("\n");
        }
        bufferedReader.close();
        return result.toString();
    }

    /**
     * Same as {@link #readCharacterStream(Reader)} but assumes the
     * stream is encoded as UTF-8.
     *
     * @param in InputStream supplying character data
     * @return String representing the data read in
     * @throws IOException
     */
    public static String readUnicodeStream(final InputStream in) throws IOException {
        return readCharacterStream(new InputStreamReader(in, "UTF-8"));
    }

    /**
     * Same as {@link #readUnicodeStream(InputStream)} but accepts a plain
     * File object for convenience
     *
     * @param file File to read from
     * @return String representing the data we read in
     * @throws IOException
     */
    @Deprecated
    public static String readUnicodeFile(final File file) throws IOException {
        final InputStream inStream = new FileInputStream(file);
        try {
            return readUnicodeStream(inStream);
        }
        finally {
            inStream.close();
        }
    }

    //----------------------------------------------------------------------------
    // Output methods

    /**
     * Writes the given String data to the given output file, encoded as
     * UTF-8.
     *
     * @param outputFile File to save to (overwriting any existing content)
     * @param data String data to store
     *
     * @throws IOException if the usual bad things happen
     */
    @Deprecated
    public static void writeUnicodeFile(final File outputFile, final String data) throws IOException {
        writeFile(outputFile, data, "UTF-8");
    }

    /**
     * Writes the given String data to the given output file, encoded using
     * the given encoding.
     *
     * @param outputFile File to save to (overwriting any existing content)
     * @param data String data to store
     *
     * @throws IOException if the usual bad things happen
     * @throws UnsupportedEncodingException if the given encoding
     *   is not supported.
     */
    @Deprecated
    public static void writeFile(final File outputFile, final String data, final String encoding) throws IOException {
        final FileOutputStream outStream = new FileOutputStream(outputFile);
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(outStream, encoding);
            writer.write(data);
        }
        finally {
            if (writer!=null) {
                writer.close();
            }
            else {
                outStream.close();
            }
        }
    }

    //--------------------------------------------------------

    /**
     * Recursively deletes the contents of the given directory (and
     * possibly the directory itself).
     *
     * @param root directory (or file) whose contents will be deleted
     * @param deleteRoot true deletes root directory, false deletes only
     *  its contents.
     *
     * @throws IOException if something goes wrong, which may leave things
     *   in an inconsistent state.
     */
    public static void recursivelyDelete(final File root, final boolean deleteRoot) throws IOException {
        if (root.isDirectory()) {
            final File [] contents = root.listFiles();
            for (final File child : contents) {
                recursivelyDelete(child, true);
            }
        }
        if (deleteRoot) {
            if (!root.delete()) {
                throw new IOException("Could not delete directory " + root);
            }
        }
    }

    /**
     * Convenience version of {@link #recursivelyDelete(File, boolean)} that
     * deletes the given root directory as well.
     */
    public static void recursivelyDelete(final File root) throws IOException {
        recursivelyDelete(root, true);
    }
}
