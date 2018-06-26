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
 * AbstractListFlowWriter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.io.FileUtils;
import org.apache.commons.io.output.WriterOutputStream;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

/**
 * Ancestor for flow writers that support the nested format.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractNestedFlowWriter
  extends AbstractFlowWriter
  implements NestedFlowWriter {

  private static final long serialVersionUID = -5346130766533981323L;

  /**
   * Writes the given nested format to the specified file.
   *
   * @param content	the content to write
   * @param file	the file to write to
   * @return		true if successfully written
   */
  public boolean write(List content, File file) {
    return write(content, file.getAbsolutePath());
  }

  /**
   * Writes the nested format to the given file.
   *
   * @param content	the content to write
   * @param filename	the file to write the content to
   * @return		true if successfully written
   */
  public boolean write(List content, String filename) {
    boolean			result;
    BufferedWriter writer;
    OutputStream output;

    result = true;

    writer = null;
    output = null;
    try {
      switch (getOutputType()) {
	case FILE:
	  result = doWrite(content, new File(filename));
	  break;
	case WRITER:
	  writer = new BufferedWriter(new FileWriter(filename));
	  result = doWrite(content, writer);
	  break;
	case STREAM:
	  output = new FileOutputStream(filename);
	  result = doWrite(content, output);
	  break;
	default:
	  throw new IllegalStateException("Unhandled output type: " + getOutputType());
      }
    }
    catch (Exception e) {
      result = false;
      e.printStackTrace();
    }
    finally {
      FileUtils.closeQuietly(writer);
      FileUtils.closeQuietly(output);
    }

    return result;
  }

  /**
   * Writes the nested format to the given output stream. The caller
   * must ensure that the stream gets closed.
   *
   * @param content	the content to write
   * @param stream	the output stream to write the content to
   * @return		true if successfully written
   */
  public boolean write(List content, OutputStream stream) {
    switch (getOutputType()) {
      case FILE:
	throw new IllegalStateException("Only supports writing to files, not output streams!");
      case STREAM:
	return doWrite(content, stream);
      case WRITER:
	return doWrite(content, new OutputStreamWriter(stream));
      default:
	throw new IllegalStateException("Unhandled output type: " + getOutputType());
    }
  }

  /**
   * Writes the nested format to the given writer. The caller
   * must ensure that the writer gets closed.
   *
   * @param content	the content to write
   * @param writer	the writer to write the content to
   * @return		true if successfully written
   */
  public boolean write(List content, Writer writer) {
    switch (getOutputType()) {
      case FILE:
	throw new IllegalStateException("Only supports writing to files, not output streams!");
      case STREAM:
	return doWrite(content, new WriterOutputStream(writer));
      case WRITER:
	return doWrite(content, writer);
      default:
	throw new IllegalStateException("Unhandled output type: " + getOutputType());
    }
  }

  /**
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   * <br><br>
   * Default implementation returns always false.
   *
   * @param content	the content to write
   * @param file	the file to write the content to
   * @return		true if successfully written
   */
  protected boolean doWrite(List content, File file) {
    return false;
  }

  /**
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   * <br><br>
   * Default implementation returns always false.
   *
   * @param content	the content to write
   * @param writer	the writer to write the content to
   * @return		true if successfully written
   */
  protected boolean doWrite(List content, Writer writer) {
    return false;
  }

  /**
   * Performs the actual writing. The caller must ensure that the output stream
   * gets closed.
   * <br><br>
   * Default implementation returns always false.
   *
   * @param content	the content to write
   * @param out		the output stream to write the content to
   * @return		true if successfully written
   */
  protected boolean doWrite(List content, OutputStream out) {
    return false;
  }
}
