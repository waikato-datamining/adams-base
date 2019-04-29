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
 * SpreadSheetTableWithSearch.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.event.SearchEvent;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Spreadsheet table with search and (optional) buttons.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetTableWithSearch
  extends SpreadSheetTableWithButtons {

  private static final long serialVersionUID = -228126271855184258L;

  /** for searching the spreadsheet. */
  protected SearchPanel m_PanelSearch;

  /** the panel for the columns combobox. */
  protected JPanel m_PanelColumnsDropdown;

  /** combobox with the columns. */
  protected SpreadSheetColumnComboBox m_ComboBoxColumnsDropdown;

  /**
   * The default constructor.
   */
  public SpreadSheetTableWithSearch() {
    this(new SpreadSheetTableModel());
  }

  /**
   * Initializes the table with the specified model.
   *
   * @param model	the model to use
   */
  public SpreadSheetTableWithSearch(SpreadSheetTableModel model) {
    super(model);
  }

  /**
   * Initializes the widget.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_PanelColumnsDropdown = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_PanelColumnsDropdown.setVisible(false);
    m_ComboBoxColumnsDropdown = new SpreadSheetColumnComboBox((SpreadSheetTable) getComponent());
    m_PanelColumnsDropdown.add(m_ComboBoxColumnsDropdown);
    add(m_PanelColumnsDropdown, BorderLayout.NORTH);

    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, true);
    m_PanelSearch.addSearchListener((SearchEvent e) ->
      ((SpreadSheetTable) getComponent()).search(e.getParameters().getSearchString(), e.getParameters().isRegExp()));
    add(m_PanelSearch, BorderLayout.SOUTH);
  }

  /**
   * Sets whether the search is visible.
   *
   * @param value	true if visible
   */
  public void setSearchVisible(boolean value) {
    m_PanelSearch.setVisible(value);
  }

  /**
   * Returns whether the search is visible.
   *
   * @return		true if visible
   */
  public boolean isSearchVisible() {
    return m_PanelSearch.isVisible();
  }

  /**
   * Sets whether the dropdown with the columns is visible.
   *
   * @param value	true if visible
   */
  public void setColumnsDropdownVisible(boolean value) {
    m_PanelColumnsDropdown.setVisible(value);
  }

  /**
   * Returns whether the dropdown with the columns is visible.
   *
   * @return		true if visible
   */
  public boolean isColumnsDropdownVisible() {
    return m_PanelColumnsDropdown.isVisible();
  }
}
