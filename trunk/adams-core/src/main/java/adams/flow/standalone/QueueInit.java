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
 * QueueInit.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import java.util.Hashtable;

import adams.core.QuickInfoHelper;
import adams.db.LogEntry;
import adams.flow.control.StorageName;
import adams.flow.control.StorageQueueHandler;
import adams.flow.control.StorageUpdater;
import adams.flow.core.AbstractActor;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorUser;

/**
 <!-- globalinfo-start -->
 * Creates an empty queue in internal storage under the specified name.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
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
 * &nbsp;&nbsp;&nbsp;default: QueueInit
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
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
 * <pre>-keep-existing &lt;boolean&gt; (property: keepExisting)
 * &nbsp;&nbsp;&nbsp;If enabled, existing queues won't get re-initialized.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-limit &lt;int&gt; (property: limit)
 * &nbsp;&nbsp;&nbsp;The limit of the queue; use &lt;= 0 for unlimited size.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-log &lt;adams.flow.core.CallableActorReference&gt; (property: log)
 * &nbsp;&nbsp;&nbsp;The name of the (optional) callable actor to use for logging errors of type 
 * &nbsp;&nbsp;&nbsp;adams.db.LogEntry.
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 * 
 * <pre>-monitor &lt;adams.flow.core.CallableActorReference&gt; (property: monitor)
 * &nbsp;&nbsp;&nbsp;The name of the (optional) callable actor to use for monitoring; generates 
 * &nbsp;&nbsp;&nbsp;tokens of type adams.db.LogEntry.
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8036 $
 */
public class QueueInit
  extends AbstractStandalone
  implements StorageUpdater, CallableActorUser {

  /** for serialization. */
  private static final long serialVersionUID = 4182914190162129217L;

  /** the key for backing up the log actor. */
  public final static String BACKUP_LOGACTOR = "log actor";

  /** the key for backing up the monitoring actor. */
  public final static String BACKUP_MONITORACTOR = "monitor actor";

  /** the name of the queue in the internal storage. */
  protected StorageName m_StorageName;
  
  /** whether to keep existing queues. */
  protected boolean m_KeepExisting;

  /** the limit of the queue (<= 0 is unlimited). */
  protected int m_Limit;

  /** the callable name for the log. */
  protected CallableActorReference m_Log;

  /** the log actor. */
  protected AbstractActor m_LogActor;

  /** the callable name for the monitor. */
  protected CallableActorReference m_Monitor;

  /** the monitor actor. */
  protected AbstractActor m_MonitorActor;

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
	"Creates an empty queue in internal storage under the specified name.";
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
	    "keep-existing", "keepExisting",
	    false);

    m_OptionManager.add(
	    "limit", "limit",
	    -1, -1, null);

    m_OptionManager.add(
	    "log", "log",
	    new CallableActorReference("unknown"));

    m_OptionManager.add(
	    "monitor", "monitor",
	    new CallableActorReference("unknown"));
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_LogActor = null;
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
    result += QuickInfoHelper.toString(this, "keepExisting", m_KeepExisting, "keep", ", ");
    result += QuickInfoHelper.toString(this, "limit", m_Limit, ", limit: ");
    result += QuickInfoHelper.toString(this, "log", m_Log, ", log: ");
    result += QuickInfoHelper.toString(this, "monitor", m_Monitor, ", monitor: ");

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
   * Sets whether to keep any existing queue rather than overwriting it.
   *
   * @param value	true if to keep existing
   */
  public void setKeepExisting(boolean value) {
    m_KeepExisting = value;
    reset();
  }

  /**
   * Returns whether to keep any existing queue rather than overwriting it.
   *
   * @return		true if to keep existing
   */
  public boolean getKeepExisting() {
    return m_KeepExisting;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keepExistingTipText() {
    return "If enabled, existing queues won't get re-initialized.";
  }

  /**
   * Sets the limit of the queue.
   *
   * @param value	the limit, <=0 for unlimited
   */
  public void setLimit(int value) {
    if (value <= 0)
      value = -1;
    m_Limit = value;
    reset();
  }

  /**
   * Returns the limit of the queue.
   *
   * @return		the limit, <=0 is unlimited
   */
  public int getLimit() {
    return m_Limit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String limitTipText() {
    return "The limit of the queue; use <= 0 for unlimited size.";
  }

  /**
   * Sets the name of the callable actor to use for logging errors.
   *
   * @param value 	the callable name
   */
  public void setLog(CallableActorReference value) {
    m_Log = value;
    reset();
  }

  /**
   * Returns the name of the callable actor in use for logging errors.
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
    return 
	"The name of the (optional) callable actor to use for logging errors of type " 
	+ LogEntry.class.getName() + ".";
  }

  /**
   * Sets the name of the callable actor to use for monitoring.
   *
   * @param value 	the callable name
   */
  public void setMonitor(CallableActorReference value) {
    m_Monitor = value;
    reset();
  }

  /**
   * Returns the name of the callable actor in use for monitoring.
   *
   * @return 		the callable name
   */
  public CallableActorReference getMonitor() {
    return m_Monitor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String monitorTipText() {
    return 
	"The name of the (optional) callable actor to use for monitoring; generates tokens of type " 
	+ LogEntry.class.getName() + ".";
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   *
   * @param name	the name of the actor
   * @return		the callable actor or null if not found
   */
  protected AbstractActor findCallableActor(CallableActorReference name) {
    return m_Helper.findCallableActorRecursive(this, name);
  }

  /**
   * Returns the currently set callable actor.
   *
   * @return		the actor, can be null
   */
  @Override
  public AbstractActor getCallableActor() {
    return m_LogActor;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    pruneBackup(BACKUP_LOGACTOR);
    pruneBackup(BACKUP_MONITORACTOR);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_LogActor != null)
      result.put(BACKUP_LOGACTOR, m_LogActor);
    if (m_MonitorActor != null)
      result.put(BACKUP_MONITORACTOR, m_MonitorActor);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    super.restoreState(state);

    if (state.containsKey(BACKUP_LOGACTOR)) {
      m_LogActor = (AbstractActor) state.get(BACKUP_LOGACTOR);
      state.remove(BACKUP_LOGACTOR);
    }

    if (state.containsKey(BACKUP_MONITORACTOR)) {
      m_MonitorActor = (AbstractActor) state.get(BACKUP_MONITORACTOR);
      state.remove(BACKUP_MONITORACTOR);
    }
  }
  
  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    
    result = super.setUp();
    
    if (result == null) {
      m_LogActor     = findCallableActor(getLog());
      m_MonitorActor = findCallableActor(getMonitor());
    }
    
    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    StorageQueueHandler	handler;
    
    if ((m_KeepExisting && !getStorageHandler().getStorage().has(m_StorageName)) || !m_KeepExisting) {
      handler = new StorageQueueHandler(m_StorageName.getValue(), m_Limit, m_LogActor, m_MonitorActor);
      handler.setLoggingLevel(getLoggingLevel());
      getStorageHandler().getStorage().put(m_StorageName, handler);
    }
    
    return null;
  }
}
