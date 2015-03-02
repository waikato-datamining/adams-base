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
 * OS.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

/**
 * Helper class for operating system related stuff.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OS {

  /** whether the OS is Windows. */
  protected static Boolean m_IsWindows;

  /** whether the OS is Mac. */
  protected static Boolean m_IsMac;

  /**
   * Checks whether the operating system is Windows.
   *
   * @return		true if the OS is Windows flavor
   */
  public static synchronized boolean isWindows() {
    String	os;

    if (m_IsWindows == null) {
      os          = System.getProperty("os.name").toLowerCase();
      m_IsWindows = (os.indexOf("windows") > -1);
    }

    return m_IsWindows;
  }

  /**
   * Checks whether the operating system is Windows.
   *
   * @return		true if the OS is Windows flavor
   */
  public synchronized static boolean isMac() {
    String	os;

    if (m_IsMac == null) {
      os      = System.getProperty("os.name").toLowerCase();
      m_IsMac = os.startsWith("mac os");
    }

    return m_IsMac;
  }

  /**
   * Returns the "bitness", ie 32 or 64 bit of the underlying OS.
   *
   * @return		the number of bits
   */
  public synchronized static int getBitness() {
    String	arch;

    arch = System.getProperty("os.arch");
    if (arch.endsWith("86"))
      return 32;
    else if (arch.endsWith("64"))
      return 64;
    else
      throw new IllegalStateException("Cannot interpret 'os.arch' for bitness: " + arch);
  }
}
