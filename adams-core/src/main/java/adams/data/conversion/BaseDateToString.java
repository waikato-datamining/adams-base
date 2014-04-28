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
 * BaseDateToString.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.base.BaseDate;

/**
 <!-- globalinfo-start -->
 * Turns a BaseDate format string into a String, evaluted using user-supplied start and end dates (ignored if future INF dates).
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
 * <pre>-start &lt;adams.core.base.BaseDate&gt; (property: start)
 * &nbsp;&nbsp;&nbsp;The start date to use in the evaluation.
 * &nbsp;&nbsp;&nbsp;default: -INF
 * </pre>
 *
 * <pre>-end &lt;adams.core.base.BaseDate&gt; (property: end)
 * &nbsp;&nbsp;&nbsp;The end date to use in the evaluation.
 * &nbsp;&nbsp;&nbsp;default: +INF
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseDateToString
  extends AbstractConversionToString {

  /** for serialization. */
  private static final long serialVersionUID = 6744245717394758406L;

  /** the start date to use as basis for the evaluation. */
  protected BaseDate m_Start;

  /** the end date to use as basis for the evaluation. */
  protected BaseDate m_End;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Turns a BaseDate format string into a String, evaluted using "
      + "user-supplied start and end dates (ignored if future INF dates).";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "start", "start",
	    new BaseDate(BaseDate.INF_PAST));

    m_OptionManager.add(
	    "end", "end",
	    new BaseDate(BaseDate.INF_FUTURE));
  }

  /**
   * Sets the start date to use in the evaluation.
   *
   * @param value	the date
   */
  public void setStart(BaseDate value) {
    m_Start = value;
    reset();
  }

  /**
   * Returns the start date used in the evaluation.
   *
   * @return		the date
   */
  public BaseDate getStart() {
    return m_Start;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startTipText() {
    return "The start date to use in the evaluation.";
  }

  /**
   * Sets the end date to use in the evaluation.
   *
   * @param value	the date
   */
  public void setEnd(BaseDate value) {
    m_End = value;
    reset();
  }

  /**
   * Returns the end date used in the evaluation.
   *
   * @return		the date
   */
  public BaseDate getEnd() {
    return m_End;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String endTipText() {
    return "The end date to use in the evaluation.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  public Class accepts() {
    return String.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected Object doConvert() throws Exception {
    BaseDate	result;

    result = new BaseDate((String) m_Input);
    if (!m_Start.isInfinity())
      result.setStart(m_Start.dateValue());
    if (!m_End.isInfinity())
      result.setEnd(m_End.dateValue());

    return result.stringValue();
  }
}
