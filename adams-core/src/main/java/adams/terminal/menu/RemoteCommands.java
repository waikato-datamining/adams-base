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
 * RemoteCommands.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.terminal.menu;

import adams.core.ClassLister;
import adams.terminal.menu.remotecommand.AbstractRemoteCommandAction;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Remote commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteCommands
  extends AbstractMenuItemDefinition {

  private static final long serialVersionUID = 4850496199819749023L;

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Remote commands";
  }

  /**
   * Actual execution.
   *
   * @param context	the context to use
   */
  protected void doRun(final WindowBasedTextGUI context) {
    ActionListDialogBuilder		builder;
    List<AbstractRemoteCommandAction> 	actions;
    AbstractRemoteCommandAction		action;
    Class[] 				classes;

    builder = new ActionListDialogBuilder();
    classes = ClassLister.getSingleton().getClasses(AbstractRemoteCommandAction.class);
    actions = new ArrayList<>();
    for (Class cls: classes) {
      try {
	action = (AbstractRemoteCommandAction) cls.newInstance();
	action.setOwner(getOwner());
	actions.add(action);
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate remote command action: " + cls.getName());
	e.printStackTrace();
      }
    }
    Collections.sort(actions);

    for (AbstractRemoteCommandAction act: actions)
      builder.addAction(act.getTitle(), act.getRunnable(context));
    builder.build().showDialog(context);
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
}
