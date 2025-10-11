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
 * WatermarkPaintlet.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.core;

import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.watermark.Default;
import adams.gui.visualization.watermark.Watermark;

import java.awt.Graphics;

/**
 * Overlays a watermark.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WatermarkPaintlet
  extends AbstractPaintlet {

  private static final long serialVersionUID = 7923819857566247771L;

  /** the watermark to apply. */
  protected Watermark m_Watermark;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Overlays a watermark.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "watermark", "watermark",
      new Default());
  }

  /**
   * Sets the watermark to apply.
   *
   * @param value	the watermark
   */
  public void setWatermark(Watermark value) {
    m_Watermark = value;
    memberChanged();
  }

  /**
   * Returns the watermark to apply.
   *
   * @return		the watermark
   */
  public Watermark getWatermark() {
    return m_Watermark;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String watermarkTipText() {
    return "The watermark scheme to apply.";
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.POST_PAINT;
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    m_Watermark.applyWatermark(g, getPanel().getPlot().getContent().getSize());
  }
}
