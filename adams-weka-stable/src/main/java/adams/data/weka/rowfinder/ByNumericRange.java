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
 * ByNumericRange.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.rowfinder;

import adams.core.Index;
import adams.core.base.BaseInterval;
import adams.data.weka.WekaAttributeIndex;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Returns indices of rows which numeric value match the min/max.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ByNumericRange
  extends AbstractRowFinder {

  /** for serialization. */
  private static final long serialVersionUID = 235661615457187608L;

  /** the attribute index to work on. */
  protected WekaAttributeIndex m_AttributeIndex;

  /** the intervals. */
  protected BaseInterval[] m_Ranges;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the indices of rows of columns which values fall inside the minimum and maximum.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "att-index", "attributeIndex",
      new WekaAttributeIndex(Index.LAST));

    m_OptionManager.add(
      "range", "ranges",
      new BaseInterval[]{new BaseInterval(BaseInterval.ALL)});
  }

  /**
   * Sets the index of the column to perform the matching on.
   *
   * @param value	the index
   */
  public void setAttributeIndex(WekaAttributeIndex value) {
    m_AttributeIndex = value;
    reset();
  }

  /**
   * Returns the index of the column to perform the matching on.
   *
   * @return		the index
   */
  public WekaAttributeIndex getAttributeIndex() {
    return m_AttributeIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String attributeIndexTipText() {
    return "The index of the column to use for matching; " + m_AttributeIndex.getExample();
  }

  /**
   * Sets the intervals.
   *
   * @param value	the intervals
   */
  public void setRanges(BaseInterval[] value) {
    m_Ranges = value;
    reset();
  }

  /**
   * Returns the intervals.
   *
   * @return		the intervals
   */
  public BaseInterval[] getRanges() {
    return m_Ranges;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String rangesTipText() {
    return "The intervals to use for matching numeric values.";
  }

  /**
   *
   * Returns the rows of interest in the spreadsheet.
   *
   * @param data	the spreadsheet to inspect
   * @return		the rows of interest
   */
  @Override
  protected int[] doFindRows(Instances data) {
    TIntArrayList	result;
    int			i;
    int			n;
    int			index;
    Instance 		row;
    double		value;

    result = new TIntArrayList();

    m_AttributeIndex.setData(data);
    index = m_AttributeIndex.getIntIndex();
    if (index == -1)
      throw new IllegalStateException("Invalid index '" + m_AttributeIndex.getIndex() + "'?");
    if (!data.attribute(index).isNumeric())
      throw new IllegalStateException("Column at index '" + m_AttributeIndex.getIndex() + "' is not numeric!");

    for (i = 0; i < data.numInstances(); i++) {
      row = data.instance(i);
      if (row.isMissing(index))
        continue;

      value = row.value(index);

      for (n = 0; n < m_Ranges.length; n++) {
	if (m_Ranges[n].isInside(value)) {
	  result.add(i);
	  break;
	}
      }
    }

    return result.toArray();
  }
}
