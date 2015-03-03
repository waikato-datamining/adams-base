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
 * TriggerableEventReference.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

/**
 * Wrapper around the name of a triggerable event (= string).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TriggerableEventReference
  extends EventReference {

  /** for serialization. */
  private static final long serialVersionUID = 3596514589424315665L;

  /**
   * Initializes the string with length 0.
   */
  public TriggerableEventReference() {
    this("");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public TriggerableEventReference(String s) {
    super(s);
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  public String getTipText() {
    return "The name of a triggerable event.";
  }
}
