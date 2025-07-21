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
 * SMBSessionProvider.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.core.net;

import com.hierynomus.smbj.session.Session;

/**
 * Interface for classes that provide SMB sessions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface SMBSessionProvider {

  /**
   * Returns the session.
   *
   * @return		the session, null if not connected
   */
  public Session getSession();

  /**
   * Returns a new session.
   *
   * @return		the session
   */
  public Session newSession();
}
