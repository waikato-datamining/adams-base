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
 * SetVariableStandaloneToTransformerSuggestion.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.actorswap;

import adams.flow.core.Actor;

import java.util.ArrayList;
import java.util.List;

/**
 * Suggests SetVariable transformer as swap partner for SetVariable standalone.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SetVariableStandaloneToTransformerSuggestion
  extends AbstractActorSwapSuggestion {

  private static final long serialVersionUID = -2879844263173160775L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Suggests " + adams.flow.transformer.SetVariable.class + " as swap partner for " + adams.flow.standalone.SetVariable.class.getName() + ".";
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

    result = new ArrayList<>();

    if (current instanceof adams.flow.standalone.SetVariable)
      result.add(new adams.flow.transformer.SetVariable());

    return result;
  }
}
