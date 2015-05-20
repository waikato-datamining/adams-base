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
 * MovingAverage.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.ArrayList;
import java.util.List;

import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * Computes the average on a window of numbers that have passed through and outputs the average.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-window-size &lt;int&gt; (property: windowSize)
 * &nbsp;&nbsp;&nbsp;The number of data points to use for computing the average.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MovingAverage
  extends AbstractConversion
  implements StreamConversion {

  /** for serialization. */
  private static final long serialVersionUID = -4092302172529978800L;

  /** the size of the window to compute the average on. */
  protected int m_WindowSize;

  /** the values to compute the average on. */
  protected List<Number> m_Queue;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Computes the average on a window of numbers that have passed through "
      + "and outputs the average.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "window-size", "windowSize",
	    10, 1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Queue = new ArrayList<Number>();
  }

  /**
   * Resets the members.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Queue.clear();
  }

  /**
   * Sets the window size.
   *
   * @param value	the window size
   */
  public void setWindowSize(int value) {
    if (value > 0) {
      m_WindowSize = value;
      reset();
    }
    else {
      getLogger().severe("Minimum window size is 1, provided: " + value);
    }
  }

  /**
   * Returns the window size.
   *
   * @return 		the window zie
   */
  public int getWindowSize() {
    return m_WindowSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String windowSizeTipText() {
    return "The number of data points to use for computing the average.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Number.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Double.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Double	result;

    result = null;

    m_Queue.add((Number) m_Input);
    while (m_Queue.size() > m_WindowSize)
      m_Queue.remove(0);

    if (m_Queue.size() == m_WindowSize)
      result = StatUtils.mean(m_Queue.toArray(new Number[m_Queue.size()]));

    return result;
  }
}
