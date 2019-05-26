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
 * SSLContextProvider.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import javax.net.ssl.SSLContext;

/**
 * Interface for actors that provide access to an instance of {@link SSLContext}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface SSLContextProvider
  extends Actor {

  /**
   * Returns the protocol to use.
   *
   * @return		the protocol
   */
  public String getProtocol();

  /**
   * Returns the SSLContext instance.
   *
   * @return		the instance, null if not available
   */
  public SSLContext getSSLContext();
}
