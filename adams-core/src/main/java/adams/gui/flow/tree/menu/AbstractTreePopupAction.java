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
 * AbstractTreePopupAction.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import adams.core.ClassLister;
import adams.gui.action.AbstractBaseAction;
import adams.gui.core.GUIHelper;
import adams.gui.flow.FlowEditorPanel;
import adams.gui.flow.tree.StateContainer;

/**
 * Ancestor for menu items in the popup menu of the flow tree.
 * 
 * @author fracpete
 * @version $Revision$
 */
public abstract class AbstractTreePopupAction
  extends AbstractBaseAction {

  /** for serialization. */
  private static final long serialVersionUID = -5921557331961517641L;

  /** the state reference. */
  protected StateContainer m_State;
  
  /**
   * Initializes the action.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_State = null;
    
    setName(getTitle());
    if (getTreeShortCutKey() != null)
      setAccelerator(FlowEditorPanel.getTreeShortcut(getTreeShortCutKey()));
    if (getIconName() != null)
      setIcon(GUIHelper.getIcon(getIconName()));
  }
  
  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  protected abstract String getTitle();
  
  /**
   * Returns the name of the icon to use.
   * 
   * @return		the name, null if not applicable
   */
  protected String getIconName() {
    return null;
  }
  
  /**
   * Returns the key for the tree shortcut in the properties file.
   * 
   * @return		the key, null if not applicable
   * @see		FlowEditorPanel#getTreeShortcut(String)
   */
  protected String getTreeShortCutKey() {
    return null;
  }
  
  /**
   * Checks whether the keystroke matches.
   * 
   * @param ks		the keystroke to match
   * @return		true if a match
   */
  public boolean keyStrokeApplies(KeyStroke ks) {
    return hasAccelerator() && ks.equals(getAccelerator());
  }
  
  /**
   * Creates a new menuitem using itself.
   */
  public abstract JMenuItem getMenuItem();
  
  /**
   * Updates the action using the current state information.
   */
  protected abstract void doUpdate();

  /**
   * Updates the action using the provided state information.
   * 
   * @param state	the current state of the tree
   */
  public void update(final StateContainer state) {
    m_State = state;
    doUpdate();
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  public abstract void actionPerformed(ActionEvent e);

  /**
   * Returns a list with classnames of menu items.
   *
   * @return		the menu item classnames
   */
  public static String[] getActions() {
    return ClassLister.getSingleton().getClassnames(AbstractTreePopupAction.class);
  }
}
