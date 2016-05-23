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
 * RuntimeIDGenerator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.id;

/**
 * Helper class for generating unique IDs at runtime.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RuntimeIDGenerator {

  /** the current ID. */
  protected int m_Current;

  /** the singleton. */
  protected static RuntimeIDGenerator m_Singleton;

  /**
   * Initializes the generator.
   */
  protected RuntimeIDGenerator() {
    super();
    m_Current = 0;
  }

  /**
   * Returns the next ID.
   *
   * @return		the next ID
   */
  public synchronized int next() {
    m_Current++;
    return m_Current;
  }

  /**
   * Returns the singleton of the generator.
   *
   * @return		the singleton
   */
  public static synchronized RuntimeIDGenerator getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new RuntimeIDGenerator();
    return m_Singleton;
  }
}
