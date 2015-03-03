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
 * DateTimeAxisModel.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.axis;

/**
 * An axis model for displaying time values.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeAxisModel
  extends AbstractDateBasedAxisModel {

  /** for serialization. */
  private static final long serialVersionUID = 6882846237550109166L;

  /**
   * Returns the display name of this model.
   *
   * @return		the display name
   */
  public String getDisplayName() {
    return "Time";
  }

  /**
   * Returns the default format for the date/time formatter.
   *
   * @return		the format string
   */
  protected String getDefaultDateFormat() {
    return "HH:mm";
  }
}