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
 * OptionalBoundingBoxFallbackSupporter.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.objectoverlap;

/**
 * Interface for classes that can (optionally) fall back on a bbox when the polygon is too small in relation to the bbox.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface OptionalBoundingBoxFallbackSupporter
  extends BoundingBoxFallbackSupporter {

  /**
   * Sets whether to fall back on the bounding box if no polygon available.
   *
   * @param value 	true if to use
   */
  public void setFallback(boolean value);

  /**
   * Returns whether to fall back on the bounding box if no polygon available.
   *
   * @return 		true if to use
   */
  public boolean getFallback();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fallbackTipText();
}
