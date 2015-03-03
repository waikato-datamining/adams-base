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
 * ConnectToDatabases.java
 * Copyright (C) 2009-2011 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import java.awt.Dimension;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractMenuItemDefinition;
import adams.gui.application.ChildFrame;
import adams.gui.application.UserMode;
import adams.gui.dialog.DatabaseConnectionsPanel;

/**
 * Connects to the database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConnectToDatabases
  extends AbstractMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -1363910914896201632L;

  /**
   * Initializes the menu item with no owner.
   */
  public ConnectToDatabases() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public ConnectToDatabases(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  public String getIconName() {
    return "database.gif";
  }

  /**
   * Launches the functionality of the menu item.
   */
  public void launch() {
    DatabaseConnectionsPanel	panel;
    ChildFrame 			frame;

    panel = new DatabaseConnectionsPanel();
    panel.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	getOwner().createTitle(getTitle());
      }
    });
    frame = createChildFrame(panel);
    frame.pack();
    frame.setSize(new Dimension((int) (frame.getWidth() * 1.2), frame.getHeight()));
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  public String getTitle() {
    return "Connect to databases";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  public boolean isSingleton() {
    return true;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  public UserMode getUserMode() {
    return UserMode.BASIC;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  public String getCategory() {
    return CATEGORY_PROGRAM;
  }
}