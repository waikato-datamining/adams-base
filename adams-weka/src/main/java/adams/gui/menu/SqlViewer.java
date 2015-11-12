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
 * SqlViewer.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.ChildFrame;
import adams.gui.application.UserMode;

import javax.swing.JPanel;

/**
 * Opens the SQL viewer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SqlViewer
  extends AbstractWekaMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -1041273949195204507L;

  /**
   * Initializes the menu item with no owner.
   */
  public SqlViewer() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public SqlViewer(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Launches the functionality of the menu item.
   */
  public void launch() {
    JPanel dummy = new JPanel();
    ChildFrame frame = createChildFrame(dummy, 800, 600);
    weka.gui.sql.SqlViewer panel = new weka.gui.sql.SqlViewer(frame);
    dummy.getParent().remove(dummy);
    frame.getContentPane().add(panel);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  public String getTitle() {
    return "WEKA SQL Viewer";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  public boolean isSingleton() {
    return false;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  public UserMode getUserMode() {
    return UserMode.EXPERT;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_TOOLS;
  }
}