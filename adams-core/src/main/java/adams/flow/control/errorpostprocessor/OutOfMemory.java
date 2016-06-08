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
 * OutOfMemory.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.errorpostprocessor;

import adams.core.Properties;
import adams.db.LogEntry;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Compatibility;
import adams.flow.core.ErrorHandler;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;

import java.util.Date;

/**
 <!-- globalinfo-start -->
 * In case of an out of memory error, sends a log entry to the specified callable actor (if available).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-log &lt;adams.flow.core.CallableActorReference&gt; (property: log)
 * &nbsp;&nbsp;&nbsp;The name of the callable log actor to use (logging disabled if actor not 
 * &nbsp;&nbsp;&nbsp;found).
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OutOfMemory
  extends AbstractErrorPostProcessor {

  private static final long serialVersionUID = -5136181833130378152L;

  public static final String OUTOFMEMORY = "java.lang.OutOfMemoryError";

  /** the callable name. */
  protected CallableActorReference m_Log;

  /** the callable log actor. */
  protected Actor m_LogActor;

  /** whether the callable log actor has been set up. */
  protected boolean m_LogActorSetup;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "In case of an out of memory error, sends a log entry to the specified "
	+ "callable actor (if available).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "log", "log",
      new CallableActorReference("unknown"));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_LogActor      = null;
    m_LogActorSetup = false;
  }

  /**
   * Sets the name of the callable log actor to use.
   *
   * @param value 	the callable name
   */
  public void setLog(CallableActorReference value) {
    m_Log = value;
    reset();
  }

  /**
   * Returns the name of the callable log actor in use.
   *
   * @return 		the callable name
   */
  public CallableActorReference getLog() {
    return m_Log;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String logTipText() {
    return "The name of the callable log actor to use (logging disabled if actor not found).";
  }

  /**
   * Hook method for checks.
   *
   * @param handler	the error handler that this call comes from
   * @param source	the source actor where the error originated
   * @param type	the type of error
   * @param msg		the error message
   */
  @Override
  protected void check(ErrorHandler handler, Actor source, String type, String msg) {
    Compatibility	comp;

    super.check(handler, source, type, msg);

    if (!m_LogActorSetup) {
      m_LogActorSetup = true;
      m_LogActor      = m_Helper.findCallableActorRecursive(source, getLog());
      if (m_LogActor == null) {
        msg = "Couldn't find callable log actor '" + getLog() + "' - logging disabled!";
        getLogger().warning(msg);
      }
      else {
	comp = new Compatibility();
	if (!comp.isCompatible(new Class[]{LogEntry.class}, ((InputConsumer) m_LogActor).accepts()))
	  getLogger().severe("Log actor '" + getLog() + "' must accept " + LogEntry.class.getName() + " - logging disabled!");
      }
    }
  }

  /**
   * Performs the actual post-processing of the error.
   *
   * @param handler	the error handler that this call comes from
   * @param source	the source actor where the error originated
   * @param type	the type of error
   * @param msg		the error message
   */
  @Override
  protected void doPostProcessError(ErrorHandler handler, Actor source, String type, String msg) {
    LogEntry		entry;
    Properties		props;

    if (msg.contains(OUTOFMEMORY)) {
      getLogger().info("Error message contains '" + OUTOFMEMORY + "'");
      if (m_LogActor != null) {
	props   = new Properties();
	props.setProperty("Message", msg);
	entry = new LogEntry();
	entry.setGeneration(new Date());
	entry.setSource(source.getFullName());
	entry.setType(type);
	entry.setStatus(LogEntry.STATUS_NEW);
	entry.setMessage(props);
	((InputConsumer) m_LogActor).input(new Token(entry));
	m_LogActor.execute();
	getLogger().info("Send error message to actor: " + m_Logger);
      }
      else {
	getLogger().info("Log actor not available to send error to: " + m_Logger);
      }
    }
  }
}
