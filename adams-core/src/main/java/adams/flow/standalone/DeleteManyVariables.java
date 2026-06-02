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
 * DeleteManyVariables.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.VariableName;
import adams.core.VariableUpdater;
import adams.core.Variables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DeleteManyVariables
  extends AbstractStandalone
  implements VariableUpdater {

  /** for serialization. */
  private static final long serialVersionUID = -3383735680425581504L;

  /** the variable names. */
  protected List<VariableName> m_VariableNames;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Deletes the specified variables.";
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
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_VariableNames = new ArrayList<>();
  }

  /**
   * Adds the variable name.
   *
   * @param value	the name to add
   */
  public void addVariableName(VariableName value) {
    m_VariableNames.add(value);
    reset();
  }

  /**
   * Adds the variable name.
   *
   * @param value	the name to add
   */
  public void addVariableName(String value) {
    addVariableName(new VariableName(value));
  }

  /**
   * Sets the variable names.
   *
   * @param value	the names
   */
  public void setVariableNames(VariableName[] value) {
    m_VariableNames.clear();
    m_VariableNames.addAll(Arrays.asList(value));
    reset();
  }

  /**
   * Returns the variable names.
   *
   * @return		the names
   */
  public VariableName[] getVariableNames() {
    return m_VariableNames.toArray(new VariableName[0]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableNamesTipText() {
    return "The names of the variables to delete.";
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
    return QuickInfoHelper.toString(this, "variableNames", m_VariableNames.size() + " variable" + (m_VariableNames.size() == 1 ? "" : "s"));
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Variables	vars;

    result = null;

    vars   = getVariables();
    for (VariableName name : m_VariableNames) {
      if (vars.has(name.getValue())) {
	vars.remove(name.getValue());
	if (isLoggingEnabled())
	  getLogger().info("Removed: " + name.getValue());
      }
      else {
	getLogger().info("Not present: " + name.getValue());
      }
    }

    return result;
  }
}
