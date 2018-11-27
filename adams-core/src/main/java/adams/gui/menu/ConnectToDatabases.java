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
 * Copyright (C) 2009-2018 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.application.ChildFrame;
import adams.gui.application.UserMode;
import adams.gui.core.BaseButton;
import adams.gui.dialog.DatabaseConnectionsPanel;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

/**
 * Connects to the database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ConnectToDatabases
  extends AbstractBasicMenuItemDefinition {

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
    final DatabaseConnectionsPanel	panelDB;
    JPanel			        panel;
    ChildFrame 			        frame;
    final BaseButton                    buttonClose;
    final BaseButton                    buttonDisconnect;

    panelDB = new DatabaseConnectionsPanel();
    panelDB.addChangeListener((ChangeEvent e) -> getOwner().createTitle(getTitle()));

    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonDisconnect = new BaseButton("Disconnect all");
    buttonDisconnect.setMnemonic('D');
    buttonDisconnect.addActionListener((ActionEvent e) -> panelDB.disconnectConnections());
    panel.add(buttonDisconnect);

    buttonClose = new BaseButton("Close");
    buttonClose.setMnemonic('l');
    buttonClose.addActionListener((ActionEvent e) -> panelDB.closeParent());
    panel.add(buttonClose);
    panelDB.add(panel, BorderLayout.SOUTH);

    frame = createChildFrame(panelDB);
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
    return "Databases";
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