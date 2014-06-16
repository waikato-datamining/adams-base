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
 * MultiBoofCVFlattenerTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.boofcv.flattener;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Test class for the MultiBoofCVFlattener flattener. Run from the command line with: <p/>
 * java adams.data.boofcv.flattener.MultiBoofCVFlattenerTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4649 $
 */
public class MultiBoofCVFlattenerTest
  extends AbstractBoofCVFlattenerTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public MultiBoofCVFlattenerTest(String name) {
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
	"adams_icon.png",
	"adams_icon.png",
	"adams_icon.png"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBoofCVFlattener[] getRegressionSetups() {
    MultiBoofCVFlattener[]	result;
    AbstractBoofCVFlattener[]	sub;

    result = new MultiBoofCVFlattener[3];

    result[0] = new MultiBoofCVFlattener();

    result[1] = new MultiBoofCVFlattener();
    sub       = new AbstractBoofCVFlattener[1];
    sub[0]    = new Pixels();
    result[1].setSubFlatteners(sub);

    result[2] = new MultiBoofCVFlattener();
    sub       = new AbstractBoofCVFlattener[2];
    sub[0]    = new Pixels();
    sub[1]    = new Pixels();
    result[2].setSubFlatteners(sub);
    result[2].setPrefix("#-");

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(MultiBoofCVFlattenerTest.class);
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
