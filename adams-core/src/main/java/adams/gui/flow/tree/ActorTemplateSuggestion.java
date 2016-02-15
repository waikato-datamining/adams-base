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
 * ActorTemplateSuggestion.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.env.ActorTemplateSuggestionDefinition;
import adams.env.Environment;
import adams.flow.core.Actor;
import adams.flow.template.AbstractActorTemplate;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for suggesting actor templates when editing a flow, depending on
 * the context.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorTemplateSuggestion {

  /** whether to output some debug information. */
  private final static Logger LOGGER = LoggingHelper.getConsoleLogger(ActorTemplateSuggestion.class);

  /** the name of the props file. */
  public final static String FILENAME = "ActorTemplateSuggestion.props";

  /** the key for the default actor. */
  public final static String KEY_DEFAULT = "Default";

  /** the properties with the rules. */
  protected Properties m_Properties;

  /** the default actor(s). */
  protected AbstractActorTemplate[] m_Defaults;

  /** the valid rules. */
  protected String[] m_Rules;

  /** the singleton. */
  protected static ActorTemplateSuggestion m_Singleton;

  /**
   * Initializes the object.
   */
  private ActorTemplateSuggestion() {
    super();
    initialize();
  }

  /**
   * Initializes the rules engine for proposing actor templates.
   */
  protected void initialize() {
    String[]					parts;
    Vector<AbstractActorTemplate>		templates;
    Vector<String>				rules;
    int						i;
    Enumeration<String>				names;
    String					name;
    String					rule;
    adams.parser.ActorTemplateSuggestion	suggestion;

    m_Properties = Environment.getInstance().read(ActorTemplateSuggestionDefinition.KEY);

    // get the default(s)
    parts     = m_Properties.getProperty(KEY_DEFAULT, "adams.flow.template.UpdateVariable").split(",");
    templates = new Vector<>();
    for (i = 0; i < parts.length; i++)  {
      try {
	templates.add((AbstractActorTemplate) Class.forName(parts[i]).newInstance());
      }
      catch (Exception e) {
	LOGGER.log(Level.SEVERE,
	    "Failed to instantiate default actor template '" + parts[i] + "':", e);
      }
    }
    if (templates.size() == 0)
      templates.add(new adams.flow.template.UpdateVariable());
    m_Defaults = templates.toArray(new AbstractActorTemplate[templates.size()]);

    LOGGER.info("Defaults: " + Utils.arrayToString(m_Defaults, true));

    // get the rules
    rules      = new Vector<String>();
    names      = (Enumeration<String>) m_Properties.propertyNames();
    suggestion = new adams.parser.ActorTemplateSuggestion();
    suggestion.setParent(new adams.flow.control.Flow());
    suggestion.setPosition(0);
    suggestion.setActors(new Actor[0]);
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
	    "Invalid actor template suggestion rule: " + rule, e);
      }
    }
    m_Rules = rules.toArray(new String[rules.size()]);

    LOGGER.info("Rules: " + Utils.arrayToString(m_Rules));
  }

  /**
   * Returns the available default actor templates.
   *
   * @return		the default actor templates
   */
  public AbstractActorTemplate[] getDefaults() {
    return m_Defaults;
  }

  /**
   * Returns the suggested actor templates.
   *
   * @param parent	the parent of the actor to suggest
   * @param position	the position of the actor template to insert in the actors
   * @param actors	the actors to insert the suggested actor template in
   * @return		the suggested actor templates
   */
  public AbstractActorTemplate[] suggest(Actor parent, int position, Actor[] actors) {
    AbstractActorTemplate[]		result;
    Vector<AbstractActorTemplate>	suggestions;
    AbstractActorTemplate[]		suggested;
    int					i;

    suggestions = new Vector<>();

    try {
      suggested = adams.parser.ActorTemplateSuggestion.evaluate(m_Rules, parent, position, actors);
      for (i = 0; i < suggested.length; i++) {
	if (suggested[i] != null)
	  suggestions.add(suggested[i]);
      }
    }
    catch (Exception e) {
      LOGGER.log(Level.SEVERE,
	  "Failed to suggest actor templates:", e);
    }

    result = suggestions.toArray(new AbstractActorTemplate[suggestions.size()]);

    LOGGER.info(
	    "suggest: "
	  + "parent=" + parent.getClass().getName() + ", "
	  + "position=" + position + ", "
	  + "actors=" + Utils.arrayToString(actors, true) + "\n"
	  + "--> " + Utils.arrayToString(result, true));

    return result;
  }

  /**
   * Returns the singleton instance for suggesting actor templates.
   *
   * @return		the singleton
   */
  public static synchronized ActorTemplateSuggestion getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new ActorTemplateSuggestion();

    return m_Singleton;
  }
}
