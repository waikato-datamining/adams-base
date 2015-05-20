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
 * ConditionalSubProcess.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.BooleanConditionSupporter;
import adams.flow.condition.bool.Expression;

/**
 <!-- globalinfo-start -->
 * Encapsulates a sequence of flow items. The first actor must accept input and the last one must produce output. But the sequence gets only executed if the condition holds true.
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
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ConditionalSubProcess
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
 * <pre>-actor &lt;adams.flow.core.AbstractActor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;All the actors that define this sequence.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-condition &lt;adams.flow.condition.bool.BooleanCondition&gt; (property: condition)
 * &nbsp;&nbsp;&nbsp;The condition to evaluate - only as long as it evaluates to 'true' the loop 
 * &nbsp;&nbsp;&nbsp;actors get executed.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.condition.bool.Expression
 * </pre>
 * 
 * <pre>-drop-tokens-on-condition-fail (property: dropTokensOnConditionFail)
 * &nbsp;&nbsp;&nbsp;If enabled, any incoming tokens that weren't processed due to the condition 
 * &nbsp;&nbsp;&nbsp;evaluating to false will get dropped and not forwarded.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConditionalSubProcess
  extends SubProcess
  implements BooleanConditionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 619693652039880564L;
  
  /** the condition that determines the execution of the subprocess items. */
  protected BooleanCondition m_Condition;
  
  /** whether to drop tokens when condition didn't hold true. */
  protected boolean m_DropTokensOnConditionFail;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Encapsulates a sequence of flow items. The first actor must accept "
	+ "input and the last one must produce output. But the sequence gets "
	+ "only executed if the condition holds true.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "condition", "condition",
	    new Expression());

    m_OptionManager.add(
	    "drop-tokens-on-condition-fail", "dropTokensOnConditionFail",
	    false);
  }

  /**
   * Sets the condition to evaluate.
   *
   * @param value	the condition
   */
  public void setCondition(BooleanCondition value) {
    m_Condition = value;
    reset();
  }

  /**
   * Returns the condtion to evaluate.
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
        "The condition to evaluate - only as long as it evaluates to 'true' "
      + "the loop actors get executed.";
  }

  /**
   * Sets whether to drop incoming tokens if condition evaluates to false
   * instead of forwarding them.
   *
   * @param value	if true tokens get dropped if evaluation evaluates to false
   */
  public void setDropTokensOnConditionFail(boolean value) {
    m_DropTokensOnConditionFail = value;
    reset();
  }

  /**
   * Returns whether to drop incoming tokens if condition evaluates to false
   * instead of forwarding them.
   *
   * @return		true if to drop tokens when condition evaluates to false
   */
  public boolean getDropTokensOnConditionFail() {
    return m_DropTokensOnConditionFail;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dropTokensOnConditionFailTipText() {
    return 
	"If enabled, any incoming tokens that weren't processed due to the "
	+ "condition evaluating to false will get dropped and not forwarded.";
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
   * Initializes the sub-actors for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (m_Condition == null)
	result = "No condition provided!";
    }

    if (result == null)
      result = m_Condition.setUp(this);
    
    return result;
  }

  /**
   * Executes the actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    if (m_Condition.evaluate(this, m_CurrentToken))
      result = super.doExecute();
    else if (!m_DropTokensOnConditionFail)
      getOutputTokens().add(m_CurrentToken);

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
