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
 * AbstractMultiMapOperation.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.multimapoperation;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;

import java.util.Map;

/**
 * Abstract base class for operations that require multiple maps.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <O> the generated output type
 */
public abstract class AbstractMultiMapOperation<O>
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = 1185449853784824033L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the minimum number of maps that are required for the operation.
   *
   * @return		the number of maps that are required, <= 0 means no lower limit
   */
  public abstract int minNumMapsRequired();

  /**
   * Returns the maximum number of maps that are required for the operation.
   *
   * @return		the number of maps that are required, <= 0 means no upper limit
   */
  public abstract int maxNumMapsRequired();

  /**
   * The type of data that is generated.
   *
   * @return		the class
   */
  public abstract Class generates();

  /**
   * Checks the maps.
   * <br><br>
   * Default implementation only ensures that maps are present.
   *
   * @param maps	the maps to check
   */
  protected void check(Map[] maps) {
    if ((maps == null) || (maps.length == 0))
      throw new IllegalStateException("No maps provided!");

    if (minNumMapsRequired() > 0) {
      if (maps.length < minNumMapsRequired())
	throw new IllegalStateException(
	  "Not enough maps supplied (min > supplied): " + minNumMapsRequired() + " > " + maps.length);
    }

    if (maxNumMapsRequired() > 0) {
      if (maps.length > maxNumMapsRequired())
	throw new IllegalStateException(
	  "Too many maps supplied (max < supplied): " + maxNumMapsRequired() + " < " + maps.length);
    }
  }

  /**
   * Performs the actual processing of the maps.
   *
   * @param maps	the containers to process
   * @param errors	for collecting errors
   * @return		the generated data
   */
  protected abstract O doProcess(Map[] maps, MessageCollection errors);

  /**
   * Processes the containers.
   *
   * @param maps	the containers to process
   * @param errors	for collecting errors
   * @return		the generated data
   */
  public O process(Map[] maps, MessageCollection errors) {
    check(maps);
    return doProcess(maps, errors);
  }
}
