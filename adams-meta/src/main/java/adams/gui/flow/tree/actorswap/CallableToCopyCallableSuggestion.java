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
 * CallableToCopyCallableSuggestion.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.actorswap;

import adams.flow.core.Actor;
import adams.flow.sink.CallableSink;
import adams.flow.sink.CopyCallableSink;
import adams.flow.source.CallableSource;
import adams.flow.source.CopyCallableSource;
import adams.flow.transformer.CallableTransformer;
import adams.flow.transformer.CopyCallableTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides suggestions for 'callable' actors, proposing their
 * 'copy callable' counterparts.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CallableToCopyCallableSuggestion
  extends AbstractActorSwapSuggestion {

  private static final long serialVersionUID = 5772773331338597685L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Provides suggestions for 'callable' actors, proposing their "
        + "'copy callable' counterparts.";
  }

  /**
   * Performs the actual search for candidates.
   *
   * @param current	the actor to find potential swaps for
   * @return		the list of potential swaps
   */
  @Override
  protected List<Actor> doSuggest(Actor current) {
    List<Actor>   	result;

    result = new ArrayList<>();

    if (current instanceof CallableSource)
      result.add(new CopyCallableSource());
    if (current instanceof CallableTransformer)
      result.add(new CopyCallableTransformer());
    if (current instanceof CallableSink)
      result.add(new CopyCallableSink());

    return result;
  }
}
