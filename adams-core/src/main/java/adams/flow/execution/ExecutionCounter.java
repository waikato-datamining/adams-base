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
 * ExecutionCounter.java
 * Copyright (C) 2013-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import adams.core.io.PlaceholderFile;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Actor;
import adams.gui.core.MapTableModel;

import javax.swing.table.TableModel;
import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Counts how often an actor was executed.<br>
 * The final counts can be written to a log file in CSV format.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-update-interval &lt;int&gt; (property: updateInterval)
 * &nbsp;&nbsp;&nbsp;The update interval after which the GUI gets refreshed.
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
 */
public class ExecutionCounter
  extends AbstractTimedFlowExecutionListenerWithTable {

  /** for serialization. */
  private static final long serialVersionUID = -4978449149708112013L;

  /** the file to write to. */
  protected PlaceholderFile m_LogFile;

  /** keeps track of the execution count (actor name - count). */
  protected Map<String,Integer> m_Counts;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Counts how often an actor was executed.\n"
      + "The final counts can be written to a log file in CSV format.";
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
    
    m_Counts = new HashMap<>();
  }

  @Override
  protected int getDefaultUpdateInterval() {
    return 100;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String updateIntervalTipText() {
    return "The update interval after which the GUI gets refreshed.";
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
    return "Execution counter";
  }

  /**
   * Creates a new table model with the current data.
   * 
   * @return		the model with the current data
   */
  @Override
  protected synchronized TableModel createTableModel() {
    return new MapTableModel(new HashMap<>(m_Counts), new String[]{"Actor", "Count"});
  }
  
  /**
   * Gets called when the flow execution starts.
   */
  @Override
  public synchronized void startListening() {
    super.startListening();
    
    m_Counts.clear();
  }
  
  /**
   * Gets called after the actor was executed.
   * 
   * @param actor	the actor that was executed
   */
  @Override
  public synchronized void postExecute(Actor actor) {
    String	key;
    int		count;
    
    super.postExecute(actor);
    
    key = actor.getFullName();
    if (!m_Counts.containsKey(key))
      count = 0;
    else
      count = m_Counts.get(key);
    count++;
    m_Counts.put(key, count);
    
    incCounter();
  }

  /**
   * Gets called when the flow execution ends.
   * <br><br>
   * Outputs the counts in debug mode.
   */
  @Override
  public void finishListening() {
    SpreadSheet			sheet;
    CsvSpreadSheetWriter 	writer;

    if (!m_LogFile.isDirectory()) {
      sheet  = m_Table.toSpreadSheet();
      writer = new CsvSpreadSheetWriter();
      if (!writer.write(sheet, m_LogFile))
        getLogger().severe("Failed to write counts to: " + m_LogFile);
    }

    super.finishListening();
    
    if (isLoggingEnabled())
      getLogger().info(m_Counts.toString());
  }
  
  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public synchronized void cleanUp() {
    super.cleanUp();

    if (m_Counts != null) {
      m_Counts.clear();
      m_Counts = null;
    }
  }
}
