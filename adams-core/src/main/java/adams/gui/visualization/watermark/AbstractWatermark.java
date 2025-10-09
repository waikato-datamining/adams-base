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
 * AbstractWatermark.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.watermark;

import adams.core.option.AbstractOptionHandler;

import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Ancestor for watermark plugins.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractWatermark
  extends AbstractOptionHandler
  implements Watermark {

  private static final long serialVersionUID = -5440417892151327596L;

  /** whether the watermark is enabled. */
  protected boolean m_Enabled;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "enabled", "enabled",
      true);
  }

  /**
   * Sets whether to enable the watermark.
   *
   * @param value	true if to enable
   */
  public void setEnabled(boolean value) {
    m_Enabled = value;
    reset();
  }

  /**
   * Returns whether the watermark is enabled.
   *
   * @return		true if enabled
   */
  public boolean getEnabled() {
    return m_Enabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String enabledTipText() {
    return "If enable, the watermark will get applied.";
  }

  /**
   * Returns whether the watermark can be applied.
   * <br>
   * Default implementation just returns true.
   *
   * @param g		the graphics context
   * @param dimension 	the dimension of the drawing area
   * @return		true if it can be applied
   */
  protected boolean canApplyWatermark(Graphics g, Dimension dimension) {
    return true;
  }

  /**
   * Applies the watermark in the specified graphics context.
   *
   * @param g		the graphics context
   * @param dimension 	the dimension of the drawing area
   */
  protected abstract void doApplyWatermark(Graphics g, Dimension dimension);

  /**
   * Applies the watermark in the specified graphics context.
   *
   * @param g		the graphics context
   * @param dimension 	the dimension of the drawing area
   */
  @Override
  public void applyWatermark(Graphics g, Dimension dimension) {
    if (getEnabled() && canApplyWatermark(g, dimension))
      doApplyWatermark(g, dimension);
  }
}
