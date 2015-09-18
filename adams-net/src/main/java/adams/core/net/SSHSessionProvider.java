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
 * SSHSessionProvider.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.net;

import com.jcraft.jsch.Session;

/**
 * Interface for classes that provide SSH sessions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface SSHSessionProvider {

  /**
   * Returns the SSH session.
   *
   * @return		the SSH session, null if not connected
   */
  public Session getSession();

  /**
   * Returns a new session for the host/port defined in the options.
   *
   * @return		the session
   */
  public Session newSession();

  /**
   * Returns a new session for the given host/port.
   *
   * @param host	the host to create the session for
   * @return		the session
   */
  public Session newSession(String host, int port);
}
