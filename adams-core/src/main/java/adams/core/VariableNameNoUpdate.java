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
 * VariableNameNoUpdate.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

/**
 * Wrapper around the name of a variable (= string). Same as {@link VariableName}
 * but doesn't trigger updates in the flow editor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VariableNameNoUpdate
  extends VariableName {

  /** for serialization. */
  private static final long serialVersionUID = -4252630797841061799L;

  /**
   * Initializes the name with a default value.
   */
  public VariableNameNoUpdate() {
    super();
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public VariableNameNoUpdate(String s) {
    super(s);
  }
}
