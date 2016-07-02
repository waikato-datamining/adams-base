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
 * SubFlowRestriction.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.execution.debug;

import adams.flow.core.Actor;
import adams.flow.core.ActorPath;
import adams.flow.execution.ExecutionStage;

/**
 <!-- globalinfo-start -->
 * Restricts the scope to the sub-flow specified by the path (= root of subflow).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-root &lt;adams.flow.core.ActorPath&gt; (property: root)
 * &nbsp;&nbsp;&nbsp;The root of the sub-flow to restrict the scope to.
 * &nbsp;&nbsp;&nbsp;default: Flow
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SubFlowRestriction
  extends AbstractScopeRestriction {

  private static final long serialVersionUID = 7714227816612692794L;

  /** the root of the tree to restrict to. */
  protected ActorPath m_Root;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Restricts the scope to the sub-flow specified by the path "
	+ "(= root of subflow).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "root", "root",
      new ActorPath("Flow"));
  }

  /**
   * Sets the root of the subtree to restrict the scope to.
   *
   * @param value	the root
   */
  public void setRoot(ActorPath value) {
    m_Root = value;
    reset();
  }

  /**
   * Returns the root of the subtree to retrict the scope to.
   *
   * @return		the root
   */
  public ActorPath getRoot() {
    return m_Root;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String rootTipText() {
    return "The root of the sub-flow to restrict the scope to.";
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
    ActorPath 	current;

    current = new ActorPath(actor.getFullName());

    return m_Root.isDescendant(current);
  }
}
