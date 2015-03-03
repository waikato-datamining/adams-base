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
 * AbstractSendEmail.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.net;

import adams.core.CleanUpHandler;
import adams.core.base.BasePassword;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for classes that can send emails.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSendEmail
  extends AbstractOptionHandler
  implements CleanUpHandler {
  
  /** for serialization. */
  private static final long serialVersionUID = -5248888725267599119L;

  /**
   * Returns whether the SMTP session needs to be initialized.
   * 
   * @return		true if the SMTP session needs to be initialized
   */
  public abstract boolean requiresSmtpSessionInitialization();
  
  /**
   * Initializes the SMTP session.
   *
   * @param server		the SMTP server
   * @param port		the SMTP port
   * @param useTLS		whether to use TLS
   * @param useSSL		whether to use SSL
   * @param timeout		the timeout
   * @param requiresAuth	whether authentication is required
   * @param user		the SMTP user
   * @param pw			the SMTP password
   * @return			the session
   * @throws Exception		if initialization fails
   */
  public abstract void initializeSmtpSession(String server, int port, boolean useTLS, boolean useSSL, int timeout, boolean requiresAuth, final String user, final BasePassword pw) throws Exception;

  /**
   * Sends an email.
   *
   * @param email	the email to send
   * @return		true if successfully sent
   * @throws Exception	in case of invalid internet addresses or messaging problem
   */
  public abstract boolean sendMail(Email email) throws Exception;

  /**
   * Cleans up data structures, frees up memory.
   * <p/>
   * Default implementation does nothing.
   */
  public void cleanUp() {
  }
}
