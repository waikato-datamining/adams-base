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
 * WekaFilterModelLoader.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.MessageCollection;
import adams.flow.container.AbstractContainer;
import adams.flow.container.WekaFilterContainer;
import weka.filters.Filter;

/**
 * Model loader for Weka filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WekaFilterModelLoader
  extends AbstractModelLoader<Filter> {

  private static final long serialVersionUID = 566855125155681191L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Manages Weka filter models.";
  }

  /**
   * Returns information how the model is loaded in case of {@link ModelLoadingType#AUTO}.
   *
   * @return		the description
   */
  public String automaticOrderInfo() {
    return super.automaticOrderInfo() + "\n"
      + "4. use specified filter definition";
  }

  /**
   * Retrieves the model from the container.
   *
   * @param cont	the container to get the model from
   * @param errors	for collecting errors
   * @return		the model, null if not in container
   */
  @Override
  protected Filter getModelFromContainer(AbstractContainer cont, MessageCollection errors) {
    if (cont instanceof WekaFilterContainer)
      return (Filter) cont.getValue(WekaFilterContainer.VALUE_FILTER);

    unhandledContainer(cont, errors);
    return null;
  }
}
