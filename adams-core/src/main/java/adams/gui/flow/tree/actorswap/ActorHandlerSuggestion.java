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
 * ActorHandlerSuggestion.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.actorswap;

import adams.core.ClassLister;
import adams.core.Properties;
import adams.core.Utils;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorUtils;
import adams.flow.core.MutableActorHandler;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Finds potential swap partners for {@link adams.flow.core.ActorHandler}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ActorHandlerSuggestion
  extends AbstractActorSwapSuggestion {

  private static final long serialVersionUID = -2879844263173160775L;

  /** the properties file to load. */
  public final static String PROPERTIES_FILENAME = "adams/gui/flow/tree/actorswap/ActorHandlerSuggestion.props";

  /** the properties. */
  protected static Properties m_Properties;

  /** classes/interfaces flagged as blacklisted. */
  protected static List<Class> m_Blacklisted;

  /** the blacklist cache. */
  protected static Set<Class> m_Suppressed;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Finds potential swap partners for " + ActorHandler.class.getName() + " actors.\n"
      + "The following props file can be used for blacklisting classes:\n"
      + PROPERTIES_FILENAME;
  }

  /**
   * Performs the actual search for candidates.
   *
   * @param current	the actor to find potential swaps for
   * @return		the list of potential swaps
   */
  @Override
  protected List<Actor> doSuggest(Actor current) {
    List<Actor>		result;
    Class[]		actors;
    int			i;
    int			n;
    boolean		isStandalone;
    boolean		isSource;
    boolean		isTransformer;
    boolean		isSink;
    boolean		blacklisted;

    initBlacklisting();

    result = new ArrayList<>();

    if (!(current instanceof ActorHandler))
      return result;

    isStandalone  = ActorUtils.isStandalone(current);
    isSource      = ActorUtils.isSource(current);
    isTransformer = ActorUtils.isTransformer(current);
    isSink        = ActorUtils.isSink(current);
    actors        = ClassLister.getSingleton().getClasses(ActorHandler.class);
    for (i = 0; i < actors.length; i++) {
      // blacklisted?
      if (m_Suppressed.contains(actors[i]))
        continue;
      blacklisted = false;
      for (n = 0; n < m_Blacklisted.size(); n++) {
        if (ClassLocator.matches(m_Blacklisted.get(n), actors[i])) {
          m_Suppressed.add(actors[i]);
          blacklisted = true;
	}
	if (blacklisted)
	  break;
      }
      if (blacklisted)
	continue;

      final ActorHandler actor = (ActorHandler) Utils.newInstance(actors[i]);
      if (!(actor instanceof MutableActorHandler))
        continue;
      if (actor instanceof Flow)
	continue;
      if (actor.getClass() == current.getClass())
	continue;
      if (isStandalone && !ActorUtils.isStandalone(actor))
	continue;
      if (isSource && !ActorUtils.isSource(actor))
	continue;
      if (isTransformer && !ActorUtils.isTransformer(actor))
	continue;
      if (isSink && !ActorUtils.isSink(actor))
	continue;
      result.add(actor);
    }
    return result;
  }

  /**
   * Returns the properties with the suggestions.
   *
   * @return		the suggestions
   */
  protected static synchronized Properties getProperties() {
    if (m_Properties == null) {
      try {
	m_Properties = Properties.read(PROPERTIES_FILENAME);
      }
      catch (Exception e) {
	System.err.println("Failed to read: " + PROPERTIES_FILENAME);
	e.printStackTrace();
	m_Properties = new Properties();
      }
    }
    return m_Properties;
  }

  /**
   * Initializes the blacklisting.
   */
  protected static synchronized void initBlacklisting() {
    Properties	props;
    Class	cls;

    if (m_Blacklisted != null)
      return;

    m_Blacklisted = new ArrayList<>();
    m_Suppressed  = new HashSet<>();
    props         = getProperties();
    for (String key: props.keySetAll()) {
      if (props.getBoolean(key, false)) {
        try {
          cls = Class.forName(key);
          if (!m_Blacklisted.contains(cls))
	    m_Blacklisted.add(cls);
	}
	catch (Exception e) {
          LOGGER.log(Level.SEVERE, "Failed to instantiate: " + key, e);
	}
      }
    }
  }
}
