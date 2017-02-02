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
 * SearchableBaseListWithButtons.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.event.SearchEvent;

import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;

/**
 * TODO: What class does.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SearchableBaseListWithButtons
  extends BaseListWithButtons {

  private static final long serialVersionUID = 8988746409098190469L;

  /** the search panel, if search is required. */
  protected SearchPanel m_PanelSearch;

  /**
   * The default constructor.
   */
  public SearchableBaseListWithButtons() {
    super();
  }

  /**
   * Initializes the list with the specified model.
   *
   * @param model	the model to use
   */
  public SearchableBaseListWithButtons(ListModel model) {
    super(model);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, false);
    m_PanelSearch.setVisible(false);
    m_PanelSearch.addSearchListener((SearchEvent e) ->
      ((SearchableBaseList) m_Component).search(e.getParameters().getSearchString(), e.getParameters().isRegExp()));
    m_PanelAll.add(m_PanelSearch, BorderLayout.SOUTH);
  }

  /**
   * Creates the component to use in the panel. If a
   *
   * @return		the component
   */
  protected BaseList createComponent() {
    SearchableBaseList	result;

    result = new SearchableBaseList();
    result.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	updateCounts();
      }
    });

    return result;
  }

  /**
   * Sets whether searching is possible.
   *
   * @param value	true if to allow
   */
  public void setAllowSearch(boolean value) {
    m_PanelSearch.setVisible(value);
  }

  /**
   * Returns whether search is available.
   *
   * @return		true if allowed
   */
  public boolean getAllowSearch() {
    return m_PanelSearch.isVisible();
  }

  /**
   * Returns the actual index in the model.
   *
   * @param index	the index of the currently displayed data
   * @return		the index in the underlying data
   */
  public int getActualIndex(int index) {
    return ((SearchableBaseList) m_Component).getActualIndex(index);
  }
}
