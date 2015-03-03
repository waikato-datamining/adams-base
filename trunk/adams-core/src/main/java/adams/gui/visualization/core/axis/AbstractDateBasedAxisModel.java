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
 * AbstractDateBasedAxisModel.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.axis;

/**
 * Ancestor axis model for displaying date-based values.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDateBasedAxisModel
  extends AbstractAxisModel 
  implements FlippableAxisModel {

  /** for serialization. */
  private static final long serialVersionUID = 6882846237550109166L;

  /** whether the axis is flipped. */
  protected boolean m_Flipped;

  /**
   * Initializes the member variables.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Flipped = false;
    m_Formatter = Formatter.getDateFormatter(getDefaultDateFormat());
  }

  /**
   * Obtains the necessary values from the given model and updates itself.
   *
   * @param model	the model to get the parameters from
   */
  @Override
  public void assign(AbstractAxisModel model) {
    if (model instanceof FlippableAxisModel)
      m_Flipped = ((FlippableAxisModel) model).isFlipped();
    
    super.assign(model);
  }

  /**
   * Checks whether the data range can be handled by the model.
   *
   * @param min		the minimum value
   * @param max		the maximum value
   * @return		always true
   */
  @Override
  public boolean canHandle(double min, double max) {
    return true;
  }

  /**
   * Returns the default format for the date/time formatter.
   *
   * @return		the format string
   */
  protected abstract String getDefaultDateFormat();

  /**
   * Returns the display name of this model.
   *
   * @return		the display name
   */
  @Override
  public abstract String getDisplayName();

  /**
   * Sets whether to flip the axis.
   * 
   * @param value	if true the axis gets flipped
   */
  public void setFlipped(boolean value) {
    m_Flipped = value;
    invalidate();
    update();
  }
  
  /**
   * Returns whether the axis is flipped.
   * 
   * @return		true if the axis is flipped
   */
  public boolean isFlipped() {
    return m_Flipped;
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

    validate();

    size   = getParent().getLength() - 1;
    result = (int) Math.round((value - m_ActualMinimum) / (m_ActualMaximum - m_ActualMinimum) * size);

    if (m_Flipped)
      result = getParent().getLength() - 1 - result;

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
    int	size;

    validate();

    if (m_Flipped)
      pos = getParent().getLength() - 1 - pos;

    size   = getParent().getLength() - 1;
    result = ((double) pos / (double) size) * (m_ActualMaximum - m_ActualMinimum) + m_ActualMinimum;

    return result;
  }

  /**
   * Adds the zoom to its internal list and updates the axis.
   *
   * @param min		the minimum of the zoom
   * @param max		the maximum of the zoom
   */
  @Override
  public void pushZoom(double min, double max) {
    if (m_Flipped)
      super.pushZoom(max, min);
    else
      super.pushZoom(min, max);
  }
}