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
 * SwapVariables.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.VariableName;
import adams.core.VariableUpdater;
import adams.core.Variables;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Swaps the values of the two variables around, whenever a token passes through.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
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
 * &nbsp;&nbsp;&nbsp;default: SwapVariables
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-var1 &lt;adams.core.VariableName&gt; (property: var1)
 * &nbsp;&nbsp;&nbsp;The first variable.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 *
 * <pre>-var2 &lt;adams.core.VariableName&gt; (property: var2)
 * &nbsp;&nbsp;&nbsp;The second variable.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SwapVariables
  extends AbstractTransformer
  implements VariableUpdater {

  private static final long serialVersionUID = 6441557348419921594L;

  /** the first variable. */
  protected VariableName m_Var1;

  /** the second variable. */
  protected VariableName m_Var2;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Swaps the values of the two variables around, whenever a token passes through.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "var1", "var1",
      new VariableName());

    m_OptionManager.add(
      "var2", "var2",
      new VariableName());
  }

  /**
   * Sets the first variable.
   *
   * @param value	the variable
   */
  public void setVar1(VariableName value) {
    m_Var1 = value;
    reset();
  }

  /**
   * Returns the first variable.
   *
   * @return		the variable
   */
  public VariableName getVar1() {
    return m_Var1;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String var1TipText() {
    return "The first variable.";
  }

  /**
   * Sets the second variable.
   *
   * @param value	the variable
   */
  public void setVar2(VariableName value) {
    m_Var2 = value;
    reset();
  }

  /**
   * Returns the second variable.
   *
   * @return		the variable
   */
  public VariableName getVar2() {
    return m_Var2;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String var2TipText() {
    return "The second variable.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "var1", m_Var1, "1: ");
    result += QuickInfoHelper.toString(this, "var2", m_Var2, ", 2: ");

    return result;
  }

  /**
   * Returns whether variables are being updated.
   *
   * @return		true if variables are updated
   */
  public boolean isUpdatingVariables() {
    return !getSkip();
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    String	valTmp;
    Variables	vars;

    result = null;
    vars   = getVariables();

    if (!vars.has(m_Var1.getValue())) {
      result = "Var1 not present: " + m_Var1;
    }
    else if (!vars.has(m_Var2.getValue())) {
      result = "Var2 not present: " + m_Var2;
    }
    else if (m_Var1.equals(m_Var2)) {
      result = "Variables have the same name: " + m_Var1 + ", " + m_Var2;
    }
    else {
      valTmp = vars.get(m_Var1.getValue());
      vars.set(m_Var1.getValue(), vars.get(m_Var2.getValue()));
      vars.set(m_Var2.getValue(), valTmp);
    }
    if (result == null)
      m_OutputToken = m_InputToken;

    return result;
  }
}
