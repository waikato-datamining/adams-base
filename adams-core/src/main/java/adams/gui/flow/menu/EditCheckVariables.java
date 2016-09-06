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
 * EditCheckVariables.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.flow.processor.CheckVariableUsage;
import adams.gui.core.BaseDialog;
import adams.gui.core.GUIHelper;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Checks the variable usage.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EditCheckVariables
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
    return "Check variables";
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    CheckVariableUsage	processor;
    final BaseDialog	dialog;
    JPanel		panel;
    JButton		button;

    processor = new CheckVariableUsage();
    processor.process(m_State.getCurrentFlow());
    if (processor.hasGraphicalOutput()) {
      if (getParentDialog() != null)
	dialog = new BaseDialog(getParentDialog());
      else
	dialog = new BaseDialog(getParentFrame());
      dialog.setTitle(processor.getClass().getSimpleName());
      dialog.getContentPane().setLayout(new BorderLayout());
      dialog.getContentPane().add(processor.getGraphicalOutput(), BorderLayout.CENTER);
      button = new JButton("Close");
      button.setMnemonic('C');
      button.addActionListener(new ActionListener() {
        @Override
	public void actionPerformed(ActionEvent e) {
          dialog.setVisible(false);
        }
      });
      panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      panel.add(button);
      dialog.getContentPane().add(panel, BorderLayout.SOUTH);
      dialog.setSize(GUIHelper.getDefaultSmallDialogDimension());
      dialog.setLocationRelativeTo(m_State);
      dialog.setVisible(true);
    }
    else {
      m_State.getCurrentPanel().showNotification("Basic check passed!\nAll variables get at least set once in the flow.", false);
    }
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(
	   m_State.hasCurrentPanel() 
	&& isInputEnabled());
  }
}
