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
 * ClearGraphicalOutput.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.multipageaction;

import adams.gui.flow.FlowMultiPagePane;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;

/**
 * Clear graphical output.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ClearGraphicalOutput
  extends AbstractMultiPageMenuItem {

  private static final long serialVersionUID = 1297273340581059101L;

  /**
   * The name for the menu item.
   *
   * @return		the name
   */
  public String getName() {
    return "Clear graphical output";
  }

  /**
   * Creates the menu item.
   */
  public JMenuItem getMenuItem(FlowMultiPagePane multi) {
    JMenuItem 	result;

    result = new JMenuItem(getName());
    result.setEnabled(
      multi.hasCurrentPanel()
	&& !multi.getCurrentPanel().isRunning()
	&& !multi.getCurrentPanel().isStopping()
	&& !multi.getCurrentPanel().isSwingWorkerRunning()
	&& (multi.getCurrentPanel().getLastFlow() != null));
    result.addActionListener((ActionEvent ae) -> multi.getCurrentPanel().cleanUp());

    return result;
  }
}
