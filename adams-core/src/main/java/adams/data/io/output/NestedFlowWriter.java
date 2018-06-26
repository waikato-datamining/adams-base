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
 * NestedFlowWriter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

/**
 * Interface for flow writers that support the faster nested format.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface NestedFlowWriter
  extends FlowWriter {

  /**
   * Writes the given nested format to the specified file.
   *
   * @param content	the content to write
   * @param file	the file to write to
   * @return		true if successfully written
   */
  public boolean write(List content, File file);

  /**
   * Writes the nested format to the given file.
   *
   * @param content	the content to write
   * @param filename	the file to write the content to
   * @return		true if successfully written
   */
  public boolean write(List content, String filename);

  /**
   * Writes the nested format to the given output stream. The caller
   * must ensure that the stream gets closed.
   *
   * @param content	the content to write
   * @param stream	the output stream to write the content to
   * @return		true if successfully written
   */
  public boolean write(List content, OutputStream stream);
}
