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
 * DeleteVariable.java
 * Copyright (C) 2010-2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.VariableName;
import adams.core.VariableUpdater;
import adams.core.base.BaseRegExp;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Removes a variable by name or the variables that match the regular expression. Each time a token passes through, the deletion process occurs.<br>
 * The transformer just forwards tokens that it receives after the deletion process.
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
 * &nbsp;&nbsp;&nbsp;default: DeleteVariable
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
 * <pre>-type &lt;NAME|REGEXP&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;How to determine the variable(s) to delete.
 * &nbsp;&nbsp;&nbsp;default: NAME
 * </pre>
 *
 * <pre>-var-name &lt;adams.core.VariableName&gt; (property: variableName)
 * &nbsp;&nbsp;&nbsp;The name of the variable to update.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 *
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression used for matching the variable names to delete.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DeleteVariable
  extends AbstractTransformer
  implements VariableUpdater {

  /** for serialization. */
  private static final long serialVersionUID = -3383735680425581504L;

  /**
   * Determines how to locate the variable.
   */
  public enum MatchingType {
    NAME,
    REGEXP,
  }

  /** how to determine variables to delete. */
  protected MatchingType m_Type;

  /** the name of the variable. */
  protected VariableName m_VariableName;

  /** the regexp to match against variable names. */
  protected BaseRegExp m_RegExp;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Removes a variable by name or the variables that match the regular "
	+ "expression. Each time a token passes through, the deletion process occurs.\n"
	+ "The transformer just forwards tokens that it receives after the "
	+ "deletion process.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      MatchingType.NAME);

    m_OptionManager.add(
      "var-name", "variableName",
      new VariableName());

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(VariableName.DEFAULT));
  }

  /**
   * Sets how to determine variables to delete.
   *
   * @param value	the matching type
   */
  public void setType(MatchingType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns how to determine variables to delete.
   *
   * @return		the matching type
   */
  public MatchingType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "How to determine the variable(s) to delete.";
  }

  /**
   * Sets the name of the variable to update.
   *
   * @param value	the name
   */
  public void setVariableName(VariableName value) {
    m_VariableName = value;
    reset();
  }

  /**
   * Returns the name of the variable to update.
   *
   * @return		the name
   */
  public VariableName getVariableName() {
    return m_VariableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableNameTipText() {
    return "The name of the variable to update.";
  }

  /**
   * Sets the regular expression to match the variable names against.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to match the variable names against.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression used for matching the variable names to delete.";
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    switch (m_Type) {
      case NAME:
	return QuickInfoHelper.toString(this, "variableName", m_VariableName.paddedValue());
      case REGEXP:
	return QuickInfoHelper.toString(this, "regExp", m_RegExp);
      default:
        throw new IllegalStateException("Unhandled matching type: " + m_Type);
    }
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
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
    String	oldValue;
    boolean	removed;

    result = null;

    try {
      if (m_InputToken.getPayload() != null) {
	switch (m_Type) {
	  case NAME:
	    oldValue = getVariables().remove(m_VariableName.getValue());
	    if (isLoggingEnabled())
	      getLogger().info("Removed variable '" + m_VariableName.getValue() + "' (" + getVariables().hashCode() + "), value:" + oldValue);
	    break;
	  case REGEXP:
	    removed = getVariables().remove(m_RegExp);
	    if (isLoggingEnabled())
	      getLogger().info("Removed any variable(s) with '" + m_RegExp + "' (" + getVariables().hashCode() + "):" + removed);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled matching type: " + m_Type);
	}
      }
    }
    catch (Exception e) {
      result = handleException("Failed to delete variable: " + m_VariableName, e);
    }

    m_OutputToken = m_InputToken;

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }
}
