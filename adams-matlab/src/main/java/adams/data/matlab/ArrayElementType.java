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
 * ArrayElementType.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.matlab;

/**
 * The element type.
 */
public enum ArrayElementType {
  BOOLEAN(Boolean.class),
  BYTE(Byte.class),
  SHORT(Short.class),
  INTEGER(Integer.class),
  LONG(Long.class),
  FLOAT(Float.class),
  DOUBLE(Double.class);

  /** the associated class. */
  private Class m_Type;

  /**
   * Initializes the enum value with the class.
   *
   * @param type	the class to associate
   */
  private ArrayElementType(Class type) {
    m_Type = type;
  }

  /**
   * Returns the associated class.
   *
   * @return		the class
   */
  public Class getType() {
    return m_Type;
  }
}
