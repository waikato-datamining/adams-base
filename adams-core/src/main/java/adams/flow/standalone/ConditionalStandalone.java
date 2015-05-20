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
 * ConditionalStandalone.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.flow.control.AbstractConditionalActor;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;

/**
 <!-- globalinfo-start -->
 * Standalone that needs to fullfil a condition before being executed.
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
 * &nbsp;&nbsp;&nbsp;default: ConditionalStandalone
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-condition &lt;adams.flow.condition.test.TestCondition&gt; (property: condition)
 * &nbsp;&nbsp;&nbsp;The condition that has to be met before the actor can be executed.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.condition.test.True
 * </pre>
 * 
 * <pre>-execution-time &lt;boolean&gt; (property: checkAtExecutionTime)
 * &nbsp;&nbsp;&nbsp;If set to true, then the condition is checked at execution time (whenever 
 * &nbsp;&nbsp;&nbsp;the actor gets executed) and not during setup.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-execute-on-fail &lt;boolean&gt; (property: executeOnFail)
 * &nbsp;&nbsp;&nbsp;If set to true, then the actor is only executed if the condition fails.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-actor &lt;adams.flow.core.AbstractActor&gt; (property: actor)
 * &nbsp;&nbsp;&nbsp;The base standalone to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.CallableActors
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConditionalStandalone
  extends AbstractConditionalActor {

  /** for serialization. */
  private static final long serialVersionUID = -8030372852516932814L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Standalone that needs to fullfil a condition before being executed.";
  }

  /**
   * Returns the default actor to use.
   *
   * @return		the default actor
   */
  @Override
  protected AbstractActor getDefaultActor() {
    return new CallableActors();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String actorTipText() {
    return "The base standalone to use.";
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(true, ActorExecution.UNDEFINED, false);
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (!ActorUtils.isStandalone(m_BaseActor))
	result = "Base actor is not a standalone!";
    }

    return result;
  }
}
