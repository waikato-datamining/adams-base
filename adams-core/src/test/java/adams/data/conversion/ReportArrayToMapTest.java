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
 * ReportArrayToMapTest.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the ReportToMap conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ReportArrayToMapTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public ReportArrayToMapTest(String name) {
    super(name);
  }
  
  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    Report[][]	result;
    
    result = new Report[1][2];
    result[0][0] = new Report();
    result[0][0].setStringValue("id", "1");
    result[0][0].setStringValue("yo", "yo");
    result[0][0].setNumericValue("yellow", 255.0);
    result[0][1] = new Report();
    result[0][1].setStringValue("id", "2");
    result[0][1].setStringValue("hello", "world");
    result[0][1].setNumericValue("blah", 11.4);
    
    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    ReportArrayToMap[]  result;

    result    = new ReportArrayToMap[1];
    result[0] = new ReportArrayToMap();
    result[0].setKey(new Field("id", DataType.STRING));

    return result;
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[]{};
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(ReportArrayToMapTest.class);
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
