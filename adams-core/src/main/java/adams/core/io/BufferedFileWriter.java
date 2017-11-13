/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * BufferedFileWriter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

/**
 * Combines a {@link BufferedWriter} and a {@link java.io.FileWriter}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BufferedFileWriter
  extends Writer {

  /** the file writer. */
  protected java.io.FileWriter m_FileWriter;

  /** the buffered writer. */
  protected BufferedWriter m_BufferedWriter;

  /**
   * Initializes the writer with the given file.
   *
   * @param file		the file to write to
   * @throws Exception		if instantiation of writer fails
   */
  public BufferedFileWriter(File file) throws Exception {
    this(file.getAbsolutePath());
  }

  /**
   * Initializes the writer with the given file.
   *
   * @param file		the file to write to
   * @throws Exception		if instantiation of writer fails
   */
  public BufferedFileWriter(String file) throws Exception {
    m_FileWriter     = new java.io.FileWriter(new PlaceholderFile(file).getAbsolutePath());
    m_BufferedWriter = new BufferedWriter(m_FileWriter);
  }

  /**
   * Writes a portion of an array of characters.
   *
   * @param  cbuf
   *         Array of characters
   *
   * @param  off
   *         Offset from which to start writing characters
   *
   * @param  len
   *         Number of characters to write
   *
   * @throws  IOException
   *          If an I/O error occurs
   */
  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    m_BufferedWriter.write(cbuf, off, len);
  }

  /**
   * Writes a portion of a string.
   *
   * @param  s
   *         A String
   *
   * @param  off
   *         Offset from which to start writing characters
   *
   * @param  len
   *         Number of characters to write
   *
   * @throws  IndexOutOfBoundsException
   *          If <tt>off</tt> is negative, or <tt>len</tt> is negative,
   *          or <tt>off+len</tt> is negative or greater than the length
   *          of the given string
   *
   * @throws  IOException
   *          If an I/O error occurs
   */
  public void write(String s, int off, int len) throws IOException {
    m_BufferedWriter.write(s, off, len);
  }

  /**
   * Writes a line separator. The line separator string is defined by the
   * system property line.separator, and is not necessarily a single
   * newline ('\n') character.
   *
   * @throws IOException
   */
  public void newLine() throws IOException {
    m_BufferedWriter.newLine();
  }

  /**
   * Flushes the stream.  If the stream has saved any characters from the
   * various write() methods in a buffer, write them immediately to their
   * intended destination.  Then, if that destination is another character or
   * byte stream, flush it.  Thus one flush() invocation will flush all the
   * buffers in a chain of Writers and OutputStreams.
   *
   * @throws  IOException
   *          If an I/O error occurs
   */
  @Override
  public void flush() throws IOException {
    m_BufferedWriter.flush();
    m_FileWriter.flush();
  }

  /**
   * Closes the stream, flushing it first. Once the stream has been closed,
   * further write() or flush() invocations will cause an IOException to be
   * thrown. Closing a previously closed stream has no effect.
   *
   * @throws  IOException
   *          If an I/O error occurs
   */
  @Override
  public void close() throws IOException {
    FileUtils.closeQuietly(m_BufferedWriter);
    FileUtils.closeQuietly(m_FileWriter);
  }
}
