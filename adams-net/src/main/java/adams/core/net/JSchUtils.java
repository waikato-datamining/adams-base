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
 * JSchUtils.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 * Copyright (C) JSch
 */

package adams.core.net;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;

/**
 * Helper class for SSH connections using {@link JSch}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
  copyright = "JCraft",
  license = License.BSD3,
  url = "http://www.jcraft.com/jsch/"
)
public class JSchUtils {

  /** for logging purposes. */
  private final static Logger LOGGER = LoggingHelper.getConsoleLogger(JSchUtils.class);

  /**
   * Instantiates a new JSch object.
   *
   * @param knownHosts	the file pointing to the known hosts config file, can be null
   * @return		the object
   * @throws Exception	if known hosts file fails
   */
  public static JSch newJsch(File knownHosts) throws Exception {
    JSch 	result;

    result = new JSch();
    result.setLogger(new com.jcraft.jsch.Logger() {
      @Override
      public boolean isEnabled(int level) {
	return true;
      }

      @Override
      public void log(int level, String message) {
	switch (level) {
	  case DEBUG:
	    LOGGER.fine(message);
	    break;
	  case INFO:
	    LOGGER.info(message);
	    break;
	  case WARN:
	    LOGGER.warning(message);
	    break;
	  case ERROR:
	  case FATAL:
	    LOGGER.severe(message);
	    break;
	}
      }
    });
    // TODO choose RSA, DSA, ECDSA?
    if (knownHosts != null)
      result.setKnownHosts(knownHosts.getAbsolutePath());

    return result;
  }

  /**
   * Creates a new session using credentials-based authentication.
   *
   * @param jsch	the JSch context
   * @param user	the user
   * @param password	the password to use
   * @param host	the host to connect to
   * @param port	the port to connect to
   * @return		the session
   * @throws Exception	if creation of session fails
   */
  public static Session newSession(JSch jsch, String user, String password, String host, int port) throws Exception {
    Session	result;

    result = jsch.getSession(user, host, port);
    result.setPassword(password);

    return result;
  }

  /**
   * Creates a new session using public-key-based authentication.
   *
   * @param jsch	the JSch context
   * @param user	the user
   * @param privateKey	the private key file
   * @param password	the password to use to open the private key file, empty or null if not necessary
   * @param host	the host to connect to
   * @param port	the port to connect to
   * @return		the session
   * @throws Exception	if creation of session fails
   */
  public static Session newSession(JSch jsch, String user, File privateKey, String password, String host, int port) throws Exception {
    Session	result;

    if ((password == null) || password.isEmpty())
      jsch.addIdentity(privateKey.getAbsolutePath());
    else
      jsch.addIdentity(privateKey.getAbsolutePath(), password);
    result = jsch.getSession(user, host, port);

    return result;
  }

  /**
   * Configures the session for strict host key checks.
   *
   * @param session	the session to configure
   * @param strict	true if to perform strict checks
   */
  public static void configureStrictHostKeyChecking(Session session, boolean strict) {
    session.setConfig("StrictHostKeyChecking", strict ? "yes" : "no");
  }

  /**
   * Configures the session for X11.
   *
   * @param session	the session to configure
   * @param host	the X11 host
   * @param port	the X11 port
   */
  public static void configureX11(Session session, String host, int port) {
    session.setX11Host(host);
    session.setX11Port(port);
  }
}
