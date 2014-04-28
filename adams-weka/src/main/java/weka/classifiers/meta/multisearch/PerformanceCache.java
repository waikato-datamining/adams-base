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
 * PerformanceCache.java
 * Copyright (C) 2008-2010 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta.multisearch;

import java.io.Serializable;
import java.util.Hashtable;

import weka.core.setupgenerator.Point;

/**
 * Represents a simple cache for performance objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PerformanceCache
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 5838863230451530252L;

  /** the cache for points in the space that got calculated. */
  protected Hashtable<String,Performance> m_Cache = new Hashtable<String,Performance>();

  /**
   * returns the ID string for a cache item.
   *
   * @param cv	the number of folds in the cross-validation
   * @param values	the point in the space
   * @return		the ID string
   */
  protected String getID(int cv, Point<Object> values) {
    String	result;
    int	i;

    result = "" + cv;

    for (i = 0; i < values.dimensions(); i++)
      result += "\t" + values.getValue(i);

    return result;
  }

  /**
   * checks whether the point was already calculated once.
   *
   * @param cv	the number of folds in the cross-validation
   * @param values	the point in the space
   * @return		true if the value is already cached
   */
  public boolean isCached(int cv, Point<Object> values) {
    return (get(cv, values) != null);
  }

  /**
   * returns a cached performance object, null if not yet in the cache.
   *
   * @param cv	the number of folds in the cross-validation
   * @param values	the point in the space
   * @return		the cached performance item, null if not in cache
   */
  public Performance get(int cv, Point<Object> values) {
    return m_Cache.get(getID(cv, values));
  }

  /**
   * adds the performance to the cache.
   *
   * @param cv	the number of folds in the cross-validation
   * @param p		the performance object to store
   */
  public void add(int cv, Performance p) {
    m_Cache.put(getID(cv, p.getValues()), p);
  }

  /**
   * returns a string representation of the cache.
   *
   * @return		the string representation of the cache
   */
  public String toString() {
    return m_Cache.toString();
  }
}