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
 * SSLHelper.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.core.net;

import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.env.SSLDefinition;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * A helper class for SSL.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SSLHelper {

  /** the name of the props file. */
  public final static String FILENAME = "SSL.props";

  /** The hostname verifier. */
  public final static String HOSTNAME_VERIFIER = "HostnameVerifier";

  /** The trust manager. */
  public final static String TRUST_MANAGER = "TrustManager";

  /** the properties. */
  protected static Properties m_Properties;

  /**
   * Returns the underlying properties.
   *
   * @return		the properties
   */
  public synchronized static Properties getProperties() {
    if (m_Properties == null) {
      try {
	m_Properties = Environment.getInstance().read(SSLDefinition.KEY);
      }
      catch (Exception e) {
	m_Properties = new Properties();
      }
    }

    return m_Properties;
  }

  /**
   * Writes the specified properties to disk.
   *
   * @return		true if successfully stored
   */
  public synchronized static boolean writeProperties() {
    return writeProperties(getProperties());
  }

  /**
   * Writes the specified properties to disk.
   *
   * @param props	the properties to write to disk
   * @return		true if successfully stored
   */
  public synchronized static boolean writeProperties(Properties props) {
    boolean	result;

    result = Environment.getInstance().write(SSLDefinition.KEY, props);
    // require reload
    m_Properties = null;

    return result;
  }

  /**
   * Returns the hostname verifier.
   *
   * @return		the verifier
   */
  public static HostnameVerifier getHostnameVerifier() {
    HostnameVerifier	result;
    String		cmdline;

    cmdline = getProperties().getProperty(HOSTNAME_VERIFIER, OptionUtils.getCommandLine(new adams.core.net.hostnameverifier.All()));
    try {
      result = (HostnameVerifier) OptionUtils.forAnyCommandLine(HostnameVerifier.class, cmdline);
    }
    catch (Exception e) {
      System.err.println("Failed to instantiate hostname verifier: " + cmdline);
      e.printStackTrace();
      result = new adams.core.net.hostnameverifier.All();
    }

    return result;
  }

  /**
   * Returns the trust manager.
   *
   * @return		the trust manager
   */
  public static X509TrustManager getTrustManager() {
    X509TrustManager 	result;
    String		cmdline;

    cmdline = getProperties().getProperty(TRUST_MANAGER, OptionUtils.getCommandLine(new adams.core.net.trustmanager.All()));
    try {
      result = (X509TrustManager) OptionUtils.forAnyCommandLine(X509TrustManager.class, cmdline);
    }
    catch (Exception e) {
      System.err.println("Failed to instantiate trust manager: " + cmdline);
      e.printStackTrace();
      result = new adams.core.net.trustmanager.All();
    }

    return result;
  }

  /**
   * Initializes the SSL context.
   *
   * @return		true if successfully initialized
   */
  public static synchronized boolean initialize() {
    SSLContext 	sc;

    try {
      sc = SSLContext.getInstance("SSL");
      sc.init(null, new TrustManager[]{getTrustManager()}, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      HttpsURLConnection.setDefaultHostnameVerifier(getHostnameVerifier());
      return true;
    }
    catch (Exception e) {
      System.err.println("Failed to initialize SSL context:");
      e.printStackTrace();
      return false;
    }
  }
}
