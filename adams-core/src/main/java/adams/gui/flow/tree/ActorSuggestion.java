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
 * ActorSuggestion.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree;

import adams.core.ClassLocator;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingHelper;
import adams.env.ActorSuggestionDefinition;
import adams.env.Environment;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for suggesting actors when editing a flow, depending on the context.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorSuggestion {

  /** whether to output some debug information. */
  private final static Logger LOGGER = LoggingHelper.getConsoleLogger(ActorSuggestion.class);

  /** the name of the props file. */
  public final static String FILENAME = "ActorSuggestion.props";

  /** the key for the default actor. */
  public final static String KEY_DEFAULT = "Default";

  /** the file to write the "add" history to .*/
  public final static String FILENAME_ADDHISTORY = "FlowAddHistory.csv";

  /** the properties with the rules. */
  protected Properties m_Properties;

  /** the default actor(s). */
  protected AbstractActor[] m_Defaults;

  /** the valid rules. */
  protected String[] m_Rules;

  /** the singleton. */
  protected static ActorSuggestion m_Singleton;

  /**
   * Initializes the object.
   */
  private ActorSuggestion() {
    super();
    initialize();
  }

  /**
   * Initializes the rules engine for proposing actors.
   */
  protected void initialize() {
    String[]				parts;
    List<AbstractActor>			actors;
    List<String>			rules;
    int					i;
    Enumeration<String>			names;
    String				name;
    String				rule;
    adams.parser.ActorSuggestion	suggestion;

    m_Properties = Environment.getInstance().read(ActorSuggestionDefinition.KEY);

    // get the default(s)
    parts  = m_Properties.getProperty(KEY_DEFAULT, "adams.flow.transformer.PassThrough").split(",");
    actors = new ArrayList<AbstractActor>();
    for (i = 0; i < parts.length; i++)  {
      try {
	actors.add((AbstractActor) Class.forName(parts[i]).newInstance());
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate default actor '" + parts[i] + "':");
	e.printStackTrace();
      }
    }
    if (actors.size() == 0)
      actors.add(new adams.flow.transformer.PassThrough());
    m_Defaults = actors.toArray(new AbstractActor[actors.size()]);

    LOGGER.info("Defaults: " + Utils.arrayToString(m_Defaults, true));

    // get the rules
    rules      = new ArrayList<String>();
    names      = (Enumeration<String>) m_Properties.propertyNames();
    suggestion = new adams.parser.ActorSuggestion();
    suggestion.setParent(new adams.flow.control.Flow());
    suggestion.setPosition(0);
    suggestion.setActors(new AbstractActor[0]);
    while (names.hasMoreElements()) {
      name = names.nextElement();
      if (name.equals(KEY_DEFAULT))
	continue;
      rule = m_Properties.getProperty(name);
      try {
	suggestion.setExpression(rule);
	suggestion.evaluate();
	rules.add(rule);
      }
      catch (Exception e) {
	LOGGER.log(Level.SEVERE,
	    "Invalid actor suggestion rule: " + rule, e);
      }
    }
    m_Rules = rules.toArray(new String[rules.size()]);

    LOGGER.info("Rules: " + Utils.arrayToString(m_Rules));
  }

  /**
   * Returns the available default actors.
   *
   * @return		the default actors
   */
  public AbstractActor[] getDefaults() {
    return m_Defaults;
  }

  /**
   * Returns the suggested actors.
   *
   * @param parent	the parent of the actor to suggest
   * @param position	the position of the actor to insert in the actors
   * @param actors	the actors to insert the suggested actor in
   * @return		the suggested actors
   */
  public AbstractActor[] suggest(AbstractActor parent, int position, AbstractActor[] actors) {
    AbstractActor[]		result;
    List<AbstractActor>		suggestions;
    AbstractActor[]		suggested;
    int				i;
    Class[]			restrictions;
    boolean			match;

    suggestions  = new ArrayList<AbstractActor>();
    restrictions = ((ActorHandler) parent).getActorHandlerInfo().getRestrictions();

    try {
      suggested = adams.parser.ActorSuggestion.evaluate(m_Rules, parent, position, actors);
      for (i = 0; i < suggested.length; i++) {
	if (suggested[i] != null) {
	  if (restrictions.length > 0) {
	    match = false;
	    for (Class cls: restrictions) {
	      try {
		if (cls.isInterface())
		  match = match || ClassLocator.hasInterface(cls, suggested[i].getClass());
		else
		  match = match || ClassLocator.isSubclass(cls, suggested[i].getClass());
	      }
	      catch (Exception e) {
		LOGGER.log(Level.SEVERE,
		    "Failed to process suggestion ('" + parent.getClass().getName() + "'): " + cls.getName(), e);
	      }
	    }
	    if (!match)
	      suggested[i] = null;
	  }
	}
	if (suggested[i] != null)
	  suggestions.add(suggested[i]);
      }
    }
    catch (Exception e) {
      System.err.println("Failed to suggest actors:");
      e.printStackTrace();
    }

    result = suggestions.toArray(new AbstractActor[suggestions.size()]);

    LOGGER.info(
	    "suggest: "
	  + "parent=" + parent.getClass().getName() + ", "
	  + "position=" + position + ", "
	  + "actors=" + Utils.arrayToString(actors, true) + "\n"
	  + "--> " + Utils.arrayToString(result, true));

    return result;
  }
  
  /**
   * Adds details about an actor.
   * 
   * @param actor 	the actor to add the details for
   * @param line	the buffer to add the details to
   */
  protected void record(Actor actor, StringBuilder line) {
    if (line.length() > 0)
      line.append(",");
    
    // class
    if (actor != null)
      line.append(actor.getClass().getName());
    line.append(",");

    // functional
    if (actor != null)
      line.append(ActorUtils.getFunctionalAspect(actor));
    line.append(",");

    // procedural
    if (actor != null)
      line.append(ActorUtils.getProceduralAspect(actor));
    line.append(",");
    
    // control?
    if (actor != null)
      line.append(ActorUtils.isControlActor(actor) ? "true" : "false");
  }
  
  /**
   * Records the actor that was added.
   * 
   * @param added	the actor that was added
   * @param parent	the parent of the added actor
   * @param before	the immediate actor before the added actor, can be null
   * @param after	the immediate actor after the added actor, can be null
   * @param position	how the actor was added
   */
  protected void record(Actor added, Actor parent, Actor before, Actor after, TreeOperations.InsertPosition position) {
    StringBuilder	line;
    String		filename;
    
    filename = Environment.getInstance().getHome() + File.separator + FILENAME_ADDHISTORY;
    
    // header?
    if (!new File(filename).exists()) {
      line = new StringBuilder();
      line.append("Actor-Class");
      line.append(",");
      line.append("Actor-Functional");
      line.append(",");
      line.append("Actor-Procedural");
      line.append(",");
      line.append("Actor-Control");
      line.append(",");
      line.append("Parent-Class");
      line.append(",");
      line.append("Parent-Functional");
      line.append(",");
      line.append("Parent-Procedural");
      line.append(",");
      line.append("Parent-Control");
      line.append(",");
      line.append("Before-Class");
      line.append(",");
      line.append("Before-Functional");
      line.append(",");
      line.append("Before-Procedural");
      line.append(",");
      line.append("Before-Control");
      line.append(",");
      line.append("After-Class");
      line.append(",");
      line.append("After-Functional");
      line.append(",");
      line.append("After-Procedural");
      line.append(",");
      line.append("After-Control");
      line.append(",");
      line.append("Position");
      FileUtils.writeToFile(filename, line, false);
    }
    
    line = new StringBuilder();
    record(added, line);
    record(parent, line);
    record(before, line);
    record(after, line);
    line.append(",");
    line.append(position.toString());
    FileUtils.writeToFile(filename, line, true);
  }

  /**
   * Records the actor that was added.
   * 
   * @param added	the node that was added
   * @param parent	the parent of the added actor
   * @param position	how the actor was added
   */
  public void record(Node added, Node parent, TreeOperations.InsertPosition position) {
    Actor	addedActor;
    Actor	parentActor;
    Actor	beforeActor;
    Actor	afterActor;
    
    addedActor  = added.getActor();
    parentActor = parent.getActor();
    beforeActor = null;
    afterActor  = null;
    if (added.getPreviousSibling() != null)
      beforeActor = ((Node) added.getPreviousSibling()).getActor();
    if (added.getNextSibling() != null)
      afterActor = ((Node) added.getNextSibling()).getActor();
    
    record(addedActor, parentActor, beforeActor, afterActor, position);
  }

  /**
   * Returns the singleton instance for suggesting actors.
   *
   * @return		the singleton
   */
  public static synchronized ActorSuggestion getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new ActorSuggestion();

    return m_Singleton;
  }
}
