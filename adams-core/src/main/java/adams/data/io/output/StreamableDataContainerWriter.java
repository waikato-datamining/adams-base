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
 * StreamableDataContainerWriter.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import adams.data.container.DataContainer;

import java.io.OutputStream;
import java.util.List;

/**
 * Interface for {@link DataContainerWriter} classes that can write to {@link OutputStream}s.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface StreamableDataContainerWriter<T extends DataContainer>
  extends DataContainerWriter<T> {

  /**
   * Performs checks and writes the data to the stream.
   *
   * @param stream 	the stream to write to
   * @param data	the data to write
   * @return		true if successfully written
   * @see		#write(OutputStream stream, List)
   */
  public boolean write(OutputStream stream, T data);

  /**
   * Performs checks and writes the data to the stream.
   *
   * @param stream 	the stream to write to
   * @param data	the data to write
   * @return		true if successfully written
   */
  public boolean write(OutputStream stream, List<T> data);

}
