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
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.sequence;

/**
 * A wrapper for XY-sequence paintlets, in order to use fixed X and Y ranges.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PaintletWithFixedXYRange
  extends PaintletWithFixedYRange
  implements XYSequencePaintletWithFixedXRange {

  /** for serialization. */
  private static final long serialVersionUID = -7452372971179139015L;

  /** the minimum of Y. */
  protected double m_MinX;

  /** the maximum of Y. */
  protected double m_MaxX;

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
	    0.0, null, null);

    m_OptionManager.add(
	    "max-x", "maxX",
	    1000.0, null, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    setPaintlet(getDefaultPaintlet());

    super.initialize();
  }

  /**
   * Returns the default paintlet to use.
   *
   * @return		the default paintlet
   */
  @Override
  protected AbstractXYSequencePaintlet getDefaultPaintlet() {
    return new DotPaintlet();
  }

  /**
   * Sets the minimum of the X range.
   *
   * @param value	the minimum
   */
  public void setMinX(double value) {
    m_MinX = value;
    memberChanged(true);
  }

  /**
   * Returns the minimum of the X range.
   *
   * @return		the minimum
   */
  public double getMinX() {
    return m_MinX;
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
  public void setMaxX(double value) {
    m_MaxX = value;
    memberChanged(true);
  }

  /**
   * Returns the maximum of the X range.
   *
   * @return		the maximum
   */
  public double getMaxX() {
    return m_MaxX;
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
