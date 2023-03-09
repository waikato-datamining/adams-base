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
 * AbstractPaintOperation.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.segmentation.paintoperation;

import adams.core.option.AbstractOptionHandler;
import adams.gui.visualization.segmentation.tool.Tool;

import java.awt.Graphics2D;

/**
 * Ancestor for additional paint operations in the segmentation panel.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractPaintOperation
  extends AbstractOptionHandler
  implements PaintOperation {

  private static final long serialVersionUID = -7420869998984195986L;

  /** the owner. */
  protected Tool m_Owner;

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
   * Sets the owning tool.
   *
   * @param value	the owner
   */
  public void setOwner(Tool value) {
    m_Owner = value;
  }

  /**
   * Returns the owning tool.
   *
   * @return		the owner
   */
  public Tool getOwner() {
    return m_Owner;
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
   * Performs a paint operation.
   *
   * @param g		the graphics context
   */
  protected abstract void doPerformPaint(Graphics2D g);

  /**
   * Performs a paint operation.
   *
   * @param g		the graphics context
   */
  @Override
  public void performPaint(Graphics2D g) {
    if (m_Enabled && (m_Owner != null))
      doPerformPaint(g);
  }
}
