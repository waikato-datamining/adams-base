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
 * OpenContainingFolder.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.multipageaction;

import adams.core.management.FileBrowser;
import adams.gui.flow.FlowMultiPagePane;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;

/**
 * Opens the folder in the system's file browser that this flow is stored in.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class OpenContainingFolder
  extends AbstractMultiPageMenuItem {

  private static final long serialVersionUID = 1297273340581059101L;

  /**
   * The name for the menu item.
   *
   * @return		the name
   */
  public String getName() {
    return "Open containing folder";
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
    return "filebrowser.png";
  }

  /**
   * Creates the menu item.
   */
  public JMenuItem getMenuItem(FlowMultiPagePane multi) {
    JMenuItem 	result;

    result = new JMenuItem(getName());
    result.setIcon(getIcon());
    result.setEnabled(
      multi.hasCurrentPanel() && (multi.getCurrentPanel().getCurrentFile() != null));
    if (result.isEnabled()) {
      result.addActionListener((ActionEvent ae) ->
	FileBrowser.launch(multi.getCurrentPanel().getCurrentFile()));
    }

    return result;
  }
}
