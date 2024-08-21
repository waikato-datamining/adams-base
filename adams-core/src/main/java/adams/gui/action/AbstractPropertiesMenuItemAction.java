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
 * AbstractPropertiesMenuItemAction.java
 * Copyright (C) 2014-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.action;

import adams.gui.core.ImageManager;

import javax.swing.JMenuItem;
import java.awt.Dialog;

/**
 * Ancestor for simple menu item actions.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of state
 * @param <D> the type of dialog
 */
public abstract class AbstractPropertiesMenuItemAction<T, D extends Dialog>
  extends AbstractPropertiesAction<T, D> {
  
  /** for serialization. */
  private static final long serialVersionUID = -6842831257705457783L;

  /**
   * Creates a new menuitem using itself.
   */
  @Override
  public JMenuItem getMenuItem() {
    JMenuItem	result;
    
    result = new JMenuItem(this);
    if (getIcon() != null)
      result.setIcon(getIcon());
    else
      result.setIcon(ImageManager.getEmptyIcon());
    if (hasMnemonic())
      result.setMnemonic(getMnemonic());
    if (hasAccelerator())
      result.setAccelerator(getAccelerator());
    
    return result;
  }
}
