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
 * MultiTransformerTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.jai.transformer.Gray8;
import adams.data.jai.transformer.Invert;
import adams.env.Environment;

/**
 * Test class for the MultiTransformer transformer. Run from the command line with: <br><br>
 * java adams.data.adams.transformer.MultiTransformerTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9242 $
 */
public class MultiTransformerTest
  extends AbstractBufferedImageTransformerTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public MultiTransformerTest(String name) {
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
	"adams_logo.png",
	"adams_logo.png",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBufferedImageTransformer[] getRegressionSetups() {
    MultiTransformer[]	result;

    result = new MultiTransformer[2];

    result[0] = new MultiTransformer();
    result[1] = new MultiTransformer();
    result[1].setTransformers(new AbstractBufferedImageTransformer[]{
	new Gray8(),
	new Invert(),
    });

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(MultiTransformerTest.class);
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
