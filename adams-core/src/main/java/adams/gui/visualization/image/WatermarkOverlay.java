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
 * WatermarkOverlay.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.image;

import adams.gui.visualization.image.ImagePanel.PaintPanel;
import adams.gui.visualization.watermark.Default;
import adams.gui.visualization.watermark.Watermark;

import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Overlays a watermark.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class WatermarkOverlay
  extends AbstractImageOverlay {

  private static final long serialVersionUID = 5659227608868789151L;

  /** the watermark to apply. */
  protected Watermark m_Watermark;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
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
    reset();
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
   * Notifies the overlay that the image has changed.
   *
   * @param panel the panel this overlay belongs to
   */
  @Override
  protected void doImageChanged(PaintPanel panel) {
    // nothing to do
  }

  /**
   * Performs the actual painting of the overlay.
   *
   * @param panel the panel this overlay is for
   * @param g     the graphics context
   */
  @Override
  protected void doPaintOverlay(PaintPanel panel, Graphics g) {
    Dimension	dim;

    if (panel.getCurrentImage() == null)
      return;

    dim = new Dimension(panel.getCurrentImage().getWidth(), panel.getCurrentImage().getHeight());
    m_Watermark.applyWatermark(g, dim);
  }
}
