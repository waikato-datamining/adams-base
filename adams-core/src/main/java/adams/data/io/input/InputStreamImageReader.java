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
 * InputStreamImageReader.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.data.image.AbstractImageContainer;

import java.io.InputStream;

/**
 * Interface for image readers that support reading from input streams.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface InputStreamImageReader<T extends AbstractImageContainer>
  extends ImageReader<T> {

  /**
   * Reads the image from the stream. Caller must close the stream.
   *
   * @param stream	the stream to read frmo
   * @return		the image container, null if failed to read
   */
  public T read(InputStream stream);
}
