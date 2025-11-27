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
 * DirChangeMonitor.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.core.io.dirchanged;

import java.io.File;

/**
 * Interface for directory change monitors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface DirChangeMonitor {

  /**
   * Checks whether the monitor has been initialized with the specified dir.
   *
   * @param dir		the dir to check
   * @return		true if setup for this file
   * @see		#initialize(File)
   */
  public boolean isInitialized(File dir);

  /**
   * Initializes the monitor with the specified dir.
   *
   * @param dir		the dir to initialize with
   * @return		null if successful, otherwise error message
   */
  public String initialize(File dir);

  /**
   * Returns the dir that is being monitored.
   *
   * @return		the dir, null if not initialized
   */
  public File getMonitoredDir();

  /**
   * Checks whether the dir has changed. Must be initialized beforehand.
   *
   * @param dir		the fildire to check
   * @return		true if changed
   * @see		#isInitialized(File)
   * @see		#initialize(File)
   */
  public boolean hasChanged(File dir);

  /**
   * Updates the monitor with the specified dir.
   *
   * @param dir		the dir to update with
   * @return		null if successful, otherwise error message
   */
  public String update(File dir);
}
