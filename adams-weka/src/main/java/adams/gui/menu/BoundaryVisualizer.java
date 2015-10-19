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
 * BoundaryVisualizer.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.UserMode;

import javax.swing.JFileChooser;
import java.io.File;

/**
 * Displays data in the boundary visualizer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see weka.gui.boundaryvisualizer.BoundaryVisualizer
 */
public class BoundaryVisualizer
  extends AbstractWekaMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -771667287275117680L;

  /** filechooser for BoundaryVisualizers. */
  protected JFileChooser m_FileChooser;

  /**
   * Initializes the menu item with no owner.
   */
  public BoundaryVisualizer() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public BoundaryVisualizer(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Initializes members.
   */
  protected void initialize() {
    super.initialize();

    m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
  }

  /**
   * Launches the functionality of the menu item.
   */
  public void launch() {
    createChildFrame(new weka.gui.boundaryvisualizer.BoundaryVisualizer(), 800, 600);
    weka.gui.boundaryvisualizer.BoundaryVisualizer.setExitIfNoWindowsOpen(false);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  public String getTitle() {
    return "[Weka] Boundary visualizer";
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
    return UserMode.BASIC;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_VISUALIZATION;
  }
}