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
 * AbstractPlotOptionGroup.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.stats.core;

import adams.core.option.AbstractOptionGroup;
import adams.gui.visualization.core.AxisPanelOptions;

/**
 * Ancestor for option groups for plots.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPlotOptionGroup
  extends AbstractOptionGroup {

  /** for serialization. */
  private static final long serialVersionUID = -408213623161206253L;

  /** the options for the X axis. */
  protected AxisPanelOptions m_AxisX;

  /** the options for the Y axis. */
  protected AxisPanelOptions m_AxisY;

  /**
   * Configures the options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "axis-x", "axisX",
	    getDefaultAxisX());

    m_OptionManager.add(
	    "axis-y", "axisY",
	    getDefaultAxisY());
  }

  /**
   * Returns the setup for the X axis.
   *
   * @return 		the setup
   */
  protected abstract AxisPanelOptions getDefaultAxisX();

  /**
   * Sets the setup for the X axis.
   *
   * @param value 	the setup
   */
  public void setAxisX(AxisPanelOptions value) {
    m_AxisX = value;
    reset();
  }

  /**
   * Returns the setup for the X axis.
   *
   * @return 		the setup
   */
  public AxisPanelOptions getAxisX() {
    return m_AxisX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String axisXTipText() {
    return "The setup for the X axis.";
  }

  /**
   * Returns the setup for the Y axis.
   *
   * @return 		the setup
   */
  protected abstract AxisPanelOptions getDefaultAxisY();

  /**
   * Sets the setup for the Y axis.
   *
   * @param value 	the setup
   */
  public void setAxisY(AxisPanelOptions value) {
    m_AxisY = value;
    reset();
  }

  /**
   * Returns the setup for the Y axis.
   *
   * @return 		the setup
   */
  public AxisPanelOptions getAxisY() {
    return m_AxisY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String axisYTipText() {
    return "The setup for the Y axis.";
  }
}
