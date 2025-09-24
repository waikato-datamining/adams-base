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
 * SpreadSheetTimeseriesReaderTest.java
 * Copyright (C) 2013-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the SpreadSheetTimeseriesReader class.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetTimeseriesReaderTest
  extends AbstractTimeseriesReaderTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public SpreadSheetTimeseriesReaderTest(String name) {
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
	"wine.csv"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected DataContainerReader[] getRegressionSetups() {
    return new SpreadSheetTimeseriesReader[]{
	new SpreadSheetTimeseriesReader()
    };
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(SpreadSheetTimeseriesReaderTest.class);
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
