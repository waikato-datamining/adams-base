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
 * AbstractOverlay.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.overlay;

import adams.core.option.AbstractOptionHandler;
import adams.gui.visualization.object.ObjectAnnotationPanel;

import java.awt.Graphics;

/**
 * Ancestor for overlays.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractOverlay
  extends AbstractOptionHandler
  implements Overlay {

  private static final long serialVersionUID = -5270536540300573516L;

  /** whether the overlay is enabled. */
  protected boolean m_Enabled;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "enabled", "enabled",
      true);
  }

  /**
   * Sets whether the overlay is enabled.
   *
   * @param value 	true if enabled
   */
  @Override
  public void setEnabled(boolean value) {
    m_Enabled = value;
    reset();
  }

  /**
   * Returns whether the overlay is enabled.
   *
   * @return 		true if enabled
   */
  @Override
  public boolean getEnabled() {
    return m_Enabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String enabledTipText() {
    return "Determines whether the overlay is enabled or not.";
  }

  /**
   * Paints the overlay.
   *
   * @param panel 	the owning panel
   * @param g		the graphics context
   */
  protected abstract void doPaint(ObjectAnnotationPanel panel, Graphics g);

  /**
   * Paints the overlay.
   *
   * @param panel 	the owning panel
   * @param g		the graphics context
   */
  @Override
  public void paint(ObjectAnnotationPanel panel, Graphics g) {
    if (getEnabled())
      doPaint(panel, g);
  }

  /**
   * Hook method for when annotations change.
   */
  @Override
  public void annotationsChanged() {
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
  }
}
