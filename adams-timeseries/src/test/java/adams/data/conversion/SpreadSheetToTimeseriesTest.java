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
 * SpreadSheetToTimeseriesTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.test.TmpFile;

/**
 * Tests the SpreadSheetToTimeseries conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetToTimeseriesTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public SpreadSheetToTimeseriesTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("wine.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("wine.csv");

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
    SpreadSheet[]	result;
    
    result = new SpreadSheet[1];
    try {
      result[0] = new CsvSpreadSheetReader().read(new TmpFile("wine.csv"));
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
    SpreadSheetToTimeseries[]	result;
    
    result = new SpreadSheetToTimeseries[2];
    result[0] = new SpreadSheetToTimeseries();
    result[0].setDateColumn(new SpreadSheetColumnIndex(SpreadSheetColumnIndex.LAST));
    result[0].setValueColumn(new SpreadSheetColumnIndex(SpreadSheetColumnIndex.FIRST));
    result[1] = new SpreadSheetToTimeseries();
    result[1].setDateColumn(new SpreadSheetColumnIndex(SpreadSheetColumnIndex.LAST));
    result[1].setValueColumn(new SpreadSheetColumnIndex("Dry-white"));
    
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
