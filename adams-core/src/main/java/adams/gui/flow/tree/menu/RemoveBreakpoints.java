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
 * RemoveBreakpoints.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.tree.TreePath;

import adams.gui.flow.tree.StateContainer;

/**
 * For removing breakpoints either below currently selected node or everywhere
 * (if no actor selected).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoveBreakpoints
  extends AbstractTreePopupMenuItem {

  /** for serialization. */
  private static final long serialVersionUID = -5266406657520363873L;

  /**
   * Creates the menuitem to add to the menus.
   * 
   * @param state	the current state of the tree
   * @return		the menu item, null if not possible to use
   */
  @Override
  protected JMenuItem getMenuItem(final StateContainer state) {
    JMenuItem	result;
    
    result = new JMenuItem("Remove Breakpoints");
    result.setEnabled(getShortcut().stateApplies(state));
    result.setAccelerator(getShortcut().getKeyStroke());
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	getShortcut().execute(state);
      }
    });
    
    return result;
  }

  /**
   * Creates the associated shortcut.
   * 
   * @return		the shortcut, null if not used
   */
  @Override
  protected AbstractTreeShortcut newShortcut() {
    return new AbstractTreeShortcut() {
      private static final long serialVersionUID = -7897333416159785241L;
      @Override
      protected String getTreeShortCutKey() {
	return "RemoveBreakpoints";
      }
      @Override
      public boolean stateApplies(StateContainer state) {
	return state.editable;
      }
      @Override
      protected void doExecute(StateContainer state) {
	TreePath path = state.selPath;
	state.tree.processActor(path, new adams.flow.processor.RemoveBreakpoints());
      }
    };
  }
}
