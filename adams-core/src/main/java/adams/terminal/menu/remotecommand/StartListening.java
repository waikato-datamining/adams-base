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
 * StartListening.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.terminal.menu.remotecommand;

import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.scripting.engine.DefaultScriptingEngine;
import adams.scripting.engine.RemoteScriptingEngine;
import adams.terminal.application.AbstractTerminalApplication;
import adams.terminal.dialog.ComponentDialog;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.BorderLayout.Location;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window.Hint;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import java.util.Arrays;

/**
 * Starts a remote engine.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StartListening
  extends AbstractRemoteCommandAction {

  /**
   * Initializes the action with no owner.
   */
  public StartListening() {
    super();
  }

  /**
   * Initializes the action.
   *
   * @param owner	the owning application
   */
  public StartListening(AbstractTerminalApplication owner) {
    super(owner);
  }

  @Override
  public String getTitle() {
    return "Start listening...";
  }

  /**
   * Actual execution.
   *
   * @param context	the context to use
   */
  @Override
  protected void doRun(WindowBasedTextGUI context) {
    TextBox engine;
    Panel panel;
    ComponentDialog dialog;
    MessageDialogButton retVal;

    panel = new Panel();
    panel.setLayoutManager(new BorderLayout());

    panel.addComponent(new Label("Engine"), Location.TOP);
    engine = new TextBox(new TerminalSize(20, 3));
    engine.setText(new DefaultScriptingEngine().toCommandLine());
    panel.addComponent(engine, Location.CENTER);

    dialog = new ComponentDialog("Start listening", null, panel);
    dialog.setSize(context, 0.75, 0.5);
    dialog.setHints(Arrays.asList(Hint.FIXED_SIZE, Hint.CENTERED));
    retVal = dialog.showDialog(context);
    if (retVal != MessageDialogButton.OK)
      return;

    try {
      getOwner().setRemoteScriptingEngine((RemoteScriptingEngine) OptionUtils.forAnyCommandLine(RemoteScriptingEngine.class, engine.getText()));
    }
    catch (Exception e) {
      MessageDialog.showMessageDialog(context, "Error", "Failed to start listening!\n" + Utils.throwableToString(e), MessageDialogButton.OK);
    }
  }
}
