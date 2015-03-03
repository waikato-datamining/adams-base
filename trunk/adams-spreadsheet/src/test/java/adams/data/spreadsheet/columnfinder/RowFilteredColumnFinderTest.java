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
 * RowFilteredColumnFinderTest.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.columnfinder;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseRegExp;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.rowfinder.ByValue;
import adams.env.Environment;

/**
 * Test class for the RowFilteredColumnFinder finder.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RowFilteredColumnFinderTest
  extends AbstractColumnFinderTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public RowFilteredColumnFinderTest(String name) {
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
	"labor.csv"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected ColumnFinder[] getRegressionSetups() {
    RowFilteredColumnFinder[]	result;
    ByValue 			byvalue;
    ByName			byname;

    result = new RowFilteredColumnFinder[2];

    result[0] = new RowFilteredColumnFinder();
    
    result[1] = new RowFilteredColumnFinder();
    byvalue = new ByValue();
    byvalue.setAttributeIndex(new SpreadSheetColumnIndex("5"));
    byvalue.setRegExp(new BaseRegExp("t.*"));
    result[1].setRowFinder(byvalue);
    byname = new ByName();
    byname.setRegExp(new BaseRegExp("w.*"));
    result[1].setColumnFinder(byname);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(RowFilteredColumnFinderTest.class);
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
