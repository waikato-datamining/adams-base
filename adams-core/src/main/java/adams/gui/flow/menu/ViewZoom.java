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
 * ViewZoom.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Sets the zoom level of the flow.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ViewZoom
  extends AbstractFlowEditorSubMenuAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Zoom";
  }
  
  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.hasCurrentPanel());
  }

  /**
   * Creates the submenu.
   */
  @Override
  public JMenu createMenu() {
    JMenu	result;
    JMenuItem	menuitem;
    int[]	zooms;
    String[]	shortcuts;
    int		i;

    result = new JMenu("Zoom");

    zooms = new int[]{
      100,
      125,
      150,
      175,
      200
    };
    shortcuts = new String[]{
      "ctrl shift 1",
      "ctrl shift 2",
      "ctrl shift 3",
      "ctrl shift 4",
      "ctrl shift 5",
    };
    for (i = 0; i < zooms.length; i++) {
      final int zoom = zooms[i];
      menuitem = new JMenuItem(zoom + "%");
      menuitem.setAccelerator(KeyStroke.getKeyStroke(shortcuts[i]));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  m_State.getCurrentPanel().setZoom(zoom / 100.0);
	}
      });
      result.add(menuitem);
    }

    return result;
  }
}
