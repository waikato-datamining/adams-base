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
 * Capability.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.capabilities;

/**
 * Capability for an algorithm.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum Capability {
  NUMERIC_ATTRIBUTE(false),
  NUMERIC_CLASS(true),
  CATEGORICAL_ATTRIBUTE(false),
  CATEGORICAL_CLASS(true),
  DATETYPE_ATTRIBUTE(false),
  DATETYPE_CLASS(true);

  /** whether the capability is class-related. */
  private boolean m_ClassRelated;

  /**
   * Initializes the capability.
   *
   * @param classRelated	true if the capability is class related
   */
  private Capability(boolean classRelated) {
    m_ClassRelated = classRelated;
  }

  /**
   * Returns whether the capability is class-related.
   *
   * @return		true if class related
   */
  public boolean isClassRelated() {
    return m_ClassRelated;
  }
}
