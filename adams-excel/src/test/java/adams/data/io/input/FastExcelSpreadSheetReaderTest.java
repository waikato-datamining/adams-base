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
 * FastExcelSpreadSheetReaderTest.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.core.io.FastExcelSpreadSheetReader class. Run from commandline with: <br><br>
 * java adams.core.io.FastExcelSpreadSheetReader
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FastExcelSpreadSheetReaderTest
  extends AbstractSpreadSheetReaderTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public FastExcelSpreadSheetReaderTest(String name) {
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
	"sample2.xlsx",
	"sample2.xlsx",
	"sample2.xlsx",
	"sample2.xlsx",
	"sample2.xlsx",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected SpreadSheetReader[] getRegressionSetups() {
    FastExcelSpreadSheetReader[]  result;

    result    = new FastExcelSpreadSheetReader[5];
    result[0] = new FastExcelSpreadSheetReader();
    result[1] = new FastExcelSpreadSheetReader();
    result[1].setNoHeader(true);
    result[2] = new FastExcelSpreadSheetReader();
    result[2].setNoHeader(true);
    result[2].setCustomColumnHeaders("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54");
    result[3] = new FastExcelSpreadSheetReader();
    result[3].setFirstRow(3);
    result[3].setNumRows(2);
    result[4] = new FastExcelSpreadSheetReader();
    result[4].setNoHeader(true);
    result[4].setFirstRow(3);
    result[4].setNumRows(2);

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(FastExcelSpreadSheetReaderTest.class);
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
