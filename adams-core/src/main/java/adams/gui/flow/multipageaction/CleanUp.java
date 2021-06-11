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
 * CleanUp.java
 * Copyright (C) 2018-2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.multipageaction;

import adams.gui.flow.FlowMultiPagePane;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;

/**
 * Cleans up.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CleanUp
  extends AbstractMultiPageMenuItem {

  private static final long serialVersionUID = 1297273340581059101L;

  /**
   * The name for the menu item.
   *
   * @return		the name
   */
  public String getName() {
    return "Clean up";
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
    return "close_window.png";
  }

  /**
   * Creates the menu item.
   */
  public JMenuItem getMenuItem(FlowMultiPagePane multi) {
    JMenuItem 	result;

    result = new JMenuItem(getName());
    result.setIcon(getIcon());
    result.setEnabled(
      ((multi.getSelectedIndices().length == 1)
        && multi.hasCurrentPanel()
        && !multi.getCurrentPanel().isRunning()
        && !multi.getCurrentPanel().isStopping()
        && !multi.getCurrentPanel().isSwingWorkerRunning()
        && (multi.getCurrentPanel().getLastFlow() != null))
        || (multi.getSelectedIndices().length > 1));
    result.addActionListener((ActionEvent ae) -> {
      for (int index: multi.getSelectedIndices()) {
	multi.getPanelAt(index).clearNotification();
	multi.getPanelAt(index).cleanUp();
      }
    });

    return result;
  }
}
