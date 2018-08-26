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
 * ExternalCommand.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.flowrestart.operation;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseCommandLine;
import adams.core.management.ProcessUtils;
import adams.flow.control.Flow;
import adams.flow.core.RunnableWithLogging;
import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;

import java.util.logging.Level;

/**
 * Executes an external command, eg systemd or Windows service.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ExternalCommand
  extends AbstractRestartOperation {

  private static final long serialVersionUID = 5721670854550551855L;

  /** the command to execute. */
  protected BaseCommandLine m_Command;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes an external command, eg systemd or Windows service.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "command", "command",
      new BaseCommandLine());
  }

  /**
   * Sets the external command.
   *
   * @param value	the command
   */
  public void setCommand(BaseCommandLine value) {
    m_Command = value;
    reset();
  }

  /**
   * Returns the external command.
   *
   * @return		the command
   */
  public BaseCommandLine getCommand() {
    return m_Command;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commandTipText() {
    return "The external command to execute.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "command", m_Command, "command: ");
  }

  /**
   * Restarts the flow.
   *
   * @param flow	the flow to handle
   * @return		null if successfully restarted, otherwise the error message
   */
  @Override
  public String restart(Flow flow) {
    RunnableWithLogging	runnable;

    stopFlow(flow);

    runnable = new RunnableWithLogging() {
      private static final long serialVersionUID = -5446295909630418597L;
      @Override
      protected void doRun() {
        try {
          if (isLoggingEnabled())
            getLogger().info("Executing: " + m_Command);
	  CollectingProcessOutput output = ProcessUtils.execute(m_Command.getValue());
	  getLogger().info("Exit code: " + output.getExitCode());
	  getLogger().info("StdErr: " + output.getStdErr());
	  getLogger().info("StdOut: " + output.getStdOut());
          if (isLoggingEnabled())
            getLogger().info("Finished executing: " + m_Command);
	}
	catch (Exception e) {
          getLogger().log(Level.SEVERE, "Failed to execute: ", e);
	}
      }
    };
    new Thread(runnable).start();

    return null;
  }
}
