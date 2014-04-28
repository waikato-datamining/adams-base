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
 * AbstractMultiCondition.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.flow.core.Actor;

/**
 * Ancestor for conditions that use multiple sub-conditions.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMultiCondition
  extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = -7930281929775307418L;
  
  /** the conditions to evaluate. */
  protected BooleanCondition[] m_Conditions;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "condition", "conditions",
	    new BooleanCondition[0]);
  }

  /**
   * Sets the conditions to evaluate.
   *
   * @param value	the conditions
   */
  public void setConditions(BooleanCondition[] value) {
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
  public abstract String conditionsTipText();
  
  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "conditions", m_Conditions.length + " condition" + ((m_Conditions.length != 1) ? "s" : ""));
  }

  /**
   * Configures the condition.
   *
   * @param owner	the actor this condition belongs to
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp(Actor owner) {
    String	result;

    result = super.setUp(owner);

    if (result == null) {
      if (m_Conditions.length == 0)
	result = "No conditions provided!";
    }

    return result;
  }
}
