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
 * CustomPopupMenuProvider.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.event.MouseEvent;

/**
 * For classes that provide a custom popup menu.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface CustomPopupMenuProvider {

  /**
   * Creates a popup menu for the given mouse event.
   * 
   * @param e		the event that triggered the request
   * @return		the menu, null if none was generated
   */
  public BasePopupMenu getCustomPopupMenu(MouseEvent e);
}
