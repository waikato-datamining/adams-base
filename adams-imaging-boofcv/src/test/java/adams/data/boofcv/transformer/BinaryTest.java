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
 * BinaryTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.boofcv.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.boofcv.transformer.Binary.ThresholdType;
import adams.env.Environment;

/**
 * Test class for the Binary transformer. Run from the command line with: <br><br>
 * java adams.data.boofcv.transformer.BinaryTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10824 $
 */
public class BinaryTest
  extends AbstractBoofCVTransformerTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public BinaryTest(String name) {
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
	"6486290583_633d994a25_z.jpg",
	"6486290583_633d994a25_z.jpg",
	"6486290583_633d994a25_z.jpg",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBoofCVTransformer[] getRegressionSetups() {
    Binary[]	result;

    result    = new Binary[3];
    result[0] = new Binary();
    result[1] = new Binary();
    result[1].setRemoveSmallBlobs(true);
    result[2] = new Binary();
    result[2].setThresholdType(ThresholdType.MEAN);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(BinaryTest.class);
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
