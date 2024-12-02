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
 * InvestigatorTabbedPane.java
 * Copyright (C) 2016-2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.CleanUpHandler;
import adams.core.MessageCollection;
import adams.core.classmanager.ClassManager;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.ButtonTabComponent;
import adams.gui.core.DragAndDropTabbedPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.MouseUtils;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab.SerializationOption;

import javax.swing.JPopupMenu;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Tabbed pane for managing the tabs of the Investigator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InvestigatorTabbedPane
  extends DragAndDropTabbedPane
  implements CleanUpHandler {

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

    setCloseTabsWithMiddleMouseButton(false);
    setShowCloseTabButton(true);
    setTabLayoutPolicy(BaseTabbedPane.SCROLL_TAB_LAYOUT);
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
   * Disambiguates the title, appending " (X)" to it.
   *
   * @param title	the base title
   * @return		the new title
   */
  public String disambiguateTitle(String title) {
    String	result;
    int		i;
    int		count;
    boolean	found;

    count = 1;
    while (true) {
      result = title + ((count > 1) ? " (" + count + ")" : "");
      found = false;
      for (i = 0; i < getTabCount(); i++) {
	if (getTitleAt(i).equals(result)) {
	  found = true;
	  break;
	}
      }
      if (found)
	count++;
      else
	break;
    }

    return result;
  }

  /**
   * Adds the tab.
   *
   * @param tab		the tab to add
   */
  public void addTab(final AbstractInvestigatorTab tab) {
    addTab(tab, false);
  }

  /**
   * Adds the tab.
   *
   * @param tab		the tab to add
   * @param show	whether to make this tab visible
   */
  public void addTab(final AbstractInvestigatorTab tab, boolean show) {
    final ButtonTabComponent	button;
    String			title;

    title = disambiguateTitle(tab.getTitle());
    tab.setActualTitle(title);
    tab.setOwner(getOwner());
    tab.setFrameTitle(title);
    addTab(title, tab);

    // icon
    button = (ButtonTabComponent) getTabComponentAt(getTabCount() - 1);
    button.setIcon((tab.getTabIcon() == null) ? null : ImageManager.getIcon(tab.getTabIcon()));
    button.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (MouseUtils.isRightClick(e)) {
          JPopupMenu menu = tab.createPopupMenu();
          menu.show(button, e.getX(), e.getY());
          e.consume();
        }
        else if (MouseUtils.isLeftClick(e)) {
          setSelectedComponent(tab);
	}
        else {
          super.mouseClicked(e);
        }
      }
    });

    if (show)
      setSelectedIndex(getTabCount() - 1);
  }

  /**
   * Creates a copy of the current tab.
   */
  public void copySelectedTab() {
    copyTabAt(getSelectedIndex());
  }

  /**
   * Creates a copy of the specified tab.
   *
   * @param index	the index of the tab to copy
   */
  public void copyTabAt(int index) {
    AbstractInvestigatorTab 	tab;
    AbstractInvestigatorTab 	tabNew;
    MessageCollection 		errors;

    if (index == -1)
      return;

    tab    = (AbstractInvestigatorTab) getComponentAt(index);
    errors = new MessageCollection();
    try {
      tabNew = tab.getClass().getDeclaredConstructor().newInstance();
      addTab(tabNew);
      tabNew.deserialize(ClassManager.getSingleton().deepCopy(tab.serialize(new HashSet<>(Arrays.asList(SerializationOption.values())))), errors);
    }
    catch (Exception ex) {
      errors.add("Failed to copy tab!", ex);
    }
    if (!errors.isEmpty())
      GUIHelper.showErrorMessage(
	InvestigatorTabbedPane.this, "Errors occurred when copying tab:\n" + errors);
  }

  /**
   * Prompts the user to enter a new name for the tab.
   *
   * @param index	the index of the tab to rename
   */
  public void renameTabAt(int index) {
    AbstractInvestigatorTab tab = ((AbstractInvestigatorTab) getComponentAt(index));
    String newTitle = GUIHelper.showInputDialog(this, "Please enter new name:", tab.getActualTitle());
    if (newTitle == null)
      return;
    if (newTitle.isEmpty())
      newTitle = tab.getTitle();
    newTitle = disambiguateTitle(newTitle);
    tab.setActualTitle(newTitle);
    setTitleAt(index, newTitle);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    removeAll();
  }
}
