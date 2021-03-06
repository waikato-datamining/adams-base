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
 * LogAxisModel.java
 * Copyright (C) 2011-2018 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.axis;

/**
 * An axis model for displaying ln values.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class LogAxisModel
  extends AbstractAxisModel {

  /** for serialization. */
  private static final long serialVersionUID = -8737139976887145167L;

  /**
   * Checks whether the data range can be handled by the model.
   *
   * @param min		the minimum value
   * @param max		the maximum value
   * @return		false if data range contains negative values
   */
  @Override
  public boolean canHandle(double min, double max) {
    return (min > 0.0) && (max > 0.0);
  }

  /**
   * Adjusts the minimum to work with this model.
   *
   * @param min		the minimum to adjust
   * @return		the updated value
   */
  public double adjustMinimum(double min) {
    if (min <= 0.0)
      return 1.0e-6;
    else
      return min;
  }

  /**
   * Adjusts the maximum to work with this model.
   *
   * @param max		the maximum to adjust
   * @return		the updated value
   */
  public double adjustMaximum(double max) {
    if (max <= 0.0)
      return 1.0e-5;
    else
      return max;
  }

  /**
   * Returns the display name of this model.
   *
   * @return		the display name
   */
  @Override
  public String getDisplayName() {
    return "Log";
  }

  /**
   * Returns the display string of the value for the tooltip, for instance.
   *
   * @param value	the value to turn into string
   * @return		the display string
   */
  @Override
  protected String doValueToDisplay(double value) {
    return getActualFormatter().format(value);
  }

  /**
   * Returns the position on the axis for the given value.
   *
   * @param value	the value to get the position for
   * @return		the corresponding position
   */
  @Override
  public int valueToPos(double value) {
    int	result;
    int	size;
    double	tmp;

    validate();

    size   = getParent().getLength() - 1;
    tmp    = Math.log(value) - Math.log(m_ActualMinimum);
    tmp    = tmp / (Math.log(m_ActualMaximum) - Math.log(m_ActualMinimum));
    tmp    = tmp * size;
    result = (int) Math.round(tmp);

    return result;
  }

  /**
   * Returns the value for the given position on the axis.
   *
   * @param pos	the position to get the corresponding value for
   * @return		the corresponding value
   */
  @Override
  public double posToValue(int pos) {
    double	result;
    int		size;
    double	tmp;

    validate();

    size   = getParent().getLength() - 1;
    tmp    = (double) pos / (double) size;
    tmp    = tmp * (Math.log(m_ActualMaximum) - Math.log(m_ActualMinimum));
    tmp    = tmp + Math.log(m_ActualMinimum);
    tmp    = Math.exp(tmp);
    result = tmp;

    return result;
  }
}