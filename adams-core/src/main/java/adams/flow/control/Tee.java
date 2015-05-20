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
 * Tee.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.flow.core.AbstractActor;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.ActorWithConditionalEquivalent;
import adams.flow.core.ActorWithTimedEquivalent;
import adams.flow.core.Compatibility;
import adams.flow.core.InputConsumer;

/**
 <!-- globalinfo-start -->
 * Allows to tap into the flow and tee-off tokens.
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
 * Conditional equivalent:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.control.ConditionalTee
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
 * &nbsp;&nbsp;&nbsp;default: Tee
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
 * <pre>-finish-before-stopping &lt;boolean&gt; (property: finishBeforeStopping)
 * &nbsp;&nbsp;&nbsp;If enabled, actor first finishes processing all data before stopping.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-asynchronous &lt;boolean&gt; (property: asynchronous)
 * &nbsp;&nbsp;&nbsp;If enabled, the sub-actors get executed asynchronously rather than the flow 
 * &nbsp;&nbsp;&nbsp;waiting for them to finish before proceeding with execution.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-tee &lt;adams.flow.core.AbstractActor&gt; [-tee ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actors to siphon-off the tokens to.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Tee
  extends AbstractTee
  implements ActorWithConditionalEquivalent, ActorWithTimedEquivalent{

  /** for serialization. */
  private static final long serialVersionUID = -7489525518244336025L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows to tap into the flow and tee-off tokens.";
  }

  /**
   * Returns the class that is the corresponding conditional equivalent.
   * 
   * @return		the class, null if none available
   */
  public Class getConditionalEquivalent() {
    return ConditionalTee.class;
  }

  /**
   * Returns the class that is the corresponding timed equivalent.
   * 
   * @return		the class, null if none available
   */
  public Class getTimedEquivalent() {
    return TimedTee.class;
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    ActorHandlerInfo	info;

    info = super.getActorHandlerInfo();

    return new ActorHandlerInfo(info.canContainStandalones(), false, info.getActorExecution(), info.getForwardsInput());
  }

  /**
   * Checks the tee actor before it is set.
   * Returns an error message if the actor is not acceptable, null otherwise.
   *
   * @param index	the index the actor gets set
   * @param actor	the actor to check
   * @return		null if accepted, otherwise error message
   */
  @Override
  protected String checkTeeActor(int index, AbstractActor actor) {
    if (ActorUtils.isSource(actor))
      return "You cannot add a source actor ('" + actor.getName() + "'/" + actor.getClass().getName() + ")!";

    return null;
  }

  /**
   * Checks the tee actor before it is set via the setTeeActor method.
   * Returns an error message if the actor is not acceptable, null otherwise.
   *
   * @param actor	the actor to check
   * @return		null if accepted, otherwise error message
   */
  @Override
  protected String checkTeeActors(AbstractActor[] actors) {
    int		i;

    if (actors.length > 0) {
      for (i = 0; i < actors.length; i++) {
	if (actors[i].getSkip())
	  continue;
	if (!(actors[i] instanceof InputConsumer))
	  return "You need to provide an actor that processes input, '" + actors[i].getName() + "'/" + actors[i].getClass().getName() + " doesn't!";
      }
    }

    return null;
  }

  /**
   * Gets called in the setUp() method. Returns null if tee-actor is fine,
   * otherwise error message.
   *
   * @return		null if everything OK, otherwise error message
   */
  @Override
  protected String setUpTeeActors() {
    String		result;
    Compatibility	comp;

    result = null;

    comp = new Compatibility();
    if (!comp.isCompatible(accepts(), m_Actors.accepts()))
      result = "Accepted input and tee actors are not compatible!";

    return result;
  }
}
