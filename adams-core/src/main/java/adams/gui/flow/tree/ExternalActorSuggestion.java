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
 * ExternalActorSuggestion.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.env.Environment;
import adams.env.ExternalActorSuggestionDefinition;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.flow.source.Start;
import adams.parser.ActorSuggestion.SuggestionData;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;

/**
 * Class for suggesting actors when editing a flow, depending on the context.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ExternalActorSuggestion {

  /** whether to output some debug information. */
  private final static Logger LOGGER = LoggingHelper.getConsoleLogger(ExternalActorSuggestion.class);

  /** the name of the props file. */
  public final static String FILENAME = "ExternalActorSuggestion.props";

  /** the key for the default actor. */
  public final static String KEY_DEFAULT = "Default";

  /** the properties with the rules. */
  protected Properties m_Properties;

  /** the valid rules. */
  protected String[] m_Rules;

  /** the singleton. */
  protected static ExternalActorSuggestion m_Singleton;

  /**
   * Initializes the object.
   */
  private ExternalActorSuggestion() {
    super();
    initialize();
  }

  /**
   * Initializes the rules engine for proposing actors.
   */
  protected void initialize() {
    List<String>				rules;
    Enumeration<String>				names;
    String					name;
    String					rule;
    adams.parser.ExternalActorSuggestion	suggestion;

    m_Properties = Environment.getInstance().read(ExternalActorSuggestionDefinition.KEY);

    // get the rules
    rules      = new ArrayList<>();
    names      = (Enumeration<String>) m_Properties.propertyNames();
    suggestion = new adams.parser.ExternalActorSuggestion();
    suggestion.setParent(new adams.flow.control.Flow());
    suggestion.setPosition(0);
    suggestion.setActors(new Actor[]{new Start()});
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
	    "Invalid external actor suggestion rule: " + rule, e);
      }
    }
    m_Rules = rules.toArray(new String[rules.size()]);

    LOGGER.info("Rules: " + Utils.arrayToString(m_Rules));
  }

  /**
   * Returns the suggested actors.
   *
   * @param context	the suggestion context
   * @return		the suggested actors
   */
  public Actor[] suggest(SuggestionData context) {
    Actor[]		result;
    List<Actor>		suggestions;
    Actor[]		suggested;
    int			i;
    Class[]		restrictions;
    boolean		match;

    suggestions  = new ArrayList<>();
    restrictions = ((ActorHandler) context.parent).getActorHandlerInfo().getRestrictions();

    try {
      suggested = adams.parser.ExternalActorSuggestion.evaluate(m_Rules, context);
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
		    "Failed to process suggestion ('" + context.parent.getClass().getName() + "'): " + cls.getName(), e);
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
      System.err.println("Failed to suggest external actors:");
      e.printStackTrace();
    }

    result = suggestions.toArray(new Actor[suggestions.size()]);

    LOGGER.info(
	    "suggest: "
	  + "parent=" + context.parent.getClass().getName() + ", "
	  + "position=" + context.position + ", "
	  + "actors=" + Utils.arrayToString(context.actors, true) + "\n"
	  + "--> " + Utils.arrayToString(result, true));

    return result;
  }

  /**
   * Returns the singleton instance for suggesting actors.
   *
   * @return		the singleton
   */
  public static synchronized ExternalActorSuggestion getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new ExternalActorSuggestion();

    return m_Singleton;
  }
}
