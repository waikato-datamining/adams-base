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
 * ClassQuickSearchPanel.java
 * Copyright (C) 2019-2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.classmanager.ClassManager;
import com.github.fracpete.javautils.enumerate.Enumerated;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * Allows user to search/select class from a class hierarchy.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ClassQuickSearchPanel
  extends AbstractQuickSearchPanel {

  private static final long serialVersionUID = -8297241550756579552L;

  /**
   * Sets the classes to display.
   *
   * @param value	the classes
   */
  public void setClasses(Class[] value) {
    String[]	items;

    items = new String[value.length];
    for (Enumerated<Class> c: enumerate(value))
      items[c.index] = c.value.getName();

    updateModel(items);
  }

  /**
   * Returns the selected class.
   *
   * @return		the class, null if none selected or failed to obtain class from string
   */
  public Class getSelectedClass() {
    if (m_ListItems.getSelectedIndex() == -1)
      return null;

    try {
      return ClassManager.getSingleton().forName("" + m_ListItems.getSelectedValue());
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append("Failed to instantiate class: " + m_ListItems.getSelectedValue(), e);
      return null;
    }
  }
}
