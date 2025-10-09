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
 * MultiWatermark.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.watermark;

import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Applies the specified watermarks sequentially.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MultiWatermark
  extends AbstractWatermark {

  private static final long serialVersionUID = 508220912806522510L;

  /** the watermarks to apply. */
  protected Watermark[] m_Watermarks;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified watermarks sequentially.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "watermark", "watermarks",
      new Watermark[0]);
  }

  /**
   * Sets the watermarks to apply.
   *
   * @param value	the watermarks
   */
  public void setWatermarks(Watermark[] value) {
    m_Watermarks = value;
    reset();
  }

  /**
   * Returns the watermarks to apply.
   *
   * @return		the watermarks
   */
  public Watermark[] getWatermarks() {
    return m_Watermarks;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String watermarksTipText() {
    return "The watermarks to apply.";
  }

  /**
   * Returns whether the watermark can be applied.
   *
   * @param g		the graphics context
   * @param dimension 	the dimension of the drawing area
   * @return		true if it can be applied
   */
  @Override
  protected boolean canApplyWatermark(Graphics g, Dimension dimension) {
    return super.canApplyWatermark(g, dimension) && (m_Watermarks.length > 0);
  }

  /**
   * Applies the watermark in the specified graphics context.
   *
   * @param g         the graphics context
   * @param dimension the dimension of the drawing area
   */
  @Override
  protected void doApplyWatermark(Graphics g, Dimension dimension) {
    for (Watermark watermark: m_Watermarks)
      watermark.applyWatermark(g, dimension);
  }
}
