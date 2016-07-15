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
 * Workbench.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.io.PlaceholderFile;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.UserMode;
import adams.gui.core.GUIHelper;
import com.googlecode.jfilechooserbookmarks.core.Utils;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ConverterUtils;
import weka.gui.WorkbenchApp;

/**
 * Opens the WEKA Workbench.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Workbench
  extends AbstractParameterHandlingWekaMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 3941702242700593202L;

  /**
   * Initializes the menu item with no owner.
   */
  public Workbench() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public Workbench(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    WorkbenchApp app = new WorkbenchApp();
    createChildFrame(app, GUIHelper.getDefaultLargeDialogDimension());
    if (app.getMainPerspective().acceptsInstances()) {
      if (m_Parameters.length > 0) {
        PlaceholderFile[] files = new PlaceholderFile[m_Parameters.length];
        for (int i = 0; i < m_Parameters.length; i++)
          files[i] = new PlaceholderFile(m_Parameters[i]);
        try {
          AbstractFileLoader loader = ConverterUtils.getLoaderForFile(files[0]);
          loader.setFile(files[0].getAbsoluteFile());
          app.getMainPerspective().setInstances(loader.getDataSet());
        }
        catch (Exception e) {
          GUIHelper.showErrorMessage(getOwner(), "Failed to load: " + files[0] + "\n" + Utils.throwableToString(e));
        }
      }
    }
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "WEKA Workbench";
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

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  @Override
  public UserMode getUserMode() {
    return UserMode.BASIC;
  }
}