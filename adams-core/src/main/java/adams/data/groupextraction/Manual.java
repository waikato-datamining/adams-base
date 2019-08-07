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
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.groupextraction;

import adams.core.QuickInfoHelper;

/**
 * Just returns the supplied group.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Manual
  extends AbstractGroupExtractor {

  private static final long serialVersionUID = 6130414784797102811L;

  /** the manual group. */
  protected String m_Group;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply returns the manually supplied group.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "group", "group",
      "");
  }

  /**
   * Sets the group to use.
   *
   * @param value	the group
   */
  public void setGroup(String value) {
    m_Group = value;
    reset();
  }

  /**
   * Returns the group to use.
   *
   * @return		the group
   */
  public String getGroup() {
    return m_Group;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String groupTipText() {
    return "The group to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "group", m_Group);
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
   * Extracts the group from the object.
   *
   * @param obj		the object to process
   * @return		the extracted group, null if failed to extract or not handled
   */
  @Override
  protected String doExtractGroup(Object obj) {
    return m_Group;
  }
}
