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
 * ToogleAction.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;


/**
 * A specialized action class for menu items derived from JCheckBoxMenuItem.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see JCheckBoxMenuItem
 */
public class ToggleAction
  extends AbstractBaseAction {

  /** for serialization. */
  private static final long serialVersionUID = 5065335824297443067L;

  /**
   * Defines an <code>Action</code> object with a default
   * description string and default icon.
   */
  public ToggleAction() {
    super();
  }

  /**
   * Defines an <code>Action</code> object with the specified
   * description string and a default icon.
   *
   * @param name	the description
   */
  public ToggleAction(String name) {
    super(name);
  }

  /**
   * Defines an <code>Action</code> object with the specified
   * description string and a the specified icon.
   *
   * @param name	the description
   * @param icon	the icon
   */
  public ToggleAction(String name, Icon icon) {
    super(name, icon);
  }

  /**
   * Defines an <code>Action</code> object with the specified
   * description string and a the specified icon.
   *
   * @param name	the description
   * @param icon	the icon file (without path)
   */
  public ToggleAction(String name, String icon) {
    super(name, icon);
  }

  /**
   * Dummy implementation, does nothing.
   *
   * @param e		ignored
   */
  public void actionPerformed(ActionEvent e) {
    // does nothing
  }
}
