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
 * CurrentlyExecuted.java
 * Copyright (C) 2013-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.flow.core.Actor;
import adams.gui.core.MapTableModel;

import javax.swing.table.TableModel;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Shows what actors are currently being executed.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-update-interval &lt;int&gt; (property: updateInterval)
 * &nbsp;&nbsp;&nbsp;The update interval after which the GUI gets refreshed.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class CurrentlyExecuted
  extends AbstractTimedFlowExecutionListenerWithTable {

  /** for serialization. */
  private static final long serialVersionUID = -6155792276833652477L;
  
  /** keeps track of the actors that are being executed (actor name - start time). */
  protected Map<String,String> m_Counts;
  
  /** the date formatter for the timestamps. */
  protected transient DateFormat m_DateFormat;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Shows what actors are currently being executed.";
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Counts = new HashMap<>();
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
   * The title of this listener.
   * 
   * @return		the title
   */
  public String getListenerTitle() {
    return "Currently executed";
  }

  /**
   * Creates a new table model with the current data.
   * 
   * @return		the model with the current data
   */
  @Override
  protected synchronized TableModel createTableModel() {
    return new MapTableModel(new HashMap<>(m_Counts), new String[]{"Actor", "Timestamp"});
  }
  
  /**
   * Gets called when the flow execution starts.
   */
  @Override
  public synchronized void startListening() {
    super.startListening();
    
    m_Counts.clear();
    m_DateFormat = DateUtils.getTimestampFormatterMsecs();
  }
  
  /**
   * Gets called before the actor gets executed.
   * 
   * @param actor	the actor that gets executed
   */
  @Override
  public synchronized void preExecute(Actor actor) {
    String	key;
    
    super.postExecute(actor);
    
    key = actor.getFullName();
    m_Counts.put(key, m_DateFormat.format(new Date()));
  }
  
  /**
   * Gets called after the actor was executed.
   * 
   * @param actor	the actor that was executed
   */
  @Override
  public synchronized void postExecute(Actor actor) {
    String	key;
    
    super.postExecute(actor);
    
    key = actor.getFullName();
    m_Counts.remove(key);
    
    incCounter();
  }

  /**
   * Gets called when the flow execution ends.
   * <br><br>
   * Outputs the counts in debug mode.
   */
  @Override
  public void finishListening() {
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
