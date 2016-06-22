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
 * VariableChangeEvent.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.event;

import adams.core.Variables;

import java.util.EventObject;

/**
 * Gets sent whenever variables get modified.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VariableChangeEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = 265149599197540318L;

  /**
   * The type of event.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Type {
    /** a variable got added. */
    ADDED,
    /** a variable's value got modified. */
    MODIFIED,
    /** a variable got removed. */
    REMOVED
  }

  /** the type of the event. */
  protected Type m_Type;

  /** the name of the variable. */
  protected String m_Name;

  /**
   * Initializes the event.
   *
   * @param source	the Variables object that triggered the event
   * @param type	the type of event
   * @param name	the name of the variable
   */
  public VariableChangeEvent(Variables source, Type type, String name) {
    super(source);

    m_Type = type;
    m_Name = name;
  }

  /**
   * Returns the variables that triggered the event.
   *
   * @return		the source
   */
  public Variables getVariables() {
    return (Variables) getSource();
  }

  /**
   * Returns the type of the event.
   *
   * @return		the type
   */
  public Type getType() {
    return m_Type;
  }

  /**
   * Returns the name of the variable of this event.
   *
   * @return		the name
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Returns a string representation of the event.
   *
   * @return		the string representation
   */
  public String toString() {
    return
        "source=" + getSource().getClass().getName() + "/" + getSource().hashCode()
        + ", name=" + getName()
        + ", type=" + getType();
  }
}
