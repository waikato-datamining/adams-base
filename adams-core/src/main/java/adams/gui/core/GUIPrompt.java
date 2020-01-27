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
 * GUIPrompt.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.Properties;
import adams.core.logging.LoggingObject;
import adams.env.Environment;

import java.util.logging.Level;

/**
 * Helper class for GUI prompts that require updating values, e.g.,
 * only displaying a warning dialog once.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GUIPrompt
  extends LoggingObject {

  /** the name of the props file. */
  public final static String FILENAME = "GUIPrompt.props";

  private static final long serialVersionUID = 8900171025794575993L;

  /** the properties. */
  protected Properties m_Properties;

  /** the singleton. */
  protected static GUIPrompt m_Singleton;

  /**
   * Initializes the class.
   */
  protected GUIPrompt() {
    initialize();
  }

  /**
   * Loads the properties.
   */
  protected void initialize() {
    try {
      m_Properties = Properties.read(FILENAME);
    }
    catch (Exception e) {
      m_Properties = new Properties();
      getLogger().log(Level.SEVERE, "Failed to load properties '" + FILENAME + "'!", e);
    }
  }

  /**
   * Stores the properties on disk.
   */
  protected void update() {
    String	propsFile;

    propsFile = Environment.getInstance().createPropertiesFilename(FILENAME);
    if (!m_Properties.save(propsFile))
      getLogger().warning("Failed to save properties to: " + propsFile);
  }

  /**
   * Returns the stored value or, if not present, the default value.
   *
   * @param key		the property key to read the boolean from
   * @param defValue	the default value to use if absent
   * @return		the stored value or the default value (if absent)
   */
  public boolean getBoolean(String key, boolean defValue) {
    return m_Properties.getBoolean(key, defValue);
  }

  /**
   * Sets the boolean value and stores the properties.
   *
   * @param key		the property to store the value under
   * @param value	the value to store
   */
  public void setBoolean(String key, boolean value) {
    m_Properties.setBoolean(key, value);
    update();
  }

  /**
   * Returns the stored value or, if not present, the default value.
   *
   * @param key		the property key to read the Integer from
   * @param defValue	the default value to use if absent
   * @return		the stored value or the default value (if absent)
   */
  public Integer getInteger(String key, Integer defValue) {
    return m_Properties.getInteger(key, defValue);
  }

  /**
   * Sets the Integer value and stores the properties.
   *
   * @param key		the property to store the value under
   * @param value	the value to store
   */
  public void setInteger(String key, Integer value) {
    m_Properties.setInteger(key, value);
    update();
  }

  /**
   * Returns the stored value or, if not present, the default value.
   *
   * @param key		the property key to read the Double from
   * @param defValue	the default value to use if absent
   * @return		the stored value or the default value (if absent)
   */
  public Double getDouble(String key, Double defValue) {
    return m_Properties.getDouble(key, defValue);
  }

  /**
   * Sets the Double value and stores the properties.
   *
   * @param key		the property to store the value under
   * @param value	the value to store
   */
  public void setDouble(String key, Double value) {
    m_Properties.setDouble(key, value);
    update();
  }

  /**
   * Returns the singleton instance.
   *
   * @return		the instance
   */
  public static synchronized GUIPrompt getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new GUIPrompt();
    return m_Singleton;
  }
}
