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
 * UpdateVariable.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import adams.core.VariableName;
import adams.flow.control.Trigger;
import adams.flow.core.Actor;
import adams.flow.source.Variable;
import adams.flow.transformer.SetVariable;

/**
 <!-- globalinfo-start -->
 * Generates a sub-flow (enlosed by a Trigger) that retrieves and sets a variable, with the user being able to add custom actors in between for updating the variable value.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The new name for the actor; leave empty to use current.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-variable &lt;adams.core.VariableName&gt; (property: variableName)
 * &nbsp;&nbsp;&nbsp;The variable to generate the sub-flow for.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class UpdateVariable
  extends AbstractActorTemplate {

  /** for serialization. */
  private static final long serialVersionUID = 2310015199489870240L;

  /** the variable to update. */
  protected VariableName m_VariableName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Generates a sub-flow (enlosed by a Trigger) that retrieves and sets "
      + "a variable, with the user being able to add custom actors in between "
      + "for updating the variable value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "variable", "variableName",
	    new VariableName());
  }

  /**
   * Sets the variable name to generate the sub-flow for.
   *
   * @param value	the variable
   */
  public void setVariableName(VariableName value) {
    m_VariableName = value;
    reset();
  }

  /**
   * Returns the variable name to generate the sub-flow for.
   *
   * @return		the variable
   */
  public VariableName getVariableName() {
    return m_VariableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String variableNameTipText() {
    return "The variable to generate the sub-flow for.";
  }

  /**
   * Generates the actor.
   *
   * @return 		the generated acto
   */
  protected Actor doGenerate() {
    Trigger	result;
    Variable	var;
    SetVariable	setVar;

    result = new Trigger();
    result.setName("Updating " + m_VariableName);

    var = new Variable();
    var.setVariableName((VariableName) m_VariableName.getClone());

    setVar = new SetVariable();
    setVar.setVariableName((VariableName) m_VariableName.getClone());

    result.setActors(new Actor[]{
	var,
	setVar
    });

    return result;
  }
}
