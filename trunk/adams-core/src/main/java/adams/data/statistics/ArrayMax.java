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
 * ArrayMax.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

/**
 <!-- globalinfo-start -->
 * Determines the max value in a numeric array.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-return-index (property: returnIndex)
 * &nbsp;&nbsp;&nbsp;If set to true, then the 0-based index of the value is returned instead
 * &nbsp;&nbsp;&nbsp;the actual value.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the data to process
 */
public class ArrayMax<T extends Number>
  extends AbstractArrayStatistic<T> {

  /** for serialization. */
  private static final long serialVersionUID = -4238169529147159807L;

  /** whether to return the index instead of the actual value. */
  protected boolean m_ReturnIndex;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Determines the max value in a numeric array.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "return-index", "returnIndex",
	    false);
  }

  /**
   * Sets whether to return the index instead of the actual value.
   *
   * @param value 	if true then the index is returned instead of the value
   */
  public void setReturnIndex(boolean value) {
    m_ReturnIndex = value;
    reset();
  }

  /**
   * Returns whether to return the index instead of the actual value.
   *
   * @return 		true if the index is returned instead of the value
   */
  public boolean getReturnIndex() {
    return m_ReturnIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String returnIndexTipText() {
    return "If set to true, then the 0-based index of the value is returned instead the actual value.";
  }

  /**
   * Returns the minimum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the minimum number, -1 for unbounded
   */
  public int getMin() {
    return 1;
  }

  /**
   * Returns the maximum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the maximum number, -1 for unbounded
   */
  public int getMax() {
    return -1;
  }

  /**
   * Generates the actual result.
   *
   * @return		the generated result
   */
  protected StatisticContainer doCalculate() {
    StatisticContainer<Number>	result;
    int				i;
    String 			prefix;

    result = new StatisticContainer<Number>(1, size());

    prefix = "max";
    if (m_ReturnIndex)
      prefix += "-index";
    if (size() > 1)
      prefix += "-";

    for (i = 0; i < size(); i++) {
      if (size() > 1)
	result.setHeader(i, prefix + (i+1));
      else
	result.setHeader(i, prefix);

      if (m_ReturnIndex)
	result.setCell(0, i, StatUtils.maxIndex(get(i)));
      else
	result.setCell(0, i, StatUtils.max(get(i)));
    }

    return result;
  }
}
