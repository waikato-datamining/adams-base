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
 * Time.java
 * Copyright (C) 2013-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import adams.core.io.PlaceholderFile;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.gui.core.MapTableModel;

import javax.swing.table.TableModel;
import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Shows how much time actors are taking in their method calls (gets accumulated).
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
 * <pre>-update-interval &lt;int&gt; (property: updateInterval)
 * &nbsp;&nbsp;&nbsp;The update interval (= number of method executions) after which the GUI
 * &nbsp;&nbsp;&nbsp;gets refreshed.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-log-file &lt;adams.core.io.PlaceholderFile&gt; (property: logFile)
 * &nbsp;&nbsp;&nbsp;The CSV log file to write to; writing is disabled if pointing to a directory.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Time
  extends AbstractTimedFlowExecutionListenerWithTable {

  /** for serialization. */
  private static final long serialVersionUID = -6155792276833652477L;

  /** the suffix for "input". */
  public final static String SUFFIX_INPUT = " [input]";

  /** the suffix for "execute". */
  public final static String SUFFIX_EXECUTE = " [execute]";

  /** the suffix for "output". */
  public final static String SUFFIX_OUTPUT = " [output]";

  /** keeps track of the start time of an actor. */
  protected Map<String,Long> m_Start;

  /** keeps track of the time that actors used up. */
  protected Map<String,Long> m_Overall;

  /** the file to write to. */
  protected PlaceholderFile m_LogFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Shows how much time actors are taking in their method calls (gets accumulated).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

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
    m_Overall = new HashMap<>();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String updateIntervalTipText() {
    return "The update interval (= number of method executions) after which the GUI gets refreshed.";
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
    return "The CSV log file to write to; writing is disabled if pointing to a directory.";
  }

  /**
   * The title of this listener.
   *
   * @return		the title
   */
  public String getListenerTitle() {
    return "Time";
  }

  /**
   * Gets called when the flow execution starts.
   */
  @Override
  public void startListening() {
    super.startListening();

    m_Overall.clear();
    m_Start.clear();
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
    long	overall;
    long	start;

    if (m_Start.containsKey(key)) {
      current = System.currentTimeMillis();
      overall = 0;
      start   = m_Start.get(key);
      if (m_Overall.containsKey(key))
	overall = m_Overall.get(key);
      overall += (current - start);
      m_Overall.put(key, overall);
      m_Start.remove(key);
    }

    incCounter();
  }

  /**
   * Returns the default update interval.
   */
  @Override
  protected int getDefaultUpdateInterval() {
    return 100;
  }

  /**
   * Creates a new table model with the current data.
   *
   * @return		the model with the current data
   */
  @Override
  protected TableModel createTableModel() {
    return new MapTableModel(new HashMap<>(m_Overall), new String[]{"Actor", "Time in msec"}, Long.class);
  }

  /**
   * Gets called before the actor receives the token.
   *
   * @param actor	the actor that will receive the token
   * @param token	the token that the actor will receive
   */
  @Override
  public void preInput(Actor actor, Token token) {
    addStart(actor.getFullName() + SUFFIX_INPUT);
  }

  /**
   * Gets called after the actor received the token.
   *
   * @param actor	the actor that received the token
   */
  @Override
  public void postInput(Actor actor) {
    addDuration(actor.getFullName() + SUFFIX_INPUT);
  }

  /**
   * Gets called before the actor gets executed.
   *
   * @param actor	the actor that gets executed
   */
  @Override
  public void preExecute(Actor actor) {
    addStart(actor.getFullName() + SUFFIX_EXECUTE);
  }

  /**
   * Gets called after the actor was executed.
   *
   * @param actor	the actor that was executed
   */
  @Override
  public void postExecute(Actor actor) {
    addDuration(actor.getFullName() + SUFFIX_EXECUTE);
  }

  /**
   * Gets called before a token gets obtained from the actor.
   *
   * @param actor	the actor the token gets obtained from
   */
  @Override
  public void preOutput(Actor actor) {
    addStart(actor.getFullName() + SUFFIX_OUTPUT);
  }

  /**
   * Gets called after a token was acquired from the actor.
   *
   * @param actor	the actor that the token was acquired from
   * @param token	the token that was acquired from the actor
   */
  @Override
  public void postOutput(Actor actor, Token token) {
    addDuration(actor.getFullName() + SUFFIX_OUTPUT);
  }

  /**
   * Gets called when the flow execution ends.
   */
  @Override
  public void finishListening() {
    SpreadSheet sheet;
    CsvSpreadSheetWriter writer;

    if (!m_LogFile.isDirectory()) {
      sheet  = m_Table.toSpreadSheet();
      writer = new CsvSpreadSheetWriter();
      if (!writer.write(sheet, m_LogFile))
	getLogger().severe("Failed to write times to: " + m_LogFile);
    }

    super.finishListening();

    if (isLoggingEnabled())
      getLogger().info(m_Table.toSpreadSheet().toString());
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    if (m_Overall != null) {
      m_Overall.clear();
      m_Overall = null;
      m_Start.clear();
      m_Start = null;
    }
  }
}
