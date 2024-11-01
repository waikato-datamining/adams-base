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
 * GroupUpdater.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.groupupdate;

import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;

/**
 * Ancestor for schemes that update the group of objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface GroupUpdater
  extends OptionHandler, QuickInfoSupporter {

  /**
   * Checks whether the data type is handled.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  public boolean handles(Object obj);

  /**
   * Updates the group of the object.
   *
   * @param obj		the object to process
   * @param group 	the new group
   * @return		null if successful, otherwise error message
   */
  public String updateGroup(Object obj, String group);
}
