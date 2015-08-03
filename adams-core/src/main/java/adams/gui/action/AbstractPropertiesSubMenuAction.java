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
 * AbstractPropertiesSubMenuAction.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.action;

import adams.gui.core.GUIHelper;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Dialog;

/**
 * Ancestor for actions in the flow editor that generate a submenu.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of state
 * @param <D> the type of dialog
 */
public abstract class AbstractPropertiesSubMenuAction<T, D extends Dialog>
  extends AbstractPropertiesAction<T, D> {

  /** for serialization. */
  private static final long serialVersionUID = 1168747259624542350L;

  /**
   * Creates the submenu.
   */
  public abstract JMenu createMenu();

  /**
   * Creates a new menuitem.
   */
  @Override
  public JMenuItem getMenuItem() {
    JMenuItem	result;

    result = createMenu();
    if (getIcon() != null)
      result.setIcon(getIcon());
    else
      result.setIcon(GUIHelper.getEmptyIcon());

    return result;
  }
}
