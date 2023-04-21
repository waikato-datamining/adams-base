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
 * BytesComplete.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.core.io.filecomplete.AbstractFileCompleteCheck;
import adams.core.io.filecomplete.NoCheck;
import adams.flow.core.Actor;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Evaluates to 'true' if the bytes from the token are considered a 'complete' file.
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
 * <pre>-check &lt;adams.core.io.filecomplete.AbstractFileCompleteCheck&gt; (property: check)
 * &nbsp;&nbsp;&nbsp;The check scheme to use.
 * &nbsp;&nbsp;&nbsp;default: adams.core.io.filecomplete.NoCheck
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BytesComplete
  extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = -6986050060604039765L;

  /** the check scheme to use. */
  protected AbstractFileCompleteCheck m_Check;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Evaluates to 'true' if the bytes from the token are considered a 'complete' file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "check", "check",
      new NoCheck());
  }

  /**
   * Sets the 'in use' check scheme.
   *
   * @param value	the check scheme
   */
  public void setCheck(AbstractFileCompleteCheck value) {
    m_Check = value;
    reset();
  }

  /**
   * Returns the 'in use' check scheme.
   *
   * @return		the check scheme
   */
  public AbstractFileCompleteCheck getCheck() {
    return m_Check;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String checkTipText() {
    return "The check scheme to use.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "check", m_Check, "check: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		Unknown
   */
  @Override
  public Class[] accepts() {
    return new Class[]{byte[].class};
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
    boolean		result;

    result = false;

    if (token.hasPayload(byte[].class))
      result = m_Check.isComplete(token.getPayload(byte[].class));

    return result;
  }
}
