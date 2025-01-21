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
 * CopyVariableName.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.menu;

import adams.flow.core.Actor;
import adams.gui.action.AbstractPropertiesAction;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * For copying variable names to the clipboard.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class CopyVariableName
  extends AbstractTreePopupSubMenuAction {

  private static final long serialVersionUID = -5579105202655859516L;

  /**
   * Returns the caption of this action.
   *
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Copy variable name";
  }

  /**
   * Ignored.
   *
   * @return		always null
   */
  @Override
  protected AbstractPropertiesAction[] getSubMenuActions() {
    return null;
  }

  /**
   * Creates a new menu.
   */
  @Override
  public JMenu createMenu() {
    JMenu 		result;
    List<String>	names;
    JMenuItem		menuitem;

    if (m_State.selNode == null)
      return null;

    result = new JMenu(getName());
    names  = m_State.tree.getOperations().findVariableNames(m_State.selNode.getActor());
    for (String name: names) {
      menuitem = new JMenuItem(name);
      menuitem.addActionListener((ActionEvent e) -> ClipboardHelper.copyToClipboard(name));
      result.add(menuitem);
    }

    return result;
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    boolean	enabled;
    Actor 	actor;

    enabled = false;
    if (m_State.isSingleSel) {
      actor   = m_State.selNode.getActor();
      enabled = !m_State.tree.getOperations().findVariableNames(actor).isEmpty();
    }

    setEnabled(enabled);
  }
}
