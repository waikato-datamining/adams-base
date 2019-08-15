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

/**
 * Allows user to search/select strings.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class StringQuickSearchPanel
  extends AbstractQuickSearchPanel {

  private static final long serialVersionUID = -8297241550756579552L;

  /**
   * Sets the items to display.
   *
   * @param value	the items
   */
  public void setItems(String[] value) {
    updateModel(value);
  }

  /**
   * Returns the selected item.
   *
   * @return		the item, null if none selected
   */
  public String getSelectedItem() {
    if (m_ListItems.getSelectedIndex() == -1)
      return null;

    return "" + m_ListItems.getSelectedValue();
  }
}
