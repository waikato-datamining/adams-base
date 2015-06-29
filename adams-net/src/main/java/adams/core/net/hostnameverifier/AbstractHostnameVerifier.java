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
 * AbstractHostnameVerifier.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.net.hostnameverifier;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Ancestor for hostname verifiers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractHostnameVerifier
  extends AbstractOptionHandler
  implements HostnameVerifier, QuickInfoSupporter {

  private static final long serialVersionUID = -3369516174458729092L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Performs the actual verification of the the host.
   *
   * @param hostname	the hostname to check
   * @param session	the current session
   * @return		true if passes check
   */
  protected abstract boolean doVerify(String hostname, SSLSession session);

  /**
   * Verifies the host.
   *
   * @param hostname	the hostname to check
   * @param session	the current session
   * @return		true if passes check
   */
  public boolean verify(String hostname, SSLSession session) {
    boolean	result;

    result = doVerify(hostname, session);
    if (isLoggingEnabled())
      getLogger().info(hostname + " -> " + result);

    return result;
  }
}
