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
 * Copyright (C) 2009-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.MessageCollection;
import adams.core.logging.LoggingObject;
import adams.flow.control.AbstractDirectedControlActor;
import adams.flow.sink.CallableSink;
import adams.flow.source.CallableSource;
import adams.flow.standalone.AbstractMultiView;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.CallableTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for callable actors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class CallableActorHelper
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -763479272812116920L;

  /**
   * Checks a reference handler's children whether they contain the callable actor
   * that we're looking for.
   *
   * @param handler	the reference handler to check
   * @param name	the name of the callable actor
   * @return		the callable actor or null if not found
   */
  public Actor findCallableActor(ActorReferenceHandler handler, CallableActorReference name) {
    Actor	result;
    int		index;

    result = null;
    index  = handler.indexOf(name.toString());
    if (index > -1)
      result = handler.get(index);

    return result;
  }

  /**
   * Checks a control actor's children whether they contain the callable actor
   * that we're looking for.
   *
   * @param handler	the handler to check
   * @param name	the name of the callable actor
   * @return		the callable actor or null if not found
   */
  public Actor findCallableActor(ActorHandler handler, CallableActorReference name) {
    Actor			result;
    int				i;
    Actor			extActor;
    ExternalActorHandler 	extHandler;

    result = null;

    for (i = 0; i < handler.size(); i++) {
      if (handler.get(i) instanceof ActorReferenceHandler) {
        result = findCallableActor((ActorReferenceHandler) handler.get(i), name);
        if (result != null)
	  break;
      }
      else if (handler.get(i) instanceof ExternalActorHandler) {
	extHandler = (ExternalActorHandler) handler.get(i);
	if (extHandler.getExternalActor() instanceof ActorHandler) {
	  extActor = extHandler.getExternalActor();
	  if (extActor instanceof ActorReferenceHandler)
	    result = findCallableActor((ActorReferenceHandler) extActor, name);
	  else
	    result = findCallableActor((ActorHandler) extActor, name);
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
  public Actor findCallableActor(Actor root, CallableActorReference name) {
    Actor	result;

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
   * @see		ActorUtils#findActorHandlers(Actor, boolean)
   */
  public Actor findCallableActorRecursive(Actor actor, CallableActorReference name) {
    Actor		result;
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
   * Locates callable actors.
   *
   * @param handler	the handler to check
   * @param collected	the actors collected so far
   */
  protected void findCallableActors(ActorReferenceHandler handler, List<Actor> collected) {
    int 	i;

    for (i = 0; i < handler.size(); i++)
      collected.add(handler.get(i));
  }

  /**
   * Locates callable actors.
   *
   * @param handler	the handler to check
   * @param collected	the actors collected so far
   */
  protected void findCallableActors(ActorHandler handler, List<Actor> collected) {
    int				i;
    ExternalActorHandler 	extHandler;
    Actor			extActor;

    for (i = 0; i < handler.size(); i++) {
      if (handler.get(i) instanceof ActorReferenceHandler) {
        findCallableActors((ActorReferenceHandler) handler.get(i), collected);
      }
      else if (handler.get(i) instanceof ExternalActorHandler) {
	extHandler = (ExternalActorHandler) handler.get(i);
	if (extHandler.getExternalActor() instanceof ActorHandler) {
	  extActor = extHandler.getExternalActor();
	  if (extActor instanceof ActorReferenceHandler)
	    findCallableActors((ActorReferenceHandler) extActor, collected);
	  else
	    findCallableActors((ActorHandler) extActor, collected);
	}
      }
    }
  }

  /**
   * Locates all callable actors .
   *
   * @param actor	the actor to start from
   * @return		the callable actors
   */
  public List<Actor> findCallableActorsRecursive(Actor actor) {
    List<Actor>		result;
    List<ActorHandler>	handlers;
    int			i;

    result   = new ArrayList<>();
    handlers = ActorUtils.findActorHandlers(actor, true);
    for (i = 0; i < handlers.size(); i++)
      findCallableActors(handlers.get(i), result);

    return result;
  }

  /**
   * Checks a control actor's children whether they contain the multi-view actor
   * that we're looking for.
   *
   * @param handler	the handler to check
   * @param name	the name of the callable actor
   * @return		the callable actor or null if not found
   */
  public Actor findMultiView(ActorHandler handler, CallableActorReference name) {
    Actor			result;
    int				i;
    Actor			extActor;
    ExternalActorHandler 	extHandler;

    result = null;

    for (i = 0; i < handler.size(); i++) {
      if (handler.get(i) instanceof AbstractMultiView) {
        if (handler.get(i).getName().equals(name.getValue()))
          result = handler.get(i);
        if (result != null)
	  break;
      }
      else if (handler.get(i) instanceof ExternalActorHandler) {
	extHandler = (ExternalActorHandler) handler.get(i);
	if (extHandler.getExternalActor() instanceof ActorHandler) {
	  extActor = extHandler.getExternalActor();
	  if (extActor instanceof AbstractMultiView) {
            if (handler.get(i).getName().equals(name.getValue()))
              result = handler.get(i);
          }
	  else {
            result = findMultiView((ActorHandler) extActor, name);
          }
	  if (result != null)
	    break;
	}
      }
    }

    return result;
  }

  /**
   * Tries to find the referenced multi-view actor. First all possible actor
   * handlers are located recursively (up to the root) that allow also
   * singletons. This list of actors is then searched for the multi-view actor.
   *
   * @param actor	the actor to start from
   * @param name	the name of the callable actor
   * @return		the callable actor or null if not found
   * @see		ActorUtils#findActorHandlers(Actor, boolean)
   */
  public Actor findMultiViewRecursive(Actor actor, CallableActorReference name) {
    Actor		result;
    List<ActorHandler>	handlers;
    int			i;

    result   = null;
    handlers = ActorUtils.findActorHandlers(actor, true);
    for (i = 0; i < handlers.size(); i++) {
      result = findMultiView(handlers.get(i), name);
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
   * @param name	the callable actor referenced
   * @param start	where the search starts from
   * @param callable	the callable actor (source or transformer) to use
   * @param errors	for storing any errors
   * @return		the setup, null if not found or didn't match class
   */
  protected static Object getSetup(Class cls, CallableActorReference name, Actor start, AbstractCallableActor callable, MessageCollection errors) {
    Object	result;
    Actor	actor;
    Token	token;
    String	msg;

    result = null;

    callable.setCallableName(name);
    callable.setParent(start.getParent());
    msg = callable.setUp();
    if (msg == null) {
      msg   = callable.execute();
      token = ((OutputProducer) callable).output();
      if (token != null) {
	result = token.getPayload();
	if (cls != null) {
	  if (!cls.isInstance(result))
	    result = null;
	}
	else {
	  errors.add("No payload in token obtained from " + name);
	}
	actor = callable.getCallableActor();
	callable.wrapUp();
	callable.cleanUp();
	// fix wrapUp/cleanUp of callable actor, e.g., re-registering variable listeners
	if (actor != null)
	  actor.setUp();
      }
      else {
	if (msg != null)
	  errors.add(msg);
      }
    }
    else {
      errors.add(msg);
    }

    return result;
  }

  /**
   * Returns the setup obtained from the callable actor, source or transformer.
   * In the latter case, the transformer must output a setup if no input
   * token is provided.
   *
   * @param cls		the class that the output must match, null to ignore
   * @param name	the callable actor referenced
   * @param start	where the search starts from
   * @param errors	for storing any errors
   * @return		the setup, null if not found or didn't match class
   */
  public static Object getSetup(Class cls, CallableActorReference name, Actor start, MessageCollection errors) {
    Object	result;
    
    // source?
    result = getSetupFromSource(cls, name, start, errors);
    if (result == null)
      errors.add("Failed to obtain setup from '" + name + "' (treated as source)");
    
    // transformer?
    if (result == null) {
      result = getSetupFromTransformer(cls, name, start, errors);
      if (result == null)
	errors.add("\nFailed to obtain setup from '" + name + "' (treated as transformer)");
    }

    if (result != null)
      errors.clear();
    
    return result;
  }

  /**
   * Returns the setup obtained from the callable source.
   *
   * @param cls		the class that the output must match, null to ignore
   * @param name	the callable actor referenced
   * @param start	where the search starts from
   * @param errors	for storing any errors
   * @return		the setup, null if not found or didn't match class
   */
  public static Object getSetupFromSource(Class cls, CallableActorReference name, Actor start, MessageCollection errors) {
    return getSetup(cls, name, start, new CallableSource(), errors);
  }

  /**
   * Returns the setup obtained from the callable transformer.
   * The transformer must output a setup if no input token is provided.
   *
   * @param cls		the class that the output must match, null to ignore
   * @param name	the callable actor referenced
   * @param start	where the search starts from
   * @return		the setup, null if not found or didn't match class
   */
  public static Object getSetupFromTransformer(Class cls, CallableActorReference name, Actor start, MessageCollection errors) {
    return getSetup(cls, name, start, new CallableTransformer(), errors);
  }
  
  /**
   * Returns all {@link CallableActors} instances that can be located in the flow
   * 
   * @param flow	the flow to use
   * @return		the {@link CallableActors} instances
   */
  public static List<Actor> findAllCallableActors(Actor flow) {
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
  public static CallableActors createCallableActors(Actor actor, boolean insert) {
    CallableActors		result;
    List<ActorHandler>		callable;
    int				i;
    Actor			root;
    
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
	((MutableActorHandler) root).add(0, result);
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
  public static Actor createCallableActor(Actor actor) {
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
