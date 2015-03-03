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
 * ByStringComparisonTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowfinder;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.env.Environment;

/**
 * Test class for the ByStringComparison finder.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8094 $
 */
public class ByStringComparisonTest
  extends AbstractRowFinderTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public ByStringComparisonTest(String name) {
    super(name);
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  @Override
  protected String[] getRegressionInputFiles() {
    return new String[]{
	"labor.csv",
	"labor.csv",
	"labor.csv",
	"labor.csv"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected RowFinder[] getRegressionSetups() {
    ByStringComparison[]	result;

    result = new ByStringComparison[4];

    result[0] = new ByStringComparison();
    result[1] = new ByStringComparison();
    result[1].setMinimum("g");
    result[2] = new ByStringComparison();
    result[2].setAttributeIndex(new SpreadSheetColumnIndex("vacation"));
    result[2].setMinimum("b");
    result[2].setMaximum("c");
    result[3] = new ByStringComparison();
    result[3].setAttributeIndex(new SpreadSheetColumnIndex("vacation"));
    result[3].setMinimum("average");
    result[3].setMinimumIncluded(true);
    result[3].setMaximum("generous");
    result[3].setMaximumIncluded(true);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(ByStringComparisonTest.class);
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
