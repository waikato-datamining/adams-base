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
 * InetUtils.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core.net;

import java.net.InetAddress;

/**
 * Utility class for internet-related stuff.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InetUtils {

  /**
   * Checks whether a connection is available to the specified host.
   *
   * @param host	the host to ping
   * @param timeout	the timeout in milli-seconds
   * @return		true if successfully pinged
   */
  public static boolean hasConnection(String host, int timeout) {
    boolean	result;
    InetAddress	address;

    try {
      address = InetAddress.getByName(host);
      result  = address.isReachable(timeout);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = false;
    }

    return result;
  }
}
