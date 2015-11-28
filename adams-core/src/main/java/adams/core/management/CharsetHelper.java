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
 * CharsetHelper.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import adams.core.Properties;
import adams.env.CharsetDefinition;
import adams.env.Environment;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Helper class for charset setup.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CharsetHelper {

  /** the props file. */
  public final static String FILENAME = "Charset.props";

  /** the charset to use. */
  public final static String CHARSET = "Charset";

  /** the constant for the predefined charset. */
  public final static String CHARSET_DEFAULT = "Default";

  /** the singleton. */
  protected static CharsetHelper m_Singleton;

  /** the properties. */
  protected Properties m_Properties;

  /** whether the settings got modified. */
  protected boolean m_Modified;
  
  /**
   * Initializes the helper.
   */
  private CharsetHelper() {
    super();
    initialize();
    reload();
  }
  
  /**
   * Initializes the helper.
   */
  protected void initialize() {
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
   * Returns the charset.
   *
   * @return		the charset
   */
  public Charset getCharset() {
    return valueOf(m_Properties.getProperty(CHARSET, Charset.defaultCharset().name()));
  }

  /**
   * Updates the charset.
   *
   * @param value	the charset
   */
  public void setCharset(Charset value) {
    setCharset(value.name());
  }

  /**
   * Updates the charset.
   *
   * @param value	the charset
   */
  public void setCharset(String value) {
    m_Modified = true;
    m_Properties.setProperty(CHARSET, value);
  }
  
  /**
   * Reloads the properties file. Discards any unsaved settings.
   */
  public synchronized void reload() {
    m_Modified = false;

    try {
      m_Properties = Environment.getInstance().read(CharsetDefinition.KEY);
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

    result = Environment.getInstance().write(CharsetDefinition.KEY, m_Properties);
    if (result)
      m_Modified = false;

    return result;
  }

  /**
   * Returns the singleton.
   *
   * @return		the singleton
   */
  public synchronized static CharsetHelper getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new CharsetHelper();

    return m_Singleton;
  }
  
  /**
   * Returns the charset as string.
   *
   * @param l		the charset object to convert
   * @return		the generated string
   */
  public static String toString(Charset l) {
    String	result;
    
    result = l.name();
    if (result.equals(Charset.defaultCharset().name()))
      result = CHARSET_DEFAULT;
    
    return result;
  }

  /**
   * Returns a charset generated from the string (eg en, en_US).
   *
   * @param str		the string to convert to a charset
   * @return		the generated charset
   */
  public static Charset valueOf(String str) {
    if (str.equals(CHARSET_DEFAULT)) {
      return Charset.defaultCharset();
    }
    else {
      try {
	return Charset.forName(str);
      }
      catch (Exception e) {
	System.err.println("Failed to parse charset: " + str);
	return Charset.defaultCharset();
      }
    }
  }
  
  /**
   * Returns all the charset IDs, including {@link #CHARSET_DEFAULT}.
   * 
   * @return		the IDs
   */
  public static String[] getIDs() {
    Charset[]		charsets;
    ArrayList<String>	result;
    
    result  = new ArrayList<String>();
    charsets = Charset.availableCharsets().values().toArray(new Charset[0]);
    for (Charset l: charsets)
      result.add(l.toString());
    Collections.sort(result);
    result.add(0, CharsetHelper.CHARSET_DEFAULT);
    
    return result.toArray(new String[result.size()]);
  }
}
