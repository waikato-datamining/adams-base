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
 * WekaForecastContainer.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.timeseries.AbstractForecaster;
import weka.classifiers.timeseries.WekaForecaster;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container for forecasts made by a forecaster.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaForecastContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = 872330681430825295L;

  /** the identifier for the forecaster model. */
  public final static String VALUE_MODEL = "Model";

  /** the identifier for the Classification. */
  public final static String VALUE_FORECASTS = "Forecasts";

  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public WekaForecastContainer() {
    this(new WekaForecaster(), new ArrayList<List<NumericPrediction>>());
  }

  /**
   * Initializes the container.
   *
   * @param model	the forecaster model
   * @param forecasts	the forecasts
   */
  public WekaForecastContainer(AbstractForecaster model, List<List<NumericPrediction>> forecasts) {
    super();

    store(VALUE_MODEL, model);
    store(VALUE_FORECASTS, forecasts);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_MODEL, "forecaster model; " + AbstractForecaster.class.getName());
    addHelp(VALUE_FORECASTS, "forecasts made; list of list of " + NumericPrediction.class.getName());
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
    result.add(VALUE_FORECASTS);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return (hasValue(VALUE_MODEL) && hasValue(VALUE_FORECASTS));
  }
}
