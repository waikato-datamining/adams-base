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
 * InputOutputListener.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;

import java.util.HashSet;
import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Listens to the input&#47;output tokens of the sub-actors, sending them to callable actors.
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
 * &nbsp;&nbsp;&nbsp;default: InputOutputListener
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
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-finish-before-stopping &lt;boolean&gt; (property: finishBeforeStopping)
 * &nbsp;&nbsp;&nbsp;If enabled, actor first finishes processing all data before stopping.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-actor &lt;adams.flow.core.AbstractActor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;All the actors that define this sequence.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-on-input &lt;boolean&gt; (property: onInput)
 * &nbsp;&nbsp;&nbsp;If enabled, input tokens are forwarded to the specified callable actor.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-input-destination &lt;adams.flow.core.CallableActorReference&gt; (property: inputDestination)
 * &nbsp;&nbsp;&nbsp;The callable actor to send the input tokens to.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-on-output &lt;boolean&gt; (property: onOutput)
 * &nbsp;&nbsp;&nbsp;If enabled, output tokens are forwarded to the specified callable actor.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-destination &lt;adams.flow.core.CallableActorReference&gt; (property: outputDestination)
 * &nbsp;&nbsp;&nbsp;The callable actor to send the output tokens to.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7842 $
 */
public class InputOutputListener
  extends SubProcess {

  /** for serialization. */
  private static final long serialVersionUID = 5975989766824652946L;

  /** the key for backing up the callable actor (input). */
  public final static String BACKUP_CALLABLEINPUT = "callable input";

  /** the key for backing up the callable actor (output). */
  public final static String BACKUP_CALLABLEOUTPUT = "callable output";

  /** the key for backing up the configured state. */
  public final static String BACKUP_CONFIGURED = "configured";

  /** whether to listen to the input tokens. */
  protected boolean m_OnInput;

  /** the callable actor to send the input tokens to. */
  protected CallableActorReference m_InputDestination;

  /** whether to listen to the output tokens. */
  protected boolean m_OnOutput;

  /** the callable actor to send the output tokens to. */
  protected CallableActorReference m_OutputDestination;

  /** the callable actor (input). */
  protected Actor m_CallableInput;

  /** the callable actor (output). */
  protected Actor m_CallableOutput;

  /** whether the callable actor has been configured. */
  protected boolean m_Configured;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Listens to the input/output tokens of the sub-actors, sending them to callable actors.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "on-input", "onInput",
      false);

    m_OptionManager.add(
      "input-destination", "inputDestination",
      new CallableActorReference());

    m_OptionManager.add(
      "on-output", "onOutput",
      false);

    m_OptionManager.add(
      "output-destination", "outputDestination",
      new CallableActorReference());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_CallableInput  = null;
    m_CallableOutput = null;
    m_Configured     = false;
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "onInput", (m_OnInput ? "on" : "off"), "input: ");
    result += QuickInfoHelper.toString(this, "inputDestination", m_InputDestination, ", input-dest: ");
    result += QuickInfoHelper.toString(this, "onOutput", (m_OnOutput ? "on" : "off"), ", output: ");
    result += QuickInfoHelper.toString(this, "outputDestination", m_OutputDestination, ", output-dest: ");

    if (super.getQuickInfo() != null)
      result += ", " + super.getQuickInfo();

    return result;
  }

  /**
   * Sets whether to listen to input tokens.
   *
   * @param value	true if to listen to input
   */
  public void setOnInput(boolean value) {
    m_OnInput = value;
    reset();
  }

  /**
   * Returns whether to listen to input tokens.
   *
   * @return		true if to listen to input
   */
  public boolean getOnInput() {
    return m_OnInput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onInputTipText() {
    return "If enabled, input tokens are forwarded to the specified callable actor.";
  }

  /**
   * Sets the callable actor to send the input tokens to.
   *
   * @param value	the callable actor
   */
  public void setInputDestination(CallableActorReference value) {
    m_InputDestination = value;
    reset();
  }

  /**
   * Returns the callable actor to send the input tokens to.
   *
   * @return		the callable actor
   */
  public CallableActorReference getInputDestination() {
    return m_InputDestination;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputDestinationTipText() {
    return "The callable actor to send the input tokens to.";
  }

  /**
   * Sets whether to listen to output tokens.
   *
   * @param value	true if to listen to output
   */
  public void setOnOutput(boolean value) {
    m_OnOutput = value;
    reset();
  }

  /**
   * Returns whether to listen to output tokens.
   *
   * @return		true if to listen to output
   */
  public boolean getOnOutput() {
    return m_OnOutput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onOutputTipText() {
    return "If enabled, output tokens are forwarded to the specified callable actor.";
  }

  /**
   * Sets the callable actor to send the output tokens to.
   *
   * @param value	the callable actor
   */
  public void setOutputDestination(CallableActorReference value) {
    m_OutputDestination = value;
    reset();
  }

  /**
   * Returns the callable actor to send the output tokens to.
   *
   * @return		the callable actor
   */
  public CallableActorReference getOutputDestination() {
    return m_OutputDestination;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputDestinationTipText() {
    return "The callable actor to send the output tokens to.";
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    pruneBackup(BACKUP_CALLABLEINPUT);
    pruneBackup(BACKUP_CALLABLEOUTPUT);
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

    if (m_CallableInput != null)
      result.put(BACKUP_CALLABLEINPUT, m_CallableInput);

    if (m_CallableOutput != null)
      result.put(BACKUP_CALLABLEOUTPUT, m_CallableOutput);

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

    if (state.containsKey(BACKUP_CALLABLEINPUT)) {
      m_CallableInput = (Actor) state.get(BACKUP_CALLABLEINPUT);
      state.remove(BACKUP_CALLABLEINPUT);
    }

    if (state.containsKey(BACKUP_CALLABLEOUTPUT)) {
      m_CallableOutput = (Actor) state.get(BACKUP_CALLABLEOUTPUT);
      state.remove(BACKUP_CALLABLEOUTPUT);
    }

    if (state.containsKey(BACKUP_CONFIGURED)) {
      m_Configured = (Boolean) state.get(BACKUP_CONFIGURED);
      state.remove(BACKUP_CONFIGURED);
    }
  }

  /**
   * Configures the callable actors.
   *
   * @return		null if OK, otherwise error message
   */
  protected String setUpCallableActors() {
    String		result;
    HashSet<String> 	variables;

    result = null;

    m_CallableInput = m_Helper.findCallableActorRecursive(this, getInputDestination());
    if (m_CallableInput == null) {
      getLogger().warning("Couldn't find callable actor (input destination) '" + getInputDestination() + "'!");
    }
    else {
      if (!(m_CallableInput instanceof InputConsumer)) {
	result = "Callable actor (input destination) '" + getInputDestination() + "' does not accept input!";
      }
      else {
	variables = findVariables(m_CallableInput);
	m_DetectedVariables.addAll(variables);
	if (m_DetectedVariables.size() > 0)
	  getVariables().addVariableChangeListener(this);
	if (getErrorHandler() != this)
	  ActorUtils.updateErrorHandler(m_CallableInput, getErrorHandler(), isLoggingEnabled());
      }
    }

    if (result == null) {
      m_CallableOutput = m_Helper.findCallableActorRecursive(this, getOutputDestination());
      if (m_CallableOutput == null) {
	getLogger().warning("Couldn't find callable actor (output destination) '" + getOutputDestination() + "'!");
      }
      else {
	if (!(m_CallableOutput instanceof InputConsumer)) {
	  result = "Callable actor (output destination) '" + getOutputDestination() + "' does not accept input!";
	}
	else {
	  variables = findVariables(m_CallableOutput);
	  m_DetectedVariables.addAll(variables);
	  if (m_DetectedVariables.size() > 0)
	    getVariables().addVariableChangeListener(this);
	  if (getErrorHandler() != this)
	    ActorUtils.updateErrorHandler(m_CallableOutput, getErrorHandler(), isLoggingEnabled());
	}
      }
    }

    m_Configured = (result == null);

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
      variable = getOptionManager().getVariableForProperty("inputDestination");
      if (variable == null) {
	variable = getOptionManager().getVariableForProperty("outputDestination");
	if (variable == null)
	  setUpCallableActors();
      }
    }

    return result;
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   */
  @Override
  public void input(Token token) {
    String	msg;

    if (m_OnInput) {
      // is variable attached?
      if (!m_Configured) {
	msg = setUpCallableActors();
	if (msg != null)
	  throw new IllegalStateException("Failed to configure callable actor(s):\n" + msg);
      }
      if (m_CallableInput != null) {
	if (!m_CallableInput.getSkip() && !m_CallableInput.isStopped()) {
	  synchronized(m_CallableInput) {
	    if (isLoggingEnabled())
	      getLogger().info("Executing callable actor (input dest) - start: " + m_CallableInput);
	    ((InputConsumer) m_CallableInput).input(token);
	    msg = m_CallableInput.execute();
	    if (isLoggingEnabled())
	      getLogger().info("Executing callable actor (input dest) - end: " + msg);
	  }
	}
      }
    }

    super.input(token);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;
    String	msg;

    result = super.output();

    if (m_OnOutput) {
      // is variable attached?
      if (!m_Configured) {
	msg = setUpCallableActors();
	if (msg != null)
	  throw new IllegalStateException("Failed to configure callable actor(s):\n" + msg);
      }
      if (m_CallableOutput != null) {
	if (!m_CallableOutput.getSkip() && !m_CallableOutput.isStopped()) {
	  synchronized(m_CallableOutput) {
	    if (isLoggingEnabled())
	      getLogger().info("Executing callable actor (output dest) - start: " + m_CallableOutput);
	    ((InputConsumer) m_CallableOutput).input(result);
	    msg = m_CallableOutput.execute();
	    if (isLoggingEnabled())
	      getLogger().info("Executing callable actor (output dest) - end: " + msg);
	  }
	}
      }
    }

    return result;
  }
}
