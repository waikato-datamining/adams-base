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
 * MultiOutput.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.simplelogging.output;

import adams.flow.core.Actor;

/**
 * Outputs the log message with all the specified sub-outputs.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MultiOutput
  extends AbstractSimpleOutput {

  private static final long serialVersionUID = -9198176486160748509L;

  /** the underlying outputs. */
  protected SimpleOutput[] m_Outputs;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the log message with all the specified sub-outputs.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output", "outputs",
      new SimpleOutput[0]);
  }

  /**
   * Sets the flow context.
   *
   * @param value	the context
   */
  @Override
  public void setFlowContext(Actor value) {
    super.setFlowContext(value);

    for (SimpleOutput output: m_Outputs)
      output.setFlowContext(value);
  }

  /**
   * Sets the outputs to use.
   *
   * @param value 	the outputs to use
   */
  public void setOutputs(SimpleOutput[] value) {
    m_Outputs = value;
    reset();
  }

  /**
   * Returns the outputs in use.
   *
   * @return 		the outputs in use
   */
  public SimpleOutput[] getOutputs() {
    return m_Outputs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputsTipText() {
    return "The outputs to use for outputting the same logging message with.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    StringBuilder	result;
    String		info;

    result = new StringBuilder();
    for (SimpleOutput output: m_Outputs) {
      if (result.length() > 0)
        result.append(", ");
      info = output.getQuickInfo();
      if ((info != null) && (info.length() > 0))
	result.append(info);
    }

    return result.toString();
  }

  /**
   * Returns whether flow context is really required.
   *
   * @return true if required
   */
  @Override
  public boolean requiresFlowContext() {
    boolean	result;

    result = false;

    for (SimpleOutput output: m_Outputs)
      result = result || output.requiresFlowContext();

    return result;
  }

  /**
   * Logs the (formatted) logging message.
   *
   * @param msg the message to log
   * @return null if successful, otherwise error message
   */
  @Override
  protected String doLogMessage(String msg) {
    String	result;

    result = null;

    for (SimpleOutput output: m_Outputs) {
      result = output.logMessage(msg);
      if (result != null)
	break;
    }

    return result;
  }
}
