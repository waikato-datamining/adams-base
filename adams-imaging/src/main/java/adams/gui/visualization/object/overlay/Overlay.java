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

import adams.core.CleanUpHandler;
import adams.core.option.OptionHandler;
import adams.gui.visualization.object.ObjectAnnotationPanel;

import java.awt.Graphics;

/**
 * Interface for overlays.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface Overlay
  extends OptionHandler, CleanUpHandler {

  /**
   * Sets whether the overlay is enabled.
   *
   * @param value 	true if enabled
   */
  public void setEnabled(boolean value);

  /**
   * Returns whether the overlay is enabled.
   *
   * @return 		true if enabled
   */
  public boolean getEnabled();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String enabledTipText();

  /**
   * Paints the overlay.
   *
   * @param panel 	the owning panel
   * @param g		the graphics context
   */
  public void paint(ObjectAnnotationPanel panel, Graphics g);

  /**
   * Hook method for when annotations change.
   */
  public void annotationsChanged();

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp();
}
