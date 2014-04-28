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
 * GPSDecimalMinutes.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

/**
 * Handler for {@link adams.data.gps.GPSDecimalMinutes} objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GPSDecimalMinutes
  extends AbstractObjectHandler<adams.data.gps.AbstractGPS> {

  /** for serialization. */
  private static final long serialVersionUID = 6259928028346964272L;

  /** whether format is "long lat" instead of "lat long". */
  protected boolean m_Swapped;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Handles instances of " + adams.data.gps.GPSDecimalMinutes.class.getName() + ".";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "swapped", "swapped",
	    false);
  }

  /**
   * Sets whether the format is swapped, 'long lat' instead of 'lat long'.
   *
   * @param value	true if swapped
   */
  public void setSwapped(boolean value) {
    m_Swapped = value;
    reset();
  }

  /**
   * Returns whether the format is swapped, 'long lat' instead of 'lat long'.
   *
   * @return		true if swapped
   */
  public boolean getSwapped() {
    return m_Swapped;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String swappedTipText() {
    return "If enabled, the format is interpreted as 'long lat' instead of 'lat long'.";
  }

  /**
   * Checks whether the handler can process the given class.
   * 
   * @param cls 	the class to check
   * @return		true if handler can process the class
   */
  @Override
  public boolean handles(Class cls) {
    return cls.equals(adams.data.gps.GPSDecimalMinutes.class);
  }

  /**
   * Parses the given string.
   * 
   * @param s		the string
   * @return		the generated object, null if failed to convert
   */
  @Override
  public adams.data.gps.GPSDecimalMinutes parse(String s) {
    return new adams.data.gps.GPSDecimalMinutes(s, m_Swapped);
  }

  /**
   * Turns the given object back into a string.
   * 
   * @param obj		the object to convert into a string
   * @return		the string representation
   */
  @Override
  public String format(adams.data.gps.AbstractGPS obj) {
    if (obj instanceof adams.data.gps.GPSDecimalMinutes)
      return obj.toString();
    else
      return new adams.data.gps.GPSDecimalMinutes(obj).toString();
  }
}
