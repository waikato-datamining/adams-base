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

/**
 * AbstractFlowWriter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.io.output.WriterOutputStream;

import adams.core.ClassLister;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;

/**
 * Ancestor for classes that can write flow objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFlowWriter
  extends AbstractOptionHandler
  implements FlowWriter {

  /** for serialization. */
  private static final long serialVersionUID = -3547064795252689769L;

  /**
   * How to read the data.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum OutputType {
    /** write to a file. */
    FILE,
    /** write using a writer. */
    WRITER,
    /** write to a stream. */
    STREAM
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  public abstract String getFormatDescription();

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  public abstract String[] getFormatExtensions();

  /**
   * Returns the default extension of the format.
   *
   * @return 			the default extension (without the dot!)
   */
  public String getDefaultFormatExtension() {
    return getFormatExtensions()[0];
  }

  /**
   * Returns how to write the data, from a file, stream or writer.
   *
   * @return		how to write the data
   */
  protected abstract OutputType getOutputType();

  /**
   * Writes the given content to the specified file.
   *
   * @param content	the content to write
   * @param file	the file to write to
   * @return		true if successfully written
   */
  public boolean write(Actor content, File file) {
    return write(content, file.getAbsolutePath());
  }

  /**
   * Writes the content to the given file.
   *
   * @param content	the content to write
   * @param filename	the file to write the content to
   * @return		true if successfully written
   */
  public boolean write(Actor content, String filename) {
    boolean			result;
    BufferedWriter		writer;
    OutputStream		output;

    result = true;
    
    try {
      switch (getOutputType()) {
	case FILE:
	  result = doWrite(content, new File(filename));
	  break;
	case WRITER:
	  writer = new BufferedWriter(new FileWriter(filename));
	  result = doWrite(content, writer);
	  writer.close();
	  break;
	case STREAM:
	  output = new FileOutputStream(filename);
	  result = doWrite(content, output);
	  output.close();
	  break;
	default:
	  throw new IllegalStateException("Unhandled output type: " + getOutputType());
      }
    }
    catch (Exception e) {
      result = false;
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Writes the content to the given output stream. The caller
   * must ensure that the stream gets closed.
   *
   * @param content	the content to write
   * @param stream	the output stream to write the content to
   * @return		true if successfully written
   */
  public boolean write(Actor content, OutputStream stream) {
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
   * Writes the content to the given writer. The caller
   * must ensure that the writer gets closed.
   *
   * @param content	the content to write
   * @param writer	the writer to write the content to
   * @return		true if successfully written
   */
  public boolean write(Actor content, Writer writer) {
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
   * <p/>
   * Default implementation returns always false.
   *
   * @param content	the content to write
   * @param file	the file to write the content to
   * @return		true if successfully written
   */
  protected boolean doWrite(Actor content, File file) {
    return false;
  }

  /**
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   * <p/>
   * Default implementation returns always false.
   *
   * @param content	the content to write
   * @param writer	the writer to write the content to
   * @return		true if successfully written
   */
  protected boolean doWrite(Actor content, Writer writer) {
    return false;
  }

  /**
   * Performs the actual writing. The caller must ensure that the output stream
   * gets closed.
   * <p/>
   * Default implementation returns always false.
   *
   * @param content	the content to write
   * @param out		the output stream to write the content to
   * @return		true if successfully written
   */
  protected boolean doWrite(Actor content, OutputStream out) {
    return false;
  }

  /**
   * Returns a list with classnames of writers.
   *
   * @return		the writer classnames
   */
  public static String[] getWriters() {
    return ClassLister.getSingleton().getClassnames(FlowWriter.class);
  }
}
