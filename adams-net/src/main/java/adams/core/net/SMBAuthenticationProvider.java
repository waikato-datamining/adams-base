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
 * SBMAuthenticationProvider.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.net;

import jcifs.smb.NtlmPasswordAuthentication;

/**
 * Interface for classes that provide SMB authentication.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface SMBAuthenticationProvider {

  /**
   * Returns the authentication.
   *
   * @return		the authentication, null if not connected
   */
  public NtlmPasswordAuthentication getAuthentication();

  /**
   * Returns a new authentication.
   *
   * @return		the authentication
   */
  public NtlmPasswordAuthentication newAuthentication();
}
