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
 * AbstractOptionGroup.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

/**
 * Ancestor for grouped options.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractOptionGroup
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -9031523837379235328L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Groups the options for " + getGroupName();
  }
  
  /**
   * Returns the group name.
   * 
   * @return		the name
   */
  protected abstract String getGroupName();
}
