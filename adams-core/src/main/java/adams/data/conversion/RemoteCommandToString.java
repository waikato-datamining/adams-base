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
 * RemoteCommandToString.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.scripting.ScriptingHelper;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.RemoteCommandWithResponse;
import adams.scripting.processor.RemoteCommandProcessor;
import adams.scripting.processor.RemoteCommandProcessorHandler;

/**
 <!-- globalinfo-start -->
 * Turns a remote command into a string.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteCommandToString
  extends AbstractConversionToString
   implements RemoteCommandProcessorHandler {
  
  /** for serialization. */
  private static final long serialVersionUID = -3431292077368307264L;

  /** the processor. */
  protected RemoteCommandProcessor m_CommandProcessor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a remote command into a string.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "command-processor", "commandProcessor",
      ScriptingHelper.getSingleton().getDefaultProcessor());
  }

  /**
   * Sets the command processor to use.
   *
   * @param value	the processor
   */
  public void setCommandProcessor(RemoteCommandProcessor value) {
    m_CommandProcessor = value;
    reset();
  }

  /**
   * Returns the command processor in use.
   *
   * @return		the processor
   */
  public RemoteCommandProcessor getCommandProcessor() {
    return m_CommandProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String commandProcessorTipText() {
    return "The processor for formatting/parsing.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return RemoteCommand.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    RemoteCommand	cmd;

    cmd = (RemoteCommand) m_Input;
    if (cmd.isRequest()) {
      return cmd.assembleRequest(m_CommandProcessor);
    }
    else {
      if (cmd instanceof RemoteCommandWithResponse)
	return ((RemoteCommandWithResponse) cmd).assembleResponse(m_CommandProcessor);
      else
	throw new IllegalStateException("Remote command is not a response but flagged as such:\n" + cmd);
    }
  }
}
