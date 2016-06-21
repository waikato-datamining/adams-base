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
 * ExternalToIncludeExternalSuggestion.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.actorswap;

import adams.flow.core.Actor;
import adams.flow.sink.ExternalSink;
import adams.flow.sink.IncludeExternalSink;
import adams.flow.source.ExternalSource;
import adams.flow.source.IncludeExternalSource;
import adams.flow.standalone.ExternalStandalone;
import adams.flow.standalone.IncludeExternalStandalone;
import adams.flow.transformer.ExternalTransformer;
import adams.flow.transformer.IncludeExternalTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides suggestions for 'external' actors, proposing their
 * 'include external' counterparts.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExternalToIncludeExternalSuggestion
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
      "Provides suggestions for 'external' actors, proposing their "
        + "'include external' counterparts.";
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

    if (current instanceof ExternalStandalone)
      result.add(new IncludeExternalStandalone());
    if (current instanceof ExternalSource)
      result.add(new IncludeExternalSource());
    if (current instanceof ExternalTransformer)
      result.add(new IncludeExternalTransformer());
    if (current instanceof ExternalSink)
      result.add(new IncludeExternalSink());

    return result;
  }
}
