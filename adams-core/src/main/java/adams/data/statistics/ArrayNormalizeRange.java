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
 * ArrayNormalizeRange.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

/**
 <!-- globalinfo-start -->
 * Normalizes the array(s) to the specified lower and upper bound.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-lower &lt;double&gt; (property: lower)
 * &nbsp;&nbsp;&nbsp;The lower bound to use.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 *
 * <pre>-upper &lt;double&gt; (property: upper)
 * &nbsp;&nbsp;&nbsp;The upper bound to use.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the data to process
 */
public class ArrayNormalizeRange<T extends Number>
  extends AbstractArrayStatistic<T>
  implements EqualLengthArrayStatistic {

  /** for serialization. */
  private static final long serialVersionUID = -5911270089583842477L;

  /** the lower bound. */
  protected double m_Lower;

  /** the upper bound. */
  protected double m_Upper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Normalizes the array(s) to the specified lower and upper bound.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "lower", "lower",
      0.0);

    m_OptionManager.add(
      "upper", "upper",
      1.0);
  }

  /**
   * Sets the lower bound to use.
   *
   * @param value the bound
   */
  public void setLower(double value) {
    m_Lower = value;
    reset();
  }

  /**
   * Returns the lower bound in use.
   *
   * @return the bound
   */
  public double getLower() {
    return m_Lower;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String lowerTipText() {
    return "The lower bound to use.";
  }

  /**
   * Sets the upper bound to use.
   *
   * @param value the bound
   */
  public void setUpper(double value) {
    m_Upper = value;
    reset();
  }

  /**
   * Returns the upper bound in use.
   *
   * @return the bound
   */
  public double getUpper() {
    return m_Upper;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String upperTipText() {
    return "The upper bound to use.";
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
    StatisticContainer<Number>	result;
    int				i;
    int				n;
    String 			prefix;
    Double[]			normalized;

    result = new StatisticContainer<>(getLength(), size());

    prefix = "normalized-range";
    if (size() > 2)
      prefix += "-";

    for (i = 0; i < size(); i++) {
      if (size() > 1)
	result.setHeader(i, prefix + "1-" + (i+1));
      else
	result.setHeader(i, prefix);

      normalized = StatUtils.normalizeRange(get(i), m_Lower, m_Upper);
      if (normalized != null) {
	for (n = 0; n < getLength(); n++)
	  result.setCell(n, i, normalized[n]);
      }
      else {
	getLogger().severe("Failed to normalize array!");
      }
    }

    return result;
  }
}
