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
 * CallableActorHelper.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import java.util.List;

import adams.core.logging.LoggingObject;
import adams.flow.control.AbstractDirectedControlActor;
import adams.flow.sink.CallableSink;
import adams.flow.source.CallableSource;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.CallableTransformer;

/**
 * Helper class for callable actors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CallableActorHelper
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -763479272812116920L;

  /**
   * Checks a control actor's children whether they contain the callable actor
   * that we're looking for.
   *
   * @param group	the group to check
   * @param name	the name of the callable actor
   * @return		the callable actor or null if not found
   */
  public AbstractActor findCallableActor(ActorHandler group, CallableActorReference name) {
    AbstractActor		result;
    int				i;
    CallableActorHandler	callable;
    int				index;
    ExternalActorHandler	external;

    result = null;

    for (i = 0; i < group.size(); i++) {
      if (group.get(i) instanceof CallableActorHandler) {
	callable = (CallableActorHandler) group.get(i);
	index  = callable.indexOf(name.toString());
	if (index > -1) {
	  result = callable.get(index);
	  break;
	}
      }
      else if (group.get(i) instanceof ExternalActorHandler) {
	external = (ExternalActorHandler) group.get(i);
	if (external.getExternalActor() instanceof ActorHandler) {
	  result = findCallableActor((ActorHandler) external.getExternalActor(), name);
	  if (result != null)
	    break;
	}
      }
    }

    return result;
  }

  /**
   * Tries to find the callable actor referenced by its name.
   *
   * @param root	the root to search in
   * @param name	the name of the callable actor
   * @return		the callable actor or null if not found
   */
  public AbstractActor findCallableActor(AbstractActor root, CallableActorReference name) {
    AbstractActor	result;

    result = null;

    if (root == null) {
      getLogger().severe("No root container found!");
    }
    else if (!(root instanceof AbstractDirectedControlActor)) {
      getLogger().severe(
	  "Root is not a container ('" + root.getFullName() + "'/"
	  + root.getClass().getName() + ")!");
      root = null;
    }

    if (root != null)
      result = findCallableActor((ActorHandler) root, name);

    return result;
  }

  /**
   * Tries to find the referenced callable actor. First all possible actor
   * handlers are located recursively (up to the root) that allow also
   * singletons. This list of actors is then searched for the callable actor.
   *
   * @param actor	the actor to start from
   * @param name	the name of the callable actor
   * @return		the callable actor or null if not found
   * @see		ActorUtils#findActorHandlers(AbstractActor, boolean)
   */
  public AbstractActor findCallableActorRecursive(AbstractActor actor, CallableActorReference name) {
    AbstractActor	result;
    List<ActorHandler>	handlers;
    int			i;

    result   = null;
    handlers = ActorUtils.findActorHandlers(actor, true);
    for (i = 0; i < handlers.size(); i++) {
      result = findCallableActor(handlers.get(i), name);
      if (result != null)
	break;
    }

    return result;
  }

  /**
   * Returns the setup obtained from the callable actor, source or transformer.
   * In the latter case, the transformer must output a setup if no input
   * token is provided.
   *
   * @param cls		the class that the output must match, null to ignore
   * @param callableName	the callable actor referenced
   * @param start	where the search starts from
   * @param callable	the callable actor (source or transformer) to use
   * @return		the setup, null if not found or didn't match class
   */
  protected static Object getSetup(Class cls, CallableActorReference callableName, AbstractActor start, AbstractCallableActor callable) {
    Object		result;
    AbstractActor	actor;
    Token		token;

    result = null;

    callable.setCallableName(callableName);
    callable.setParent(start.getParent());
    if (callable.setUp() == null) {
      callable.execute();
      token = ((OutputProducer) callable).output();
      if (token != null) {
	result = token.getPayload();
	if (cls != null) {
	  if (!cls.isInstance(result))
	    result = null;
	}
	actor = callable.getCallableActor();
	callable.wrapUp();
	callable.cleanUp();
	// fix wrapUp/cleanUp of callable actor, e.g., re-registering variable listeners
	if (actor != null)
	  actor.setUp();
      }
    }

    return result;
  }

  /**
   * Returns the setup obtained from the callable actor, source or transformer.
   * In the latter case, the transformer must output a setup if no input
   * token is provided.
   *
   * @param cls		the class that the output must match, null to ignore
   * @param callableName	the callable actor referenced
   * @param start	where the search starts from
   * @return		the setup, null if not found or didn't match class
   */
  public static Object getSetup(Class cls, CallableActorReference callableName, AbstractActor start) {
    Object	result;
    
    // source?
    result = getSetupFromSource(cls, callableName, start);
    
    // transformer?
    if (result == null)
      result = getSetupFromTransformer(cls, callableName, start);
    
    return result;
  }

  /**
   * Returns the setup obtained from the callable source.
   *
   * @param cls		the class that the output must match, null to ignore
   * @param callableName	the callable actor referenced
   * @param start	where the search starts from
   * @return		the setup, null if not found or didn't match class
   */
  public static Object getSetupFromSource(Class cls, CallableActorReference callableName, AbstractActor start) {
    return getSetup(cls, callableName, start, new CallableSource());
  }

  /**
   * Returns the setup obtained from the callable transformer.
   * The transformer must output a setup if no input token is provided.
   *
   * @param cls		the class that the output must match, null to ignore
   * @param callableName	the callable actor referenced
   * @param start	where the search starts from
   * @return		the setup, null if not found or didn't match class
   */
  public static Object getSetupFromTransformer(Class cls, CallableActorReference callableName, AbstractActor start) {
    return getSetup(cls, callableName, start, new CallableTransformer());
  }
  
  /**
   * Returns all {@link CallableActors} instances that can be located in the flow
   * 
   * @param flow	the flow to use
   * @return		the {@link CallableActors} instances
   */
  public static List<AbstractActor> findAllCallableActors(AbstractActor flow) {
    return ActorUtils.enumerate(flow.getRoot(), new Class[]{CallableActors.class});
  }
  
  /**
   * Returns the {@link CallableActors} instance. Can insert one if required.
   * 
   * @param actor	the actor to start the search from
   * @param insert	whether to insert a {@link CallableActors} instance if 
   * 			none present
   * @return		the {@link CallableActors} instance, 
   * 			null in case of error
   */
  public static CallableActors createCallableActors(AbstractActor actor, boolean insert) {
    CallableActors		result;
    List<ActorHandler>		callable;
    int				i;
    AbstractActor		root;
    
    result = null;
    
    callable = ActorUtils.findActorHandlers(actor, true, true);
    i      = 0;
    while (i < callable.size()) {
      if (callable.get(i) instanceof CallableActors)
	i++;
      else
	callable.remove(i);
    }

    // no CallableActors available?
    if ((callable.size() == 0) && insert) {
      root = actor.getRoot();
      if (!((ActorHandler) root).getActorHandlerInfo().canContainStandalones()) {
	System.err.println(
	    "Root actor '" + root.getName() + "' cannot contain standalones!");
	return null;
      }
      result = new CallableActors();
      if (root instanceof MutableActorHandler) 
	((MutableActorHandler) root).add(0, (AbstractActor) result);
      else
	System.err.println(
	    "Cannot add " + CallableActors.class.getSimpleName() + " container to root actor!");
    }
    else {
      result = (CallableActors) callable.get(callable.size() - 1);
    }
    
    return result;
  }
  
  /**
   * Turns the specified actor into a callable actor.
   * 
   * @param actor	the actor to turn into a callable actor
   * @return		the replacement actor ({@link CallableSink} etc.), 
   * 			null in case of error
   */
  public static AbstractActor createCallableActor(AbstractActor actor) {
    CallableActors		callableActors;
    AbstractCallableActor	replacement;
    int				index;
    ActorHandler		parent;
    
    callableActors = createCallableActors(actor, true);
    if (callableActors == null)
      return null;

    // move actor
    index  = actor.index();
    parent = (ActorHandler) actor.getParent();
    callableActors.add(actor);

    // create replacement
    replacement = null;
    if (ActorUtils.isSource(actor))
      replacement = new CallableSource();
    else if (ActorUtils.isTransformer(actor))
      replacement = new CallableTransformer();
    else if (ActorUtils.isSink(actor))
      replacement = new CallableSink();
    replacement.setCallableName(new CallableActorReference(actor.getName()));
    parent.set(index, replacement);
    
    return replacement;
  }
}
