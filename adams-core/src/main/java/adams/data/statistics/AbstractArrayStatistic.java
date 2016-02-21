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
 * AbstractArrayStatistic.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

import adams.core.ClassLister;
import adams.core.ShallowCopySupporter;
import adams.core.Utils;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Ancestor for classes that calculate statistics from arrays.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of element in the arrays
 */
public abstract class AbstractArrayStatistic<T extends Serializable>
  extends AbstractOptionHandler
  implements ShallowCopySupporter<AbstractArrayStatistic> {

  /** for serialization. */
  private static final long serialVersionUID = 5803268124112742362L;

  /**
   * The container for the generated statistic result.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   * @param <T> the type of element in the arrays
   */
  public static class StatisticContainer<T extends Serializable>
    implements Serializable {

    /** for serialization. */
    private static final long serialVersionUID = 4477965744045104127L;

    /** for the header row. */
    protected String[] m_Header;

    /** for the actual data. */
    protected Object[][] m_Data;

    /** for storing additional information. */
    protected Hashtable<String,Object> m_MetaData;

    /**
     * Initializes the container with the specified (data) dimensions.
     *
     * @param rows	the number of rows
     * @param cols	the number of cols
     */
    public StatisticContainer(int rows, int cols) {
      super();

      m_Header   = new String[cols];
      m_Data     = new Object[rows][cols];
      m_MetaData = new Hashtable<String,Object>();
    }

    /**
     * Clears the header and the data, as well as the meta-data.
     */
    public void clear() {
      int	i;
      int	n;

      for (i = 0; i < m_Header.length; i++)
	m_Header[i] = "";

      for (i = 0; i < m_Data.length; i++) {
	for (n = 0; n < m_Data[i].length; n++)
	  m_Data[i] = null;
      }

      m_MetaData.clear();
    }

    /**
     * Returns the number of columns.
     *
     * @return		the number of columns
     */
    public int getColumnCount() {
      return m_Header.length;
    }

    /**
     * Returns the number of (data) rows.
     *
     * @return		the number of (data) rows
     */
    public int getRowCount() {
      return m_Data.length;
    }

    /**
     * Sets the header for the given column.
     *
     * @param col	the column to set the header for
     * @param value	the value for the header
     */
    public void setHeader(int col, String value) {
      m_Header[col] = value;
    }

    /**
     * Returns the value of the header at the specified column.
     *
     * @param col	the column to get the header for
     * @return		the header string
     */
    public String getHeader(int col) {
      return m_Header[col];
    }

    /**
     * Sets the value of the specified cell.
     *
     * @param row	the row of the cell
     * @param col	the column of the cell
     * @param value	the new value for the cell
     */
    public void setCell(int row, int col, T value) {
      m_Data[row][col] = value;
    }

    /**
     * Returns the value of the specified cell.
     *
     * @param row	the row of the cell
     * @param col	the column of the cell
     * @return		the stored value, can be null
     */
    public T getCell(int row, int col) {
      return (T) m_Data[row][col];
    }

    /**
     * Sets the meta-data to store.
     *
     * @param key	the key for the value
     * @param value	the value to store
     */
    public void setMetaData(String key, Object value) {
      m_MetaData.put(key, value);
    }

    /**
     * Returns whether meta-data is associated with the specified key.
     *
     * @param key	the key of the value to look for
     * @return		true if meta-data available
     */
    public boolean hasMetaData(String key) {
      return m_MetaData.containsKey(key);
    }

    /**
     * Returns the meta-data value associated with the specified key.
     *
     * @param key	the key of the value to retrieve
     * @return		the associated value, null if not found
     */
    public Object getMetaData(String key) {
      return m_MetaData.get(key);
    }

    /**
     * Returns all the keys of the stored meta-data.
     *
     * @return		the keys
     */
    public Enumeration<String> keysMetaData() {
      return m_MetaData.keys();
    }

    /**
     * Generates a SpreadSheet object from the stored data.
     *
     * @return		the generated spreadsheet
     */
    public SpreadSheet toSpreadSheet() {
      SpreadSheet	result;
      Row		row;
      Cell		cell;
      int		i;
      int		n;

      result = new DefaultSpreadSheet();

      // header
      row = result.getHeaderRow();
      for (i = 0; i < m_Header.length; i++) {
	cell = row.addCell("" + (i+1));
	cell.setContent(m_Header[i]);
      }

      // data
      for (n = 0; n < m_Data.length; n++) {
	row = result.addRow("" + (n+1));

	for (i = 0; i < m_Data[n].length; i++) {
	  cell = row.addCell("" + (i+1));
	  if (m_Data[n][i] != null)
	    cell.setContent("" + (T) m_Data[n][i]);
	}
      }

      return result;
    }

    /**
     * Returns the stored data in CSV format.
     *
     * @return		the data as CSV string
     */
    @Override
    public String toString() {
      return toSpreadSheet().toString();
    }
  }

  /** for storing the arrays. */
  protected List<T[]> m_Data;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Data = new ArrayList<T[]>();
  }

  /**
   * Removes all data currently stored.
   */
  public void clear() {
    m_Data.clear();
  }

  /**
   * Returns the number of arrays stored.
   *
   * @return		the number of arrays
   */
  public int size() {
    return m_Data.size();
  }

  /**
   * Adds the array at the end.
   *
   * @param data	the array to add
   */
  public void add(T[] data) {
    m_Data.add(data);
  }

  /**
   * Returns the array at the specified location.
   *
   * @param index	the index of the array
   * @return		the array
   */
  public T[] get(int index) {
    return m_Data.get(index);
  }

  /**
   * Replaces the array at the specified location.
   *
   * @param index	the index of the array
   * @param data	the new array
   * @return		the old array
   */
  public T[] set(int index, T[] data) {
    return m_Data.set(index, data);
  }

  /**
   * Removes the array at the specified location.
   *
   * @param index	the index of the array
   * @return		the deleted array
   */
  public T[] remove(int index) {
    return m_Data.remove(index);
  }

  /**
   * Returns the minimum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the minimum number, -1 for unbounded
   */
  public abstract int getMin();

  /**
   * Returns the maximum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the maximum number, -1 for unbounded
   */
  public abstract int getMax();

  /**
   * Checks whether all the arrays have the same length.
   */
  protected void checkEqualLength() {
    int		i;
    int		len;

    len = 0;
    for (i = 0; i < size(); i++) {
      if (i == 0) {
	len = get(i).length;
	continue;
      }

      if (len != get(i).length)
	throw new IllegalStateException(
	    getClass().getName() + ": Arrays #1 and #" + (i+1) + " differ in length: " + len + " != " + get(i).length);
    }
  }

  /**
   * Checks whether all pre-conditions are met. Throws an IllegalStateException
   * otherwise.
   */
  protected void check() {
    if (getMin() != -1) {
      if (size() < getMin())
	throw new IllegalStateException(
	    getClass().getName() + ": Requires at least " + getMin()  + " arrays, current: " + size());
    }

    if (getMax() != -1) {
      if (size() > getMax())
	throw new IllegalStateException(
	    getClass().getName() + ": Requires at most " + getMax()  + " arrays, current: " + size());
    }

    if ((getMin() != -1) && (getMax() != -1)) {
      if (getMin() > getMax())
	throw new IllegalStateException(
	    getClass().getName() + ": Min must be smaller than max (min=" + getMin() + ", max=" + getMax() + ")!");
    }

    if (this instanceof EqualLengthArrayStatistic)
      checkEqualLength();
  }

  /**
   * Generates the actual result.
   *
   * @return		the generated result
   */
  protected abstract StatisticContainer doCalculate();

  /**
   * Calculates the statistics and returns the result container.
   *
   * @return		the generated result
   */
  public StatisticContainer calculate() {
    check();
    return doCalculate();
  }

  /**
   * Returns a string representation of the current setup.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    StringBuilder	result;
    int			i;

    result = new StringBuilder(super.toString());
    result.append("\nStored arrays:\n");

    for (i = 0; i < size(); i++) {
      result.append((i+1) + ".: ");
      result.append(Utils.arrayToString(get(i)));
      result.append("\n");
    }

    return result.toString();
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractArrayStatistic shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractArrayStatistic shallowCopy(boolean expand) {
    return (AbstractArrayStatistic) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of statistics.
   *
   * @return		the statistic classnames
   */
  public static String[] getStatistics() {
    return ClassLister.getSingleton().getClassnames(AbstractArrayStatistic.class);
  }

  /**
   * Instantiates the statistic with the given options.
   *
   * @param classname	the classname of the statistic to instantiate
   * @param options	the options for the statistic
   * @return		the instantiated statistic or null if an error occurred
   */
  public static AbstractArrayStatistic forName(String classname, String[] options) {
    AbstractArrayStatistic	result;

    try {
      result = (AbstractArrayStatistic) OptionUtils.forName(AbstractArrayStatistic.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the statistic from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			statistic to instantiate
   * @return		the instantiated statistic
   * 			or null if an error occurred
   */
  public static AbstractArrayStatistic forCommandLine(String cmdline) {
    return (AbstractArrayStatistic) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
