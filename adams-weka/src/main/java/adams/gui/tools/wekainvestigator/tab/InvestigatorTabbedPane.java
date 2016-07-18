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
 * InvestigatorTabbedPane.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.gui.core.ButtonTabComponent;
import adams.gui.core.DragAndDropTabbedPane;
import adams.gui.core.GUIHelper;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;

/**
 * Tabbed pane for managing the tabs of the Investigator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InvestigatorTabbedPane
  extends DragAndDropTabbedPane {

  private static final long serialVersionUID = -8555377473460866456L;

  /** the owner. */
  protected InvestigatorPanel m_Owner;

  /**
   * Initializes the tabbed pane.
   *
   * @param owner	the owning investigator instance
   */
  public InvestigatorTabbedPane(InvestigatorPanel owner) {
    super();

    m_Owner = owner;

    setCloseTabsWithMiddelMouseButton(true);
    setShowCloseTabButton(true);
  }

  /**
   * Returns the owner.
   *
   * @return		the owner
   */
  public InvestigatorPanel getOwner() {
    return m_Owner;
  }

  /**
   * Adds the tab.
   *
   * @param tab		the tab to add
   */
  public void addTab(AbstractInvestigatorTab tab) {
    ButtonTabComponent	button;

    tab.setOwner(getOwner());
    addTab(tab.getTitle(), tab);

    // icon
    button = (ButtonTabComponent) getTabComponentAt(getTabCount() - 1);
    button.setIcon((tab.getTabIcon() == null) ? null : GUIHelper.getIcon(tab.getTabIcon()));
  }
}
