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
 * IsAnyStringValue.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseString;
import adams.flow.core.Actor;
import adams.flow.core.Token;

import java.util.HashSet;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Evaluates to 'true' if the payload of the token is any of specified string values.
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
 * <pre>-value &lt;adams.core.base.BaseString&gt; [-value ...] (property: values)
 * &nbsp;&nbsp;&nbsp;The string values that the payload must match (one of them).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class IsAnyStringValue
  extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = 3278345095591806425L;

  /** the string values to check against. */
  protected BaseString[] m_Values;

  /** the set of values. */
  protected Set<String> m_ValuesSet;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Evaluates to 'true' if the payload of the token is any of specified string values.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "value", "values",
      new BaseString[0]);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ValuesSet = null;
  }

  /**
   * Sets the string values that the payload must match (one of them).
   *
   * @param values	the values
   */
  public void setValues(BaseString[] values) {
    m_Values = values;
    reset();
  }

  /**
   * Returns the string values that the payload must match (one of them).
   *
   * @return		the values
   */
  public BaseString[] getValues() {
    return m_Values;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valuesTipText() {
    return "The string values that the payload must match (one of them).";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		always null
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "values", m_Values, "values: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		Unknown
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    boolean	result;

    result = false;

    if (m_ValuesSet == null) {
      m_ValuesSet = new HashSet<>();
      for (BaseString value: m_Values)
	m_ValuesSet.add(value.getValue());
    }

    if (token.getPayload() != null)
      result = m_ValuesSet.contains(token.getPayload(String.class));

    return result;
  }
}
