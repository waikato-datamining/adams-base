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
 * AbstractIndexedSplitsRunsCompatibility.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.indexedsplits;

import adams.core.option.AbstractOptionHandler;

/**
 * For checking compatibility between indexed splits and data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractIndexedSplitsRunsCompatibility
  extends AbstractOptionHandler
  implements IndexedSplitsRunsCompatibility {

  private static final long serialVersionUID = -7790713819190384379L;

  /** whether to be lenient. */
  protected boolean m_Lenient;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "lenient", "lenient",
      false);
  }

  /**
   * Sets whether to be lenient with the checks.
   *
   * @param value	true if lenient
   */
  public void setLenient(boolean value){
    m_Lenient = value;
    reset();
  }

  /**
   * Returns whether to be lenient with the checks.
   *
   * @return		true if lenient
   */
  public boolean getLenient(){
    return m_Lenient;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String lenientTipText();
}
