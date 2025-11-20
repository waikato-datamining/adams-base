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
 * Xor.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.flow.core.Actor;
import adams.flow.core.Compatibility;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Combines the specified conditions using a logical XOR (exclusive OR).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-first &lt;adams.flow.condition.bool.BooleanCondition&gt; (property: first)
 * &nbsp;&nbsp;&nbsp;The first boolean condition in the XOR.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.condition.bool.True
 * </pre>
 *
 * <pre>-second &lt;adams.flow.condition.bool.BooleanCondition&gt; (property: second)
 * &nbsp;&nbsp;&nbsp;The second boolean condition in the XOR.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.condition.bool.False
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Xor
  extends AbstractBooleanCondition
  implements BooleanConditionWithSimplification {

  /** for serialization. */
  private static final long serialVersionUID = -7930281929775307418L;

  /** the first condition. */
  protected BooleanCondition m_First;

  /** the second condition. */
  protected BooleanCondition m_Second;

  /**
   * Default constructor
   */
  public Xor() {
    super();
  }

  /**
   * Initializes the object with the specified conditions.
   *
   * @param first  	the first condition to use
   * @param second 	the second condition to use
   */
  public Xor(BooleanCondition first, BooleanCondition second) {
    this();
    setFirst(first);
    setSecond(second);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Combines the specified conditions using a logical XOR (exclusive OR).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "first", "first",
      new True());

    m_OptionManager.add(
      "second", "second",
      new True());
  }

  /**
   * Sets the first condition to evaluate.
   *
   * @param value	the condition
   */
  public void setFirst(BooleanCondition value) {
    m_First = value;
    reset();
  }

  /**
   * Returns the first condition to evaluate.
   *
   * @return		the condition
   */
  public BooleanCondition getFirst() {
    return m_First;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String firstTipText() {
    return "The first boolean condition in the XOR.";
  }

  /**
   * Sets the second condition to evaluate.
   *
   * @param value	the condition
   */
  public void setSecond(BooleanCondition value) {
    m_Second = value;
    reset();
  }

  /**
   * Returns the second condition to evaluate.
   *
   * @return		the condition
   */
  public BooleanCondition getSecond() {
    return m_Second;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String secondTipText() {
    return "The second boolean condition in the XOR.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "first", m_First, "first: ");
    result += QuickInfoHelper.toString(this, "second", m_Second, ", second: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return adams.flow.core.Unknown.class
   */
  @Override
  public Class[] accepts() {
    return new Compatibility().getCompatibleClasses(
      m_First.accepts(), m_Second.accepts()).toArray(new Class[0]);
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner the owning actor
   * @param token the current token passing through
   * @return the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    return m_First.evaluate(owner, token) ^ m_Second.evaluate(owner, token);
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_First.stopExecution();
    m_Second.stopExecution();
  }

  /**
   * Returns a simplified boolean condition, if possible.
   *
   * @return a potentially simplified boolean condition
   */
  @Override
  public BooleanCondition simplify() {
    return null;
  }
}
