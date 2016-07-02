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
 * MultiScopeRestriction.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.execution.debug;

import adams.flow.core.Actor;
import adams.flow.execution.ExecutionStage;

/**
 <!-- globalinfo-start -->
 * Combines multiple scope restrictions.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-restriction &lt;adams.flow.execution.debug.AbstractScopeRestriction&gt; [-restriction ...] (property: restrictions)
 * &nbsp;&nbsp;&nbsp;The array of scope restrictions to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-combination &lt;AND|OR|XOR&gt; (property: combination)
 * &nbsp;&nbsp;&nbsp;How to combine the results of the specified scope restrictions; AND: all 
 * &nbsp;&nbsp;&nbsp;must be true, OR: at least one must be true, XOR: exactly one must be true.
 * &nbsp;&nbsp;&nbsp;default: AND
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiScopeRestriction
  extends AbstractScopeRestriction {

  /** for serialization. */
  private static final long serialVersionUID = 2134802149210184280L;

  /**
   * How the scope checks get combined.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum ScopeCombination {
    AND,
    OR,
    XOR;
  }
  /** the restrictions. */
  protected AbstractScopeRestriction[] m_Restrictions;

  /** how to apply the scope restrictions. */
  protected ScopeCombination m_Combination;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Combines multiple scope restrictions.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "restriction", "restrictions",
      new AbstractScopeRestriction[0]);

    m_OptionManager.add(
      "combination", "combination",
      ScopeCombination.AND);
  }

  /**
   * Sets the scope restrictions to use.
   *
   * @param value	the restrictions to use
   */
  public void setRestrictions(AbstractScopeRestriction[] value) {
    m_Restrictions = value;
    reset();
  }

  /**
   * Returns the restrictions in use.
   *
   * @return		the restrictions
   */
  public AbstractScopeRestriction[] getRestrictions() {
    return m_Restrictions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String restrictionsTipText() {
    return "The array of scope restrictions to use.";
  }

  /**
   * Sets how to combine the results of the restrictions.
   *
   * @param value	the combination
   */
  public void setCombination(ScopeCombination value) {
    m_Combination = value;
    reset();
  }

  /**
   * Returns how to combine the results of the restrictions.
   *
   * @return		the combination
   */
  public ScopeCombination getCombination() {
    return m_Combination;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String combinationTipText() {
    return
      "How to combine the results of the specified scope restrictions; "
      + "AND: all must be true, OR: at least one must be true, XOR: exactly one must be true.";
  }

  /**
   * Checks whether the specified actor falls within the scope.
   *
   * @param actor	the actor to check
   * @param stage	the execution stage
   * @return		true if within scope
   */
  @Override
  public boolean checkScope(Actor actor, ExecutionStage stage) {
    boolean	result;
    int		count;

    count = 0;
    for (AbstractScopeRestriction restriction: m_Restrictions) {
      if (restriction.checkScope(actor, stage))
	count++;
    }

    switch (m_Combination) {
      case AND:
	result = (count == m_Restrictions.length);
	break;
      case OR:
	result = (count > 0);
	break;
      case XOR:
	result = (count == 1);
	break;
      default:
	throw new IllegalStateException("Unhandled scope combination: " + m_Combination);
    }

    return result;
  }
}
