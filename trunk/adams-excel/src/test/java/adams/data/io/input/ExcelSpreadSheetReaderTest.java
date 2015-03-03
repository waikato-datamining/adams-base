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
 * ExcelSpreadSheetReaderTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the adams.core.io.ExcelSpreadSheetReader class. Run from commandline with: <p/>
 * java adams.core.io.ExcelSpreadSheetReader
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExcelSpreadSheetReaderTest
  extends AbstractSpreadSheetReaderTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ExcelSpreadSheetReaderTest(String name) {
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
	"sample.xls",
	"sample2.xlsx"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected SpreadSheetReader[] getRegressionSetups() {
    return new ExcelSpreadSheetReader[]{
	new ExcelSpreadSheetReader(),
	new ExcelSpreadSheetReader()
    };
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ExcelSpreadSheetReaderTest.class);
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
