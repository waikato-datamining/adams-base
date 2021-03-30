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
 * AbstractImageSegmentationAnnotationWriter.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.data.io.input.ImageSegmentationAnnotationReader;
import adams.flow.container.ImageSegmentationContainer;

/**
 * Ancestor for writers for image segmentation annotations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractImageSegmentationAnnotationWriter
  extends AbstractOptionHandler 
  implements ImageSegmentationAnnotationWriter {

  private static final long serialVersionUID = -2475426542124421777L;

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  public abstract ImageSegmentationAnnotationReader getCorrespondingReader();

  /**
   * Hook method for performing checks before writing the data.
   *
   * @param file	the file to check
   * @param annotations the annotations to write
   * @return		null if no errors, otherwise error message
   */
  protected String check(PlaceholderFile file, ImageSegmentationContainer annotations) {
    if (file == null)
      return "No file provided!";
    return null;
  }

  /**
   * Writes the image segmentation annotations.
   *
   * @param file	the file to write to
   * @param annotations the annotations to write
   * @return		null if successfully written, otherwise error message
   */
  protected abstract String doWrite(PlaceholderFile file, ImageSegmentationContainer annotations);

  /**
   * Writes the image segmentation annotations.
   *
   * @param file	the file to write to
   * @param annotations the annotations to write
   * @return		null if successfully written, otherwise error message
   */
  public String write(PlaceholderFile file, ImageSegmentationContainer annotations) {
    String	result;

    result = check(file, annotations);
    if (result == null)
      result = doWrite(file, annotations);

    return result;
  }
}
