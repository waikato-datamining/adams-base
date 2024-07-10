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
 * AbstractPointPreprocessor.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence.pointpreprocessor;

import adams.core.option.AbstractOptionHandler;
import adams.data.sequence.XYSequencePoint;
import adams.gui.visualization.core.AxisPanel;

/**
 * Ancestor for classes that perform preprocessing on sequence points.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractPointPreprocessor
  extends AbstractOptionHandler
  implements PointPreprocessor {

  private static final long serialVersionUID = 8852045237133754852L;

  /** whether the preprocessor is enabled. */
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
   * Sets whether the preprocessor is enabled.
   *
   * @param value	true if enabled
   */
  public void setEnabled(boolean value) {
    m_Enabled = value;
    reset();
  }

  /**
   * Returns whether the preprocessor is enabled.
   *
   * @return		true if enabled
   */
  public boolean isEnabled() {
    return m_Enabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String enabledTipText() {
    return "Determines whether the preprocessor is enabled.";
  }

  /**
   * Resets the processor for another sequence.
   * <br>
   * Default implementation does nothing.
   */
  @Override
  public void resetPreprocessor() {
  }

  /**
   * Preprocesses the point.
   *
   * @param point	the point to process
   * @param axisX 	the X axis to use
   * @param axisY 	the Y axis to use
   * @return		the new point
   */
  protected abstract XYSequencePoint doPreprocess(XYSequencePoint point, AxisPanel axisX, AxisPanel axisY);

  /**
   * Preprocesses the point.
   *
   * @param point	the point to process
   * @param axisX 	the X axis to use
   * @param axisY 	the Y axis to use
   * @return		the new point
   */
  @Override
  public XYSequencePoint preprocess(XYSequencePoint point, AxisPanel axisX, AxisPanel axisY) {
    if (isEnabled())
      return doPreprocess(point, axisX, axisY);
    else
      return point;
  }
}
