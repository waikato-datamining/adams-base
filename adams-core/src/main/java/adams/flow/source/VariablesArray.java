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
 * VariablesArray.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.VariableName;
import adams.core.VariableUser;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Outputs the values associated with the specified variable names as a string array.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: VariablesArray
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-var-name &lt;adams.core.VariableName&gt; [-var-name ...] (property: variableNames)
 * &nbsp;&nbsp;&nbsp;The names of the variables to retrieve as array.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VariablesArray
  extends AbstractSource 
  implements VariableUser {

  /** for serialization. */
  private static final long serialVersionUID = -8533328604800956145L;

  /** the names of the stored values. */
  protected VariableName[] m_VariableNames;

  /** the stored value. */
  protected String[] m_StoredValue;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Outputs the values associated with the specified variable names "
      + "as a string array.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "var-name", "variableNames",
	    new VariableName[0]);
  }

  /**
   * Sets the names of the variables.
   *
   * @param value	the names
   */
  public void setVariableNames(VariableName[] value) {
    m_VariableNames = value;
    reset();
  }

  /**
   * Returns the names of the variables.
   *
   * @return		the names
   */
  public VariableName[] getVariableNames() {
    return m_VariableNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableNamesTipText() {
    return "The names of the variables to retrieve as array.";
  }

  /**
   * Returns whether variables are being used.
   * 
   * @return		true if variables are used
   */
  public boolean isUsingVariables() {
    return !getSkip() && (m_VariableNames.length > 0);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "variableNames", Utils.flatten(m_VariableNames, ", "), "Names: ");
    if (result == null)
      result = "-no names specified-";

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String[].class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String[].class};
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_StoredValue = null;
  }

  /**
   * Hook for performing setup checks -- used in setUp() and preExecute().
   *
   * @param fromSetUp	whether the method has been called from within setUp()
   * @return		null if everything OK, otherwise error message
   */
  @Override
  protected String performSetUpChecks(boolean fromSetUp) {
    String	result;

    result = super.performSetUpChecks(fromSetUp);

    if (result == null) {
      if (canPerformSetUpCheck(fromSetUp, "variableNames")) {
	if ((m_VariableNames == null) || (m_VariableNames.length == 0))
	  result = "No names specified for variables!";
      }
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    int		i;
    String[]	values;
    
    result = null;

    // get storage items
    values = new String[m_VariableNames.length];
    for (i = 0; i < m_VariableNames.length; i++) {
      if (getVariables().has(m_VariableNames[i].getValue()))
	values[i] = getVariables().get(m_VariableNames[i].getValue());
      else
	result = "Storage item #" + (i+1) + " (" + m_VariableNames[i] + ") not found!";
      if (result != null)
	break;
    }
    
    if (result == null)
      m_StoredValue = values;
    else
      m_StoredValue = null;

    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;

    result          = new Token(m_StoredValue);
    m_StoredValue = null;

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_StoredValue != null);
  }
}
