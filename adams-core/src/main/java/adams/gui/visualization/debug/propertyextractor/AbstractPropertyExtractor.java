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
 * AbstractPropertyExtractor.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.propertyextractor;

import java.util.Hashtable;

import adams.core.ClassLister;
import adams.core.CleanUpHandler;

/**
 * Ancestor for property extractors, used for populating the object tree.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPropertyExtractor
  implements CleanUpHandler {

  /** the cache for object class / extractor relation. */
  protected static Hashtable<Class,Class> m_Cache;

  /** the extractors (classnames) currently available. */
  protected static String[] m_Extractors;

  /** the extractors (classes) currently available. */
  protected static Class[] m_ExtractorClasses;

  /** the current object to inspect. */
  protected Object m_Current;

  static {
    m_Cache            = new Hashtable<Class,Class>();
    m_Extractors       = null;
    m_ExtractorClasses = null;
  }

  /**
   * Initializes the extractors.
   */
  protected static synchronized void initExtractors() {
    int		i;

    if (m_Extractors != null)
      return;

    m_Extractors       = ClassLister.getSingleton().getClassnames(AbstractPropertyExtractor.class);
    m_ExtractorClasses = new Class[m_Extractors.length];
    for (i = 0; i < m_Extractors.length; i++) {
      try {
	m_ExtractorClasses[i] = Class.forName(m_Extractors[i]);
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate inspection extractor '" + m_Extractors[i] + "': ");
	e.printStackTrace();
      }
    }
  }

  /**
   * Returns a extractor for the specified object.
   *
   * @param obj		the object to get an extractor for
   * @return		the extractor
   */
  public static synchronized AbstractPropertyExtractor getExtractor(Object obj) {
    return getExtractor(obj.getClass());
  }

  /**
   * Returns a extractor for the specified class.
   *
   * @param cls		the class to get an extractor for
   * @return		the extractor
   */
  public static synchronized AbstractPropertyExtractor getExtractor(Class cls) {
    AbstractPropertyExtractor	result;
    AbstractPropertyExtractor	extractor;
    int				i;

    result = null;

    initExtractors();

    // already cached?
    if (m_Cache.containsKey(cls)) {
      try {
	result = (AbstractPropertyExtractor) m_Cache.get(cls).newInstance();
	return result;
      }
      catch (Exception e) {
	// ignored
	result = null;
      }
    }

    // find suitable extractor
    for (i = 0; i < m_ExtractorClasses.length; i++) {
      if (m_ExtractorClasses[i] == DefaultPropertyExtractor.class)
	continue;
      try {
	extractor = (AbstractPropertyExtractor) m_ExtractorClasses[i].newInstance();
	if (extractor.handles(cls)) {
	  result = extractor;
	  break;
	}
      }
      catch (Exception e) {
	// ignored
      }
    }

    if (result == null)
      result = new DefaultPropertyExtractor();

    // store in cache
    m_Cache.put(cls, result.getClass());

    return result;
  }
  
  /**
   * Checks whether this extractor actually handles this type of class.
   * 
   * @param cls		the class to check
   * @return		true if the extractor handles the object/class
   */
  public abstract boolean handles(Class cls);
  
  /**
   * Sets the current object to inspect.
   * 
   * @param value	the object to inspect
   * @see		#initialize()
   */
  public void setCurrent(Object value) {
    m_Current = value;
    initialize();
  }
  
  /**
   * Returns the current object that is inspected.
   * 
   * @return		the current object
   */
  public Object getCurrent() {
    return m_Current;
  }

  /**
   * Initializes the extractor.
   * <p/>
   * Default implementation does nothing.
   */
  protected void initialize() {
  }  
  
  /**
   * The number of properties that are available.
   * 
   * @return		the number of properties
   */
  public abstract int size();
  
  /**
   * Checks whether the specified property has a value.
   * 
   * @param index	the index of the property to check
   * @return		true if a value exists
   */
  public boolean hasValue(int index) {
    return (getValue(index) != null);
  }
  
  /**
   * Returns the current value of the specified property.
   * 
   * @param index	the index of the property to retrieve
   * @return		the current value of the property
   */
  public abstract Object getValue(int index);
  
  /**
   * Returns the label for the specified property.
   * 
   * @param index	the index of the property to get the label for
   * @return		the label for the property
   */
  public abstract String getLabel(int index);

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_Current = null;
  }
}
