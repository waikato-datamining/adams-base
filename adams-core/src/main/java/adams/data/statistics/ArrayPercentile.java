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
 * ArrayPercentile.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

/**
 <!-- globalinfo-start -->
 * Determines the percentile for an array.
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
 * <pre>-percentile &lt;double&gt; (property: percentile)
 * &nbsp;&nbsp;&nbsp;The percentile to calculate (0.0-1.0); eg, use 0.75 for the 75th percentile.
 * &nbsp;&nbsp;&nbsp;default: 0.75
 * &nbsp;&nbsp;&nbsp;minimum: 1.0E-5
 * &nbsp;&nbsp;&nbsp;maximum: 0.99999
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the data to process
 */
public class ArrayPercentile<T extends Number>
  extends AbstractArrayStatistic<T> {

  /** for serialization. */
  private static final long serialVersionUID = 8011213325443103860L;

  /** the percentile to return. */
  protected double m_Percentile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Determines the percentile for an array.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "percentile", "percentile",
	    0.75, 0.00001, 0.99999);
  }

  /**
   * Sets the percentile to calculate.
   *
   * @param value 	the percentile
   */
  public void setPercentile(double value) {
    m_Percentile = value;
    reset();
  }

  /**
   * Returns the percentile to calculate.
   *
   * @return 		true if the index is returned instead of the value
   */
  public double getPercentile() {
    return m_Percentile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String percentileTipText() {
    return "The percentile to calculate (0.0-1.0); eg, use 0.75 for the 75th percentile.";
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
    int				n;
    String 			prefix;
    Percentile<Double>		perc;

    result = new StatisticContainer<Number>(1, size());

    prefix = "percentile-" + m_Percentile;
    if (size() > 1)
      prefix += "-";

    perc = new Percentile<Double>();
    for (i = 0; i < size(); i++) {
      if (size() > 1)
	result.setHeader(i, prefix + (i+1));
      else
	result.setHeader(i, prefix);

      perc.clear();
      for (n = 0; n < get(i).length; n++)
	perc.add(get(i)[n].doubleValue());

      result.setCell(0, i, perc.getPercentile(m_Percentile));
    }

    return result;
  }
}
