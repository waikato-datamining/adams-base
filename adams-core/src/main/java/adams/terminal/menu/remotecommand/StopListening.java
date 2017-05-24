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
 * StopListening.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.terminal.menu.remotecommand;

import adams.terminal.application.AbstractTerminalApplication;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

/**
 * Stops the remote engine.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StopListening
  extends AbstractRemoteCommandAction {

  /**
   * Initializes the action with no owner.
   */
  public StopListening() {
    super();
  }

  /**
   * Initializes the action.
   *
   * @param owner	the owning application
   */
  public StopListening(AbstractTerminalApplication owner) {
    super(owner);
  }

  /**
   * Returns the title of the action.
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Stop listening";
  }

  /**
   * Actual execution.
   *
   * @param context	the context to use
   */
  @Override
  protected void doRun(WindowBasedTextGUI context) {
    getOwner().setRemoteScriptingEngine(null);
  }
}
