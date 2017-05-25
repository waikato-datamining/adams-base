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
 * AbstractRemoteCommandActionWithGUI.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.terminal.menu.remotecommand;

import adams.terminal.application.AbstractTerminalApplication;
import adams.terminal.dialog.ComponentDialog;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window.Hint;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import java.util.Arrays;

/**
 * Ancestor for actions that display a GUI.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRemoteCommandActionWithGUI
  extends AbstractRemoteCommandAction {

  /**
   * Initializes the action with no owner.
   */
  public AbstractRemoteCommandActionWithGUI() {
    super();
  }

  /**
   * Initializes the action.
   *
   * @param owner	the owning application
   */
  public AbstractRemoteCommandActionWithGUI(AbstractTerminalApplication owner) {
    super(owner);
  }

  /**
   * Creates the panel to display.
   *
   * @return		the panel
   */
  protected abstract Panel createPanel();

  /**
   * Actual execution.
   *
   * @param context	the context to use
   */
  @Override
  protected void doRun(WindowBasedTextGUI context) {
    ComponentDialog.showDialog(
      context, getTitle(), null, createPanel(),
      Arrays.asList(Hint.CENTERED, Hint.FIT_TERMINAL_WINDOW, Hint.FULL_SCREEN),
      false, false, true);
  }
}
