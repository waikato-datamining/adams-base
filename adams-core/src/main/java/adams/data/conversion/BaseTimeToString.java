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
 * BaseTimeToString.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.base.BaseTime;
import adams.parser.GrammarSupplier;

/**
 <!-- globalinfo-start -->
 * Turns a BaseTime format string into a String, evaluted using user-supplied start and end times (ignored if future INF times).<br>
 * <br>
 * Example: 07:13:12 +3 MINUTE<br>
 * <br>
 * (&lt;date&gt;|NOW|-INF|+INF|START|END) [expr (SECOND|MINUTE|HOUR)]*<br>
 * expr ::=   ( expr )<br>
 *          | - expr<br>
 *          | + expr<br>
 *          | expr + expr<br>
 *          | expr - expr<br>
 *          | expr * expr<br>
 *          | expr &#47; expr<br>
 *          | expr % expr<br>
 *          | expr ^ expr<br>
 *          | abs ( expr )<br>
 *          | sqrt ( expr )<br>
 *          | log ( expr )<br>
 *          | exp ( expr )<br>
 *          | rint ( expr )<br>
 *          | floor ( expr )<br>
 *          | pow[er] ( expr , expr )<br>
 *          | ceil ( expr )<br>
 *          | NUMBER<br>
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-start &lt;adams.core.base.BaseTime&gt; (property: start)
 * &nbsp;&nbsp;&nbsp;The start time to use in the evaluation.
 * &nbsp;&nbsp;&nbsp;default: -INF
 * </pre>
 * 
 * <pre>-end &lt;adams.core.base.BaseTime&gt; (property: end)
 * &nbsp;&nbsp;&nbsp;The end time to use in the evaluation.
 * &nbsp;&nbsp;&nbsp;default: +INF
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseTimeToString
  extends AbstractConversionToString
  implements GrammarSupplier {

  /** for serialization. */
  private static final long serialVersionUID = 6744245717394758406L;

  /** the start time to use as basis for the evaluation. */
  protected BaseTime m_Start;

  /** the end time to use as basis for the evaluation. */
  protected BaseTime m_End;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Turns a BaseTime format string into a String, evaluted using "
      + "user-supplied start and end times (ignored if future INF times).\n\n"
      + "Example: 07:13:12 +3 MINUTE\n\n"
      + getGrammar();
  }

  /**
   * Returns a string representation of the grammar.
   *
   * @return		the grammar, null if not available
   */
  public String getGrammar() {
    return new BaseTime().getGrammar();
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "start", "start",
	    new BaseTime(BaseTime.INF_PAST));

    m_OptionManager.add(
	    "end", "end",
	    new BaseTime(BaseTime.INF_FUTURE));
  }

  /**
   * Sets the start time to use in the evaluation.
   *
   * @param value	the time
   */
  public void setStart(BaseTime value) {
    m_Start = value;
    reset();
  }

  /**
   * Returns the start time used in the evaluation.
   *
   * @return		the time
   */
  public BaseTime getStart() {
    return m_Start;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startTipText() {
    return "The start time to use in the evaluation.";
  }

  /**
   * Sets the end time to use in the evaluation.
   *
   * @param value	the time
   */
  public void setEnd(BaseTime value) {
    m_End = value;
    reset();
  }

  /**
   * Returns the end time used in the evaluation.
   *
   * @return		the time
   */
  public BaseTime getEnd() {
    return m_End;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String endTipText() {
    return "The end time to use in the evaluation.";
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
    BaseTime	result;

    result = new BaseTime((String) m_Input);
    if (!m_Start.isInfinity())
      result.setStart(m_Start.dateValue());
    if (!m_End.isInfinity())
      result.setEnd(m_End.dateValue());

    return result.stringValue();
  }
}
