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
 * ByDateValueTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowfinder;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.DateFormatString;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.env.Environment;

/**
 * Test class for the ByDateValue finder.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ByDateValueTest
  extends AbstractRowFinderTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public ByDateValueTest(String name) {
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
	"wine.csv",
	"wine.csv",
	"wine.csv",
	"wine.csv"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected RowFinder[] getRegressionSetups() {
    ByDateValue[]	result;

    result = new ByDateValue[4];

    result[0] = new ByDateValue();
    result[1] = new ByDateValue();
    result[1].setAttributeIndex(new SpreadSheetColumnIndex("1"));
    result[1].setFormat(new DateFormatString("yyyy-MM-dd"));
    result[1].setMinimum("1990-01-01");
    result[2] = new ByDateValue();
    result[2].setAttributeIndex(new SpreadSheetColumnIndex("1"));
    result[2].setFormat(new DateFormatString("yyyy-MM-dd"));
    result[2].setMinimum("1990-01-01");
    result[2].setMaximum("1993-06-01");
    result[3] = new ByDateValue();
    result[3].setAttributeIndex(new SpreadSheetColumnIndex("1"));
    result[3].setFormat(new DateFormatString("yyyy-MM-dd"));
    result[3].setMinimum("1990-01-01");
    result[3].setMinimumIncluded(true);
    result[3].setMaximum("1993-06-01");
    result[3].setMaximumIncluded(true);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(ByDateValueTest.class);
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
