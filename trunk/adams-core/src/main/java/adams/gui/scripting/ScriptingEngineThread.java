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
 * ScriptingEngineThread.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.scripting;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import adams.core.Utils;

/**
 * A class for processing the scripting commands.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScriptingEngineThread
  extends Thread {

  /** the owning engine. */
  protected AbstractScriptingEngine m_Owner;

  /** the current command queue. */
  protected LinkedBlockingQueue<ScriptingCommand> m_Commands;

  /** whether the thread is running. */
  protected boolean m_Running;

  /** whether any command is currently being processed. */
  protected boolean m_Processing;

  /**
   * Initializes the thread.
   *
   * @param owner	the owning scripting engine
   */
  public ScriptingEngineThread(AbstractScriptingEngine owner) {
    super();

    m_Owner      = owner;
    m_Commands   = new LinkedBlockingQueue<ScriptingCommand>();
    m_Running    = true;
    m_Processing = false;
  }

  /**
   * Returns the owning scripting engine.
   *
   * @return		the owner
   */
  public AbstractScriptingEngine getOwner() {
    return m_Owner;
  }

  /**
   * Clears the queue of scripting commands.
   */
  public void clear() {
    m_Commands.clear();
  }

  /**
   * Adds the command to the queue.
   *
   * @param cmd	the command to add
   */
  public void add(ScriptingCommand cmd) {
    m_Commands.add(cmd);
  }

  /**
   * Stops the execution of scripting commands.
   */
  public void stopExecution() {
    m_Running = false;
  }

  /**
   * Returns whether the thread is still active and waits for commands
   * to execute.
   *
   * @return		true if accepting commands to process
   */
  public boolean isRunning() {
    return m_Running;
  }

  /**
   * Returns whether a command is currently being processed.
   *
   * @return		true if a command is being processed
   */
  public boolean isProcessing() {
    return m_Processing;
  }

  /**
   * Returns whether there are no commands currently in the queue.
   *
   * @return		true if no commands waiting to be executed
   */
  public synchronized boolean isEmpty() {
    return m_Commands.isEmpty();
  }

  /**
   * Performs some preprocessing.
   *
   * @param cmd	the command that is about to be executed
   */
  protected void preProcess(ScriptingCommand cmd) {
    getOwner().notifyScriptingInfoListeners(cmd.getCommand());
    getOwner().getLogger().info(cmd.getCommand());
  }

  /**
   * Executes the given command.
   *
   * @param cmd		the command to execute
   * @return			the error message, null if no problems occurred
   * @throws Exception	if execution fails
   */
  protected String doProcess(ScriptingCommand cmd) throws Exception {
    AbstractCommandProcessor	processor;
    String			output;
    String			result;

    processor = getOwner().getProcessor();
    output    = processor.process(cmd);
    if (output != null) {
      result =   "Problem encountered processing the following command:\n"
               + Utils.insertLineBreaks(cmd.getCommand(), 80)
               + "\n\nEncountered error:\n" + output;
      getOwner().setLastError(result);
      getOwner().getLogger().severe(result);
    }
    else {
      result = null;
      getOwner().addToHistory(cmd.getCommand());
    }

    return result;
  }

  /**
   * Performs some postprocessing.
   *
   * @param cmd	the command that was executed
   * @param success	true if successfully executed
   * @param lastError	the error, or null if none happened
   */
  protected void postProcess(ScriptingCommand cmd, boolean success, String lastError) {
    ScriptingLogger.getSingleton().log(getOwner(), cmd, success, lastError);
  }

  /**
   * Executes the scripting commands.
   */
  @Override
  public void run() {
    ScriptingCommand		command;
    ScriptingCommandCode	code;
    boolean			success;
    String			lastError;
    StringWriter		writer;

    while (m_Running) {
      try {
        command = m_Commands.poll(100, TimeUnit.MILLISECONDS);
        if ((command != null) && (m_Running)) {
          if (!AbstractScriptingEngine.check(command.getCommand()))
            continue;

          m_Processing = true;

          preProcess(command);

          // process command
          try {
            lastError = doProcess(command);
            success   = (lastError == null);
          }
          catch (Exception e) {
            success = false;
            writer  = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            lastError =   "Exception encountered processing the following command:\n"
      		  + Utils.insertLineBreaks(command.getCommand(), 80)
      		  + "\n\nEncountered exception:\n" + writer.toString();
            System.err.println(lastError);
          }

          postProcess(command, success, lastError);

          // any code to execute?
          if (command.hasCode()) {
            code = command.getCode();
            code.setError(lastError);
            code.execute();
          }

          m_Processing = false;
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}