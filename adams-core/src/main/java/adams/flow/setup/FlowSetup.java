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
 * FlowSetup.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.setup;

import java.util.HashSet;
import java.util.Iterator;

import adams.core.Pausable;
import adams.core.Stoppable;
import adams.core.io.FlowFile;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.event.FlowSetupStateEvent;
import adams.event.FlowSetupStateListener;
import adams.flow.core.AbstractActor;

/**
 <!-- globalinfo-start -->
 * Container object for a flow file with name and information about it.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 *         The name of the flow.
 *         default: noname
 * </pre>
 *
 * <pre>-description &lt;java.lang.String&gt; (property: description)
 *         The description of the flow.
 *         default: nodescription
 * </pre>
 *
 * <pre>-file &lt;adams.core.io.FlowFile&gt; (property: file)
 *         The flow file.
 *         default: .
 * </pre>
 *
 * <pre>-headless (property: headless)
 *         If set to true, the flow is run in headless mode without GUI components.
 * </pre>
 *
 * <pre>-on-error &lt;java.lang.String&gt; (property: onError)
 *         The name of the flow setup to execute when this setup exist with an error
 *         .
 *         default:
 * </pre>
 *
 * <pre>-on-finish &lt;java.lang.String&gt; (property: onFinish)
 *         The name of the flow setup to execute when this setup finishes.
 *         default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowSetup
  extends AbstractOptionHandler
  implements Comparable, Stoppable, Pausable {

  /** for serialization. */
  private static final long serialVersionUID = -1802605995073395422L;

  /** the flow file. */
  protected FlowFile m_File;

  /** the name of the flow. */
  protected String m_Name;

  /** the description of the flow. */
  protected String m_Description;

  /** whether the execution is to be headless, i.e., no GUI components. */
  protected boolean m_Headless;

  /** the name of the flow to execute when this one finishes. */
  protected String m_OnFinish;

  /** the name of the flow to execute when this one exits with an error. */
  protected String m_OnError;

  /** the thread executing the flow. */
  protected transient FlowSetupWorker m_FlowSetupWorker;

  /** the last actor that was executed. */
  protected AbstractActor m_LastActor;

  /** the last error when executing the flow. */
  protected transient String m_LastError;

  /** the listeners for the flow execution to finish. */
  protected transient HashSet<FlowSetupStateListener> m_FlowSetupStateListeners;

  /** the manager this setup belongs to. */
  protected transient FlowSetupManager m_Manager;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Container object for a flow file with name and information about it.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "name", "name",
	    "noname");

    m_OptionManager.add(
	    "description", "description",
	    "nodescription");

    m_OptionManager.add(
	    "file", "file",
	    new FlowFile("."));

    m_OptionManager.add(
	    "headless", "headless",
	    false);

    m_OptionManager.add(
	    "on-error", "onError",
	    "");

    m_OptionManager.add(
	    "on-finish", "onFinish",
	    "");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FlowSetupWorker    = null;
    m_LastActor = null;
    m_LastError = null;
    m_Manager   = null;
  }

  /**
   * Returns the listeners hashtable, instantiates it if necessary.
   *
   * @return		the hashtable
   */
  protected HashSet<FlowSetupStateListener> getFlowSetupStateChangeListeners() {
    if (m_FlowSetupStateListeners == null)
      m_FlowSetupStateListeners = new HashSet<FlowSetupStateListener>();
    return m_FlowSetupStateListeners;
  }


  /**
   * Sets the flow file.
   *
   * @param value	the file
   */
  public void setFile(FlowFile value) {
    m_File = value;
  }

  /**
   * Returns the current flow file.
   *
   * @return		the file
   */
  public FlowFile getFile() {
    return m_File;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String fileTipText() {
    return "The flow file.";
  }

  /**
   * Sets the name of the flow.
   *
   * @param value	the name
   */
  public void setName(String value) {
    m_Name = value;
  }

  /**
   * Returns the name of the flow.
   *
   * @return		the name
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String nameTipText() {
    return "The name of the flow.";
  }

  /**
   * Sets the description for the flow.
   *
   * @param value	the description
   */
  public void setDescription(String value) {
    m_Description = value;
  }

  /**
   * Returns the description of the flow.
   *
   * @return		the description
   */
  public String getDescription() {
    return m_Description;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String descriptionTipText() {
    return "The description of the flow.";
  }

  /**
   * Sets whether the actor is to be run in headless mode, i.e., suppressing
   * GUI components.
   *
   * @param value	if true then GUI components will be suppressed
   */
  public void setHeadless(boolean value) {
    m_Headless = value;
  }

  /**
   * Returns whether the actor is run in headless mode.
   *
   * @return		true if GUI components are suppressed
   */
  public boolean isHeadless() {
    return m_Headless;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String headlessTipText() {
    return "If set to true, the flow is run in headless mode without GUI components.";
  }

  /**
   * Sets the name of the flow to execute when this setup finishes.
   *
   * @param value	the name of the flow
   */
  public void setOnFinish(String value) {
    if (value == null)
      value = "";

    if (value.equals(m_Name)) {
      getLogger().severe("Circular reference!");
      return;
    }

    m_OnFinish = value;
  }

  /**
   * Returns the name of the flow to execute when this setup finishes.
   *
   * @return		the name of the flow
   */
  public String getOnFinish() {
    return m_OnFinish;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String onFinishTipText() {
    return "The name of the flow setup to execute when this setup finishes.";
  }

  /**
   * Checks whether a flow is defined in case of finishing the execution.
   *
   * @return		true if a flow is defined
   */
  public boolean hasOnFinish() {
    return (m_OnFinish.length() > 0);
  }

  /**
   * Sets the name of the flow to execute when this setup exits with an error.
   *
   * @param value	the name of the flow
   */
  public void setOnError(String value) {
    if (value == null)
      value = "";

    if (value.equals(m_Name)) {
      getLogger().severe("Circular reference!");
      return;
    }

    m_OnError = value;
  }

  /**
   * Returns the name of the flow to execute when this setup exist with an error.
   *
   * @return		the name of the flow
   */
  public String getOnError() {
    return m_OnError;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String onErrorTipText() {
    return "The name of the flow setup to execute when this setup exist with an error.";
  }

  /**
   * Checks whether a flow is defined in case of an error.
   *
   * @return		true if a flow is defined
   */
  public boolean hasOnError() {
    return (m_OnError.length() > 0);
  }

  /**
   * Checks whether an error was recorded with the last execution.
   *
   * @return		true if an error had occurred
   */
  public boolean hasLastError() {
    return (m_LastError != null);
  }

  /**
   * Sets the error that was encountered.
   *
   * @param value	the error string or null
   */
  public void setLastError(String value) {
    m_LastError = value;
    if (value != null)
      showStatus("Error ('" + m_Name + "'): " + value);
    else
      showStatus("");
  }

  /**
   * Returns the last error that occurred, if any.
   *
   * @return		the error or null if none occurred
   */
  public String retrieveLastError() {
    return m_LastError;
  }

  /**
   * Sets the manager this setup belongs to.
   *
   * @param value	the manager
   */
  public void setOwner(FlowSetupManager value) {
    m_Manager = value;
  }

  /**
   * Returns the manager thi setup belongs to.
   *
   * @return		the manager
   */
  public FlowSetupManager getOwner() {
    return m_Manager;
  }

  /**
   * Displays a message, using the manager's StatusMessageHandler (if one is
   * set).
   *
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    if ((getOwner() != null) && (getOwner().getStatusMessageHandler() != null))
      getOwner().getStatusMessageHandler().showStatus(msg);
  }

  /**
   * Returns whether the flow is running at the moment.
   *
   * @return		true if the flow is being executed
   */
  public boolean isRunning() {
    return (m_FlowSetupWorker != null);
  }

  /**
   * Pauses the execution.
   */
  public void pauseExecution() {
    if (m_FlowSetupWorker != null)
      m_FlowSetupWorker.pauseExecution();
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  public boolean isPaused() {
    if (m_FlowSetupWorker != null)
      return m_FlowSetupWorker.isPaused();
    else
      return false;
  }

  /**
   * Resumes the execution.
   */
  public void resumeExecution() {
    if (m_FlowSetupWorker != null)
      m_FlowSetupWorker.resumeExecution();
  }

  /**
   * Executes the flow, if not already running. Does not wait for the
   * execution to finish.
   *
   * @return		true if successfully started
   */
  public boolean execute() {
    return execute(false);
  }

  /**
   * Executes the flow, if not already running.
   *
   * @param wait	whether to wait for the execution to finish
   * @return		true if successfully started
   */
  public boolean execute(boolean wait) {
    if (m_FlowSetupWorker != null) {
      setLastError("Is the flow still running?");
      return false;
    }

    showStatus("");
    cleanUp();

    // a few sanity checks
    if (!m_File.exists()) {
      setLastError("Flow file '" + m_File + "' does not exist!");
      return false;
    }
    if (m_File.isDirectory()) {
      setLastError("Flow file '" + m_File + "' points to a directory!");
      return false;
    }

    showStatus("Running: " + m_Name + " (" + m_File + ")");
    m_LastActor       = null;
    m_FlowSetupWorker = new FlowSetupWorker(this);
    m_FlowSetupWorker.execute();

    if (wait) {
      while (isRunning()) {
	try {
	  wait(200);
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return true;
  }

  /**
   * Stops the flow, if running.
   */
  public void stopExecution() {
    if (m_FlowSetupWorker != null)
      m_FlowSetupWorker.stopExecution();
  }

  /**
   * Called when the worker thread finished.
   */
  public void finish() {
    m_LastActor       = m_FlowSetupWorker.getLastActor();
    m_FlowSetupWorker.cleanUp();
    m_FlowSetupWorker = null;
  }

  /**
   * Performs a clean up of a previously run actor.
   */
  public void cleanUp() {
    if (m_LastActor != null) {
      m_LastActor.cleanUp();
      m_LastActor = null;
    }
  }

  /**
   * Adds the listener to the internal list.
   *
   * @param l		the listener to add
   */
  public void addFlowSetupStateChangeListener(FlowSetupStateListener l) {
    getFlowSetupStateChangeListeners().add(l);
  }

  /**
   * Removes the listener to the internal list.
   *
   * @param l		the listener to remove
   */
  public void removeFlowSetupStateChangeListener(FlowSetupStateListener l) {
    getFlowSetupStateChangeListeners().remove(l);
  }

  /**
   * Notifies all listeners.
   *
   * @param e		the event to send to all listeners
   */
  public void notifyFlowSetupStateChangeListeners(FlowSetupStateEvent e) {
    Iterator<FlowSetupStateListener>	iter;

    iter = getFlowSetupStateChangeListeners().iterator();
    while (iter.hasNext())
      iter.next().flowSetupStateChanged(e);
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <p/>
   * Only compares the commandlines of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
  }

  /**
   * Returns whether the two objects are the same.
   * <p/>
   * Only compares the commandlines of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Returns a shallow copy of itself.
   *
   * @return		the shallow copy
   */
  public FlowSetup shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public FlowSetup shallowCopy(boolean expand) {
    return (FlowSetup) OptionUtils.shallowCopy(this, expand);
  }
}
