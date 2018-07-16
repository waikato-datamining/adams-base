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
 * ProcessActorContainer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.container;

import adams.flow.core.Actor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container for output from actor processors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see adams.flow.processor.ActorProcessor
 */
public class ProcessActorContainer
  extends AbstractContainer {

  private static final long serialVersionUID = -418617166464706249L;

  /** the key for the actor. */
  public final static String VALUE_ACTOR = "Actor";

  /** the key for the list. */
  public final static String VALUE_LIST = "List";

  /**
   * Default constructor.
   */
  public ProcessActorContainer() {
    this(null);
  }

  /**
   * Initializes the container with the actor.
   *
   * @param actor	the actor to store
   */
  public ProcessActorContainer(Actor actor) {
    this(actor, null);
  }

  /**
   * Initializes the container with the actor and list.
   *
   * @param actor	the actor to store
   * @param list	the list to store, can be null
   */
  public ProcessActorContainer(Actor actor, String[] list) {
    super();

    store(VALUE_ACTOR, actor);
    store(VALUE_LIST, list);
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		iterator over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String> 	result;

    result = new ArrayList<>();
    result.add(VALUE_ACTOR);
    result.add(VALUE_LIST);

    return result.iterator();
  }

  /**
   * Initializes the help strings.
   */
  @Override
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_ACTOR, "processed actor", Actor.class);
    addHelp(VALUE_LIST, "the generated list (if )", String[].class);
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_ACTOR);
  }
}
