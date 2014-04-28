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
 * EditListeners.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree.menu;

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import adams.core.Pausable;
import adams.core.ShallowCopySupporter;
import adams.core.Utils;
import adams.flow.execution.FlowExecutionListener;
import adams.gui.flow.tree.StateContainer;
import adams.gui.goe.GenericObjectEditorDialog;

/**
 * Allows the ataching of .
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EditListeners
  extends AbstractTreePopupMenuItem {

  /** for serialization. */
  private static final long serialVersionUID = -1359983192445709718L;

  /**
   * Creates the menuitem to add to the menus.
   * 
   * @param state	the current state of the tree
   * @return		the menu item, null if not possible to use
   */
  @Override
  protected JMenuItem getMenuItem(final StateContainer state) {
    JMenuItem	result;
    
    result = new JMenuItem("Edit listeners...");
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
	return "EditListeners";
      }
      @Override
      public boolean stateApplies(StateContainer state) {
	return (state.runningFlow != null) && (state.runningFlow instanceof Pausable) && (((Pausable) state.runningFlow).isPaused());
      }
      @Override
      protected void doExecute(StateContainer state) {
	GenericObjectEditorDialog	dialog;
	
	if ((state.tree != null) && (state.tree.getParentDialog() != null))
	  dialog = new GenericObjectEditorDialog(state.tree.getParentDialog(), ModalityType.DOCUMENT_MODAL);
	else if ((state.tree != null) && (state.tree.getParentFrame() != null))
	  dialog = new GenericObjectEditorDialog(state.tree.getParentFrame(), true);
	else
	  dialog = new GenericObjectEditorDialog((Dialog) null, ModalityType.DOCUMENT_MODAL);
	dialog.setTitle("Edit listeners");
	dialog.getGOEEditor().setClassType(FlowExecutionListener.class);
	dialog.getGOEEditor().setCanChangeClassInDialog(true);
	if (state.runningFlow.getFlowExecutionListener() instanceof ShallowCopySupporter)
	  dialog.setCurrent(((ShallowCopySupporter) state.runningFlow.getFlowExecutionListener()).shallowCopy());
	else
	  dialog.setCurrent(Utils.deepCopy(state.runningFlow.getFlowExecutionListener()));
	dialog.setLocationRelativeTo(state.tree);
	dialog.setVisible(true);
	if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
	  return;
	state.runningFlow.startListeningAtRuntime((FlowExecutionListener) dialog.getCurrent());
      }
    };
  }
}
