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
 * PID.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.shutdown;

import adams.core.management.ProcessUtils;

/**
 * Just outputs message with PID on stdout.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PID
  extends AbstractShutdownHook {

  private static final long serialVersionUID = -4069724495805072093L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Just outputs message with PID on stdout.";
  }

  /**
   * Configures the runnable that gets executed when shutting down.
   *
   * @return		the runnable
   */
  @Override
  public Runnable configure() {
    return () -> {
      System.out.println("Shutting down PID " + ProcessUtils.getVirtualMachinePID());
    };
  }
}
