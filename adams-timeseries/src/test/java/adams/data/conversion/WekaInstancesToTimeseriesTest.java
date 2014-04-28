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
 * WekaInstancesToTimeseriesTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.data.weka.WekaAttributeIndex;
import adams.test.TmpFile;

/**
 * Tests the WekaInstancesToTimeseries conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstancesToTimeseriesTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public WekaInstancesToTimeseriesTest(String name) {
    super(name);
  }
  
  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("wine.arff");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("wine.arff");

    super.tearDown();
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
    Instances[]	result;
    
    result = new Instances[1];
    try {
      result[0] = DataSource.read(new TmpFile("wine.arff").getAbsolutePath());
    }
    catch (Exception e) {
      throw new IllegalStateException(e);
    }
    
    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    WekaInstancesToTimeseries[]	result;
    
    result = new WekaInstancesToTimeseries[2];
    result[0] = new WekaInstancesToTimeseries();
    result[0].setDateAttribute(new WekaAttributeIndex(WekaAttributeIndex.LAST));
    result[0].setValueAttribute(new WekaAttributeIndex(WekaAttributeIndex.FIRST));
    result[1] = new WekaInstancesToTimeseries();
    result[1].setDateAttribute(new WekaAttributeIndex(WekaAttributeIndex.LAST));
    result[1].setValueAttribute(new WekaAttributeIndex("Dry-white"));
    
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
