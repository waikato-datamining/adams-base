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
 * AbstractDirChangeMonitor.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.core.io.dirchanged;

import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.base.BaseRegExp;
import adams.core.option.AbstractOptionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ancestor for dir change monitors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractDirChangeMonitor
  extends AbstractOptionHandler
  implements DirChangeMonitor, QuickInfoSupporter {

  private static final long serialVersionUID = 6525849559105359877L;

  /** the regexp for the files to list. */
  protected BaseRegExp m_RegExp;

  /** whether the monitor has been initialized. */
  protected boolean m_Initialized;

  /** the dir to monitor. */
  protected File m_Dir;

  /** the list of files from the last check. */
  protected Set<File> m_LastFiles;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Initialized = false;
    m_Dir         = null;
    m_LastFiles   = null;
  }

  /**
   * Sets the regular expression for selecting the subset of files to monitor in the directory.
   *
   * @param value	the regexp
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression for selecting a subset of files to monitor.
   *
   * @return		the regexp
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression for selecting the subset of files to monitor in the directory (gets applied only to the file name, not the path).";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "regExp", m_RegExp, "regexp: ");
  }

  /**
   * Checks whether the monitor has been initialized with the specified dir.
   *
   * @param dir		the dir to check
   * @return		true if setup for this dir
   * @see		#initialize(File)
   */
  @Override
  public boolean isInitialized(File dir) {
    return m_Initialized;
  }

  /**
   * Performs some basic checks on the dir.
   *
   * @param dir		the dir to check
   * @return		null if successful, otherwise error message
   */
  protected String checkFile(File dir) {
    if (dir == null)
      return "No directory provided!";

    if (!dir.exists())
      return "Directory does not exist: " + dir;

    if (!dir.isDirectory())
      return "Does not point to a directory: " + dir;

    return null;
  }

  /**
   * Performs the actual initialization of the monitor with the specified dir.
   *
   * @param dir		the dir to initialize with
   * @return		null if successful, otherwise error message
   */
  protected abstract String doInitialize(File dir);

  /**
   * Initializes the monitor with the specified file.
   *
   * @param dir		the dir to initialize with
   * @return		null if successful, otherwise error message
   */
  @Override
  public String initialize(File dir) {
    String	result;

    result = checkFile(dir);

    if (result == null) {
      result = doInitialize(dir);
      if (result == null)
	m_LastFiles = new HashSet<>(listFiles(dir));
    }

    if (result == null) {
      m_Dir         = dir;
      m_Initialized = true;
    }

    if (isLoggingEnabled())
      getLogger().info("init: dir=" + dir + ", initialized=" + m_Initialized + ", msg=" + result);

    return result;
  }

  /**
   * Returns the dir that is being monitored.
   *
   * @return		the dir, null if not initialized
   */
  @Override
  public File getMonitoredDir() {
    return m_Dir;
  }

  /**
   * Returns a list of files in the specified directory.
   *
   * @param dir		the directory to get the files for
   * @return		the list of files
   */
  protected List<File> listFiles(File dir) {
    List<File>	result;

    result = new ArrayList<>();

    for (File file: dir.listFiles(pathname -> !pathname.isDirectory())) {
      if (m_RegExp.isMatchAll() || m_RegExp.isMatch(file.getName()))
	result.add(file);
    }

    return result;
  }

  /**
   * Fast check whether the number of files or names have changed.
   *
   * @param dir		the dir to check
   * @return		true if changed
   */
  protected boolean checkContentChanged(File dir) {
    Set<File>	files;

    files = new HashSet<>(listFiles(dir));

    return (files.size() != m_LastFiles.size())
	     || !files.containsAll(m_LastFiles)
	     || !m_LastFiles.containsAll(files);
  }

  /**
   * Performs the actual check whether the dir has changed.
   *
   * @param dir		the dir to check
   * @return		true if changed
   */
  protected abstract boolean checkChange(File dir);

  /**
   * Checks whether the dir has changed. Must be initialized beforehand.
   *
   * @param dir		the dir to check
   * @return		true if changed
   * @see		#isInitialized(File)
   * @see		#initialize(File)
   */
  public boolean hasChanged(File dir) {
    boolean	result;

    result = isInitialized(dir) && (checkContentChanged(dir) || checkChange(dir));

    if (isLoggingEnabled())
      getLogger().info("changed: dir=" + dir + ", initialized=" + m_Initialized + ", changed=" + result);

    return result;
  }

  /**
   * Performs the actual updating of the monitor with the specified dir.
   *
   * @param dir		the dir to update with
   * @return		null if successful, otherwise error message
   */
  protected abstract String doUpdate(File dir);

  /**
   * Updates the monitor with the specified dir.
   *
   * @param dir		the dir to update with
   * @return		null if successful, otherwise error message
   */
  public String update(File dir) {
    String	result;

    result = null;
    if (!m_Dir.equals(dir))
      result = "Trying to update a different directory? initialized=" + m_Dir + ", current=" + dir;
    if (result == null)
      result = checkFile(dir);
    if (result == null) {
      m_LastFiles = new HashSet<>(listFiles(dir));
      result      = doUpdate(dir);
    }

    if (isLoggingEnabled())
      getLogger().info("update: dir=" + dir + ", #files=" + m_LastFiles.size() + ", msg=" + result);

    return result;
  }
}
