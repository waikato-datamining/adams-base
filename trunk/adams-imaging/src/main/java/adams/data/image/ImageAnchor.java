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
 * ImageAnchor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image;

/**
 * Enumeration for an anchor on an image.
 * 
 * <pre>
 * TL - TC - TR
 * |    |    |
 * ML - MC - MR
 * |    |    |
 * BL - BC - BR
 * </pre>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum ImageAnchor {
  /** top left corner. */
  TOP_LEFT,
  /** top center. */
  TOP_CENTER,
  /** top right corner. */
  TOP_RIGHT,
  /** middle left. */
  MIDDLE_LEFT,
  /** middle center. */
  MIDDLE_CENTER,
  /** middle right. */
  MIDDLE_RIGHT,
  /** bottom left corner. */
  BOTTOM_LEFT,
  /** bottom center. */
  BOTTOM_CENTER,
  /** bottom right corner. */
  BOTTOM_RIGHT
}