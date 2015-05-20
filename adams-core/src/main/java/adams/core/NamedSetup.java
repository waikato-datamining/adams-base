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
 * NamedSetup.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.io.Serializable;

/**
 * A wrapper around the name of a named setup. In order to bring up custom
 * editor in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NamedSetup
  implements Serializable, Comparable<NamedSetup> {

  /** for serialization. */
  private static final long serialVersionUID = 648959606440756217L;

  /** dummy setup name. */
  public final static String DUMMY_SETUP = "name_of_setup";

  /** the name of the setup. */
  protected String m_Name;

  /**
   * Initializes the setup with a default name.
   *
   * @see		#DUMMY_SETUP
   */
  public NamedSetup() {
    this(DUMMY_SETUP);
  }

  /**
   * Initializes the setup with the specified name.
   *
   * @param name	the name for the setup
   */
  public NamedSetup(String name) {
    super();

    m_Name = name;
  }

  /**
   * Returns the name of the setup.
   *
   * @return		the name
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Returns whether the name is just the dummy one.
   *
   * @return		true if dummy name
   * @see		#DUMMY_SETUP
   */
  public boolean isDummy() {
    return (m_Name.equals(DUMMY_SETUP));
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
   * Only compares the names of the two objects.
   *
   * @param o 		the object to be compared.
   * @return  		a negative integer, zero, or a positive integer as this object
   *			is less than, equal to, or greater than the specified object.
   */
  public int compareTo(NamedSetup o) {
    if (o == null)
      return 1;

    return getName().compareTo(o.getName());
  }

  /**
   * Returns whether the two objects are the same.
   * <br><br>
   * Only compares the names of the two objects.
   *
   * @param o		the object to be compared
   * @return		true if the object is the same as this one
   */
  public boolean equals(Object o) {
    if (!(o instanceof NamedSetup))
      return false;
    else
      return (compareTo((NamedSetup) o) == 0);
  }

  /**
   * Hashcode so can be used as hashtable key. Returns the hashcode of the
   * name string.
   *
   * @return		the hashcode
   */
  public int hashCode() {
    return m_Name.hashCode();
  }

  /**
   * Returns whether a setup with that name actually exists.
   *
   * @return		true if a setup with that name exists
   */
  public boolean exists() {
    return NamedSetups.getSingleton().has(getName());
  }

  /**
   * Returns the associated setup, if any.
   *
   * @return		the setup
   */
  public Object getSetup() {
    return NamedSetups.getSingleton().get(getName());
  }

  /**
   * Merely returns the name of the setup.
   *
   * @return		the name
   */
  public String toString() {
    return m_Name;
  }
}
