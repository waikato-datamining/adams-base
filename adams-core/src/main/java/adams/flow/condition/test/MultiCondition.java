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
 * MultiCondition.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.condition.test;

/**
 <!-- globalinfo-start -->
 * Checks multiple conditions. Stops checking when the first fails.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-condition &lt;adams.flow.condition.AbstractCondition&gt; [-condition ...] (property: subConditions)
 *         The conditions to check.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiCondition
  extends AbstractTestCondition {

  /** for serialization. */
  private static final long serialVersionUID = -3833261282991705L;

  /** the sub-conditions to check. */
  protected AbstractTestCondition[] m_Conditions;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks multiple conditions. Stops checking when the first fails.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "condition", "subConditions",
	    new AbstractTestCondition[0]);
  }

  /**
   * Sets the conditions to use.
   *
   * @param value	the conditions
   */
  public void setSubConditions(AbstractTestCondition[] value) {
    m_Conditions = value;
    reset();
  }

  /**
   * Returns the conditions to check.
   *
   * @return		the conditions
   */
  public AbstractTestCondition[] getSubConditions() {
    return m_Conditions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String subConditionsTipText() {
    return "The conditions to check.";
  }

  /**
   * Performs the actual testing of the condition.
   *
   * @return		the test result, null if everything OK, otherwise
   * 			the error message
   */
  @Override
  protected String performTest() {
    String	result;
    int		i;

    result = null;

    for (i = 0; i < m_Conditions.length; i++) {
      result = m_Conditions[i].getTestResult();
      if (result != null) {
	result = "Failed condition #" + (i+1) + ": " + result;
	break;
      }
    }

    return result;
  }
}
