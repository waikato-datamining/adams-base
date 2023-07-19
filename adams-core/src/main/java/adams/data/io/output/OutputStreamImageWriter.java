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
 * OutputStreamImageWriter.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import adams.data.image.AbstractImageContainer;

import java.io.OutputStream;

/**
 * Interface for image writers that can write to output streams.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface OutputStreamImageWriter<T extends AbstractImageContainer>
  extends ImageWriter<T> {

  /**
   * Writes the image to the stream. Callers must close the stream.
   *
   * @param stream	the stream to write to
   * @param cont	the image container to write
   * @return		null if successfully written, otherwise error message
   */
  public String write(OutputStream stream, T cont);
}
