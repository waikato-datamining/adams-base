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
 * TraceExecutionTime.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Actor;
import adams.flow.core.Token;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Shows how much time actors are taking being executed (each execution is logged) separately.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-type &lt;Input|Execute|Output&gt; [-type ...] (property: types)
 * &nbsp;&nbsp;&nbsp;The execution types to log.
 * &nbsp;&nbsp;&nbsp;default: EXECUTE
 * </pre>
 *
 * <pre>-log-file &lt;adams.core.io.PlaceholderFile&gt; (property: logFile)
 * &nbsp;&nbsp;&nbsp;The CSV log file to write to, uses TAB as column separator; writing is disabled
 * &nbsp;&nbsp;&nbsp;if pointing to a directory.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class TraceExecutionTime
  extends AbstractFlowExecutionListener {

  /** for serialization. */
  private static final long serialVersionUID = -6155792276833652477L;

  /** the type for "input". */
  public final static String TYPE_INPUT = "input";

  /** the type for "execute". */
  public final static String TYPE_EXECUTE = "execute";

  /** the type for "output". */
  public final static String TYPE_OUTPUT = "output";

  /** the types to output. */
  protected ExecutionType[] m_Types;

  /** the file to write to. */
  protected PlaceholderFile m_LogFile;

  /** keeps track of the start time of an actor. */
  protected Map<String,Long> m_Start;

  /** whether we can log anything. */
  protected boolean m_Enabled;

  /** the types to log. */
  protected Set<ExecutionType> m_TypesSet;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Shows how much time actors are taking being executed (each execution is logged) separately.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "types",
      new ExecutionType[]{ExecutionType.EXECUTE});

    m_OptionManager.add(
      "log-file", "logFile",
      new PlaceholderFile("."));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Start   = new HashMap<>();
    m_Enabled = false;
  }

  /**
   * Sets the types to log.
   *
   * @param value	the types
   */
  public void setTypes(ExecutionType[] value) {
    m_Types = value;
    reset();
  }

  /**
   * Returns the types to log.
   *
   * @return		the types
   */
  public ExecutionType[] getTypes() {
    return m_Types;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typesTipText() {
    return "The execution types to log.";
  }

  /**
   * Sets the log file.
   *
   * @param value	the file
   */
  public void setLogFile(PlaceholderFile value) {
    m_LogFile = value;
    reset();
  }

  /**
   * Returns the log file.
   *
   * @return		the condition
   */
  public PlaceholderFile getLogFile() {
    return m_LogFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String logFileTipText() {
    return "The CSV log file to write to, uses TAB as column separator; writing is disabled if pointing to a directory.";
  }

  /**
   * The title of this listener.
   *
   * @return		the title
   */
  public String getListenerTitle() {
    return "Trace execution time";
  }

  /**
   * Gets called when the flow execution starts.
   */
  @Override
  public void startListening() {
    super.startListening();
    m_TypesSet = new HashSet<>(Arrays.asList(m_Types));
    m_Start.clear();
    m_Enabled = !m_LogFile.isDirectory() && !m_TypesSet.isEmpty();
    if (m_Enabled && m_LogFile.exists()) {
      if (!m_LogFile.delete()) {
	getLogger().severe("Failed to remove old log file: " + m_LogFile);
	m_Enabled = false;
      }
    }
    if (m_Enabled)
      FileUtils.writeToFile(m_LogFile.getAbsolutePath(), "Actor\tType\tTime (msec)");
  }

  /**
   * Adds the start time.
   *
   * @param key		the key in the hashtable
   */
  protected void addStart(String key) {
    m_Start.put(key, System.currentTimeMillis());
  }

  /**
   * Adds the duration to the overall count.
   *
   * @param key		the key in the hashtable
   */
  protected void addDuration(String key) {
    long	current;
    long	start;
    long	overall;

    if (m_Start.containsKey(key)) {
      start   = m_Start.get(key);
      current = System.currentTimeMillis();
      overall = (current - start);
      m_Start.remove(key);
      if (m_Enabled)
	FileUtils.writeToFile(m_LogFile.getAbsolutePath(), key + "\t" + overall);
    }
  }

  /**
   * Gets called before the actor receives the token.
   *
   * @param actor	the actor that will receive the token
   * @param token	the token that the actor will receive
   */
  @Override
  public void preInput(Actor actor, Token token) {
    if (m_TypesSet.contains(ExecutionType.INPUT))
      addStart(actor.getFullName() + "\t" + TYPE_INPUT);
  }

  /**
   * Gets called after the actor received the token.
   *
   * @param actor	the actor that received the token
   */
  @Override
  public void postInput(Actor actor) {
    if (m_TypesSet.contains(ExecutionType.INPUT))
      addDuration(actor.getFullName() + "\t" + TYPE_INPUT);
  }

  /**
   * Gets called before the actor gets executed.
   *
   * @param actor	the actor that gets executed
   */
  @Override
  public void preExecute(Actor actor) {
    if (m_TypesSet.contains(ExecutionType.EXECUTE))
      addStart(actor.getFullName() + "\t" + TYPE_EXECUTE);
  }

  /**
   * Gets called after the actor was executed.
   *
   * @param actor	the actor that was executed
   */
  @Override
  public void postExecute(Actor actor) {
    if (m_TypesSet.contains(ExecutionType.EXECUTE))
      addDuration(actor.getFullName() + "\t" + TYPE_EXECUTE);
  }

  /**
   * Gets called before a token gets obtained from the actor.
   *
   * @param actor	the actor the token gets obtained from
   */
  @Override
  public void preOutput(Actor actor) {
    if (m_TypesSet.contains(ExecutionType.OUTPUT))
      addStart(actor.getFullName() + "\t" + TYPE_OUTPUT);
  }

  /**
   * Gets called after a token was acquired from the actor.
   *
   * @param actor	the actor that the token was acquired from
   * @param token	the token that was acquired from the actor
   */
  @Override
  public void postOutput(Actor actor, Token token) {
    if (m_TypesSet.contains(ExecutionType.OUTPUT))
      addDuration(actor.getFullName() + "\t" + TYPE_OUTPUT);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    if (m_Start != null) {
      m_Start.clear();
      m_Start = null;
    }
  }
}
