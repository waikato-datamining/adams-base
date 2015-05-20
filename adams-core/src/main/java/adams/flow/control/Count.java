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
 * Count.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.Counting;
import adams.flow.core.Token;

import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Counts the number of tokens that pass through and meet the condition and tees off the current count every n-th token.
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
 * Valid options are: <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: Count
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
 * <pre>-tee &lt;adams.flow.core.AbstractActor&gt; [-tee ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actors to siphon-off the tokens to.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-condition &lt;adams.flow.condition.bool.BooleanCondition&gt; (property: condition)
 * &nbsp;&nbsp;&nbsp;The boolean condition to evaluate.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.condition.bool.Counting
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Count
  extends Tee {

  /** for serialization. */
  private static final long serialVersionUID = -3408173173526076280L;

  /** the key for storing the current counter in the backup. */
  public final static String BACKUP_CURRENT = "current";

  /** the condition to use. */
  protected BooleanCondition m_Condition;

  /** only increments the counter when the condition is met. */
  protected boolean m_IncrementOnlyIfConditionMet;
  
  /** the current count. */
  protected int m_Current;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Counts the number of tokens that pass through and meet the condition and tees off the current count every n-th token.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "condition", "condition",
	    new Counting());

    m_OptionManager.add(
	    "increment-only-if-condition-met", "incrementOnlyIfConditionMet",
	    false);
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
    result += QuickInfoHelper.toString(this, "incrementOnlyIfConditionMet", m_IncrementOnlyIfConditionMet, "[incr. only if condition met]", " ");
    
    if (super.getQuickInfo() != null)
      result += ", " + super.getQuickInfo();
    
    return result;
  }

  /**
   * Sets the condition responsible for tee-ing of the token.
   *
   * @param value	the condition
   */
  public void setCondition(BooleanCondition value) {
    m_Condition = value;
    reset();
  }

  /**
   * Returns the condition responsible for tee-ing of the token.
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
    return "The boolean condition to evaluate.";
  }

  /**
   * Sets whether to increment the counter only if the condition is met.
   *
   * @param value	if true then the counter is only increment if the 
   * 			boolean condition returns true
   */
  public void setIncrementOnlyIfConditionMet(boolean value) {
    m_IncrementOnlyIfConditionMet = value;
    reset();
  }

  /**
   * Returns whether the counter is only incremented if the condition is met.
   *
   * @return		true if the counter is only incremented of the boolean
   * 			condition returns true
   */
  public boolean getIncrementOnlyIfConditionMet() {
    return m_IncrementOnlyIfConditionMet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String incrementOnlyIfConditionMetTipText() {
    return 
	"If enabled, the counter gets only incremented if the condition is "
	+ "met; useful if you want to count tokens of a certain value.";
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_CURRENT);
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

    result.put(BACKUP_CURRENT, m_Current);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_CURRENT)) {
      m_Current = (Integer) state.get(BACKUP_CURRENT);
      state.remove(BACKUP_CURRENT);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Current = 0;
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   */
  @Override
  public void input(Token token) {
    if (!m_IncrementOnlyIfConditionMet)
      m_Current++;
    super.input(token);
  }

  /**
   * Checks whether we can process the token.
   *
   * @return		true if token can be processed
   */
  protected boolean canFire() {
    return m_Condition.evaluate(this, m_InputToken);
  }

  /**
   * Returns whether the token can be processed in the tee actor.
   *
   * @param token	the token to process
   * @return		true if token can be processed
   */
  @Override
  protected boolean canProcessInput(Token token) {
    return (super.canProcessInput(token) && canFire());
  }

  /**
   * Creates the token to tee-off.
   *
   * @param token	the input token
   * @return		the token to tee-off
   */
  @Override
  protected Token createTeeToken(Token token) {
    return new Token(new Integer(m_Current));
  }

  /**
   * Processes the token normal, i.e., not in thread.
   *
   * @param token	the token to process
   * @return		an optional error message, null if everything OK
   */
  @Override
  protected String processInput(Token token) {
    if (m_IncrementOnlyIfConditionMet)
      m_Current++;
    return super.processInput(token);
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
   * Stops the execution.
   */
  public void stopExecution() {
    m_Condition.stopExecution();
    super.stopExecution();
  }
}
