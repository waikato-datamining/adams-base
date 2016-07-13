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
 * FileSupplierToSelectFileSuggestion.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.actorswap;

import adams.flow.core.Actor;
import adams.flow.source.FileSupplier;
import adams.flow.source.SelectFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Suggests SelectFile as swap partner for FileSupplier.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileSupplierToSelectFileSuggestion
  extends AbstractActorSwapSuggestion {

  private static final long serialVersionUID = -2879844263173160775L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Suggests " + SelectFile.class + " as swap partner for " + FileSupplier.class.getName() + ".";
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

    if (current instanceof FileSupplier)
      result.add(new SelectFile());

    return result;
  }
}
