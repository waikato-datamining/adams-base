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
 * PackageManager.java
 * Copyright (C) 2010-2020 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.ClassLister;
import adams.core.logging.LoggingLevel;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.UserMode;
import adams.gui.core.ConsolePanel.ConsolePanelOutputStream;
import adams.gui.core.GUIHelper;
import adams.gui.core.GUIPrompt;
import adams.gui.dialog.ApprovalDialog;
import weka.core.WekaPackageManager;

import java.io.PrintStream;

/**
 * Opens the WEKA PackageManager.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class PackageManager
  extends AbstractWekaMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 3941702242700593202L;

  public static final String PROMPT = "StaticClassDiscoveryPackageManagerPrompted";

  /**
   * Initializes the menu item with no owner.
   */
  public PackageManager() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public PackageManager(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    int		retVal;

    retVal = ApprovalDialog.APPROVE_OPTION;
    if (ClassLister.getSingleton().isStatic() && !GUIPrompt.getSingleton().getBoolean(PROMPT, false)) {
      retVal = GUIHelper.showConfirmMessage(
        null,
        "Class discovery is set to static use. If you want to use additional "
	  + "Weka packages, you need to enable dynamic discovery again.\n"
	  + "See adams-core-manual.pdf, chapter 'Applications without dynamic class discovery'.");
      GUIPrompt.getSingleton().setBoolean(PROMPT, (retVal == ApprovalDialog.APPROVE_OPTION));
    }
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;
    WekaPackageManager.establishCacheIfNeeded(new PrintStream(new ConsolePanelOutputStream(LoggingLevel.INFO)));
    createChildFrame(new weka.gui.PackageManager(), GUIHelper.getDefaultDialogDimension());
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "WEKA Package manager";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return true;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  @Override
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