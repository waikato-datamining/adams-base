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
 * HelpActors.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.core.ClassLister;
import adams.flow.core.Actor;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.Child;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.tools.ClassHelpPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

/**
 * Shows help on actors.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class HelpActors
  extends AbstractFlowEditorMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Actors";
  }
  
  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    ClassHelpPanel	panel;
    Child		child;
    ApprovalDialog	dialog;

    panel = new ClassHelpPanel();
    panel.setListedClassNames(ClassLister.getSingleton().getClassnames(Actor.class));

    child = GUIHelper.getParentChild(m_State);
    if ((child != null) && (child.getParentFrame() != null)) {
      AbstractApplicationFrame.createChildFrame(
	child.getParentFrame(),
	getTitle(),
	panel,
	GUIHelper.getDefaultDialogDimension(),
	"actor.gif");
    }
    else {
      if (getParentDialog() != null)
	dialog = ApprovalDialog.getInformationDialog(getParentDialog());
      else
        dialog = ApprovalDialog.getInformationDialog(getParentFrame());
      dialog.setTitle(getTitle());
      dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
      dialog.getContentPane().add(panel, BorderLayout.CENTER);
      dialog.setLocationRelativeTo(null);
      dialog.setVisible(true);
    }
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(true);
  }
}
