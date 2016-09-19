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
 * DirectoryLister.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.lister;

import adams.core.StoppableWithFeedback;
import adams.core.base.BaseRegExp;
import adams.core.io.FileObject;
import adams.core.logging.LoggingLevelHandler;
import adams.core.logging.LoggingSupporter;

/**
 * Interface for directory listers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface DirectoryLister
  extends LoggingSupporter, LoggingLevelHandler, StoppableWithFeedback {

  /**
   * Sets the directory to watch.
   *
   * @param value 	the directory
   */
  public void setWatchDir(String value);

  /**
   * Returns the directory to watch.
   *
   * @return 		the directory
   */
  public String getWatchDir();

  /**
   * Sets whether to list directories or not.
   *
   * @param value 	true if directories are included in the list
   */
  public void setListDirs(boolean value);

  /**
   * Returns whether to list directories or not.
   *
   * @return 		true if directories are listed
   */
  public boolean getListDirs();

  /**
   * Sets whether to list files or not.
   *
   * @param value 	true if files are included in the list
   */
  public void setListFiles(boolean value);

  /**
   * Returns whether to list files or not.
   *
   * @return 		true if files are listed
   */
  public boolean getListFiles();

  /**
   * Sets the sorting type.
   *
   * @param value 	the sorting
   */
  public void setSorting(Sorting value);

  /**
   * Returns the sorting type.
   *
   * @return 		the sorting
   */
  public Sorting getSorting();

  /**
   * Sets whether to sort in descending manner.
   *
   * @param value 	true if desending sort manner
   */
  public void setSortDescending(boolean value);

  /**
   * Returns whether to sort in descending manner.
   *
   * @return 		true if descending sort manner
   */
  public boolean getSortDescending();

  /**
   * Sets the maximum number of items to return.
   *
   * @param value 	the maximum number, &lt;=0 means unbounded
   */
  public void setMaxItems(int value);

  /**
   * Returns the maximum number of items to return.
   *
   * @return 		the maximum number, &lt;=0 means unbounded
   */
  public int getMaxItems();

  /**
   * Sets the regular expressions that the items have to match.
   *
   * @param value 	the regular expression, "" matches all
   */
  public void setRegExp(BaseRegExp value);

  /**
   * Returns the regular expression that the items have to match.
   *
   * @return 		the regular expression, "" matches all
   */
  public BaseRegExp getRegExp();

  /**
   * Returns whether the directory lister operates locally or remotely.
   *
   * @return		true if local lister
   */
  public boolean isLocal();

  /**
   * Returns whether the watch directory has a parent directory.
   *
   * @return		true if parent directory available
   */
  public boolean hasParentDirectory();

  /**
   * Returns a new directory relative to the watch directory.
   *
   * @param dir		the directory name
   * @return		the new wrapper
   */
  public FileObject newDirectory(String dir);

  /**
   * Returns the list of files/directories in the watched directory. In case
   * the execution gets stopped, this method returns a 0-length array.
   *
   * @return		 the list of absolute file/directory names
   */
  public String[] list();

  /**
   * Returns the list of files/directories in the watched directory. In case
   * the execution gets stopped, this method returns a 0-length array.
   *
   * @return		 the list of file/directory wrappers
   */
  public FileObject[] listObjects();
}
