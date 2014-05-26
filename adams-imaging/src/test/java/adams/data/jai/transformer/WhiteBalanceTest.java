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
 * WhiteBalanceTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.jai.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.jai.transformer.whitebalance.GIMP;
import adams.env.Environment;

/**
 * Test class for the WhiteBalance transformer. Run from the command line with: <p/>
 * java adams.data.jai.transformer.WhiteBalanceTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8475 $
 */
public class WhiteBalanceTest
  extends AbstractJAITransformerTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public WhiteBalanceTest(String name) {
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
	"ColorChecker100423.jpg",
	"ColorChecker100423.jpg"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractJAITransformer[] getRegressionSetups() {
    WhiteBalance[]	result;
    GIMP		gimp;

    result    = new WhiteBalance[2];
    result[0] = new WhiteBalance();
    gimp      = new GIMP();
    result[1] = new WhiteBalance();
    result[1].setAlgorithm(gimp);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(WhiteBalanceTest.class);
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
