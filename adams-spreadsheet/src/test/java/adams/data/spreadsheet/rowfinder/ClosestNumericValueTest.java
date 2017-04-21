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
 * ClosestNumericValueTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowfinder;

import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.rowfinder.ClosestNumericValue.SearchDirection;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test class for the ClosestNumericValue finder.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClosestNumericValueTest
  extends AbstractRowFinderTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public ClosestNumericValueTest(String name) {
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
	"bolts.csv",
	"bolts.csv",
	"bolts.csv",
	"bolts.csv"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected RowFinder[] getRegressionSetups() {
    ClosestNumericValue[]	result;

    result = new ClosestNumericValue[4];

    result[0] = new ClosestNumericValue();
    result[1] = new ClosestNumericValue();
    result[1].setValue(20.0);
    result[2] = new ClosestNumericValue();
    result[2].setAttributeIndex(new SpreadSheetColumnIndex("2"));
    result[2].setValue(4.0);
    result[2].setSearchDirection(SearchDirection.FROM_BELOW);
    result[3] = new ClosestNumericValue();
    result[3].setAttributeIndex(new SpreadSheetColumnIndex("2"));
    result[3].setValue(5.5);
    result[3].setSearchDirection(SearchDirection.FROM_ABOVE);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(ClosestNumericValueTest.class);
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
