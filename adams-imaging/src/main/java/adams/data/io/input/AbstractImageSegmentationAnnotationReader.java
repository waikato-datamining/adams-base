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
 * AbstractImageSegmentationAnnotationReader.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.data.io.output.AbstractImageSegmentationAnnotationWriter;
import adams.flow.container.ImageSegmentationContainer;

/**
 * Ancestor for readers for image segmentation annotations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractImageSegmentationAnnotationReader
  extends AbstractOptionHandler
  implements FileFormatHandler {

  private static final long serialVersionUID = -2475426542124421777L;

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  public abstract AbstractImageSegmentationAnnotationWriter getCorrespondingWriter();

  /**
   * Hook method for performing checks before reading the data.
   *
   * @param file	the file to check
   * @return		null if no errors, otherwise error message
   */
  protected String check(PlaceholderFile file) {
    if (file == null)
      return "No file provided!";
    return null;
  }

  /**
   * Reads the image segmentation annotations.
   *
   * @param file	the file to read from
   * @return		the annotations
   */
  protected abstract ImageSegmentationContainer doRead(PlaceholderFile file);

  /**
   * Reads the image segmentation annotations.
   *
   * @param file	the file to read from
   * @return		the annotations
   */
  public ImageSegmentationContainer read(PlaceholderFile file) {
    String	msg;

    msg = check(file);
    if (msg != null)
      throw new IllegalStateException("Failed to read annotations from: " + file + "\n" + msg);

    return doRead(file);
  }
}
