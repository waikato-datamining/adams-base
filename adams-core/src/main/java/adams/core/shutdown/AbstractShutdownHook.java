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
 * AbstractShutdownHook.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.shutdown;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for shutdown hooks for the JVM.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractShutdownHook
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 6684292517732217844L;

  /** the installed hook. */
  protected Thread m_Hook;

  /**
   * Hook method before generating the runnable and installing it.
   * <br>
   * Default implementation just returns null.
   *
   * @return		null if successful, otherwise error message
   */
  public String check() {
    return null;
  }

  /**
   * Configures the runnable that gets executed when shutting down.
   *
   * @return		the runnable
   */
  public abstract Runnable configure();

  /**
   * Installs the hook, if possible.
   *
   * @return		null if successful, otherwise error message
   */
  public String install() {
    String	result;

    result = check();
    if (result == null) {
      m_Hook = new Thread(configure());
      Runtime.getRuntime().addShutdownHook(m_Hook);
    }

    return result;
  }

  /**
   * Removes the hook, if possible.
   *
   * @return		true if successfully removed (or not installed)
   */
  public boolean remove() {
    if (m_Hook == null)
      return true;
    return Runtime.getRuntime().removeShutdownHook(m_Hook);
  }
}
