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
 * LoadVariables.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.VariableUpdater;
import adams.core.Variables;
import adams.flow.standalone.loadvariables.Null;
import adams.flow.standalone.loadvariables.VariableLoader;

/**
 <!-- globalinfo-start -->
 * Loads variables using the specified loader.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: LoadVariables
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-loader &lt;adams.flow.standalone.loadvariables.VariableLoader&gt; (property: loader)
 * &nbsp;&nbsp;&nbsp;The variable loader to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.loadvariables.Null
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class LoadVariables
  extends AbstractStandalone
  implements VariableUpdater {

  private static final long serialVersionUID = 9203727296106661544L;

  /** the variable loader to use. */
  protected VariableLoader m_Loader;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Loads variables using the specified loader.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "loader", "loader",
      new Null());
  }

  /**
   * Sets the loader to use.
   *
   * @param value	the loader
   */
  public void setLoader(VariableLoader value) {
    m_Loader = value;
    reset();
  }

  /**
   * Returns the input file to process.
   *
   * @return 		the file
   */
  public VariableLoader getLoader() {
    return m_Loader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String loaderTipText() {
    return "The variable loader to use.";
  }

  /**
   * Returns whether variables are being updated.
   *
   * @return		true if variables are updated
   */
  @Override
  public boolean isUpdatingVariables() {
    return !getSkip();
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "loader", m_Loader, "loader: ");
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    MessageCollection	errors;
    Variables		vars;

    result = null;
    errors = new MessageCollection();
    vars   = m_Loader.loadVariables(errors);
    if (errors.isEmpty()) {
      for (String var: vars.nameSet()) {
	if (isLoggingEnabled())
	  getLogger().info("Adding variable: " + var);
	getVariables().set(var, vars.get(var));
      }
    }
    else {
      result = errors.toString();
    }

    return result;
  }
}
