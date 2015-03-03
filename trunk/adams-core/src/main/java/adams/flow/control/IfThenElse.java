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
 * IfThenElse.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.util.Hashtable;

import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.BooleanConditionSupporter;
import adams.flow.condition.bool.Expression;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.FixedNameActorHandler;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Emulates an If-Then-Else construct. The 'Then' branch gets executed if the 'If' condition evaluates to 'true', otherwise the 'else' branch gets executed.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: IfThenElse
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-condition &lt;adams.flow.condition.AbstractIfThenElseCondition&gt; (property: condition)
 * &nbsp;&nbsp;&nbsp;The condition that determines which branch to execute: if it evaluates to
 * &nbsp;&nbsp;&nbsp;'true' then the 'then' branch gets executed.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.condition.Expression
 * </pre>
 *
 * <pre>-then &lt;adams.flow.core.AbstractActor&gt; (property: thenActor)
 * &nbsp;&nbsp;&nbsp;The actor of the 'then' branch.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.control.Sequence -name then
 * </pre>
 *
 * <pre>-else &lt;adams.flow.core.AbstractActor&gt; (property: elseActor)
 * &nbsp;&nbsp;&nbsp;The actor of the 'else' branch.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.control.Sequence -name else
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class IfThenElse
  extends AbstractDirectedControlActor
  implements InputConsumer, FixedNameActorHandler, BooleanConditionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 185561131623293880L;

  /**
   * A specialized director for an AbstractIfThenElse control actor.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class IfThenElseDirector
    extends AbstractDirector {

    /** for serialization. */
    private static final long serialVersionUID = 8414511259688024553L;

    /** the token to use in the then/else branches. */
    protected transient Token m_BranchToken;

    /**
     * Calls the super implementation of setControlActor.
     *
     * @param value	the control actor to set
     */
    protected void setIfThenElseActor(AbstractDirectedControlActor value) {
      super.setControlActor(value);
    }

    /**
     * Sets the group to execute.
     *
     * @param value 	the group
     */
    public void setControlActor(AbstractDirectedControlActor value) {
      if ((value instanceof IfThenElse) || (value == null))
	setIfThenElseActor(value);
      else
	System.err.println(
	    "Control actor must be a IfThenElse actor (provided: "
	    + ((value != null) ? value.getClass().getName() : "-null-") + ")!");
    }

    /**
     * Sets the token to use in the then/else branches.
     *
     * @param value	the token to use
     */
    public void setBranchToken(Token value) {
      m_BranchToken = value;
    }

    /**
     * Returns the token to be forwarded to then/else branches.
     *
     * @return		the token, can be null if not yet set
     */
    public Token getBranchToken() {
      return m_BranchToken;
    }

    /**
     * Determines whether to execute the 'then' branch.
     *
     * @return		true if the 'then' branch should get executed
     */
    protected boolean doThen() {
      IfThenElse	owner;

      owner = (IfThenElse) getControlActor();
      if (owner.getCondition() == null)
	throw new IllegalStateException("No configured condition available??");

      return owner.getCondition().evaluate(owner, m_BranchToken);
    }

    /**
     * Executes the group of actors.
     *
     * @return		null if everything went smooth
     */
    @Override
    public String execute() {
      String		result;
      AbstractActor	branch;

      if (doThen())
	branch = ((IfThenElse) m_ControlActor).getThenActor();
      else
	branch = ((IfThenElse) m_ControlActor).getElseActor();

      try {
	// input
	if ((m_BranchToken != null) && (branch instanceof InputConsumer)) {
	  if (branch.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	    branch.getFlowExecutionListeningSupporter().getFlowExecutionListener().preInput(branch, m_BranchToken);
	  ((InputConsumer) branch).input(m_BranchToken);
	  if (branch.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	    branch.getFlowExecutionListeningSupporter().getFlowExecutionListener().postInput(branch);
	}
	// execute
	if (branch.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	  branch.getFlowExecutionListeningSupporter().getFlowExecutionListener().preExecute(branch);
	result = branch.execute();
	if (branch.getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	  branch.getFlowExecutionListeningSupporter().getFlowExecutionListener().postExecute(branch);
      }
      catch (Throwable t) {
	result = handleException(branch.getFullName() + " generated the following exception: ", t);
      }

      if (result != null)
	result = branch.getErrorHandler().handleError(branch, "execute", result);

      return result;
    }

    /**
     * Cleans up data structures, frees up memory.
     */
    @Override
    public void cleanUp() {
      m_BranchToken = null;

      super.cleanUp();
    }
  }

  /** the key for storing the current input token in the backup. */
  public final static String BACKUP_INPUT = "input";

  /** the name of the "then" actor. */
  public final static String NAME_THEN = "then";

  /** the name of the "else" actor. */
  public final static String NAME_ELSE = "else";
  
  /** the condition used for determining to execute then/else branch. */
  protected BooleanCondition m_Condition;

  /** the actor to execute in the "then" branch. */
  protected AbstractActor m_ThenActor;

  /** the actor to execute in the "else" branch. */
  protected AbstractActor m_ElseActor;

  /** the input token. */
  protected transient Token m_InputToken;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Emulates an If-Then-Else construct. The 'Then' branch gets executed "
      + "if the 'If' condition evaluates to 'true', otherwise the 'else' "
      + "branch gets executed.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "condition", "condition",
	    getDefaultCondition());

    m_OptionManager.add(
	    "then", "thenActor",
	    getDefaultThen());

    m_OptionManager.add(
	    "else", "elseActor",
	    getDefaultElse());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_InputToken = null;

    // need to be initialized because of "updateParent()" call
    m_ThenActor  = getDefaultThen();
    m_ElseActor  = getDefaultElse();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result = m_Condition.getQuickInfo();
    
    if (super.getQuickInfo() != null)
      result += ", " + super.getQuickInfo();
    
    return result;
  }

  /**
   * Returns the default condition.
   *
   * @return		the default condition
   */
  protected BooleanCondition getDefaultCondition() {
    return new Expression();
  }

  /**
   * Returns the default 'Then' actor.
   *
   * @return		the default actor
   */
  protected AbstractActor getDefaultThen() {
    AbstractActor	result;

    result = new Sequence();
    result.setName(NAME_THEN);

    return result;
  }

  /**
   * Returns the default 'Else' actor.
   *
   * @return		the default actor
   */
  protected AbstractActor getDefaultElse() {
    AbstractActor	result;

    result = new Sequence();
    result.setName(NAME_ELSE);

    return result;
  }

  /**
   * Returns an instance of a director.
   *
   * @return		the director
   */
  @Override
  protected AbstractDirector newDirector() {
    return new IfThenElseDirector();
  }

  /**
   * Sets the condition.
   *
   * @param value	the condition
   */
  public void setCondition(BooleanCondition value) {
    m_Condition = value;
    reset();
  }

  /**
   * Returns the current condition.
   *
   * @return		the condition
   */
  public BooleanCondition getCondition() {
    return m_Condition;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String conditionTipText() {
    return
        "The condition that determines which branch to execute: if it "
      + "evaluates to 'true' then the 'then' branch gets executed.";
  }

  /**
   * Sets the actor of the 'then' branch.
   *
   * @param value	the actor
   */
  public void setThenActor(AbstractActor value) {
    m_ThenActor = value;
    m_ThenActor.setName(NAME_THEN);
    reset();
    updateParent();
  }

  /**
   * Returns the actor of the 'then' branch.
   *
   * @return		the actor
   */
  public AbstractActor getThenActor() {
    return m_ThenActor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String thenActorTipText() {
    return "The actor of the 'then' branch.";
  }

  /**
   * Sets the actor of the 'else' branch.
   *
   * @param value	the actor
   */
  public void setElseActor(AbstractActor value) {
    m_ElseActor = value;
    m_ElseActor.setName(NAME_ELSE);
    reset();
    updateParent();
  }

  /**
   * Returns the actor of the 'else' branch.
   *
   * @return		the actor
   */
  public AbstractActor getElseActor() {
    return m_ElseActor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String elseActorTipText() {
    return "The actor of the 'else' branch.";
  }

  /**
   * Returns the size of the group.
   *
   * @return		always 2
   */
  @Override
  public int size() {
    return 2;
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  @Override
  public AbstractActor get(int index) {
    if (index == 0)
      return m_ThenActor;
    else if (index == 1)
      return m_ElseActor;
    else
      throw new IndexOutOfBoundsException("Only two items available, requested index: " + index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  @Override
  public void set(int index, AbstractActor actor) {
    if (index == 0)
      setThenActor(actor);
    else if (index == 1)
      setElseActor(actor);
    else
      getLogger().severe("Index out of range: " + index);
  }

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  @Override
  public int indexOf(String actor) {
    if (m_ThenActor.getName().equals(actor))
      return 0;
    else if (m_ElseActor.getName().equals(actor))
      return 1;
    else
      return -1;
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(false, ActorExecution.PARALLEL, true);
  }

  /**
   * Returns the name for the sub-actor at this position.
   * 
   * @param index	the position of the sub-actor
   * @return		the name to use
   */
  public String getFixedName(int index) {
    if (index == 0)
      return NAME_THEN;
    else if (index == 1)
      return NAME_ELSE;
    else
      throw new IllegalArgumentException("Invalid index: " + index);
  }

  /**
   * Returns the class that the condition accepts.
   *
   * @return		what the condition accepts
   */
  public Class[] accepts() {
    return m_Condition.accepts();
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
      ((IfThenElseDirector) m_Director).setBranchToken(m_InputToken);
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

    result = super.setUp();

    if (result == null)
      result = m_Condition.setUp(this);

    return result;
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   */
  public void input(Token token) {
    m_InputToken = token;
    ((IfThenElseDirector) m_Director).setBranchToken(m_InputToken);
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
}
