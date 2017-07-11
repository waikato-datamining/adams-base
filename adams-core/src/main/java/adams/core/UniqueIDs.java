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
 * UniqueIDs.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core;

import adams.core.management.ProcessUtils;

/**
 * Class for creating unique IDs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class UniqueIDs {

  /** the counter. */
  protected static long m_Counter;
  static {
    m_Counter = 0;
  }

  /**
   * Creates a new unique ID, using current nanotime, virtual machine PID,
   * counter (within JVM session).
   *
   * @return		the generated ID
   */
  public static synchronized String next() {
    m_Counter++;

    return Long.toHexString(System.nanoTime())
	+ "-" + Long.toHexString(ProcessUtils.getVirtualMachinePID())
	+ "-" + Long.toHexString(m_Counter);
  }
}
