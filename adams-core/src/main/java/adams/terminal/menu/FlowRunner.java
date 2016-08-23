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
 * FlowRunner.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.terminal.menu;

import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.FileDialog;
import com.googlecode.lanterna.gui2.dialogs.FileDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import java.io.File;

/**
 * Lets the user select a flow and run it.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowRunner
  extends AbstractMenuItemDefinition {

  private static final long serialVersionUID = 4850496199819749023L;

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Flow runner";
  }


  /**
   * Actual execution.
   *
   * @param context	the context to use
   */
  protected void doRun(WindowBasedTextGUI context) {
    FileDialogBuilder	builder;
    FileDialog 		dialog;
    File 		file;
    Actor		flow;
    MessageDialogButton	retVal;
    String		msg;

    builder = new FileDialogBuilder();
    builder.setTitle("Open");
    builder.setDescription("Select a flow");
    builder.setActionLabel("Open");
    dialog = builder.build();
    file   = dialog.showDialog(context);
    if (file == null)
      return;

    flow = ActorUtils.read(file.getAbsolutePath());
    if (!(flow instanceof Flow)) {
      MessageDialog.showMessageDialog(context, getTitle(),
	"Root actor is not a " + Flow.class.getName() + ", cannot execute!");
      return;
    }

    retVal = MessageDialog.showMessageDialog(context, getTitle(),
      "Execute flow?", MessageDialogButton.Yes, MessageDialogButton.No);
    if ((retVal == null) || (retVal == MessageDialogButton.No))
      return;

    ((Flow) flow).setHeadless(true);
    msg = flow.setUp();
    if (msg != null) {
      MessageDialog.showMessageDialog(context, getTitle(),
	"Failed to setup flow:\n" + msg);
      flow.cleanUp();
      return;
    }
    msg = flow.execute();
    if (msg != null) {
      MessageDialog.showMessageDialog(context, getTitle(),
	"Failed to execute flow:\n" + msg);
      flow.cleanUp();
      return;
    }
    flow.cleanUp();
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
