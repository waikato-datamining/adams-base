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
 * FileChangeMonitor.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filechanged;

import java.io.File;

/**
 * Interface for file change monitors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface FileChangeMonitor {

  /**
   * Checks whether the monitor has been initialized with the specified file.
   *
   * @param file	the file to check
   * @return		true if setup for this file
   * @see		#initialize(File)
   */
  public boolean isInitialized(File file);

  /**
   * Initializes the monitor with the specified file.
   *
   * @param file	the file to initialize with
   * @return		null if successful, otherwise error message
   */
  public String initialize(File file);

  /**
   * Returns the file that is being monitored.
   *
   * @return		the file, null if not initialized
   */
  public File getMonitoredFile();

  /**
   * Checks whether the file has changed. Must be initialized beforehand.
   *
   * @param file	the file to check
   * @return		true if changed
   * @see		#isInitialized(File)
   * @see		#initialize(File)
   */
  public boolean hasChanged(File file);

  /**
   * Updates the monitor with the specified file.
   *
   * @param file	the file to update with
   * @return		null if successful, otherwise error message
   */
  public String update(File file);
}
