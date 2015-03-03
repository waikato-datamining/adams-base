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
 * RestartableApplication.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import adams.core.option.OptionHandler;

/**
 * For applications that can be restarted via the Launcher class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Launcher
 */
public interface RestartableApplication
  extends OptionHandler {

  /** the option for enabling restarting. */
  public final static String OPTION_ENABLE_RESTART = "enable-restart";

  /** the command-line flag for enabling restarting. */
  public final static String FLAG_ENABLE_RESTART = "-" + OPTION_ENABLE_RESTART;

  /**
   * Sets whether to enable the restart through the Launcher.
   *
   * @param value	true if to enable restart via Launcher class
   */
  public void setEnableRestart(boolean value);

  /**
   * Returns whether to enable the restart through the Launcher.
   *
   * @return		true if restart is enabled
   */
  public boolean getEnableRestart();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String enableRestartTipText();
}
