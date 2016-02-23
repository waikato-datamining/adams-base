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
 * RemoteCommands.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.option.OptionUtils;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractSubMenuDefinition;
import adams.gui.application.UserMode;
import adams.gui.core.GUIHelper;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.scripting.engine.DefaultScriptingEngine;
import adams.scripting.engine.RemoteScriptingEngine;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;

/**
 * Provides submenu for starting/stopping of remote command scripting.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteCommands
  extends AbstractSubMenuDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -6548349613973153076L;

  /** the start listening menu item. */
  protected JMenuItem m_MenuItemStart;

  /** the stop listening menu item. */
  protected JMenuItem m_MenuItemStop;

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public RemoteCommands(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Remote commands...";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "remote_command.png";
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
    return CATEGORY_PROGRAM;
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
   * Allows the user to select and configure a scripting engine before
   * starting it.
   */
  protected void startScripting() {
    GenericObjectEditorDialog	dialog;

    dialog = new GenericObjectEditorDialog(getOwner(), true);
    dialog.setTitle("Scripting engine");
    dialog.getGOEEditor().setClassType(RemoteScriptingEngine.class);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    if (getOwner().getRemoteScriptingEngine() == null)
      dialog.setCurrent(new DefaultScriptingEngine());
    else
      dialog.setCurrent(OptionUtils.shallowCopy(getOwner().getRemoteScriptingEngine()));
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    getOwner().setRemoteScriptingEngine((RemoteScriptingEngine) dialog.getCurrent());
  }

  /**
   * Updates the menu items.
   */
  protected void updateMenu() {
    m_MenuItemStop.setEnabled(getOwner().getRemoteScriptingEngine() != null);
  }

  /**
   * Returns the menu items to add to the submenu.
   *
   * @return		the menu items
   */
  @Override
  protected JMenuItem[] getSubMenuItems() {
    JMenuItem[]		result;

    m_MenuItemStart = new JMenuItem("Start listening...", GUIHelper.getIcon("run.gif"));
    m_MenuItemStart.addActionListener((ActionEvent e) -> {
      startScripting();
      updateMenu();
    });

    m_MenuItemStop = new JMenuItem("Stop listening", GUIHelper.getIcon("stop_blue.gif"));
    m_MenuItemStop.addActionListener((ActionEvent e) -> {
      getOwner().setRemoteScriptingEngine(null);
      updateMenu();
    });

    updateMenu();

    result = new JMenuItem[]{m_MenuItemStart, m_MenuItemStop};

    return result;
  }
}