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
 * DeQueue.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.flow.control.StorageName;
import adams.flow.control.StorageQueueHandler;
import adams.flow.control.StorageUpdater;
import adams.flow.core.QueueHelper;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Polls the specified queue in internal storage for an item, blocks till an item is available.<br/>
 * NB: The user needs to stop the flow as the DeQueue source will never stop, as it assumes that there will be more data coming through.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: DeQueue
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the queue in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: queue
 * </pre>
 * 
 * <pre>-interval &lt;int&gt; (property: interval)
 * &nbsp;&nbsp;&nbsp;The polling interval in milli-seconds.
 * &nbsp;&nbsp;&nbsp;default: 50
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7171 $
 */
public class DeQueue
  extends AbstractSource
  implements StorageUpdater {

  /** for serialization. */
  private static final long serialVersionUID = 6216146938771296415L;

  /** the name of the queue in the internal storage. */
  protected StorageName m_StorageName;

  /** the poll interval in msec. */
  protected int m_Interval;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Polls the specified queue in internal storage for an item, blocks "
	+ "till an item is available.\n"
        + "NB: The user needs to stop the flow as the DeQueue source will "
	+ "never stop, as it assumes that there will be more data coming through.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "storage-name", "storageName",
	    new StorageName("queue"));

    m_OptionManager.add(
	    "interval", "interval",
	    50, 1, null);
  }

  /**
   * Returns whether storage items are being updated.
   * 
   * @return		true if storage items are updated
   */
  public boolean isUpdatingStorage() {
    return !getSkip();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
    result += QuickInfoHelper.toString(this, "interval", m_Interval, ", interval: ");

    return result;
  }

  /**
   * Sets the name for the queue in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name for the queue in the internal storage.
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the queue in the internal storage.";
  }

  /**
   * Sets the polling interval in seconds.
   *
   * @param value	the interval
   */
  public void setInterval(int value) {
    m_Interval = value;
    reset();
  }

  /**
   * Returns the polling interval in milli-seconds.
   *
   * @return		the interval
   */
  public int getInterval() {
    return m_Interval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String intervalTipText() {
    return "The polling interval in milli-seconds.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything ok, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    
    result = null;
    
    if (!QueueHelper.hasQueue(this, m_StorageName))
      result = "Queue '" + m_StorageName + "' not available from internal storage!";
    
    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return m_Executed;
  }
  
  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token		result;
    StorageQueueHandler	queue;

    result = null;
    queue  = QueueHelper.getQueue(this, m_StorageName);
    
    while ((result == null) && !m_Stopped) {
      try {
	if (queue.size() > 0) {
	  result = new Token(queue.remove());
	}
	else {
	  synchronized(this) {
	    wait(m_Interval);
	  }
	}
      }
      catch (Exception e) {
	// ignored
      }
    }
    
    return result;
  }
}
