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
 * EmailHelper.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.core.net;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.base.BasePassword;
import adams.core.option.OptionUtils;
import adams.env.EmailDefinition;
import adams.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A helper class for emails.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EmailHelper {

  /** the name of the props file. */
  public final static String FILENAME = "Email.props";

  /** Whether Email support is enabled. */
  public final static String ENABLED = "Enabled";

  /** The SMTP server. */
  public final static String SMTP_SERVER = "SmtpServer";

  /** The SMTP port. */
  public final static String SMTP_PORT = "SmtpPort";

  /** Whether authentication is necessary. */
  public final static String SMTP_REQUIRES_AUTHENTICATION = "SmtpRequiresAuthentication";

  /** Whether STARTTLS is necessary. */
  public final static String SMTP_START_TLS = "SmtpStartTls";

  /** Whether to use SSL. */
  public final static String SMTP_USE_SSL = "SmtpUseSsl";

  /** The user for the SMTP server. */
  public final static String SMTP_USER = "SmtpUser";

  /** The password the SMTP server. */
  public final static String SMTP_PASSWORD = "SmtpPassword";

  /** The timeout for the SMTP server. */
  public final static String SMTP_TIMEOUT = "SmtpTimeout";

  /** The default "from" email address. */
  public final static String DEFAULT_ADDRESS_FROM = "DefaultAddressFrom";

  /** The default "signature". */
  public final static String DEFAULT_SIGNATURE = "DefaultSignature";

  /** The default "send email". */
  public final static String DEFAULT_SENDEMAIL = "DefaultSendEmail";

  /** The support email address. */
  public final static String SUPPORT_EMAIL = "SupportEmail";

  /** the separator between body and signature. */
  public final static String SIGNATURE_SEPARATOR = "--";

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
	m_Properties = Environment.getInstance().read(EmailDefinition.KEY);
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

    result = Environment.getInstance().write(EmailDefinition.KEY, props);
    // require reload
    m_Properties = null;

    return result;
  }

  /**
   * Returns whether email support has been enabled.
   *
   * @return		true if enabled
   */
  public static boolean isEnabled() {
    return getProperties().getBoolean(ENABLED, false);
  }

  /**
   * Returns the SMTP server.
   *
   * @return		the server
   */
  public static String getSmtpServer() {
    return getProperties().getProperty(SMTP_SERVER, "somehost");
  }

  /**
   * Returns the SMTP port.
   *
   * @return		the port
   */
  public static int getSmtpPort() {
    return getProperties().getInteger(SMTP_PORT, 25);
  }

  /**
   * Returns whether to start TLS.
   *
   * @return		true if to start TLS
   */
  public static boolean getSmtpStartTLS() {
    return getProperties().getBoolean(SMTP_START_TLS, false);
  }

  /**
   * Returns whether to use SSL.
   *
   * @return		true if to use SSL
   */
  public static boolean getSmtpUseSSL() {
    return getProperties().getBoolean(SMTP_USE_SSL, false);
  }

  /**
   * Returns whether the server requires authentication.
   *
   * @return		true if authentication required
   */
  public static boolean getSmtpRequiresAuthentication() {
    return getProperties().getBoolean(SMTP_REQUIRES_AUTHENTICATION, false);
  }

  /**
   * Returns the timeout (in msecs) for the server.
   *
   * @return		the timeout
   */
  public static int getSmtpTimeout() {
    return getProperties().getInteger(SMTP_TIMEOUT, 30000);
  }

  /**
   * Returns the SMTP user.
   *
   * @return		the user
   */
  public static String getSmtpUser() {
    return getProperties().getProperty(SMTP_USER, "john.doe");
  }

  /**
   * Returns the SMTP password.
   *
   * @return		the password
   */
  public static BasePassword getSmtpPassword() {
    return getProperties().getPassword(SMTP_PASSWORD, new BasePassword("password"));
  }

  /**
   * Returns the default FROM address.
   *
   * @return		the default address
   */
  public static String getDefaultFromAddress() {
    String	result;
    
    result = getProperties().getProperty(DEFAULT_ADDRESS_FROM, EmailAddress.DUMMY_ADDRESS);
    if (result.trim().isEmpty() || !(new EmailAddress().isValid(result)))
      result = EmailAddress.DUMMY_ADDRESS;
    
    return result;
  }

  /**
   * Returns the default signature.
   *
   * @return		the default signature (back quoted)
   * @see		Utils#backQuoteChars(String)
   */
  public static String getDefaultSignature() {
    return getProperties().getProperty(DEFAULT_SIGNATURE, "");
  }

  /**
   * Returns the default send email class.
   *
   * @return		the default class
   */
  public static AbstractSendEmail getDefaultSendEmail() {
    try {
      return (AbstractSendEmail) OptionUtils.forCommandLine(
	  AbstractSendEmail.class, 
	  getProperties().getProperty(DEFAULT_SENDEMAIL, JavaMailSendEmail.class.getName()));
    }
    catch (Exception e) {
      return new JavaMailSendEmail();
    }
  }

  /**
   * Returns the email address for the support.
   *
   * @return		the email address, if any
   */
  public static String getSupportEmail() {
    return getProperties().getProperty(SUPPORT_EMAIL, "");
  }

  /**
   * Combines body and signature, but only if the signatures is neither null
   * nor empty.
   *
   * @param body	the actual body of the email
   * @param signature	the signature to add, ignored if null or empty
   * @return		the extended body
   */
  public static String combine(String body, String signature) {
    if ((signature == null) || (signature.trim().length() == 0))
      return body;
    else
      return body + "\n" + SIGNATURE_SEPARATOR + "\n" + signature;
  }
  
  /**
   * Creates a random boundary string.
   * 
   * @return		the random boundary string
   */
  public static String createBoundary() {
    String	result;
    Random	rand;
    
    rand     = new Random();
    result = Integer.toHexString(rand.nextInt()) + Integer.toHexString(rand.nextInt()) + Integer.toHexString(rand.nextInt());
    
    return result;
  }
  
  /**
   * Breaks up the string into lines, using the specified hard line limit.
   * 
   * @param s		the string to break up
   * @param columns	the hard line limt
   * @return		the broken up string
   */
  public static String[] breakUp(String s, int columns) {
    List<String>	result;
    int			i;
    StringBuilder	current;
    char		c;
    
    result  = new ArrayList<String>();
    current = null;
    
    if (columns < 1)
      columns = 1;
    
    for (i = 0; i < s.length(); i++) {
      if (current == null)
	current = new StringBuilder();
      c = s.charAt(i);
      current.append(c);
      if (current.length() == columns) {
	result.add(current.toString());
	current = null;
      }
    }
    
    if (current != null)
      result.add(current.toString());
      
    return result.toArray(new String[result.size()]);
  }
}
