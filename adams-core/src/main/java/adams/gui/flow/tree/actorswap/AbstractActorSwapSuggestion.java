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
 * AbstractActorSwapSuggestion.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.actorswap;

import adams.core.ClassLister;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ancestor for classes the return potential swaps for an actor.
 * <br>
 * Make sure that the options get transferred as well, by having
 * the appropriate {@link adams.core.optiontransfer.AbstractOptionTransfer}
 * class(es) in place.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractActorSwapSuggestion
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -3692601200334292986L;

  /** the logger for static methods. */
  protected static Logger LOGGER = LoggingHelper.getLogger(AbstractActorSwapSuggestion.class);

  /** for caching suggestions. */
  protected static HashMap<Class, List<Actor>> m_Cache = new HashMap<>();

  /**
   * Performs the actual search for candidates.
   *
   * @param current	the actor to find potential swaps for
   * @return		the list of potential swaps
   */
  protected abstract List<Actor> doSuggest(Actor current);

  /**
   * Searches for potential candidates for swaps.
   *
   * @param current	the actor to find potential swaps for
   * @return		the list of potential swaps
   */
  public List<Actor> suggest(Actor current) {
    return doSuggest(current);
  }

  /**
   * Searches for potential candidates for swaps. Applies all potential
   * swap suggestions and combines the results.
   *
   * @param current	the actor to find potential swaps for
   * @return		the list of potential swaps
   */
  public static List<Actor> suggestAll(Actor current) {
    List<Actor>			result;
    List<Actor>			list;
    Class[]			classes;
    AbstractActorSwapSuggestion	suggestion;

    if (m_Cache.containsKey(current.getClass()))
      return m_Cache.get(current.getClass());

    result  = new ArrayList<>();
    classes = ClassLister.getSingleton().getClasses(AbstractActorSwapSuggestion.class);
    for (Class cls: classes) {
      try {
	suggestion = (AbstractActorSwapSuggestion) cls.newInstance();
	list       = suggestion.suggest(current);
	if (list.size() > 0)
	  result.addAll(list);
      }
      catch (Exception e) {
	LOGGER.log(Level.SEVERE, "Failed to retrieve swap suggestions from: " + cls.getName());
      }
    }
    m_Cache.put(current.getClass(), result);

    return result;
  }
}
