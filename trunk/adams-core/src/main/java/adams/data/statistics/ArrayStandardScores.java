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
 * ArrayStandardScores.java
 * Copyright (C) 2010-2011 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;


/**
 <!-- globalinfo-start -->
 * Calculates the standard scores (or z scores) of the provided arrays.The arrays must be numeric, of course.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-always-first-stats (property: useAlwaysFirstStats)
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the data to process
 */
public class ArrayStandardScores<T extends Number>
  extends AbstractOptionalSampleArrayStatistic<T>
  implements EqualLengthArrayStatistic {

  /** for serialization. */
  private static final long serialVersionUID = 3595293227007460735L;

  /** whether to always use the mean/stdev from the first array. */
  protected boolean m_UseAlwaysFirstStats;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Calculates the standard scores (or z scores) of the provided arrays."
      + "The arrays must be numeric, of course.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "always-first-stats", "useAlwaysFirstStats",
	    false);
  }

  /**
   * Sets whether to always use the mean/stdev of the first array.
   *
   * @param value 	if true then the mean/stdev of the first array is always used
   */
  public void setUseAlwaysFirstStats(boolean value) {
    m_UseAlwaysFirstStats = value;
    reset();
  }

  /**
   * Returns whether to always use the mean/stdev of the first array.
   *
   * @return 		true if the mean/stdev of first array is always used
   */
  public boolean getUseAlwaysFirstStats() {
    return m_UseAlwaysFirstStats;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useAlwaysFirstStdevTipText() {
    return "If set to true, then the mean/stdev of the first array are always used to calculate the scores.";
  }

  /**
   * Returns the length of the stored arrays.
   *
   * @return		the length of the arrays, -1 if none stored
   */
  public int getLength() {
    if (size() > 0)
      return get(0).length;
    else
      return -1;
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
    StatisticContainer<Double>	result;
    int				i;
    String			prefix;
    double[]			scores;
    int				n;
    int				start;

    prefix = "z-scores";
    if (getIsSample())
      prefix += "-sample";
    else
      prefix += "-pop.";

    start  = 0;
    if (m_UseAlwaysFirstStats)
      start++;
    result = new StatisticContainer<Double>(getLength(), size() - start);

    for (i = start; i < size(); i++) {
      if (size() > 1) {
	if (m_UseAlwaysFirstStats)
	  result.setHeader(i - start, prefix + " 1-" + (i+1));
	else
	  result.setHeader(i - start, prefix + " " + (i+1));
      }
      else {
	result.setHeader(i - start, prefix);
      }

      if (!m_UseAlwaysFirstStats)
	scores = StatUtils.standardScores(get(i), getIsSample());
      else
	scores = StatUtils.standardScores(get(0), get(i), getIsSample());
      for (n = 0; n < scores.length; n++)
	result.setCell(n, i - start, scores[n]);
    }

    return result;
  }
}
