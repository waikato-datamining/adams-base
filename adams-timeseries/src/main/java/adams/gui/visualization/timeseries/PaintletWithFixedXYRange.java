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

/**
 * PaintletWithFixedXYRange.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.timeseries;

import adams.core.base.BaseDateTime;

/**
 * A wrapper for XY-sequence paintlets, in order to use fixed X and Y ranges.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PaintletWithFixedXYRange
  extends PaintletWithFixedYRange
  implements adams.gui.visualization.core.PaintletWithFixedXRange {

  /** for serialization. */
  private static final long serialVersionUID = -7452372971179139015L;

  /** the minimum of Y. */
  protected BaseDateTime m_MinX;

  /** the maximum of Y. */
  protected BaseDateTime m_MaxX;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Meta-paintlet that uses a fixed X and Y ranges (for faster drawing) and a base-paintlet to draw the actual data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "min-x", "minX",
	    new BaseDateTime(BaseDateTime.INF_PAST));

    m_OptionManager.add(
	    "max-x", "maxX",
	    new BaseDateTime(BaseDateTime.INF_FUTURE));
  }

  /**
   * Sets the minimum of the X range.
   *
   * @param value	the minimum
   */
  public void setMinX(BaseDateTime value) {
    m_MinX = value;
    memberChanged(true);
  }

  /**
   * Returns the minimum of the X range.
   *
   * @return		the minimum
   */
  public BaseDateTime getMinX() {
    return m_MinX;
  }

  /**
   * Returns the minimum of the X range.
   *
   * @return		the minimum
   */
  public double getMinimumX() {
    return m_MinX.dateValue().getTime();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minXTipText() {
    return "The minimum value for the X range.";
  }

  /**
   * Sets the maximum of the X range.
   *
   * @param value	the maximum
   */
  public void setMaxX(BaseDateTime value) {
    m_MaxX = value;
    memberChanged(true);
  }

  /**
   * Returns the maximum of the X range.
   *
   * @return		the maximum
   */
  public BaseDateTime getMaxX() {
    return m_MaxX;
  }

  /**
   * Returns the maximum of the X range.
   *
   * @return		the maximum
   */
  public double getMaximumX() {
    return m_MaxX.dateValue().getTime();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxXTipText() {
    return "The maximum value for the X range.";
  }
}
