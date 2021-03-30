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
 * ImageSegmentationReaderWithLayerNames.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.base.BaseString;

/**
 * Interface for image segmentation readers that make use of layer names.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface ImageSegmentationAnnotationReaderWithLayerNames
  extends ImageSegmentationAnnotationReader {

  /**
   * Sets the names for the layers to use.
   *
   * @param value	the names
   */
  public void setLayerNames(BaseString[] value);

  /**
   * Returns the names for the layers to use.
   *
   * @return		the names
   */
  public BaseString[] getLayerNames();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String layerNamesTipText();
}
