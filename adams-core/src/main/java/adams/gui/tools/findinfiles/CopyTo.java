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
 * CopyTo.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.findinfiles;

import adams.core.MessageCollection;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.gui.chooser.BaseDirectoryChooser;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;

import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Copies the file(s) to the selected directory.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CopyTo
  extends AbstractFindInFilesAction {

  private static final long serialVersionUID = 2602074439493570865L;

  /** the directory chooser to use. */
  protected BaseDirectoryChooser m_DirChooser;

  /**
   * Returns the text for the menu item.
   *
   * @return		the text
   */
  protected String getMenuItemText() {
    return "Copy to...";
  }

  /**
   * Updates the action based on the current state of the owner.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_Owner.getSelectedFiles().length > 0);
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    int				retVal;
    File[]			files;
    PlaceholderDirectory 	dir;
    MessageCollection		errors;

    files = getOwner().getSelectedFiles();

    // select dir
    if (m_DirChooser == null)
      m_DirChooser = new BaseDirectoryChooser();
    retVal = m_DirChooser.showOpenDialog(getOwner());
    if (retVal != BaseDirectoryChooser.APPROVE_OPTION)
      return;
    dir = m_DirChooser.getSelectedDirectory();

    // confirm
    retVal = GUIHelper.showConfirmMessage(
      getOwner(),
      "Do you want to copy " + files.length + " file(s) to the following directory?\n" + dir,
      "Copying files");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    // copy
    errors = new MessageCollection();
    for (File file: files) {
      try {
	if (!FileUtils.copy(file, dir))
	  errors.add("Failed to copy '" + file + "' to '" + dir + "'");
      }
      catch (Exception ex) {
	errors.add("Failed to copy '" + file + "' to '" + dir + "'", ex);
      }
    }
    if (!errors.isEmpty())
      GUIHelper.showErrorMessage(getOwner(), errors.toString());
  }
}
