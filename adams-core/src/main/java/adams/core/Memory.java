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
 * Memory.java
 * Copyright (C) 2005-2011 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.core;

/**
 * A little helper class for Memory management. Very crude, since JDK 1.4
 * doesn't offer real Memory Management.<br><br>
 * The memory management can be disabled by using the setEnabled(boolean)
 * method.
 *
 * @author    FracPete (fracpete at waikato dot ac dot nz)
 * @version   $Revision$
 * @see       #setEnabled(boolean)
 */
public class Memory {

  /** whether memory management is enabled. */
  protected boolean m_Enabled;

  /** the total memory that is used. */
  protected long m_Total;

  /** the maximum amount of memory that can be used. */
  protected long m_Max;

  /** the current runtime variable.  */
  protected Runtime m_Runtime;

  /** the singleton. */
  protected static Memory m_Singleton;

  /**
   * initializes the memory management without GUI support.
   */
  private Memory() {
    m_Enabled = true;
    m_Runtime = Runtime.getRuntime();
    m_Max     = m_Runtime.maxMemory();
    m_Total   = m_Runtime.totalMemory();
  }

  /**
   * returns whether the memory management is enabled.
   *
   * @return		true if enabled
   */
  public boolean isEnabled() {
    return m_Enabled;
  }

  /**
   * sets whether the memory management is enabled.
   *
   * @param value	true if the management should be enabled
   */
  public void setEnabled(boolean value) {
    m_Enabled = value;
  }

  /**
   * returns the current memory consumption.
   *
   * @return		the current size in bytes
   */
  public long getCurrent() {
    m_Runtime = Runtime.getRuntime();
    m_Total   = m_Runtime.totalMemory();

    return m_Total;
  }

  /**
   * returns the maximum amount of memory that can be assigned.
   *
   * @return		the maximum size in bytes
   */
  public long getMax() {
    return m_Max;
  }

  /**
   * Returns the singleton.
   *
   * @return		the singleton
   */
  public static synchronized Memory getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new Memory();

    return m_Singleton;
  }
}
