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
 * Trigger.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.flow.core.AbstractActor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.ActorWithConditionalEquivalent;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Executes the tee-actor whenever a token gets passed through. In contrast to the Tee actor, it doesn't feed the tee-actor with the current token.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * <p/>
 <!-- flow-summary-end -->
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
 * &nbsp;&nbsp;&nbsp;default: Trigger
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Trigger
  extends AbstractTee
  implements ActorWithConditionalEquivalent {

  /** for serialization. */
  private static final long serialVersionUID = 4690934665757923783L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Executes the subs-actors whenever a token gets passed through. In "
      + "contrast to the " + Tee.class.getName() + " actor, it doesn't feed "
      + "the sub-actors with the current token.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Actors.setAllowStandalones(true);
    m_Actors.setAllowSource(true);
  }

  /**
   * Returns the class that is the corresponding conditional equivalent.
   * 
   * @return		the class, null if none available
   */
  public Class getConditionalEquivalent() {
    return ConditionalTrigger.class;
  }

  /**
   * Gets called in the setUp() method. Returns null if tee-actors are fine,
   * otherwise error message.
   *
   * @return		always null
   */
  @Override
  protected String setUpTeeActors() {
    return ActorUtils.checkForSource(getActors());
  }

  /**
   * Returns whether singletons are allowed in this group or not.
   *
   * @return		true if singletons are allowed
   */
  public boolean canContainStandalones() {
    return true;
  }

  /**
   * Checks the tee actor before it is set.
   * Returns an error message if the actor is not acceptable, null otherwise.
   *
   * @param index	the index the actor gets set
   * @param actor	the actor to check
   * @return		always null
   */
  @Override
  protected String checkTeeActor(int index, AbstractActor actor) {
    return null;
  }

  /**
   * Checks the tee actors before they are set via the setTeeActors method.
   * Returns an error message if the actors are not acceptable, null otherwise.
   *
   * @param actors	the actors to check
   * @return		null if checks passed or null in case of an error
   */
  @Override
  protected String checkTeeActors(AbstractActor[] actors) {
    return ActorUtils.checkForSource(actors);
  }

  /**
   * Processes the token.
   *
   * @param token	ignored
   * @return		an optional error message, null if everything OK
   */
  @Override
  protected String processInput(Token token) {
    String	result;

    try {
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().preExecute(this);
      result = m_Actors.execute();
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().postExecute(this);
    }
    catch (Exception e) {
      result = handleException("Failed to trigger: ", e);
    }

    return result;
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(true, ActorExecution.SEQUENTIAL, false);
  }
}
