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
 * NewInstance.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core;

import adams.core.logging.LoggingObject;
import adams.env.Environment;
import adams.env.NewInstanceDefinition;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * For instantiating objects, with or without custom constructor.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NewInstance
  extends LoggingObject {

  /** the filename. */
  public final static String FILENAME = "NewInstance.props";

  /** the suffix for the type. */
  public final static String SUFFIX_TYPE = ".type";

  /** the suffix for the type. */
  public final static String SUFFIX_VALUE = ".value";

  private static final long serialVersionUID = 6122003243388578002L;

  /** the singleton. */
  protected static NewInstance m_Singleton;

  /** the placeholders. */
  protected Properties m_Properties;

  /** the classnames that have a custom constructor. */
  protected Set<String> m_Classnames;

  /** the classes that have a custom constructor. */
  protected Set<Class> m_Classes;

  /** the constructors. */
  protected Map<Class,Constructor> m_Constructors;

  /** the constructor default values. */
  protected Map<Class,Object[]> m_DefaultValues;

  /**
   * Initializes the classlister.
   */
  private NewInstance() {
    super();

    initialize();
  }

  /**
   * loads the props file and interpretes it.
   */
  protected void initialize() {
    Class	cls;

    m_Classes       = new HashSet<>();
    m_Classnames    = new HashSet<>();
    m_Constructors  = new HashMap<>();
    m_DefaultValues = new HashMap<>();

    if (m_Properties == null) {
      try {
	m_Properties = Environment.getInstance().read(NewInstanceDefinition.KEY);
      }
      catch (Exception e) {
	m_Properties = new Properties();
      }
    }

    for (String key: m_Properties.keySetAll()) {
      if (key.endsWith(SUFFIX_TYPE) || key.endsWith(SUFFIX_VALUE))
	continue;
      try {
	cls = Class.forName(key);
	m_Classnames.add(key);
	m_Classes.add(cls);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to instantiate class: " + key);
      }
    }
  }

  /**
   * Checks whether the classname is registered with a custom instantiation.
   *
   * @param classname	the classname to check
   * @return		true if custom instantiation
   */
  public boolean hasCustomInstantiation(String classname) {
    return m_Classnames.contains(classname);
  }

  /**
   * Checks whether the class is registered with a custom instantiation.
   *
   * @param cls		the class to check
   * @return		true if custom instantiation
   */
  public boolean hasCustomInstantiation(Class cls) {
    return m_Classes.contains(cls);
  }

  /**
   * Returns a new object instance.
   *
   * @param classname	the class to instantiate
   * @return		the instance, null if failed to instantiate
   */
  public Object newObject(String classname) {
    try {
      return newObject(Class.forName(classname));
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to instantiate class: " + classname, e);
      return null;
    }
  }

  /**
   * Configures constructor and default values.
   *
   * @param cls		the class to configure
   * @return		true if successfully configured (or already present)
   * @throws Exception	if configuration fails (eg instantiating a custom class)
   */
  protected boolean configure(Class cls) throws Exception {
    int		numArgs;
    int		i;
    Class[]	params;
    Object[]	values;
    Constructor	constr;
    String	type;
    String	value;

    if (m_Constructors.containsKey(cls))
      return true;

    numArgs = m_Properties.getInteger(cls.getName(), 0);
    if (numArgs < 1) {
      getLogger().severe("No custom arguments specified for class " + Utils.classToString(cls) + "?");
      return false;
    }

    params = new Class[numArgs];
    values = new Object[numArgs];
    for (i = 0; i < numArgs; i++) {
      type = m_Properties.getProperty(cls.getName() + "." + i + SUFFIX_TYPE, null);
      if (type == null) {
        getLogger().severe("No type specified for argument #" + i + " for class " + Utils.classToString(cls) + "?");
        return false;
      }
      value = m_Properties.getProperty(cls.getName() + "." + i + SUFFIX_VALUE, null);
      if (value == null) {
        getLogger().severe("No value specified for argument #" + i + " for class " + Utils.classToString(cls) + "?");
        return false;
      }
      switch (type) {
	case "boolean":
	  params[i] = Boolean.TYPE;
	  values[i] = Boolean.parseBoolean(value);
	  break;
	case "Boolean":
	  params[i] = Boolean.class;
	  values[i] = Boolean.parseBoolean(value);
	  break;
	case "char":
	  params[i] = Character.TYPE;
	  values[i] = value.charAt(0);
	  break;
	case "Character":
	  params[i] = Character.class;
	  values[i] = value.charAt(0);
	  break;
	case "String":
	  params[i] = String.class;
	  values[i] = value;
	  break;
	case "byte":
	  params[i] = Byte.TYPE;
	  values[i] = Byte.parseByte(value);
	  break;
	case "Byte":
	  params[i] = Byte.class;
	  values[i] = Byte.parseByte(value);
	  break;
	case "short":
	  params[i] = Short.TYPE;
	  values[i] = Short.parseShort(value);
	  break;
	case "Short":
	  params[i] = Short.class;
	  values[i] = Short.parseShort(value);
	  break;
	case "int":
	  params[i] = Integer.TYPE;
	  values[i] = Integer.parseInt(value);
	  break;
	case "Integer":
	  params[i] = Integer.class;
	  values[i] = Integer.parseInt(value);
	  break;
	case "long":
	  params[i] = Long.TYPE;
	  values[i] = Long.parseLong(value);
	  break;
	case "Long":
	  params[i] = Long.class;
	  values[i] = Long.parseLong(value);
	  break;
	case "float":
	  params[i] = Float.TYPE;
	  values[i] = Float.parseFloat(value);
	  break;
	case "Float":
	  params[i] = Float.class;
	  values[i] = Float.parseFloat(value);
	  break;
	case "double":
	  params[i] = Double.TYPE;
	  values[i] = Double.parseDouble(value);
	  break;
	case "Double":
	  params[i] = Double.class;
	  values[i] = Double.parseDouble(value);
	  break;
	default:
	  params[i] = Class.forName(type);
	  values[i] = newObject(value);
	  break;
      }
    }
    constr = cls.getConstructor(params);
    m_Constructors.put(cls, constr);
    m_DefaultValues.put(cls, values);

    return (constr != null);
  }

  /**
   * Returns a new object instance.
   *
   * @param cls		the class to instantiate
   * @return		the instance, null if failed to instantiate
   */
  public Object newObject(Class cls) {
    if (hasCustomInstantiation(cls)) {
      try {
	if (!configure(cls)) {
	  getLogger().severe("Cannot instantiate class: " + Utils.classToString(cls));
	  return null;
	}
	return m_Constructors.get(cls).newInstance(m_DefaultValues.get(cls));
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to instantiate class with custom constructor: " + Utils.classToString(cls), e);
	return null;
      }
    }
    else {
      try {
	return cls.newInstance();
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to create instance of class: " + Utils.classToString(cls), e);
	return null;
      }
    }
  }

  /**
   * Returns the singleton instance of the NewInstance.
   *
   * @return		the singleton
   */
  public static synchronized NewInstance getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new NewInstance();

    return m_Singleton;
  }

  /**
   * Returns a new object instance.
   *
   * @param classname	the class to instantiate
   * @return		the instance, null if failed to instantiate
   */
  public static Object newInstance(String classname) {
    return getSingleton().newObject(classname);
  }

  /**
   * Returns a new object instance.
   *
   * @param cls		the class to instantiate
   * @return		the instance, null if failed to instantiate
   */
  public static Object newInstance(Class cls) {
    return getSingleton().newObject(cls);
  }
}
