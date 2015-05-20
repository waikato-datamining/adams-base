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
 * WekaForecastModelContainer.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import weka.classifiers.timeseries.AbstractForecaster;
import weka.core.Instances;

/**
 * Specialized container for {@link AbstractForecaster} models.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaForecastModelContainer
  extends WekaModelContainer {

  /** for serialization. */
  private static final long serialVersionUID = 7967559843896854521L;

  /** the transformed data. */
  public final static String VALUE_TRANSFORMED = "Transformed";
  
  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public WekaForecastModelContainer() {
    super();
  }

  /**
   * Initializes the container with no header.
   *
   * @param model	the model to use
   */
  public WekaForecastModelContainer(Object model) {
    super(model);
  }

  /**
   * Initializes the container with no header.
   *
   * @param model	the model to use
   * @param header	the header to use
   */
  public WekaForecastModelContainer(Object model, Instances header) {
    super(model, header);
  }

  /**
   * Initializes the container with no header.
   *
   * @param model	the model to use
   * @param header	the header to use
   * @param data	the data to use
   */
  public WekaForecastModelContainer(Object model, Instances header, Instances data) {
    super(model, header, data);
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String>	result;
    Iterator<String>	enm;

    result = new ArrayList<String>();
    enm    = super.names();
    while (enm.hasNext())
      result.add(enm.next());

    result.add(VALUE_TRANSFORMED);

    return result.iterator();
  }
}
