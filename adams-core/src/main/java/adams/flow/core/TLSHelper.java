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
 * TLSHelper.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import com.github.fracpete.javautils.struct.Struct3;

/**
 * Helper class for TLS related operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TLSHelper {

  /**
   * Locates the following actors KeyManager (=0), TrustManager (=1), SSLContext (=2)
   *
   * @param context	the flow context
   * @param requireAll 	whether all the actors need to be present
   * @return		the actors, null if all required and not all were present
   */
  public static Struct3<KeyManagerFactoryProvider,TrustManagerFactoryProvider,SSLContextProvider> locateActors(Actor context, boolean requireAll) {
    Struct3<KeyManagerFactoryProvider,TrustManagerFactoryProvider,SSLContextProvider>	result;
    KeyManagerFactoryProvider 		keyManager;
    TrustManagerFactoryProvider 	trustManager;
    SSLContextProvider			sslContext;

    keyManager   = (KeyManagerFactoryProvider) ActorUtils.findClosestType(context, KeyManagerFactoryProvider.class, true);
    trustManager = (TrustManagerFactoryProvider) ActorUtils.findClosestType(context, TrustManagerFactoryProvider.class, true);
    sslContext   = (SSLContextProvider) ActorUtils.findClosestType(context, SSLContextProvider.class, true);

    if (requireAll) {
      if ((keyManager == null) || (trustManager == null) || sslContext == null)
        return null;
      result = new Struct3<>(keyManager, trustManager, sslContext);
    }
    else {
      result = new Struct3<>(keyManager, trustManager, sslContext);
    }

    return result;
  }
}
