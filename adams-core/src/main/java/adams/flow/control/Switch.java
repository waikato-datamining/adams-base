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
 * Switch.java
 * Copyright (C) 2010-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.LenientModeSupporter;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.IndexedBooleanCondition;
import adams.flow.condition.bool.IndexedBooleanConditionSupporter;
import adams.flow.condition.bool.True;
import adams.flow.core.Actor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.InputConsumer;
import adams.flow.core.MutableActorHandler;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.sink.Null;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Emulates a Switch control statement. The first 'condition' that evaluates to 'true' executes the corresponding 'case' actor and stops evaluation of conditions.<br>
 * A catch-all or default can be set up as well by having one more 'case' than 'conditions' (the last case acts as default).<br>
 * If any output is generated then this gets recorded and forwarded in the flow.<br>
 * Normally, the number of conditions and number of switch branches have to be the same. A special case are adams.flow.condition.bool.IndexedBooleanCondition conditions. They can be used alone, as they determine which switch branch to use.
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
 * &nbsp;&nbsp;&nbsp;default: Switch
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
 * <pre>-stopping-timeout &lt;int&gt; (property: stoppingTimeout)
 * &nbsp;&nbsp;&nbsp;The timeout in milliseconds when waiting for actors to finish (&lt;= 0 for
 * &nbsp;&nbsp;&nbsp;infinity; see 'finishBeforeStopping').
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-condition &lt;adams.flow.condition.bool.BooleanCondition&gt; [-condition ...] (property: conditions)
 * &nbsp;&nbsp;&nbsp;The switch conditions to evaluate - the first condition that evaluates to
 * &nbsp;&nbsp;&nbsp;'true' triggers the execution of the corresponding 'case' actor.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.condition.bool.True
 * </pre>
 *
 * <pre>-case &lt;adams.flow.core.Actor&gt; [-case ...] (property: cases)
 * &nbsp;&nbsp;&nbsp;The 'cases' - one of them gets executed if the corresponding 'condition'
 * &nbsp;&nbsp;&nbsp;evaluates to 'true'.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.Null
 * </pre>
 *
 * <pre>-lenient &lt;boolean&gt; (property: lenient)
 * &nbsp;&nbsp;&nbsp;If enabled, the token passing through is not required to have a payload
 * &nbsp;&nbsp;&nbsp;(eg generated by the Start source).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Switch
  extends AbstractDirectedControlActor
  implements InputConsumer, OutputProducer, MutableActorHandler, 
             IndexedBooleanConditionSupporter, LenientModeSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 185561131623293880L;

  /**
   * A specialized director for the Switch control actor.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static class SwitchDirector
    extends AbstractDirector {

    /** for serialization. */
    private static final long serialVersionUID = 8414511259688024553L;

    /** the token to use in the switch cases. */
    protected transient Token m_CaseToken;

    /**
     * Sets the group to execute.
     *
     * @param value 	the group
     */
    public void setControlActor(AbstractDirectedControlActor value) {
      if ((value instanceof Switch) || (value == null))
	super.setControlActor(value);
      else
	System.err.println(
	    "Handler must be a Switch actor (provided: " + value.getClass().getName() + ")!");
    }

    /**
     * Sets the token to use in the switch cases.
     *
     * @param value	the token to use
     */
    public void setCaseToken(Token value) {
      m_CaseToken = value;
    }

    /**
     * Returns the token to be forwarded to switch cases.
     *
     * @return		the token, can be null if not yet set
     */
    public Token getCaseToken() {
      return m_CaseToken;
    }

    /**
     * Determines which case to execute.
     *
     * @return		the index of the case to execute
     */
    protected int whichCase() {
      int	result;
      int	i;
      Switch	switchActor;
      boolean	indexed;

      result = -1;
      if (m_CaseToken == null)
	return result;

      switchActor = (Switch) m_ControlActor;
      if (!switchActor.getLenient() && (m_CaseToken.getPayload() == null))
	return result;

      indexed = (switchActor.getConditions().length == 1) && (switchActor.getConditions()[0] instanceof IndexedBooleanCondition);

      if (indexed) {
	result = ((IndexedBooleanCondition) switchActor.getConditions()[0]).getCaseIndex(switchActor, m_CaseToken);
      }
      else {
	for (i = 0; i < switchActor.getConditions().length; i++) {
	  try {
	    if (switchActor.getConditions()[i].evaluate(switchActor, m_CaseToken)) {
	      result = i;
	      break;
	    }
	  }
	  catch (Throwable t) {
	    handleException("Error evaluating boolean condition: " + switchActor.getConditions()[i], t);
	  }
	}
      }

      // default case?
      if (result == -1) {
	if (indexed) {
	  result = ((IndexedBooleanCondition) switchActor.getConditions()[0]).getDefaultCaseIndex(switchActor, m_CaseToken);
	  if (result >= switchActor.getCases().length)
	    result = -1;
	}
	else if (switchActor.getCases().length > switchActor.getConditions().length) {
	  result = switchActor.getCases().length - 1;
	}
      }

      return result;
    }

    /**
     * Executes the group of actors.
     *
     * @return		null if everything went smooth
     */
    @Override
    public String execute() {
      String	result;
      Actor 	caseActor;
      int	index;

      result = null;

      index = whichCase();
      if (index == -1) {
	result = "No matching case found for token: " + m_CaseToken;
      }
      else {
	caseActor = ((Switch) m_ControlActor).get(index);
	try {
	  // input
	  if (caseActor instanceof InputConsumer) {
	    if (caseActor.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	      caseActor.getFlowExecutionListeningSupporter().getFlowExecutionListener().preInput(caseActor, m_CaseToken);
	    ((InputConsumer) caseActor).input(m_CaseToken);
	    if (caseActor.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	      caseActor.getFlowExecutionListeningSupporter().getFlowExecutionListener().postInput(caseActor);
	  }
	  // execute
	  if (caseActor.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	    caseActor.getFlowExecutionListeningSupporter().getFlowExecutionListener().preExecute(caseActor);
	  result = caseActor.execute();
	  if (caseActor.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	    caseActor.getFlowExecutionListeningSupporter().getFlowExecutionListener().postExecute(caseActor);
	  // record output (if available)
	  if (caseActor instanceof OutputProducer) {
	    while (((OutputProducer) caseActor).hasPendingOutput()) {
	      ((Switch) m_ControlActor).recordOutput(((OutputProducer) caseActor).output());
	    }
	  }
	}
	catch (Throwable t) {
	  result = handleException(caseActor.getFullName() + " generated the following exception: ", t);
	}

	if (result != null)
	  result = caseActor.getErrorHandler().handleError(caseActor, "execute", result);
      }

      m_CaseToken = null;

      return result;
    }

    /**
     * Cleans up data structures, frees up memory.
     */
    @Override
    public void cleanUp() {
      m_CaseToken = null;

      super.cleanUp();
    }
  }

  /** the key for storing the current input token in the backup. */
  public final static String BACKUP_INPUT = "input";

  /** the "conditions" for the various switch cases. */
  protected BooleanCondition[] m_Conditions;

  /** the "cases" to execute if the corresponding expression matches. */
  protected List<Actor> m_Cases;

  /** whether we operate in lenient mode, ie not requiring a payload. */
  protected boolean m_Lenient;

  /** the input token. */
  protected transient Token m_InputToken;

  /** the generated tokens. */
  protected List m_Queue;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Emulates a Switch control statement. The first 'condition' that evaluates to 'true' "
	+ "executes the corresponding 'case' actor and stops evaluation of conditions.\n"
	+ "A catch-all or default can be set up as well by having one more 'case' than "
	+ "'conditions' (the last case acts as default).\n"
	+ "If any output is generated then this gets recorded and forwarded in the flow.\n"
	+ "Normally, the number of conditions and number of switch branches have to be the same. "
	+ "A special case are " + Utils.classToString(IndexedBooleanCondition.class) +  " conditions. "
	+ "They can be used alone, as they determine which switch branch to use.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "condition", "conditions",
      new BooleanCondition[]{new True()});

    m_OptionManager.add(
      "case", "cases",
      new Actor[]{new Null()});

    m_OptionManager.add(
      "lenient", "lenient",
      false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_InputToken = null;
    m_Cases      = new ArrayList<>();
    m_Queue      = new ArrayList();
  }
  
  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Queue.clear();
  }

  /**
   * Returns an instance of a director.
   *
   * @return		the director
   */
  @Override
  protected AbstractDirector newDirector() {
    return new SwitchDirector();
  }

  /**
   * Sets the conditions to evaluate.
   *
   * @param value	the conditions
   */
  public void setConditions(BooleanCondition[] value) {
    int		i;
    
    // check for IndexedBooleanCondition
    if (value.length > 1) {
      for (i = 0; i < value.length; i++) {
	if (value[i] instanceof IndexedBooleanCondition) {
	  getLogger().severe("When using " + IndexedBooleanCondition.class.getName() + " conditions, only a single one is allowed!");
	  return;
	}
      }
    }
    
    m_Conditions = value;
    reset();
  }

  /**
   * Returns the conditions to evaluate.
   *
   * @return		the conditions
   */
  public BooleanCondition[] getConditions() {
    return m_Conditions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String conditionsTipText() {
    return
        "The switch conditions to evaluate - the first condition that "
      + "evaluates to 'true' triggers the execution of the corresponding "
      + "'case' actor.";
  }

  /**
   * Sets the cases.
   *
   * @param value 	the cases
   */
  public void setCases(Actor[] value) {
    int		i;

    ActorUtils.uniqueNames(value);

    m_Cases.clear();
    for (i = 0; i < value.length; i++)
      m_Cases.add(value[i]);

    updateParent();
    reset();
  }

  /**
   * Returns the cases.
   *
   * @return 		the cases
   */
  public Actor[] getCases() {
    return m_Cases.toArray(new Actor[m_Cases.size()]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String casesTipText() {
    return
        "The 'cases' - one of them gets executed if the corresponding "
      + "'condition' evaluates to 'true'.";
  }

  /**
   * Sets whether in lenient mode, ie not requiring a payload in the tokens.
   *
   * @param value 	true if lenient
   */
  public void setLenient(boolean value) {
    m_Lenient = value;
    reset();
  }

  /**
   * Returns whether in lenient mode, ie not requiring a payload in the tokens.
   *
   * @return 		true if lenient
   */
  public boolean getLenient() {
    return m_Lenient;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lenientTipText() {
    return
        "If enabled, the token passing through is not required to have a "
	  + "payload (eg generated by the Start source).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = m_Conditions.length + " condition";
    if (m_Conditions.length != 1)
	result += "s";
    result = QuickInfoHelper.toString(this, "conditions", result);
    result += QuickInfoHelper.toString(this, "lenient", m_Lenient, "lenient", ", ");

    if (super.getQuickInfo() != null)
      result += ", " + super.getQuickInfo();
    
    return result;
  }

  /**
   * Returns the size of the group.
   *
   * @return		the number of cases
   */
  @Override
  public int size() {
    return m_Cases.size();
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  @Override
  public Actor get(int index) {
    return m_Cases.get(index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   * @return		null if successful, otherwise error message
   */
  @Override
  public String set(int index, Actor actor) {
    String	result;

    result = null;

    if ((index > -1) && (index < m_Cases.size())) {
      ActorUtils.uniqueName(actor, this, index);
      m_Cases.set(index, actor);
      m_Cases.get(index).setParent(null);
      m_Cases.get(index).setParent(this);
      reset();
    }
    else {
      result = "Index out of range (0-" + (m_Cases.size() - 1) + "): " + index;
      getLogger().severe(result);
    }

    return result;
  }

  /**
   * Inserts the actor at the end.
   *
   * @param actor	the actor to insert
   * @return		null if successful, otherwise error message
   */
  public String add(Actor actor) {
    return add(size(), actor);
  }

  /**
   * Inserts the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to insert
   * @return		null if successful, otherwise error message
   */
  public String add(int index, Actor actor) {
    if (actor == this)
      throw new IllegalArgumentException("Cannot add itself!");
    m_Cases.add(index, actor);
    m_Cases.get(index).setParent(null);
    m_Cases.get(index).setParent(this);
    reset();
    return null;
  }

  /**
   * Removes the actor at the given position and returns the removed object.
   *
   * @param index	the position
   * @return		the removed actor
   */
  public Actor remove(int index) {
    Actor	result;

    result = m_Cases.remove(index);
    reset();

    return result;
  }

  /**
   * Removes all actors.
   */
  public void removeAll() {
    m_Cases.clear();
    reset();
  }

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  @Override
  public int indexOf(String actor) {
    int		result;
    int		i;

    result = -1;

    for (i = 0; i < m_Cases.size(); i++) {
      if (m_Cases.get(i).getName().equals(actor)) {
	result = i;
	break;
      }
    }

    return result;
  }

  /**
   * Returns whether the condition is supported.
   * 
   * @return		true if supported, false otherwise
   */
  @Override
  public boolean supports(BooleanCondition condition) {
    return (condition instanceof IndexedBooleanCondition);
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo()
      .allowStandalones(false)
      .actorExecution(ActorExecution.PARALLEL)
      .forwardsInput(true);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    HashSet<Class>	result;

    result = new HashSet<Class>();

    for (Actor actor: m_Cases) {
      if (actor instanceof InputConsumer)
	result.addAll(Arrays.asList(((InputConsumer) actor).accepts()));
    }
    if (result.size() == 0)
      result.add(Unknown.class);

    return result.toArray(new Class[result.size()]);
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

    if (m_InputToken != null)
      result.put(BACKUP_INPUT, m_InputToken);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_INPUT)) {
      m_InputToken = (Token) state.get(BACKUP_INPUT);
      ((SwitchDirector) m_Director).setCaseToken(m_InputToken);
      state.remove(BACKUP_INPUT);
    }

    super.restoreState(state);
  }

  /**
   * Initializes the sub-actors for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    int		i;

    result = super.setUp();

    if (result == null) {
      if ((m_Conditions == null) || (m_Conditions.length == 0)) {
	result = "No condition provided!";
      }
      else if (m_Cases.size() > m_Conditions.length + 1) {
	if ((m_Conditions.length == 1) && (m_Conditions[0] instanceof IndexedBooleanCondition)) {
	  // we presume it's fine
	}
	else {
	  result =   "Only 1 default case can be defined: "
	      + m_Conditions.length + " condition(s) but " + m_Cases.size() + " case(s)";
	}
      }
      else if (m_Cases.size() < m_Conditions.length) {
	result = "Not enough cases defined: " + m_Conditions.length + " required";
      }
    }
    
    if (result == null) {
      for (i = 0; i < m_Conditions.length; i++) {
	result = m_Conditions[i].setUp(this);
	if (result != null) {
	  result = "Condition #" + (i+1) + ": " + result;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   */
  public void input(Token token) {
    m_InputToken = token;
    m_Queue.clear();
    ((SwitchDirector) m_Director).setCaseToken(m_InputToken);
  }

  /**
   * Returns whether an input token is currently present.
   *
   * @return		true if input token present
   */
  public boolean hasInput() {
    return (m_InputToken != null);
  }

  /**
   * Returns the current input token, if any.
   *
   * @return		the input token, null if none present
   */
  public Token currentInput() {
    return m_InputToken;
  }

  /**
   * Post-execute hook.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String postExecute() {
    String	result;

    result = super.postExecute();

    m_InputToken = null;

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }
  
  /**
   * Records the output generated by a case actor.
   * 
   * @param token		the token to record
   */
  protected void recordOutput(Token token) {
    if (m_Lenient || (!m_Lenient && (token.getPayload() != null)))
      m_Queue.add(token.getPayload());
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Queue.size() > 0);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    return new Token(m_Queue.remove(0));
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    m_InputToken = null;

    super.cleanUp();
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    for (BooleanCondition cond: m_Conditions)
      cond.stopExecution();
    super.stopExecution();
  }
}
