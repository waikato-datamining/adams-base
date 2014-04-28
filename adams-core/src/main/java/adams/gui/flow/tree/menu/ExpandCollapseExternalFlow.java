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
 * ExpandCollapseExternalFlow.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import adams.core.io.FlowFile;
import adams.flow.core.AbstractExternalActor;
import adams.gui.core.GUIHelper;
import adams.gui.flow.tree.StateContainer;

/**
 * For expanding/collapsing of an external flow.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExpandCollapseExternalFlow
  extends AbstractTreePopupMenuItem {

  /** for serialization. */
  private static final long serialVersionUID = 2861368330653134074L;

  /**
   * Creates the menuitem to add to the menus.
   * 
   * @param state	the current state of the tree
   * @return		the menu item, null if not possible to use
   */
  @Override
  protected JMenuItem getMenuItem(final StateContainer state) {
    JMenuItem	result;
    
    if ((state.selNode != null) && state.selNode.getExpansionOccurred()) {
      result = new JMenuItem("Collapse");
      result.setIcon(GUIHelper.getIcon("collapse.png"));
      result.setEnabled(getShortcut().stateApplies(state));
      result.setAccelerator(getShortcut().getKeyStroke());
      result.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          getShortcut().execute(state);
        }
      });
    }
    else {
      result = new JMenuItem("Expand");
      result.setIcon(GUIHelper.getIcon("expand.png"));
      result.setEnabled(getShortcut().stateApplies(state));
      result.setAccelerator(getShortcut().getKeyStroke());
      result.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          getShortcut().execute(state);
        }
      });
    }
    
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
	return "ExternalFlow.CollapseExpand";
      }
      @Override
      public boolean stateApplies(StateContainer state) {
	boolean result = state.isSingleSel && (state.selNode.getActor() instanceof AbstractExternalActor);
	if (result) {
	  FlowFile file = ((AbstractExternalActor) state.selNode.getActor()).getActorFile();
	  result = file.exists() && !file.isDirectory();
	}
	return result;
      }
      @Override
      protected void doExecute(StateContainer state) {
	if (state.selNode.getExpansionOccurred())
	  state.selNode.collapse();
	else
	  state.selNode.expand();
      }
    };
  }
}
