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
 * OutlierDetectorContainer.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.container;

import adams.core.Utils;
import adams.data.container.DataContainer;
import adams.data.outlier.OutlierDetector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container to store outlier detection information.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class OutlierDetectorContainer
  extends AbstractContainer {

  private static final long serialVersionUID = 1960872156580346093L;

  /** the identifier for the the detection output (if any). */
  public final static String VALUE_DETECTION = "Detection";

  /** the identifier for the input data. */
  public final static String VALUE_INPUT = "Input";

  /** the identifier for the detector itself. */
  public final static String VALUE_DETECTOR = "Detector";

  /**
   * Initializes the container.
   */
  public OutlierDetectorContainer() {
    this(null, null, null);
  }

  /**
   * Initializes the container with the specified plot name and no X value.
   *
   * @param detector	the detector used
   * @param detection	the detection messages
   * @param input	the original input data
   */
  public OutlierDetectorContainer(OutlierDetector detector, String[] detection, DataContainer input) {
    super();

    store(VALUE_DETECTOR,  detector);
    store(VALUE_DETECTION, detection);
    store(VALUE_INPUT,     input);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_DETECTOR, "detection algorithm; " + OutlierDetector.class.getName());
    addHelp(VALUE_DETECTION, "detection output; " + Utils.classToString(String[].class));
    addHelp(VALUE_INPUT, "original input data; " + DataContainer.class.getName());
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		iterator over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String> result;

    result = new ArrayList<>();

    result.add(VALUE_DETECTOR);
    result.add(VALUE_DETECTION);
    result.add(VALUE_INPUT);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_DETECTOR) && hasValue(VALUE_DETECTION) && hasValue(VALUE_INPUT);
  }
}
