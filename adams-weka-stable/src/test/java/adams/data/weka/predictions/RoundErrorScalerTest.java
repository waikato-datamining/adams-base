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
 * RoundErrorScalerTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.predictions;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Test class for the RoundErrorScaler scaler. Run from the command line with: <br><br>
 * java adams.data.weka.predictions.RoundErrorScalerTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RoundErrorScalerTest
  extends AbstractErrorScalerTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public RoundErrorScalerTest(String name) {
    super(name);
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  protected String[] getRegressionInputFiles() {
    return new String[]{
	"double_large.txt",
	"double_large.txt",
	"double_mixed.txt",
	"double_small.txt",
	"integer.txt"
    };
  }

  /**
   * Returns whether the input files contain doubles or integers.
   *
   * @return		true if input file contains integers
   */
  protected boolean[] getRegressionInputFileContainDoubles() {
    return new boolean[]{
	true,
	true,
	true,
	true,
	false
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected AbstractErrorScaler[] getRegressionSetups() {
    RoundErrorScaler[]	result;

    result = new RoundErrorScaler[5];

    result[0] = new RoundErrorScaler();
    result[1] = new RoundErrorScaler();
    result[2] = new RoundErrorScaler();
    result[3] = new RoundErrorScaler();
    result[4] = new RoundErrorScaler();

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(RoundErrorScalerTest.class);
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
