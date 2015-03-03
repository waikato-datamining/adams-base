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
 * NamedSetups.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.util.Enumeration;

import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.env.NamedSetupsDefinition;

/**
 * For obtaining named setups.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NamedSetups {

  /** the name of the props file. */
  public final static String FILENAME = "NamedSetups.props";

  /** the properties. */
  protected Properties m_Properties;

  /** the singleton. */
  protected static NamedSetups m_Singleton;

  /**
   * Initializes the classlister.
   */
  private NamedSetups() {
    super();

    initialize();
  }

  /**
   * loads the props file and interpretes it.
   */
  protected void initialize() {
    if (m_Properties == null)
      reload();
  }

  /**
   * Reloads the properties.
   */
  public synchronized void reload() {
    try {
      m_Properties = Environment.getInstance().read(NamedSetupsDefinition.KEY);
    }
    catch (Exception e) {
      m_Properties = new Properties();
    }
  }

  /**
   * Returns all the names of the setups.
   *
   * @return		the names of the setups
   */
  public Enumeration<String> names() {
    return (Enumeration<String>) m_Properties.propertyNames();
  }

  /**
   * Checks whether a setup exists with the specified name.
   *
   * @param name	the name of the setup
   * @return		true if the setup exists
   */
  public boolean has(String name) {
    return m_Properties.hasKey(name);
  }

  /**
   * Returns the setup with the given name.
   *
   * @param name	the name of the setup
   * @return		the setup, null if not available
   */
  public Object get(String name) {
    return get(name, null);
  }

  /**
   * Returns the setup with the given name.
   *
   * @param name	the name of the setup
   * @param defValue	the default value in case no setup stored under this
   * 			name
   * @return		the setup, defValue if not available
   */
  public Object get(String name, Object defValue) {
    Object	result;

    result = defValue;

    if (!name.equals(NamedSetup.DUMMY_SETUP)) {
      if (has(name)) {
	try {
	  result = OptionUtils.forAnyCommandLine(Object.class, m_Properties.getProperty(name));
	}
	catch (Exception e) {
	  System.err.println("Failed to get named setup '" + name + "':");
	  e.printStackTrace();
	  if (defValue != null)
	    System.err.println(
		"Using default for named setup '" + name + "': " + OptionUtils.getCommandLine(defValue));
	  result = defValue;
	}
      }
      else {
	System.err.println(
	    "Named setup '" + name + "' does not exist"
	    + ((defValue != null) ? ", using default instead: " + OptionUtils.getCommandLine(defValue) : "!"));
      }
    }

    return result;
  }

  /**
   * Adds the named setup to the listed setups.
   *
   * @param name	the name of the setup
   * @param setup	the setup
   * @return		the previous setup under this name, null if none replaced
   */
  public Object add(String name, Object setup) {
    Object	result;

    if (has(name))
      result = get(name);
    else
      result = null;

    m_Properties.setProperty(name, OptionUtils.getCommandLine(setup));

    return result;
  }

  /**
   * Removes the named setup from the available setups.
   *
   * @param name	the name of the setup
   * @return		the previous setup under this name, null if none removed
   */
  public Object remove(String name) {
    Object	result;

    if (has(name))
      result = get(name);
    else
      result = null;

    m_Properties.removeKeyRecursive(name);

    return result;
  }

  /**
   * Saves the properties in the user's home directory.
   *
   * @return		true if successfully saved
   */
  public boolean save() {
    return Environment.getInstance().write(NamedSetupsDefinition.KEY, m_Properties);
  }

  /**
   * Returns the singleton instance of the Placeholders.
   *
   * @return		the singleton
   */
  public static synchronized NamedSetups getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new NamedSetups();

    return m_Singleton;
  }
}

