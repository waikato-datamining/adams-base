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
 * ClipBoundingBoxes.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfilter;

import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Ensures that bounding boxes fall within the image boundaries.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ClipBoundingBoxes
  extends AbstractObjectFilter {

  private static final long serialVersionUID = -2181381799680316619L;

  /** the image width to use. */
  protected int m_Width;

  /** the image height to use. */
  protected int m_Height;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Ensures that bounding boxes fall within the image boundaries.\n"
      + "Automatically removes invalid polygons that require clipping and any "
      + "bounding boxes that have width or height of zero.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "width", "width",
      1000, 1, null);

    m_OptionManager.add(
      "height", "height",
      1000, 1, null);
  }

  /**
   * Sets the width of the image to use.
   *
   * @param value	the image width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the width of the image to use.
   *
   * @return		the image width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the image to use.";
  }

  /**
   * Sets the height of the image to use.
   *
   * @param value	the image height
   */
  public void setHeight(int value) {
    m_Height = value;
    reset();
  }

  /**
   * Returns the height of the image to use.
   *
   * @return		the image height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the image to use.";
  }

  /**
   * Filters the image objects.
   *
   * @param objects	the located objects
   * @return		the updated list of objects
   */
  @Override
  protected LocatedObjects doFilter(LocatedObjects objects) {
    LocatedObjects 	result;
    LocatedObject	fixed;
    boolean		update;
    int			x0;
    int			y0;
    int			x1;
    int			y1;
    int			w;
    int			h;

    result = new LocatedObjects();
    for (LocatedObject object: objects) {
      update = false;
      x0 = object.getX();
      y0 = object.getY();
      x1 = x0 + object.getWidth() - 1;
      y1 = y0 + object.getHeight() - 1;

      if (x0 < 0) {
        x0 = 0;
        update = true;
      }
      if (x0 >= m_Width) {
        x0 = m_Width - 1;
        update = true;
      }
      if (y0 < 0) {
        y0 = 0;
        update = true;
      }
      if (y0 >= m_Height) {
        y0 = m_Height - 1;
        update = true;
      }
      if (x1 < 0) {
        x1 = 0;
        update = true;
      }
      if (x1 >= m_Width) {
        x1 = m_Width - 1;
        update = true;
      }
      if (y1 < 0) {
        y1 = 0;
        update = true;
      }
      if (y1 >= m_Height) {
        y1 = m_Height - 1;
        update = true;
      }

      if (update) {
        if ((x0 != x1) && (y0 != y1)) {
	  fixed = new LocatedObject(object.getImage(), x0, y0, x1 - x0 + 1, y1 - y0 + 1, object.getMetaData(true));
	  result.add(fixed);
	}
      }
      else {
        result.add(object.getClone());
      }
    }

    return result;
  }
}
