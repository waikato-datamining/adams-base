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
 * Not.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Negates the output of the specified condition.
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
 * <pre>-condition &lt;adams.flow.condition.bool.AbstractBooleanCondition&gt; (property: condition)
 * &nbsp;&nbsp;&nbsp;The condition to evaluate and negate.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.condition.bool.Expression
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Not
  extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = -7927342245398106669L;

  /** the condition to evaluate and negate. */
  protected BooleanCondition m_Condition;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Negates the output of the specified condition.";
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
  }

  /**
   * Returns the default condition.
   *
   * @return		the default condition
   */
  protected BooleanCondition getDefaultCondition() {
    return new False();
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
    return "The condition to evaluate and negate.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the quick info
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result = m_Condition.getQuickInfo();
    if (result != null)
      return "! " + result;
    else
      return "! " + m_Condition.getClass().getName();
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		Unknown
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Configures the condition.
   *
   * @param owner	the actor this condition belongs to
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp(Actor owner) {
    return m_Condition.setUp(owner);
  }

  /**
   * Evaluates the condition.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through the actor
   * @return		always true
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    return !m_Condition.evaluate(owner, token);
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Condition.stopExecution();
  }
}
