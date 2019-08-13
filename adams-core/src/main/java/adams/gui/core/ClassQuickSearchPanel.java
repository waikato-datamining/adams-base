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
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.DefaultListModel;

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
    DefaultListModel<String>	model;

    model = new DefaultListModel<>();
    for (Class cls: value)
      model.addElement(cls.getName());
    m_ListItems.setModel(model);
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
      return Class.forName("" + m_ListItems.getSelectedValue());
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append("Failed to instantiate class: " + m_ListItems.getSelectedValue(), e);
      return null;
    }
  }
}
