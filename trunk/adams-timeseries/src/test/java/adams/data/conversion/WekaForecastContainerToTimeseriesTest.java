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
 * WekaForecastContainerToTimeseriesTest.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.evaluation.NumericPrediction;
import adams.core.Index;
import adams.core.base.BaseDateTime;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.env.Environment;
import adams.flow.container.WekaForecastContainer;

/**
 * Tests the WekaForecastContainerToTimeseries conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaForecastContainerToTimeseriesTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public WekaForecastContainerToTimeseriesTest(String name) {
    super(name);
  }

  /**
   * Turns the data object into a useful string representation.
   *
   * @param data	the object to convert
   * @return		the string representation
   */
  @Override
  protected String toString(Object data) {
    Timeseries		series;
    TimeseriesPoint	point;
    StringBuilder	result;
    
    series = (Timeseries) data;
    result = new StringBuilder();
    for (Object obj: series.toList()) {
      point = (TimeseriesPoint) obj;
      result.append(point.toString());
      result.append("\n");
    }
    
    return result.toString();
  }
  
  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    WekaForecastContainer[]		result;
    List<List<NumericPrediction>>	list;
    List<NumericPrediction>		sublist;
    
    result = new WekaForecastContainer[1];
    
    list = new ArrayList<List<NumericPrediction>>();
    sublist = new ArrayList<NumericPrediction>();
    sublist.add(new NumericPrediction(1.0, 1.0));
    sublist.add(new NumericPrediction(1.0, 1.1));
    sublist.add(new NumericPrediction(1.0, 1.2));
    list.add(sublist);
    sublist = new ArrayList<NumericPrediction>();
    sublist.add(new NumericPrediction(2.0, 2.0));
    sublist.add(new NumericPrediction(2.0, 2.1));
    sublist.add(new NumericPrediction(2.0, 2.2));
    list.add(sublist);
    sublist = new ArrayList<NumericPrediction>();
    sublist.add(new NumericPrediction(3.0, 3.0));
    sublist.add(new NumericPrediction(3.0, 3.1));
    sublist.add(new NumericPrediction(3.0, 3.2));
    list.add(sublist);
    sublist = new ArrayList<NumericPrediction>();
    sublist.add(new NumericPrediction(4.0, 4.0));
    sublist.add(new NumericPrediction(4.0, 4.1));
    sublist.add(new NumericPrediction(4.0, 4.2));
    list.add(sublist);
    
    result[0] = new WekaForecastContainer();
    result[0].setValue(WekaForecastContainer.VALUE_FORECASTS, list);
    
    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    WekaForecastContainerToTimeseries[]	result;
    
    result = new WekaForecastContainerToTimeseries[3];
    result[0] = new WekaForecastContainerToTimeseries();
    result[1] = new WekaForecastContainerToTimeseries();
    result[1].setIndex(new Index("2"));
    result[2] = new WekaForecastContainerToTimeseries();
    result[2].setIndex(new Index("2"));
    result[2].setStart(new BaseDateTime("2013-07-01 09:00:00"));
    result[2].setInterval(new BaseDateTime("START +1 HOUR"));
    
    return result;
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(WekaForecastContainerToTimeseriesTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
