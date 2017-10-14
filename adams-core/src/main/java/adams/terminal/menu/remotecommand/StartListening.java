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
import adams.scripting.ScriptingHelper;
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
  extends AbstractRemoteCommandActionWithGUI {

  /** the remote engine commandline. */
  protected TextBox m_Engine;

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

  /**
   * Returns the title of the action.
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Start listening...";
  }

  /**
   * Creates the panel to display.
   *
   * @return		the panel
   */
  protected Panel createPanel() {
    Panel 	result;

    result = new Panel();
    result.setLayoutManager(new BorderLayout());

    result.addComponent(new Label("Engine"), Location.TOP);
    m_Engine = new TextBox(new TerminalSize(20, 3));
    m_Engine.setText(ScriptingHelper.getSingleton().getDefaultEngine().toCommandLine());
    result.addComponent(m_Engine, Location.CENTER);

    return result;
  }

  /**
   * Actual execution.
   *
   * @param context	the context to use
   */
  @Override
  protected void doRun(WindowBasedTextGUI context) {
    ComponentDialog 		dialog;
    MessageDialogButton 	retVal;

    dialog = new ComponentDialog("Start listening", null, createPanel());
    dialog.setSize(context, 0.75, 0.5);
    dialog.setHints(Arrays.asList(Hint.FIXED_SIZE, Hint.CENTERED));
    retVal = dialog.showDialog(context);
    if (retVal != MessageDialogButton.OK)
      return;

    try {
      getOwner().setRemoteScriptingEngine(
	(RemoteScriptingEngine) OptionUtils.forAnyCommandLine(RemoteScriptingEngine.class, m_Engine.getText()));
    }
    catch (Exception e) {
      MessageDialog.showMessageDialog(
	context, "Error", "Failed to start listening!\n" + Utils.throwableToString(e), MessageDialogButton.OK);
    }
  }
}
