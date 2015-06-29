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
 * All.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.net.hostnameverifier;

import javax.net.ssl.SSLSession;

/**
 * Verifies all hosts.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class All
  extends AbstractHostnameVerifier {

  private static final long serialVersionUID = 8421540491799583225L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs no checks, verifies all hosts.";
  }

  /**
   * Performs the actual verification of the the host.
   *
   * @param hostname	the hostname to check
   * @param session	the current session
   * @return		true if passes check
   */
  @Override
  protected boolean doVerify(String hostname, SSLSession session) {
    return true;
  }
}
