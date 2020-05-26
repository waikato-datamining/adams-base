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
 * ActorUtils.java
 * Copyright (C) 2009-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.ClassLister;
import adams.core.DateUtils;
import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.Variables;
import adams.core.VariablesHandler;
import adams.core.classmanager.ClassManager;
import adams.core.io.FileUtils;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.NestedConsumer;
import adams.core.option.NestedProducer;
import adams.core.option.OptionHandler;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.core.option.OptionTraverserWithResult;
import adams.data.io.input.DefaultFlowReader;
import adams.data.io.input.FlowReader;
import adams.data.io.output.DefaultFlowWriter;
import adams.data.io.output.FlowWriter;
import adams.env.Environment;
import adams.env.Modules;
import adams.flow.control.Flow;
import adams.flow.control.Sequence;
import adams.flow.control.SubProcess;
import adams.flow.core.actorfilter.AcceptAll;
import adams.flow.core.actorfilter.ActorFilter;
import adams.flow.core.actorfilter.ExactMatch;
import adams.flow.processor.ActorProcessor;
import adams.flow.processor.CheckProcessor;
import adams.flow.processor.CheckStorageUsage;
import adams.flow.processor.CheckVariableUsage;
import adams.flow.processor.CleanUpProcessor;
import adams.flow.processor.MultiProcessor;
import adams.flow.processor.RemoveDisabledActors;
import adams.flow.source.SequenceSource;
import adams.flow.standalone.Standalones;
import adams.flow.template.ActorTemplateUser;
import adams.flow.transformer.CallableTransformer;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.chooser.FlowFileChooser;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.core.FlowAwarePaintlet;
import adams.gui.visualization.core.PaintablePanel;
import adams.gui.visualization.core.Paintlet;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Helper class for actors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ActorUtils {

  /** the variable for the full flow filename. */
  public final static String FLOW_FILENAME_LONG = "flow_filename_long";

  /** the variable for the short flow filename. */
  public final static String FLOW_FILENAME_SHORT = "flow_filename_short";

  /** the variable for the flow restoration filename (long filename, but with .props extension). */
  public final static String FLOW_FILENAME_RESTORE = "flow_filename_restore";

  /** the variable for the directory the flow is located in. */
  public final static String FLOW_DIR = "flow_dir";

  /** the variable for the flow ID. */
  public final static String FLOW_ID = "flow_id";

  /** the variable for the start timestamp of the flow. */
  public final static String FLOW_START_TIMESTAMP = "flow_start_timestamp";

  /** the variable for the headless flag. */
  public final static String IS_HEADLESS = "is_headless";

  /** the variable for the gui flag. */
  public final static String HAS_GUI = "has_gui";

  /** the variable for the project name. */
  public final static String PROJECT_NAME = "project_name";

  /** the variable for the project home. */
  public final static String PROJECT_HOME = "project_home";

  /** the variable for the project modules (comma-separated list). */
  public final static String PROJECT_MODULES = "project_modules";

  /** programmatically set variables. */
  public final static String[] PROGRAMMATIC_VARIABLES = {
    FLOW_FILENAME_LONG,
    FLOW_FILENAME_SHORT,
    FLOW_FILENAME_RESTORE,
    FLOW_DIR,
    FLOW_ID,
    FLOW_START_TIMESTAMP,
    IS_HEADLESS,
    HAS_GUI,
    PROJECT_NAME,
    PROJECT_HOME,
    PROJECT_MODULES,
  };

  /** programmatically set variables. */
  public final static Map<String,String> PROGRAMMATIC_VARIABLES_HELP = new HashMap<>();
  static {
    PROGRAMMATIC_VARIABLES_HELP.put(FLOW_FILENAME_LONG, "The absolute file name of the flow (flow must be saved)");
    PROGRAMMATIC_VARIABLES_HELP.put(FLOW_FILENAME_SHORT, "The file name of the flow without path (flow must be saved)");
    PROGRAMMATIC_VARIABLES_HELP.put(FLOW_FILENAME_RESTORE, "The absolute file name of the flow restoration file (like " + FLOW_FILENAME_LONG + " but with .props extension; flow must be saved)");
    PROGRAMMATIC_VARIABLES_HELP.put(FLOW_DIR, "The absolute directory the flow is stored in (flow must be saved)");
    PROGRAMMATIC_VARIABLES_HELP.put(FLOW_ID, "The flow's unique ID");
    PROGRAMMATIC_VARIABLES_HELP.put(FLOW_START_TIMESTAMP, "The timestamp the flow started (format: " + DateUtils.getTimestampFormatterMsecs().toPattern() + ")");
    PROGRAMMATIC_VARIABLES_HELP.put(IS_HEADLESS, "Whether the environment is headless (eg server process)");
    PROGRAMMATIC_VARIABLES_HELP.put(HAS_GUI, "Whether the flow can use a GUI");
    PROGRAMMATIC_VARIABLES_HELP.put(PROJECT_NAME, "The name of the project (usually ADAMS)");
    PROGRAMMATIC_VARIABLES_HELP.put(PROJECT_HOME, "The absolute directory name of the project home directory (eg $HOME/.adams)");
    PROGRAMMATIC_VARIABLES_HELP.put(PROJECT_MODULES, "Comma-separated list of modules that this application consists of");
  }

  /** functional type: primitive. */
  public final static String FUNCTIONAL_PRIMITIVE = "primitive";

  /** functional type: handler. */
  public final static String FUNCTIONAL_HANDLER = "handler";

  /** procedural type: standalone. */
  public final static String PROCEDURAL_STANDALONE = "standalone";

  /** procedural type: source. */
  public final static String PROCEDURAL_SOURCE = "source";

  /** procedural type: transformer. */
  public final static String PROCEDURAL_TRANSFORMER = "transformer";

  /** procedural type: sink. */
  public final static String PROCEDURAL_SINK = "sink";

  /** the environment variable to enable lenient checking for invalid variables. */
  public final static String ENV_INVALID_VARIABLES_LENIENT = "INVALID_VARIABLES_LENIENT";

  /** whether to be lenient with invalid variables. */
  protected static Boolean m_InvalidVariablesLenient;

  /** the debugging level. */
  private final static Logger LOGGER = LoggingHelper.getConsoleLogger(ActorUtils.class);

  /**
   * Returns whether logging is enabled.
   *
   * @return		true if enabled
   */
  protected static boolean isLoggingEnabled() {
    return LoggingHelper.isAtLeast(LOGGER.getLevel(), Level.INFO);
  }

  /**
   * Enumerates all children of the given actor (depth-first search).
   *
   * @param actor	the actor to obtain all children from
   * @param children	for collecting the children
   * @param filter	for filtering the classes
   */
  protected static void enumerate(Actor actor, List<Actor> children, ActorFilter filter) {
    int			i;
    ActorHandler	handler;
    Actor		other;

    if (actor == null)
      return;
    
    if (actor instanceof ActorHandler) {
      handler = (ActorHandler) actor;
      for (i = 0; i < handler.size(); i++) {
	if (filter.accept(handler.get(i)))
	  children.add(handler.get(i));
	enumerate(handler.get(i), children, filter);
      }
    }
    if (ClassLocator.hasInterface(CallableActorUser.class, actor.getClass())) {
      other = ((CallableActorUser) actor).getCallableActor();
      enumerate(other, children, filter);
    }
    if (ClassLocator.hasInterface(InternalActorHandler.class, actor.getClass())) {
      other = ((InternalActorHandler) actor).getInternalActor();
      enumerate(other, children, filter);
    }
    if (ClassLocator.hasInterface(ExternalActorHandler.class, actor.getClass())) {
      other = ((ExternalActorHandler) actor).getExternalActor();
      enumerate(other, children, filter);
    }
  }

  /**
   * Enumerates all children of the given actor (depth-first search).
   *
   * @param actor	the actor to obtain all children from
   * @return		all the children (if any)
   */
  public static List<Actor> enumerate(Actor actor) {
    return enumerate(actor, (Class[]) null);
  }

  /**
   * Enumerates all children of the given actor (depth-first search).
   *
   * @param actor	the actor to obtain all children from
   * @param filter	the classes of actors to only enumerate, null for no filtering
   * @return		all the children (if any)
   */
  public static List<Actor> enumerate(Actor actor, Class[] filter) {
    ArrayList<Actor>	result;
    ActorFilter		actorFilter;

    result    = new ArrayList<>();
    if (filter != null)
      actorFilter = new ExactMatch(filter);
    else
      actorFilter = new AcceptAll();

    enumerate(actor, result, actorFilter);

    return result;
  }

  /**
   * Enumerates all children of the given actor (depth-first search).
   *
   * @param actor	the actor to obtain all children from
   * @param filter	the filter to use
   * @return		all the children (if any)
   */
  public static List<Actor> enumerate(Actor actor, ActorFilter filter) {
    ArrayList<Actor>	result;

    result = new ArrayList<>();
    enumerate(actor, result, filter);

    return result;
  }

  /**
   * Replaces any occurrence of the object to find with the replacement. Can
   * be done recursive. Performs the replacement also in arrays and nested
   * OptionHandlers.
   *
   * @param handler	the OptionHandler to process
   * @param find	the object to look for
   * @param replace	the replacement
   * @param recursive	if true then all children (if any) of the actor will
   * 			be processed as well
   * @return		the number of replacements
   */
  public static int replace(OptionHandler handler, Comparable find, Comparable replace, boolean recursive) {
    return replace(handler, find, replace, recursive, new HashSet<Class>());
  }

  /**
   * Replaces any occurrence of the object to find with the replacement. Can
   * be done recursive. Performs the replacement also in arrays and nested
   * OptionHandlers.
   *
   * @param handler	the OptionHandler to process
   * @param find	the object to look for
   * @param replace	the replacement
   * @param recursive	if true then all children (if any) of the actor will
   * 			be processed as well
   * @param excluded	the base classes to skip
   * @return		the number of replacements
   */
  public static int replace(OptionHandler handler, Comparable find, Comparable replace, boolean recursive, HashSet<Class> excluded) {
    int				result;
    boolean			updated;
    List<AbstractOption>	options;
    int				i;
    int				n;
    PropertyDescriptor		desc;
    Object			value;
    AbstractArgumentOption	argoption;

    result = 0;

    options = handler.getOptionManager().getOptionsList();
    for (i = 0; i < options.size(); i++) {
      updated = false;

      // superclass?
      if (!(options.get(i) instanceof AbstractArgumentOption))
	continue;
      argoption = (AbstractArgumentOption) options.get(i);

      // skipped?
      if (excluded.contains(argoption.getBaseClass()))
	continue;

      // correct class?
      if (!argoption.getBaseClass().equals(find.getClass())) {
	// nested OptionHandler?
	if (recursive && (ClassLocator.hasInterface(OptionHandler.class, argoption.getBaseClass()))) {
	  // get current value
	  desc = options.get(i).getDescriptor();
	  try {
	    value = desc.getReadMethod().invoke(handler);
	  }
	  catch (Exception e) {
	    e.printStackTrace();
	    value = null;
	  }
	  if (value == null)
	    continue;

	  // check nested OptionHandler(s)
	  if (argoption.isMultiple()) {
	    for (n = 0; n < Array.getLength(value); n++)
	      result += replace((OptionHandler) Array.get(value, n), find, replace, recursive);
	  }
	  else {
	    result += replace((OptionHandler) value, find, replace, recursive);
	  }
	}
	continue;
      }

      // get current value
      desc = options.get(i).getDescriptor();
      try {
	value = desc.getReadMethod().invoke(handler);
      }
      catch (Exception e) {
	e.printStackTrace();
	value = null;
      }
      if (value == null)
	continue;

      // the value we're looking for?
      if (argoption.isMultiple()) {
	for (n = 0; n < Array.getLength(value); n++) {
	  if (Array.get(value, n).equals(find)) {
	    Array.set(value, n, ClassManager.getSingleton().deepCopy(replace));
	    updated = true;
	    result++;
	  }
	}
      }
      else {
	if (value.equals(find)) {
	  value   = ClassManager.getSingleton().deepCopy(replace);
	  updated = true;
	  result++;
	}
      }

      // update values
      if (updated) {
	try {
	  desc.getWriteMethod().invoke(handler, value);
	}
	catch (Exception e) {
	  e.printStackTrace();
	}
      }
    }

    return result;
  }

  /**
   * Updates the name of the actor to make it unique among all the other
   * actors of the given handler.
   *
   * @param actor	the actor which name needs to be made unique
   * @param handler	the handler to take into account
   * @param index	the index that this actor will be placed
   * @return		true if the name was updated
   */
  public static boolean uniqueName(Actor actor, ActorHandler handler, int index) {
    HashSet<String>	names;
    int			i;

    // get all names
    names = new HashSet<>();
    for (i = 0; i < handler.size(); i++) {
      if (i != index)
	names.add(handler.get(i).getName());
    }

    return uniqueName(actor, names);
  }

  /**
   * Updates the name of the actor to make it unique among all the other
   * specified names.
   *
   * @param actor	the actor which name needs to be made unique
   * @param names	the existing names
   * @return		true if the name was updated
   */
  public static boolean uniqueName(Actor actor, Set<String> names) {
    // don't remove "tail" as it can change actor names in flow when loading/
    // debugging, creating bizarre jumping around behaviour
    return uniqueName(actor, names, false);
  }

  /**
   * Updates the name of the actor to make it unique among all the other
   * specified names.
   *
   * @param actor	the actor which name needs to be made unique
   * @param names	the existing names
   * @param tailRemoval whether the "tail" (eg ' (2)') can be removed to determine basename
   * @return		true if the name was updated
   */
  public static boolean uniqueName(Actor actor, Set<String> names, boolean tailRemoval) {
    boolean		result;
    String		name;
    String		baseName;
    int			i;

    // create unique name
    baseName = actor.getName().trim();
    if (tailRemoval)
      baseName = baseName.replaceAll(" \\([0-9]+\\)$", "").trim();

    i = 1;
    do {
      if (i == 1)
	name = baseName;
      else
	name = baseName + " (" + i + ")";
      i++;
    }
    while (names.contains(name));

    result = (!name.equals(actor.getName()));

    // update name
    if (result)
      actor.setName(name);

    return result;
  }

  /**
   * Updates the names of the actors to make then unique among all of them.
   * NB: any suffix matching "-XYZ" gets stripped first.
   *
   * @param actors	the actors which names need to be made unique
   * @return		true if at least one name was updated
   */
  public static boolean uniqueNames(Actor[] actors) {
    boolean		result;
    HashSet<String>	names;
    int			i;

    result = false;

    names = new HashSet<>();
    for (i = 0; i < actors.length; i++) {
      if (i > 0)
	result = uniqueName(actors[i], names) || result;
      names.add(actors[i].getName());
    }

    return result;
  }

  /**
   * Returns a list of actor handlers, starting from the current note (excluded).
   * The search goes up in the actor hierarchy, up to the root (i.e., the last
   * item in the returned list will be most likely a "Flow" actor).
   *
   * @param actor	the actor to start the search from
   * @return		the list of actor handlers found along the path to
   * 			the root actor
   */
  public static List<ActorHandler> findActorHandlers(Actor actor) {
    return findActorHandlers(actor, false);
  }

  /**
   * Returns a list of actor handlers, starting from the current note (excluded).
   * The search goes up in the actor hierarchy, up to the root (i.e., the last
   * item in the returned list will be most likely a "Flow" actor).
   *
   * @param actor			the actor to start the search from
   * @param mustAllowStandalones	whether the handler must allow standalones
   * @return				the list of actor handlers found along the path
   * 					to the root actor
   */
  public static List<ActorHandler> findActorHandlers(Actor actor, boolean mustAllowStandalones) {
    return findActorHandlers(actor, mustAllowStandalones, false);
  }

  /**
   * Returns a list of actor handlers, starting from the current note (excluded).
   * The search goes up in the actor hierarchy, up to the root (i.e., the last
   * item in the returned list will be most likely a "Flow" actor).
   *
   * @param actor			the actor to start the search from
   * @param mustAllowStandalones	whether the handler must allow standalones
   * @param includeSameLevel		allows adding of actor handlers that are on
   * 					the same level as the current actor, but
   * 					have a lower index in the parent
   * @return				the list of actor handlers found along the path
   * 					to the root actor
   */
  public static List<ActorHandler> findActorHandlers(Actor actor, boolean mustAllowStandalones, boolean includeSameLevel) {
    ArrayList<ActorHandler>	result;
    Actor			parent;
    Actor			child;
    int				index;
    int				i;
    ActorHandler		handler;
    ActorHandler		subhandler;
    ExternalActorHandler	extActor;
    Actor			root;
    ArrayList<String>		list;

    result = new ArrayList<>();

    root   = actor.getRoot();
    child  = actor;
    parent = actor.getParent();
    while (parent != null) {
      if (parent instanceof ActorHandler) {
	handler = (ActorHandler) parent;
	if (includeSameLevel) {
	  index = handler.indexOf(child.getName());
	  for (i = index - 1; i >= 0; i--) {
	    subhandler = null;
	    if (handler.get(i) instanceof ExternalActorHandler) {
	      extActor = (ExternalActorHandler) handler.get(i);
	      if (extActor.getExternalActor() instanceof ActorHandler)
		subhandler = (ActorHandler) extActor.getExternalActor();
	    }
	    else if (handler.get(i) instanceof ActorHandler) {
	      subhandler = (ActorHandler) handler.get(i);
	    }
	    if (subhandler == null)
	      continue;
	    if (mustAllowStandalones) {
	      if (subhandler.getActorHandlerInfo().canContainStandalones())
		result.add(subhandler);
	    }
	    else {
	      result.add(subhandler);
	    }
	  }
	}
	if (mustAllowStandalones) {
	  if (handler.getActorHandlerInfo().canContainStandalones())
	    result.add(handler);
	}
	else {
	  result.add(handler);
	}
      }

      if (parent == root) {
	parent = null;
      }
      else {
	child  = parent;
	parent = parent.getParent();
      }
    }

    if (isLoggingEnabled()) {
      list = new ArrayList<>();
      for (ActorHandler h: result)
	list.add(h.getClass().getName() + "/" + h.getFullName());
      LOGGER.fine("Actor handlers: " + list + "\n" + LoggingHelper.getStackTrace(20));
    }

    return result;
  }

  /**
   * Checks whether this actor is a standalone.
   *
   * @param actor	the actor to check
   * @return		true if standalone
   */
  public static boolean isStandalone(Actor actor) {
    return (!(actor instanceof InputConsumer)) && (!(actor instanceof OutputProducer));
  }

  /**
   * Checks whether this actor is a source (output).
   *
   * @param actor	the actor to check
   * @return		true if source
   */
  public static boolean isSource(Actor actor) {
    return (!(actor instanceof InputConsumer)) && (actor instanceof OutputProducer);
  }

  /**
   * Checks whether this actor is a sink (input).
   *
   * @param actor	the actor to check
   * @return		true if sink
   */
  public static boolean isSink(Actor actor) {
    return (actor instanceof InputConsumer) && (!(actor instanceof OutputProducer));
  }

  /**
   * Checks whether this actor is a transformer (input/output).
   *
   * @param actor	the actor to check
   * @return		true if transformer
   */
  public static boolean isTransformer(Actor actor) {
    return (actor instanceof InputConsumer) && (actor instanceof OutputProducer);
  }

  /**
   * Checks whether this actor is a control actor.
   *
   * @param actor	the actor to check
   * @return		true if control actor
   */
  public static boolean isControlActor(Actor actor) {
    return (actor instanceof ControlActor);
  }

  /**
   * Checks whether this actor is an actor handler.
   *
   * @param actor	the actor to check
   * @return		true if actor handler
   */
  public static boolean isActorHandler(Actor actor) {
    return (actor instanceof ActorHandler);
  }

  /**
   * Checks whether the actor or its sub-actors are interactive ones.
   * Does not check any callable actors. Cannot check any internally used actors
   * if they are either not exposed (like ExternalActorHandler) or not yet
   * executed/generated, so no guaranteed outcome.
   *
   * @param actor	the actor to check
   * @return		true if at least one (active) interactive actor detected
   */
  public static boolean isInteractive(Actor actor) {
    boolean		result;
    ActorHandler	handler;
    int			i;

    if (actor == null)
      return false;

    result = false;

    if (!actor.getSkip()) {
      // interactive actors
      if (actor instanceof AutomatableInteraction) {
	result = !((AutomatableInteraction) actor).isNonInteractive();
      }
      else if (actor instanceof OptionalPasswordPrompt) {
	result = ((OptionalPasswordPrompt) actor).getPromptForPassword();
      }
      else if (actor instanceof InteractiveActor) {
	result = true;
      }

      // uses flows internally?
      if (!result) {
        if (actor instanceof ActorTemplateUser) {
          result = ((ActorTemplateUser) actor).getTemplate().isInteractive();
	}
	else if (actor instanceof ExternalActorHandler) {
	  result = isInteractive(((ExternalActorHandler) actor).getExternalActor());
	}
	else if (actor instanceof InternalActorHandler) {
	  result = isInteractive(((InternalActorHandler) actor).getInternalActor());
	}
      }

      // handles other actors?
      if (!result) {
	if (actor instanceof ActorHandler) {
	  handler = (ActorHandler) actor;
	  for (i = 0; i < handler.size(); i++) {
	    if (handler.get(i).getSkip())
	      continue;
	    result = isInteractive(handler.get(i));
	    if (result)
	      break;
	  }
	}
      }
    }

    return result;
  }

  /**
   * Writes the actor to a file.
   *
   * @param filename	the file to write to
   * @param actor	the actor to write
   * @return		true if successfully written
   */
  public static boolean write(String filename, Actor actor) {
    boolean		result;
    FlowWriter 		writer;

    writer = FlowFileChooser.writerForFile(new File(filename));
    if (writer == null)
      writer = new DefaultFlowWriter();
    result = writer.write(actor, filename);

    return result;
  }

  /**
   * Reads an actor from a file.
   *
   * @param filename	the file to read the actor
   * @return		the actor or null in case of an error
   */
  public static Actor read(String filename) {
    return read(filename, null);
  }

  /**
   * Reads an actor from a file.
   *
   * @param filename	the file to read the actor
   * @param errors	for storing (potential) errors, ignored if null
   * @return		the actor or null in case of an error
   */
  public static Actor read(String filename, MessageCollection errors) {
    return read(filename, errors, null);
  }

  /**
   * Reads an actor from a file.
   *
   * @param filename	the file to read the actor
   * @param errors	for storing (potential) errors, ignored if null
   * @param warnings	for storing (potential) warnings, ignored if null
   * @return		the actor or null in case of an error
   */
  public static Actor read(String filename, MessageCollection errors, MessageCollection warnings) {
    Actor		result;
    FlowReader 		reader;

    reader = FlowFileChooser.readerForFile(new File(filename));
    if (reader == null)
      reader = new DefaultFlowReader();
    result = reader.readActor(filename);

    // transfer errors/warnings
    if (errors != null)
      errors.addAll(reader.getErrors());
    if (warnings != null)
      warnings.addAll(reader.getWarnings());

    return result;
  }

  /**
   * Locates callable transformers.
   *
   * @param actor	the actor (and its sub-actors) to check
   * @return		the names of the callable actors referenced and how
   * 			often they were referenced
   */
  public static Hashtable<String,Integer> findCallableTransformers(Actor actor) {
    List<Actor>		actors;
    Hashtable<String,Integer>	count;
    String			name;

    actors = enumerate(actor);
    count  = new Hashtable<>();
    for (Actor current: actors) {
      if (current.getSkip())
	continue;
      if (!(current instanceof CallableTransformer))
	continue;
      name = ((CallableTransformer) current).getCallableName().toString();
      if (!count.containsKey(name))
	count.put(name, 1);
      else
	count.put(name, count.get(name) + 1);
    }

    return count;
  }

  /**
   * Checks an actor handler's children whether they contain the actor type
   * we're looking for.
   *
   * @param handler	the actor handler to check
   * @param type	the type of actor to find the closest for
   * @return		the closest actor or null if not found
   */
  protected static Actor findClosestType(ActorHandler handler, Class type) {
    Actor			result;
    int				i;
    ExternalActorHandler	external;

    result = null;

    for (i = 0; i < handler.size(); i++) {
      if (type.isInstance(handler.get(i)) && !handler.get(i).getSkip()) {
	result = handler.get(i);
	break;
      }
      else if (handler.get(i) instanceof ExternalActorHandler) {
	external = (ExternalActorHandler) handler.get(i);
	if (external.getExternalActor() instanceof ActorHandler) {
	  result = findClosestType((ActorHandler) external.getExternalActor(), type);
	  if (result != null)
	    break;
	}
      }
    }

    return result;
  }

  /**
   * Tries to find the closest type in the actor tree, starting with the current
   * actor.
   *
   * @param actor		the actor to start from
   * @param type		the type of actor to find the closest for
   * @return			the closest actor or null if not found
   */
  public static Actor findClosestType(Actor actor, Class type) {
    return findClosestType(actor, type, false);
  }

  /**
   * Tries to find the closest type in the actor tree, starting with the current
   * actor.
   *
   * @param actor		the actor to start from
   * @param type		the type of actor to find the closest for
   * @param includeSameLevel	whether to include actor handlers that are on
   * 				the same level as the actor, but with a lower
   * 				index in the parent
   * @return			the closest actor or null if not found
   */
  public static Actor findClosestType(Actor actor, Class type, boolean includeSameLevel) {
    Actor			result;
    List<ActorHandler>		handlers;
    int				i;

    result   = null;
    handlers = ActorUtils.findActorHandlers(actor, true, includeSameLevel);
    for (i = 0; i < handlers.size(); i++) {
      // check handlers themselves
      if (type.isInstance(handlers.get(i)) && !handlers.get(i).getSkip()) {
	result = handlers.get(i);
	break;
      }
      // check children of handlers
      result = findClosestType(handlers.get(i), type);
      if (result != null)
	break;
    }

    return result;
  }

  /**
   * Checks an actor handler's children whether they contain the actor type
   * we're looking for.
   *
   * @param handler	the actor handler to check
   * @param type	the type of actor to find the closest for
   * @return		the closest actors, empty list if none found
   */
  protected static List<Actor> findClosestTypes(ActorHandler handler, Class type) {
    List<Actor>			result;
    int				i;
    ExternalActorHandler	external;

    result = new ArrayList<>();

    for (i = 0; i < handler.size(); i++) {
      if (type.isInstance(handler.get(i)) && !handler.get(i).getSkip()) {
	result.add(handler.get(i));
      }
      else if (handler.get(i) instanceof ExternalActorHandler) {
	external = (ExternalActorHandler) handler.get(i);
	if (external.getExternalActor() instanceof ActorHandler)
	  result.addAll(findClosestTypes((ActorHandler) external.getExternalActor(), type));
      }
    }

    return result;
  }

  /**
   * Tries to find the closest types in the actor tree, starting with the current
   * actor.
   *
   * @param actor		the actor to start from
   * @param type		the type of actor to find the closest for
   * @return			the closest actors or empty list if not found
   */
  public static List<Actor> findClosestTypes(Actor actor, Class type) {
    return findClosestTypes(actor, type, false);
  }

  /**
   * Tries to find the closest types in the actor tree, starting with the current
   * actor.
   *
   * @param actor		the actor to start from
   * @param type		the type of actor to find the closest for
   * @param includeSameLevel	whether to include actor handlers that are on
   * 				the same level as the actor, but with a lower
   * 				index in the parent
   * @return			the closest actors or empty list if not found
   */
  public static List<Actor> findClosestTypes(Actor actor, Class type, boolean includeSameLevel) {
    List<Actor>			result;
    List<ActorHandler>		handlers;
    int				i;

    result   = new ArrayList<>();
    handlers = ActorUtils.findActorHandlers(actor, true, includeSameLevel);
    for (i = 0; i < handlers.size(); i++) {
      // check handlers themselves
      if (type.isInstance(handlers.get(i)) && !handlers.get(i).getSkip()) {
	result.add(handlers.get(i));
	break;
      }
      // check children of handlers
      result.addAll(findClosestTypes(handlers.get(i), type));
      if (result.size() > 0)
	break;
    }

    return result;
  }

  /**
   * Determines the initial location of the window.
   *
   * @param window	the window to determine the location for
   * @param x		the position (-1: left, -2: middle, -3: right)
   * @param y		the position (-1: top, -2: middle, -3: bottom)
   * @return		the position
   */
  public static Point determineLocation(Window window, int x, int y) {
    Dimension	size;

    size   = window.getSize();
    if ((size.getWidth() == 0) || (size.getHeight() == 0))
      size = window.getPreferredSize();

    return determineLocation(window.getGraphicsConfiguration(), size, x, y);
  }

  /**
   * Determines the initial location of the window.
   *
   * @param gc		the graphics configuration
   * @param size	the size of the window
   * @param x		the position (-1: left, -2: middle, -3: right)
   * @param y		the position (-1: top, -2: middle, -3: bottom)
   * @return		the position
   */
  public static Point determineLocation(GraphicsConfiguration gc, Dimension size, int x, int y) {
    Point			result;
    int				actX;
    int				actY;
    AbstractApplicationFrame	main;
    int				diffHeight;
    Rectangle			bounds;

    result = new Point();
    bounds = GUIHelper.getScreenBounds(gc);

    // X
    if (x == -1)
      actX = bounds.x;
    else if (x == -2)
      actX = bounds.x + (int) ((bounds.width - size.getWidth()) / 2);
    else if (x == -3)
      actX = bounds.x + (int) (bounds.width - size.getWidth());
    else
      actX = bounds.x + x;

    // Y
    main       = Environment.getInstance().getApplicationFrame();
    diffHeight = ((main != null) ? main.getSize().height : 0);
    if (y == -1)
      actY = bounds.y + diffHeight;
    else if (y == -2)
      actY = bounds.y + (int) ((bounds.height - size.getHeight() - diffHeight) / 2);
    else if (y == -3)
      actY = bounds.y + (int) (bounds.height - size.getHeight());
    else
      actY = bounds.y + y;

    result.setLocation(actX, actY);

    return result;
  }

  /**
   * Determines the height of the window.
   *
   * @param window	the frame/dialog to determine the location for
   * @param x		the position (-1: left, -2: middle, -3: right)
   * @param y		the position (-1: top, -2: middle, -3: bottom)
   * @param width	the width (0: as is, -1: maximum width)
   * @param height	the width (0: as is, -1: maximum height)
   * @return		the size
   */
  public static Dimension determineSize(Window window, int x, int y, int width, int height) {
    Dimension	result;
    Dimension	size;
    int		actWidth;
    int		actHeight;
    Rectangle	bounds;

    size = window.getSize();
    if ((size.getWidth() == 0) || (size.getHeight() == 0))
      size = window.getPreferredSize();
    bounds = GUIHelper.getScreenBounds(window);

    if (width == 0)
      actWidth = size.width;
    else if (width == -1)
      actWidth = (x > -1) ? bounds.width - x : bounds.width;
    else
      actWidth = width;

    if (height == 0)
      actHeight = size.height;
    else if (height == -1)
      actHeight = (x > -1) ? bounds.height - x : bounds.height;
    else
      actHeight = height;

    result = new Dimension(actWidth, actHeight);

    return result;
  }

  /**
   * Returns the database connection object to use.
   *
   * @param actor	the actor start the search from (towards the root)
   * @param cls		the DatabaseConnection actor class to look for
   * @param defCon	the default database connection, in case none is found
   * 			in the flow
   * @return		the connection object to use
   */
  public static adams.db.AbstractDatabaseConnection getDatabaseConnection(Actor actor, Class cls, adams.db.AbstractDatabaseConnection defCon) {
    Object						closest;
    adams.db.AbstractDatabaseConnection			result;

    closest = ActorUtils.findClosestType(actor, cls, true);
    if (closest != null) {
      if (closest instanceof adams.flow.standalone.AbstractDatabaseConnection) {
	result = ((adams.flow.standalone.AbstractDatabaseConnection) closest).getConnection();
	if (isLoggingEnabled())
	  LOGGER.fine("Database connection found: " + result + "\n" + LoggingHelper.getStackTrace(20));
      }
      else  if (closest instanceof adams.flow.standalone.AbstractDatabaseConnectionProvider) {
	result = ((adams.flow.standalone.AbstractDatabaseConnectionProvider) closest).getConnection();
	if (isLoggingEnabled())
	  LOGGER.fine("Database connection found: " + result + "\n" + LoggingHelper.getStackTrace(20));
      }
      else {
	result = defCon;
	LOGGER.warning("Unhandled actor type '" + closest.getClass().getName() + "', using default connection: " + defCon + "\n" + LoggingHelper.getStackTrace(20));
      }
    }
    else {
      result = defCon;
      if (isLoggingEnabled())
	LOGGER.info("No database connection found, using default: " + defCon + "\n" + LoggingHelper.getStackTrace(20));
    }
    if (!result.isConnected() && result.getConnectOnStartUp()) {
      try {
	result.connect();
      }
      catch (Exception e) {
	LOGGER.log(Level.SEVERE,
	    "Failed to enable database connection (" + cls.getName() + ") for actor " + actor.getFullName() + ":", e);
      }
    }

    return result;
  }

  /**
   * Removes all disabled actors (recursively) from the actor.
   * <br><br>
   * NB: creates a copy of the actor first before removing the disabled actors.
   *
   * @param actor	the actor to process
   * @return		the cleaned up actor (or original, if nothing changed)
   */
  public static Actor removeDisabledActors(Actor actor) {
    Actor			result;
    RemoveDisabledActors	processor;

    processor = new RemoveDisabledActors();
    processor.process(actor);
    result = processor.getModifiedActor();
    if (result == null)
      result = actor;

    return result;
  }

  /**
   * Sets the actors in the handler.
   *
   * @param handler	the handler to update
   * @param actors	the actors to set
   * @return		null if successful, otherwise error message
   */
  protected static String setActors(ActorHandler handler, Actor[] actors) {
    MutableActorHandler	mutable;
    int			i;

    if (handler instanceof MutableActorHandler) {
      mutable = (MutableActorHandler) handler;
      mutable.removeAll();
      for (Actor actor: actors)
        mutable.add(actor);
      return null;
    }
    else {
      if (handler.size() != actors.length)
        return "Actor handler '" + Utils.classToString(handler) + "' expects " + handler.size() + " actors, but provided: " + actors.length;
      for (i = 0; i < actors.length; i++)
        handler.set(i, actors[i]);
      return null;
    }
  }

  /**
   * Ensures that the actors are enclosed in an "instantiable" wrapper.
   *
   * @param actors	the actors to enclose
   * @param suggestion 	the suggested actor handler, can be null
   * @return		the processed actor
   */
  public static Actor createExternalActor(Actor[] actors, ActorHandler suggestion) {
    int			first;
    int			last;
    int			i;
    ActorHandler	handler;
    HashSet<String>	paths;
    ActorPath		path;
    String		msg;

    if (actors.length == 0)
      throw new IllegalArgumentException(
	  "At least one actor must be provided for externalizing!");

    // ensure that all actors have same prefix, i.e., on same level in tree
    paths = new HashSet<>();
    for (Actor actor: actors) {
      path = new ActorPath(actor.getFullName());
      paths.add(path.getParentPath().toString());
      if (paths.size() > 1)
	throw new IllegalArgumentException(
	    "Actor is not on the same level in the same sub-flow!\n" + actor.getFullName());
    }

    first = -1;
    for (i = 0; i < actors.length; i++) {
      if (!actors[i].getSkip()) {
	first = i;
	break;
      }
    }
    if (first == -1)
      throw new IllegalArgumentException(
	  "At least one actor must be enabled!");

    last = -1;
    for (i = actors.length - 1; i >= 0; i--) {
      if (!actors[i].getSkip()) {
	last = i;
	break;
      }
    }

    handler = suggestion;

    // appears as standalone
    if (isStandalone(actors[first]) && isStandalone(actors[last])) {
      if ((handler == null) || !isStandalone(handler))
	handler = new Standalones();
      msg = setActors(handler, actors);
      if (msg != null)
	throw new IllegalStateException(
	  "Failed to set actors in handler '" + Utils.classToString(handler) + "': " + msg);
      return handler;
    }

    // appears as transformer
    if (isTransformer(actors[first]) && isTransformer(actors[last])) {
      if ((handler == null) || !isTransformer(suggestion))
	handler = new SubProcess();
      msg = setActors(handler, actors);
      if (msg != null)
	throw new IllegalStateException(
	  "Failed to set actors in handler '" + Utils.classToString(handler) + "': " + msg);
      return handler;
    }

    // appears as source
    if (isSource(actors[first]) && (actors[last] instanceof OutputProducer)) {
      if ((handler == null) || !isSource(suggestion))
	handler = new SequenceSource();
      msg = setActors(handler, actors);
      if (msg != null)
	throw new IllegalStateException(
	  "Failed to set actors in handler '" + Utils.classToString(handler) + "': " + msg);
      return handler;
    }

    // appears as sink
    if ((actors[first] instanceof InputConsumer) && (isSink(actors[last]))) {
      if ((handler == null) || !isSink(handler))
	handler = new Sequence();
      msg = setActors(handler, actors);
      if (msg != null)
	throw new IllegalStateException(
	  "Failed to set actors in handler '" + Utils.classToString(handler) + "': " + msg);
      return handler;
    }

    throw new IllegalArgumentException(
	"Failed to find suitable actor handler to enclose all actors!");
  }

  /**
   * Tries to locate the actor specified by the path parts.
   *
   * @param parent	the parent to start with
   * @param path	the path elements to traverse (below the parent)
   * @return		the located actor or null if none found
   */
  public static Actor locate(ActorPath path, Actor parent) {
    return locate(path, parent, false, false);
  }

  /**
   * Tries to locate the actor specified by the path parts.
   *
   * @param parent	the parent to start with
   * @param path	the path elements to traverse (below the parent)
   * @param included	whether the actor should be included in the search, rather than searching only below
   * @param quiet	whether to suppress any error messages
   * @return		the located actor or null if none found
   */
  public static Actor locate(ActorPath path, Actor parent, boolean included, boolean quiet) {
    Actor		result;
    ActorHandler	parentHandler;
    Actor		child;
    int			index;
    int			i;

    result = null;
    if (!(parent instanceof ActorHandler))
      return result;

    if (path.getPathCount() == 0) {
      if (!quiet)
	LOGGER.warning("Empty path? " + path);
      return null;
    }

    if (included) {
      if (path.getFirstPathComponent().equals(parent.getName())) {
	if (path.getPathCount() == 1)
	  result = parent;
	else
	  result = locate(path.getChildPath(), parent);
	return result;
      }
    }

    parentHandler = (ActorHandler) parent;
    index         = -1;
    for (i = 0; i < parentHandler.size(); i++) {
      child = parentHandler.get(i);
      if (child.getName().equals(path.getFirstPathComponent())) {
	index = i;
	break;
      }
    }
    if (index != -1) {
      child = parentHandler.get(index);
      if (path.getPathCount() == 1)
	result = child;
      else
	result = locate(path.getChildPath(), child);
    }
    else {
      if (!quiet)
	LOGGER.warning("Malformed path? " + path);
    }

    return result;
  }

  /**
   * Locates the actor in the actor tree based on the specified path.
   *
   * @param path	the path of the node to locate
   * @param root	the root actor start the search
   * @return		the located actor or null if none found
   */
  public static Actor locate(String path, Actor root) {
    return locate(path, root, false, false);
  }

  /**
   * Locates the actor in the actor tree based on the specified path.
   *
   * @param path	the path of the node to locate
   * @param root	the root actor start the search
   * @param included	whether the actor should be included in the search, rather than searching only below
   * @param quiet	whether to suppress any error messages
   * @return		the located actor or null if none found
   */
  public static Actor locate(String path, Actor root, boolean included, boolean quiet) {
    return locate(new ActorPath(path), root, included, quiet);
  }

  /**
   * Checks whether the actor has a source as first actor (can be nested).
   *
   * @param actor	the actor to analyze
   * @return		null if ok, otherwise error message
   */
  public static String checkForSource(Actor actor) {
    return checkForSource(new Actor[]{actor});
  }

  /**
   * Checks whether the actors have a source as first actor (can be nested).
   *
   * @param actors	the actors to analyze
   * @return		null if ok, otherwise error message
   */
  public static String checkForSource(Actor[] actors) {
    String	result;
    int		i;
    Actor	actor;

    result = null;

    for (i = 0; i < actors.length; i++) {
      actor = actors[i];
      if (actor.getSkip())
	continue;
      if (ActorUtils.isStandalone(actor))
	continue;
      if (!ActorUtils.isSource(actor))
	result = "First active, non-standalone actor must be a source, but '" + actor.getName() + "' is not!";
      break;
    }

    return result;
  }

  /**
   * Checks whether an external actor is present in this sub-tree.
   *
   * @param actor	the actor to analyze
   * @return		true if external actor present
   */
  public static boolean checkForExternalActor(Actor actor) {
    boolean	result;
    List<Actor>	actors;

    result = (actor instanceof ExternalActorHandler);

    if (!result) {
      actors = enumerate(actor);
      for (Actor a : actors) {
	if (a instanceof ExternalActorHandler) {
	  result = true;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Checks whether a callable actor user is present in this sub-tree.
   *
   * @param actor	the actor to analyze
   * @return		true if callable actor user present
   */
  public static boolean checkForCallableActorUser(Actor actor) {
    boolean	result;
    List<Actor>	actors;

    result = (actor instanceof CallableActorUser);

    if (!result) {
      actors = enumerate(actor);
      for (Actor a : actors) {
	if (a instanceof CallableActorUser) {
	  result = true;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Cleans up the flow, e.g., removing disabled actors, unused callable actors.
   *
   * @param actor	the flow to clean up
   * @return		null if nothing changed, otherwise the updated flow
   * @see		CleanUpProcessor
   */
  public static Actor cleanUpFlow(Actor actor) {
    Actor			result;
    MultiProcessor		processor;
    String[]			names;
    ActorProcessor[]		procs;
    int				i;

    result = null;

    processor = new MultiProcessor();
    names     = ClassLister.getSingleton().getClassnames(CleanUpProcessor.class);
    procs     = new ActorProcessor[names.length];
    for (i = 0; i < names.length; i++) {
      try {
	procs[i] = (ActorProcessor) ClassManager.getSingleton().forName(names[i]).newInstance();
      }
      catch (Exception e) {
	LOGGER.log(Level.SEVERE,
	    "Failed to instantiate cleanup processor: " + names[i], e);
	return null;
      }
    }
    processor.setSubProcessors(procs);
    processor.process(actor);
    if (processor.isModified())
      result = processor.getModifiedActor();

    return result;
  }

  /**
   * Checks the flow.
   *
   * @param actor	the flow to check
   * @param file	the actor file to use
   * @return		null if all checks passed, otherwise the warnings
   * @see		CheckProcessor
   */
  public static String checkFlow(Actor actor, File file) {
    return checkFlow(actor, true, true, file);
  }

  /**
   * Checks the flow.
   *
   * @param actor	the flow to check
   * @param variables	whether the check variable usage
   * @param storage	whether to check storage usage
   * @param file	the actor file to use
   * @return		null if all checks passed, otherwise the warnings
   * @see		CheckProcessor
   */
  public static String checkFlow(Actor actor, boolean variables, boolean storage, File file) {
    String			result;
    MultiProcessor		processor;
    String[]			names;
    List<ActorProcessor>	procs;
    ActorProcessor		proc;
    int				i;

    // general check
    actor = actor.shallowCopy();
    if (actor instanceof VariablesHandler)
      ActorUtils.updateProgrammaticVariables((VariablesHandler & Actor) actor, file);
    result = actor.setUp();
    actor.destroy();
    
    if (result == null) {
      processor = new MultiProcessor();
      names     = ClassLister.getSingleton().getClassnames(CheckProcessor.class);
      procs     = new ArrayList<>();
      for (i = 0; i < names.length; i++) {
	try {
	  proc = (ActorProcessor) ClassManager.getSingleton().forName(names[i]).newInstance();
	  if (!variables && (proc instanceof CheckVariableUsage))
	    continue;
	  if (!storage && (proc instanceof CheckStorageUsage))
	    continue;
	  procs.add(proc);
	}
	catch (Exception e) {
	  LOGGER.log(Level.SEVERE,
	      "Failed to instantiate check processor: " + names[i], e);
	  return null;
	}
      }
      processor.setSubProcessors(procs.toArray(new ActorProcessor[0]));
      processor.process(actor);
      result = processor.getWarnings();
    }

    return result;
  }

  /**
   * Adds some programmatic variables.
   *
   * @param context	the actor, must be a
   * @param flow	the flow file name
   * @see		#FLOW_FILENAME_LONG
   * @see		#FLOW_FILENAME_SHORT
   * @see		#FLOW_DIR
   * @see		#FLOW_ID
   * @see		#HAS_GUI
   * @see		#IS_HEADLESS
   * @see		#PROJECT_NAME
   * @see		#PROJECT_HOME
   * @see		#PROJECT_MODULES
   * @see		#PROGRAMMATIC_VARIABLES
   */
  public static <T extends Actor & VariablesHandler> void updateProgrammaticVariables(T context, File flow) {
    if (context != null) {
      context.getLocalVariables().set(PROJECT_NAME, Environment.getInstance().getProject());
      context.getLocalVariables().set(PROJECT_HOME, Environment.getInstance().getHome());
      context.getLocalVariables().set(PROJECT_MODULES, Utils.flatten(Modules.getSingleton().getModules(), ","));
      context.getLocalVariables().set(ActorUtils.IS_HEADLESS, "" + GUIHelper.isHeadless());
    }
    if (flow != null) {
      context.getLocalVariables().set(FLOW_DIR, flow.getParentFile().getAbsolutePath());
      context.getLocalVariables().set(FLOW_FILENAME_LONG, flow.getAbsolutePath());
      context.getLocalVariables().set(FLOW_FILENAME_SHORT, flow.getName());
      context.getLocalVariables().set(FLOW_FILENAME_RESTORE, FileUtils.replaceExtension(flow.getAbsolutePath(), ".props"));
    }
    if ((context != null) && (context.getRoot() != null)) {
      context.getLocalVariables().set(ActorUtils.FLOW_ID, (context.getRoot() instanceof Flow) ? "" + ((Flow) context.getRoot()).getFlowID() : "-1");
      context.getLocalVariables().set(ActorUtils.FLOW_START_TIMESTAMP, DateUtils.getTimestampFormatterMsecs().format(new Date()));
      context.getLocalVariables().set(ActorUtils.HAS_GUI, "" + !context.getRoot().isHeadless());
    }
  }

  /**
   * Adds some programmatic variables.
   *
   * @param handler	the handler to add the filenames to
   * @param flow	the flow file name
   * @see		#FLOW_FILENAME_LONG
   * @see		#FLOW_FILENAME_SHORT
   * @see		#FLOW_FILENAME_RESTORE
   * @see		#FLOW_DIR
   * @see		#FLOW_ID
   * @see		#HAS_GUI
   * @see		#IS_HEADLESS
   * @see		#PROJECT_NAME
   * @see		#PROJECT_HOME
   * @see		#PROJECT_MODULES
   * @see		#PROGRAMMATIC_VARIABLES
   */
  public static void updateProgrammaticVariables(VariablesHandler handler, Actor context, File flow) {
    if (handler != null) {
      handler.getLocalVariables().set(PROJECT_NAME, Environment.getInstance().getProject());
      handler.getLocalVariables().set(PROJECT_HOME, Environment.getInstance().getHome());
      handler.getLocalVariables().set(PROJECT_MODULES, Utils.flatten(Modules.getSingleton().getModules(), ","));
      handler.getLocalVariables().set(ActorUtils.IS_HEADLESS, "" + GUIHelper.isHeadless());
    }
    if (flow != null) {
      handler.getLocalVariables().set(FLOW_DIR, flow.getParentFile().getAbsolutePath());
      handler.getLocalVariables().set(FLOW_FILENAME_LONG, flow.getAbsolutePath());
      handler.getLocalVariables().set(FLOW_FILENAME_SHORT, flow.getName());
      handler.getLocalVariables().set(FLOW_FILENAME_RESTORE, FileUtils.replaceExtension(flow.getAbsolutePath(), ".props"));
    }
    if ((handler != null) && (context != null) && (context.getRoot() != null)) {
      handler.getLocalVariables().set(ActorUtils.FLOW_ID, (context.getRoot() instanceof Flow) ? "" + ((Flow) context.getRoot()).getFlowID() : "-1");
      handler.getLocalVariables().set(ActorUtils.FLOW_START_TIMESTAMP, DateUtils.getTimestampFormatterMsecs().format(new Date()));
      handler.getLocalVariables().set(ActorUtils.HAS_GUI, "" + !context.getRoot().isHeadless());
    }
  }

  /**
   * Processes the data using the specified transformer.
   * Automatically sets up, executes, wraps up and cleans up the actor.
   *
   * @param transformer	the transformer to use
   * @param input	the input data
   * @return		the generated output data as list, null if none produced.
   * @throws Exception	if actor is not a transformer or transformation fails
   */
  public static List transform(Actor transformer, Object input) throws Exception {
    ArrayList	result;
    String	msg;

    result = null;

    if (!isTransformer(transformer))
      throw new IllegalArgumentException("Actor is not a transformer!");

    msg = transformer.setUp();
    if (msg != null) {
      transformer.cleanUp();
      throw new Exception("Setup of actor failed: " + msg);
    }

    ((InputConsumer) transformer).input(new Token(input));

    msg = transformer.execute();
    if (msg != null) {
      transformer.cleanUp();
      throw new Exception("Execution of actor failed: " + msg);
    }

    while (((OutputProducer) transformer).hasPendingOutput()) {
      if (result == null)
	result = new ArrayList();
      result.add(((OutputProducer) transformer).output().getPayload());
    }

    transformer.cleanUp();

    return result;
  }

  /**
   * Updates the error handler.
   *
   * @param actor	the actor to update the error handler for
   * @param handler	the handler to use
   * @param trace	whether to output the names of the actors that were updated
   */
  public static void updateErrorHandler(Actor actor, final ErrorHandler handler, final boolean trace) {
    if (actor == null)
      return;
    actor.getOptionManager().traverse(new OptionTraverser() {
      protected void update(Object obj, final ErrorHandler handler) {
        if (ClassLocator.hasInterface(Actor.class, obj.getClass())) {
          ((Actor) obj).setErrorHandler(handler);
          if (trace)
            System.out.println(((Actor) obj).getFullName());
        }
        if (ClassLocator.hasInterface(CallableActorUser.class, obj.getClass())) {
          Actor callable = ((CallableActorUser) obj).getCallableActor();
          updateErrorHandler(callable, handler, trace);
        }
        if (ClassLocator.hasInterface(InternalActorHandler.class, obj.getClass())) {
          Actor internal = ((InternalActorHandler) obj).getInternalActor();
          updateErrorHandler(internal, handler, trace);
        }
        if (ClassLocator.hasInterface(ExternalActorHandler.class, obj.getClass())) {
          Actor external = ((ExternalActorHandler) obj).getExternalActor();
          updateErrorHandler(external, handler, trace);
        }
      }

      @Override
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
        Object current = option.getCurrentValue();
        if (option.isMultiple()) {
          for (int i = 0; i < Array.getLength(current); i++) {
            update(Array.get(current, i), handler);
          }
        }
        else {
          update(current, handler);
        }
      }

      @Override
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
        // ignored
      }

      @Override
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
        // ignored
      }

      @Override
      public boolean canHandle(AbstractOption option) {
        return true;
      }

      @Override
      public boolean canRecurse(Class cls) {
        return !ClassLocator.hasInterface(VariablesHandler.class, cls)
	  && !ClassLocator.hasInterface(ErrorScopeRestrictor.class, cls);
      }

      @Override
      public boolean canRecurse(Object obj) {
        return canRecurse(obj.getClass());
      }
    });
  }

  /**
   * Determines the functional aspect of an actor.
   * 
   * @param actor	the actor to investigate
   * @return		the functional description
   */
  public static String getFunctionalAspect(Actor actor) {
    if (isActorHandler(actor))
      return FUNCTIONAL_HANDLER;
    else
      return FUNCTIONAL_PRIMITIVE;
  }

  /**
   * Determines the procedural aspect of an actor.
   * 
   * @param actor	the actor to investigate
   * @return		the procedural description
   */
  public static String getProceduralAspect(Actor actor) {
    if (isStandalone(actor))
      return PROCEDURAL_STANDALONE;
    else if (isSource(actor))
      return PROCEDURAL_SOURCE;
    else if (isTransformer(actor))
      return PROCEDURAL_TRANSFORMER;
    else if (isSink(actor))
      return PROCEDURAL_SINK;
    else
      throw new IllegalStateException("Neither standalone/source/transformer/sink??");
  }

  /**
   * Updates all the "flow-aware" paintlets of the panel with the specified
   * actor.
   *
   * @param panel       this panel's paintlets get updated
   * @param actor       the actor to set
   */
  public static void updateFlowAwarePaintlets(PaintablePanel panel, Actor actor) {
    Iterator<Paintlet>  iter;
    Paintlet            paintlet;

    iter = panel.paintlets();
    while (iter.hasNext()) {
      paintlet = iter.next();
      if (paintlet instanceof FlowAwarePaintlet)
        ((FlowAwarePaintlet) paintlet).setActor(actor);
    }
  }

  /**
   * Updates a "flow-aware" paintlet with the specified actor.
   *
   * @param paintlet    the paintlet to update, if necessary
   * @param actor       the actor to set
   */
  public static void updateFlowAwarePaintlet(Paintlet paintlet, Actor actor) {
    if (paintlet instanceof FlowAwarePaintlet)
      ((FlowAwarePaintlet) paintlet).setActor(actor);
  }

  /**
   * Extracts all the valid actor names from the given text fragment that
   * exist within the specified flow.
   *
   * @param current	the flow to verify the actor paths with
   * @param text	the text to extract the names from
   * @return		the full names of the located actors
   */
  public static List<String> extractActorNames(Actor current, String text) {
    List<String> 	result;
    Actor		actor;
    ActorPath 		path;
    String[]		lines;
    String[]		parts;

    result = new ArrayList<>();
    lines  = text.split(":");
    if (lines.length > 0) {
      for (String line : lines) {
	path  = new ActorPath(line.trim());
	actor = ActorUtils.locate(path, current, true, true);
	if (actor != null) {
	  if (!result.contains(path.toString()))
	    result.add(path.toString());
	}
	// nested?
	else if (line.indexOf('\'') > -1) {
	  parts = line.split("'");
	  for (String part: parts) {
	    path  = new ActorPath(part.trim());
	    actor = ActorUtils.locate(path, current, true, true);
	    if (actor != null) {
	      if (!result.contains(path.toString()))
		result.add(path.toString());
	    }
	  }
	}
      }
    }

    Collections.sort(result);

    return result;
  }

  /**
   * Forces the update of the variables of this actor.
   *
   * @param actor	the actor to update
   * @param vars	the variables to propagate
   * @return		null if successful, otherwise error message
   */
  public static String forceVariables(Actor actor, Variables vars) {
    String	result;

    result = null;

    if (actor != null) {
      actor.setVariables(vars);
      actor.getOptionManager().setVariables(vars);
      actor.getOptionManager().updateVariablesInstance(vars);
      result = actor.getOptionManager().updateVariableValues(true);
    }

    return result;
  }

  /**
   * Sorts the child-actors using the supplied comparator.
   *
   * @param handler 	the handler to sort the children for
   * @param comp	the comparator to use
   * @return		if the order was modified
   */
  public static boolean sort(SortableActorHandler handler, ActorComparator comp) {
    List<Actor>	children;
    int		i;
    int		n;
    boolean result;
    int		comparison;
    Actor	backup;

    if (handler.size() < 2)
      return false;

    result = false;

    children = new ArrayList<>();
    for (i = 0; i < handler.size(); i++)
      children.add(handler.get(i));

    for (i = 0; i < children.size() - 1; i++) {
      for (n = i + 1; n < children.size(); n++) {
	comparison = comp.compare(children.get(i), children.get(n));
	if (comparison > 0) {
	  result = true;
	  backup = children.get(i);
	  children.set(i, children.get(n));
	  children.set(n, backup);
	}
      }
    }

    if (result) {
      handler.removeAll();
      for (Actor child: children)
        handler.add(child);
    }

    return result;
  }

  /**
   * Returns how many actors this flow is made of.
   *
   * @param flow	the flow to check
   * @return		the number of actors
   */
  public static int determineNumActors(Actor flow) {
    return enumerate(flow).size();
  }

  /**
   * Returns a stripped down version of the actor, i.e., for ActorHandlers,
   * a copy of the actor without any sub-actors gets returned.
   *
   * @param actor	the actor to strip down
   * @return		the stripped down actor
   * @see		ActorHandler
   */
  public static Actor strip(Actor actor) {
    Actor		result;
    NestedProducer producer;
    NestedConsumer consumer;

    // create actor with only default sub-actors
    if (actor instanceof ActorHandler) {
      producer = new NestedProducer();
      producer.setBlacklisted(new Class[]{AbstractActor[].class, AbstractActor.class, Actor[].class, Actor.class});
      producer.produce(actor);
      consumer = new NestedConsumer();
      consumer.setInput(producer.getOutput());
      result = (Actor) consumer.consume();
      producer.cleanUp();
      consumer.cleanUp();
    }
    // create a shallow copy of actor
    else {
      result = actor.shallowCopy();
    }

    result.setParent(actor.getParent());

    return result;
  }

  /**
   * Ensures that the variables within this actor are valid.
   *
   * @param actor 	the actor to check
   * @return		null if all variables are valid
   */
  public static String ensureValidVariables(Actor actor) {
    OptionTraverserWithResult<StringBuilder> traverser;
    StringBuilder				result;

    traverser = new OptionTraverserWithResult<StringBuilder>() {
      protected StringBuilder m_Result;
      public void resetResult() {
	m_Result = new StringBuilder();
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	handleArgumentOption(option, path);
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	handleArgumentOption(option, path);
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	if (option.isVariableAttached()) {
	  String error = null;
	  if (!option.getOwner().getVariables().has(option.getVariableName()))
	    error = "Invalid variable '" + option.getVariableName() + "'!";
	  if (error != null) {
	    if (m_Result.length() > 0)
	      m_Result.append("\n");
	    m_Result.append(error);
	  }
	}
      }
      public boolean canHandle(AbstractOption option) {
        return true;
      }
      public boolean canRecurse(Class cls) {
        return !ClassLocator.hasInterface(Actor.class, cls);
      }
      public boolean canRecurse(Object obj) {
	return canRecurse(obj.getClass());
      }
      public StringBuilder getResult() {
        return m_Result;
      }
    };
    traverser.resetResult();
    actor.getOptionManager().traverse(traverser, true);

    result = traverser.getResult();
    if (result.length() == 0)
      return null;
    else
      return result.toString();
  }

  /**
   * Returns whether invalid variables are to be treated lenient (ie no error, just warning).
   *
   * @return		true if to treat lenient
   * @see		#ENV_INVALID_VARIABLES_LENIENT
   */
  protected static synchronized boolean checkInvalidVariablesLenient() {
    if (m_InvalidVariablesLenient == null) {
      m_InvalidVariablesLenient = (System.getenv(ENV_INVALID_VARIABLES_LENIENT) != null)
	&& System.getenv(ENV_INVALID_VARIABLES_LENIENT).equalsIgnoreCase("true");
      System.out.println(ActorUtils.class.getName() + ": lenient handling of invalid variables is " + (m_InvalidVariablesLenient ? "ON" : "OFF"));
    }
    return m_InvalidVariablesLenient;
  }
}
