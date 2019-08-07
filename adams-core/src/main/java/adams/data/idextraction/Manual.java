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
 * Manual.java
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
 */

package adams.data.idextraction;

import adams.core.QuickInfoHelper;

/**
 * Just returns the supplied ID.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Manual
  extends AbstractIDExtractor {

  private static final long serialVersionUID = 6130414784797102811L;

  /** the manual ID. */
  protected String m_ID;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply returns the manually supplied ID.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "id", "ID",
      "");
  }

  /**
   * Sets the ID to use.
   *
   * @param value	the ID
   */
  public void setID(String value) {
    m_ID = value;
    reset();
  }

  /**
   * Returns the ID to use.
   *
   * @return		the ID
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String IDTipText() {
    return "The ID to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "ID", m_ID);
  }

  /**
   * Checks whether the data type is handled.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  public boolean handles(Object obj) {
    return true;
  }

  /**
   * Extracts the ID from the object.
   *
   * @param obj		the object to process
   * @return		the extracted ID
   */
  @Override
  protected String doExtractID(Object obj) {
    return m_ID;
  }
}
