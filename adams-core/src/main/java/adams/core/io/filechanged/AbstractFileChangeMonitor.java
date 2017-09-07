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
 * AbstractFileChangeMonitor.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filechanged;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;

import java.io.File;

/**
 * Ancestor for file change monitors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFileChangeMonitor
  extends AbstractOptionHandler
  implements FileChangeMonitor, QuickInfoSupporter {

  private static final long serialVersionUID = 6525849559105359877L;

  /** whether the monitor has been initialized. */
  protected boolean m_Initialized;

  /** the file to monitor. */
  protected File m_File;

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Initialized = false;
    m_File        = null;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Checks whether the monitor has been initialized with the specified file.
   *
   * @param file	the file to check
   * @return		true if setup for this file
   * @see		#initialize(File)
   */
  public boolean isInitialized(File file) {
    return m_Initialized;
  }

  /**
   * Performs some basic checks on the file.
   *
   * @param file	the file to check
   * @return		null if successful, otherwise error message
   */
  protected String checkFile(File file) {
    if (file == null)
      return "No file provided!";

    if (!file.exists())
      return "File does not exist: " + file;

    if (file.isDirectory())
      return "File points to a directory: " + file;

    return null;
  }

  /**
   * Performs the actual initialization of the monitor with the specified file.
   *
   * @param file	the file to initialize with
   * @return		null if successful, otherwise error message
   */
  protected abstract String doInitialize(File file);

  /**
   * Initializes the monitor with the specified file.
   *
   * @param file	the file to initialize with
   * @return		null if successful, otherwise error message
   */
  public String initialize(File file) {
    String	result;

    result = checkFile(file);

    if (result == null)
      result = doInitialize(file);

    if (result == null) {
      m_File        = file;
      m_Initialized = true;
    }

    if (isLoggingEnabled())
      getLogger().info("init: file=" + file + ", initialized=" + m_Initialized + ", msg=" + result);

    return result;
  }

  /**
   * Returns the file that is being monitored.
   *
   * @return		the file, null if not initialized
   */
  public File getMonitoredFile() {
    return m_File;
  }

  /**
   * Performs the actual check whether the file has changed.
   *
   * @param file	the file to check
   * @return		true if changed
   */
  protected abstract boolean checkChange(File file);

  /**
   * Checks whether the file has changed. Must be initialized beforehand.
   *
   * @param file	the file to check
   * @return		true if changed
   * @see		#isInitialized(File)
   * @see		#initialize(File)
   */
  public boolean hasChanged(File file) {
    boolean	result;

    result = isInitialized(file) && checkChange(file);

    if (isLoggingEnabled())
      getLogger().info("changed: file=" + file + ", initialized=" + m_Initialized + ", changed=" + result);

    return result;
  }

  /**
   * Performs the actual updating of the monitor with the specified file.
   *
   * @param file	the file to update with
   * @return		null if successful, otherwise error message
   */
  protected abstract String doUpdate(File file);

  /**
   * Updates the monitor with the specified file.
   *
   * @param file	the file to update with
   * @return		null if successful, otherwise error message
   */
  public String update(File file) {
    String	result;

    result = checkFile(file);
    if (result == null)
      result = doUpdate(file);

    if (isLoggingEnabled())
      getLogger().info("update: file=" + file + ", msg=" + result);

    return result;
  }
}
