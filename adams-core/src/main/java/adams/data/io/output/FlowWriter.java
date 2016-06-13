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
 * FlowWriter.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.io.FileFormatHandler;
import adams.core.option.OptionHandler;
import adams.data.io.input.FlowReader;
import adams.flow.core.Actor;
import adams.gui.flow.tree.Node;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Interface for flow writers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface FlowWriter 
  extends OptionHandler, FileFormatHandler {

  /**
   * Writes the given content to the specified file.
   *
   * @param content	the content to write
   * @param file	the file to write to
   * @return		true if successfully written
   */
  public boolean write(Node content, File file);

  /**
   * Writes the content to the given file.
   *
   * @param content	the content to write
   * @param filename	the file to write the content to
   * @return		true if successfully written
   */
  public boolean write(Node content, String filename);

  /**
   * Writes the content to the given output stream. The caller
   * must ensure that the stream gets closed.
   *
   * @param content	the content to write
   * @param stream	the output stream to write the content to
   * @return		true if successfully written
   */
  public boolean write(Node content, OutputStream stream);

  /**
   * Writes the content to the given writer. The caller
   * must ensure that the writer gets closed.
   *
   * @param content	the content to write
   * @param writer	the writer to write the content to
   * @return		true if successfully written
   */
  public boolean write(Node content, Writer writer);

  /**
   * Writes the given content to the specified file.
   *
   * @param content	the content to write
   * @param file	the file to write to
   * @return		true if successfully written
   */
  public boolean write(Actor content, File file);

  /**
   * Writes the content to the given file.
   *
   * @param content	the content to write
   * @param filename	the file to write the content to
   * @return		true if successfully written
   */
  public boolean write(Actor content, String filename);

  /**
   * Writes the content to the given output stream. The caller
   * must ensure that the stream gets closed.
   *
   * @param content	the content to write
   * @param stream	the output stream to write the content to
   * @return		true if successfully written
   */
  public boolean write(Actor content, OutputStream stream);

  /**
   * Writes the content to the given writer. The caller
   * must ensure that the writer gets closed.
   *
   * @param content	the content to write
   * @param writer	the writer to write the content to
   * @return		true if successfully written
   */
  public boolean write(Actor content, Writer writer);

  /**
   * Returns the corresponding reader, if available.
   *
   * @return		the reader, null if none available
   */
  public FlowReader getCorrespondingReader();
}
