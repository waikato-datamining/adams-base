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
 * Save.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.notificationareaaction;

import adams.core.io.FileUtils;
import adams.gui.chooser.TextFileChooser;
import adams.gui.core.GUIHelper;

import java.awt.event.ActionEvent;

/**
 * Saves the content to a file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Save
  extends AbstractNotificationAreaAction {

  private static final long serialVersionUID = -2884370713454014768L;

  /**
   * Instantiates the action.
   */
  public Save() {
    super();
    setName("Save...");
    setIcon(GUIHelper.getIcon("save.gif"));
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    TextFileChooser fileChooser = new TextFileChooser();
    int retval;
    if (m_Owner.getParentDialog() != null)
      retval = fileChooser.showSaveDialog(m_Owner.getParentDialog());
    else
      retval = fileChooser.showSaveDialog(m_Owner.getParentFrame());
    if (retval != TextFileChooser.APPROVE_OPTION)
      return;
    String msg = FileUtils.writeToFileMsg(
      fileChooser.getSelectedFile().getAbsolutePath(),
      m_Owner.getContent(), false, fileChooser.getEncoding());
    if (msg != null)
      GUIHelper.showErrorMessage(null, "Failed to save text to: " + fileChooser.getSelectedFile());
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(!m_Owner.getContent().isEmpty());
  }
}
