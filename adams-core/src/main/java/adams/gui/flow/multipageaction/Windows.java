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
 * Windows.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.multipageaction;

import adams.flow.control.Flow;
import adams.gui.core.BaseMenu;
import adams.gui.flow.FlowMultiPagePane;

import javax.swing.JMenuItem;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Map;

/**
 * Displays the currently active windows.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Windows
  extends AbstractMultiPageMenuItem {

  private static final long serialVersionUID = 1297273340581059101L;

  /**
   * The name for the menu item.
   *
   * @return		the name
   */
  public String getName() {
    return "Windows";
  }

  /**
   * Creates the menu item.
   */
  public JMenuItem getMenuItem(FlowMultiPagePane multi) {
    BaseMenu 		result;
    JMenuItem		menuitem;
    Map<Window,String> 	windows;

    windows = null;
    if (multi.hasCurrentPanel()) {
      if (multi.getCurrentPanel().getRunningFlow() != null)
        windows = ((Flow) multi.getCurrentPanel().getRunningFlow().getRoot()).getWindowRegister();
      else if (multi.getCurrentPanel().getLastFlow() != null)
        windows = ((Flow) multi.getCurrentPanel().getLastFlow().getRoot()).getWindowRegister();
    }
    result = new BaseMenu(getName());
    result.setEnabled((windows != null) && windows.size() > 0);
    if (windows != null) {
      for (final Window window: windows.keySet()) {
	menuitem = new JMenuItem(windows.get(window));
	menuitem.addActionListener((ActionEvent ae) -> {
	  if (window instanceof Frame)
	    ((Frame) window).setExtendedState(Frame.NORMAL);
	  window.toFront();
	});
	result.add(menuitem);
      }
      result.sort();
    }

    return result;
  }
}
