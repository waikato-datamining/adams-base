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
 * BlacklistedOptionVisitorProducer.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

/**
 * Interface for visitors that Allow skipping of options based on the class of
 * the return type of the properties.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface BlacklistedOptionProducer {

  /**
   * Sets the classes to avoid.
   *
   * @param value	the classes
   */
  public void setBlacklisted(Class[] value);

  /**
   * Returns the blacklisted classes.
   *
   * @return		the classes
   */
  public Class[] getBlacklisted();
}
