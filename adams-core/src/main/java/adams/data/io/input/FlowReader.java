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
 * FlowReader.java
 * Copyright (C) 2013-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.MessageCollection;
import adams.core.io.FileFormatHandler;
import adams.core.option.OptionHandler;
import adams.data.io.output.FlowWriter;
import adams.flow.core.Actor;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

/**
 * Interface for flow readers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface FlowReader
  extends OptionHandler, FileFormatHandler {

  /**
   * Reads the flow from the specified file.
   *
   * @param file	the file to read from
   * @return		null in case of an error, otherwise the flow
   */
  public Actor readActor(File file);

  /**
   * Reads the flow from the given file.
   *
   * @param filename	the file to read from
   * @return		the flow or null in case of an error
   */
  public Actor readActor(String filename);

  /**
   * Reads the flow from the stream. The caller must ensure to
   * close the stream.
   *
   * @param stream	the stream to read from
   * @return		the flow or null in case of an error
   */
  public Actor readActor(InputStream stream);

  /**
   * Reads the flow from the given reader. The caller must ensure to
   * close the reader.
   *
   * @param r		the reader to read from
   * @return		the flow or null in case of an error
   */
  public Actor readActor(Reader r);
  
  /**
   * Returns any warnings that were encountered while reading.
   * 
   * @return		the warnings
   */
  public MessageCollection getWarnings();
  
  /**
   * Returns any errors that were encountered while reading.
   * 
   * @return		the errors
   */
  public MessageCollection getErrors();

  /**
   * Returns the corresponding writer, if available.
   *
   * @return		the writer, null if none available
   */
  public FlowWriter getCorrespondingWriter();
}
