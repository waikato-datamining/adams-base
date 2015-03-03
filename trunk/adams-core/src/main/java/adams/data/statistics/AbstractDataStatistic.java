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
 * DataStatistic.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.statistics;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import adams.core.CleanUpHandler;
import adams.core.logging.LoggingObject;
import adams.data.container.DataContainer;

/**
 * A class for statistics about data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data the statistic processes
 */
public abstract class AbstractDataStatistic<T extends DataContainer>
  extends LoggingObject
  implements InformativeStatistic, CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = 7783784686641582858L;

  /** contains the statistics. */
  protected Hashtable<String,Double> m_Statistics;

  /** contains the ordered names. */
  protected List<String> m_Names;

  /** indicates whether statistics have been calculated. */
  protected boolean m_Calculated;

  /** the underlying chromatogram. */
  protected T m_Data;

  /**
   * Initializes the statistic.
   */
  public AbstractDataStatistic() {
    super();

    m_Statistics = new Hashtable<String,Double>();
    m_Names      = new ArrayList<String>();
    m_Calculated = false;
    m_Data       = null;
  }

  /**
   * Initializes the statistic.
   *
   * @param data	the data to initialize with
   */
  public AbstractDataStatistic(T data) {
    this();
    setData(data);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public abstract String globalInfo();

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    clear();
  }

  /**
   * Removes the stored content.
   */
  protected void clear() {
    m_Calculated = false;
    m_Statistics.clear();
    m_Names.clear();
  }

  /**
   * Re-calculates the statistics. Derived classes must override this method
   * if they want to return anything via statisticNames() or
   * getStatistic(String), the default implementation does nothing.
   *
   * @see #statisticNames()
   * @see #getStatistic(String)
   * @see #m_Names
   * @see #m_Statistics
   */
  protected void calculate() {
    clear();
  }

  /**
   * Sets the data to use as basis for the calculations.
   *
   * @param value	the data to use, can be null
   */
  public void setData(T value) {
    m_Calculated = false;
    m_Data       = value;
  }

  /**
   * Returns the currently stored data container.
   *
   * @return		the data, can be null
   */
  public T getData() {
    return m_Data;
  }

  /**
   * Adds the name/value pair to the internal list.
   *
   * @param name	the name of the statistic
   * @param value	the corresponding value
   */
  public void add(String name, double value) {
    if (!m_Statistics.containsKey(name))
      m_Names.add(name);
    m_Statistics.put(name, value);
  }

  /**
   * Returns a double primitive from the given Number. In case of null,
   * Double.NaN is returned.
   *
   * @param value	the number to get the double value from
   * @return		the double value
   */
  protected double numberToDouble(Number value) {
    if (value == null)
      return Double.NaN;
    else
      return value.doubleValue();
  }

  /**
   * Returns all the names of the available statistical values.
   *
   * @return		the enumeration of names
   */
  public Iterator<String> statisticNames() {
    if (!m_Calculated)
      calculate();

    return new ArrayList<String>(m_Names).iterator();
  }

  /**
   * Returns the statistical value for the given statistic name.
   *
   * @param name	the name of the statistical value
   * @return		the corresponding value
   */
  public double getStatistic(String name) {
    double	result;

    if (!m_Calculated)
      calculate();

    if (m_Statistics.containsKey(name)) {
      result = m_Statistics.get(name);
    }
    else {
      getLogger().severe("Statistic '" + name + "' is unknown!");
      result = Double.NaN;
    }

    return result;
  }

  /**
   * Returns the container as string.
   *
   * @return		a string representation
   */
  @Override
  public String toString() {
    StringBuilder	result;
    Iterator<String>	names;
    String		name;

    result = new StringBuilder();
    names  = statisticNames();
    while (names.hasNext()) {
	name = names.next();
	result.append(name + ": " + getStatistic(name) + "\n");
    }

    return result.toString();
  }
}
