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
 * EmailAddressBook.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.net;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.env.Environment;

/**
 * Helper class for email addresses.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EmailAddressBook {

  /** the properties filename. */
  public final static String FILENAME = "EmailAddressBook.props";
  
  /** the singleton. */
  protected static EmailAddressBook m_Singleton;
  
  /** the underlying properties. */
  protected Properties m_Properties;
  
  /**
   * Returns the properties, loads them if necessary.
   * 
   * @return		the properties
   */
  public synchronized Properties getProperties() {
    if (m_Properties == null) {
      m_Properties = new Properties();
      m_Properties.load(Environment.getInstance().createPropertiesFilename(FILENAME));
    }
    return m_Properties;
  }

  /**
   * Saves the properties.
   */
  public synchronized boolean save() {
    return getProperties().save(Environment.getInstance().createPropertiesFilename(FILENAME));
  }
  
  /**
   * Adds the contact to the properties.
   * 
   * @param value	the contact to add
   */
  public Object addContact(EmailContact value) {
    return getProperties().setProperty(createKey(value), toString(value));
  }
  
  /**
   * Returns the contact associated with the email address, null if not found.
   * 
   * @param address	the email address to look up
   * @return		the contact, or null if not found
   */
  public EmailContact getContact(EmailAddress address) {
    return getContact(address, null);
  }
  
  /**
   * Returns the contact associated with the given email address, the default
   * value if not found.
   * 
   * @param address	the email address to look up
   * @return		the contact, or the default if not found
   */
  public EmailContact getContact(EmailAddress address, EmailContact defValue) {
    return fromString(getProperties().getProperty(address.stringValue(), toString(defValue)));
  }
  
  /**
   * Returns all stored contacts.
   * 
   * @return		the contacts
   */
  public List<EmailContact> getContacts() {
    List<EmailContact>	result;
    EmailContact	value;
    Enumeration		enm;
    String		key;
    
    result = new ArrayList<EmailContact>();
    enm    = getProperties().propertyNames();
    while (enm.hasMoreElements()) {
      key   = enm.nextElement().toString();
      value = fromString(getProperties().getProperty(key));
      if (value != null)
	result.add(value);
    }
    
    return result;
  }

  /**
   * Turns the string obtained from the properties file into an object.
   * 
   * @param s		the string to parse
   * @return		the generated object, null if failed to generate
   */
  public static EmailContact fromString(String s) {
    try {
      // add classname
      s = EmailContact.class.getName() + " " + s;
      return (EmailContact) OptionUtils.forCommandLine(EmailContact.class, s);
    }
    catch (Exception e) {
      System.err.println("Failed to parse: " + s);
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Generates a property key for the contact.
   * 
   * @param value	the object to generate the key for
   * @return		the generated key
   */
  public static String createKey(EmailContact value) {
    return value.getEmail();
  }

  /**
   * Turns the object into a string to be stored in the properties file.
   * 
   * @param value	the object to convert
   * @return		the generated string
   */
  public static String toString(EmailContact value) {
    String	result;
    
    if (value == null)
      return "";
    
    result = value.toCommandLine();
    // strip the classname
    result = result.replace(EmailContact.class.getName(), "");
    
    return result;
  }
  
  /**
   * Returns the singleton.
   * 
   * @return		the singleton
   */
  public static synchronized EmailAddressBook getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new EmailAddressBook();
    return m_Singleton;
  }
}
