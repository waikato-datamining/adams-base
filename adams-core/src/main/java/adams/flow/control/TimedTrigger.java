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
 * TimedTrigger.java
 * Copyright (C) 2014-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.container.TimingContainer;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Compatibility;
import adams.flow.core.InputConsumer;
import adams.flow.core.TimedActor;
import adams.flow.core.Token;
import org.apache.commons.lang.time.StopWatch;

import java.util.HashSet;
import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Executes the subs-actors whenever a token gets passed through. In contrast to the adams.flow.control.Tee actor, it doesn't feed the sub-actors with the current token.<br>
 * Times how long the sub-flow execution takes and sends the time in milli-seconds (as double) in a adams.flow.container.TimingContainer container to the specified callable actor.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: TimedTrigger
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
 * <pre>-finish-before-stopping &lt;boolean&gt; (property: finishBeforeStopping)
 * &nbsp;&nbsp;&nbsp;If enabled, actor first finishes processing all data before stopping.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-asynchronous &lt;boolean&gt; (property: asynchronous)
 * &nbsp;&nbsp;&nbsp;If enabled, the sub-actors get executed asynchronously rather than the flow 
 * &nbsp;&nbsp;&nbsp;waiting for them to finish before proceeding with execution.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-tee &lt;adams.flow.core.Actor&gt; [-tee ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actors to siphon-off the tokens to.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-timing-enabled &lt;boolean&gt; (property: timingEnabled)
 * &nbsp;&nbsp;&nbsp;If enabled, then the actors performs timing on its execution.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix to store in the timing container; automatically expands variables.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-callable &lt;adams.flow.core.CallableActorReference&gt; (property: callableName)
 * &nbsp;&nbsp;&nbsp;The name of the callable actor to use.
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 * 
 * <pre>-optional &lt;boolean&gt; (property: optional)
 * &nbsp;&nbsp;&nbsp;If enabled, then the callable actor is optional, ie no error is raised if 
 * &nbsp;&nbsp;&nbsp;not found, merely ignored.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class TimedTrigger
  extends Trigger
  implements TimedActor {

  /** for serialization. */
  private static final long serialVersionUID = -7390723853001469503L;

  /** the key for backing up the callable actor. */
  public final static String BACKUP_CALLABLEACTOR = "callable actor";

  /** the key for backing up the configured state. */
  public final static String BACKUP_CONFIGURED = "configured";

  /** whether timing is enabled. */
  protected boolean m_TimingEnabled;

  /** the prefix to use. */
  protected String m_Prefix;

  /** the callable name. */
  protected CallableActorReference m_CallableName;

  /** the callable actor. */
  protected Actor m_CallableActor;

  /** whether the callable actor has been configured. */
  protected boolean m_Configured;
  
  /** the helper class. */
  protected CallableActorHelper m_Helper;
  
  /** whether the callable actor is optional. */
  protected boolean m_Optional;
  
  /** the stopwatch for timing. */
  protected transient StopWatch m_StopWatch;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	super.globalInfo() + "\n"
	  + "Times how long the sub-flow execution takes and sends the time "
	  + "in milli-seconds (as double) in a " + TimingContainer.class.getName()
	  + " container to the specified callable actor.";
  }

  /**
   * Returns the class that is the corresponding conditional equivalent.
   * 
   * @return		the class, null if none available
   */
  @Override
  public Class getConditionalEquivalent() {
    return null;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "timing-enabled", "timingEnabled",
      true);

    m_OptionManager.add(
      "prefix", "prefix",
      "");

    m_OptionManager.add(
      "callable", "callableName",
      new CallableActorReference(CallableActorReference.UNKNOWN));

    m_OptionManager.add(
      "optional", "optional",
      false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_CallableActor = null;
    m_Configured    = false;
    m_StopWatch     = null;
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
   * Sets whether to perform timing on its execution.
   *
   * @param value 	true if timing enabled
   */
  @Override
  public void setTimingEnabled(boolean value) {
    m_TimingEnabled = value;
    reset();
  }

  /**
   * Returns whether to perform timing on its execution.
   *
   * @return 		true if timing enabled
   */
  @Override
  public boolean getTimingEnabled() {
    return m_TimingEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String timingEnabledTipText() {
    return "If enabled, then the actors performs timing on its execution.";
  }

  /**
   * Sets the prefix to store in the timing container.
   *
   * @param value 	the prefix
   */
  @Override
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix to store in the timing container.
   *
   * @return 		the prefix
   */
  @Override
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String prefixTipText() {
    return "The prefix to store in the timing container; automatically expands variables.";
  }

  /**
   * Sets the name of the callable actor to use.
   *
   * @param value 	the callable name
   */
  @Override
  public void setCallableName(CallableActorReference value) {
    m_CallableName = value;
    reset();
  }

  /**
   * Returns the name of the callable actor in use.
   *
   * @return 		the callable name
   */
  @Override
  public CallableActorReference getCallableName() {
    return m_CallableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String callableNameTipText() {
    return "The name of the callable actor to use.";
  }

  /**
   * Sets whether the callable actor is optional.
   *
   * @param value 	true if optional
   */
  @Override
  public void setOptional(boolean value) {
    m_Optional = value;
    reset();
  }

  /**
   * Returns whether the callable actor is optional.
   *
   * @return 		true if optional
   */
  @Override
  public boolean getOptional() {
    return m_Optional;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String optionalTipText() {
    return 
	"If enabled, then the callable actor is optional, ie no error is "
	+ "raised if not found, merely ignored.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "callableName", m_CallableName);
    result += QuickInfoHelper.toString(this, "prefix", (m_Prefix.isEmpty() ? "-none-" : m_Prefix), ", prefix: ");
    result += QuickInfoHelper.toString(this, "optional", m_Optional, "optional", ", ");
    result += QuickInfoHelper.toString(this, "timingEnabled", m_TimingEnabled, "enabled", ", ");

    return result;
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected Actor findCallableActor() {
    return m_Helper.findCallableActorRecursive(this, getCallableName());
  }

  /**
   * Checks whether a reference to the callable actor is currently available.
   *
   * @return		true if a reference is available
   * @see		#getCallableActor()
   */
  @Override
  public boolean hasCallableActor() {
    return (m_CallableActor != null);
  }

  /**
   * Returns the currently set callable actor.
   *
   * @return		the actor, can be null
   */
  @Override
  public Actor getCallableActor() {
    return m_CallableActor;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    pruneBackup(BACKUP_CALLABLEACTOR);
    pruneBackup(BACKUP_CONFIGURED);
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

    if (m_CallableActor != null)
      result.put(BACKUP_CALLABLEACTOR, m_CallableActor);
    
    result.put(BACKUP_CONFIGURED, m_Configured);

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

    if (state.containsKey(BACKUP_CALLABLEACTOR)) {
      m_CallableActor = (Actor) state.get(BACKUP_CALLABLEACTOR);
      state.remove(BACKUP_CALLABLEACTOR);
    }

    if (state.containsKey(BACKUP_CONFIGURED)) {
      m_Configured = (Boolean) state.get(BACKUP_CONFIGURED);
      state.remove(BACKUP_CONFIGURED);
    }
  }

  /**
   * Configures the callable actor.
   *
   * @return		null if successful, otherwise error message
   */
  protected String setUpCallableActor() {
    String		result;
    HashSet<String>	variables;
    Compatibility	comp;

    result = null;

    m_Configured = true;
    if (!m_TimingEnabled)
      return result;
    
    m_CallableActor = findCallableActor();
    if (m_CallableActor == null) {
      if (!m_Optional)
	result = "Couldn't find callable actor '" + getCallableName() + "'!";
      else
	getLogger().info("Callable actor '" + getCallableName() + "' not found, ignoring.");
    }
    else {
      if (!(m_CallableActor instanceof InputConsumer)) {
	result = "Callable actor '" + getCallableName() + "' does not accept input!";
      }
      else  {
	comp = new Compatibility();
	if (!comp.isCompatible(new Class[]{Double.class}, ((InputConsumer) m_CallableActor).accepts()))
	  result = "Callable actor '" + getCallableName() + "' does not accept " + Double.class.getName() 
	  	+ " (" + Utils.classesToString(((InputConsumer) m_CallableActor).accepts()) + ")!";
      }
      if (result == null) {
	variables = findVariables(m_CallableActor);
	m_DetectedVariables.addAll(variables);
	if (m_DetectedVariables.size() > 0)
	  getVariables().addVariableChangeListener(this);
	if (getErrorHandler() != this)
	  ActorUtils.updateErrorHandler(m_CallableActor, getErrorHandler(), isLoggingEnabled());
      }
    }

    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    String	variable;

    result = super.setUp();

    if (result == null) {
      // do we have to wait till execution time because of attached variable?
      variable = getOptionManager().getVariableForProperty("callableName");
      if (variable == null)
	result = setUpCallableActor();
    }

    return result;
  }

  /**
   * Executes the callable actor. Derived classes might need to override this
   * method to ensure atomicity.
   *
   * @param msec	the time in milli-seconds
   * @return		null if no error, otherwise error message
   */
  protected String executeCallableActor(double msec) {
    String		result;
    TimingContainer 	cont;

    cont = new TimingContainer(msec, getVariables().expand(m_Prefix), getFullName());
    ((InputConsumer) m_CallableActor).input(new Token(cont));
    result = m_CallableActor.execute();

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    if (m_TimingEnabled) {
      if (m_StopWatch == null)
	m_StopWatch = new StopWatch();
    }

    // time execution
    if (m_TimingEnabled) {
      m_StopWatch.reset();
      m_StopWatch.start();
    }

    result = super.doExecute();

    if (m_TimingEnabled) {
      m_StopWatch.stop();
    }
    
    // is variable attached?
    if (!m_Configured)
      result = setUpCallableActor();

    if (result == null) {
      if (m_TimingEnabled) {
	if (m_CallableActor != null) {
	  if (!m_CallableActor.getSkip() && !m_CallableActor.isStopped()) {
	    synchronized(m_CallableActor) {
	      if (isLoggingEnabled())
		getLogger().info("Executing callable actor - start: " + m_CallableActor);
	      result = executeCallableActor(m_StopWatch.getTime());
	      if (isLoggingEnabled())
		getLogger().info("Executing callable actor - end: " + result);
	    }
	  }
	}
      }
    }

    return result;
  }

  /**
   * Returns whether the actor has finished.
   *
   * @return		true if finished
   */
  @Override
  public boolean isFinished() {
    if (m_CallableActor == null)
      return true;
    else
      return m_CallableActor.isFinished();
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    try {
      if (m_CallableActor != null) {
	m_CallableActor.notifyAll();
	m_CallableActor.stopExecution();
      }
    }
    catch (Exception e) {
      // ignored
    }

    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    if (m_CallableActor != null) {
      synchronized(m_CallableActor) {
	m_CallableActor.wrapUp();
      }
    }

    super.wrapUp();
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    if (m_CallableActor != null) {
      m_CallableActor.cleanUp();
      m_CallableActor = null;
    }
  }
}
