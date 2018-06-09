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
 * ViewShowSource.java
 * Copyright (C) 2014-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.data.io.output.DefaultFlowWriter;
import adams.gui.dialog.TextDialog;

import java.awt.event.ActionEvent;
import java.io.StringWriter;

/**
 * Displays the source of the flow.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ViewShowSource
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
    return "Show source...";
  }
  
  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Runnable	runnable;

    runnable = () -> {
      StringWriter swriter = new StringWriter();
      DefaultFlowWriter writer = new DefaultFlowWriter();
      writer.setUseCompact(true);
      writer.write(m_State.getCurrentTree().getRootNode(), swriter);
      String buffer = swriter.toString();
      TextDialog dialog;
      if (getParentDialog() != null)
	dialog = new TextDialog(getParentDialog());
      else
	dialog = new TextDialog(getParentFrame());
      dialog.setDialogTitle(
	m_State.getCurrentPanel().getTitleGenerator().generate(
	  m_State.getCurrentFile(), m_State.getCurrentTree().isModified()) + " [Source]");
      dialog.setTabSize(2);
      dialog.setContent(buffer);
      dialog.setLocationRelativeTo(m_State);
      dialog.setVisible(true);
    };
    m_State.getCurrentPanel().startBackgroundTask(runnable, "Generating source...", true);
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(
	   m_State.hasCurrentPanel()
	&& !m_State.isSwingWorkerRunning());
  }
}
