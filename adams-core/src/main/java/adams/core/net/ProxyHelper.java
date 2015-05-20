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
 * ProxyHelper.java
 * Copyright (C) 2010-2012 University of Waikato, Hamilton, New Zealand
 * Copyright (C) 2010 Real Gagnon (example code)
 */
package adams.core.net;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Proxy;

import adams.core.License;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.annotation.MixedCopyright;
import adams.core.base.BasePassword;
import adams.env.Environment;
import adams.env.ProxyDefinition;

/**
 * Helper class for proxy setups.
 * <br><br>
 * Code was based on Real Gagnon's example code located
 * <a href="http://www.rgagnon.com/javadetails/java-0085.html" target="_blank">here</a>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "2010 Real Gagnon",
    author = "Real Gagnon",
    license = License.CC_BY_NC_SA_25,
    url = "http://www.rgagnon.com/javadetails/java-0085.html"
)
public class ProxyHelper {

  /** the props file. */
  public final static String FILENAME = "Proxy.props";

  /** the type of proxy to use. */
  public final static String PROXY_TYPE = "ProxyType";

  /** the http host. */
  public final static String HTTP_HOST = "HttpHost";

  /** the http port. */
  public final static String HTTP_PORT = "HttpPort";

  /** whether authentication is necessary for http proxy. */
  public final static String HTTP_AUTHENTICATION = "HttpAuthentication";

  /** the http user. */
  public final static String HTTP_USER = "HttpUser";

  /** the http password. */
  public final static String HTTP_PASSWORD = "HttpPassword";

  /** the hosts that bypass the http proxy. */
  public final static String HTTP_NOPROXY = "HttpNoProxy";

  /** the socks host. */
  public final static String SOCKS_HOST = "SocksHost";

  /** the socks port. */
  public final static String SOCKS_PORT = "SocksPort";

  /** whether authentication is necessary. */
  public final static String SOCKS_AUTHENTICATION = "SocksAuthentication";

  /** the socks user. */
  public final static String SOCKS_USER = "SocksUser";

  /** the socks password. */
  public final static String SOCKS_PASSWORD = "SocksPassword";

  /** the singleton. */
  protected static ProxyHelper m_Singleton;

  /** the properties. */
  protected Properties m_Properties;

  /** whether the settings got modified. */
  protected boolean m_Modified;

  /**
   * Initializes the helper.
   */
  private ProxyHelper() {
    super();
    reload();
  }

  /**
   * Initializes the proxy with the current settings.
   * <br><br>
   * See following URL for Java properties:
   * <a href="http://java.sun.com/j2se/1.4.2/docs/guide/net/properties.html"
   * target="_blank">http://java.sun.com/j2se/1.4.2/docs/guide/net/properties.html</a>
   */
  public void initializeProxy() {
    final Proxy.Type	type;

    type = getProxyType();

    // authentication?
    if (getAuthentication(type)) {
      Authenticator.setDefault(
	  new Authenticator() {
	    @Override
	    protected PasswordAuthentication getPasswordAuthentication() {
	      return new PasswordAuthentication(getUser(type), getPassword(type).getValue().toCharArray());
	    }
	  });
    }
    else {
      Authenticator.setDefault(null);
    }

    switch (type) {
      case HTTP:
	System.setProperty("http.proxyHost", getHost(type));
	System.setProperty("http.proxyPort", Integer.toString(getPort(type)));
	System.setProperty("ftp.proxyHost", getHost(type));
	System.setProperty("ftp.proxyPort", Integer.toString(getPort(type)));
	break;

      case SOCKS:
	System.setProperty("socksProxyHost", getHost(type));
	System.setProperty("socksProxyPort", Integer.toString(getPort(type)));
	break;
    }
  }

  /**
   * Whether the settings got modified.
   *
   * @return		true if modified
   */
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Returns the proxy type.
   *
   * @return		the type
   */
  public Proxy.Type getProxyType() {
    Proxy.Type	result;

    try {
      result = Proxy.Type.valueOf(m_Properties.getProperty(PROXY_TYPE, "DIRECT"));
    }
    catch (Exception e) {
      System.err.println("Failed to parse proxy type:");
      e.printStackTrace();
      result = Proxy.Type.DIRECT;
    }

    return result;
  }

  /**
   * Updates the proxy type.
   *
   * @param value	the proxy type
   */
  public void setProxyType(Proxy.Type value) {
    m_Modified = true;
    m_Properties.setProperty(PROXY_TYPE, value.toString());
  }

  /**
   * Returns the host.
   *
   * @param type	the proxy type
   * @return		the host
   */
  public String getHost(Proxy.Type type) {
    switch (type) {
      case HTTP:
	return m_Properties.getProperty(HTTP_HOST, "http.proxy.com");
      case SOCKS:
	return m_Properties.getProperty(SOCKS_HOST, "socks.proxy.com");
      default:
	return "";
    }
  }

  /**
   * Sets the host.
   *
   * @param type	the proxy type
   * @param value	the host
   */
  public void setHost(Proxy.Type type, String value) {
    m_Modified = true;

    switch (type) {
      case HTTP:
	m_Properties.setProperty(HTTP_HOST, value);
	break;
      case SOCKS:
	m_Properties.setProperty(SOCKS_HOST, value);
	break;
    }
  }

  /**
   * Returns the port.
   *
   * @param type	the proxy type
   * @return		the port
   */
  public int getPort(Proxy.Type type) {
    switch (type) {
      case HTTP:
	return m_Properties.getInteger(HTTP_PORT, 80);
      case SOCKS:
	return m_Properties.getInteger(SOCKS_PORT, 1080);
      default:
	return -1;
    }
  }

  /**
   * Sets the HTTP port.
   *
   * @param type	the proxy type
   * @param value	the port
   */
  public void setPort(Proxy.Type type, int value) {
    switch (type) {
      case HTTP:
	m_Modified = true;
	m_Properties.setInteger(HTTP_PORT, value);
	break;
      case SOCKS:
	m_Modified = true;
	m_Properties.setInteger(SOCKS_PORT, value);
	break;
    }
  }

  /**
   * Returns whether authentication is necessary.
   *
   * @param type	the proxy type
   * @return		true if authentication is necessary
   */
  public boolean getAuthentication(Proxy.Type type) {
    switch (type) {
      case HTTP:
	return m_Properties.getBoolean(HTTP_AUTHENTICATION, false);
      case SOCKS:
	return m_Properties.getBoolean(SOCKS_AUTHENTICATION, false);
      default:
	return false;
    }
  }

  /**
   * Sets whether authentication is required for the proxy.
   *
   * @param type	the proxy type
   * @param value	if true then authentication is required
   */
  public void setAuthentication(Proxy.Type type, boolean value) {
    switch (type) {
      case HTTP:
	m_Modified = true;
	m_Properties.setBoolean(HTTP_AUTHENTICATION, value);
	break;
      case SOCKS:
	m_Modified = true;
	m_Properties.setBoolean(SOCKS_AUTHENTICATION, value);
	break;
    }
  }

  /**
   * Returns the proxy user.
   *
   * @param type	the proxy type
   * @return		the user name
   */
  public String getUser(Proxy.Type type) {
    switch (type) {
      case HTTP:
	return m_Properties.getProperty(HTTP_USER, "");
      case SOCKS:
	return m_Properties.getProperty(SOCKS_USER, "");
      default:
	return "";
    }
  }

  /**
   * Sets the proxy user.
   *
   * @param type	the proxy type
   * @param value	the user
   */
  public void setUser(Proxy.Type type, String value) {
    switch (type) {
      case HTTP:
	m_Modified = true;
	m_Properties.setProperty(HTTP_USER, value);
	break;
      case SOCKS:
	m_Modified = true;
	m_Properties.setProperty(SOCKS_USER, value);
	break;
    }
  }

  /**
   * Returns the proxy password.
   *
   * @param type	the proxy type
   * @return		the password
   */
  public BasePassword getPassword(Proxy.Type type) {
    switch (type) {
      case HTTP:
	return m_Properties.getPassword(HTTP_PASSWORD, new BasePassword());
      case SOCKS:
	return m_Properties.getPassword(SOCKS_PASSWORD, new BasePassword());
      default:
	return new BasePassword();
    }
  }

  /**
   * Sets the proxy password.
   *
   * @param type	the proxy type
   * @param value	the password
   */
  public void setPassword(Proxy.Type type, BasePassword value) {
    switch (type) {
      case HTTP:
	m_Modified = true;
	m_Properties.setPassword(HTTP_PASSWORD, value);
	break;
      case SOCKS:
	m_Modified = true;
	m_Properties.setPassword(SOCKS_PASSWORD, value);
	break;
    }
  }

  /**
   * Returns the hosts that bypass the proxy.
   *
   * @param type	the proxy type
   * @return		the hosts
   */
  public String[] getNoProxy(Proxy.Type type) {
    switch (type) {
      case HTTP:
	return m_Properties.getProperty(HTTP_NOPROXY, "localhost|127.0.0.1").split("\\|");
      default:
	return new String[0];
    }
  }

  /**
   * Sets the hosts that bypass the proxy.
   *
   * @param type	the proxy type
   * @param value	the hosts
   */
  public void setNoProxy(Proxy.Type type, String[] value) {
    switch (type) {
      case HTTP:
	m_Modified = true;
	m_Properties.setProperty(HTTP_NOPROXY, Utils.flatten(value, "|"));
	break;
    }
  }

  /**
   * Reloads the properties file. Discards any unsaved settings.
   */
  public synchronized void reload() {
    m_Modified = false;

    try {
      m_Properties = Environment.getInstance().read(ProxyDefinition.KEY);
    }
    catch (Exception e) {
      m_Properties = new Properties();
    }
  }

  /**
   * Saves the settings in the user's home directory.
   *
   * @return		true if successfully saved
   */
  public synchronized boolean save() {
    boolean	result;

    result = Environment.getInstance().write(ProxyDefinition.KEY, m_Properties);
    if (result)
      m_Modified = false;

    return result;
  }

  /**
   * Returns the singleton.
   *
   * @return		the singleton
   */
  public synchronized static ProxyHelper getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new ProxyHelper();

    return m_Singleton;
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    for (Proxy.Type type: Proxy.Type.values()) {
      System.out.println("--> " + type);
      System.out.println("  Host: " + getSingleton().getHost(type) + ":" + getSingleton().getPort(type));
      System.out.println("  No Proxy: " + Utils.arrayToString(getSingleton().getNoProxy(type)));
      System.out.println("  Use authentication: " + getSingleton().getAuthentication(type));
      if (getSingleton().getAuthentication(type))
	System.out.println("  Authentication: " + getSingleton().getUser(type) + "/" + getSingleton().getPassword(type).getValue().replaceAll(".", "*"));
    }
  }
}
