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
 * ExecuteActor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.Variables;
import adams.flow.container.EncapsulatedActorsContainer;
import adams.flow.control.Storage;
import adams.flow.control.StorageName;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.execution.FlowExecutionListener;
import adams.flow.execution.FlowExecutionListeningSupporter;
import adams.flow.execution.NullListener;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Executes the actor passing through and forwards it once finished.<br>
 * If the actor is an instance of adams.flow.execution.FlowExecutionListeningSupporter and flow execution listening enabled, then the specified flow execution listener gets attached.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ExecuteActor
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-call-setup &lt;boolean&gt; (property: callSetUp)
 * &nbsp;&nbsp;&nbsp;If enabled, the actor's 'setUp()' method gets called.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-call-wrapup &lt;boolean&gt; (property: callWrapUp)
 * &nbsp;&nbsp;&nbsp;If enabled, the actor's 'wrapUp()' method gets called.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-call-cleanup &lt;boolean&gt; (property: callCleanUp)
 * &nbsp;&nbsp;&nbsp;If enabled, the actor's 'cleanUp()' method gets called.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-flow-execution-listening-enabled &lt;boolean&gt; (property: flowExecutionListeningEnabled)
 * &nbsp;&nbsp;&nbsp;Enables&#47;disables the flow execution listener.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-flow-execution-listener &lt;adams.flow.execution.FlowExecutionListener&gt; (property: flowExecutionListener)
 * &nbsp;&nbsp;&nbsp;The listener for the flow execution; must be enabled explicitly.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.execution.NullListener
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ExecuteActor
  extends AbstractTransformer
  implements FlowExecutionListeningSupporter {

  private static final long serialVersionUID = 1877006726746922569L;

  /** whether to call the setUp method. */
  protected boolean m_CallSetUp;

  /** whether to call the wrapUp method. */
  protected boolean m_CallWrapUp;

  /** whether to call the cleanUp method. */
  protected boolean m_CallCleanUp;

  /** the current actor being executed. */
  protected transient Actor m_Actor;

  /** whether flow execution listening is enabled. */
  protected boolean m_FlowExecutionListeningEnabled;

  /** the execution listener to use. */
  protected FlowExecutionListener m_FlowExecutionListener;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes the actor passing through and forwards it once finished.\n"
      + "If the actor is an instance of " + Utils.classToString(FlowExecutionListeningSupporter.class) + " and "
      + "flow execution listening enabled, then the specified flow execution listener "
      + "gets attached.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "call-setup", "callSetUp",
      false);

    m_OptionManager.add(
      "call-wrapup", "callWrapUp",
      false);

    m_OptionManager.add(
      "call-cleanup", "callCleanUp",
      false);

    m_OptionManager.add(
      "flow-execution-listening-enabled", "flowExecutionListeningEnabled",
      false);

    m_OptionManager.add(
      "flow-execution-listener", "flowExecutionListener",
      new NullListener());
  }

  /**
   * Sets whether to call the actor's setUp method.
   *
   * @param value	true if to call
   */
  public void setCallSetUp(boolean value) {
    m_CallSetUp = value;
    reset();
  }

  /**
   * Returns whether to call the actor's setUp method.
   *
   * @return		true if to call
   */
  public boolean getCallSetUp() {
    return m_CallSetUp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String callSetUpTipText() {
    return "If enabled, the actor's 'setUp()' method gets called.";
  }

  /**
   * Sets whether to call the actor's wrapUp method.
   *
   * @param value	true if to call
   */
  public void setCallWrapUp(boolean value) {
    m_CallWrapUp = value;
    reset();
  }

  /**
   * Returns whether to call the actor's wrapUp method.
   *
   * @return		true if to call
   */
  public boolean getCallWrapUp() {
    return m_CallWrapUp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String callWrapUpTipText() {
    return "If enabled, the actor's 'wrapUp()' method gets called.";
  }

  /**
   * Sets whether to call the actor's cleanUp method.
   *
   * @param value	true if to call
   */
  public void setCallCleanUp(boolean value) {
    m_CallCleanUp = value;
    reset();
  }

  /**
   * Returns whether to call the actor's cleanUp method.
   *
   * @return		true if to call
   */
  public boolean getCallCleanUp() {
    return m_CallCleanUp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String callCleanUpTipText() {
    return "If enabled, the actor's 'cleanUp()' method gets called.";
  }

  /**
   * Sets whether flow execution listening is enabled.
   *
   * @param value	true if to enable listening
   */
  public void setFlowExecutionListeningEnabled(boolean value) {
    m_FlowExecutionListeningEnabled = value;
    reset();
  }

  /**
   * Returns whether flow execution listening is enabled.
   *
   * @return		true if listening is enabled
   */
  public boolean isFlowExecutionListeningEnabled() {
    return m_FlowExecutionListeningEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String flowExecutionListeningEnabledTipText() {
    return "Enables/disables the flow execution listener.";
  }

  /**
   * Sets the listener to use.
   *
   * @param l		the listener to use
   */
  public void setFlowExecutionListener(FlowExecutionListener l) {
    m_FlowExecutionListener = l;
    reset();
  }

  /**
   * Returns the current listener in use.
   *
   * @return		the listener
   */
  public FlowExecutionListener getFlowExecutionListener() {
    return m_FlowExecutionListener;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String flowExecutionListenerTipText() {
    return "The listener for the flow execution; must be enabled explicitly.";
  }

  /**
   * Returns whether listeners can be attached at runtime.
   *
   * @return		true if listeners can be attached dynamically
   */
  public boolean canStartListeningAtRuntime() {
    return false;
  }

  /**
   * Attaches the listener and starts listening.
   *
   * @param l		the listener to attach and use immediately
   * @return		true if listening could be started successfully
   */
  public boolean startListeningAtRuntime(FlowExecutionListener l) {
    return false;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String> 	options;

    result = "";
    if (m_FlowExecutionListeningEnabled || QuickInfoHelper.hasVariable(this, "executionListener"))
      result = QuickInfoHelper.toString(this, "executionListener", m_FlowExecutionListener, "listener: ");

    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "callSetUp",   m_CallSetUp,   "call setUp"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "callWrapUp",  m_CallWrapUp,  "call wrapUp"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "callCleanUp", m_CallCleanUp, "call cleanUp"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Actor.class, EncapsulatedActorsContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Actor.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    EncapsulatedActorsContainer	cont;
    Storage			storage;
    Variables			variables;

    result    = null;
    cont      = null;
    storage   = null;
    variables = null;
    if (m_InputToken.hasPayload(Actor.class)) {
      m_Actor = m_InputToken.getPayload(Actor.class);
    }
    else if (m_InputToken.hasPayload(EncapsulatedActorsContainer.class)) {
      cont      = m_InputToken.getPayload(EncapsulatedActorsContainer.class);
      m_Actor   = cont.getValue(EncapsulatedActorsContainer.VALUE_ACTOR, Actor.class);
      storage   = cont.getValue(EncapsulatedActorsContainer.VALUE_STORAGE, Storage.class);
      variables = cont.getValue(EncapsulatedActorsContainer.VALUE_VARIABLES, Variables.class);
    }
    else {
      result = m_InputToken.unhandledData();
    }

    if (m_Actor == null)
      result = "No actor available!";

    if (result == null) {
      // attach listener?
      if (m_FlowExecutionListeningEnabled && (m_Actor instanceof FlowExecutionListeningSupporter)) {
	((FlowExecutionListeningSupporter) m_Actor).setFlowExecutionListener(ObjectCopyHelper.copyObject(m_FlowExecutionListener));
	((FlowExecutionListeningSupporter) m_Actor).setFlowExecutionListeningEnabled(true);
      }

      try {
        // init actor
	if (m_CallSetUp)
	  result = m_Actor.setUp();

        // execute actor
	if (result == null) {
	  if (variables != null)
	    m_Actor.getVariables().assign(variables);
	  if (storage != null)
	    m_Actor.getStorageHandler().getStorage().assign(storage);
	  result = m_Actor.execute();
	}

	// finish up
	if (m_CallWrapUp)
	  m_Actor.wrapUp();
	if (m_CallCleanUp)
	  m_Actor.cleanUp();

	// generate output
	if (result == null) {
	  if (cont == null) {
	    m_OutputToken = new Token(m_Actor);
	  }
	  else {
	    if (cont.hasValue(EncapsulatedActorsContainer.VALUE_OUTPUTNAME))
	      cont.setValue(
	        EncapsulatedActorsContainer.VALUE_OUTPUT,
		m_Actor.getStorageHandler().getStorage().get(cont.getValue(EncapsulatedActorsContainer.VALUE_OUTPUTNAME, StorageName.class)));
	    m_OutputToken = new Token(cont);
	  }
	}
      }
      catch (Exception e) {
	result = handleException("Failed to execute actor!", e);
      }
    }

    m_Actor = null;

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Actor != null)
      m_Actor.stopExecution();
    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_Actor = null;
    super.wrapUp();
  }
}
