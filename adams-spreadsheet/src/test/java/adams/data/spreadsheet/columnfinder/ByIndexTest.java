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
 * ByIndexTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.columnfinder;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.env.Environment;

/**
 * Test class for the ByIndex finder.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ByIndexTest
  extends AbstractColumnFinderTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public ByIndexTest(String name) {
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
	"bolts.csv"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected ColumnFinder[] getRegressionSetups() {
    ByIndex[]	result;

    result = new ByIndex[3];

    result[0] = new ByIndex();
    result[1] = new ByIndex();
    result[1].setColumns(new SpreadSheetColumnRange("1-3"));
    result[2] = new ByIndex();
    result[2].setColumns(new SpreadSheetColumnRange("last"));

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(ByIndexTest.class);
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
