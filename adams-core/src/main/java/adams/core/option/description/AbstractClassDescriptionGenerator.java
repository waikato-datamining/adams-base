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
 * AbstractClassDescriptionGenerator.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option.description;

import adams.core.ClassLister;
import adams.core.logging.LoggingObject;
import adams.core.option.DefaultCommandLineHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Ancestor for classes that generate descriptions from classes as nested Map objects.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractClassDescriptionGenerator
  extends LoggingObject
  implements ClassDescriptionGenerator {

  private static final long serialVersionUID = 3013330079402523952L;

  /** the cache for object class / handler relation. */
  protected static Map<Class,Class> m_Cache;

  /** the handlers (classes) currently available. */
  protected static Class[] m_GeneratorClasses;

  static {
    m_Cache          = new HashMap<>();
    m_GeneratorClasses = null;
  }

  /**
   * Initializes the handler.
   */
  protected AbstractClassDescriptionGenerator() {
    super();
    initialize();
  }

  /**
   * For initializing members.
   * <br>
   * Default implementation does nothing.
   */
  protected void initialize() {
  }

  /**
   * Checks whether this object is handled by this generator.
   *
   * @param obj the object to check
   * @return true if handled
   */
  @Override
  public boolean handles(Object obj) {
    return (obj != null) && handles(obj.getClass());
  }

  /**
   * Generates the description for the class.
   *
   * @param cls 	the class to process
   * @return 		the description, null if failed to generate
   */
  @Override
  public Map<String, Object> generate(Class cls) {
    Object	obj;

    try {
      obj = cls.getDeclaredConstructor().newInstance();
    }
    catch (Exception e) {
      // no default constructor, so we can't do anything
      return null;
    }

    return generate(obj);
  }

  /**
   * Initializes the handlers.
   */
  protected static synchronized void initHandlers() {
    if (m_GeneratorClasses != null)
      return;

    m_GeneratorClasses = ClassLister.getSingleton().getClasses(AbstractClassDescriptionGenerator.class);
  }

  /**
   * Returns a handler for the specified object.
   *
   * @param obj		the object to get a commandline handler for
   * @return		the handler
   */
  public static synchronized AbstractClassDescriptionGenerator getHandler(Object obj) {
    return getHandler(obj.getClass());
  }

  /**
   * Returns a handler for the specified class.
   *
   * @param cls		the class to get a commandline handler for
   * @return		the handler, null if none available
   */
  public static synchronized AbstractClassDescriptionGenerator getHandler(Class cls) {
    AbstractClassDescriptionGenerator result;
    AbstractClassDescriptionGenerator handler;
    int						i;

    result = null;

    initHandlers();

    // already cached?
    if (m_Cache.containsKey(cls)) {
      if (m_Cache.get(cls) == null)
	return null;
      try {
	result = (AbstractClassDescriptionGenerator) m_Cache.get(cls).getDeclaredConstructor().newInstance();
	return result;
      }
      catch (Exception e) {
	// ignored
      }
    }

    // find suitable handler
    for (i = 0; i < m_GeneratorClasses.length; i++) {
      if (m_GeneratorClasses[i] == DefaultCommandLineHandler.class)
	continue;
      try {
	handler = (AbstractClassDescriptionGenerator) m_GeneratorClasses[i].getDeclaredConstructor().newInstance();
	if (handler.handles(cls)) {
	  result = handler;
	  break;
	}
      }
      catch (Exception e) {
	// ignored
      }
    }

    // store in cache
    if (result != null)
      m_Cache.put(cls, result.getClass());
    else
      m_Cache.put(cls, null);

    return result;
  }
}
