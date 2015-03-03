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
 * NamedSetupTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.predictions;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Test class for the NamedSetup scaler. Run from the command line with: <p/>
 * java adams.data.weka.predictions.NamedSetupTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NamedSetupTest
  extends AbstractErrorScalerTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public NamedSetupTest(String name) {
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
	"double_small.txt"
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
	true
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected AbstractErrorScaler[] getRegressionSetups() {
    NamedSetup[]	result;

    result = new NamedSetup[4];

    result[0] = new NamedSetup();
    result[0].setSetup(new adams.core.NamedSetup("weka_predictions_relativescaler"));
    result[1] = new NamedSetup();
    result[1].setSetup(new adams.core.NamedSetup("weka_predictions_relativescaler"));
    result[2] = new NamedSetup();
    result[2].setSetup(new adams.core.NamedSetup("weka_predictions_relativescaler"));
    result[3] = new NamedSetup();
    result[3].setSetup(new adams.core.NamedSetup("weka_predictions_relativescaler"));

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(NamedSetupTest.class);
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
