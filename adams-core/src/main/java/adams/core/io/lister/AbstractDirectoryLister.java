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
 * AbstractDirectoryLister.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io.lister;

import adams.core.base.BaseRegExp;
import adams.core.logging.CustomLoggingLevelObject;

import java.util.logging.Level;

/**
 * Ancestor for directory listers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDirectoryLister
  extends CustomLoggingLevelObject
  implements DirectoryLister {

  /** for serialization. */
  private static final long serialVersionUID = -1846677500660003814L;

  /** the directory to watch. */
  protected String m_WatchDir;

  /** whether to list directories. */
  protected boolean m_ListDirs;

  /** whether to list files. */
  protected boolean m_ListFiles;

  /** the type of sorting to perform. */
  protected Sorting m_Sorting;

  /** whether to sort descending. */
  protected boolean m_SortDescending;

  /** the maximum number of files/dirs to return. */
  protected int m_MaxItems;

  /** the regular expression for the files/dirs to match. */
  protected BaseRegExp m_RegExp;

  /** whether to stop the currently listing. */
  protected boolean m_Stopped;

  /**
   * Initializes the object.
   */
  public AbstractDirectoryLister() {
    super();

    m_WatchDir       = ".";
    m_ListDirs       = false;
    m_ListFiles      = true;
    m_Sorting        = Sorting.NO_SORTING;
    m_SortDescending = false;
    m_MaxItems       = -1;
    m_RegExp         = new BaseRegExp("");
    m_Stopped        = false;
  }

  /**
   * Set debugging mode.
   *
   * @param value 	true if debug output should be printed
   */
  public void setDebug(boolean value) {
    getLogger().setLevel(value ? Level.INFO : Level.OFF);
  }

  /**
   * Returns whether debugging is turned on.
   *
   * @return 		true if debugging output is on
   */
  public boolean getDebug() {
    return (getLogger().getLevel() != Level.OFF);
  }

  /**
   * Sets the directory to watch.
   *
   * @param value 	the directory
   */
  public void setWatchDir(String value) {
    m_WatchDir = value;
  }

  /**
   * Returns the directory to watch.
   *
   * @return 		the directory
   */
  public String getWatchDir() {
    return m_WatchDir;
  }

  /**
   * Sets whether to list directories or not.
   *
   * @param value 	true if directories are included in the list
   */
  public void setListDirs(boolean value) {
    m_ListDirs = value;
  }

  /**
   * Returns whether to list directories or not.
   *
   * @return 		true if directories are listed
   */
  public boolean getListDirs() {
    return m_ListDirs;
  }

  /**
   * Sets whether to list files or not.
   *
   * @param value 	true if files are included in the list
   */
  public void setListFiles(boolean value) {
    m_ListFiles = value;
  }

  /**
   * Returns whether to list files or not.
   *
   * @return 		true if files are listed
   */
  public boolean getListFiles() {
    return m_ListFiles;
  }

  /**
   * Sets the sorting type.
   *
   * @param value 	the sorting
   */
  public void setSorting(Sorting value) {
    m_Sorting = value;
  }

  /**
   * Returns the sorting type.
   *
   * @return 		the sorting
   */
  public Sorting getSorting() {
    return m_Sorting;
  }

  /**
   * Sets whether to sort in descending manner.
   *
   * @param value 	true if desending sort manner
   */
  public void setSortDescending(boolean value) {
    m_SortDescending = value;
  }

  /**
   * Returns whether to sort in descending manner.
   *
   * @return 		true if descending sort manner
   */
  public boolean getSortDescending() {
    return m_SortDescending;
  }

  /**
   * Sets the maximum number of items to return.
   *
   * @param value 	the maximum number, &lt;=0 means unbounded
   */
  public void setMaxItems(int value) {
    m_MaxItems = value;
  }

  /**
   * Returns the maximum number of items to return.
   *
   * @return 		the maximum number, &lt;=0 means unbounded
   */
  public int getMaxItems() {
    return m_MaxItems;
  }

  /**
   * Sets the regular expressions that the items have to match.
   *
   * @param value 	the regular expression, "" matches all
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
  }

  /**
   * Returns the regular expression that the items have to match.
   *
   * @return 		the regular expression, "" matches all
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Stops the current list generation.
   */
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Checks whether the list generation has been stopped.
   *
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * A string representation of the object.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    String	result;

    result  = "WatchDir=" + m_WatchDir + ", ";
    result += "ListDirs=" + m_ListDirs + ", ";
    result += "ListFiles=" + m_ListFiles + ", ";
    result += "MaxItems=" + m_MaxItems + ", ";
    result += "RegExp=" + m_RegExp + ", ";
    result += "Sorting=" + m_Sorting + ", ";
    result += "Descending=" + m_SortDescending;

    return result;
  }
}
