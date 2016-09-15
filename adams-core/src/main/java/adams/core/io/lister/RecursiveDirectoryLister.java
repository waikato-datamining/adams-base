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
 * RecursiveDirectoryLister.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.lister;

/**
 * Interface for recrusive directory listers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface RecursiveDirectoryLister
  extends DirectoryLister {

  /**
   * Sets whether to search recursively.
   *
   * @param value 	true if to search recursively
   */
  public void setRecursive(boolean value);

  /**
   * Returns whether to search recursively.
   *
   * @return 		true if search is recursively
   */
  public boolean getRecursive();

  /**
   * Sets the maximum depth to search (1 = only watch dir, -1 = infinite).
   *
   * @param value 	the maximum depth
   */
  public void setMaxDepth(int value);

  /**
   * Returns the maximum depth to search (1 = only watch dir, -1 = infinite).
   *
   * @return 		the maximum depth
   */
  public int getMaxDepth();
}
