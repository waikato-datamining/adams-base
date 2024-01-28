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
 * AdamsHomeDir.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.management.FileBrowser;
import adams.env.Environment;
import adams.gui.application.AbstractApplicationFrame;

import java.io.File;

/**
 * Opens the ADAMS home directory in the file browser.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class AdamsHomeDir
  extends AbstractJDKMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -1502903491659697700L;

  /**
   * Initializes the menu item with no owner.
   */
  public AdamsHomeDir() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public AdamsHomeDir(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  protected void doLaunch() {
    FileBrowser.launch(new File(Environment.getInstance().getHome()));
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "ADAMS home directory";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "filebrowser.png";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return false;
  }
}