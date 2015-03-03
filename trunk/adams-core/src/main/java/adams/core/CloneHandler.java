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
 * CloneSupporter.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

/**
 * Interface for getting around the problem with Java's design flaw in
 * regards to interfaces cannot specify the clone method.
 * See <a href="http://c2.com/cgi/wiki?CloneableDoesNotImplementClone" target="_blank">here</a> for more information.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data that gets returned by the getClone() method
 */
public interface CloneHandler<T> {

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  public T getClone();
}
