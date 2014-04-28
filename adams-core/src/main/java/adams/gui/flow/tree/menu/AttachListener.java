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
 * AttachListener.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree.menu;

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import adams.core.Pausable;
import adams.flow.core.ActorUtils;
import adams.flow.execution.AbstractBreakpoint;
import adams.flow.execution.Debug;
import adams.flow.execution.FlowExecutionListener;
import adams.flow.execution.PathBreakpoint;
import adams.gui.flow.tree.StateContainer;
import adams.gui.goe.GenericObjectEditorDialog;

/**
 * Allows the ataching of flow execution listeners.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AttachListener
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
    
    result = new JMenuItem("Attach listener...");
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
	return "AttachListener";
      }
      @Override
      public boolean stateApplies(StateContainer state) {
	return (state.numSel == 1) && (state.runningFlow != null) && (state.runningFlow instanceof Pausable) && (((Pausable) state.runningFlow).isPaused());
      }
      @Override
      protected void doExecute(StateContainer state) {
	GenericObjectEditorDialog	dialog;
	Debug				debug;
	PathBreakpoint			pbreak;
	
	pbreak = new PathBreakpoint();
	pbreak.setPath(state.selNode.getFullName());
	if (ActorUtils.isSource(state.selNode.getActor()))
	  pbreak.setOnPostOutput(true);
	if (ActorUtils.isTransformer(state.selNode.getActor()))
	  pbreak.setOnPreInput(true);
	if (ActorUtils.isSink(state.selNode.getActor()))
	  pbreak.setOnPreInput(true);
	debug = new Debug();
	debug.setBreakpoints(new AbstractBreakpoint[]{
	    pbreak
	});
	
	if ((state.tree != null) && (state.tree.getParentDialog() != null))
	  dialog = new GenericObjectEditorDialog(state.tree.getParentDialog(), ModalityType.DOCUMENT_MODAL);
	else if ((state.tree != null) && (state.tree.getParentFrame() != null))
	  dialog = new GenericObjectEditorDialog(state.tree.getParentFrame(), true);
	else
	  dialog = new GenericObjectEditorDialog((Dialog) null, ModalityType.DOCUMENT_MODAL);
	dialog.setTitle("Attach listener [" + state.selNode.getFullName() + "]");
	dialog.getGOEEditor().setClassType(FlowExecutionListener.class);
	dialog.getGOEEditor().setCanChangeClassInDialog(true);
	dialog.setCurrent(debug);
	dialog.setLocationRelativeTo(state.tree);
	dialog.setVisible(true);
	if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
	  return;
	state.runningFlow.startListeningAtRuntime((FlowExecutionListener) dialog.getCurrent());
      }
    };
  }
}
