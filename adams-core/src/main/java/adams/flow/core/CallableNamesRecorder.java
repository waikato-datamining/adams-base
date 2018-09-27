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
 * CallableNamesRecorder.java
 * Copyright (C) 2016-2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.logging.LoggingObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Records callables to check for duplicates.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CallableNamesRecorder
  extends LoggingObject {

  private static final long serialVersionUID = -3440146307210935285L;

  /** the names recorded so far (per parent full name of actor reference handler). */
  protected Map<String,Set<String>> m_Names;

  /** the full names recorded to so far (per parent full name of actor reference handler). */
  protected Map<String,Set<String>> m_FullNames;

  /**
   * Initializes the recorder.
   */
  public CallableNamesRecorder() {
    m_Names     = new HashMap<>();
    m_FullNames = new HashMap<>();
  }

  /**
   * Clears the recorded data.
   */
  public void clear() {
    m_Names.clear();
    m_FullNames.clear();
  }

  /**
   * Generates the key for the handler.
   *
   * @param handler	the handler to generate the key for
   * @return		the key
   */
  protected String generateKey(ActorHandler handler) {
    return handler.getParent().getFullName();
  }

  /**
   * Checks whether the name of the actor is already in use.
   *
   * @param handler 	the handler for the actor
   * @param actor	the actor to check
   * @return		true if already in use
   */
  public boolean contains(ActorHandler handler, Actor actor) {
    String	key;

    key = generateKey(handler);
    if (!m_FullNames.containsKey(key))
      return false;
    if (m_FullNames.get(key).contains(actor.getFullName()))
      return false;
    else
      return m_Names.get(key).contains(actor.getName());
  }

  /**
   * Adds the name to the recorded ones.
   *
   * @param handler 	the handler for the actor
   * @param actor	the actor to add
   */
  public void add(ActorHandler handler, Actor actor) {
    String	key;

    key = generateKey(handler);
    if (!m_FullNames.containsKey(key)) {
      m_FullNames.put(key, new HashSet<>());
      m_Names.put(key, new HashSet<>());
    }
    if (!m_FullNames.get(key).contains(actor.getFullName())) {
      m_FullNames.get(key).add(actor.getFullName());
      m_Names.get(key).add(actor.getName());
    }
  }
}
