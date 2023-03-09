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
 * PaintOperation.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.segmentation.paintoperation;

import adams.core.option.OptionHandler;
import adams.gui.visualization.segmentation.tool.Tool;

import java.awt.Graphics2D;

/**
 * Interface for additional paint operations in the segmentation panel.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface PaintOperation
  extends OptionHandler {

  /**
   * Sets the owning tool.
   *
   * @param value	the owner
   */
  public void setOwner(Tool value);

  /**
   * Returns the owning tool.
   *
   * @return		the owner
   */
  public Tool getOwner();

  /**
   * Sets whether the paint operation is enabled.
   *
   * @param value 	true if enabled
   */
  public void setEnabled(boolean value);

  /**
   * Returns whether the paint operation is enabled.
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
   * Performs a paint operation.
   *
   * @param g		the graphics context
   */
  public void performPaint(Graphics2D g);
}
