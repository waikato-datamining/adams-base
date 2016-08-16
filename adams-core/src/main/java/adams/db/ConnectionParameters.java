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
 * ConnectionParameters.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db;

import adams.core.CloneHandler;
import adams.core.base.BasePassword;
import adams.core.logging.LoggingLevel;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Container class for connection information.
 *
 *  @author  fracpete (fracpete at waikato dot ac dot nz)
 *  @version $Revision$
 */
public class ConnectionParameters
  implements Serializable, Comparable<ConnectionParameters>, CloneHandler<ConnectionParameters> {

  /** for serialization. */
  private static final long serialVersionUID = -1414581492377334939L;

  /** the class parameter. */
  public final static String PARAM_CLASS = "Class";

  /** the URL parameter. */
  public final static String PARAM_URL = "URL";

  /** the user parameter. */
  public final static String PARAM_USER = "User";

  /** the password parameter. */
  public final static String PARAM_PASSWORD = "Password";

  /** the logging level parameter. */
  public final static String PARAM_LOGGINGLEVEL = "LoggingLevel";

  /** the connect on startup parameter. */
  public final static String PARAM_CONNECTONSTARTUP = "ConnectOnStartup";

  /** the auto commit. */
  public final static String PARAM_AUTOCOMMIT = "AutoCommit";

  /** the URL. */
  protected String m_URL;

  /** the user. */
  protected String m_User;

  /** the password. */
  protected BasePassword m_Password;

  /** the logging level. */
  protected LoggingLevel m_LoggingLevel;

  /** whether to connect on startup. */
  protected boolean m_ConnectOnStartUp;

  /** whether to use auto-commit. */
  protected boolean m_AutoCommit;

  /**
   * Initializes the container.
   */
  public ConnectionParameters() {
    super();
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_URL              = "";
    m_User             = "";
    m_Password         = new BasePassword();
    m_LoggingLevel     = LoggingLevel.OFF;
    m_ConnectOnStartUp = false;
    m_AutoCommit       = false;
  }

  /**
   * Returns the URL.
   *
   * @return		the URL
   */
  public String getURL() {
    return m_URL;
  }

  /**
   * Returns the user.
   *
   * @return		the user
   */
  public String getUser() {
    return m_User;
  }

  /**
   * Returns the password.
   *
   * @return		the password
   */
  public BasePassword getPassword() {
    return m_Password;
  }

  /**
   * Returns the logging level.
   *
   * @return		the logging level
   */
  public LoggingLevel getLoggingLevel() {
    return m_LoggingLevel;
  }

  /**
   * Returns whether to connect on startup.
   *
   * @return		true if to connect on startup
   */
  public boolean getConnectOnStartUp() {
    return m_ConnectOnStartUp;
  }

  /**
   * Returns whether to use auto-commit.
   *
   * @return		true if to use auto-commit
   */
  public boolean getAutoCommit() {
    return m_AutoCommit;
  }

  /**
   * Returns the available parameter keys.
   *
   * @return		the parameter keys
   */
  public Enumeration<String> parameters() {
    Vector<String>	result;

    result = new Vector<>();

    result.add(PARAM_CLASS);
    result.add(PARAM_URL);
    result.add(PARAM_USER);
    result.add(PARAM_PASSWORD);
    result.add(PARAM_LOGGINGLEVEL);
    result.add(PARAM_CONNECTONSTARTUP);
    result.add(PARAM_AUTOCOMMIT);

    return result.elements();
  }

  /**
   * Returns the parameter for the specified key.
   *
   * @param key		the key of the parameter to retrieve
   * @return		the associated value, null if not available
   */
  public String getParameter(String key) {
    if (key.equals(PARAM_CLASS))
      return getClass().getName();
    if (key.equals(PARAM_URL))
      return m_URL;
    if (key.equals(PARAM_USER))
      return m_User;
    if (key.equals(PARAM_PASSWORD))
      return m_Password.stringValue();
    if (key.equals(PARAM_LOGGINGLEVEL))
      return "" + m_LoggingLevel;
    if (key.equals(PARAM_CONNECTONSTARTUP))
      return "" + m_ConnectOnStartUp;
    if (key.equals(PARAM_AUTOCOMMIT))
      return "" + m_AutoCommit;

    return null;
  }

  /**
   * Returns the parameter for the specified key.
   *
   * @param key		the key of the parameter to retrieve
   * @param value	the associated value
   */
  public void setParameter(String key, String value) {
    if (key.equals(PARAM_CLASS))
      ;  // ignored
    if (key.equals(PARAM_URL))
      m_URL = value;
    else if (key.equals(PARAM_USER))
      m_User = value;
    else if (key.equals(PARAM_PASSWORD))
      m_Password = new BasePassword(value);
    else if (key.equals(PARAM_LOGGINGLEVEL))
      m_LoggingLevel = LoggingLevel.valueOf(value);
    else if (key.equals(PARAM_CONNECTONSTARTUP))
      m_ConnectOnStartUp = Boolean.parseBoolean(value);
    else if (key.equals(PARAM_AUTOCOMMIT))
      m_AutoCommit = Boolean.parseBoolean(value);
  }

  /**
   * Returns a new (empty) instance of a ConnectionParameters object.
   *
   * @return		the empty instance
   */
  protected ConnectionParameters newInstance() {
    return new ConnectionParameters();
  }

  /**
   * Returns a copy of ifself.
   *
   * @return		the copy
   */
  public ConnectionParameters getClone() {
    ConnectionParameters	result;
    Enumeration<String>		keys;
    String			key;

    result = newInstance();
    keys   = parameters();
    while (keys.hasMoreElements()) {
      key = keys.nextElement();
      result.setParameter(key, getParameter(key));
    }

    return result;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  public int compareTo(ConnectionParameters o) {
    int				result;
    Enumeration<String>		keys;
    String			key;
    Object			oThis;
    Object			oOther;

    if (o == null)
      return 1;

    keys   = parameters();
    result = 0;
    while ((result == 0) && keys.hasMoreElements()) {
      key    = keys.nextElement();
      oThis  = getParameter(key);
      oOther = o.getParameter(key);
      if (oOther == null) {
	result = 1;
      }
      else {
	if (oThis instanceof Comparable)
	  result = ((Comparable) oThis).compareTo(oOther);
      }
    }

    return result;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj		the reference object with which to compare.
   * @return		true if this object is the same as the obj argument;
   * 			false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ConnectionParameters)
      return (compareTo((ConnectionParameters) obj) == 0);
    else
      return false;
  }

  /**
   * Hashcode so can be used as hashtable key. Returns the hashcode of the
   * "url \t user \t password" string.
   *
   * @return		the hashcode
   */
  @Override
  public int hashCode() {
    return (m_URL + "\t" + m_User + "\t" + m_Password).hashCode();
  }

  /**
   * Returns a shortened URL.
   *
   * @return		the shortened URL
   */
  @Override
  public String toString() {
    return m_URL.replaceAll(".*\\/\\/", "");
  }

  /**
   * Returns the instance of a new database connection object.
   *
   * @param dbcon	the database connection object class to instantiate
   * @return		the new database connection object
   */
  public AbstractDatabaseConnection toDatabaseConnection(Class dbcon) {
    AbstractDatabaseConnection	result;

    try {
      result = (AbstractDatabaseConnection) dbcon.newInstance();
      result.setURL(getURL());
      result.setUser(getUser());
      result.setPassword(getPassword());
      result.setLoggingLevel(getLoggingLevel());
      result.setConnectOnStartUp(getConnectOnStartUp());
      result.setAutoCommit(getAutoCommit());
    }
    catch (Exception e) {
      System.err.println("Failed to create database connection object:");
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Creates a new object based on the classname, falls back to the default
   * class, if instantiation fails.
   *
   * @param classname	the class to instantiate
   * @return		the new object
   */
  public static ConnectionParameters forName(String classname) {
    ConnectionParameters	result;

    try {
      result = (ConnectionParameters) Class.forName(classname).newInstance();
    }
    catch (Exception e) {
      result = new ConnectionParameters();
    }

    return result;
  }
}