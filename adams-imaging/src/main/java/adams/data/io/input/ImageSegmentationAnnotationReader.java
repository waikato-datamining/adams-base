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
 * ImageSegmentationAnnotationReader.java
 * Copyright (C) 2021-2025 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionHandler;
import adams.data.io.output.ImageSegmentationAnnotationWriter;
import adams.flow.container.ImageSegmentationContainer;

/**
 * Interface for readers for image segmentation annotations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface ImageSegmentationAnnotationReader
  extends OptionHandler, FileFormatHandler {

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  public abstract ImageSegmentationAnnotationWriter getCorrespondingWriter();

  /**
   * Sets whether to use an alternative directory for the annotations.
   *
   * @param value	true if to use alternative dir
   */
  public abstract void setUseAlternativeAnnotationDir(boolean value);

  /**
   * Returns whether to use an alternative directory for the annotations.
   *
   * @return		true if to use alternative dir
   */
  public abstract boolean getUseAlternativeAnnotationDir();

  /**
   * Sets the alternative directory for the annotations.
   *
   * @param value	the alternative dir
   */
  public abstract void setAlternativeAnnotationDir(PlaceholderDirectory value);

  /**
   * Returns the alternative directory for the annotations.
   *
   * @return		the alternative dir
   */
  public abstract PlaceholderDirectory getAlternativeAnnotationDir();

  /**
   * Reads the image segmentation annotations.
   *
   * @param file	the file to read from
   * @return		the annotations
   */
  public ImageSegmentationContainer read(PlaceholderFile file);
}
