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
 * NoChange.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.core.io.dirchanged;

import java.io.File;

/**
 * Dummy, never reports a change.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NoChange
  extends AbstractDirChangeMonitor {

  private static final long serialVersionUID = 7861456311356953324L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, never reports a change.";
  }

  /**
   * Performs the actual initialization of the monitor with the specified dir.
   *
   * @param dir		the dir to initialize with
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doInitialize(File dir) {
    return null;
  }

  /**
   * Performs the actual check whether the dir has changed.
   *
   * @param dir		the dir to check
   * @return		true if changed
   */
  @Override
  protected boolean checkChange(File dir) {
    return false;
  }

  /**
   * Performs the actual updating of the monitor with the specified dir.
   *
   * @param dir		the dir to update with
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doUpdate(File dir) {
    return null;
  }
}
