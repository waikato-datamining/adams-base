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
 * NestedFlowReader.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Interface for flow readers that support the faster nested format.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface NestedFlowReader
  extends FlowReader {

  /**
   * Reads the flow in nested format from the specified file.
   *
   * @param file	the file to read from
   * @return		null in case of an error, otherwise the flow
   */
  public List readNested(File file);

  /**
   * Reads the flow in nested format from the given file.
   *
   * @param filename	the file to read from
   * @return		the flow or null in case of an error
   */
  public List readNested(String filename);

  /**
   * Reads the flow in nested format from the stream. The caller must ensure to
   * close the stream.
   *
   * @param stream	the stream to read from
   * @return		the flow or null in case of an error
   */
  public List readNested(InputStream stream);
}
