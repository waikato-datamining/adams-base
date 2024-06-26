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
 * CallableActors.java
 * Copyright (C) 2009-2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;


import adams.flow.control.AbstractDirector;
import adams.flow.control.MutableControlActor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorReferenceHandler;
import adams.flow.core.ActorReferenceHandlerHelper;

import java.util.HashSet;

/**
 <!-- globalinfo-start -->
 * Container for actors that need to be accessed via their name.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
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
 * &nbsp;&nbsp;&nbsp;default: CallableActors
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
 * <pre>-actor &lt;adams.flow.core.AbstractActor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;All the actors that are to be accessed via their name.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class CallableActors
  extends MutableControlActor 
  implements ActorReferenceHandler {

  /** for serialization. */
  private static final long serialVersionUID = -5282103315016094476L;

  /**
   * Dummy director.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static class CallableActorsDirector
    extends AbstractDirector {

    /** for serialization. */
    private static final long serialVersionUID = -7581710637774405432L;

    /**
     * Executes the group of actors.
     *
     * @return		always null
     */
    @Override
    public String execute() {
      return null;
    }
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Container for actors that need to be accessed via their name.";
  }

  /**
   * Returns an instance of a director.
   *
   * @return		the director
   */
  @Override
  protected AbstractDirector newDirector() {
    return new CallableActorsDirector();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String actorsTipText() {
    return "All the actors that are to be accessed via their name.";
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo()
      .allowStandalones(true)
      .actorExecution(ActorExecution.UNDEFINED)
      .forwardsInput(false);
  }

  /**
   * Checks whether the names of the contained actors are unique or not.
   *
   * @return		null if names unique, otherwise offending pair
   */
  protected String checkNames() {
    String		result;
    HashSet<String>	names;
    int			i;

    result = null;

    names = new HashSet<>();
    for (i = 0; i < size(); i++) {
      if (names.contains(get(i).getName())) {
	result = "Actor '" + get(i).getFullName() + "' has duplicate name '" + get(i).getName() + "'!";
	break;
      }
      else {
	if (getScopeHandler() != null)
	  result = getScopeHandler().addCallableName(this, get(i));
	if (result != null)
	  break;
	names.add(get(i).getName());
      }
    }

    return result;
  }

  /**
   * Ensures that the handlers appear in the correct order.
   *
   * @return		null if OK, otherwise error message
   * @see		#getProhibitedPrecedingActorReferenceHandlers()
   */
  @Override
  public String checkActorReferenceHandlers() {
    return ActorReferenceHandlerHelper.checkActorReferenceHandlers(this);
  }

  /**
   * Returns whether actors have to be referenced elsewhere in the flow
   * or whether it is optional.
   *
   * @return		true if required, false if optional
   */
  public boolean isReferencingRequired() {
    return true;
  }

  /**
   * Checks the names for uniqueness.
   *
   * @return		null if everything is fine, otherwise the offending
   * 			connection
   */
  @Override
  public String check() {
    String	result;
    
    result = checkNames();
    if (result == null)
      result = checkActorReferenceHandlers();
    
    return result;
  }

  /**
   * Returns the classes that are prohibited to appear before this reference
   * handler.
   *
   * @return		the classes
   */
  public Class[] getProhibitedPrecedingActorReferenceHandlers() {
    return new Class[]{};
  }

  /**
   * Does nothing.
   *
   * @return		null
   */
  @Override
  protected String doExecute() {
    return null;
  }

  /**
   * Finishes up the execution.
   */
  @Override
  public void wrapUp() {
    int		i;

    for (i = 0; i < size(); i++)
      get(i).wrapUp();

    super.wrapUp();
  }
}
