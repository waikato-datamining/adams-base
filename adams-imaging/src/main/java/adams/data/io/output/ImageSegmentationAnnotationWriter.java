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
 * ImageSegmentationAnnotationWriter.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionHandler;
import adams.data.io.input.ImageSegmentationAnnotationReader;
import adams.flow.container.ImageSegmentationContainer;

/**
 * Interface for writers for image segmentation annotations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface ImageSegmentationAnnotationWriter
  extends OptionHandler, FileFormatHandler {

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  public abstract ImageSegmentationAnnotationReader getCorrespondingReader();

  /**
   * Writes the image segmentation annotations.
   *
   * @param file	the file to write to
   * @param annotations the annotations to write
   * @return		null if successfully written, otherwise error message
   */
  public String write(PlaceholderFile file, ImageSegmentationContainer annotations);
}
