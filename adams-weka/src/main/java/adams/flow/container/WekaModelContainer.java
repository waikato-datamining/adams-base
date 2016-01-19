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
 * WekaModelContainer.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import weka.core.Instances;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container for models (e.g., classifier or clusterer) and an optional
 * header of a dataset.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaModelContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = 5581530171877321061L;

  /** the identifier for the Model. */
  public final static String VALUE_MODEL = "Model";

  /** the identifier for the Header. */
  public final static String VALUE_HEADER = "Header";

  /** the identifier for the full dataset. */
  public final static String VALUE_DATASET = "Dataset";

  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public WekaModelContainer() {
    this(null);
  }

  /**
   * Initializes the container with no header.
   *
   * @param model	the model to use
   */
  public WekaModelContainer(Object model) {
    this(model, null);
  }

  /**
   * Initializes the container with no header.
   *
   * @param model	the model to use
   * @param header	the header to use
   */
  public WekaModelContainer(Object model, Instances header) {
    this(model, header, null);
  }

  /**
   * Initializes the container with no header.
   *
   * @param model	the model to use
   * @param header	the header to use
   * @param data	the data to use
   */
  public WekaModelContainer(Object model, Instances header, Instances data) {
    super();

    store(VALUE_MODEL, model);
    store(VALUE_HEADER, header);
    store(VALUE_DATASET, data);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_MODEL, "model object; " + Object.class.getName());
    addHelp(VALUE_HEADER, "dataset header; " + Instances.class.getName());
    addHelp(VALUE_DATASET, "full dataset; " + Instances.class.getName());
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String>	result;

    result = new ArrayList<String>();

    result.add(VALUE_MODEL);
    result.add(VALUE_HEADER);
    result.add(VALUE_DATASET);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return   (hasValue(VALUE_MODEL) && !hasValue(VALUE_HEADER))
           | (hasValue(VALUE_MODEL) &&  hasValue(VALUE_HEADER));
  }
}
