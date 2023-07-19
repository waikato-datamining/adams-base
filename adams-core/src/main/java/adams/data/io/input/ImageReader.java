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
 * ImageReader.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionHandler;
import adams.data.image.AbstractImageContainer;
import adams.data.io.output.ImageWriter;

/**
 * Interface for image readers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ImageReader<T extends AbstractImageContainer>
  extends OptionHandler, FileFormatHandler {

  /**
   * Returns, if available, the corresponding writer.
   * 
   * @return		the writer, null if none available
   */
  public abstract ImageWriter getCorrespondingWriter();
  
  /**
   * Returns whether the reader is actually available.
   * 
   * @return		true if available and ready to use
   */
  public boolean isAvailable();

  /**
   * Reads the image file.
   * 
   * @param file	the file to read
   * @return		the image container, null if failed to read
   */
  public T read(PlaceholderFile file);
}
