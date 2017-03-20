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
 * ManualFeedScriptingEngine.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.core.Utils;
import adams.multiprocess.PausableFixedThreadPoolExecutor;
import adams.scripting.command.RemoteCommand;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Scripting engine that gets commands fed programmatically.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see #addCommand(RemoteCommand)
 */
public class ManualFeedScriptingEngine
  extends AbstractScriptingEngineWithJobQueue {

  private static final long serialVersionUID = -3763240773922918567L;

  /** the queue to add the commands to. */
  protected BlockingQueue<RemoteCommand> m_Queue;

  /** the maximum number of commands to allow in queue. */
  protected int m_MaxCommands;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Scripting engine that gets commands fed programmatically.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "max-commands", "maxCommands",
      100, 1, null);
  }

  /**
   * Sets the maximum number of commands allowed in the queue.
   *
   * @param value	the maximum
   */
  public void setMaxCommands(int value) {
    if (getOptionManager().isValid("maxCommands", value)) {
      m_MaxCommands = value;
      m_Queue = new ArrayBlockingQueue<RemoteCommand>(m_MaxCommands);
      reset();
    }
  }

  /**
   * Returns the maximum number of commands allowed in the queue.
   *
   * @return		the maximum
   */
  public int getMaxCommands() {
    return m_MaxCommands;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maxCommandsTipText() {
    return "The maximum number of commands allowed in the queue.";
  }

  /**
   * Adds the command to the queue.
   *
   * @param cmd		the command to add
   */
  public void addCommand(RemoteCommand cmd) {
    m_Queue.add(cmd);
  }

  /**
   * Handles the command.
   *
   * @param cmd		the command to handle
   */
  protected void handleCommand(RemoteCommand cmd) {
    String		msg;

    // permitted?
    if (!m_PermissionHandler.permitted(cmd)) {
      m_RequestHandler.requestRejected(cmd, "Not permitted!");
      return;
    }

    // handle command
    msg = m_CommandHandler.handle(cmd);
    if (msg != null)
      getLogger().severe("Failed to handle command:\n" + msg);
  }

  /**
   * Executes the scripting engine.
   *
   * @return		error message in case of failure to start up or run,
   * 			otherwise null
   */
  @Override
  protected String doExecute() {
    String		result;
    RemoteCommand	cmd;

    result = null;

    // start up job queue
    m_Executor = new PausableFixedThreadPoolExecutor(m_MaxConcurrentJobs);

    while (!m_Stopped) {
      while (m_Paused && !m_Stopped) {
	Utils.wait(this, this, 1000, 50);
      }

      try {
	cmd = m_Queue.poll(100, TimeUnit.MILLISECONDS);
	if (cmd == null)
	  continue;
	handleCommand(cmd);
      }
      catch (InterruptedException i) {
	// ignored
      }
      catch (Exception e) {
	Utils.handleException(this, "Failed to poll next command!", e);
      }
    }

    if (!m_Executor.isTerminated()) {
      getLogger().info("Shutting down job queue...");
      m_Executor.shutdown();
      while (!m_Executor.isTerminated())
	Utils.wait(this, 1000, 100);
      getLogger().info("Job queue shut down");
    }

    return result;
  }

  /**
   * Starts the scripting engine from commandline.
   *
   * @param args  	additional options for the scripting engine
   */
  public static void main(String[] args) {
    runScriptingEngine(ManualFeedScriptingEngine.class, args);
  }
}
