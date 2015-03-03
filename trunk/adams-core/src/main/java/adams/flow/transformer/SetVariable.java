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
 * SetVariable.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.VariableName;
import adams.core.VariableUpdater;
import adams.core.base.BaseText;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Sets the value of a variable. Each time a token passes through, the variable value will get updated according to the update type.<br/>
 * Optionally, the specified value (or incoming value) can be expanded, in case it is made up of variables itself.<br/>
 * The transformer just forwards tokens that it receives after the variable has been set.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: SetVariable
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
 * <pre>-var-name &lt;adams.core.VariableName&gt; (property: variableName)
 * &nbsp;&nbsp;&nbsp;The name of the variable to update.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 * 
 * <pre>-var-value &lt;adams.core.base.BaseText&gt; (property: variableValue)
 * &nbsp;&nbsp;&nbsp;The fixed value to use instead of the current token; only used if non-empty.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-update-type &lt;REPLACE|APPEND|PREPEND&gt; (property: updateType)
 * &nbsp;&nbsp;&nbsp;Determines how to update the variable.
 * &nbsp;&nbsp;&nbsp;default: REPLACE
 * </pre>
 * 
 * <pre>-expand-value &lt;boolean&gt; (property: expandValue)
 * &nbsp;&nbsp;&nbsp;If enabled, the value (either parameter value or incoming token) gets expanded 
 * &nbsp;&nbsp;&nbsp;first in case it is made up of variables itself.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SetVariable
  extends AbstractTransformer
  implements VariableUpdater {

  /** for serialization. */
  private static final long serialVersionUID = -3383735680425581504L;

  /**
   * How to update the variable value.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum UpdateType {
    /** replaces the current value. */
    REPLACE,
    /** appends the value to the existing value. */
    APPEND,
    /** prepends the value to the existing value. */
    PREPEND
  }
  
  /** the name of the variable. */
  protected VariableName m_VariableName;

  /** the optional fixed value. */
  protected BaseText m_VariableValue;

  /** how to update the variable value. */
  protected UpdateType m_UpdateType;
  
  /** whether to expand the value. */
  protected boolean m_ExpandValue;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Sets the value of a variable. Each time a token passes "
	+ "through, the variable value will get updated according to the "
	+ "update type.\n"
	+ "Optionally, the specified value (or incoming value) can be expanded, "
	+ "in case it is made up of variables itself.\n"
	+ "The transformer just forwards tokens that it receives after the "
	+ "variable has been set.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "var-name", "variableName",
	    new VariableName());

    m_OptionManager.add(
	    "var-value", "variableValue",
	    new BaseText(""));

    m_OptionManager.add(
	    "update-type", "updateType",
	    UpdateType.REPLACE);

    m_OptionManager.add(
	    "expand-value", "expandValue",
	    false);
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
   * Sets the fixed value to use instead of current token.
   *
   * @param value	the value
   */
  public void setVariableValue(BaseText value) {
    m_VariableValue = value;
    reset();
  }

  /**
   * Returns the fixed value to use instead of current token.
   *
   * @return		the name
   */
  public BaseText getVariableValue() {
    return m_VariableValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableValueTipText() {
    return "The fixed value to use instead of the current token; only used if non-empty.";
  }

  /**
   * Sets how to update the variable.
   *
   * @param value	the type
   */
  public void setUpdateType(UpdateType value) {
    m_UpdateType = value;
    reset();
  }

  /**
   * Returns how to update the variable.
   *
   * @return		the type
   */
  public UpdateType getUpdateType() {
    return m_UpdateType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String updateTypeTipText() {
    return "Determines how to update the variable.";
  }

  /**
   * Sets whether to expand the value before settting it
   * (eg if it is made up of variables itself).
   *
   * @param value	true if to expand
   */
  public void setExpandValue(boolean value) {
    m_ExpandValue = value;
    reset();
  }

  /**
   * Returns whether the value gets expanded before setting it 
   * (eg if it is made up of variables itself).
   *
   * @return		true if expanded
   */
  public boolean getExpandValue() {
    return m_ExpandValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expandValueTipText() {
    return 
	"If enabled, the value (either parameter value or incoming token) "
	+ "gets expanded first in case it is made up of variables itself.";
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
    String		variable;
    String		result;
    String		value;
    List<String>	options;

    variable = QuickInfoHelper.getVariable(this, "variableName");
    if (variable != null)
      result = variable;
    else
      result = m_VariableName.paddedValue();
    value = QuickInfoHelper.toString(this, "variableValue", m_VariableValue.getValue(), " = ");
    if (value != null)
      result += value;

    // further options
    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "updateType", m_UpdateType));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "expandValue", m_ExpandValue, "expand"));
    result += QuickInfoHelper.flatten(options);

    return result;
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
    String	value;
    String	current;

    result = null;

    try {
      value = null;
      if (!m_VariableValue.isEmpty()) {
	value = m_VariableValue.getValue();
      }
      else {
	if (m_InputToken.getPayload() != null)
	  value = m_InputToken.getPayload().toString();
      }
      
      if (value != null) {
	if (m_ExpandValue)
	  value = getVariables().expand(value);
	
	if (getVariables().has(m_VariableName.getValue()))
	  current = getVariables().get(m_VariableName.getValue());
	else
	  current = "";
	
	switch (m_UpdateType) {
	  case REPLACE:
	    getVariables().set(m_VariableName.getValue(), value);
	    if (isLoggingEnabled())
	      getLogger().info("Replacing variable '" + m_VariableName + "': " + value);
	    break;
	  case APPEND:
	    getVariables().set(m_VariableName.getValue(), current + value);
	    if (isLoggingEnabled())
	      getLogger().info("Appending variable '" + m_VariableName + "': " + current + value);
	    break;
	  case PREPEND:
	    getVariables().set(m_VariableName.getValue(), value + current);
	    if (isLoggingEnabled())
	      getLogger().info("Prepending variable '" + m_VariableName + "': " + value + current);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled update type: " + m_UpdateType);
	}
      }
    }
    catch (Exception e) {
      result = handleException("Failed to update variable: " + m_VariableName, e);
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
