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
 * ExecutionCounter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import java.util.Hashtable;

import javax.swing.table.TableModel;

import adams.flow.core.Actor;
import adams.gui.core.HashtableTableModel;

/**
 <!-- globalinfo-start -->
 * Counts how often an actor was executed.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExecutionCounter
  extends AbstractTimedFlowExecutionListenerWithTable {

  /** for serialization. */
  private static final long serialVersionUID = -4978449149708112013L;
  
  /** keeps track of the execution count (actor name - count). */
  protected Hashtable<String,Integer> m_Counts;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Counts how often an actor was executed.";
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Counts = new Hashtable<String,Integer>();
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
  protected TableModel createTableModel() {
    return new HashtableTableModel((Hashtable) m_Counts.clone(), new String[]{"Actor", "Count"});
  }
  
  /**
   * Gets called when the flow execution starts.
   */
  @Override
  public void startListening() {
    super.startListening();
    
    m_Counts.clear();
  }
  
  /**
   * Gets called after the actor was executed.
   * 
   * @param actor	the actor that was executed
   */
  @Override
  public void postExecute(Actor actor) {
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
    super.finishListening();
    
    if (isLoggingEnabled())
      getLogger().info(m_Counts.toString());
  }
  
  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    if (m_Counts != null) {
      m_Counts.clear();
      m_Counts = null;
    }
  }
}
