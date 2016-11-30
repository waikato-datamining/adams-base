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
 * PredictionEccentricityContainer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import adams.data.image.BooleanArrayMatrixView;
import adams.data.spreadsheet.SpreadSheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container for prediction eccentricity data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PredictionEccentricityContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = 5581530171877321061L;

  /** the identifier for the underlying predictions. */
  public final static String VALUE_PREDICTIONS = "Predictions";

  /** the identifier for the calculated eccentricity. */
  public final static String VALUE_ECCENTRICITY = "Eccentricity";

  /** the identifier for the binary matrix used to calculate the eccentricity. */
  public final static String VALUE_MATRIX = "Matrix";

  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public PredictionEccentricityContainer() {
    this(null, null, null);
  }

  /**
   * Initializes the container with no header.
   *
   * @param predictions	the original data
   * @param clean	the clean data, can be null
   * @param matrix	the outlier data, can be null
   */
  public PredictionEccentricityContainer(SpreadSheet predictions, Double eccentricity, BooleanArrayMatrixView matrix) {
    super();

    store(VALUE_PREDICTIONS, predictions);
    store(VALUE_ECCENTRICITY, eccentricity);
    store(VALUE_MATRIX, matrix);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_PREDICTIONS, "predictions; " + SpreadSheet.class.getName());
    addHelp(VALUE_ECCENTRICITY, "eccentricity; " + Double.class.getName());
    addHelp(VALUE_MATRIX, "matrix; " + BooleanArrayMatrixView.class.getName());
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String>	result;

    result = new ArrayList<>();

    result.add(VALUE_PREDICTIONS);
    result.add(VALUE_ECCENTRICITY);
    result.add(VALUE_MATRIX);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_PREDICTIONS) && hasValue(VALUE_ECCENTRICITY) && hasValue(VALUE_PREDICTIONS);
  }
}
