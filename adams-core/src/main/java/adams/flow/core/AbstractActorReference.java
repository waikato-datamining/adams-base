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
 * AbstractActorReference.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.base.AbstractBaseString;

/**
 * Ancestor for actor references.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractActorReference
  extends AbstractBaseString {

  private static final long serialVersionUID = -8799715468190941509L;

  /**
   * Initializes the string with length 0.
   */
  public AbstractActorReference() {
    this("");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public AbstractActorReference(String s) {
    super(s);
  }
}
