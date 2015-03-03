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
 * AbstractSearchableContainerList.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.container;

import java.awt.BorderLayout;
import java.awt.Dimension;

import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;

/**
 * Container list that can be searched, if the container manager implements
 * {@link SearchableContainerManager}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSearchableContainerList<M extends AbstractContainerManager, C extends AbstractContainer>
  extends AbstractContainerList<M,C> {

  /** for serialization. */
  private static final long serialVersionUID = -5812608011245974771L;
  
  /** the panel for searching the entry names. */
  protected SearchPanel m_PanelSearch;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, false, "Search", true, "");
    m_PanelSearch.setTextColumns(10);
    m_PanelSearch.setVisible(false);  // needs to be enabled explicitly, using setAllowSearch(boolean)
    m_PanelSearch.addSearchListener(new SearchListener() {
      @Override
      public void searchInitiated(SearchEvent e) {
	if (!getAllowSearch())
	  return;
	SearchableContainerManager smanager = (SearchableContainerManager) getManager();
	if (e.getParameters().getSearchString() == null)
	  smanager.clearSearch();
	else
	  smanager.search(e.getParameters().getSearchString(), e.getParameters().isRegExp());
      }
    });
    add(m_PanelSearch, BorderLayout.SOUTH);
  }
  
  /**
   * Sets whether the entry list is searchable. Container manager must implement
   * {@link SearchableContainerManager} interface to allow enabling of search
   * 
   * @param value	true if to make the list searchable
   * @see		SearchableContainerManager
   */
  public void setAllowSearch(boolean value) {
    if (getManager() instanceof SearchableContainerManager)
      m_PanelSearch.setVisible(value);
    else
      m_PanelSearch.setVisible(false);
  }
  
  /**
   * Returns whether the entry list is searchable.
   * 
   * @return		true if list is searchable
   */
  public boolean getAllowSearch() {
    return m_PanelSearch.isVisible();
  }
  
  /**
   * Returns the preferred dimensions of this widget.
   * 
   * @return		the preferred size
   */
  @Override
  public Dimension getPreferredSize() {
    Dimension	result;
    
    result = (Dimension) super.getPreferredSize().clone();
    if (result.width < m_PanelSearch.getPreferredSize().width)
      result.width = m_PanelSearch.getPreferredSize().width;
    
    return result;
  }
}
