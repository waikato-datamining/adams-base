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
 * ActorHandlerSuggestion.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.actorswap;

import adams.core.ClassLister;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorUtils;
import adams.flow.core.MutableActorHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds potential swap partners for {@link adams.flow.core.ActorHandler}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorHandlerSuggestion
  extends AbstractActorSwapSuggestion {

  private static final long serialVersionUID = -2879844263173160775L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Finds potential swap partners for " + ActorHandler.class.getName() + " actors.";
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
    String[]		actors;
    int			i;
    boolean		isStandalone;
    boolean		isSource;
    boolean		isTransformer;
    boolean		isSink;

    result = new ArrayList<>();

    if (!(current instanceof ActorHandler))
      return result;

    isStandalone  = ActorUtils.isStandalone(current);
    isSource      = ActorUtils.isSource(current);
    isTransformer = ActorUtils.isTransformer(current);
    isSink        = ActorUtils.isSink(current);
    actors        = ClassLister.getSingleton().getClassnames(ActorHandler.class);
    for (i = 0; i < actors.length; i++) {
      final ActorHandler actor = (ActorHandler) AbstractActor.forName(actors[i], new String[0]);
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
}
