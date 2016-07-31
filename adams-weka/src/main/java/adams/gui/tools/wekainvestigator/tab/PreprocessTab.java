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
 * PreprocessTab.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import javax.swing.ListSelectionModel;

/**
 * Preprocessing tab.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PreprocessTab
  extends AbstractInvestigatorTabWithDataTable {

  private static final long serialVersionUID = -94945456385486233L;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
  }

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Preprocess";
  }

  /**
   * Returns the icon name for the tab icon.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return "filter.png";
  }

  /**
   * Returns whether a readonly table is used.
   *
   * @return		true if readonly
   */
  protected boolean hasReadOnlyTable() {
    return false;
  }

  /**
   * Returns the list selection mode to use.
   *
   * @return		the mode
   * @see                ListSelectionModel
   */
  protected int getDataTableListSelectionMode() {
    return ListSelectionModel.SINGLE_SELECTION;
  }

  /**
   * Gets called when the used changes the selection.
   */
  protected void dataTableSelectionChanged() {
    displayData();
  }

  /**
   * Displays the data.
   */
  protected void displayData() {
    // TODO
  }
}
