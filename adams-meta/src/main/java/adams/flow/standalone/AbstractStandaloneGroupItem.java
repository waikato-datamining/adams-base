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
 * AbstractStandaloneGroupItem.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

/**
 * Ancestor of a standalone group item.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of the enclosing group
 */
public abstract class AbstractStandaloneGroupItem<T extends StandaloneGroup>
  extends AbstractStandalone
  implements StandaloneGroupItem<T> {

  /** for serialization. */
  private static final long serialVersionUID = -739244942139022557L;

  /**
   * Returns the enclosing group.
   * 
   * @return		the group, null if not available (eg if parent not set)
   */
  public T getEnclosingGroup() {
    if (getParent() == null)
      return null;
    
    if (getParent() instanceof StandaloneGroup)
      return (T) getParent();
    
    return null;
  }
}
