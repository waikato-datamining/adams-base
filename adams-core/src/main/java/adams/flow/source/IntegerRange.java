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
 * IntegerRange.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.Range;

/**
 <!-- globalinfo-start -->
 * Outputs the integers defined by the range expression.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: IntegerRange
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Whether to output the integers one-by-one or as an array.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-range &lt;adams.core.Range&gt; (property: range)
 * &nbsp;&nbsp;&nbsp;The range expression to use for generating the integers.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-max &lt;int&gt; (property: max)
 * &nbsp;&nbsp;&nbsp;The maximum number for the range.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-zero-based &lt;boolean&gt; (property: zeroBased)
 * &nbsp;&nbsp;&nbsp;If enabled, 0-based integers are output instead of 1-based ones ('first' 
 * &nbsp;&nbsp;&nbsp;= 0 instead of 1).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class IntegerRange
  extends AbstractArrayProvider {

  private static final long serialVersionUID = -8634150731979965438L;

  /** the range. */
  protected Range m_Range;

  /** the maximum for the range. */
  protected int m_Max;

  /** whether to output 0-based or 1-based integers. */
  protected boolean m_ZeroBased;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the integers defined by the range expression.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "range", "range",
      new Range(Range.ALL));

    m_OptionManager.add(
      "max", "max",
      100, 1, null);

    m_OptionManager.add(
      "zero-based", "zeroBased",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "range", m_Range, "range: ");
    result += QuickInfoHelper.toString(this, "max", m_Max, ", max: ");
    result += QuickInfoHelper.toString(this, "zeroBased", (m_ZeroBased ? "0-based" : "1-based"), ", ");
    result += QuickInfoHelper.toString(this, "outputArray", (m_OutputArray ? "as array" : "one by one"), ", ");

    return result;
  }

  /**
   * Sets the range to use.
   *
   * @param value	the range
   */
  public void setRange(Range value) {
    m_Range = value;
    reset();
  }

  /**
   * Returns the range to use.
   *
   * @return		the range
   */
  public Range getRange() {
    return m_Range;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rangeTipText() {
    return "The range expression to use for generating the integers.";
  }

  /**
   * Sets the maximum for the range.
   *
   * @param value	the maximum
   */
  public void setMax(int value) {
    m_Max = value;
    reset();
  }

  /**
   * Returns the maximum for the range.
   *
   * @return		the maximum
   */
  public int getMax() {
    return m_Max;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxTipText() {
    return "The maximum number for the range.";
  }

  /**
   * Sets whether to output 1-based or 0-based integers.
   *
   * @param value	true if 0-based
   */
  public void setZeroBased(boolean value) {
    m_ZeroBased = value;
    reset();
  }

  /**
   * Returns whether to output 1-based or 0-based integers.
   *
   * @return		true if 0-based
   */
  public boolean getZeroBased() {
    return m_ZeroBased;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String zeroBasedTipText() {
    return "If enabled, 0-based integers are output instead of 1-based ones ('first' = 0 instead of 1).";
  }

  /**
   * Returns the based class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return Integer.class;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Whether to output the integers one-by-one or as an array.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    m_Queue.clear();
    m_Range.setMax(m_Max);
    for (int i: m_Range.getIntIndices()) {
      if (m_ZeroBased)
        m_Queue.add(i);
      else
        m_Queue.add(i + 1);
    }

    return null;
  }
}
