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
 * SimpleCsvSpreadSheetReaderTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.data.io.input.SimpleCsvSpreadSheetReader class. Run from commandline with: <br><br>
 * java adams.data.io.input.SimpleCsvSpreadSheetReader
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleCsvSpreadSheetReaderTest
  extends AbstractSpreadSheetReaderTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SimpleCsvSpreadSheetReaderTest(String name) {
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
	"simple.csv",
	"simple2.csv",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected SpreadSheetReader[] getRegressionSetups() {
    SimpleCsvSpreadSheetReader[]	result;
    
    result    = new SimpleCsvSpreadSheetReader[2];
    
    result[0] = new SimpleCsvSpreadSheetReader();
    result[0].setCustomColumnHeaders("ID,SEPALLENGTH,SEPALWIDTH,PETALLENGTH,PETALWIDTH,CLASS");

    result[1] = new SimpleCsvSpreadSheetReader();
    result[1].setCustomColumnHeaders("ID,SEPALLENGTH,SEPALWIDTH,PETALLENGTH,PETALWIDTH,CLASS");
    
    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SimpleCsvSpreadSheetReaderTest.class);
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
