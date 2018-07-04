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
 * RenameFlow.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.multipageaction;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.gui.core.GUIHelper;
import adams.gui.core.RecentFilesHandlerWithCommandline.Setup;
import adams.gui.flow.FlowMultiPagePane;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Lets the user rename the flow (if saved).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RenameFlow
  extends AbstractMultiPageMenuItem {

  private static final long serialVersionUID = 1297273340581059101L;

  /**
   * The name for the menu item.
   *
   * @return		the name
   */
  public String getName() {
    return "Rename flow";
  }

  /**
   * The name of the group this item belongs to.
   *
   * @return		the name
   */
  public String getGroup() {
    return "Admin";
  }

  /**
   * The name of the icon to use.
   *
   * @return		the name
   */
  public String getIconName() {
    return "rename.png";
  }

  /**
   * Creates the menu item.
   */
  public JMenuItem getMenuItem(FlowMultiPagePane multi) {
    JMenuItem 	result;

    result = new JMenuItem(getName());
    result.setIcon(getIcon());
    result.setEnabled(
      multi.hasCurrentPanel() && (multi.getCurrentPanel().getCurrentFile() != null) && !multi.getCurrentPanel().isModified());
    if (result.isEnabled()) {
      result.addActionListener((ActionEvent ae) -> {
        File oldFile = multi.getCurrentPanel().getCurrentFile();
        String path = oldFile.getParent();
        String name = oldFile.getName();
        String ext = "." + FileUtils.getExtension(oldFile);
        name = GUIHelper.showInputDialog(multi.getOwner(), "Please enter new file name:", name);
        if (name == null)
          return;
        if (!name.endsWith(ext))
          name += ext;
        File newFile = new PlaceholderFile(path + File.separator + name);
        if (!oldFile.renameTo(newFile.getAbsoluteFile())) {
	  GUIHelper.showErrorMessage(multi.getOwner(), "Failed to rename flow from:\n" + oldFile + "\nto:\n" + newFile);
	}
        else {
	  multi.getCurrentPanel().updateCurrentFile(newFile);
	  multi.getOwner().getRecentFilesHandler().addRecentItem(
	    new Setup(newFile, multi.getOwner().getReaderForFile(newFile)));
	}
      });
    }

    return result;
  }
}
