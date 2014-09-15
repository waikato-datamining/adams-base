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
 * GaborTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.lire.features;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.base.features.AbstractBufferedImageFeatureGenerator;
import adams.env.Environment;

/**
 * Test class for the Gabor flattener. Run from the command line with: <p/>
 * java adams.data.lire.features.GaborTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4649 $
 */
public class GaborTest
  extends AbstractLireFeatureGeneratorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public GaborTest(String name) {
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
	"3666455665_18795f0741.jpg"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBufferedImageFeatureGenerator[] getRegressionSetups() {
    Gabor[]	result;

    result = new Gabor[1];

    result[0] = new Gabor();

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(GaborTest.class);
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
