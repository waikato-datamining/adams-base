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
 * RemoveUnusedCallableActors.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.flow.core.AbstractCallableActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.MutableActorHandler;
import adams.flow.standalone.CallableActors;

import java.util.HashSet;

/**
 <!-- globalinfo-start -->
 * Removes all unused callable actors. Disabled actors referencing callable actors are treated as if they were enabled. If a CallableActors actor ends up being empty, it will get removed as well.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoveUnusedCallableActors
  extends AbstractModifyingProcessor 
  implements CleanUpProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 1634101991639994065L;
  
  /** the callable actor helper. */
  protected CallableActorHelper m_Helper;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Removes all unused callable actors. Disabled actors referencing callable "
      + "actors are treated as if they were enabled. If a CallableActors actor "
      + "ends up being empty, it will get removed as well.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Locates all callable actors and returns their full names.
   *
   * @param actor	the actor to search below
   * @return		the full names
   */
  protected void locateCallableActors(Actor actor, HashSet<String> fullNames) {
    ActorHandler	handler;
    int			i;

    if (actor instanceof ActorHandler) {
      handler = (ActorHandler) actor;
      if (handler instanceof CallableActors) {
	for (i = 0; i < handler.size(); i++)
	  fullNames.add(handler.get(i).getFullName());
      }
      else {
	for (i = 0; i < handler.size(); i++)
	  locateCallableActors(handler.get(i), fullNames);
      }
    }
  }

  /**
   * Locates all references to callable actors and returns their full names.
   *
   * @param actor	the actor to search below
   * @return		the full names of the callable actors
   */
  protected void locateCallableActorReferences(Actor actor, HashSet<String> fullNames) {
    ActorHandler		handler;
    int				i;
    AbstractCallableActor	reference;
    Actor			callable;

    if (actor instanceof AbstractCallableActor) {
      reference = (AbstractCallableActor) actor;
      callable    = m_Helper.findCallableActorRecursive(reference, reference.getCallableName());
      if (callable != null)
	fullNames.add(callable.getFullName());
    }
    else if (actor instanceof ActorHandler) {
      handler = (ActorHandler) actor;
      for (i = 0; i < handler.size(); i++)
	locateCallableActorReferences(handler.get(i), fullNames);
    }
  }

  /**
   * Removes the unused callable actors.
   *
   * @param actor	the actor process
   * @param unused	the unused callable actors to remove
   */
  protected void removeUnused(Actor actor, HashSet<String> unused) {
    CallableActors		callableActors;
    HashSet<CallableActors>	parents;
    Actor			callable;

    m_Modified = true;

    parents = new HashSet<CallableActors>();
    for (String fullname: unused) {
      callable = ActorUtils.locate(fullname, actor);
      if (callable == null) {
	getLogger().severe("Failed to locate callable actor: " + fullname);
	continue;
      }
      callableActors = (CallableActors) callable.getParent();
      callableActors.remove(callable.index());
      parents.add(callableActors);
    }

    // check for empty CallableActors
    for (CallableActors gactors: parents) {
      if (gactors.size() == 0) {
	if (isLoggingEnabled())
	  getLogger().info("Removing empty " + CallableActors.class.getSimpleName() + ": " + gactors.getFullName());
	((MutableActorHandler) gactors.getParent()).remove(gactors.index());
      }
    }
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process
   * @return		the processed actor
   */
  @Override
  protected void processActor(Actor actor) {
    HashSet<String>	callable;
    HashSet<String>	referenced;
    HashSet<String>	unused;

    callable = new HashSet<String>();
    locateCallableActors(actor, callable);
    if (isLoggingEnabled())
      getLogger().info("callable actors found: " + callable);

    referenced = new HashSet<String>();
    locateCallableActorReferences(actor, referenced);
    if (isLoggingEnabled())
      getLogger().info("references found: " + referenced);

    unused = (HashSet<String>) callable.clone();
    unused.removeAll(referenced);
    if (isLoggingEnabled())
      getLogger().info("unused callable actors: " + unused);

    if (unused.size() > 0)
      removeUnused(actor, unused);
  }
}
