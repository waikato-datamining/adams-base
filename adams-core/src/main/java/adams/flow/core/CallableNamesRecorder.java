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
 * CallableNamesRecorder.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.logging.LoggingObject;

import java.util.HashSet;

/**
 * Records callables to check for duplicates.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CallableNamesRecorder
  extends LoggingObject {

  private static final long serialVersionUID = -3440146307210935285L;

  /** the names recorded so far. */
  protected HashSet<String> m_Names;

  /** the full names recorded to so far. */
  protected HashSet<String> m_FullNames;

  /**
   * Initializes the recorder.
   */
  public CallableNamesRecorder() {
    m_Names     = new HashSet<>();
    m_FullNames = new HashSet<>();
  }

  /**
   * Clears the recorded data.
   */
  public void clear() {
    m_Names.clear();
    m_FullNames.clear();
  }

  /**
   * Checks whether the name of the actor is already in use.
   *
   * @param actor	the actor to check
   * @return		true if already in use
   */
  public boolean contains(Actor actor) {
    if (m_FullNames.contains(actor.getFullName()))
      return false;
    else
      return m_Names.contains(actor.getName());
  }

  /**
   * Adds the name to the recorded ones.
   *
   * @param actor	the actor to add
   */
  public void add(Actor actor) {
    if (!m_FullNames.contains(actor.getFullName())) {
      m_FullNames.add(actor.getFullName());
      m_Names.add(actor.getName());
    }
  }
}
