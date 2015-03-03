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
 * HomeRelocator.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.env;

/**
 * Interface for classes that can "relocate" the project's home directory.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see	    AbstractEnvironment#setHome(String)
 */
public interface HomeRelocator {

  /**
   * Sets the new home directory to use.
   * <p/>
   * Notes: cannot contain placeholders, should be absolute.
   *
   * @param value	the directory to use
   */
  public void setHome(String value);

  /**
   * Returns the value use as new home directory.
   *
   * @return		the directory to use
   */
  public String getHome();
}
