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
 * PanelSettings.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.DateTime;
import adams.core.Properties;
import adams.core.Time;
import adams.core.base.BasePassword;
import adams.env.Environment;
import adams.gui.core.DelayedActionRunnable.AbstractAction;

import java.awt.Color;
import java.io.File;
import java.util.Date;

/**
 * Class for keeping track of parameters for panels, like divider locations.
 * Uses the class (or class of the object) and the property to generate a key.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PanelSettings {

  /** the filename. */
  public final static String FILENAME = "PanelSettings.props";

  /** the seconds to wait before saving the settings. */
  public final static int SECONDS_WAIT = 15;

  /** the properties. */
  protected static Properties m_Properties;

  /** for delaying the saving. */
  protected static DelayedActionRunnable m_SaveRunnable;

  /** the threading for saving. */
  protected static Thread m_SaveThread;

  /** whether settings are not yet saved. */
  protected static boolean m_Modified;

  /**
   * Initializes the properties if necessary.
   */
  protected static synchronized void initializeProperties() {
    if (m_Properties == null) {
      try {
	m_Properties = Properties.read(FILENAME);
      }
      catch (Exception e) {
	m_Properties = new Properties();
      }
    }
  }

  /**
   * Returns the properties in use.
   *
   * @return		the properties
   */
  protected static synchronized Properties getProperties() {
    initializeProperties();
    return m_Properties;
  }

  /**
   * Assembles the key.
   *
   * @param cls		the class
   * @param property	the property
   * @return		the key
   */
  protected static String createKey(Class cls, String property) {
    return cls.getName() + "-" + property;
  }

  /**
   * Sets the value for the object.
   *
   * @param obj		the object to set the value for
   * @param property	the property to set
   * @param value	the value to set
   * @see		#set(Class, String, Object)
   */
  public static synchronized void set(Object obj, String property, Object value) {
    set(obj.getClass(), property, value);
  }

  /**
   * Sets the value for the class.
   *
   * @param cls		the class to set the value for
   * @param property	the property to set
   * @param value	the value to set
   */
  public static synchronized void set(Class cls, String property, Object value) {
    String	key;

    key = createKey(cls, property);

    if (value instanceof Integer)
      getProperties().setInteger(key, (Integer) value);
    else if (value instanceof Long)
      getProperties().setLong(key, (Long) value);
    else if (value instanceof Double)
      getProperties().setDouble(key, (Double) value);
    else if (value instanceof Boolean)
      getProperties().setBoolean(key, (Boolean) value);
    else if (value instanceof Time)
      getProperties().setTime(key, (Time) value);
    else if (value instanceof DateTime)
      getProperties().setDateTime(key, (DateTime) value);
    else if (value instanceof Date)
      getProperties().setDate(key, (Date) value);
    else if (value instanceof Color)
      getProperties().setColor(key, (Color) value);
    else if (value instanceof BasePassword)
      getProperties().setPassword(key, (BasePassword) value);
    else
      getProperties().setProperty(key, "" + value);

    m_Modified = true;

    queueSave();
  }

  /**
   * Returns the boolean value.
   *
   * @param obj		the object
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized boolean get(Object obj, String property, boolean defValue) {
    return get(obj.getClass(), property, defValue);
  }

  /**
   * Returns the boolean value.
   *
   * @param cls		the class
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized boolean get(Class cls, String property, boolean defValue) {
    return getProperties().getBoolean(createKey(cls, property), defValue);
  }

  /**
   * Returns the int value.
   *
   * @param obj		the object
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized int get(Object obj, String property, int defValue) {
    return get(obj.getClass(), property, defValue);
  }

  /**
   * Returns the int value.
   *
   * @param cls		the class
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized int get(Class cls, String property, int defValue) {
    return getProperties().getInteger(createKey(cls, property), defValue);
  }

  /**
   * Returns the long value.
   *
   * @param obj		the object
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized long get(Object obj, String property, long defValue) {
    return get(obj.getClass(), property, defValue);
  }

  /**
   * Returns the long value.
   *
   * @param cls		the class
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized long get(Class cls, String property, long defValue) {
    return getProperties().getLong(createKey(cls, property), defValue);
  }

  /**
   * Returns the double value.
   *
   * @param obj		the object
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized double get(Object obj, String property, double defValue) {
    return get(obj.getClass(), property, defValue);
  }

  /**
   * Returns the double value.
   *
   * @param cls		the class
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized double get(Class cls, String property, double defValue) {
    return getProperties().getDouble(createKey(cls, property), defValue);
  }

  /**
   * Returns the Time value.
   *
   * @param obj		the object
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized Time get(Object obj, String property, Time defValue) {
    return get(obj.getClass(), property, defValue);
  }

  /**
   * Returns the Time value.
   *
   * @param cls		the class
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized Time get(Class cls, String property, Time defValue) {
    return getProperties().getTime(createKey(cls, property), defValue);
  }

  /**
   * Returns the Date value.
   *
   * @param obj		the object
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized Date get(Object obj, String property, Date defValue) {
    return get(obj.getClass(), property, defValue);
  }

  /**
   * Returns the Date value.
   *
   * @param cls		the class
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized Date get(Class cls, String property, Date defValue) {
    return getProperties().getDate(createKey(cls, property), defValue);
  }

  /**
   * Returns the DateTime value.
   *
   * @param obj		the object
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized DateTime get(Object obj, String property, DateTime defValue) {
    return get(obj.getClass(), property, defValue);
  }

  /**
   * Returns the DateTime value.
   *
   * @param cls		the class
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized DateTime get(Class cls, String property, DateTime defValue) {
    return getProperties().getDateTime(createKey(cls, property), defValue);
  }

  /**
   * Returns the Color value.
   *
   * @param obj		the object
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized Color get(Object obj, String property, Color defValue) {
    return get(obj.getClass(), property, defValue);
  }

  /**
   * Returns the Color value.
   *
   * @param cls		the class
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized Color get(Class cls, String property, Color defValue) {
    return getProperties().getColor(createKey(cls, property), defValue);
  }

  /**
   * Returns the Password value.
   *
   * @param obj		the object
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized BasePassword get(Object obj, String property, BasePassword defValue) {
    return get(obj.getClass(), property, defValue);
  }

  /**
   * Returns the Password value.
   *
   * @param cls		the class
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized BasePassword get(Class cls, String property, BasePassword defValue) {
    return getProperties().getPassword(createKey(cls, property), defValue);
  }

  /**
   * Returns the String value.
   *
   * @param obj		the object
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized String get(Object obj, String property, String defValue) {
    return get(obj.getClass(), property, defValue);
  }

  /**
   * Returns the String value.
   *
   * @param cls		the class
   * @param property	the property
   * @param defValue	the default value
   * @return		the stored value or the default value
   */
  public static synchronized String get(Class cls, String property, String defValue) {
    return getProperties().getProperty(createKey(cls, property), defValue);
  }

  /**
   * Returns the whether the value is present.
   *
   * @param obj		the object
   * @param property	the property
   * @return		true if available
   */
  public static synchronized boolean has(Object obj, String property) {
    return has(obj.getClass(), property);
  }

  /**
   * Returns the whether the value is present.
   *
   * @param cls		the class
   * @param property	the property
   * @return		true if available
   */
  public static synchronized boolean has(Class cls, String property) {
    return getProperties().hasKey(createKey(cls, property));
  }

  /**
   * Returns whether the settings are currently unsaved.
   *
   * @return		true if not yet saved
   */
  public synchronized static boolean isModified() {
    return m_Modified;
  }

  /**
   * Queues saving the settings after {@link #SECONDS_WAIT} seconds.
   */
  public synchronized static void queueSave() {
    if (m_SaveRunnable == null) {
      m_SaveRunnable = new DelayedActionRunnable(SECONDS_WAIT * 1000, 250);
      m_SaveThread = new Thread(m_SaveRunnable);
      m_SaveThread.start();
    }
    m_SaveRunnable.queue(new AbstractAction(m_SaveRunnable) {
      @Override
      public String execute() {
        m_SaveRunnable.stopExecution();
        m_SaveRunnable = null;
	return save();
      }
    });
  }

  /**
   * Stores the settings.
   *
   * @return		null if successful, otherwise error message
   */
  public synchronized static String save() {
    if (!getProperties().save(Environment.getInstance().createPropertiesFilename(new File(FILENAME).getName())))
      return "Failed to save panel settings!";
    m_Modified = false;
    return null;
  }
}
