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
 * RelativeDirectoryLister.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.io.lister;

/**
 * Interface for recrusive directory listers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface RelativeDirectoryLister
  extends DirectoryLister {

  /**
   * Sets whether to output relative paths.
   *
   * @param value 	true if to output relative paths
   */
  public void setUseRelativePaths(boolean value);

  /**
   * Returns whether to output relative paths.
   *
   * @return 		true if to output relative paths
   */
  public boolean getUseRelativePaths();
}
