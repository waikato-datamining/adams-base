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
 * PixelsTest.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.features;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.image.features.Pixels.PixelType;
import adams.env.Environment;

/**
 * Test class for the Pixels flattener. Run from the command line with: <br><br>
 * java adams.data.jai.flattener.PixelsTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PixelsTest
  extends AbstractBufferedImageFeatureGeneratorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public PixelsTest(String name) {
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
	"adams_icon.png",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBufferedImageFeatureGenerator[] getRegressionSetups() {
    Pixels[]	result;

    result = new Pixels[3];

    result[0] = new Pixels();
    
    result[1] = new Pixels();
    result[1].setPixelType(PixelType.RGB_SEPARATE);
    
    result[2] = new Pixels();
    result[2].setPixelType(PixelType.HSB_SEPARATE);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(PixelsTest.class);
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
