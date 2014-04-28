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
 * WekaForecastContainerToArrayTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.evaluation.NumericPrediction;
import adams.core.Index;
import adams.flow.container.WekaForecastContainer;

/**
 * Tests the WekaForecastContainerToArray conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaForecastContainerToArrayTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public WekaForecastContainerToArrayTest(String name) {
    super(name);
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
    WekaForecastContainerToArray[]	result;
    
    result = new WekaForecastContainerToArray[2];
    result[0] = new WekaForecastContainerToArray();
    result[1] = new WekaForecastContainerToArray();
    result[1].setIndex(new Index("2"));
    
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
}
